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
 * $Id: DatabaseSqlPage.java,v 1.10 2002-12-28 20:07:36 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.StatementInfo;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.sql.StatementDialog;
import com.netspective.sparx.xaf.task.sql.StatementTask;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationPageException;
import com.netspective.sparx.util.value.ValueContext;

public class DatabaseSqlPage extends AceServletPage
{
    public final String getName()
    {
        return "sql";
    }

    public final String getEntityImageUrl()
    {
        return "sql.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return "SQL Statements";
    }

    public final String getHeading(ValueContext vc)
    {
        return "SQL Statements";
    }

    public static boolean useDialogParams(ValueContext vc)
    {
        return "yes".equals(vc.getRequest().getParameter("ui"));
    }

    public void handlePageBody(Writer writer, NavigationPathContext nc) throws NavigationPageException, IOException
    {
        String testItem = getTestCommandItem(nc);
        if(testItem != null)
        {
            handleUnitTestPageBegin(writer, nc, "Static SQL Unit Test");
            if (useDialogParams(nc))
                handleTestStatementWithUI(nc, testItem);
            else
                handleTestStatementNoUI(nc, testItem);
            handleUnitTestPageEnd(writer, nc);
        }
        else
        {
            ServletContext context = nc.getServletContext();
            StatementManager manager = StatementManagerFactory.getManager(context);
            manager.updateExecutionStatistics();
            manager.addMetaInfoOptions();
            transform(nc, manager.getDocument(nc.getServletContext(), null), com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "sql-browser-xsl");
        }
    }

    public void handleTestStatementWithUI(NavigationPathContext nc, String stmtId) throws NavigationPageException, IOException
    {
        ServletContext context = nc.getServletContext();
        StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = nc.getResponse().getWriter();

        StatementInfo si = manager.getStatement(nc.getServletContext(), null, stmtId);
        if(si != null)
        {
            out.write("<h1>SQL Unit Test: " + stmtId + "</h1>");
            StatementDialog dialog = si.getDialog();
            if(dialog != null)
            {
                DialogContext dc = dialog.createContext(context, nc.getServlet(), (HttpServletRequest) nc.getRequest(), (HttpServletResponse) nc.getResponse(), SkinFactory.getDialogSkin());
                dialog.prepareContext(dc);
                dialog.renderHtml(out, dc, true);
                out.write("<p>");
                out.write(si.getDebugHtml(nc, false, false, null));
            }
            else
                out.write("Statement '"+ stmtId +"' produced a NULL dialog.");
        }
        else
            out.write("Statement '"+ stmtId +"' not found in default context.");
    }

    public void handleTestStatementNoUI(NavigationPathContext nc, String stmtId) throws NavigationPageException, IOException
    {
        ServletContext context = nc.getServletContext();
        StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = nc.getResponse().getWriter();
        DatabaseContext dbc = DatabaseContextFactory.getContext(nc.getRequest(), context);

        out.write("<h1>SQL: " + stmtId + "</h1>");
        StatementInfo si = manager.getStatement(nc.getServletContext(), null, stmtId);
        try
        {
            if ("yes".equals(nc.getRequest().getParameter("pageable")))
            {
                try
                {
                    StatementTask task = new StatementTask();
                    task.setPageableReport(true);
                    task.setStmtName(stmtId);
                    String rows = nc.getRequest().getParameter("rows");
                    if (rows != null && rows.length() > 0)
                        task.setRowsPerPage(Integer.parseInt(rows));

                    TaskContext tc = new TaskContext(nc);
                    task.execute(tc);
                    if(tc.hasError())
                        out.write(tc.getErrorMessage());
                    else if(tc.hasResultMessage())
                        out.write(tc.getResultMessage());
                }
                catch (TaskExecuteException e)
                {
                    StringWriter msg = new StringWriter();
                    msg.write(e.toString());

                    PrintWriter pw = new PrintWriter(msg);
                    e.printStackTrace(pw);
                    out.write("<pre>");
                    out.write(msg.toString());
                    out.write("</pre>");
                }
            }
            else
                manager.produceReport(out, dbc, nc, null, SkinFactory.getReportSkin("report"), stmtId, null, null, null);
        }
        catch(Exception e)
        {
            StringWriter msg = new StringWriter();
            msg.write(e.toString());

            PrintWriter pw = new PrintWriter(msg);
            e.printStackTrace(pw);
            out.write("<pre>");
            out.write(msg.toString());
            out.write("</pre>");
        }
        out.write("<br>");
        out.write(si.getDebugHtml(nc, false, false, null));
    }
}
