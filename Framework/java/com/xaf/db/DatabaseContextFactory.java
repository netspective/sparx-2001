package com.xaf.db;

import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.sql.*;
import javax.naming.*;

import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.value.*;

/**
 * Provides a factory pattern for constructing DatabaseContext instances.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DatabaseContextFactory
{
	public static final String CONTEXTNAME_PROPNAME = "com.netspective.sparx.DatabaseContext.class";
	private static DatabaseContext useContext = null;
    private static Map databasePolicies = new HashMap();
    private static boolean defaultPoliciesSetup = false;

    static public void addDatabasePolicy(String databaseProductName, DatabasePolicy policy)
    {
        databasePolicies.put(databaseProductName, policy);
    }

    static public void addDatabasePolicy(String databaseProductName, String policyClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        DatabasePolicy policy = (DatabasePolicy) Class.forName(policyClassName).newInstance();
        databasePolicies.put(databaseProductName, policy);
    }

    static public void setupDefaultPolicies()
    {
        addDatabasePolicy("Oracle", new OracleDatabasePolicy());
        addDatabasePolicy("Microsoft SQL Server", new SqlServerDatabasePolicy());
        defaultPoliciesSetup = true;
    }

    static public DatabasePolicy getDatabasePolicy(String databaseProductName)
    {
        if(!defaultPoliciesSetup) setupDefaultPolicies();
        return (DatabasePolicy) databasePolicies.get(databaseProductName);
    }

    static public DatabasePolicy getDatabasePolicy(Connection conn) throws SQLException
    {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        DatabasePolicy policy = getDatabasePolicy(databaseProductName);
        if(policy == null)
            throw new SQLException("Database policy not found for database '"+ databaseProductName +"'");
        else
            return policy;
    }

	/**
	 * If the DatabaseContext is stored in a ServletContext atttibute, then this is the
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
    public static void createCatalog(ValueContext vc, Element parent) throws NamingException
    {
        ServletContext servletContext = vc.getServletContext();
        ServletRequest servletRequest = vc.getRequest();
		DatabaseContext dc = getContext(servletRequest, servletContext);

        Document doc = parent.getOwnerDocument();
		Element dataSourcesElem = doc.createElement("properties");
		parent.appendChild(dataSourcesElem);
		dataSourcesElem.setAttribute("class", dc != null ? dc.getClass().getName() : "No DatabaseContext found.");

		if(dc != null)
			dc.createCatalog(vc, dataSourcesElem);
		else
			addErrorProperty(doc, dataSourcesElem, "DatabaseContext could not be located. Check system property '"+CONTEXTNAME_PROPNAME+"'");
    }

	public static DatabaseContext getSystemContext()
	{
		if(useContext != null)
			return useContext;

        /**
		 * if we haven't figured out our "default" connection manager,
		 * try and figure it out now. Basically, we'll try and instantiate
		 * the poolman context and if it's not found we'll use the Basic
		 * one (which relies on JNDI and the web application server).
		 */

		DatabaseContext dc = null;
		try
		{
			String contextName = System.getProperty(CONTEXTNAME_PROPNAME, BasicDatabaseContext.class.getName());
			dc = (DatabaseContext) Class.forName(contextName).newInstance();
		}
		catch(ClassNotFoundException e)
		{
			dc = new BasicDatabaseContext();
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			dc = new BasicDatabaseContext();
			e.printStackTrace();
		}
		catch(InstantiationException e)
		{
			dc = new BasicDatabaseContext();
			e.printStackTrace();
		}

		useContext = dc;
		return useContext;
	}

	public static DatabaseContext getContext(ServletRequest servletRequest, ServletContext servletContext)
	{
		DatabaseContext dc = (DatabaseContext) servletContext.getAttribute(DBCONTEXT_SERVLETCONTEXT_ATTR_NAME);
		return dc != null ? dc : getSystemContext();
	}

	public static DatabaseContext getContext(DialogContext dialogContext)
	{
		DatabaseContext dc = dialogContext.getDatabaseContext();
		return dc != null ? dc : getContext(dialogContext.getRequest(), dialogContext.getServletContext());
	}
}