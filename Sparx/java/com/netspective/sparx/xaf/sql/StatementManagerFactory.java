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
 * $Id: StatementManagerFactory.java,v 1.6 2002-12-23 04:43:24 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.naming.NamingException;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

public class StatementManagerFactory implements Factory
{
    static final String ATTRNAME_STATEMENTMGR = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "statement-mgr";
    private static Map managers = new HashMap();

    /**
     * Method used for retrieving a <code>StatementManager</code> object representing a
     * SQL XML file. The factory first looks for the <code>StatementManager</code> object
     * from a <code>Map</code> and if it doesn't exist, it creates a new one and adds it
     * to the map of statement managers.
     *
     * @param file static SQL XML file name
     * @return StatementManager
     */
    public static StatementManager getManager(String file)
    {
        StatementManager activeManager = (StatementManager) managers.get(file);
        if(activeManager == null)
        {
            activeManager = new StatementManager(new File(file));
            managers.put(file, activeManager);
        }
        return activeManager;
    }

    /**
     * Method used for retrieving a <code>StatementManager</code> object within a web application context.
     * The factory retrieves the static SQL XML file name from <code>app.sql.source-file</code> configuraton entry
     * defined in <code>WEB-INF/conf/sparx.xml</code> of the web application.
     *
     * @param context the servlet context
     * @return StatementManager
     */
    public static StatementManager getManager(ServletContext context)
    {
        StatementManager manager = (StatementManager) context.getAttribute(ATTRNAME_STATEMENTMGR);
        if(manager != null)
            return manager;

        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
        ValueContext vc = new ServletValueContext(context, null, null, null);
        manager = getManager(appConfig.getTextValue(vc, "app.sql.source-file"));
        manager.initializeForServlet(context);
        context.setAttribute(ATTRNAME_STATEMENTMGR, manager);
        return manager;
    }

    /**
     * cmdParams for StatementCommands should look like:
     *   0 statement name (required)
     *   1 reportId name (optional, may be empty or set to "-" to mean default)
     *   2 rows per page (optional, may be empty or set to "-" to mean unlimited)
     *   3 skin name (optional, may be empty or set to "-" to mean "none")
     *   4.. + are same as DialogCommands to show dialog next to a statement
     */


    public static StatementCommands getStatementCommands(String cmdParams)
    {
        return new StatementCommands(cmdParams);
    }

    public static class StatementCommands
    {
        static public final int UNLIMITED_ROWS = Integer.MAX_VALUE;

        private String statementName;
        private int rowsPerPage;
        private String skinName;
        private String reportId;
        private DialogManagerFactory.DialogCommands dialogCommands;

        public StatementCommands(String cmdParams)
        {
            StringTokenizer st = new StringTokenizer(cmdParams, ",");
            statementName = st.nextToken();

            if(st.hasMoreTokens())
            {
                reportId = st.nextToken();
                if(reportId.length() == 0 || reportId.equals("-"))
                    reportId = null;
            }
            else
                reportId = null;

            if(st.hasMoreTokens())
            {
                String rowsPerPageStr = st.nextToken();
                if(rowsPerPageStr.length() == 0 || rowsPerPageStr.equals("-"))
                    rowsPerPage = UNLIMITED_ROWS;
                else
                    rowsPerPage = Integer.parseInt(rowsPerPageStr);
            }
            else
                rowsPerPage = UNLIMITED_ROWS;

            if(st.hasMoreTokens())
            {
                skinName = st.nextToken();
                if(skinName.length() == 0 || skinName.equals("-"))
                    skinName = null;
            }
            else
                skinName = null;

            if(st.hasMoreTokens())
                dialogCommands = new DialogManagerFactory.DialogCommands(st);
        }

        public String getStatementName()
        {
            return statementName;
        }

        public String getSkinName()
        {
            return skinName;
        }

        public void setStatementName(String statementName)
        {
            this.statementName = statementName;
        }

        public void setSkinName(String skinName)
        {
            this.skinName = skinName;
        }

        public String generateCommand()
        {
            StringBuffer sb = new StringBuffer(statementName);
            sb.append(",");
            sb.append(reportId != null ? reportId : "-");
            sb.append(",");
            sb.append(rowsPerPage != UNLIMITED_ROWS ? Integer.toString(rowsPerPage) : "-");
            sb.append(",");
            sb.append(skinName != null ? skinName : "-");
            if(dialogCommands != null)
            {
                sb.append(",");
                sb.append(dialogCommands.generateCommand());
            }
            return sb.toString();
        }

