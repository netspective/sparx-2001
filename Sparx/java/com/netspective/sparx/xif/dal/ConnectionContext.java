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
 * $Id: ConnectionContext.java,v 1.3 2002-08-29 03:35:35 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabasePolicy;
/**
 * Database Object used by other DAL objects for accessing the database. To execute DAL operations within a
 * transaction, use the <code>beginTransaction()</code> and <code>commitTransaction()</code> methods to
 * mark the beginning and ending of the transaction.
 * <p>
 * It is important when using a transaction, the rollback
 * method, <code>rollbackTransaction()</code>, is used when exceptions occur. For example:
 * </p>
 * <pre>
 * try
 * {
 *      ..
 *      ConnectionContext cc =  ConnectionContext.getConnectionContext(databaseContext, dataSourceId,
 *          ConnectionContext.CONNCTXTYPE_TRANSACTION);
 *      cc.beginTransaction();
 *      // do DAL operations here
 *      ...
 *      cc.commitTransaction();
 * }
 * catch (Exception e)
 * {
 *      try
 *      {
 *          cc.rollbackTransaction();
 *      }
 *      catch (SQLException sqle)
 *      {
 *          // Failed to rollback. Do application specific handling
 *          ...
 *      }
 * }
 * </pre>
 *
 */
public class ConnectionContext
{
    static public final short CONNCTXTYPE_TRANSACTION = 0;
    static public final short CONNCTXTYPE_AUTO = 1;

    private short type;
    private Connection conn;
    private DatabaseContext dbc;
    private DatabasePolicy dbp;
    private String dataSourceId;

    public static ConnectionContext getConnectionContext(DatabaseContext dbc, String dataSourceId, short type) throws NamingException, SQLException
    {
        return new ConnectionContext(dbc, dataSourceId, type);
    }

    public ConnectionContext(DatabaseContext dbc, String dataSourceId, short type) throws NamingException, SQLException
    {
        this.dbc = dbc;
        this.dataSourceId = dataSourceId;
        this.type = type;
        open();
    }

    public Connection getConnection() throws NamingException, SQLException
    {
        return conn == null ? open() : conn;
    }

    public void returnConnection() throws SQLException
    {
        if(type != CONNCTXTYPE_TRANSACTION && conn != null)
        {
            conn.close();
            conn = null;
        }
    }

    public DatabaseContext getDatabaseContext()
    {
        return dbc;
    }

    public DatabasePolicy getDatabasePolicy()
    {
        return dbp;
    }

    public String getDataSourceId()
    {
        return dataSourceId;
    }

    public short getType()
    {
        return type;
    }

    public Connection open() throws NamingException, SQLException
    {
        conn = dbc.getConnection(dataSourceId);
        if (type == CONNCTXTYPE_TRANSACTION)
            conn.setAutoCommit(false);
        dbp = dbc.getDatabasePolicy(conn);
        return conn;
    }

    /**
     * Start a database transaction. If the <code>ConnectionContext</code> object was created
     * without specifying the type to be <code>CONNCTXTYPE_TRANSACTION</code>, this method will
     * modify the object into a transaction type and any database operation after this will become
     * a part of a new transaction.
     *
     */
    public void beginTransaction() throws SQLException
    {
        // if the connection context object was created without specifying it as a transaction type
        // then it needs to be changed to a transaction type. Also disable the
        // autocommit feature of the database connection so that any database operation after this
        // will be a part of the transaction
        if (type != CONNCTXTYPE_TRANSACTION)
        {
            type = CONNCTXTYPE_TRANSACTION;
            if (conn != null)
                conn.setAutoCommit(false);
        }
    }

    /**
     * Rolls back database operations executed within the transaction and closes the connection.
     * No action is taken if the connection is not a transaction type.
     *
     * @exception SQLException
     * @since Version 2.0.2 Build 0
     */
    public void rollbackTransaction() throws SQLException
    {
        if (conn != null && type == CONNCTXTYPE_TRANSACTION)
        {
            conn.rollback();
            conn.close();
            conn = null;
        }
    }

    /**
     * Commits database operations executed within the transaction and closes the connection.
     * No action is taken if the connection is not a transaction type.
     *
     * @exception SQLException
     */
    public void commitTransaction() throws SQLException
    {
        if(conn != null && type == CONNCTXTYPE_TRANSACTION)
        {
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            conn = null;
        }
    }

    /**
     * Rolls back database operations executed within the transaction but leaves the connection open.
     * No action is taken if the connection is not a transaction type.
     *
     * @exception SQLException
     * @since Version 2.0.2 Build 0
     */
    public void rollbackActiveTransaction() throws SQLException
    {
        if (conn != null && type == CONNCTXTYPE_TRANSACTION)
            conn.rollback();
    }

    /**
     * Commits database operations executed within the transaction but leaves the connection open
     * No action is taken if the connection is not a transaction type.
     *
     * @exception SQLException
     */
    public void commitActiveTransaction() throws SQLException
    {
        if(conn != null && type == CONNCTXTYPE_TRANSACTION)
            conn.commit();
    }
}
