package com.xaf.db;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.sql.*;
import javax.naming.*;

import org.w3c.dom.*;

import com.xaf.value.*;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       Shahid N. Shah
 * @version 1.0
 */

public abstract class AbstractDatabaseContext implements DatabaseContext
{
	private static boolean forceNonScrollableRS;

    public AbstractDatabaseContext()
    {
    }

    public DatabasePolicy getDatabasePolicy(Connection conn) throws SQLException
    {
        return DatabaseContextFactory.getDatabasePolicy(conn);
    }

    public abstract Connection getConnection(String dataSourceId) throws NamingException, SQLException;

	public String translateDataSourceId(ValueContext vc, String dataSourceId)
	{
		return dataSourceId != null ? dataSourceId : vc.getServletContext().getInitParameter("default-data-source");
	}

	public abstract Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

    static public void setNonScrollableResultSet(boolean force)
	{
		forceNonScrollableRS = force;
	}

	public int getScrollableResultSetType(ValueContext vc, String dataSourceId) throws NamingException, SQLException
	{
		return getScrollableResultSetType(getConnection(vc, dataSourceId));
	}

	public int getScrollableResultSetType(Connection connection) throws NamingException, SQLException
	{
        if(forceNonScrollableRS)
            return RESULTSET_NOT_SCROLLABLE;

		DatabaseMetaData dbmd = connection.getMetaData();

		if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))
			return ResultSet.TYPE_SCROLL_INSENSITIVE;

		if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))
			return ResultSet.TYPE_SCROLL_SENSITIVE;

		return RESULTSET_NOT_SCROLLABLE;
	}

    public void createCatalog(ValueContext vc, Element parent) throws NamingException
	{
	}
}