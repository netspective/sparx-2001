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
 * $Id: QueryBuilderDialog.java,v 1.7 2002-09-03 22:29:19 aye.thu Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalDisplay;
import com.netspective.sparx.xaf.form.field.BooleanField;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.field.SeparatorField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnsList;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportDestination;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.StandardReport;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.ResultInfo;
import com.netspective.sparx.xaf.sql.ResultSetScrollState;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.QueryDefnSelectsListValue;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.QueryDefnFieldsListValue;

public class QueryBuilderDialog extends Dialog
{
    static public final int QBDLGFLAG_HIDE_OUTPUT_DESTS = DLGFLAG_CUSTOM_START;
    static public final int QBDLGFLAG_ALLOW_DEBUG = QBDLGFLAG_HIDE_OUTPUT_DESTS * 2;
    static public final int QBDLGFLAG_HIDE_CRITERIA = QBDLGFLAG_ALLOW_DEBUG * 2;
    static public final int QBDLGFLAG_ALWAYS_SHOW_RSNAV = QBDLGFLAG_HIDE_CRITERIA * 2;
    static public final int QBDLGFLAG_ALLOW_MULTIPLE_QSSS = QBDLGFLAG_ALWAYS_SHOW_RSNAV * 2; // allow multiple query select scroll states to be active

    static public final String QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME = "queryDefnName";
    static public final String QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME = "active-query-select-scroll-state";
    static public final String QBDIALOG_RESORT_PARAMNAME = "_qbd_resort";

    static public final int MAX_ROWS_IN_SINGLE_BROWSER_PAGE = 9999;

    static public final int OUTPUTSTYLE_HTML = 0;
    static public final int OUTPUTSTYLE_TEXT_CSV = 1;
    static public final int OUTPUTSTYLE_TEXT_TAB = 2;

    private int maxConditions;
    private QueryDefinition queryDefn;

    public QueryBuilderDialog()
    {
        setName("queryDialog");
        setLoopEntries(true);
    }

    public QueryBuilderDialog(QueryDefinition queryDefn)
    {
        setName("queryDialog");
        setQueryDefn(queryDefn);
        setFlag(QBDLGFLAG_ALLOW_DEBUG);
        setMaxConditions(5);
        setLoopEntries(true);
    }

    public void importFromXml(String packageName, Element elem)
    {
        super.importFromXml(packageName, elem);

        if(elem.getAttribute("show-output-dests").equals("no"))
            setFlag(QBDLGFLAG_HIDE_OUTPUT_DESTS);

        if(elem.getAttribute("allow-debug").equals("yes"))
            setFlag(QBDLGFLAG_ALLOW_DEBUG);

        if(elem.getAttribute("show-criteria").equals("no"))
            setFlag(QBDLGFLAG_HIDE_CRITERIA);

        if(elem.getAttribute("always-show-rs-nav").equals("yes"))
            setFlag(QBDLGFLAG_ALWAYS_SHOW_RSNAV);
    }

