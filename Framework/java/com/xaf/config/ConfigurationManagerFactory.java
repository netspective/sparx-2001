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
	private static final String CONFIGMGR_ATTR_NAME = "framework.config-mgr";
	private static final String APPCONFIG_ATTR_NAME = "framework.app-config";
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
		ConfigurationManager manager = (ConfigurationManager) context.getAttribute(CONFIGMGR_ATTR_NAME);
		if(manager != null)
			return manager;

		String configFile = context.getInitParameter("framework.config-file");
		if(configFile == null)
			configFile = "WEB-INF/configuration.xml";
		manager = getManager(context.getRealPath(configFile));
		context.setAttribute(CONFIGMGR_ATTR_NAME, manager);
		context.setAttribute(APPCONFIG_ATTR_NAME, manager.getDefaultConfiguration());
		return manager;
	}

	public static Configuration getDefaultConfiguration(ServletContext context)
	{
		Configuration config = (Configuration) context.getAttribute(APPCONFIG_ATTR_NAME);
		if(config != null)
			return config;

		// when we call getManager(context) it will automatically set the APPCONFIG attribute
		getManager(context);
		return (Configuration) context.getAttribute(APPCONFIG_ATTR_NAME);
	}
}