/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:02:48 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ServletValueContext;
import com.xaf.form.DialogContext;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Map;
import java.util.Iterator;

public class DataContext extends ServletValueContext
{
    public static int ROWDATAFLAG_VALUEISSINGLEVALUESRC = 1;
    public static int ROWDATAFLAG_VALUEISSQLEXPR = ROWDATAFLAG_VALUEISSINGLEVALUESRC * 2;

    private Row row;
    private int[] flags;
    private Object[] data;
    private DialogContext dialogContext;

    public DataContext(Row row, DialogContext dc, boolean fillFieldValues)
	{
        this.row = row;
        this.dialogContext = dc;
        initialize(dc.getServletContext(), dc.getServlet(), dc.getRequest(), dc.getResponse());
        if(fillFieldValues) setValuesFromFields();
	}

    public DataContext(Row row, ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
	{
        this.row = row;
        initialize(context, servlet, request, response);
	}

    public void initialize(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
    {
        super.initialize(context, servlet, request, response);
        data = new Object[row.getColumnsCount()];
        flags = new int[row.getColumnsCount()];
    }

    public Row getRow() { return row; }

    public boolean hasValue(Column column) { return data[column.getIndexInRow()] != null; }
    public boolean valueIsSqlExpr(Column column) { return (flags[column.getIndexInRow()] & ROWDATAFLAG_VALUEISSQLEXPR) != 0; }

    public Object getValue(Column column)
    {
        int colIndex = column.getIndexInRow();
        Object dataItem = data[colIndex];
        if((flags[colIndex] & ROWDATAFLAG_VALUEISSINGLEVALUESRC) != 0)
            return dataItem != null ? (((SingleValueSource) dataItem).getObjectValue(this)) : null;
        else
            return dataItem;
    }

    public void setValue(Column column, Object value)
    {
        int colIndex = column.getIndexInRow();
        data[colIndex] = value;
        if(value instanceof SingleValueSource)
            flags[colIndex] |= ROWDATAFLAG_VALUEISSINGLEVALUESRC;
    }

    public void setSqlExprValue(Column column, Object value)
    {
        setValue(column, value);
        flags[column.getIndexInRow()] |= ROWDATAFLAG_VALUEISSQLEXPR;
    }

    public DialogContext findDialogContext()
    {
        if(dialogContext != null) return dialogContext;
        return (DialogContext) getRequest().getAttribute(DialogContext.DIALOG_CONTEXT_ATTR_NAME);
    }

    public void setValuesFromFields()
    {
        DialogContext dc = findDialogContext();
        if(dc == null)
            return;

        Map fieldStates = dc.getFieldStates();
        for(Iterator i = fieldStates.values().iterator(); i.hasNext(); )
        {
            DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) i.next();
            String fieldName = state.field.getQualifiedName();
            if(fieldName != null)
            {
                Column column = row.getColumn(fieldName);
                if(column != null)
                    column.setValueObject(this, state.value);
            }

        }
    }

    public void setValuesFromFields(String[] fields)
    {
        DialogContext dc = findDialogContext();
        if(dc == null)
            return;

        for(int i = 0; i < fields.length; i++)
        {
            String fieldName = fields[i];
            Column column = row.getColumn(fieldName);
            if(column != null)
                column.setValueObject(this, dc.getValue(fieldName));
        }
    }

    public void setValuesFromFields(String[] fieldNames, String[] columnNames)
    {
        DialogContext dc = findDialogContext();
        if(dc == null)
            return;

        for(int i = 0; i < fieldNames.length; i++)
        {
            Column column = row.getColumn(columnNames[i]);
            if(column != null)
                column.setValueObject(this, dc.getValue(fieldNames[i]));
        }
    }
}
