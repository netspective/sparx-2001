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

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;

public class ResultInfo
{
    private ValueContext vc;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public ResultInfo(ValueContext vc, Connection conn, Statement stmt) throws SQLException
    {
        this.vc = vc;
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
        if(rs != null)
        {
            rs.close();
            rs = null;
        }

        if(stmt != null)
        {
            stmt.close();
            stmt = null;
        }

        if(conn != null)
        {
            if(conn.getAutoCommit() == true)
            {
                conn.close();
                conn = null;
            }
        }
    }

    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }
}
