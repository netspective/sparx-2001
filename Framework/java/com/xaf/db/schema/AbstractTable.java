/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:27:54 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.xaf.sql.query.*;
import com.xaf.sql.DmlStatement;
import com.xaf.db.ConnectionContext;

import javax.naming.NamingException;

/**
 * NOTE: SUPPORTS ONLY TABLES WITH SINGLE-COLUMN FOREIGN AND PRIMARY KEYS (NO COMPOUND PRIMARY OR FOREIGN KEYS)
 */

public abstract class AbstractTable implements Table
{
    private Schema schema;
    private String name;
    private String description;
    private List columnsList = new ArrayList();
    private Map columnsMap = new HashMap();
    private List columnNames = new ArrayList(); // useful for caching just the names (used in DmlStatement)
    private String columNamesForSelect;
    private String selectByPrimaryKeySql;
    private String primaryKeyBindSql;
    private Map childTables;
    private Column[] allColumns;
    private Column[] requiredColumns;
    private Column[] sequencedColumns;
	private Column[] primaryKeys;

    static public String convertTableNameForMapKey(String name)
    {
        return name.toUpperCase();
    }

    public AbstractTable(Schema schema, String name)
    {
        setName(name);
        setParentSchema(schema);
        initializeDefn();
        schema.addTable(this);
    }

    public Schema getParentSchema() { return schema; }
    public void setParentSchema(Schema value) { schema = value; }

    abstract public void initializeDefn();

    public void finalizeDefn()
    {
        List sequencedCols = new ArrayList();
        List requiredCols = new ArrayList();

        for(Iterator i = getColumnsList().iterator(); i.hasNext(); )
        {
            Column column = (Column) i.next();
            column.finalizeDefn();
            if(column.isPrimaryKey())
                primaryKeyBindSql = column.getName() + " = ?";
            if(column.getSequenceName() != null)
                sequencedCols.add(column);
            if(column.isRequired() && ! column.isSequencedPrimaryKey())
                requiredCols.add(column);
        }

        if(allColumns == null)
            allColumns = (Column[]) columnsList.toArray(new Column[columnsList.size()]);

        if(sequencedCols.size() > 0)
            sequencedColumns = (Column[]) sequencedCols.toArray(new Column[sequencedCols.size()]);

        if(requiredCols.size() > 0)
            requiredColumns = (Column[]) requiredCols.toArray(new Column[requiredCols.size()]);
    }

