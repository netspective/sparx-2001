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

public class AbstractDatabaseContext implements DatabaseContext
{
	private static boolean forceNonScrollableRS;

    public AbstractDatabaseContext()
    {
    }

	public String translateDataSourceId(ValueContext vc, String dataSourceId)
	{
		return dataSourceId != null ? dataSourceId : vc.getServletContext().getInitParameter("default-data-source");
	}

	public Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException
	{
		return null;
	}

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

    public void createCatalog(Element parent) throws NamingException
	{
	}
}