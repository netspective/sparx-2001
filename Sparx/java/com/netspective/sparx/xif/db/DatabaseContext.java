/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: DatabaseContext.java,v 1.5 2002-10-14 00:15:43 shahid.shah Exp $
 */

package com.netspective.sparx.xif.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.w3c.dom.Element;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.DataSourceEntriesListValue;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;

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
     * Given a connection, return the DatabasePolicy object for the specific database.
     */
    public DatabasePolicy getDatabasePolicy(Connection conn) throws SQLException;

    /**
     * Given a data source identifier, translate the name to a suitable name for
     * the given ValueContext. For example, if the dataSourceId is null, return
     * the <i>default</i> dataSource. If any per-user or per-request dataSourceId
     * name changes are needed, override this method. ValueContext can be null when
     * not running within a servlet environment.
     */
    public String translateDataSourceId(ValueContext vc, String dataSourceId);

    /**
     * Given a data source, perform any special runtime translation of the data source parameters such URL and user name.
     * ValueContext can be null when not running within a servlet environment. DataSource can also be null if no
     * dataSource was found by a given dataSourceId.
     */
    public DataSource translateDataSource(ValueContext vc, String dataSourceId, DataSource dataSource);

    /**
     * Returns a connection (with appropriate pooling) for the given dataSourceId (no datasource translation performed)
     */
    public Connection getConnection(String dataSourceId) throws NamingException, SQLException;

    /**
     * Returns a connection (with appropriate pooling) for the given dataSourceId. Datasource translation is performed
     * by using the translateDataSourceId() method (e.g. null dataSource is the "default" data source).
     */
    public Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

    /**
     * Get a connection for the dataSourceId but set it to be shared, with autoCommit turned off.
     * In the shared connection mode, anytime the dataSourceId is requested later using getConnection() it will
     * return the provided shared connection. For example, this is useful when a single connection should used
     * throughout multiple SQL statements within a transaction.
     * @param dataSourceId the dataSourceId of the data source to share
     * @param conn the actual connection object to share
     */
    public Connection beginConnectionSharing(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

    /**
     * Ends the connection sharing the provided dataSourceId. Connection is automatically closed. If rollback
     * is called then the connection's transaction will be rolled back otherwise the transaction will be committed.
     **/
    public void endConnectionSharing(ValueContext vc, String dataSourceId, boolean commit) throws SQLException;

    /**
     * If beginConnectionSharing method was called, this method returns the shared connection. Otherwise, it returns
     * null indicating there is currently no shared connection.
     */
    public Connection getSharedConnection(ValueContext vc, String dataSourceId);

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

    /**
     * Given a filter criteria, get a list of all the available data sources suitable for a selection from the UI.
     */
    public void populateDataSources(ValueContext vc, SelectChoicesList scl, DataSourceEntriesListValue dselv);
}