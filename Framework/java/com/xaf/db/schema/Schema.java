/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 2:21:51 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.util.Map;
import java.util.List;

public interface Schema
{
    public String getName();
    public void setName(String value);

    public void initializeDefn();
    public void finalizeDefn();

    public void addTable(Table table);
    public Table getTable(String name);
    public Column getColumn(String tableName, String tableColumn);

    public List getTablesList();
    public Map getTablesMap();

    public ForeignKey getForeignKey(short type, String ref);
}
