package com.xaf.form;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.*;
import com.xaf.config.*;
import com.xaf.security.*;
import com.xaf.value.*;

public class DialogManagerFactory
{
	static final String ATTRNAME_DIALOGMGR = "framework.dialog-mgr";
	static final String REQPARAMNAME_SOURCE = "dlgsrc";
	static Map managers = new Hashtable();
	static List listeners;

    public static Map getManagers()
    {
        return managers;
    }

	public static void addListener(FactoryListener listener)
	{
		if(listeners == null)
			listeners = new ArrayList();

		if(! listeners.contains(listener))
			listeners.add(listener);
	}

	public static void contentsChanged()
	{
		if(listeners == null)
			return;

		FactoryEvent event = new FactoryEvent(DialogManagerFactory.class);
		for(Iterator i = listeners.iterator(); i.hasNext(); )
		{
			FactoryListener listener = (FactoryListener) i.next();
			listener.factoryContentsChanged(event);
		}
	}

	public static void generatePermissions(AccessControlList acl, Element parentElem)
	{
		for(Iterator m = managers.values().iterator(); m.hasNext(); )
		{
			DialogManager manager = (DialogManager) m.next();
			Map dialogs = manager.getDialogs();
			for(Iterator d = dialogs.values().iterator(); d.hasNext(); )
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
		manager = getManager(appConfig.getValue(vc, "app.ui.source-file"));
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
