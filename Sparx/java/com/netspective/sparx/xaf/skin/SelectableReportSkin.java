package com.netspective.sparx.xaf.skin;

import com.netspective.sparx.xaf.report.*;
import com.netspective.sparx.xaf.sql.ResultSetScrollState;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

public class SelectableReportSkin extends HtmlReportSkin
{
    protected String rowHighlightColor;
    protected boolean highlightRow;

    public SelectableReportSkin()
    {
        super(true);
        rowHighlightColor = "#ccd9e5";
        highlightRow = false;
    }

    public SelectableReportSkin(boolean fullWidth)
    {
        super(fullWidth);
        rowHighlightColor = "#ccd9e5";
        highlightRow = false;
    }

    protected int getRowDecoratorPrependColsCount(ReportContext rc)
    {
       return (rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_IS_SELECTABLE) != 0 ? 1 : 0;
    }

    public void produceHeadingRowDecoratorAppend(Writer writer, ReportContext rc) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_IS_SELECTABLE) == 0)
            return;

        writer.write("<td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">");
        writer.write("&nbsp;");
        writer.write("</font></td><td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">&nbsp;&nbsp;</font></td>");

    }

    private int getTableColumnsCount(ReportContext rc)
    {
        return (rc.getVisibleColsCount() * 2) +
                (getRowDecoratorPrependColsCount(rc) * 2) +
                (getRowDecoratorAppendColsCount(rc) * 2) +
                +1; // each column has "spacer" in between, first column as spacer before too
    }

    public void produceDataRows(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
    {
        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(rc.getServletContext());
        String rowSepImgSrc = appConfig.getTextValue(rc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "report.row-sep-img-src", getRowSepImgSrc());

        Report defn = rc.getReport();
        ReportColumnsList columns = rc.getColumns();
        ReportContext.ColumnState[] states = rc.getStates();

        boolean addRowSeps = flagIsSet(HTMLFLAG_ADD_ROW_SEPARATORS);
        int rowsWritten = 0;
        int dataColsCount = columns.size();
        int tableColsCount = getTableColumnsCount(rc);

        ResultSetScrollState scrollState = rc.getScrollState();
        boolean paging = scrollState != null;

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();
        boolean isOddRow = false;

        while (rs.next())
        {
            // the reason why we need to copy the objects here is that
            // most JDBC drivers will only let data be ready one time; calling
            // the resultSet.getXXX methods more than once is problematic
            //
            Object[] rowData = new Object[resultSetColsCount];
            for (int i = 1; i <= resultSetColsCount; i++)
                rowData[i - 1] = rs.getObject(i);

            isOddRow = !isOddRow;
            int rowNum = rs.getRow();

            //writer.write("<tr " + (isOddRow ? dataOddRowAttrs : dataEvenRowAttrs) + "><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</td>");

            highlightRow = false;
            String prependHtml = produceDataRowDecoratorPrepend(rc, rowNum, rowData, isOddRow);
            if (highlightRow)
            {
                writer.write("<tr bgcolor='"+ rowHighlightColor + "'><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</td>" +
                        prependHtml);
            }
            else
            {
                writer.write("<tr " + (isOddRow ? dataOddRowAttrs : dataEvenRowAttrs) + "><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</td>" +
                        prependHtml);
            }

            for (int i = 0; i < dataColsCount; i++)
            {

                ReportColumn column = columns.getColumn(i);
                ReportContext.ColumnState state = states[i];

                if (state.isHidden())
                    continue;

                String data =
                        state.flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN) ?
                        state.getOutputFormat() :
                        column.getFormattedData(rc, rowNum, rowData, true);

                String dataTagsBegin = "";
                String dataTagsEnd = "";
                if (column.flagIsSet(ReportColumn.COLFLAG_NOWORDBREAKS))
                {
                    dataTagsBegin = "<nobr>";
                    dataTagsEnd = "</nobr>";
                }

                String singleRow = "<td align='" + ALIGN_ATTRS[column.getAlignStyle()] + "'>" + dataTagsBegin + "<font " + dataFontAttrs + ">" +
                        (state.flagIsSet(ReportColumn.COLFLAG_WRAPURL) ? "<a href=\"" + state.getUrl() + "\" " + state.getUrlAnchorAttrs() + ">" + data + "</a>" : data) +
                        "</font>" + dataTagsEnd + "</td><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</td>";

                writer.write(defn.replaceOutputPatterns(rc, rowNum, rowData, singleRow));
            }

            produceDataRowDecoratorAppend(writer, rc, rowNum, rowData, isOddRow);

            if (addRowSeps)
                writer.write("</tr><tr><td colspan='" + tableColsCount + "'><img src='" + rowSepImgSrc + "' height='1' width='100%'></td></tr>");
            else
                writer.write("</tr>");

            rowsWritten++;
            if (paging && rc.endOfPage())
                break;
        }

        if (rowsWritten == 0)
        {
            writer.write("</tr><tr><td colspan='" + tableColsCount + "'><font " + dataFontAttrs + ">No data found.</font></td></tr>");
            if (paging)
                scrollState.setNoMoreRows();
        }
        else if (paging)
        {
            scrollState.accumulateRowsProcessed(rowsWritten);
            if (rowsWritten < scrollState.getRowsPerPage())
                scrollState.setNoMoreRows();
        }
    }

    /**
     * Produce the HTML string to prepend to the row
     * @param rc
     * @param rowNum
     * @param rowData
     * @param isOddRow
     * @return
     * @throws IOException
     */
    public String produceDataRowDecoratorPrepend(ReportContext rc, int rowNum, Object[] rowData, boolean isOddRow) throws IOException
    {
        StringBuffer writer = new StringBuffer();

        if ((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_IS_SELECTABLE) == 0)
            return "";

        SingleValueSource value = getReportFrame(rc).getSelectableValue();
        HttpServletRequest request = (HttpServletRequest) rc.getRequest();
        String[] selectedValues = request.getParameterValues("_dc.selected_item_list");
        if (value != null)
        {
            writer.append("<td><input type=\"checkbox\" value=\"" + rowData[0] + "\" name=\"checkbox_" + rowData[0] +
                    "\" title=\"Click here to select the row.\" ");

            if (selectedValues != null)
            {
                for (int i = 0; i < selectedValues.length; i++)
                {
                    System.out.println(selectedValues[i] + " " + rowData[0]);
                    if (selectedValues[i].equalsIgnoreCase(rowData[0].toString()))
                    {
                        writer.append("checked");
                        highlightRow = true;
                    }
                }
            }
            writer.append(" onClick=\"handleRowCheckEvent(this, 'selected_item_list', " + rowData[0] + ")\">\n");
            writer.append("</td><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</font></td>");
        }
        return writer.toString();
    }


    public void produceDataRowDecoratorPrepend(Writer writer, ReportContext rc, int rowNum, Object[] rowData, boolean isOddRow) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_IS_SELECTABLE) == 0)
            return;

        SingleValueSource value = getReportFrame(rc).getSelectableValue();
        HttpServletRequest request = (HttpServletRequest) rc.getRequest();
        String[] selectedValues = request.getParameterValues("_dc.selected_item_list");
        if (value != null)
        {
            writer.write("<td><input type=\"checkbox\" value=\"" + rowData[0] + "\" name=\"checkbox_" + rowData[0] +
                    "\" title=\"Click here to select the row.\" ");

            if (selectedValues != null)
            {
                for (int i=0; i < selectedValues.length; i++)
                {
                    System.out.println(selectedValues[i] + " " + rowData[0]);
                    if (selectedValues[i].equalsIgnoreCase(rowData[0].toString()))
                        writer.write("checked");
                    highlightRow = true;
                }
            }
            writer.write(" onClick=\"handleRowCheckEvent(this, 'selected_item_list', " + rowData[0] + ")\">\n");
            writer.write("</td><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</font></td>");
        }

    }
}