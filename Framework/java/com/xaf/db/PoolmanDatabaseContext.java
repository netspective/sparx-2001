package com.xaf.db;

import com.codestudio.sql.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

public class PoolmanDatabaseContext implements DatabaseContext
{
    static private boolean forceNonScrollableRS;
	static private Context env;

	private String jndiKey;
	private Connection connection;

    public PoolmanDatabaseContext(Connection conn)
    {
        connection = conn;
    }

    public PoolmanDatabaseContext(String aJndiKey)
	{
		jndiKey = aJndiKey;
    }

	public final Connection getConnection() throws NamingException, SQLException
	{
		if(connection == null)
			connection = getConnection(jndiKey);
	    return connection;
	}

	public final Connection getConnection(String dataSourceId) throws NamingException, SQLException
	{
	    // load the PoolMan JDBC Driver
        try
	    {
            Class.forName("com.codestudio.sql.PoolMan").newInstance();
	    }
        catch (Exception e)
        {
            throw new SQLException("Poolman ClassNotFoundException");
        }
	    Connection con = DriverManager.getConnection("jdbc:poolman://" + dataSourceId);

		return con;
	}

    static public void setNonScrollableResultSet(boolean force) { forceNonScrollableRS = force; }

	public final int getScrollableResultSetType() throws NamingException, SQLException
	{
        if(forceNonScrollableRS)
            return RESULTSET_NOT_SCROLLABLE;

		getConnection();

		DatabaseMetaData dbmd = connection.getMetaData();

		if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))
			return ResultSet.TYPE_SCROLL_INSENSITIVE;

		if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))
			return ResultSet.TYPE_SCROLL_SENSITIVE;

		return RESULTSET_NOT_SCROLLABLE;
	}
}
