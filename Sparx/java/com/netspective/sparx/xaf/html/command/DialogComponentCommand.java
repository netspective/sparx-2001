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
 * $Id: DialogComponentCommand.java,v 1.1 2002-12-26 19:30:27 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.html.command;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.xif.dal.TableDialog;

import java.util.StringTokenizer;
import java.io.IOException;
import java.io.Writer;

public class DialogComponentCommand extends AbstractComponentCommand
{
    static public final String COMMAND_ID = "dialog";
    static public final String[] DIALOG_COMMAND_RETAIN_PARAMS =
            {
                PAGE_COMMAND_REQUEST_PARAM_NAME
            };

    private String dialogName;
    private String dataCmd;
    private String skinName;
    private String debugFlagsSpec;

    public Documentation getDocumentation()
    {
        return new Documentation(
                "Displays a dialog box. The dialog-name is required, but data-command (like 'add', 'edit', or 'delete'), " +
                "skin-name, and debug-flags are optional (may be empty or set to '-' to mean 'none'. Debug-flags may be "+
                "set to SHOW_DATA if you want to ignore the execute portion of the dialog and just dump the data.",
                new Documentation.Parameter[]
                    {
                        new Documentation.Parameter("dialog-name", true),
                        new Documentation.Parameter("data-command", false),
                        new Documentation.Parameter("skin-name", false),
                        new Documentation.Parameter("debug-flags", false),
                    }
        );
    }

    public void setCommand(StringTokenizer st)
    {
        dialogName = st.nextToken();

        if(st.hasMoreTokens())
        {
            dataCmd = st.nextToken();
            if(dataCmd.length() == 0 || dataCmd.equals(PARAMVALUE_DEFAULT))
                dataCmd = null;
        }
        else
            dataCmd = null;

        if(st.hasMoreTokens())
        {
            skinName = st.nextToken();
            if(skinName.length() == 0 || skinName.equals(PARAMVALUE_DEFAULT))
                skinName = null;
        }
        else
            skinName = null;

        if(st.hasMoreTokens())
        {
            debugFlagsSpec = st.nextToken();
            if(debugFlagsSpec.equals(PARAMVALUE_DEFAULT))
                debugFlagsSpec = null;
        }
        else
            debugFlagsSpec = null;
    }

    public String getDataCmd()
    {
        return dataCmd;
    }

    public String getDialogName()
    {
        return dialogName;
    }

    public String getSkinName()
    {
        return skinName;
    }

    public String getDebugFlagsSpec()
    {
        return debugFlagsSpec;
    }

    public void setDataCmd(String dataCmd)
    {
        this.dataCmd = dataCmd;
    }

    public void setDialogName(String dialogName)
    {
        this.dialogName = dialogName;
    }

    public void setSkinName(String skinName)
    {
        this.skinName = skinName;
    }

    public void setDebugFlagsSpec(String debugFlagsSpec)
    {
        this.debugFlagsSpec = debugFlagsSpec;
    }

    public String getCommand()
    {
        String delim = getParametersDelimiter();
        StringBuffer sb = new StringBuffer(dialogName);
        sb.append(delim);
        sb.append(dataCmd != null ? dataCmd : PARAMVALUE_DEFAULT);
        if(skinName != null)
        {
            sb.append(delim);
            sb.append(skinName);
        }
        if(debugFlagsSpec != null)
        {
            sb.append(delim);
            sb.append(debugFlagsSpec);
        }
        return sb.toString();
    }

    public void handleCommand(ValueContext vc, Writer writer, boolean unitTest) throws ComponentCommandException, IOException
    {
        if(dataCmd != null)
            vc.getRequest().setAttribute(com.netspective.sparx.xaf.form.Dialog.PARAMNAME_DATA_CMD_INITIAL, dataCmd);

        javax.servlet.ServletContext context = vc.getServletContext();
        com.netspective.sparx.xaf.form.DialogManager manager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(context);
        if(manager == null)
        {
            writer.write("DialogManager not found in ServletContext");
            return;
        }

        com.netspective.sparx.xaf.form.Dialog dialog = manager.getDialog(vc.getServletContext(), null, dialogName);
        if(dialog == null)
        {
            writer.write("Dialog '" + dialogName + "' not found in manager '" + manager + "'.");
            return;
        }

        com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
        if(skin == null)
        {
            writer.write("DialogSkin '" + skinName + "' not found in skin factory.");
            return;
        }

        com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(context, vc.getServlet(), (javax.servlet.http.HttpServletRequest) vc.getRequest(), (javax.servlet.http.HttpServletResponse) vc.getResponse(), skin);
        if(debugFlagsSpec != null)
            dc.setDebugFlags(debugFlagsSpec);
        dc.setRetainRequestParams(DIALOG_COMMAND_RETAIN_PARAMS);
        dialog.prepareContext(dc);
        if(unitTest)
            dc.setRedirectDisabled(true);

        if(dc.inExecuteMode())
        {
            if(dc.debugFlagIsSet(Dialog.DLGDEBUGFLAG_SHOW_FIELD_DATA))
            {
                writer.write(dc.getDebugHtml());
                writer.write(dialog.getLoopSeparator());
                dc.getSkin().renderHtml(writer, dc);
            }
            else
            {
                dialog.execute(writer, dc);

                if(unitTest && dialog instanceof TableDialog)
                    writer.write("<pre>Last row processed: "+ dc.getLastRowManipulated() +"</pre>");

                if(! dc.executeStageHandled())
                {
                    writer.write("Dialog '" + dialogName + "' did not handle the execute mode.<p>");
                    writer.write(dc.getDebugHtml());
                }
            }
        }
        else
            dialog.renderHtml(writer, dc, true);
    }
}
