package com.xaf.db;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import javax.sql.*;
import javax.naming.*;

import org.w3c.dom.*;
import com.xaf.value.*;

public class PoolmanDatabaseContext extends AbstractDatabaseContext
{
	static public final String POOLMAN_CLASS_NAME = "com.codestudio.sql.PoolMan";

	public PoolmanDatabaseContext() throws SQLException
	{
		try
		{
			// load the PoolMan JDBC Driver
			Class.forName(POOLMAN_CLASS_NAME);
		}
		catch(ClassNotFoundException e)
		{
			throw new SQLException(e.toString());
		}
	}

	public final Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException
	{
		dataSourceId = translateDataSourceId(vc, dataSourceId);
	    return DriverManager.getConnection("jdbc:poolman://" + dataSourceId);
	}

    /**
     * Creates a catalog of all the data sources available. Because Poolman is
	 * an optional DatabaseContext we can not simply "import" all the com.codestudio.*
	 * files. Instead, we use java reflection to get the methods and classes.
     *
     * @param parent
     * @returns
     */
    public void createCatalog(Element parent) throws NamingException
    {
        Document doc = parent.getOwnerDocument();

        Class sqlutilClass = null;
		Class jdbcPoolClass = null;
		Method getAllPoolNamesMethod = null;
		Method getPoolMethod = null;
		Method getPoolDataSourceMethod = null;
        Method getNewInstanceMethod = null;
        Object sqlUtil = null;
        Object[] empty = new Object[] {};
        String error = null;
        try
        {
            sqlutilClass = Class.forName("com.codestudio.util.SQLUtil");
			jdbcPoolClass = Class.forName("com.codestudio.util.JDBCPool");

            getNewInstanceMethod = sqlutilClass.getMethod("getInstance", null);
			getAllPoolNamesMethod = sqlutilClass.getMethod("getAllPoolnames", null);
			getPoolMethod = sqlutilClass.getMethod("getPool", new Class[] { String.class });
			getPoolDataSourceMethod = jdbcPoolClass.getMethod("getDataSource", null);


        }
		catch(ClassNotFoundException e)
		{
			DatabaseContextFactory.addErrorProperty(doc, parent, error+ e.toString());
            return;
		}
		catch(NoSuchMethodException e)
		{
			DatabaseContextFactory.addErrorProperty(doc, parent, error + e.toString());
            return;
		}

		try
		{
            sqlUtil = getNewInstanceMethod.invoke(null, empty);
			Enumeration poolList = (Enumeration) getAllPoolNamesMethod.invoke(sqlUtil, empty);
			while (poolList.hasMoreElements())
			{
				Element propertyElem = doc.createElement("property");
				String entry = (String) poolList.nextElement();
				DatabaseContextFactory.addText(doc, propertyElem, "name", "jdbc/" + entry);

				try
				{
					Object pool = getPoolMethod.invoke(sqlUtil, new Object[] { entry });
					DataSource source = (DataSource) getPoolDataSourceMethod.invoke(pool, null);
					DatabaseMetaData dbmd = source.getConnection().getMetaData();
					DatabaseContextFactory.addText(doc, propertyElem, "value", dbmd.getDriverName());
					DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Version " + dbmd.getDriverVersion());
					DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "URL: " + dbmd.getURL());
					DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "User: " + dbmd.getUserName());
				}
				catch (Exception ex)
				{
					DatabaseContextFactory.addText(doc, propertyElem, "value", ex.toString());
				}
				parent.appendChild(propertyElem);
			}
		}
		catch(InvocationTargetException e)
		{
			DatabaseContextFactory.addErrorProperty(doc, parent, e.toString());
		}
		catch(IllegalAccessException e)
		{
			DatabaseContextFactory.addErrorProperty(doc, parent, e.toString());
		}
    }
}
