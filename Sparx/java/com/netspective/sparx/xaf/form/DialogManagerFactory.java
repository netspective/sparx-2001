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
 * $Id: DialogManagerFactory.java,v 1.5 2002-12-15 17:50:55 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.FactoryEvent;
import com.netspective.sparx.util.factory.FactoryListener;
import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.security.AccessControlList;
import com.netspective.sparx.xaf.page.PageContext;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

public class DialogManagerFactory implements Factory
{
    static final String ATTRNAME_DIALOGMGR = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "dialog-mgr";
    static final String REQPARAMNAME_SOURCE = "dlgsrc";
    static Map managers = new HashMap();
    static List listeners;

    public static Map getManagers()
    {
        return managers;
    }

    public static void addListener(FactoryListener listener)
    {
        if(listeners == null)
            listeners = new ArrayList();

        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public static void contentsChanged(DialogManager instance)
    {
        if(listeners == null)
            return;

        FactoryEvent event = new FactoryEvent(DialogManagerFactory.class, instance);
        for(Iterator i = listeners.iterator(); i.hasNext();)
        {
            FactoryListener listener = (FactoryListener) i.next();
            listener.factoryContentsChanged(event);
        }
    }

    public static void generatePermissions(AccessControlList acl, Element parentElem)
    {
        for(Iterator m = managers.values().iterator(); m.hasNext();)
        {
            DialogManager manager = (DialogManager) m.next();
            Map dialogs = manager.getDialogs();
            for(Iterator d = dialogs.values().iterator(); d.hasNext();)
            {
                DialogManager.DialogInfo info = (DialogManager.DialogInfo) d.next();
                acl.addPermissionElem(parentElem, info.getLookupName());
            }
        }
    }

    public static DialogManager getManager(String file)
    {
        DialogManager activeManager = (DialogManager) managers.get(file);
        if(activeManager == null)
        {
            activeManager = new DialogManager(new File(file));
            managers.put(file, activeManager);
            contentsChanged(activeManager);
        }
        return activeManager;
    }

    public static DialogManager getManager(ServletContext context)
    {
        DialogManager manager = (DialogManager) context.getAttribute(ATTRNAME_DIALOGMGR);
        if(manager != null)
            return manager;

        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
        ValueContext vc = new ServletValueContext(context, null, null, null);
        manager = getManager(appConfig.getTextValue(vc, "app.ui.source-file"));
        manager.initializeForServlet(context);
        context.setAttribute(ATTRNAME_DIALOGMGR, manager);
        return manager;
    }

    public static DialogManager getManager(ServletRequest request, ServletContext context)
    {
        String dlgSource = request.getParameter(REQPARAMNAME_SOURCE);
        if(dlgSource == null || dlgSource.length() == 0)
            return getManager(context);
        else
            return getManager(dlgSource);
    }

    /**
     * cmdParams should look like:
     *   0 dialog name (required)
     *   1 data command like add,edit,delete,confirm (optional, may be empty or set to "-" to mean "none")
     *   2 skin name (optional, may be empty or set to "-" to mean "none")
     */

    public static DialogCommands getCommands(String cmdParams)
    {
        return new DialogCommands(cmdParams);
    }

    public static class DialogCommands
    {
        static public final String PAGE_COMMAND_REQUEST_PARAM_NAME = "cmd";
        static public final String[] DIALOG_COMMAND_RETAIN_PARAMS =
                {
                    PAGE_COMMAND_REQUEST_PARAM_NAME
                };

        private String dialogName;
        private String dataCmd;
        private String skinName;
        private String debugFlagsSpec;

        public DialogCommands(String cmdParams)
        {
            this(new StringTokenizer(cmdParams, ","));
        }

        public DialogCommands(StringTokenizer st)
        {
            dialogName = st.nextToken();

            if(st.hasMoreTokens())
            {
                dataCmd = st.nextToken();
                if(dataCmd.length() == 0 || dataCmd.equals("-"))
                    dataCmd = null;
            }
            else
                dataCmd = null;

            if(st.hasMoreTokens())
            {
                skinName = st.nextToken();
                if(skinName.length() == 0 || skinName.equals("-"))
                    skinName = null;
            }
            else
                skinName = null;

            if(st.hasMoreTokens())
            {
                debugFlagsSpec = st.nextToken();
                if(debugFlagsSpec.equals("-"))
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

        public String generateCommand()
        {
            StringBuffer sb = new StringBuffer(dialogName);
            sb.append(",");
            sb.append(dataCmd != null ? dataCmd : "-");
            if(skinName != null)
            {
                sb.append(",");
                sb.append(skinName);
            }
            if(debugFlagsSpec != null)
            {
                sb.append(",");
                sb.append(debugFlagsSpec);
            }
            return sb.toString();
        }

        public void handleDialog(ValueContext vc) throws IOException
        {

            if(dataCmd != null)
                vc.getRequest().setAttribute(com.netspective.sparx.xaf.form.Dialog.PARAMNAME_DATA_CMD_INITIAL, dataCmd);

            PrintWriter out = vc.getResponse().getWriter();
            javax.servlet.ServletContext context = vc.getServletContext();
            com.netspective.sparx.xaf.form.DialogManager manager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("DialogManager not found in ServletContext");
                return;
            }

            com.netspective.sparx.xaf.form.Dialog dialog = manager.getDialog(vc.getServletContext(), dialogName);
            if(dialog == null)
            {
                out.write("Dialog '" + dialogName + "' not found in manager '" + manager + "'.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + skinName + "' not found in skin factory.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(context, vc.getServlet(), (javax.servlet.http.HttpServletRequest) vc.getRequest(), (javax.servlet.http.HttpServletResponse) vc.getResponse(), skin);
            if(debugFlagsSpec != null)
                dc.setDebugFlags(debugFlagsSpec);
            dc.setRetainRequestParams(DIALOG_COMMAND_RETAIN_PARAMS);
            dialog.prepareContext(dc);

            if(dc.inExecuteMode())
            {
                if(dc.debugFlagIsSet(Dialog.DLGDEBUGFLAG_SHOW_FIELD_DATA))
                {
                    out.write(dc.getDebugHtml());
                    out.write(dialog.getLoopSeparator());
                    dc.getSkin().renderHtml(out, dc);
                }
                else
                {
                    dialog.execute(out, dc);
                    if(! dc.executeStageHandled())
                    {
                        out.write("Dialog '" + dialogName + "' did not handle the execute mode.<p>");
                        out.write(dc.getDebugHtml());
                    }
                }
            }
            else
                dialog.renderHtml(out, dc, true);
        }
    }
}
