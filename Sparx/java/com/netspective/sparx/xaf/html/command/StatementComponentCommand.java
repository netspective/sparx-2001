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
 * $Id: StatementComponentCommand.java,v 1.1 2002-12-26 19:30:27 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.html.command;

import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xaf.sql.StatementDialog;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.html.ComponentCommandFactory;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import javax.naming.NamingException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

public class StatementComponentCommand extends AbstractComponentCommand
{
    static public final String COMMAND_ID = "statement";
    static public final int UNLIMITED_ROWS = Integer.MAX_VALUE;

    private String statementName;
    private int rowsPerPage;
    private String skinName;
    private String reportId;
    private String[] urlFormats;
    private DialogComponentCommand dialogCommand;

    public Documentation getDocumentation()
    {
        return new Documentation(
                "Displays results of a SQL statement. The dialog-name is required, all other parameters are optional. To skip a " +
                "parameter and have it accept the default, just set it to '-'. URL formats are used to override URLs for given "+
                "columns and are delimited by semi-colons.",
                new Documentation.Parameter[]
                    {
                        new Documentation.Parameter("statement-name", true),
                        new Documentation.Parameter("report-id", false),
                        new Documentation.Parameter("rows-per-page", false),
                        new Documentation.Parameter("skin-name", false),
                        new Documentation.Parameter("url-formats", false),
                        new Documentation.Parameter("dialog-commands", false),
                    }
        );
    }

    public void setCommand(StringTokenizer params)
    {
        statementName = params.nextToken();

        if(params.hasMoreTokens())
        {
            reportId = params.nextToken();
            if(reportId.length() == 0 || reportId.equals(PARAMVALUE_DEFAULT))
                reportId = null;
        }
        else
            reportId = null;

        if(params.hasMoreTokens())
        {
            String rowsPerPageStr = params.nextToken();
            if(rowsPerPageStr.length() == 0 || rowsPerPageStr.equals(PARAMVALUE_DEFAULT))
                rowsPerPage = UNLIMITED_ROWS;
            else
                rowsPerPage = Integer.parseInt(rowsPerPageStr);
        }
        else
            rowsPerPage = UNLIMITED_ROWS;

        if(params.hasMoreTokens())
        {
            skinName = params.nextToken();
            if(skinName.length() == 0 || skinName.equals(PARAMVALUE_DEFAULT))
                skinName = null;
        }
        else
            skinName = null;

        if(params.hasMoreTokens())
        {
            String urlFormatsStr = params.nextToken();
            if(urlFormatsStr.length() == 0 || urlFormatsStr.equals(PARAMVALUE_DEFAULT))
                setUrlFormats(null);
            else
            {
                StringTokenizer urlFmtTokenizer = new StringTokenizer(urlFormatsStr, ";");
                List urlFormatsList = new ArrayList();
                while(urlFmtTokenizer.hasMoreTokens())
                {
                    String urlFormat = urlFmtTokenizer.nextToken();
                    if(urlFormat.length() == 0 || urlFormat.equals(PARAMVALUE_DEFAULT))
                        urlFormatsList.add(null);
                    else
                        urlFormatsList.add(urlFormat);
                }
                setUrlFormats((String[]) urlFormatsList.toArray(new String[urlFormatsList.size()]));
            }
        }
        else
            setUrlFormats(null);

        if(params.hasMoreTokens())
            dialogCommand = ComponentCommandFactory.getDialogCommand(params);
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

    public String[] getUrlFormats()
    {
        return urlFormats;
    }

    public void setUrlFormats(String[] urlFormats)
    {
        this.urlFormats = urlFormats;
    }

    public String getCommand()
    {
        String delim = getParametersDelimiter();
        StringBuffer sb = new StringBuffer(statementName);
        sb.append(delim);
        sb.append(reportId != null ? reportId : PARAMVALUE_DEFAULT);
        sb.append(delim);
        sb.append(rowsPerPage != UNLIMITED_ROWS ? Integer.toString(rowsPerPage) : PARAMVALUE_DEFAULT);
        sb.append(delim);
        sb.append(skinName != null ? skinName : PARAMVALUE_DEFAULT);
        sb.append(delim);
        if(urlFormats != null)
        {
            for(int i = 0; i < urlFormats.length; i++)
            {
                if(i > 0) sb.append(";");
                sb.append(urlFormats[i]);
            }
        }
        else
            sb.append(PARAMVALUE_DEFAULT);
        if(dialogCommand != null)
        {
            sb.append(delim);
            sb.append(dialogCommand.getCommand());
        }
        return sb.toString();
    }

    public void handleCommand(ValueContext vc, Writer writer, boolean unitTest) throws ComponentCommandException, IOException
    {
        javax.servlet.ServletContext context = vc.getServletContext();

        com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
        if(manager == null)
        {
            writer.write("StatementManager not found in ServletContext");
            return;
        }

        if(dialogCommand != null)
            writer.write("<table><tr valign='top'><td>");

        if(rowsPerPage > 0 && rowsPerPage < UNLIMITED_ROWS)
        {
            // Special Case: This static query must produce a report that is pageable
            StatementDialog stmtDialog = new StatementDialog(manager.getStatement(vc.getServletContext(), null, statementName), reportId, skinName, urlFormats);
            stmtDialog.setRowsPerPage(rowsPerPage);
            DialogSkin dialogSkin = com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin();
            DialogContext dc = stmtDialog.createContext(context, vc.getServlet(),
                    (javax.servlet.http.HttpServletRequest) vc.getRequest(),
                    (javax.servlet.http.HttpServletResponse) vc.getResponse(), dialogSkin);
            stmtDialog.prepareContext(dc);
            stmtDialog.renderHtml(writer, dc, false);
        }
        else
        {
            ReportSkin skin = skinName != null ? SkinFactory.getReportSkin(skinName) : SkinFactory.getDefaultReportSkin();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), context);
            try
            {
                manager.produceReport(writer, dbContext, vc, null, skin, statementName, null, null, urlFormats);
            }
            catch (StatementNotFoundException e)
            {
                throw new ComponentCommandException(this, e);
            }
            catch (NamingException e)
            {
                throw new ComponentCommandException(this, e);
            }
            catch (SQLException e)
            {
                throw new ComponentCommandException(this, e);
            }
        }

        if(dialogCommand != null)
        {
            writer.write("</td><td>");
            dialogCommand.handleCommand(vc, writer, unitTest);
            writer.write("</td></tr></td></table>");
        }
    }
}
