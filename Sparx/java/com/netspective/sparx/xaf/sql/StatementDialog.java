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
 * @author Aye K. Thu
 */

package com.netspective.sparx.xaf.sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.querydefn.ResultSetNavigatorButtonsField;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

public class StatementDialog extends Dialog
{
    static public final int STMTDLGFLAG_HIDE_OUTPUT_DESTS = DLGFLAG_CUSTOM_START;
    static public final int STMTDLGFLAG_ALLOW_DEBUG = STMTDLGFLAG_HIDE_OUTPUT_DESTS * 2;
    static public final int STMTDLGFLAG_ALWAYS_SHOW_RSNAV = STMTDLGFLAG_ALLOW_DEBUG * 2;
    static public final int STMTDLGFLAG_ALLOW_MULTIPLE_QSSS = STMTDLGFLAG_ALWAYS_SHOW_RSNAV * 2; // allow multiple query select scroll states to be active

    static public final String STMTDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME = "active-statement-scroll-state";

    private String stmtName;
    private int rowsPerPage;
    private StatementInfo statementInfo;
    private String reportName;
    private String skinName;

    public StatementDialog(StatementInfo si, String reportName, String skinName)
    {
        setName("statementDialog");
        setLoopEntries(true);
        setRetainAllRequestParams(true);
        setStatementInfo(si);
        setSkinName(skinName);
        setReportName(reportName);
        ResultSetNavigatorButtonsField btn = new ResultSetNavigatorButtonsField();
        btn.clearFlag(DialogField.FLDFLAG_INVISIBLE);
        addField(btn);
    }

    public void setReportName(String reportName)
    {
        this.reportName = reportName;
    }

    public void setSkinName(String skinName)
    {
        this.skinName = skinName;
    }

    public void setStatementInfo(StatementInfo statementInfo)
    {
        this.statementInfo = statementInfo;
    }

    public String getReportName()
    {
        return reportName;
    }

    public String getSkinName()
    {
        return skinName;
    }

    public StatementInfo getStatementInfo()
    {
        return statementInfo;
    }

    public int getRowsPerPage()
    {
        return rowsPerPage;
    }

    /**
     * Set the number of rows to display on each report page
     *
     * @param rows number of rows
     */
    public void setRowsPerPage(int rows)
    {
        this.rowsPerPage = rows;
    }
    /**
     * Get the static query statement associated with the dialog
     *
     * @param stmt query name
     */
    public StatementInfo getStatement()
    {
        return this.statementInfo;
    }

    /**
     * Import the dialog configuration from XML
     *
     * @param packageName
     * @param elem DOM element
     */
    public void importFromXml(String packageName, Element elem)
    {
        super.importFromXml(packageName, elem);

    }

    /**
     * return the output from the execute method or the execute method and the dialog (which contains
     * the ResultSetNavigagors next/prev buttons). If there is only one page or scrolling is not being
     * performed (state == null) then only show the output of the query. However, if there is more than
     * one page or the number of pages is unknown, then show the entire dialog.
     */

    public void renderHtml(Writer writer, DialogContext dc, boolean contextPreparedAlready) throws IOException
    {
        if (flagIsSet(STMTDLGFLAG_ALWAYS_SHOW_RSNAV))
        {
            super.renderHtml(writer, dc, contextPreparedAlready);
            return;
        }

        if (!contextPreparedAlready)
            prepareContext(dc);

        // Dialog is ALWAYS in EXECUTE mode
        execute(writer, dc);
        ResultSetScrollState state = (ResultSetScrollState) dc.getRequest().getAttribute(dc.getTransactionId() + "_state");
        if (state != null)
        {
            int totalPages = state.getTotalPages();
            if (totalPages == -1 || totalPages > 1)
            {
                writer.write(getLoopSeparator());
                dc.getSkin().renderHtml(writer, dc);
            }
            else
            {
                String transactionId = dc.getTransactionId();
                HttpSession session = dc.getSession();
                session.removeAttribute(transactionId);
                session.removeAttribute(STMTDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);

                try
                {
                    state.close();
                }
                catch (SQLException e)
                {
                    throw new IOException(e.toString());
                }
            }
        }
        else
        {
            System.out.println("STATE IS NULL");
            dc.getSkin().renderHtml(writer, dc);
        }
    }

    /**
     * Execute the dialog and produce HTML for the output
     *
     * @param writer
     * @param dc dialog context
     * @param destination
     */
    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        String transactionId = dc.getTransactionId();
        HttpSession session = dc.getSession();
        HttpServletRequest request = (HttpServletRequest) dc.getRequest();
        StatementScrollState state = (StatementScrollState) session.getAttribute(transactionId);

        boolean keepScrollState = true;
        int pageSize = this.rowsPerPage;

        try
        {
            /*
                If the state is not found, then we have not executed at all yet;
                if the state is found and it's the initial execution then it means
                that the user has pressed the "back" button -- which means we
                should reset the state management.
             */
            if (state == null || (state != null && dc.isInitialExecute()))
            {
                ResultSetScrollState activeState = (ResultSetScrollState) session.getAttribute(STMTDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);
                // If a scroll state object already exists and we don't allow multiple scroll
                // states to exist, close out the existing scroll state object and remove it from the session
                if (activeState != null && !flagIsSet(STMTDLGFLAG_ALLOW_MULTIPLE_QSSS))
                {
                    activeState.close();
                    session.removeAttribute(STMTDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);
                }
                // create a new scroll state object for this query
                DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());
                String dataSourceId = statementInfo.getDataSource() != null ?statementInfo.getDataSource().getValue(dc) : null;
                state = new StatementScrollState(statementInfo, dbContext, dc, dataSourceId, getReportName(), getSkinName(),
                        getRowsPerPage(), ResultSetScrollState.SCROLLTYPE_USERESULTSET);

                if(state.isValid())
                {
                    if(keepScrollState)
                    {
                        session.setAttribute(transactionId, state);
                        session.setAttribute(STMTDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME, state);
                    }
                }
                else
                {
                    writer.write("Could not execute SQL: " + this.statementInfo.getSql(dc));
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

            state.produceReport(writer, dc);

            if (!keepScrollState)
            {
                request.removeAttribute(stateReqAttrName);
                state.close();
                state = null;
            }
        }
        catch (Exception e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            String sql = statementInfo.getSql(dc) + "<p><br>";
            writer.write(e.toString() + "<p><pre><code>" + (sql + (sql == null ? "<p>"
                     : "")) + "\n" + stack.toString() + "</code></pre>");
        }
    }

}
