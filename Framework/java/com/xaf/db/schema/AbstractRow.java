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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractRow implements Row
{
    private List columnsList = new ArrayList();
    private Map columnsMap = new HashMap();

    public AbstractRow()
    {
    }

    public AbstractRow(List columns)
    {
        for(int i = 0; i < columns.size(); i++)
            addColumn((Column) columns.get(i));
    }

    public AbstractRow(Column[] columns)
    {
        for(int i = 0; i < columns.length; i++)
            addColumn(columns[i]);
    }

    public int getColumnsCount()
    {
        return columnsList.size();
    }

    public void addColumn(Column column)
    {
        column.setIndexInRow(columnsList.size());
        columnsList.add(column);
        columnsMap.put(column.getNameForMapKey(), column);
    }

    public Column[] getColumns()
    {
        return (Column[]) columnsList.toArray(new Column[columnsList.size()]);
    }

    public List getColumnsList()
    {
        return columnsList;
    }

    public Map getColumnsMap()
    {
        return columnsMap;
    }

    public Column getColumn(String name)
    {
        return (Column) columnsMap.get(name);
    }

    public Column getColumn(int index)
    {
        return (Column) columnsList.get(index);
    }

    public RowData createRowData()
    {
        return new BasicRowData(this);
    }

    public DataContext createDataContext(DialogContext dc, boolean fillFieldValues)
    {
        return new DataContext(this, dc, fillFieldValues);
    }

    public DataContext createDataContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
    {
        return new DataContext(this, context, servlet, request, response);
    }

    public void finalizeDefn(Schema schema)
    {
    }
}
