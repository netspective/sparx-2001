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

public class StatementManagerFactory
{
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
		return getManager(context.getRealPath(context.getInitParameter("sql-statements-file")));
	}
}