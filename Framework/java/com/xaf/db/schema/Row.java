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
import java.sql.Connection;

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

    public RowData createRowData();

    public DataContext createDataContext(DialogContext dc, boolean fillFieldValues);
    public DataContext createDataContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response);

/*
    public void initializeRowData(RowData rowData);

    public boolean isRowDataValid(RowData rowData);
    public boolean isRowDataValidForInsert(RowData rowData);
    public boolean isRowDataValidForUpdate(RowData rowData);
    public boolean isRowDataValidForRemove(RowData rowData);

    public boolean allowInsert(Connection conn, RowData rowData);
    public void insertCompleted(Connection conn, RowData rowData);

    public boolean allowUpdate(Connection conn, RowData rowData);
    public void updateCompleted(Connection conn, RowData rowData);

    public boolean allowDelete(Connection conn, RowData rowData);
    public void deleteCompleted(Connection conn, RowData rowData);
*/
}
