/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:27:54 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.util.*;

abstract public class AbstractTable extends AbstractRow implements Table
{
    private Schema schema;
    private String name;
    private String description;
    private Map childTables;

    public AbstractTable(Schema schema, String name)
    {
        this.schema = schema;
        this.name = name;
        schema.addTable(this);
    }

    abstract public void initializeDefn(Schema schema);
    public void finalizeDefn(Schema schema)
    {
        for(Iterator i = getColumnsList().iterator(); i.hasNext(); )
        {
            Column column = (Column) i.next();
            column.finalizeDefn(schema, this);
        }
    }

    public String getName() { return name; }
    public String getNameForMapKey() { return name.toUpperCase(); }
    public void setName(String value) { name = value; }
    public Row getRow() { return this; }

    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }

    public void registerForeignKey(ForeignKey fKey, boolean isSource)
    {
        // if we are the "referenced" foreign key, then the source is a child of ours
        if(fKey.getType() == ForeignKey.FKEYTYPE_PARENT && ! isSource)
        {
            if(childTables == null) childTables = new HashMap();
            Table childTable = fKey.getSourceColumn().getTable();
            childTables.put(childTable.getNameForMapKey(), childTable);
        }
    }
}
