/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:39 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.form.DialogContext;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.List;
import java.util.Map;

public interface Row
{
    public int getColumnsCount();
    public void addColumn(Column column);
    public Column[] getColumns();
    public List getColumnsList();
    public Map getColumnsMap();

    public Column getColumn(String name);
    public Column getColumn(int index);

    public void finalizeDefn(Schema schema);
    public DataContext createDataContext(DialogContext dc, boolean fillFieldValues);
    public DataContext createDataContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response);
}