    public void addInputFields()
    {
        int lastConditionNum = maxConditions - 1;
        QueryDefnFieldsListValue fieldsList = null;
        if(queryDefn.isDynamic())
        {
            fieldsList = new QueryDefnFieldsListValue();
            fieldsList.setQueryDefn(queryDefn);
        }
        else
            fieldsList = (QueryDefnFieldsListValue) ValueSourceFactory.getListValueSource("query-defn-fields:" + queryDefn.getName());
        ListValueSource compList = ValueSourceFactory.getListValueSource("sql-comparisons:all");

        DialogField hiddenName = new DialogField(QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, null);
        hiddenName.setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);
        addField(hiddenName);
        addField(new SeparatorField("conditions_separator", "Conditions"));
        for(int i = 0; i < maxConditions; i++)
        {
            SelectField queryFieldsSelect =
                    new SelectField("field", null, SelectField.SELECTSTYLE_COMBO, fieldsList);

            SelectField compareSelect =
                    new SelectField("compare", null, SelectField.SELECTSTYLE_COMBO, compList);

            TextField valueText = new TextField("value", null);

            DialogField condition = new DialogField();
            condition.setSimpleName("condition_" + i);
            condition.addChildField(queryFieldsSelect);
            condition.addChildField(compareSelect);
            condition.addChildField(valueText);

            if(i != lastConditionNum)
            {
                SelectField joinSelect =
                        new SelectField("join", null, SelectField.SELECTSTYLE_COMBO, " ;and;or");
                condition.addChildField(joinSelect);
            }

            if(i > 0)
            {
                condition.addConditionalAction(new DialogFieldConditionalDisplay(condition, "condition_" + (i - 1) + ".join", "control.value != ' '"));
            }

            addField(condition);
        }
    }

    public void addResultsSepatorField()
    {
        addField(new SeparatorField("results_separator", "Results"));
    }

    public void addOutputDestinationFields()
    {
        DialogField output = new DialogField();
        output.setSimpleName("output");
        output.setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);

        SelectField outputStyle = new SelectField("style", "Style", SelectField.SELECTSTYLE_COMBO, "HTML=0;CSV Text File=1;Tab-delimited Text File=2");
        outputStyle.setDefaultValue(new StaticValue("0"));
        output.addChildField(outputStyle);

        /* the numbers should match com.netspective.sparx.xaf.report.ReportDestination.DEST_* */

        SelectField outputDest = new SelectField("destination", "Destination", SelectField.SELECTSTYLE_COMBO, "Browser (HTML) multiple pages=0;Browser (HTML) single page=1;Download File=2;E-mail as Attachment=3");
        outputDest.setDefaultValue(new StaticValue("0"));
        output.addChildField(outputDest);

        SelectField rowsPerPage = new SelectField("rows_per_page", null, SelectField.SELECTSTYLE_COMBO, "10 rows per page=10;20 rows per page=20;30 rows per page=30");
        rowsPerPage.setDefaultValue(new StaticValue("10"));
        rowsPerPage.addConditionalAction(new DialogFieldConditionalDisplay(rowsPerPage, "output.destination", "control.selectedIndex == 0"));
        output.addChildField(rowsPerPage);

        addField(output);
    }


    public void addDisplayOptionsFields()
    {
        QueryDefnFieldsListValue fieldsList = null;
        if(queryDefn.isDynamic())
        {
            fieldsList = new QueryDefnFieldsListValue();
            fieldsList.setQueryDefn(queryDefn);
        }
        else
            fieldsList = (QueryDefnFieldsListValue) ValueSourceFactory.getListValueSource("query-defn-fields:" + queryDefn.getName());
        ListValueSource compList = ValueSourceFactory.getListValueSource("sql-comparisons:all");

        SelectField predefinedSels = null;
        List predefinedSelects = queryDefn.getSelectsList();
        if(predefinedSelects.size() > 0)
        {
            ListValueSource selectsList = ValueSourceFactory.getListValueSource("query-defn-selects:" + queryDefn.getName());
            predefinedSels = new SelectField("predefined_select", "Display", SelectField.SELECTSTYLE_COMBO, selectsList);
        }

        SelectField displayFields =
                new SelectField("display_fields", null, SelectField.SELECTSTYLE_MULTIDUAL, fieldsList);
        displayFields.setMultiDualCaptions("Available Display Fields", "Show Fields");
        displayFields.setMultiDualWidth(150);
        displayFields.setSize(7);

        SelectField sortFields =
                new SelectField("sort_fields", null, SelectField.SELECTSTYLE_MULTIDUAL, fieldsList);
        sortFields.setMultiDualCaptions("Available Sort Fields", "Sort Fields");
        sortFields.setMultiDualWidth(150);
        sortFields.setSize(5);

        if(predefinedSels != null)
        {
            displayFields.addConditionalAction(new DialogFieldConditionalDisplay(displayFields, "options.predefined_select", "control.options[control.selectedIndex].value == '" + QueryDefnSelectsListValue.CUSTOMIZE + "'"));
            sortFields.addConditionalAction(new DialogFieldConditionalDisplay(sortFields, "options.predefined_select", "control.options[control.selectedIndex].value == '" + QueryDefnSelectsListValue.CUSTOMIZE + "'"));
        }

        DialogField options = new DialogField();
        options.setSimpleName("options");
        options.setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);
        if(predefinedSels != null)
            options.addChildField(predefinedSels);

        if(flagIsSet(QBDLGFLAG_ALLOW_DEBUG))
            options.addChildField(new BooleanField("debug", "View Generated SQL", BooleanField.BOOLSTYLE_CHECK, 0));

        addField(options);
        addField(displayFields);
        addField(sortFields);
    }

    public void createContents()
    {
        clearFields();

        addInputFields();
        addResultsSepatorField();
        addOutputDestinationFields();
        addDisplayOptionsFields();

        addField(new DialogDirector());
        addField(new ResultSetNavigatorButtonsField());
    }

    public int getMaxConditions()
    {
        return maxConditions;
    }

    public void setMaxConditions(int value)
    {
        maxConditions = value;
        createContents();
    }

    public QueryDefinition getQueryDefn()
    {
        return queryDefn;
    }

    public void setQueryDefn(QueryDefinition value)
    {
        queryDefn = value;
    }

    public void makeStateChanges(DialogContext dc, int stage)
    {
        Iterator k = this.getFields().iterator();
        while(k.hasNext())
        {
            DialogField field = (DialogField) k.next();
            field.makeStateChanges(dc, stage);
        }

        dc.setValue(QBDIALOG_QUERYDEFN_NAME_PASSTHRU_FIELDNAME, queryDefn.getName());
        if(dc.inExecuteMode() && stage == DialogContext.STATECALCSTAGE_FINAL)
        {
            dc.setFlag("conditions_separator", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("results_separator", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("output", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("options", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("display_fields", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("sort_fields", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("director", DialogField.FLDFLAG_INVISIBLE);
            dc.clearFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);

            int lastCondition = maxConditions - 1;
            int flag = flagIsSet(QBDLGFLAG_HIDE_CRITERIA) ? DialogField.FLDFLAG_INVISIBLE : DialogField.FLDFLAG_READONLY;
            for(int i = 0; i < maxConditions; i++)
            {
                dc.setFlag("condition_" + i, flag);
            }
        }
        else
        {
            if(flagIsSet(QBDLGFLAG_HIDE_OUTPUT_DESTS))
                dc.setFlag("output", DialogField.FLDFLAG_INVISIBLE);
            dc.setFlag("rs_nav_buttons", DialogField.FLDFLAG_INVISIBLE);
        }
    }

    public QuerySelect createSelect(DialogContext dc)
    {
        QuerySelect select = new QuerySelect(queryDefn);

        boolean customizing = true;
        String predefinedSel = dc.getValue("options.predefined_select");
        if(predefinedSel != null && !predefinedSel.equals(QueryDefnSelectsListValue.CUSTOMIZE))
        {
            customizing = false;
            select.importFromSelect(queryDefn.getSelect(predefinedSel));
        }

        if(customizing)
        {
            String[] display = dc.getValues("display_fields");
            if(display != null && display.length > 0)
                select.addReportFields(display);
            else
                select.addReportField("*");
        }

        for(int i = 0; i < maxConditions; i++)
        {
            String conditionId = "condition_" + i;
            String value = dc.getValue(conditionId + ".value");
            String join = dc.getValue(conditionId + ".join");

            if(value != null && value.length() > 0)
            {
                select.addCondition(
                        dc.getValue(conditionId + ".field"),
                        dc.getValue(conditionId + ".compare"),
                        value, join);
            }

            if(join == null || join.equals(" "))
                break;
        }

        if(customizing)
        {
            String[] sort = dc.getValues("sort_fields");
            if(sort != null && sort.length > 0)
                select.addOrderBy(sort);
        }

        return select;
    }

    public void handleSortOrderChange(DialogContext dc, QuerySelectScrollState activeState, String resortBy)
    {
        try
        {
            activeState.populateData(dc);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        QueryDefinition.QueryFieldSortInfo activeSortFieldInfo = activeState.getSortFieldInfo();
        String activeSortFieldName = activeSortFieldInfo != null ? activeSortFieldInfo.getField().getName() : null;
        if(activeSortFieldInfo != null && resortBy.equals(activeSortFieldName))
            resortBy = activeSortFieldInfo.isDescending() ? resortBy : ("-" + resortBy);

        dc.setValue("sort_order", resortBy);
    }

    public void executeHtml(Writer writer, DialogContext dc, int destination) throws IOException
    {
        String transactionId = dc.getTransactionId();
        HttpSession session = dc.getSession();
        HttpServletRequest request = (HttpServletRequest) dc.getRequest();
        QuerySelectScrollState state = (QuerySelectScrollState) session.getAttribute(transactionId);

        boolean keepScrollState = true;
        int pageSize = -1;
        if(destination == ReportDestination.DEST_BROWSER_SINGLE_PAGE)
        {
            pageSize = MAX_ROWS_IN_SINGLE_BROWSER_PAGE;
            keepScrollState = false;
        }
        else if(destination == ReportDestination.DEST_FILE_DOWNLOAD || destination == ReportDestination.DEST_FILE_EMAIL)
        {
            pageSize = Integer.MAX_VALUE;
            keepScrollState = false;
        }

        try
        {
            /*
                If the state is not found, then we have not executed at all yet;
                if the state is found and it's the initial execution then it means
                that the user has pressed the "back" button -- which means we
                should reset the state management.
             */
            if(state == null || (state != null && dc.isInitialExecute()))
            {
                // if our transaction does not have a scroll state, but there is an active scroll state available, then it
                // means that we need to get close the previous one and remove the attribute so that the connection can be
                // closed and returned to the pool
                QuerySelectScrollState activeState = (QuerySelectScrollState) session.getAttribute(QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);

                String resortBy = dc.getRequest().getParameter(QBDIALOG_RESORT_PARAMNAME);
                if(activeState != null && resortBy != null)
                    handleSortOrderChange(dc, activeState, resortBy);

                if(activeState != null && !flagIsSet(QBDLGFLAG_ALLOW_MULTIPLE_QSSS))
                {
                    activeState.close();
                    session.removeAttribute(QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);
                }

                QuerySelect select = createSelect(dc);
                // check to see if user has created a field called 'rows_per_page'
                // which overwrites the default one
                String rowsPerPageStr = dc.getValue("rows_per_page");
                if(rowsPerPageStr == null || rowsPerPageStr.length() == 0)
                    rowsPerPageStr = dc.getValue("output.rows_per_page");
                state = new QuerySelectScrollState(DatabaseContextFactory.getContext(dc), dc, select, pageSize == -1 ? (rowsPerPageStr == null ? 20 : Integer.parseInt(rowsPerPageStr)) : pageSize, ResultSetScrollState.SCROLLTYPE_USERESULTSET);
                if(state.isValid())
                {
                    if(keepScrollState)
                    {
                        session.setAttribute(transactionId, state);
                        session.setAttribute(QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME, state);
                    }
                }
                else
                {
                    writer.write("Could not execute SQL: " + state.getErrorMsg());
                    return;
                }
            }

            String stateReqAttrName = transactionId + "_state";
            request.setAttribute(stateReqAttrName, state);

            if (request.getParameter(ResultSetNavigatorButtonsField.RSNAV_BUTTONNAME_NEXT) != null)
                state.setPageDelta(1);
            else if (request.getParameter(ResultSetNavigatorButtonsField.RSNAV_BUTTONNAME_PREV) != null)
                state.setPageDelta(-1);
            else if (request.getParameter(ResultSetNavigatorButtonsField.RSNAV_BUTTONNAME_LAST) != null)
                state.setPage(state.getTotalPages());
            else if (request.getParameter(ResultSetNavigatorButtonsField.RSNAV_BUTTONNAME_FIRST) != null)
                state.setPage(1);

            if(destination == ReportDestination.DEST_BROWSER_MULTI_PAGE || destination == ReportDestination.DEST_BROWSER_SINGLE_PAGE)
            {
                state.produceReport(writer, dc);
            }
            else
            {
                ReportDestination reportDest = new ReportDestination(destination, dc, state.getSkin());
                state.produceReport(reportDest.getWriter(), dc);
                reportDest.getWriter().close();
                writer.write(reportDest.getUserMessage());
            }

            if(! keepScrollState)
            {
                request.removeAttribute(stateReqAttrName);
                state.close();
                state = null;
            }
        }
        catch(Exception e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

            QuerySelect select = createSelect(dc);
            String sql = select.getSql(dc) + "<p><br>" + select.getBindParamsDebugHtml(dc);
            writer.write(e.toString() + "<p><pre><code>" + (sql + (sql == null ? "<p>" + select.getErrorSql() : "")) + "\n" + stack.toString() + "</code></pre>");
        }
    }

    public void executeText(Writer writer, DialogContext dc, int destination, ReportSkin skin) throws IOException
    {
        QuerySelect select = createSelect(dc);
        DatabaseContext dbc = DatabaseContextFactory.getContext(dc);
        try
        {
            ResultInfo ri = select.execute(dbc, dc);
            ResultSet rs = ri.getResultSet();

            if(rs != null)
            {
                Report reportDefn = new StandardReport();
                reportDefn.initialize(rs, null);

                ReportColumnsList rcl = reportDefn.getColumns();
                List selectFields = select.getReportFields();
                for(int i = 0; i < rcl.size(); i++)
                {
                    ReportColumn rc = ((QueryField) selectFields.get(i)).getReportColumn();
                    if(rc != null)
                        rcl.getColumn(i).importFromColumn(rc);
                }

                ReportContext rc = new ReportContext(dc, reportDefn, skin);
                if(destination == ReportDestination.DEST_BROWSER_MULTI_PAGE || destination == ReportDestination.DEST_BROWSER_SINGLE_PAGE)
                {
                    skin.produceReport(writer, rc, rs);
                }
                else
                {
                    ReportDestination reportDest = new ReportDestination(destination, dc, skin);
                    skin.produceReport(reportDest.getWriter(), rc, rs);
                    reportDest.getWriter().close();
                    writer.write(reportDest.getUserMessage());
                }
            }
            else
            {
                writer.write("Unable to execute SQL Statement.");
            }
        }
        catch(Exception e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

            String sql = select.getSql(dc) + "<p><br>" + select.getBindParamsDebugHtml(dc);
            writer.write(e.toString() + "<p><pre><code>" + (sql + (sql == null ? "<p>" + select.getErrorSql() : "")) + "\n" + stack.toString() + "</code></pre>");
        }
    }

    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        String debugStr = dc.getValue("options.debug");
        if(debugStr != null && debugStr.equals("1"))
        {
            QuerySelect select = createSelect(dc);
            String sql = select.getSql(dc);
            writer.write("<p><pre><code>SQL:<p>" + sql + (sql == null ? "<p>" + select.getErrorSql() : select.getBindParamsDebugHtml(dc)) + "</code></pre>");
            return;
        }

        // check to see if the user has created field(s) called 'output_style' and 'output_destination'.
        // If they are not defined, check for their counterparts defined through the 'show-output-dests' attribute,
        String outputStyleStr = dc.getValue("output_style");
        if(outputStyleStr == null || outputStyleStr.length() == 0)
            outputStyleStr = dc.getValue("output.style");
        String outputDestStr = dc.getValue("output_destination");
        if(outputDestStr == null || outputDestStr.length() == 0)
            outputDestStr = dc.getValue("output.destination");

        int outputStyle = outputStyleStr != null ? Integer.parseInt(outputStyleStr) : OUTPUTSTYLE_HTML;
        int outputDest = outputDestStr != null ? Integer.parseInt(outputDestStr) : ReportDestination.DEST_BROWSER_MULTI_PAGE;

        switch(outputStyle)
        {
            case OUTPUTSTYLE_HTML:
                executeHtml(writer, dc, outputDest);
                break;

            case OUTPUTSTYLE_TEXT_CSV:
                executeText(writer, dc, outputDest, SkinFactory.getReportSkin("text-csv"));
                break;

            case OUTPUTSTYLE_TEXT_TAB:
                executeText(writer, dc, outputDest, SkinFactory.getReportSkin("text-tab"));
                break;

            default:
                writer.write("Output Style " + outputStyle + " is unknown.");
        }
    }

    /**
     * return the output from the execute method or the execute method and the dialog (which contains
     * the ResultSetNavigagors next/prev buttons). If there is only one page or scrolling is not being
     * performed (state == null) then only show the output of the query. However, if there is more than
     * one page or the number of pages is unknown, then show the entire dialog.
     */

    public void renderHtml(Writer writer, DialogContext dc, boolean contextPreparedAlready) throws IOException
    {
        if(flagIsSet(QBDLGFLAG_ALWAYS_SHOW_RSNAV))
        {
            super.renderHtml(writer, dc, contextPreparedAlready);
            return;
        }

        if(!contextPreparedAlready)
            prepareContext(dc);

        if(dc.inExecuteMode())
        {
            execute(writer, dc);
            QuerySelectScrollState state = (QuerySelectScrollState) dc.getRequest().getAttribute(dc.getTransactionId() + "_state");
            if(state != null)
            {
                int totalPages = state.getTotalPages();
                if(totalPages == -1 || totalPages > 1)
                {
                    writer.write(getLoopSeparator());
                    dc.getSkin().renderHtml(writer, dc);
                }
                else
                {
                    String transactionId = dc.getTransactionId();
                    HttpSession session = dc.getSession();
                    session.removeAttribute(transactionId);
                    session.removeAttribute(QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);

                    try
                    {
                        state.close();
                    }
                    catch(SQLException e)
                    {
                        throw new IOException(e.toString());
                    }
                }
            }
        }
        else
        {
            dc.getSkin().renderHtml(writer, dc);
        }
    }
}