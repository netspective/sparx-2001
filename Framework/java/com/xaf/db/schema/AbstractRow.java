/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:20:47 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.form.DialogContext;
import com.xaf.sql.DmlStatement;
import com.xaf.db.ConnectionContext;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.naming.NamingException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public abstract class AbstractRow implements Row
{
    protected Table rowTable;
    protected Column[] rowColumns;
    protected boolean[] haveSqlExprData;
    protected String[] sqlExprData;

    public AbstractRow(Column[] columns)
    {
        setColumns(columns);
    }

    public AbstractRow(Table table)
    {
        setTable(table);
    }

    public Object getActivePrimaryKeyValue()
    {
        return null;
    }

    public void setCustomSqlExpr(int column, String sqlExpr)
    {
        Column[] columns = getColumns();
        if(sqlExprData == null)
            sqlExprData = new String[columns.length];
        sqlExprData[column] = sqlExpr;
        haveSqlExprData[column] = true;
    }

    public Table getTable() { return rowTable; }
    public void setTable(Table value)
    {
        rowTable = value;
        haveSqlExprData = new boolean[rowTable.getColumnsCount()];
    }

    public Column[] getColumns() { return rowTable != null ? rowTable.getAllColumns() : rowColumns; }
    public void setColumns(Column[] value)
    {
        rowColumns = value;
        haveSqlExprData = new boolean[rowColumns.length];
    }

    abstract public Object[] getData();
    abstract public List getDataForDmlStatement();

    abstract public void populateData(ResultSet rs) throws SQLException;
    abstract public void populateData(DialogContext dc);

    abstract public void setData(DialogContext dc);

    public boolean valuesAreEqual(Object primary, Object compareTo)
    {
        if(primary == null && compareTo == null)
            return true;

        if((primary == null && compareTo != null) || (primary != null && compareTo == null))
            return false;

        return primary.equals(compareTo);
    }

    public DmlStatement createInsertDml(Table table)
    {
        return new DmlStatement(table.getName(), table.getColumnNames(), getDataForDmlStatement());
    }

    public DmlStatement createUpdateDml(Table table, String whereCond)
    {
        return new DmlStatement(table.getName(), table.getColumnNames(), getDataForDmlStatement(), whereCond);
    }

    public DmlStatement createDeleteDml(Table table, String whereCond)
    {
        return new DmlStatement(table.getName(), whereCond);
    }

    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public void afterInsert(ConnectionContext cc) throws NamingException, SQLException
    {
    }

    public void afterUpdate(ConnectionContext cc) throws NamingException, SQLException
    {
    }

    public void afterDelete(ConnectionContext cc) throws NamingException, SQLException
    {
    }
}
