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
 * $Id: DialogManagerFactory.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.FactoryEvent;
import com.netspective.sparx.util.factory.FactoryListener;
import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.security.AccessControlList;
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

    public static void contentsChanged()
    {
        if(listeners == null)
            return;

        FactoryEvent event = new FactoryEvent(DialogManagerFactory.class);
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
            contentsChanged();
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
}
