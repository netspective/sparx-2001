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
    /**
     * This method is called to give the Table the opportunity to fill its
     * columns.
     */
    public void initializeDefn();

    /**
     * This method is called once all the columns and tables in the entire schema
     * have been created so that foreign-key references and other late-binding
     * activities can take place.
     */
    public void finalizeDefn();

    /**
     * Returns the schema that owns this table.
     */
    public Schema getParentSchema();

    /**
     * Sets the schema that owns this table.
     */
    public void setParentSchema(Schema value);

    /**
     * Returns the name of the table as it appears in the database.
     */
    public String getName();

    /**
     * Returns the name of the table suitable for use as a key in a Map for
     * runtime lookup purposes.
     */
    public String getNameForMapKey();

    /**
     * Sets the name of the table as it appears in the database.
     */
    public void setName(String value);

    /**
     * Returns the name of the table suitable for use as an XML node/element.
     */
    public String getNameForXmlNode();

    /**
     * Sets the name of the table suitable for use as an XML node/element.
     */
    public void setNameForXmlNode(String value);

    /**
     * Returns the description of this table.
     */
    public String getDescription();

    /**
     * Sets the description of this table.
     */
    public void setDescription(String value);

    /**
     * Returns the number of columns in this table.
     */
    public int getColumnsCount();

    /**
     * Add a column definition to this table.
     */
    public void addColumn(Column column);

    /**
     * Returns all the of the Column definitions in this table as a List
     */
    public List getColumnsList();

    /**
     * Returns all the of the column names in this table as a List
     */
    public List getColumnNames();

    /**
     * Returns a Map of all the columns in the this table (each column name
     * is the map key and the value is a Column object).
     */
    public Map getColumnsMap();

    /**
     * Returns all the of the Column definitions in this table as an array
     */
    public Column[] getAllColumns();

    /**
     * Returns all the of the Column definitions of the sequenced columns in this table as an array
     */
    public Column[] getSequencedColumns();

    /**
     * Returns the column with the specified name.
     */
    public Column getColumn(String name);

    /**
     * Returns the column at the specified index.
     */
    public Column getColumn(int index);

    /**
     * Create a single row object suitable for storing data for this table's columns
     */
    public Row createRow();

    /**
     * Create a multiple rows object suitable for storing data for this table's columns
     */
    public Rows createRows();

    /**
     * Register that the fKey is dependent upon this table.
     */
    public void registerForeignKeyDependency(ForeignKey fKey);

    /**
     * Using the primary key of the given row, go back to the database and reload the
     * data into the row.
     */
    public void refreshData(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /**
     * Using the primary key of the given row, go to the database and load the
     * data into a temporary row and return true if the data in the database has
     * changed.
     */
    public boolean dataChangedInStorage(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /**
     * Insert the given Row into the database.
     */
    public boolean insert(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /**
     * Update the given Row in the database using the whereCond and bind parameters.
     * @param whereCond The condition that is appened to the update statement like <code>" where " + whereCond</code>
     * @param whereCondBindParams Any optional list of bind parameters that should be bound to the whereCond
     */
    public boolean update(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException;

    /**
     * Delete the given Row in the database using the whereCond and bind parameters.
     * @param whereCond The condition that is appened to the delete statement like <code>" where " + whereCond</code>
     * @param whereCondBindParams Any optional list of bind parameters that should be bound to the whereCond
     */
    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object[] whereCondBindParams) throws NamingException, SQLException;

    /**
     * Update the given Row in the database using the primary key of the row that is provided.
     */
    public boolean update(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /**
     * Delete the given Row in the database using the primary key of the row that is provided.
     */
    public boolean delete(ConnectionContext cc, Row row) throws NamingException, SQLException;

    /**
     * Update the given Row in the database using the whereCond and bind parameter.
     * @param whereCond The condition that is appened to the update statement like <code>" where " + whereCond</code>
     * @param whereCondBindParams A single bind parameters that should be bound to the whereCond
     */
    public boolean update(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException;

    /**
     * Delete the given Row in the database using the whereCond and bind parameter.
     * @param whereCond The condition that is appened to the delete statement like <code>" where " + whereCond</code>
     * @param whereCondBindParams A single bind parameters that should be bound to the whereCond
     */
    public boolean delete(ConnectionContext cc, Row row, String whereCond, Object whereCondBindParam) throws NamingException, SQLException;
}
