package com.xaf.config;

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

public class ConfigurationManagerFactory
{
	private static Map managers = new Hashtable();

	public static ConfigurationManager getManager(String file)
	{
		ConfigurationManager activeManager = (ConfigurationManager) managers.get(file);
		if(activeManager == null)
		{
			activeManager = new ConfigurationManager(new File(file));
			managers.put(file, activeManager);
		}
		return activeManager;
	}

	public static ConfigurationManager getManager(ServletContext context)
	{
		String configFile = context.getInitParameter("config-file");
		if(configFile == null)
			configFile = "WEB-INF/configuration.xml";
		return getManager(context.getRealPath(configFile));
	}
}