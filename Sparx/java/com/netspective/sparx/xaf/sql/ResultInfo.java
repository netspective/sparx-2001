/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Feb 6, 2002
 * Time: 11:56:59 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.netspective.sparx.xaf.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Element;

import com.netspective.sparx.util.value.ValueContext;

public class ResultInfo
{
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public ResultInfo(Connection conn, Statement stmt) throws SQLException
    {
        this.conn = conn;
        this.stmt = stmt;
        this.rs = stmt.getResultSet();
    }

    public ResultSet getResultSet()
    {
        return rs;
    }

    public void close() throws SQLException
    {
        rs.close();
        rs = null;
        stmt.close();
        stmt = null;
        if(conn.getAutoCommit() == true)
        {
            conn.close();
            conn = null;
        }
    }

    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }
}
