/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 3, 2001
 * Time: 10:06:08 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db;

import com.xaf.value.ValueContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BasicDatabasePolicy implements DatabasePolicy
{
    public Object handleAutoIncPreDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        return null;
    }

    public Object handleAutoIncPostDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName, Object autoIncColumnValue) throws SQLException
    {
        return autoIncColumnValue;
    }

    public Object handleAutoIncPreDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, List columnNames, List columnValues) throws SQLException
    {
        return null;
    }

    public Object handleAutoIncPostDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, Object autoIncColumnValue) throws SQLException
    {
        return autoIncColumnValue;
    }

    public Object getAutoIncCurrentValue(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        return null;
    }
}
