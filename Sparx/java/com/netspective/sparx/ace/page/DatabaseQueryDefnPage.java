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
 * $Id: DatabaseQueryDefnPage.java,v 1.3 2002-09-23 03:47:27 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.page.PageContext;
import com.netspective.sparx.xaf.page.VirtualPath;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.querydefn.QueryBuilderDialog;
import com.netspective.sparx.xaf.querydefn.QueryDefinition;
import com.netspective.sparx.xaf.querydefn.QuerySelectDialog;

public class DatabaseQueryDefnPage extends AceServletPage
{
    public final String getName()
    {
        return "query-defn";
    }

    public final String getPageIcon()
    {
        return "sql_query_defn.gif";
    }

    public final String getCaption(PageContext pc)
    {
        return "SQL Query Definitions";
    }

    public final String getHeading(PageContext pc)
    {
        return "SQL Query Definitions";
    }

    public void handlePageBody(PageContext pc) throws ServletException, IOException
    {
        String testWhat = getTestCommandItem(pc);
        if(testWhat != null)
        {
            VirtualPath.FindResults results = pc.getActivePath();
            String[] testParams = results.unmatchedPathItems();
            // note -- testParams[0] will be the word "test"
            //         testParams[1] will be "test what"
            handleUnitTestPageBegin(pc, "Dynamic Query Unit Test");
            if(testWhat.equals("query-defn"))
                handleTestQueryDefn(pc, testParams[2]);
            else if(testWhat.equals("query-defn-dlg"))
                handleTestQueryDefnSelectDialog(pc, testParams[2], testParams[3]);
            handleUnitTestPageEnd(pc);
            return;
        }

        ServletContext context = pc.getServletContext();
        StatementManager manager = StatementManagerFactory.getManager(context);
        manager.updateExecutionStatistics();
        manager.addMetaInfoOptions();
        transform(pc, manager.getDocument(), com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "query-defn-browser-xsl");
    }

    public void handleTestQueryDefn(PageContext pc, String queryDefnId) throws ServletException, IOException
    {
        ServletContext context = pc.getServletContext();
        StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = pc.getResponse().getWriter();

        out.write("<h1>Query Definition: " + queryDefnId + "</h1>");

        QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
        if(queryDefn == null)
        {
            out.write("QueryDefinition not found.");
            return;
        }

        QueryBuilderDialog dialog = queryDefn.getBuilderDialog();
        dialog.renderHtml(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
    }

    public void handleTestQueryDefnSelectDialog(PageContext pc, String queryDefnId, String dialogId) throws ServletException, IOException
    {
        ServletContext context = pc.getServletContext();
        StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = pc.getResponse().getWriter();

        out.write("<h1>Query Definition: " + queryDefnId + ", Dialog: " + dialogId + "</h1>");

        QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
        QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogId);
        dialog.renderHtml(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
    }
}
