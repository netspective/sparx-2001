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
 * $Id: Table.java,v 1.2 2002-08-29 03:35:13 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

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
     * Register the given table as a child table of this table
     */
    public void registerChildTable(Table table);
    /**
     *
     * Returns a child table with the given name or null if there is no such child table
     */
    public Table getChildTable(String name);

    /**
     * Returns a child table with the given XML node name or null if there is no such child table
     */
    public Table getChildTableForXmlNode(String nodeName);

    /**
     * Returns the number of child tables for this table
     */
    public int getChildTablesCount();

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
