package com.xaf.skin;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import com.xaf.report.*;

public class HtmlSingleRowReportSkin extends HtmlReportSkin
{
	protected int tableCols;
	protected boolean horizontalLayout;

    public HtmlSingleRowReportSkin(int tableCols, boolean horizontalLayout)
    {
		super();
		this.tableCols = tableCols;
		this.horizontalLayout = horizontalLayout;
        setFlag(HTMLFLAG_SHOW_BANNER);
		clearFlag(HTMLFLAG_SHOW_HEAD_ROW | HTMLFLAG_SHOW_FOOT_ROW);
    }

    public void produceDataRows(Writer writer, ReportContext rc, Object[] rowData) throws IOException
    {
        Report defn = rc.getReport();
		ReportColumnsList columns = rc.getColumns();
		int dataColsCount = columns.size();

		String[] outputFormats = new String[dataColsCount];
		String[] urls = new String[dataColsCount];

        for(int i = 0; i < dataColsCount; i++)
        {
            ReportColumn column = columns.getColumn(i);
            if(column.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
                continue;

            if(column.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN))
				outputFormats[i] = column.resolvePattern(column.getOutput());

			if(column.flagIsSet(ReportColumn.COLFLAG_WRAPURL))
				urls[i] = column.resolvePattern(column.getUrl());
        }

		StringBuffer dataTable = new StringBuffer();
		if(horizontalLayout)
		{
			int colCount = 0;

			dataTable.append("<tr>");
			for(int i = 0; i < dataColsCount; i++)
			{
                ReportColumn column = columns.getColumn(i);
                if(column.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
                    continue;

                String data =
                    column.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        outputFormats[i] :
                        column.getFormattedData(rc, rowData, true);

				dataTable.append("<td align='right'><font "+dataHdFontAttrs+">"+column.getHeading()+":</font></td>");
				dataTable.append("<td align='"+ ALIGN_ATTRS[column.getAlignStyle()] +"'><font "+dataFontAttrs+">"+(column.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href='"+ urls[i] +"'>"+ data +"</a>" : data)+"</font></td>");

				colCount++;
				if(colCount >= tableCols)
				{
					dataTable.append("</tr><tr>");
					colCount = 0;
				}
			}

			dataTable.append("</tr>");
            writer.write(defn.replaceOutputPatterns(rc, rowData, dataTable.toString()));
		}
		else
		{
			writer.write("Vertical layout not supported yet :-(.");
		}
    }

	public void produceDataRows(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
	{
		// position the single row -- if we can't do "next" then no row exists
		if(! rs.next())
			return;

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();

        Object[] rowData = new Object[resultSetColsCount];
        for(int i = 1; i <= resultSetColsCount; i++)
            rowData[i-1] = rs.getObject(i);

        produceDataRows(writer, rc, rowData);
	}

	public void produceDataRows(Writer writer, ReportContext rc, Object[][] data) throws IOException
	{
		// position the single row -- if we can't do "next" then no row exists
		if(data == null || data.length < 1)
			return;

        produceDataRows(writer, rc, data[0]);
	}
}