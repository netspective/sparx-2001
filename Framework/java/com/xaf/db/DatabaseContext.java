package com.xaf.db;

import java.sql.*;
import javax.naming.*;
import org.w3c.dom.*;
import com.xaf.value.*;

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

	/**
	 * Given a data source identifier, translate the name to a suitable name for
	 * the given ValueContext. For example, if the dataSourceId is null, return
	 * the <i>default</i> dataSource. If any per-user or per-request dataSourceId
	 * name changes are needed, override this method.
	 */
	public String translateDataSourceId(ValueContext vc, String dataSourceId);

	/**
	 * Returns a connection (with appropriate pooling) for the given dataSourceId.
	 */
	public Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

	/**
	 * Returns the ResultSet type (scrollabe, non-scrollable) for a given
	 * dataSourceId. This method is preferred over Connection.getResultSetType
	 * because many JDBC 2.0 drivers purport to support but are buggy so this
	 * method allows the opportunity to force an otherwise scrollable result set
	 * to be non-scrollable.
	 */
	public int getScrollableResultSetType(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

	/**
	 * Returns the ResultSet type (scrollabe, non-scrollable) for a given
	 * connection. This method is preferred over Connection.getResultSetType
	 * because many JDBC 2.0 drivers purport to support but are buggy so this
	 * method allows the opportunity to force an otherwise scrollable result set
	 * to be non-scrollable.
	 */
	public int getScrollableResultSetType(Connection connection) throws NamingException, SQLException;

	/**
	 * Creates catalog of dataSource identifiers and drivers for ACE Data Source
	 * tab.
	 */
    public void createCatalog(ValueContext vc, Element parent) throws NamingException;
}