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

public class OracleDatabasePolicy extends BasicDatabasePolicy
{
    public Object handleAutoIncPreDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, List columnNames, List columnValues) throws SQLException
    {
        Object autoIncValue = null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select "+ seqOrTableName +".nextval from dual");
        if(rs.next())
            autoIncValue = rs.getObject(1);
        rs.close();
        stmt.close();

        if(autoIncValue != null)
        {
            columnNames.add(autoIncColumnName);
            columnValues.add(autoIncValue);
            return autoIncValue;
        }
        else
            throw new SQLException("Unable to obtain next ORACLE sequence value from sequence '"+ seqOrTableName +"'");
    }

    public Object handleAutoIncPostDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, Object autoIncColumnValue) throws SQLException
    {
        return autoIncColumnValue;
    }

    public Object getAutoIncCurrentValue(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        Object autoIncValue = null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select "+ seqOrTableName +".currval from dual");
        if(rs.next())
            autoIncValue = rs.getObject(1);
        rs.close();
        stmt.close();

        if(autoIncValue != null)
            return autoIncValue;
        else
            throw new SQLException("Unable to obtain current ORACLE sequence value from sequence '"+ seqOrTableName +"'");
    }
}
