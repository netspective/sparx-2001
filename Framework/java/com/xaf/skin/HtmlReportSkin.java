package com.xaf.skin;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import com.xaf.report.*;
import com.xaf.value.*;

public class HtmlReportSkin implements ReportSkin
{
    static public final int HTMLFLAG_SHOW_BANNER        = 1;
    static public final int HTMLFLAG_SHOW_HEAD_ROW      = HTMLFLAG_SHOW_BANNER * 2;
    static public final int HTMLFLAG_SHOW_FOOT_ROW      = HTMLFLAG_SHOW_HEAD_ROW * 2;
    static public final int HTMLFLAG_ADD_ROW_SEPARATORS = HTMLFLAG_SHOW_FOOT_ROW * 2;

	static public final String[] ALIGN_ATTRS = { "LEFT", "CENTER", "RIGHT" };

    protected int flags;
	protected String outerTableAttrs = "border=0 cellspacing=1 cellpadding=2 bgcolor='#EEEEEE'";
	protected String innerTableAttrs = "cellpadding='1' cellspacing='0' border='0'";
	protected String frameHdRowAttrs = "bgcolor='#6699CC''";
	protected String frameHdFontAttrs = "face='verdana,arial,helvetica' size=2 color=white";
    protected String frameHdTableRowBgcolorAttrs = "#FFFFCC";
	protected String bannerRowAttrs = "bgcolor='lightyellow'";
	protected String bannerItemFontAttrs = "face='arial,helvetica' size=2";
	protected String dataHdFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";
	protected String dataFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;'";
	protected String dataFtFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";

