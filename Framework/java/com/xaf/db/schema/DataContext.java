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
    private RowData rowData;
    private DialogContext dialogContext;

    public DataContext(Row row, DialogContext dc, boolean fillFieldValues)
	{
        rowData = row.createRowData();
        dialogContext = dc;
        initialize(dc.getServletContext(), dc.getServlet(), dc.getRequest(), dc.getResponse());
        if(fillFieldValues) setValuesFromFields();
	}

    public DataContext(Row row, ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
	{
        rowData = row.createRowData();
        initialize(context, servlet, request, response);
	}

    public RowData getRowData() { return rowData; }

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
        Row row = rowData.getRow();
        for(Iterator i = fieldStates.values().iterator(); i.hasNext(); )
        {
            DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) i.next();
            String fieldName = state.field.getQualifiedName();
            if(fieldName != null)
            {
                Column column = row.getColumn(fieldName);
                if(column != null)
                    column.setValueObject(rowData, state.value);
            }

        }
    }

    public void setValuesFromFields(String[] fields)
    {
        DialogContext dc = findDialogContext();
        if(dc == null)
            return;

        Row row = rowData.getRow();
        for(int i = 0; i < fields.length; i++)
        {
            String fieldName = fields[i];
            Column column = row.getColumn(fieldName);
            if(column != null)
                column.setValueObject(rowData, dc.getValue(fieldName));
        }
    }

    public void setValuesFromFields(String[] fieldNames, String[] columnNames)
    {
        DialogContext dc = findDialogContext();
        if(dc == null)
            return;

        Row row = rowData.getRow();
        for(int i = 0; i < fieldNames.length; i++)
        {
            Column column = row.getColumn(columnNames[i]);
            if(column != null)
                column.setValueObject(rowData, dc.getValue(fieldNames[i]));
        }
    }
}
