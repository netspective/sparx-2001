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
 * $Id: Column.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.xif.dal;

import java.util.List;

public interface Column
{
    /**
     * This method is called once all the columns and tables in the entire schema
     * have been created so that foreign-key references and other late-binding
     * activities can take place.
     */
    public void finalizeDefn();

    /**
     * Returns the name of the column as it appears in the database.
     */
    public String getName();

    /**
     * Returns the name of the column suitable for use as a key in a Map for
     * runtime lookup purposes.
     */
    public String getNameForMapKey();

    /**
     * Sets the name of the column as it appears in the database.
     */
    public void setName(String value);

    /**
     * Return the SQL definition that can be used to create this column for the
     * database ID specified in the dbms parameter.
     */
    public String getSqlDefn(String dbms);

    /**
     * Set the SQL definition that can be used to create this column for the
     * database ID specified in the dbms parameter.
     */
    public void setSqlDefn(String dbms, String value);

    /**
     * Return the description of this column.
     */
    public String getDescription();

    /**
     * Sets the description of this column.
     */
    public void setDescription(String value);

    /**
     * Returns the table that owns this column.
     */
    public Table getParentTable();

    /**
     * Sets the table that owns this column.
     */
    public void setParentTable(Table value);

    /**
     * If this column is sequenced in the database (assuming the database supports sequences),
     * then return the sequence name or return null if the column is not sequenced.
     */
    public String getSequenceName();

    /**
     * Set the sequence name of this column.
     */
    public void setSequenceName(String value);

    /**
     * Returns the @link ForeignKey object for this column or null if the column is not a
     * foreign key reference.
     */
    public ForeignKey getForeignKey();

    /**
     * Sets the foreign key object for this column.
     * @param value A @link ForeignKey in the current schema
     */
    public void setForeignKey(ForeignKey value);

    /**
     * Sets the foreign key reference for this column.
     * @param type Either ForeignKey.FKEYTYPE_SELF, ForeignKey.FKEYTYPE_PARENT, or ForeignKey.FKEYTYPE_LOOKUP
     * @param value A string in the format Table_Name.column_name that refers to an existing table and column
     */
    public void setForeignKeyRef(short type, String value);

    /**
     * Registers foreign key dependency for this column.
     * @param fkey The foreign key from another table that references this column
     */
    public void registerForeignKeyDependency(ForeignKey fKey);

    /**
     * Returns the list of foreign keys that are dependent upon this column.
     */
    public List getDependentForeignKeys();

    /**
     * Returns the size of this column if the column is a text or string column.
     */
    public int getSize();

    /**
     * Sets the size of this column.
     */
    public void setSize(int value);

    /**
     * Returns the name of the Java class that is used to store this column's data
     */
    public String getDataClassName();

    /**
     * Sets the name of the Java class that is used to store this column's data
     */
    public void setDataClassName(String value);

    /**
     * Returns the SQL expression used to set the default value of this column.
     */
    public String getDefaultSqlExprValue();

    /**
     * Sets the SQL expression used to set the default value of this column.
     */
    public void setDefaultSqlExprValue(String value);

    public boolean isIndexed();

    public boolean isNaturalPrimaryKey();

    public boolean isPrimaryKey();

    public boolean isRequired();

    public boolean isSequencedPrimaryKey();

    public boolean isUnique();

    public void setIsIndexed(boolean flag);

    public void setIsNaturalPrimaryKey(boolean flag);

    public void setIsRequired(boolean flag);

    public void setIsSequencedPrimaryKey(boolean flag);

    public void setIsUnique(boolean flag);
}
