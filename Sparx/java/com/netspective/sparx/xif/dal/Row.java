/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: Row.java,v 1.6 2002-12-04 17:50:50 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xif.dal;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.sql.DmlStatement;
import com.netspective.sparx.xif.dal.validation.result.RowValidationResult;
import com.netspective.sparx.xif.db.DatabasePolicy;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import javax.naming.NamingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface Row
{
    /**
     * Passed into methods when the value of a NULL variable should be ignored and not assigned
     * to a column.
     */
    public final static int VALUEHANDLE_NULLIGNORE = 0;

    /**
     * Passed into methods when the value of a NULL variable should be not be ignored and instead
     * a null value should be assisgned to a column.
     */
    public final static int VALUEHANDLE_ASSIGN = 1;

    /**
     * Returns the table that this row belongs to
     */
    public Table getTable();

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
    // public List getDataForDmlStatement();

    /**
     * Returns a List that of the current values of each of the columns in the Row. This list is
     * suitable for passing into the DmlStatement object.
     */
    public List getDataForDmlStatement(DatabasePolicy dbPolicy);

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
     * a matching field value is not found, the value of the column will remain unchanged. If
     * valueHandling is set to VALUEHANDLE_NULLIGNORE then nulls will not be assigned
     * (use VALUEHANDLE_ASSIGN to set).
     */
    public void populateDataByNames(DialogContext dc, int valueHandling);

    /**
     * Given a DialogContext, populate the Row's column values with the values of the fields
     * in the DialogContext that match the names of the columns. For each column in the Row, the
     * colNameFieldNameMap is checked for the mapping of a column name to a field name. If a
     * matching field name is found in the map, it is used. If a matching name is not found in
     * the map, then a field name the same as the column name will be located. In the
     * colNameFieldNameMap, the key is the column name and the value of
     * the key is the name of field that column's value comes from. If no matching column is
     * found either in the map or the regular field name, the column's current value is maintained. If
     * valueHandling is set to VALUEHANDLE_NULLIGNORE then nulls will not be assigned
     * (use VALUEHANDLE_ASSIGN to set).
     */
    public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap, int valueHandling);

    /**
     * Given a DialogContext, populate the Row's column values with the values of the fields
     * in the DialogContext that match the names of the columns. For each column in the Row, if
     * a matching field value is not found, the value of the column will remain unchanged.
     * By default, this method simply calls populateDataByNames(dc, VALUEHANDLE_NULLIGNORE).
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
     * By default, this method simply calls populateDataByNames(dc, colNameFieldNameMap, VALUEHANDLE_NULLIGNORE).
     */
    public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap);

    /**
     * Given an XML element that contains column data, extract each column and
     * assign it to the appropriate data variable. The main element must follow this DTD (the
     * element parameter in the method call is considered the "row" element):
     * <pre>
     *     <!ELEMENT row (col)*>
     *          <!ELEMENT col %DATATYPE.TEXT;>
     *              <!ATTLIST col name CDATA #REQUIRED>
     * </pre>
     */
    public void populateDataByNames(Element element) throws ParseException, DOMException;

    /**
     * Returns true if the given XML node name is a valid column in this row.
     */
    public boolean isValidXmlNodeNameForColumn(String nodeName);

    /**
     * Set the column identified by an XML nodeName to the given value. XML node names may
     * be different than the actual column names so it's the responsibility of the row to
     * set it's appropriate data. This is typically called by Schema.importFromXml().
     * Append is set to true if the value should be appended to the current value or false
     * if the value should be replaced.
     */
    public boolean populateDataForXmlNodeName(String nodeName, String value, boolean append) throws ParseException;

    /**
     * Set the column identified by an XML nodeName to the given expression. XML node names may
     * be different than the actual column names so it's the responsibility of the row to
     * set it's appropriate data. This is typically called by Schema.importFromXml().
     */
    public boolean populateSqlExprForXmlNodeName(String nodeName, String expr) throws ParseException;

    /**
     * Return true if the given node name is valid XML node name for a child table/row
     */
    public boolean isValidXmlNodeNameForChildRow(String nodeName);

    /**
     * If the XML node name represents a valid child table for this row, return a new child row
     * for the appropriate child table. Otherwise, return null.
     */
    public Row createChildRowForXmlNodeName(String nodeName);

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

    /**
     * Create the DML that can be used to insert this row into the database.
     */
    public DmlStatement createInsertDml(Table table, DatabasePolicy dbPolicy);

    /**
     * Create the DML that can be used to update this row in the database.
     */
    public DmlStatement createUpdateDml(Table table, DatabasePolicy dbPolicy, String whereCond);

    /**
     * Create the DML that can be used to delete this row from the database.
     */
    public DmlStatement createDeleteDml(Table table, DatabasePolicy dbPolicy, String whereCond);

    /**
     * This method is executed immediately prior to an insert action on this row and
     * returns true if the insert that is about to be performed on this row should be allowed.
     */
    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;

    /**
     * This method is executed immediately prior to an update action on this row and returns
     * true if the insert that is about to be performed on this row should be allowed.
     */
    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;

    /**
     * This method is executed immediately prior to a delete action on this row and
     * true if the insert that is about to be performed on this row should be allowed.
     */
    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;

    /**
     * This method is executed immediately after a successful insert action on this row.
     */
    public void afterInsert(ConnectionContext cc) throws NamingException, SQLException;

    /**
     * This method is executed immediately after a successful update action on this row.
     */
    public void afterUpdate(ConnectionContext cc) throws NamingException, SQLException;

    /**
     * This method is executed immediately after a successful delete action on this row.
     */
    public void afterDelete(ConnectionContext cc) throws NamingException, SQLException;

    /**
     * Return true if this row has the ability to contain children. This method only returns
     * true if the row <i>can</i> contain children, not necessarily that the child rows
     * of this row have already been loaded.
     */
    public boolean isParentRow();

    /**
     * If this row has the ability to contain children, use the given ConnectionContext to
     * retrieve all of them now.
     */
    public void retrieveChildren(ConnectionContext cc) throws NamingException, SQLException;

    /**
     * Return true if this row contains valid data.  This check is a row-wide check
     * and should be designed to catch any errors that might be introduced due to
     * data in one column not corresponding to information in another column.  Such
     * errors cannot be caught at the Column level and must be caught at the Row
     * level
     */
    public boolean isValid();

    /**
     * Return true if this row contains valid data.  This check is the first check
     * that is performed on the data contained in this row.  It ensures that all
     * data stored in all the columns of the row pass each column's individual
     * validity checks before a row-wide validity check is done on the data.
     */
    public RowValidationResult getValidationResult();

}
