/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:20:47 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.form.DialogContext;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.*;

public abstract class AbstractSchema implements Schema
{
    private Map tablesMap = new HashMap();

    public AbstractSchema()
    {
    }

    public AbstractSchema(List tables)
    {
        for(int i = 0; i < tables.size(); i++)
            addTable((Table) tables.get(i));
    }

    public AbstractSchema(Table[] tables)
    {
        for(int i = 0; i < tables.length; i++)
            addTable(tables[i]);
    }

    abstract public void initializeDefn();

    public void finalizeDefn()
    {
        for(Iterator i = tablesMap.values().iterator(); i.hasNext(); )
        {
            Table table = (Table) i.next();
            table.finalizeDefn();
        }
    }

    public int getTablesCount()
    {
        return tablesMap.size();
    }

    public void addTable(Table table)
    {
        tablesMap.put(table.getNameForMapKey(), table);
    }

    public Map getTablesMap()
    {
        return tablesMap;
    }

    public Table getTable(String name)
    {
        return (Table) tablesMap.get(AbstractTable.convertTableNameForMapKey(name));
    }

    public Column getColumn(String tableName, String tableColumn)
    {
        Table table = getTable(tableName);
        if(table == null)
            return null;
        return table.getColumn(tableColumn);
    }

    public ForeignKey getForeignKey(Column srcColumn, short type, String ref)
    {
        String refTableName = null;
        String refColumnName = null;

        int delimPos = ref.indexOf(".");
        if(delimPos == -1)
        {
            refTableName = ref;
            refColumnName = "id";
        }
        else
        {
            refTableName = ref.substring(0, delimPos);
            refColumnName = ref.substring(delimPos+1);
        }

        Table table = getTable(refTableName);
        if(table == null)
            return null;

        Column refColumn = table.getColumn(refColumnName);
        if(refColumn == null)
            return null;

        switch(type)
        {
            case ForeignKey.FKEYTYPE_LOOKUP:
                return new BasicForeignKey(srcColumn, refColumn);

            case ForeignKey.FKEYTYPE_PARENT:
                return new ParentForeignKey(srcColumn, refColumn);

            case ForeignKey.FKEYTYPE_SELF:
                return new SelfForeignKey(srcColumn, refColumn);
        }

        return null;
    }
}
