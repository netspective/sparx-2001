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

import com.xaf.config.*;
import com.xaf.security.*;
import com.xaf.value.*;

public class DialogManagerFactory
{
	static final String ATTRNAME_DIALOGMGR = "framework.dialog-mgr";
	static final String REQPARAMNAME_SOURCE = "dlgsrc";
	static Hashtable managers = new Hashtable();

	public static void generatePermissions(AccessControlList acl, Element parentElem, String name)
	{
		Element dialogsElem = acl.addPermissionElem(parentElem, name);
		for(Iterator m = managers.values().iterator(); m.hasNext(); )
		{
			DialogManager manager = (DialogManager) m.next();
			Map dialogs = manager.getDialogs();
			for(Iterator d = dialogs.values().iterator(); d.hasNext(); )
			{
				DialogManager.DialogInfo info = (DialogManager.DialogInfo) d.next();
				acl.addPermissionElem(dialogsElem, info.getLookupName());
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
