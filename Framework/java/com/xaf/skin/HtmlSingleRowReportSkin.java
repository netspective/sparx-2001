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
    protected String breakFontAttrs = "face='verdana,arial,helvetica' size=2 color=navy";
    protected String captionCellAttrs = "";

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
		ReportContext.ColumnState[] states = rc.getStates();

		StringBuffer dataTable = new StringBuffer();
		if(horizontalLayout)
		{
			int colCount = 0;

			dataTable.append("<tr>");
			for(int i = 0; i < dataColsCount; i++)
			{
                ReportColumn column = columns.getColumn(i);

                if (column.getBreak()!=null)
                {
                    dataTable.append("<td height='10'></td></tr><tr><td align='left' bgcolor='#FFFBA5' colspan='2'><table border=0 cellspacing=0>");
                    dataTable.append("<tr><td align='left'><font "+breakFontAttrs+">"+ column.getBreak() +"</a></font></td></tr>");
                    dataTable.append("</table></td></tr><tr height='2' bgcolor='#ABA61B'><td colspan='2'></td><tr>");
                }

				ReportContext.ColumnState state = states[i];

                if(state.isHidden())
                    continue;

                String data =
                    state.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        state.getOutputFormat() :
                        column.getFormattedData(rc, 1, rowData, true);

				dataTable.append("<td " + captionCellAttrs + "><font "+dataHdFontAttrs+">"+
                        column.getHeading().getValue(rc)+  ":</font></td>");
				dataTable.append("<td align='"+ ALIGN_ATTRS[column.getAlignStyle()] +"'><font "+dataFontAttrs+">"+(state.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href='"+ state.getUrl() +"'"+ state.getUrlAnchorAttrs() +">"+ data +"</a>" : data)+"</font></td>");

				colCount++;
				if(colCount >= tableCols)
				{
					dataTable.append("</tr><tr>");
					colCount = 0;
				}
			}

			dataTable.append("</tr>");
            writer.write(defn.replaceOutputPatterns(rc, 1, rowData, dataTable.toString()));
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

	public void produceDataRows(Writer writer, ReportContext rc, Object[][] data, int startDataRow) throws IOException
	{
		// position the single row -- if we can't do "next" then no row exists
		if(data == null || data.length < 1)
			return;

        produceDataRows(writer, rc, data[0]);
	}
}