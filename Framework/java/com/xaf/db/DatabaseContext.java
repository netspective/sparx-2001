package com.xaf.db;

import java.sql.*;
import javax.naming.*;

/**
 * Provides an interface for obtaining a default or named JDBC Connection object;
 * the implementations of this interface should manage connection pooling.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public interface DatabaseContext
{
	static public final int RESULTSET_NOT_SCROLLABLE = -9999;

	public Connection getConnection() throws NamingException, SQLException;
	public Connection getConnection(String dataSourceId) throws NamingException, SQLException;

	public int getScrollableResultSetType() throws NamingException, SQLException;
}