    public String getName() { return name; }
    public String getNameForMapKey() { return name.toUpperCase(); }
    public void setName(String value) { name = value; }

    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }

    public int getColumnsCount() { return columnsList.size(); }
    public List getColumnsList() { return columnsList; }
    public Map getColumnsMap() { return columnsMap; }
    public Column getColumn(String name) { return (Column) columnsMap.get(AbstractColumn.convertColumnNameForMapKey(name)); }
    public Column getColumn(int index) { return (Column) columnsList.get(index); }
    public List getColumnNames() { return columnNames; }

    public Column[] getAllColumns() { return allColumns; }
    public Column[] getSequencedColumns() { return sequencedColumns; }
    public Column[] getPrimaryKeyColumns() { return primaryKeys; }

    protected void setAllColumns(Column[] value) { allColumns = value; }

    public void addColumn(Column column)
    {
        if(columnsMap.containsKey(column.getNameForMapKey()))
            return;

        columnNames.add(column.getName());
        columnsList.add(column);
        columnsMap.put(column.getNameForMapKey(), column);
    }

    public String getColumnNamesForSelect()
    {
        if(columNamesForSelect == null)
        {
            StringBuffer str = new StringBuffer();
            for(int c = 0; c < columnsList.size(); c++)
            {
                if(str.length() > 0)
                    str.append(", ");
                str.append(((Column) columnsList.get(c)).getName());
            }
            columNamesForSelect = str.toString();
        }
        return columNamesForSelect;
    }

    abstract public Row createRow();

    public void refreshData(ConnectionContext cc, Row row) throws NamingException, SQLException
    {
        getRecordByPrimaryKey(cc, row.getActivePrimaryKeyValue(), row);
    }

    public boolean dataChangedInStorage(ConnectionContext cc, Row row) throws NamingException, SQLException
    {
        Row compareTo = getRecordByPrimaryKey(cc, row.getActivePrimaryKeyValue(), null);
        return ! row.equals(compareTo);
    }

    protected Row getRecordByPrimaryKey(ConnectionContext cc, Object pkValue, Row row) throws NamingException, SQLException
    {
        Row result = row;

        if(selectByPrimaryKeySql == null)
            selectByPrimaryKeySql = "select " + getColumnNamesForSelect() + " from " + getName() + " where " + primaryKeyBindSql;

        Connection conn = cc.getConnection();
        try
        {
            PreparedStatement stmt = null;
            try
            {
                stmt = conn.prepareStatement(selectByPrimaryKeySql);
                stmt.setObject(1, pkValue);
                if(stmt.execute())
                {
                    ResultSet rs = stmt.getResultSet();
                    if(rs.next())
                    {
                        if(result == null) result = createRow();
                        result.populateData(rs);
                    }
                }
                return result;
            }
            catch(SQLException e)
            {
                throw new SQLException(e.toString() + " ["+ selectByPrimaryKeySql +" (bind = "+ (pkValue != null ? "'" + pkValue + "' {"+ pkValue.getClass().getName() + "}" : "none") +")]");
            }
            finally
            {
                if(stmt != null) stmt.close();
            }
        }
        finally
        {
            cc.returnConnection();
        }
    }

    public void registerForeignKeyDependency(ForeignKey fKey)
    {
        // if we are the "referenced" foreign key, then the source is a child of ours
        if(fKey.getType() == ForeignKey.FKEYTYPE_PARENT)
        {
            if(childTables == null) childTables = new HashMap();
            Table childTable = fKey.getSourceColumn().getParentTable();
            childTables.put(childTable.getNameForMapKey(), childTable);
        }
    }

    public void validateDmlValues(DmlStatement dml) throws SQLException
    {
        List columnValues = dml.getColumnValues();
        for(int i = 0; i < allColumns.length; i++)
        {
            Column col = allColumns[i];
            if((col.isRequired() && ! col.isSequencedPrimaryKey()) && columnValues.get(i) == null)
                throw new SQLException("Required column '"+ col.getName() +"' does not have a value in table '"+ col.getParentTable().getName() +"'");
        }
    }

    public boolean executeDml(ConnectionContext cc, Row row, DmlStatement dml, Object[] additionalBindParams) throws NamingException, SQLException
    {
        boolean result = false;
        PreparedStatement stmt = null;
        try
        {
            stmt = cc.getConnection().prepareStatement(dml.getSql());

            int columnNum = 1;
            boolean[] bindValues = dml.getBindValues();
            List columnValues = dml.getColumnValues();
            if(bindValues != null)
            {
                for(int c = 0; c < bindValues.length; c++)
                {
                    if(bindValues[c])
                    {
                        stmt.setObject(columnNum, columnValues.get(c));
                        columnNum++;
                    }
                }
            }

            if(additionalBindParams != null)
            {
                for(int i = 0; i < additionalBindParams.length; i++)
                {
                    stmt.setObject(columnNum, additionalBindParams[i]);
                    columnNum++;
                }
            }

            return stmt.execute();
        }
        catch(SQLException e)
        {
            StringBuffer bindParams = new StringBuffer();
            int columnNum = 1;
            boolean[] bindValues = dml.getBindValues();
            List columnValues = dml.getColumnValues();
            if(bindValues != null)
            {
                for(int c = 0; c < bindValues.length; c++)
                {
                    if(bindValues[c])
                    {
                        Object value = columnValues.get(c);
                        if(columnNum > 1)
                            bindParams.append(", ");
                        bindParams.append(columnNum);
                        bindParams.append(": ");
                        bindParams.append(value == null ? "NULL" : (value.toString() + " {" + value.getClass().getName() + "}"));
                        columnNum++;
                    }
                }
            }

            if(additionalBindParams != null)
            {
                for(int i = 0; i < additionalBindParams.length; i++)
                {
                    Object value = additionalBindParams[i];
                    if(columnNum > 1)
                        bindParams.append(", ");
                    bindParams.append(columnNum);
                    bindParams.append(": ");
                    bindParams.append(value == null ? "NULL" : (value.toString() + " {" + value.getClass().getName() + "}"));
                    columnNum++;
                }
            }

            throw new SQLException(e.toString() + " ["+ dml.getSql() +"\n(bind "+ (bindParams.length() > 0 ? bindParams.toString() : "none") +")\n"+ row.toString() +"]");
        }
        finally
        {
            if(stmt != null) stmt.close();
        }
    }

    public boolean insert(ConnectionContext cc, Row row) throws NamingException, SQLException
    {
        DmlStatement dml = row.createInsertDml(this);
        validateDmlValues(dml);

        if(! row.beforeInsert(cc, dml))
            return false;

        boolean successful = executeDml(cc, row, dml, null);
        row.afterInsert(cc);

        cc.returnConnection();
        return successful;
    }

    public boolean update(ConnectionContext cc, Row row) throws NamingException, SQLException
    {
        return update(cc, row, primaryKeyBindSql, new Object[] { row.getActivePrimaryKeyValue() });
    }

    public boolean update(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException
    {
        DmlStatement dml = row.createUpdateDml(this, whereCond);
        validateDmlValues(dml);

        if(! row.beforeUpdate(cc, dml))
            return false;

        boolean successful = executeDml(cc, row, dml, whereCondBindParams);
        row.afterUpdate(cc);

        cc.returnConnection();
        return successful;
    }

    public boolean delete(ConnectionContext cc, Row row) throws NamingException, SQLException
    {
        return delete(cc, row, primaryKeyBindSql, new Object[] { row.getActivePrimaryKeyValue() });
    }

    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException
    {
        DmlStatement dml = row.createUpdateDml(this, whereCond);
        if(! row.beforeDelete(cc, dml))
            return false;

        boolean successful = executeDml(cc, row, dml, whereCondBindParams);
        row.afterDelete(cc);

        cc.returnConnection();
        return successful;
    }

    public boolean update(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException
    {
        return update(cc, row, whereCond, new Object[] { whereCondBindParam });
    }

    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException
    {
        return delete(cc, row, whereCond, new Object[] { whereCondBindParam });
    }
}
