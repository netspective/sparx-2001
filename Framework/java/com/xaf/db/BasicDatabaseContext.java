package com.xaf.db;

import java.io.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

/**
 * The reference DatabaseContext implementation for a servlet or JSP using XAF
 * database and SQL packages.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class BasicDatabaseContext implements DatabaseContext
{
    static private boolean forceNonScrollableRS;
	static private Context env;

	private String jndiKey;
	private Connection connection;

    public BasicDatabaseContext(Connection conn)
	{
		connection = conn;
    }

    public BasicDatabaseContext(String aJndiKey)
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
		if(env == null)
			env = (Context) new InitialContext().lookup("java:comp/env");

		DataSource source = (DataSource) env.lookup(dataSourceId);
        if(source == null)
            throw new NamingException("Data source '" + dataSourceId + "' not found");

		return source.getConnection();
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