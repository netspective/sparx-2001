package com.xaf.sql;

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

public class StatementManagerFactory
{
	static final String ATTRNAME_STATEMENTMGR = "framework.statement-mgr";
	private static Map managers = new Hashtable();

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

	public static StatementManager getManager(ServletContext context)
	{
		StatementManager manager = (StatementManager) context.getAttribute(ATTRNAME_STATEMENTMGR);
		if(manager != null)
			return manager;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(context, null, null, null);
		manager = getManager(appConfig.getValue(vc, "app.sql.source-file"));
		manager.initializeForServlet(context);
		context.setAttribute(ATTRNAME_STATEMENTMGR, manager);
		return manager;
	}
}