package com.xaf.sql.taglib;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.report.*;
import com.xaf.sql.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class DmlTag extends TagSupport
{
    private String command;
	private String tableName;
	private String dataSourceId;
    private String fields;
    private String transaction;
    private String whereCond;
    private String debug;
    private String columns;
    private String dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;

	public void release()
	{
		super.release();
        command = null;
		tableName = null;
		dataSourceId = null;
		fields = null;
        transaction = null;
        whereCond = null;
        debug = null;
        columns = null;
        dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;
	}

	public String getCommand() { return command; }
	public void setCommand(String value) { command = value; }

	public String getTable() { return tableName; }
	public void setTable(String value) { tableName = value; }

	public String getDataSource() { return dataSourceId; }
	public void setDataSource(String value) { dataSourceId = value; }

	public String getFields() { return fields; }
	public void setFields(String value) { fields = value; }

   	public String getTransaction() { return transaction; }
	public void setTransaction(String value) { transaction = value; }

   	public String getWhere() { return whereCond; }
	public void setWhere(String value) { whereCond = value; }

   	public String getColumns() { return columns; }
	public void setColumns(String value) { columns = value; }

   	public String getDebug() { return debug; }
	public void setDebug(String value) { debug = value; }

    public String getContext() { return dialogContextAttr; }
    public void setContext(String value) { dialogContextAttr = value; }

    public void populateFieldsDmlItems(List columnNames, List columnValues)
    {
        if(fields == null)
            return;

        ServletRequest request = pageContext.getRequest();
        if(dialogContextAttr == null)
            throw new RuntimeException("dml tag requires context attribute (for DialogContext)");

        DialogContext dc = (DialogContext) request.getAttribute(dialogContextAttr);
        if(dc == null)
            throw new RuntimeException("dml tag requires a valid DialogContext in '"+dialogContextAttr+"'");

        if(fields.equals("*"))
        {
            for(Iterator i = dc.values().iterator(); i.hasNext(); )
            {
                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) i.next();
                columnNames.add(state.field.getSimpleName());
                columnValues.add(state.value == null ? null : state.field.getValueForSqlBindParam(state.value));
            }
        }
        else
        {
            StringTokenizer st = new StringTokenizer(fields, ",");
            while(st.hasMoreTokens())
            {
                String fieldName = st.nextToken();
                int aliasLoc = fieldName.indexOf("=");
                if(aliasLoc != -1)
                {
                    // format here is field_name=column_name
                    columnNames.add(fieldName.substring(aliasLoc+1));
                    fieldName = fieldName.substring(0, aliasLoc);
                }
                else
                    columnNames.add(fieldName);

                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) dc.get(fieldName);
                if(state == null)
                    throw new RuntimeException("In dml tag, field '"+fieldName+"' does not exist in DialogContext");
                columnValues.add(state.value == null ? null : state.field.getValueForSqlBindParam(state.value));
            }
        }
    }

    public void populateColumnsDmlItems(List columnNames, List columnValues) throws JspException
    {
        if(columns == null)
            return;

        StringTokenizer st = new StringTokenizer(columns, ",");
		ValueContext valueContext = new ServletValueContext((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext.getServletContext());
        while(st.hasMoreTokens())
        {
            String columnInfo = st.nextToken();
            int valueLoc = columnInfo.indexOf("=");
            if(valueLoc != -1)
            {
                String colName = columnInfo.substring(0, valueLoc);
                String vsName = columnInfo.substring(valueLoc+1);
                SingleValueSource vs = ValueSourceFactory.getSingleValueSource(vsName);
                if(vs == null)
                    throw new JspException("In DmlTag, the 'columns' attribute item '"+columnInfo+"' has an invalid ValueSource '"+vsName+"'.");
                Object colValue = vs.getObjectValue(valueContext);
                if(colName.equals("col-value-map"))
                {
                    Map colNameValue = (Map) colValue;
                    for (Iterator i=colNameValue.entrySet().iterator(); i.hasNext();)
                    {
                        Map.Entry entry = (Map.Entry) i.next();
                        columnNames.add(entry.getKey());
                        columnValues.add(entry.getValue());
                    }

                }
                else
                {
                    columnNames.add(colName);
                    columnValues.add(colValue);
                }
            }
            else
            {
                throw new JspException("In DmlTag, the 'columns' attribute values must look like colname=colvalue.");
            }
        }
    }

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
        Generator.DmlStatement dml = null;
        List columnNames = new ArrayList();
        List columnValues = new ArrayList();
        Object[] columnsAndValues = null;

		JspWriter out = pageContext.getOut();
		try
		{
            populateFieldsDmlItems(columnNames, columnValues);
            populateColumnsDmlItems(columnNames, columnValues);

            columnsAndValues = new Object[columnNames.size() * 2];
            for(int i = 0; i < columnNames.size(); i++)
            {
                int columnIndex = i * 2;
                columnsAndValues[columnIndex] = columnNames.get(i);
                columnsAndValues[columnIndex+1] = columnValues.get(i);
            }

            if("insert".equals(command))
            {
                dml = Generator.createInsertStmt(tableName, columnsAndValues);
            }
            else if("update".equals(command))
            {
                if(whereCond == null)
                    throw new JspException("No 'where' attribute provided.");
                dml = Generator.createUpdateStmt(tableName, columnsAndValues, whereCond, null);
            }

            if("yes".equals(debug))
            {
                out.print("<p><pre>");
                out.print(dml.toString());
                out.print("</pre>");
                return EVAL_PAGE;
            }
		}
        catch(Exception e)
        {
            throw new JspException(e.toString());
        }

        try
        {
            ServletContext context = pageContext.getServletContext();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(pageContext.getRequest(), context);
            Connection conn = dataSourceId == null ? dbContext.getConnection() : dbContext.getConnection(dataSourceId);
            PreparedStatement stmt = conn.prepareStatement(dml.sql);

            //out.write(dml.sql);
            //out.flush();
           // bind all of the parameters
            int columnNum = 1;
            long bindColFlags = dml.bindColFlags;
            for(int c = 0; c < columnsAndValues.length; c++)
            {
                long valueIndexFlag = (long) java.lang.Math.pow(2.0, c);
                if((bindColFlags & valueIndexFlag) != 0)
                {
                    // ???NO??? we do a c/2 because of name/value pairs in columnsAndValues
                    // we then add +1 because SQL bind parameters start at 1, not zero
                    //int columnNum = (c/2)+1;
                    //out.write("<li>Column " + columnNum + "/" + c + ": bind " + columnsAndValues[c].toString() + " " + columnsAndValues[c].getClass().getName());
                    //out.flush();
                    stmt.setObject(columnNum, columnsAndValues[c]);
                    columnNum++;
                }
            }

            stmt.execute();
        }
        catch(SQLException e)
        {
            try { out.write(e.toString()); out.flush(); } catch(Exception e2) { }
        }
        catch(NamingException e)
        {
            try { out.write(e.toString()); out.flush(); } catch(Exception e2) { }
        }
        //catch(IOException e)
        //{
        //    try { out.write(e.toString()); out.flush(); } catch(Exception e2) { }
        //}

		return EVAL_PAGE;
	}

}