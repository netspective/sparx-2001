/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:26 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueContext;

public interface Column
{
    public void finalizeDefn(Schema schema, Table table);

    public String getName();
    public String getNameForMapKey();
    public void setName(String value);

    public String getSqlDefn(String dbms);
    public void setSqlDefn(String dbms, String value);

    public String getDescription();
    public void setDescription(String value);

    public Table getTable();
    public void setTable(Table value);

    public int getIndexInRow();
    public void setIndexInRow(int value);

    public ForeignKey getForeignKey();
    public void setForeignKey(ForeignKey value);
    public void setForeignKeyRef(short type, String value);

    public int getSize();
    public void setSize(int value);

    public String getDataClassName();
    public void setDataClassName(String value);

    public boolean hasValue(RowData rowData);
    public Object getObjectValue(RowData rowData);
    public void setValueObject(RowData rowData, Object value);
    public void setValueSqlExpr(RowData rowData, String expr);

    public boolean hasValue(DataContext dc);
    public Object getObjectValue(DataContext dc);

    public ColumnData getDefaultValue();
    public void setDefaultValue(ColumnData value);

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
