package com.xaf.task.sql;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;

import org.w3c.dom.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.report.*;
import com.xaf.sql.*;
import com.xaf.skin.*;
import com.xaf.task.*;
import com.xaf.value.*;

public class DmlTask extends AbstractTask
{
	public final int DMLCMD_UNKNOWN = 0;
	public final int DMLCMD_INSERT = 1;
	public final int DMLCMD_UPDATE = 2;
	public final int DMLCMD_REMOVE = 3;

    private int command;
	private String tableName;
	private String dataSourceId;
    private String fields;
    private String transaction;
    private String whereCond;
    private String columns;
    private String dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;

    public DmlTask()
    {
		super();
    }

    public void reset()
    {
		super.reset();
        command = DMLCMD_UNKNOWN;
		tableName = null;
		dataSourceId = null;
		fields = null;
        transaction = null;
        whereCond = null;
        columns = null;
        dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;
    }

	public int getCommand() { return command; }
	public void setCommand(int value) { command = value; }
	public void setCommand(String value)
	{
        if("insert".equals(value))
			setCommand(DMLCMD_INSERT);
        else if("update".equals(value))
			setCommand(DMLCMD_UPDATE);
        else if("remove".equals(value))
			setCommand(DMLCMD_REMOVE);
		else
			setCommand(DMLCMD_UNKNOWN);
	}

	public String getTable() { return tableName; }
	public void setTable(String value) { tableName = (value != null && value.length() > 0) ? value : null; }

	public String getDataSource() { return dataSourceId; }
	public void setDataSource(String value) { dataSourceId = (value != null && value.length() > 0) ? value : null; }

	public String getFields() { return fields; }
	public void setFields(String value) { fields = (value != null && value.length() > 0) ? value : null; }

   	public String getTransaction() { return transaction; }
	public void setTransaction(String value) { transaction = (value != null && value.length() > 0) ? value : null; }

   	public String getWhere() { return whereCond; }
	public void setWhere(String value) { whereCond = (value != null && value.length() > 0) ? value : null; }

   	public String getColumns() { return columns; }
	public void setColumns(String value) { columns = (value != null && value.length() > 0) ? value : null; }

    public String getDialogContextAttrName() { return dialogContextAttr; }
    public void setDialogContextAttrName(String value) { dialogContextAttr = (value != null && value.length() > 0) ? value : null; }

    public void initialize(Element elem) throws TaskInitializeException
    {
		super.initialize(elem);

		setTable(elem.getAttribute("table"));
		setCommand(elem.getAttribute("command"));
		setDataSource(elem.getAttribute("data-src"));
		setTransaction(elem.getAttribute("transaction"));
		setWhere(elem.getAttribute("where"));
		setFields(elem.getAttribute("fields"));
		setColumns(elem.getAttribute("columns"));
		setDialogContextAttrName(elem.getAttribute("dialog-context-attr"));
    }

    public void populateFieldsDmlItems(TaskContext tc, List columnNames, List columnValues)
    {
        if(fields == null)
            return;

        ServletRequest request = tc.getRequest();
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

    public void populateColumnsDmlItems(TaskContext tc, List columnNames, List columnValues) throws TaskExecuteException
    {
        if(columns == null)
            return;

        StringTokenizer st = new StringTokenizer(columns, ",");
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
                    throw new TaskExecuteException("In DmlTag, the 'columns' attribute item '"+columnInfo+"' has an invalid ValueSource '"+vsName+"'.");
                Object colValue = vs.getObjectValue(tc);
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
                throw new TaskExecuteException("In DmlTag, the 'columns' attribute values must look like colname=colvalue.");
            }
        }
    }

    public void execute(TaskContext tc) throws TaskExecuteException
    {
        Generator.DmlStatement dml = null;
        List columnNames = new ArrayList();
        List columnValues = new ArrayList();
        Object[] columnsAndValues = null;

		try
		{
            populateFieldsDmlItems(tc, columnNames, columnValues);
            populateColumnsDmlItems(tc, columnNames, columnValues);

            columnsAndValues = new Object[columnNames.size() * 2];
            for(int i = 0; i < columnNames.size(); i++)
            {
                int columnIndex = i * 2;
                columnsAndValues[columnIndex] = columnNames.get(i);
                columnsAndValues[columnIndex+1] = columnValues.get(i);
            }

			switch(command)
			{
				case DMLCMD_INSERT:
	                dml = Generator.createInsertStmt(tableName, columnsAndValues);
					break;

				case DMLCMD_UPDATE:
					if(whereCond == null)
						throw new TaskExecuteException("No 'where' attribute provided.");
					dml = Generator.createUpdateStmt(tableName, columnsAndValues, whereCond, null);
					break;

				case DMLCMD_REMOVE:
					throw new TaskExecuteException("DML Command 'remove' not implemented yet.");

				case DMLCMD_UNKNOWN:
					throw new TaskExecuteException("No appropriate DML command provided.");
			}

            if(flagIsSet(TASKFLAG_DEBUG))
            {
                tc.addResultMessage("<p><pre>");
                tc.addResultMessage(dml.toString());
                tc.addResultMessage("</pre>");
                return;
            }
		}
        catch(Exception e)
        {
            throw new TaskExecuteException(e);
        }

        try
        {
            ServletContext context = tc.getServletContext();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(tc.getRequest(), context);
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
			throw new TaskExecuteException(e);
        }
        catch(NamingException e)
        {
			throw new TaskExecuteException(e);
        }
	}
}