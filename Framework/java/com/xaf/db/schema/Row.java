/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:39 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.form.DialogContext;
import com.xaf.sql.DmlStatement;
import com.xaf.value.ValueContext;
import com.xaf.db.ConnectionContext;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public interface Row
{
    /**
     * Returns an array of all the column definitions in the order in which they were specified
     * for table creation.
     */
    public Column[] getColumns();

    /**
     * Returns an array that contains the current values of each of the columns in the Row.
     * The columns are ordered in the same order as the original definition of the table.
     */
    public Object[] getData();

    /**
     * Returns a List that of the current values of each of the columns in the Row. This list is
     * suitable for passing into the DmlStatement object.
     */
    public List getDataForDmlStatement();

    /**
     * Returns the value of primary key in the Row.
     */
    public Object getActivePrimaryKeyValue();

    /**
     * Given a ResultSet, populate the values of the Row with the values provided in the
     * ResultSet. This method assumes that the ResultSet columns are ordered in the same
     * manner as when the table was defined and is almost always used to populate a Row
     * when the ResultSet contains all the columns that are defined for a particular table.
     */
    public void populateDataByIndexes(ResultSet resultSet) throws SQLException;

    /**
     * Given a ResultSet, populate the values with the columns indexed in the manner
     * specified by the colNameIndexMap. Each entry in the colNameIndexMap supplies a
     * lower-cased column name as the map key and the column index number in the ResultSet
     * as the map key's value. If colNameIndexMap is null, then this method automatically
     * creates a map that is created by reading the ResultSetMetaData for the resultSet.
     * If a column of the Row is not found in the ResultSet, it's value is left unchanged.
     * This allows multiple calls to this method and the latest change is the change that
     * will remain.
     */
    public void populateDataByNames(ResultSet resultSet, Map colNameIndexMap) throws SQLException;

    /**
     * Given a DialogContext, populate the Row's column values with the values of the fields
     * in the DialogContext that match the names of the columns. For each column in the Row, if
     * a matching field value is not found, the value of the column will remain unchanged.
     */
    public void populateDataByNames(DialogContext dc);

    /**
     * Given a DialogContext, populate the Row's column values with the values of the fields
     * in the DialogContext that match the names of the columns. For each column in the Row, the
     * colNameFieldNameMap is checked for the mapping of a column name to a field name. If a
     * matching field name is found in the map, it is used. If a matching name is not found in
     * the map, then a field name the same as the column name will be located. In the
     * colNameFieldNameMap, the key is the column name and the value of
     * the key is the name of field that column's value comes from. If no matching column is
     * found either in the map or the regular field name, the column's current value is maintained.
     */
    public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap);

    /**
     * Given a DialogContext, populate the DialogContext's field values with the Row's column values
     * that match the names of the columns. If no matching field is found for any given column, that column's
     * value is ignored.
     */
    public void setData(DialogContext dc);

    /**
     * Given a DialogContext, populate the DialogContext's field values with the Row's column values
     * that match the names of the columns specified in the colNameFieldNamesMap. If a matching name
     * is not found in the map, then a field name the same as the column name will be located. In the
     * colNameFieldNameMap, the key is the column name and the value of the key is the name of field
     * that column's value goes to. If no matching field is found for any given column, that column's
     * value is ignored.
     */
    public void setData(DialogContext dc, Map colNameFieldNameMap);

    public DmlStatement createInsertDml(Table table);
    public DmlStatement createUpdateDml(Table table, String whereCond);
    public DmlStatement createDeleteDml(Table table, String whereCond);

    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;
    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;
    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;

    public void afterInsert(ConnectionContext cc) throws NamingException, SQLException;
    public void afterUpdate(ConnectionContext cc) throws NamingException, SQLException;
    public void afterDelete(ConnectionContext cc) throws NamingException, SQLException;
}
