package com.xaf.db;

import java.util.*;
import javax.servlet.*;

import com.xaf.form.*;

/**
 * Provides a factory pattern for constructing DatabaseContext instances.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DatabaseContextFactory
{
	static Map contexts = new Hashtable();

	/**
	 * If the DatabaseContex is stored in a ServletRequest attribute, then this is the
	 * preferred name of the attribute.
	 */
	static public final String DBCONTEXT_REQUEST_ATTR_NAME = "DatabaseContext";

	/**
	 * If the DatabaseContex is stored in a ServletContext atttibute, then this is the
	 * preferred name of the attribute.
	 */
	static public final String DBCONTEXT_SERVLETCONTEXT_ATTR_NAME = "DatabaseContext";

	public static DatabaseContext getContext(String contextId)
	{
		return (DatabaseContext) contexts.get(contextId);
	}

	public static DatabaseContext getContext(ServletRequest servletRequest, ServletContext servletContext, boolean throwExceptionIfDoesntExist)
	{
		DatabaseContext dc = (DatabaseContext) servletRequest.getAttribute(DBCONTEXT_REQUEST_ATTR_NAME);
		if(dc != null)
			return dc;

		dc = (DatabaseContext) servletContext.getAttribute(DBCONTEXT_SERVLETCONTEXT_ATTR_NAME);
		if(dc != null)
			return dc;

		if(dc == null && throwExceptionIfDoesntExist)
			throw new RuntimeException("DatabaseContext instance not found in ServletRequest attribute '"+DBCONTEXT_REQUEST_ATTR_NAME+"', or ServletContext attribute '"+DBCONTEXT_SERVLETCONTEXT_ATTR_NAME+"'");
		else
			dc = new BasicDatabaseContext(servletContext.getInitParameter("default-data-source"));

		return dc;
	}

	public static DatabaseContext getContext(ServletRequest servletRequest, ServletContext servletContext)
	{
		return getContext(servletRequest, servletContext, false);
	}

	public static DatabaseContext getContext(DialogContext dialogContext, boolean throwExceptionIfDoesntExist)
	{
		DatabaseContext dc = dialogContext.getDatabaseContext();
		if(dc == null)
			dc = getContext(dialogContext.getRequest(), dialogContext.getServletContext(), false);

		if(dc == null && throwExceptionIfDoesntExist)
			throw new RuntimeException("DatabaseContext instance not found in DialogContext, ServletRequest attribute '"+DBCONTEXT_REQUEST_ATTR_NAME+"', or ServletContext attribute '"+DBCONTEXT_SERVLETCONTEXT_ATTR_NAME+"'");

		return dc;
	}

	public static DatabaseContext getContext(DialogContext dialogContext)
	{
		return getContext(dialogContext, false);
	}

	public static void addContext(String id, DatabaseContext dc)
	{
		contexts.put(id, dc);
	}
}