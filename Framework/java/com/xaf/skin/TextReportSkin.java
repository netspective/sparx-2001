package com.xaf.skin;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import com.xaf.report.*;
import com.xaf.sql.*;
import com.xaf.value.*;

public class TextReportSkin implements ReportSkin
{
	private String fileExtn;
	private String delimiter;
	private String textQualifier;
	private boolean firstRowContainsFieldNames;

    public TextReportSkin(String fileExtn, String delimiter, String textQualifier, boolean firstRowContainsFieldNames)
    {
		this.fileExtn = fileExtn;
		this.delimiter = delimiter;
		this.textQualifier = textQualifier;
		this.firstRowContainsFieldNames = firstRowContainsFieldNames;
    }

	public String getFileExtension() { return fileExtn; }
	public String getDelimiter() { return delimiter; }
	public String getTextQualifier() { return textQualifier; }
	public boolean firstRowContainsFieldNames() { return firstRowContainsFieldNames; }

    public void produceReport(Writer writer, ReportContext rc, ResultSet rs, Object[][] data) throws SQLException, IOException
    {
		int startDataRow = 0;
		if(firstRowContainsFieldNames)
		{
			if(! rc.getReport().flagIsSet(StandardReport.REPORTFLAG_FIRST_DATA_ROW_HAS_HEADINGS))
	    	{
		    	produceHeadingRow(writer, rc, (Object[]) null);
			}
			else
			{
			    if(rs != null)
					produceHeadingRow(writer, rc, rs);
				else if(data.length > 0)
				{
					produceHeadingRow(writer, rc, data[0]);
					startDataRow = 1;
				}
			}
		}
        if(rs != null)
        {
            produceDataRows(writer, rc, rs);
        }
        else
        {
            produceDataRows(writer, rc, data, startDataRow);
        }
    }

	public void produceReport(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
	{
        produceReport(writer, rc, rs, null);
	}

	public void produceReport(Writer writer, ReportContext rc, Object[][] data) throws IOException
    {
        try
        {
            produceReport(writer, rc, null, data);
        }
        catch(SQLException e)
        {
            throw new RuntimeException("This should never happen.");
        }
    }

	public void writeText(Writer writer, String text, boolean delimiterColumn) throws IOException
	{
		if(textQualifier != null)
		{
			writer.write(textQualifier);
			if(text != null)
				writer.write(text);
			writer.write(textQualifier);
		}
		else if(text != null)
			writer.write(text);

		if(delimiterColumn)
			writer.write(delimiter);
		else
			writer.write("\n");
	}

	public void produceHeadingRow(Writer writer, ReportContext rc, Object[] headings) throws IOException
	{
		ReportColumnsList columns = rc.getColumns();
		ReportContext.ColumnState[] states = rc.getStates();
		int dataColsCount = columns.size();
		int lastDataCol = dataColsCount-1;

		if(headings == null)
		{
			for(int i = 0; i < dataColsCount; i++)
			{
				ReportColumn rcd = columns.getColumn(i);
				if(! states[i].isHidden())
					writeText(writer, rcd.getHeading().getValue(rc), i < lastDataCol);
			}
		}
		else
		{
			for(int i = 0; i < dataColsCount; i++)
			{
				ReportColumn rcd = columns.getColumn(i);
				if(states[i].isHidden())
					continue;

				Object heading = headings[rcd.getColIndexInArray()];
				if(heading != null)
					writeText(writer, heading.toString(), i < lastDataCol);
				else
					writeText(writer, null, i < lastDataCol);
			}
		}
	}

	public void produceHeadingRow(Writer writer, ReportContext rc, ResultSet rs) throws IOException, SQLException
	{
		ReportColumnsList columns = rc.getColumns();
		ReportContext.ColumnState[] states = rc.getStates();
		int dataColsCount = columns.size();
		int lastDataCol = dataColsCount-1;

		if(! rs.next()) return;

		for(int i = 0; i < dataColsCount; i++)
		{
			ReportColumn rcd = columns.getColumn(i);
			if(states[i].isHidden())
				continue;

			Object heading = rs.getString(rcd.getColIndexInResultSet());
			if(heading != null)
				writeText(writer, heading.toString(), i < lastDataCol);
			else
				writeText(writer, null, i < lastDataCol);
		}
	}

	/*
	  This method and the next one (produceDataRows with Object[][] data) are almost
	  identical except for their data sources (ResultSet vs. Object[][]). Be sure to
	  modify that method when this method changes, too
	*/

	public void produceDataRows(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
	{
        Report defn = rc.getReport();
		ReportColumnsList columns = rc.getColumns();
		ReportContext.ColumnState[] states = rc.getStates();

		int rowsWritten = 0;
		int dataColsCount = columns.size();
		int lastDataCol = dataColsCount-1;

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();

        while(rs.next())
        {
            // the reason why we need to copy the objects here is that
            // most JDBC drivers will only let data be ready one time; calling
            // the resultSet.getXXX methods more than once is problematic
            //
            Object[] rowData = new Object[resultSetColsCount];
            for(int i = 1; i <= resultSetColsCount; i++)
                rowData[i-1] = rs.getObject(i);

            for(int i = 0; i < dataColsCount; i++)
            {
				int rowNum = rs.getRow();
                ReportColumn column = columns.getColumn(i);
				ReportContext.ColumnState state = states[i];

                if(! state.isHidden())
					writeText(writer, column.getFormattedData(rc, rowNum, rowData, true), i < lastDataCol);
            }
        }
    }

	/*
	  This method and the previous one (produceDataRows with ResultSet) are almost
	  identical except for their data sources (Object[][] vs. ResultSet). Be sure to
	  modify that method when this method changes, too.
	*/

	public void produceDataRows(Writer writer, ReportContext rc, Object[][] data, int startDataRow) throws IOException
	{
        Report defn = rc.getReport();
		ReportColumnsList columns = rc.getColumns();
		ReportContext.ColumnState[] states = rc.getStates();

		int rowsWritten = 0;
		int dataColsCount = columns.size();
		int lastDataCol = dataColsCount-1;

        for(int row = startDataRow; row < data.length; row++)
        {
            Object[] rowData = data[row];
			int rowNum = row - startDataRow;

            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn column = columns.getColumn(i);
				ReportContext.ColumnState state = states[i];

                if(! state.isHidden())
					writeText(writer, column.getFormattedData(rc, rowNum, rowData, true), i < lastDataCol);
            }
        }
    }
}