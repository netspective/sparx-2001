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
    public void finalizeDefn();

    public String getName();
    public String getNameForMapKey();
    public void setName(String value);

    public String getSqlDefn(String dbms);
    public void setSqlDefn(String dbms, String value);

    public String getDescription();
    public void setDescription(String value);

    public Table getParentTable();
    public void setParentTable(Table value);

    public String getSequenceName();
    public void setSequenceName(String value);

    public ForeignKey getForeignKey();
    public void setForeignKey(ForeignKey value);
    public void setForeignKeyRef(short type, String value);
    public void registerForeignKeyDependency(ForeignKey fKey);
    public List getDependentForeignKeys();

    public int getSize();
    public void setSize(int value);

    public String getDataClassName();
    public void setDataClassName(String value);

    public String getDefaultSqlExprValue();
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