        public void handleStatement(ValueContext vc, boolean unitTest) throws IOException, StatementNotFoundException, NamingException, SQLException
        {
            PrintWriter out = vc.getResponse().getWriter();
            javax.servlet.ServletContext context = vc.getServletContext();

            com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("StatementManager not found in ServletContext");
                return;
            }

            if(dialogCommands != null)
                out.write("<table><tr valign='top'><td>");

            if(rowsPerPage > 0 && rowsPerPage < UNLIMITED_ROWS)
            {
                // Special Case: This static query must produce a report that is pageable
                StatementDialog stmtDialog = new StatementDialog(manager.getStatement(statementName), reportId, skinName);
                stmtDialog.setRowsPerPage(rowsPerPage);
                DialogSkin dialogSkin = com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin();
                DialogContext dc = stmtDialog.createContext(context, vc.getServlet(),
                        (javax.servlet.http.HttpServletRequest) vc.getRequest(),
                        (javax.servlet.http.HttpServletResponse) vc.getResponse(), dialogSkin);
                stmtDialog.prepareContext(dc);
                stmtDialog.renderHtml(out, dc, false);
            }
            else
            {
                ReportSkin skin = skinName != null ? SkinFactory.getReportSkin(skinName) : SkinFactory.getDefaultReportSkin();
                DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), context);
                manager.produceReport(out, dbContext, vc, null, skin, statementName, null, null);
            }

            if(dialogCommands != null)
            {
                out.write("</td><td>");
                dialogCommands.handleDialog(vc, unitTest);
                out.write("</td></tr></td></table>");
            }
        }
    }

    /**
     * cmdParams for QuerySelectDialogCommands should look like:
     *   0 query definition name (required)
     *   1 dialog name (required)
     *   2 skin name (optional, may be empty or set to "-" to mean "none")
     */


    public static QuerySelectDialogCommands getQuerySelectDialogCommands(String cmdParams)
    {
        return new QuerySelectDialogCommands(cmdParams);
    }

    public static class QuerySelectDialogCommands
    {
        private String dialogName;
        private String source;
        private String skinName;

        public QuerySelectDialogCommands(String cmdParams)
        {
            StringTokenizer st = new StringTokenizer(cmdParams, ",");
            source = st.nextToken();

            if(st.hasMoreTokens())
                dialogName = st.nextToken();
            else
                dialogName = "unknown";

            if(st.hasMoreTokens())
            {
                skinName = st.nextToken();
                if(skinName.equals("-"))
                    skinName = null;
            }
            else
                skinName = null;
        }

        public String getSource()
        {
            return source;
        }

        public String getDialogName()
        {
            return dialogName;
        }

        public String getSkinName()
        {
            return skinName;
        }

        public void setSource(String dataCmd)
        {
            this.source = dataCmd;
        }

        public void setDialogName(String dialogName)
        {
            this.dialogName = dialogName;
        }

        public void setSkinName(String skinName)
        {
            this.skinName = skinName;
        }

        public String generateCommand()
        {
            StringBuffer sb = new StringBuffer(source);
            sb.append(",");
            sb.append(dialogName != null ? source : "-");
            if(skinName != null)
            {
                sb.append(",");
                sb.append(skinName);
            }
            return sb.toString();
        }

        public void handleDialog(ValueContext vc) throws IOException
        {
            PrintWriter out = vc.getResponse().getWriter();
            javax.servlet.ServletContext context = vc.getServletContext();

            com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("StatementManager not found in ServletContext");
                return;
            }

            com.netspective.sparx.xaf.querydefn.QueryDefinition queryDefn = manager.getQueryDefn(source);
            if(queryDefn == null)
            {
                out.write("QueryDefinition '" + source + "' not found in StatementManager");
                return;
            }

            com.netspective.sparx.xaf.querydefn.QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogName);
            if(dialog == null)
            {
                out.write("QuerySelectDialog '" + dialogName + "' not found in QueryDefinition '" + source + "'");
                return;
            }

            com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + skinName + "' not found in skin factory.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(vc.getServletContext(), vc.getServlet(), (javax.servlet.http.HttpServletRequest) vc.getRequest(), (javax.servlet.http.HttpServletResponse) vc.getResponse(), skin);
            dc.setRetainRequestParams(DialogManagerFactory.DialogCommands.DIALOG_COMMAND_RETAIN_PARAMS);
            dialog.prepareContext(dc);

            dialog.renderHtml(out, dc, true);
        }
    }

}