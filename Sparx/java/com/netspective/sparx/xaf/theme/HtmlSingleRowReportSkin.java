/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: HtmlSingleRowReportSkin.java,v 1.3 2003-06-03 14:26:02 aye.thu Exp $
 */

package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnsList;
import com.netspective.sparx.xaf.report.ReportContext;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class HtmlSingleRowReportSkin extends HtmlReportSkin
{
    public static final int HTMLFLAG_SKIPNULLCOLUMNS = HTMLFLAG_STARTCUSTOM;

    protected int tableCols;
    protected boolean horizontalLayout;
    protected String breakFontAttrs = "face='verdana,arial,helvetica' size=2 color=navy";
    protected String captionCellAttrs = "";

    public HtmlSingleRowReportSkin(boolean fullWidth, int tableCols, boolean horizontalLayout)
    {
        super(fullWidth);
        this.tableCols = tableCols;
        this.horizontalLayout = horizontalLayout;
        setFlag(HTMLFLAG_SHOW_BANNER | HTMLFLAG_SKIPNULLCOLUMNS);
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

                if(column.getBreak() != null)
                {
                    dataTable.append("<td height='10'></td></tr><tr><td align='left' bgcolor='#FFFBA5' colspan='2'><table border=0 cellspacing=0>");
                    dataTable.append("<tr><td align='left'><font " + breakFontAttrs + ">" + column.getBreak() + "</a></font></td></tr>");
                    dataTable.append("</table></td></tr><tr height='2' bgcolor='#ABA61B'><td colspan='2'></td><tr>");
                }

                ReportContext.ColumnState state = states[i];

                if(state.isHidden())
                    continue;

                if(flagIsSet(HTMLFLAG_SKIPNULLCOLUMNS) && rowData[column.getColIndexInArray()] == null)
                    continue;

                String data =
                        state.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        state.getOutputFormat() :
                        column.getFormattedData(rc, 1, rowData, true);

                dataTable.append("<td class=\"report-field-detail\"><nobr>" +
                        column.getHeading().getValue(rc) + "</nobr></td>");
                dataTable.append("<td class=\"report-detail\" align='" + ALIGN_ATTRS[column.getAlignStyle()] + "'>" +
                        (state.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href='" + state.getUrl() + "'" +
                        state.getUrlAnchorAttrs() + ">" + data + "</a>" : data) + "</td>");

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
        if(!rs.next())
        {
            writer.write("<tr><td class=\"report-detail\">No data found.</td></tr>");
            return;
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();

        Object[] rowData = new Object[resultSetColsCount];
        for(int i = 1; i <= resultSetColsCount; i++)
            rowData[i - 1] = rs.getObject(i);

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