/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:26:57 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.db.ConnectionContext;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface Table
{
    public void initializeDefn();
    public void finalizeDefn();

    public Schema getParentSchema();
    public void setParentSchema(Schema value);

    public String getName();
    public String getNameForMapKey();
    public void setName(String value);

    public String getDescription();
    public void setDescription(String value);

    public int getColumnsCount();
    public void addColumn(Column column);
    public List getColumnsList();
    public List getColumnNames();
    public Map getColumnsMap();

    public Column[] getAllColumns();
    public Column[] getSequencedColumns();

    public Column getColumn(String name);
    public Column getColumn(int index);

    public Row createRow();
    public Rows createRows();

    public void registerForeignKeyDependency(ForeignKey fKey);

    public void refreshData(ConnectionContext cc, Row row) throws NamingException, SQLException;
    public boolean dataChangedInStorage(ConnectionContext cc, Row row) throws NamingException, SQLException;

    public boolean insert(ConnectionContext cc, Row row) throws NamingException, SQLException;
    public boolean update(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException;
    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException;

    /* convenience methods for updating/deleting the row based on the active primary key in the record */

    public boolean update(ConnectionContext cc, Row row) throws NamingException, SQLException;
    public boolean delete(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /* convenience methods for when there is a single bind parameter (most common occurrences) */

    public boolean update(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException;
    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException;
}