    public HtmlReportSkin()
    {
        setFlag(HTMLFLAG_SHOW_BANNER | HTMLFLAG_SHOW_HEAD_ROW | HTMLFLAG_SHOW_FOOT_ROW | HTMLFLAG_ADD_ROW_SEPARATORS);
    }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }
	public final void updateFlag(long flag, boolean set) { if(set) flags |= flag; else flags &= ~flag; }

    public void produceReport(Writer writer, ReportContext rc, ResultSet rs, Object[][] data) throws SQLException, IOException
    {
        ReportFrame frame = rc.getReport().getFrame();
        ReportBanner banner = rc.getReport().getBanner();

		boolean haveOuterTable = (frame != null || banner != null);
        if(haveOuterTable)
        {
            writer.write("<table "+ outerTableAttrs +">");
			if(frame != null)
			{
				String heading = null;
				SingleValueSource hvs = frame.getHeading();
				if(hvs != null)
					heading = hvs.getValue(rc);

				writer.write("<tr "+ frameHdRowAttrs +"><td><font "+ frameHdFontAttrs + "><b>" + heading + "</b></font></td></tr>");
			}

			if(banner != null)
			{
				writer.write("<tr "+ bannerRowAttrs +"><td><table border=0 cellspacing=0>");
				ArrayList bannerItems = banner.getItems();
				for(int i = 0; i < bannerItems.size(); i++)
				{
					ReportBanner.Item item = (ReportBanner.Item) bannerItems.get(i);
					writer.write("<tr><td>*</td><td><font "+bannerItemFontAttrs+"><a href='"+ item.getUrl() +"'>"+ item.getCaption() +"</a></font></td></tr>");
				}
				writer.write("</table></td></tr>");
			}
			writer.write("<tr><td bgcolor='white'>");
        }

		writer.write("<table "+innerTableAttrs+">");
		if(flagIsSet(HTMLFLAG_SHOW_HEAD_ROW))
			produceHeadingRow(writer, rc);
        if(rs != null)
        {
            produceDataRows(writer, rc, rs);
        }
        else
        {
            produceDataRows(writer, rc, data);
        }
        if(flagIsSet(HTMLFLAG_SHOW_FOOT_ROW) && rc.getCalcsCount() > 0)
            produceFootRow(writer, rc);
        writer.write("</table>");

        if(haveOuterTable)
        {
            writer.write("</td></tr></table>");
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

	public void produceHeadingRow(Writer writer, ReportContext rc) throws IOException
	{
		ReportColumnsList columns = rc.getColumns();
		int dataColsCount = columns.size();
		int tableColsCount = (rc.getReport().getVisibleColsCount() * 2) + 1; // each column has "spacer" in between, first column as spacer before too

		writer.write("<tr bgcolor="+frameHdTableRowBgcolorAttrs+"><td><font "+dataHdFontAttrs+">&nbsp;&nbsp;</font></td>");
		for(int i = 0; i < dataColsCount; i++)
		{
			ReportColumn rcd = columns.getColumn(i);
			if(rcd.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
				continue;

			writer.write("<td><font "+dataHdFontAttrs+"><b>"+ rcd.getHeading() +"</b></font></td><td><font "+dataHdFontAttrs+">&nbsp;&nbsp;</font></td>");
		}
        if(flagIsSet(HTMLFLAG_ADD_ROW_SEPARATORS))
    		writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><img src='/shared/resources/images/design/bar.gif' height='2' width='100%'></td></tr>");
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
        boolean addRowSeps = flagIsSet(HTMLFLAG_ADD_ROW_SEPARATORS);
		int rowsWritten = 0;
		int dataColsCount = columns.size();
		int tableColsCount = (defn.getVisibleColsCount() * 2) + 1;
		boolean paging = rc.scrollingResults();

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();

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

        while(rs.next())
        {
            // the reason why we need to copy the objects here is that
            // most JDBC drivers will only let data be ready one time; calling
            // the resultSet.getXXX methods more than once is problematic
            //
            Object[] rowData = new Object[resultSetColsCount];
            for(int i = 1; i <= resultSetColsCount; i++)
                rowData[i-1] = rs.getObject(i);

            writer.write("<tr><td><font "+dataFontAttrs+">&nbsp;&nbsp;</td>");
            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn column = columns.getColumn(i);
                if(column.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
                    continue;

                String data =
                    column.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        outputFormats[i] :
                        column.getFormattedData(rc, rowData, true);

                String singleRow = "<td align='"+ ALIGN_ATTRS[column.getAlignStyle()] +"'><font "+dataFontAttrs+">"+
                    (column.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href='"+ urls[i] +"'>"+ data +"</a>" : data) +
                    "</font></td><td><font "+dataFontAttrs+">&nbsp;&nbsp;</td>";

				//writer.write(MessageFormat.format(singleRow, rowData));
                writer.write(defn.replaceOutputPatterns(rc, rowData, singleRow));
            }
			writer.write("</tr>");

            if(addRowSeps)
                writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><img src='/shared/resources/images/design/bar.gif' height='1' width='100%'></td></tr>");

			rowsWritten++;
            if(paging && rc.endOfPage())
                break;
        }

		if(rowsWritten == 0)
		{
            writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><font "+dataFontAttrs+">No data found.</font></td></tr>");
		}
    }

	/*
	  This method and the previous one (produceDataRows with ResultSet) are almost
	  identical except for their data sources (Object[][] vs. ResultSet). Be sure to
	  modify that method when this method changes, too.
	*/

	public void produceDataRows(Writer writer, ReportContext rc, Object[][] data) throws IOException
	{
        Report defn = rc.getReport();
		ReportColumnsList columns = rc.getColumns();
        boolean addRowSeps = flagIsSet(HTMLFLAG_ADD_ROW_SEPARATORS);
		int rowsWritten = 0;
		int dataColsCount = columns.size();
		int tableColsCount = (defn.getVisibleColsCount() * 2) + 1;
		boolean paging = rc.scrollingResults();

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

        for(int row = 0; row < data.length; row++)
        {
            Object[] rowData = data[row];

            writer.write("<tr><td><font "+dataFontAttrs+">&nbsp;&nbsp;</td>");
            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn column = columns.getColumn(i);
                if(column.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
                    continue;

                String colData =
                    column.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        outputFormats[i] :
                        column.getFormattedData(rc, rowData, true);

                String singleRow = "<td align='"+ ALIGN_ATTRS[column.getAlignStyle()] +"'><font "+dataFontAttrs+">"+
                    (column.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href='"+ urls[i] +"'>"+ colData +"</a>" : colData) +
                    "</font></td><td><font "+dataFontAttrs+">&nbsp;&nbsp;</td>";

				//writer.write(MessageFormat.format(singleRow, rowData));
                writer.write(defn.replaceOutputPatterns(rc, rowData, singleRow));
            }
			writer.write("</tr>");

            if(addRowSeps)
                writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><img src='/shared/resources/images/design/bar.gif' height='1' width='100%'></td></tr>");

			rowsWritten++;
            if(paging && rc.endOfPage())
                break;
        }

		if(rowsWritten == 0)
		{
            writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><font "+dataFontAttrs+">No data found.</font></td></tr>");
		}
    }

	public void produceFootRow(Writer writer, ReportContext rc) throws SQLException, IOException
	{
        int calcsCount = rc.getCalcsCount();
        if(calcsCount == 0)
            return;

        ColumnDataCalculator[] calcs = rc.getCalcs();
		ReportColumnsList columns = rc.getColumns();
		int dataColsCount = columns.size();
		int tableColsCount = (rc.getReport().getVisibleColsCount() * 2) + 1; // each column has "spacer" in between, first column as spacer before too

        if(flagIsSet(HTMLFLAG_ADD_ROW_SEPARATORS))
    		writer.write("</tr><tr><td colspan='"+ tableColsCount +"'><img src='/shared/resources/images/design/bar.gif' height='1' width='100%'></td></tr>");
		writer.write("<tr bgcolor='lightyellow'><td><font "+dataFtFontAttrs+">&nbsp;&nbsp;</font></td>");
		for(int i = 0; i < dataColsCount; i++)
		{
			ReportColumn column = columns.getColumn(i);
			if(column.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
				continue;

			writer.write("<td align='"+ ALIGN_ATTRS[column.getAlignStyle()] +"'><font "+dataFtFontAttrs+"><b>"+ column.getFormattedData(rc, calcs[i]) +"</b></font></td><td><font "+dataFtFontAttrs+">&nbsp;&nbsp;</font></td>");
		}
		writer.write("</tr>");
    }
}