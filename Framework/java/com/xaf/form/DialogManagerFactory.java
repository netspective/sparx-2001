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

import com.xaf.config.*;
import com.xaf.value.*;

public class DialogManagerFactory
{
	static final String ATTRNAME_DIALOGMGR = "framework.dialog-mgr";
	static final String REQPARAMNAME_SOURCE = "dlgsrc";
	static Hashtable managers = new Hashtable();

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
		ValueContext vc = new ServletValueContext(null, null, context);
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
