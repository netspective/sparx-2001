package com.xaf.db;

import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.sql.*;
import javax.naming.*;

import org.w3c.dom.*;

import com.xaf.form.*;

/**
 * Provides a factory pattern for constructing DatabaseContext instances.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DatabaseContextFactory
{
	public static final String CONTEXTNAME_PROPNAME = "com.netspective.sparx.DatabaseContext.class";
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


	public static void addText(Document doc, Element parent, String elemName, String text)
	{
		Element elemNode = doc.createElement(elemName);
		Text textNode = doc.createTextNode(text);
		elemNode.appendChild(textNode);
		parent.appendChild(elemNode);
	}

	public static void addErrorProperty(Document doc, Element parent, String errorMsg)
	{
		Element propertyElem = doc.createElement("property");
		addText(doc, propertyElem, "name", "error");
		addText(doc, propertyElem, "value", errorMsg);
		parent.appendChild(propertyElem);
	}

    /**
     * Creates a catalog of all the data sources available
     *
     * @param parent
     * @returns
     */
    public static void createCatalog(ServletContext servletContext, ServletRequest servletRequest, Element parent) throws NamingException
    {
		DatabaseContext dc = getContext(servletRequest, servletContext, false);

        Document doc = parent.getOwnerDocument();
		Element dataSourcesElem = doc.createElement("properties");
		parent.appendChild(dataSourcesElem);
		dataSourcesElem.setAttribute("name", "Data Sources");
		dataSourcesElem.setAttribute("class", dc != null ? dc.getClass().getName() : "No DatabaseContext found.");

		if(dc != null)
			dc.createCatalog(dataSourcesElem);
		else
			addErrorProperty(doc, dataSourcesElem, "DatabaseContext could not be located. Check system property '"+CONTEXTNAME_PROPNAME+"'");
    }


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
        {
            // get user specified database context
            String contextName = System.getProperty(CONTEXTNAME_PROPNAME);
            boolean usePoolman = false;

            if (contextName == null || contextName.equals("com.codestudio.sql.PoolMan"))
            {
                try
                {
                    Class.forName("com.codestudio.sql.PoolMan").newInstance();
                    usePoolman = true;
                }
                catch (Exception e)
                {
                    usePoolman = false;
                }
            }

            if (usePoolman)
            {
                dc = new PoolmanDatabaseContext(servletContext.getInitParameter("default-data-source"));
            }
            else
            {
			    dc = new BasicDatabaseContext(servletContext.getInitParameter("default-data-source"));
            }
        }

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