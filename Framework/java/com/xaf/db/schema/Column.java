/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:26 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.util.List;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueContext;
import org.w3c.dom.Element;

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
