/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 3, 2001
 * Time: 10:06:30 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db;

import com.xaf.value.ValueContext;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SqlServerDatabasePolicy extends BasicDatabasePolicy
{
    public Object executeAndGetSingleValue(Connection conn, String sql) throws SQLException
    {
        Object value = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            try
            {
                rs = stmt.executeQuery(sql);
                if(rs.next())
                    value = rs.getObject(1);
            }
            finally
            {
                if(rs != null) rs.close();
            }
        }
        catch(SQLException e)
        {
            throw new SQLException(e.toString() + " ["+ sql +"]");
        }
        finally
        {
            if(stmt != null) stmt.close();
        }
        return value;
    }

    public Object handleAutoIncPreDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        return null;
        //Object autoIncValue = executeAndGetSingleValue(conn, "select "+ seqOrTableName +".nextval from dual");
        //if(autoIncValue == null)
        //    throw new SQLException("Unable to obtain next ORACLE sequence value from sequence '"+ seqOrTableName +"'");
        //return autoIncValue;
    }

    public Object handleAutoIncPreDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, List columnNames, List columnValues) throws SQLException
    {
			return null;
        //Object autoIncValue = executeAndGetSingleValue(conn, "select "+ seqOrTableName +".nextval from dual");
        //if(autoIncValue != null)
        //{
        //    columnNames.add(autoIncColumnName);
        //    columnValues.add(autoIncValue);
        //    return autoIncValue;
        //}
        //else
        //    throw new SQLException("Unable to obtain next ORACLE sequence value from sequence '"+ seqOrTableName +"'");
    }

    public Object getAutoIncCurrentValue(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        //return executeAndGetSingleValue(conn, "select "+ seqOrTableName +".currval from dual");
        return null;
    }
}
