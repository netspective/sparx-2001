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
 * $Id: QueryDefinitionDialogComponentCommand.java,v 1.4 2003-01-16 16:38:06 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.html.command;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.xaf.querydefn.QueryBuilderDialog;

import java.util.StringTokenizer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class QueryDefinitionDialogComponentCommand extends AbstractComponentCommand
{
    static public final String COMMAND_ID = "qd-dialog";
    static public final Documentation DOCUMENTATION = new Documentation(
                 "Displays results of a query definition select dialog.",
                 new Documentation.Parameter[]
                     {
                         new Documentation.Parameter("query-defn-name", true, null, null, "The name of the query definition."),
                         new Documentation.Parameter("query-select-dialog-name", true, null, null, "The name of the query select dialog."),
                         new DialogComponentCommand.SkinParameter(),
                         new StatementComponentCommand.SkinParameter(),
                     });

    private String dialogName;
    private String source;
    private String dialogSkinName;
    private String reportSkinName;

    public Documentation getDocumentation()
    {
         return DOCUMENTATION;
    }

    public void setCommand(StringTokenizer params)
    {
        source = params.nextToken();

        if(params.hasMoreTokens())
            dialogName = params.nextToken();
        else
            dialogName = "unknown";

        if(params.hasMoreTokens())
        {
            dialogSkinName = params.nextToken();
            if(dialogSkinName.equals(PARAMVALUE_DEFAULT))
                dialogSkinName = null;
        }
        else
            dialogSkinName = null;

        if(params.hasMoreTokens())
        {
            reportSkinName = params.nextToken();
            if(reportSkinName.equals(PARAMVALUE_DEFAULT))
                reportSkinName = null;
        }
        else
            reportSkinName = null;
    }

    public String getSource()
    {
        return source;
    }

    public String getDialogName()
    {
        return dialogName;
    }

    public String getDialogSkinName()
    {
        return dialogSkinName;
    }

    public String getReportSkinName()
    {
        return reportSkinName;
    }

    public void setSource(String dataCmd)
    {
        this.source = dataCmd;
    }

    public void setDialogName(String dialogName)
    {
        this.dialogName = dialogName;
    }

    public void setDialogSkinName(String dialogSkinName)
    {
        this.dialogSkinName = dialogSkinName;
    }

    public void setReportSkinName(String reportSkinName)
    {
        this.reportSkinName = reportSkinName;
    }

    public String getCommand()
    {
        String delim = getParametersDelimiter();
        StringBuffer sb = new StringBuffer(source);
        sb.append(delim);
        sb.append(dialogName != null ? source : PARAMVALUE_DEFAULT);
        if(dialogSkinName != null)
        {
            sb.append(delim);
            sb.append(dialogSkinName);
        }
        else
            sb.append(PARAMVALUE_DEFAULT);
        if(reportSkinName != null)
        {
            sb.append(delim);
            sb.append(reportSkinName);
        }
        return sb.toString();
    }

    public void handleCommand(ValueContext vc, Writer writer, boolean unitTest) throws ComponentCommandException, IOException
    {
        PrintWriter out = vc.getResponse().getWriter();
        javax.servlet.ServletContext context = vc.getServletContext();

        com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
        if(manager == null)
        {
            out.write("StatementManager not found in ServletContext");
            return;
        }

        com.netspective.sparx.xaf.querydefn.QueryDefinition queryDefn = manager.getQueryDefn(vc.getServletContext(), null, source);
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

        com.netspective.sparx.xaf.form.DialogSkin skin = dialogSkinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(dialogSkinName);
        if(skin == null)
        {
            out.write("DialogSkin '" + dialogSkinName + "' not found in skin factory.");
            return;
        }

        com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(vc.getServletContext(), vc.getServlet(), (javax.servlet.http.HttpServletRequest) vc.getRequest(), (javax.servlet.http.HttpServletResponse) vc.getResponse(), skin);
        dc.setRetainRequestParams(DialogComponentCommand.DIALOG_COMMAND_RETAIN_PARAMS);
        dialog.prepareContext(dc);

        if(reportSkinName != null)
            dc.setValue(QueryBuilderDialog.QBDIALOG_REPORT_SKIN_FIELD_NAME, reportSkinName);

        dialog.renderHtml(out, dc, true);
    }
}
