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
	static public final int DMLCMD_UNKNOWN = 0;
	static public final int DMLCMD_INSERT = 1;
	static public final int DMLCMD_UPDATE = 2;
	static public final int DMLCMD_REMOVE = 3;
	static public final int DMLCMD_INSERT_OR_UPDATE = 4;

    private int command;
	private String tableName;
	private SingleValueSource dataSourceValueSource;
    private String fields;
	private SingleValueSource insertCheckValueSource;
	private SingleValueSource updateCheckValueSource;
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
		dataSourceValueSource = null;
		fields = null;
        whereCond = null;
        columns = null;
		insertCheckValueSource = null;
		updateCheckValueSource = null;
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
		else if("insert-or-update".equals(value))
			setCommand(DMLCMD_INSERT_OR_UPDATE);
        else if("remove".equals(value))
			setCommand(DMLCMD_REMOVE);
		else
			setCommand(DMLCMD_UNKNOWN);
	}

	public String getTable() { return tableName; }
	public void setTable(String value) { tableName = (value != null && value.length() > 0) ? value : null; }

	public SingleValueSource getDataSource() { return dataSourceValueSource; }
	public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public String getFields() { return fields; }
	public void setFields(String value) { fields = (value != null && value.length() > 0) ? value : null; }

   	public String getWhere() { return whereCond; }
	public void setWhere(String value) { whereCond = (value != null && value.length() > 0) ? value : null; }

   	public String getColumns() { return columns; }
	public void setColumns(String value) { columns = (value != null && value.length() > 0) ? value : null; }

    public String getDialogContextAttrName() { return dialogContextAttr; }
    public void setDialogContextAttrName(String value) { dialogContextAttr = (value != null && value.length() > 0) ? value : null; }

	public SingleValueSource getInsertCheckValueSource() { return insertCheckValueSource; }
	public void setInsertCheckValueSource(SingleValueSource value) { insertCheckValueSource = value; }
	public void setInsertCheckValueSource(String value) { insertCheckValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null; }

	public SingleValueSource getUpdateCheckValueSource() { return updateCheckValueSource; }
	public void setUpdateCheckValueSource(SingleValueSource value) { updateCheckValueSource = value; }
	public void setUpdateCheckValueSource(String value) { updateCheckValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null; }

    public void initialize(Element elem) throws TaskInitializeException
    {
		super.initialize(elem);

		setTable(elem.getAttribute("table"));
		setCommand(elem.getAttribute("command"));
		setDataSource(elem.getAttribute("data-src"));
		setWhere(elem.getAttribute("where"));
		setFields(elem.getAttribute("fields"));
		setColumns(elem.getAttribute("columns"));
		setDialogContextAttrName(elem.getAttribute("dialog-context-attr"));
		setInsertCheckValueSource(elem.getAttribute("insert-check"));
		setUpdateCheckValueSource(elem.getAttribute("update-check"));
    }

    public void populateFieldsDmlItems(TaskContext tc, List columnNames, List columnValues)
    {
        if(fields == null)
            return;

        ServletRequest request = tc.getRequest();
        if(dialogContextAttr == null)
			dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;

        DialogContext dc = (DialogContext) request.getAttribute(dialogContextAttr);
        if(dc == null)
            throw new RuntimeException("dml tag requires a valid DialogContext in '"+dialogContextAttr+"'");

        if(fields.equals("*"))
        {
            for(Iterator i = dc.getFieldStates().values().iterator(); i.hasNext(); )
            {
                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) i.next();
                columnNames.add(state.field.getSimpleName());
                columnValues.add((state.value == null  || state.value.length() == 0)? null : state.field.getValueForSqlBindParam(state.value));
            }
        }
        else
        {
            Map dialogFieldStates = dc.getFieldStates();

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

                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) dialogFieldStates.get(fieldName);
                if(state == null)
                    throw new RuntimeException("In dml tag, field '"+fieldName+"' does not exist in DialogContext");
                columnValues.add((state.value == null || state.value.length() == 0) ? null : state.field.getValueForSqlBindParam(state.value));
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
				else if(vs instanceof CustomSqlValue)
				{
					/* Generator.CustomSql is a special-purpose class used to signify
					   to the Sql Generator that the sql should be placed as-is (no changes);
					   very useful for things like sequences
					*/
					columnNames.add(colName);
					columnValues.add(new Generator.CustomSql(colValue.toString()));
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

			int tempCmd = command;
			if(command == DMLCMD_INSERT_OR_UPDATE)
			{
				/**
				 * In this case we will see if a user has provided us a single-value source
				 * for checking inserts or updates. If a value source is provided for inserts
				 * that means that we'll be looking for a value to be returned; if the value
				 * is null or zero, it means that the record should be updated. Non-null and
				 * anything other than zero would mean it should be inserted.
				 */
				if(insertCheckValueSource != null)
				{
					Object value = insertCheckValueSource.getValue(tc);
					if(value == null)
						tempCmd = DMLCMD_UPDATE;
					else
					{
						int intValue = Integer.parseInt(value.toString());
						if(intValue == 0)
							tempCmd = DMLCMD_UPDATE;
						else
							tempCmd = DMLCMD_INSERT;
					}
					if(flagIsSet(TASKFLAG_DEBUG))
					{
						tc.addResultMessage("<p><pre>");
						tc.addResultMessage("checking insert-or-update -- insertchk '"+ insertCheckValueSource.getId() +"' = "+ value + "<p>");
					}
				}
				else if(updateCheckValueSource != null)
				{
					Object value = updateCheckValueSource.getValue(tc);
					if(value == null)
						tempCmd = DMLCMD_INSERT;
					else
					{
						int intValue = Integer.parseInt(value.toString());
						if(intValue == 0)
							tempCmd = DMLCMD_INSERT;
						else
							tempCmd = DMLCMD_UPDATE;
					}

					if(flagIsSet(TASKFLAG_DEBUG))
					{
						tc.addResultMessage("<p><pre>");
						tc.addResultMessage("checking insert-or-update -- updatechk '"+ updateCheckValueSource.getId() +"' = "+ value + "<p>");
					}

				}
				else
				{
					throw new TaskExecuteException("Either insert-check or update-check value source must be provided.");
				}
			}

			switch(tempCmd)
			{
				case DMLCMD_INSERT:
	                dml = Generator.createInsertStmt(tableName, columnsAndValues);
					break;

				case DMLCMD_INSERT_OR_UPDATE:
					throw new TaskExecuteException("Should never get here!.");

				case DMLCMD_UPDATE:
					if(whereCond == null)
						throw new TaskExecuteException("No 'where' attribute provided.");
					dml = Generator.createUpdateStmt(tableName, columnsAndValues, whereCond, null);
					break;

				case DMLCMD_REMOVE:
					dml = Generator.createDeleteStmt(tableName, whereCond, null);
					break;
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
            String dataSourceId = this.getDataSource() != null ?this.getDataSource().getValue(tc) : null;
            Connection conn = dbContext.getConnection(tc, dataSourceId);
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