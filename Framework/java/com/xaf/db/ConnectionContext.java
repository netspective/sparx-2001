/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 17, 2001
 * Time: 8:29:12 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionContext
{
    static public final short CONNCTXTYPE_TRANSACTION = 0;
    static public final short CONNCTXTYPE_AUTO        = 1;

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

    public Connection getConnection() throws NamingException, SQLException { return conn == null ? open() : conn; }
    public void returnConnection() throws SQLException
    {
        if(type != CONNCTXTYPE_TRANSACTION && conn != null)
        {
            conn.close();
            conn = null;
        }
    }

    public DatabaseContext getDatabaseContext() { return dbc; }
    public DatabasePolicy getDatabasePolicy() { return dbp; }
    public String getDataSourceId() { return dataSourceId; }
    public short getType() { return type; }

    public Connection open() throws NamingException, SQLException
    {
        conn = dbc.getConnection(dataSourceId);
        dbp = dbc.getDatabasePolicy(conn);
        return conn;
    }

    public void beginTransaction() throws SQLException
    {
        type = CONNCTXTYPE_TRANSACTION;
    }

    public void endTransaction() throws SQLException
    {
        if(conn != null)
        {
            conn.close();
            conn = null;
        }
    }
}
