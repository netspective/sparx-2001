/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: DmlTask.java,v 1.5 2002-08-18 21:08:31 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.task.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xif.db.DatabasePolicy;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogFieldConditionalAction;
import com.netspective.sparx.xaf.form.DialogFieldFactory;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalData;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalApplyFlag;
import com.netspective.sparx.xaf.sql.DmlStatement;
import com.netspective.sparx.xaf.sql.StatementInfo;
import com.netspective.sparx.xaf.sql.DmlStatementConditionalAction;
import com.netspective.sparx.xaf.task.BasicTask;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.task.TaskInitializeException;
import com.netspective.sparx.util.value.CustomSqlValue;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;

public class DmlTask extends BasicTask
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
    private String columns;
    private String autoIncDefn;
    private SingleValueSource autoIncStoreValueSource;
    private SingleValueSource insertCheckValueSource;
    private SingleValueSource updateCheckValueSource;
    private SingleValueSource whereCond;
    private String whereCondBindParams;
    private String dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;
    private List conditionalActions;

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
        whereCondBindParams = null;
        columns = null;
        insertCheckValueSource = null;
        updateCheckValueSource = null;
        dialogContextAttr = DialogContext.DIALOG_CONTEXT_ATTR_NAME;
        autoIncDefn = null;
        autoIncStoreValueSource = null;
    }

    public List getConditionalActions()
    {
        return conditionalActions;
    }

    public void setConditionalActions(List conditionalActions)
    {
        this.conditionalActions = conditionalActions;
    }

    public int getCommand()
    {
        return command;
    }

    public void setCommand(int value)
    {
        command = value;
    }

    public void setCommand(String value)
    {
        if("insert".equals(value))
            setCommand(DMLCMD_INSERT);
        else if("update".equals(value))
            setCommand(DMLCMD_UPDATE);
        else if("insert-or-update".equals(value))
            setCommand(DMLCMD_INSERT_OR_UPDATE);
        else if("delete".equals(value))
            setCommand(DMLCMD_REMOVE);
        else if("remove".equals(value))
            setCommand(DMLCMD_REMOVE);
        else
            setCommand(DMLCMD_UNKNOWN);
    }

    public String getTable()
    {
        return tableName;
    }

    public void setTable(String value)
    {
        tableName = (value != null && value.length() > 0) ? value : null;
    }

    public SingleValueSource getDataSource()
    {
        return dataSourceValueSource;
    }

    public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public String getFields()
    {
        return fields;
    }

    public void setFields(String value)
    {
        fields = (value != null && value.length() > 0) ? value : null;
    }

    public String getAutoInc()
    {
        return autoIncDefn;
    }

    public void setAutoInc(String value)
    {
        autoIncDefn = (value != null && value.length() > 0) ? value : null;
    }

    public SingleValueSource getAutoIncStore()
    {
        return autoIncStoreValueSource;
    }

    public void setAutoIncStore(String value)
    {
        autoIncStoreValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public SingleValueSource getWhereCond()
    {
        return whereCond;
    }

    public void setWhereCond(String value)
    {
        whereCond = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public String getWhereCondBindParams()
    {
        return whereCondBindParams;
    }

    public void setWhereCondBindParams(String value)
    {
        whereCondBindParams = (value != null && value.length() > 0) ? value : null;
    }

    public String getColumns()
    {
        return columns;
    }

    public void setColumns(String value)
    {
        columns = (value != null && value.length() > 0) ? value : null;
    }

    public String getDialogContextAttrName()
    {
        return dialogContextAttr;
    }

    public void setDialogContextAttrName(String value)
    {
        dialogContextAttr = (value != null && value.length() > 0) ? value : null;
    }

    public SingleValueSource getInsertCheckValueSource()
    {
        return insertCheckValueSource;
    }

    public void setInsertCheckValueSource(SingleValueSource value)
    {
        insertCheckValueSource = value;
    }

    public void setInsertCheckValueSource(String value)
    {
        insertCheckValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public SingleValueSource getUpdateCheckValueSource()
    {
        return updateCheckValueSource;
    }

    public void setUpdateCheckValueSource(SingleValueSource value)
    {
        updateCheckValueSource = value;
    }

    public void setUpdateCheckValueSource(String value)
    {
        updateCheckValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public void initialize(Element elem) throws TaskInitializeException
    {
        super.initialize(elem);

        setTable(elem.getAttribute("table"));
        setAutoInc(elem.getAttribute("auto-inc"));
        setAutoIncStore(elem.getAttribute("auto-inc-store"));
        setCommand(elem.getAttribute("command"));
        setDataSource(elem.getAttribute("data-src"));
        setWhereCond(elem.getAttribute("where"));
        setWhereCondBindParams(elem.getAttribute("where-bind"));
        setFields(elem.getAttribute("fields"));
        setColumns(elem.getAttribute("columns"));
        setDialogContextAttrName(elem.getAttribute("dialog-context-attr"));
        setInsertCheckValueSource(elem.getAttribute("insert-check"));
        setUpdateCheckValueSource(elem.getAttribute("update-check"));

        // handle conditionals if they exist
        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String childName = node.getNodeName();
            if (childName.equals("conditional"))
            {
                importConditionalFromXml((Element) node);
            }
        }
    }
    /**
     * Attributes allowed:
     *  1. action = "execute"
     *  2. has-value
     *  3. has-permission
     *  4. is-true
     */
    public void importConditionalFromXml(Element elem) throws TaskInitializeException
    {
        String action = elem.getAttribute("action");
        if(action == null || action.length() == 0)
        {
            throw new TaskInitializeException("In DmlTag, no 'action' specified for conditional.");
        }
        DmlStatementConditionalAction actionInst =  new  DmlStatementConditionalAction();
        if (actionInst.importFromXml(elem))
        {
            addConditionalAction(actionInst);
        }
    }

    /**
     * Add the condition to the list of conditions for the DML statement
     *
     * @param      DmlStatementConditionalAction
     */
    public void addConditionalAction(DmlStatementConditionalAction action)
    {
        if (this.conditionalActions == null)
            this.conditionalActions = new ArrayList();

        this.conditionalActions.add(action);
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
            throw new RuntimeException("dml tag requires a valid DialogContext in '" + dialogContextAttr + "'");

        if(fields.equals("*"))
        {
            for(Iterator i = dc.getFieldStates().values().iterator(); i.hasNext();)
            {
                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) i.next();
                columnNames.add(state.field.getSimpleName());
                columnValues.add((state.value == null || state.value.length() == 0)? null : state.field.getValueForSqlBindParam(state.value));
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
                    columnNames.add(fieldName.substring(aliasLoc + 1));
                    fieldName = fieldName.substring(0, aliasLoc);
                }
                else
                    columnNames.add(fieldName);

                DialogContext.DialogFieldState state = (DialogContext.DialogFieldState) dialogFieldStates.get(fieldName);
                if(state == null)
                    throw new RuntimeException("In dml tag, field '" + fieldName + "' does not exist in DialogContext");
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
                String vsName = columnInfo.substring(valueLoc + 1);
                SingleValueSource vs = ValueSourceFactory.getSingleValueSource(vsName);
                if(vs == null)
                    throw new TaskExecuteException("In DmlTag, the 'columns' attribute item '" + columnInfo + "' has an invalid ValueSource '" + vsName + "'.");
                Object colValue = vs.getObjectValue(tc);
                if(colName.equals("col-value-map"))
                {
                    Map colNameValue = (Map) colValue;
                    for(Iterator i = colNameValue.entrySet().iterator(); i.hasNext();)
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
                    columnValues.add(new DmlStatement.CustomSql(colValue.toString()));
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

    public int bindWhereCondParams(TaskContext tc, PreparedStatement stmt, int startColumnNum) throws SQLException, TaskExecuteException
    {
        if(whereCondBindParams == null)
            return startColumnNum;

        int activeColumnNum = startColumnNum;
        StringTokenizer st = new StringTokenizer(whereCondBindParams, ",");
        while(st.hasMoreTokens())
        {
            String vsName = st.nextToken();
            SingleValueSource vs = ValueSourceFactory.getSingleValueSource(vsName);
            if(vs == null)
                throw new TaskExecuteException("In DmlTag, the where condition bind params has an invalid ValueSource '" + vsName + "'.");

            Object value = vs.getObjectValue(tc);
            stmt.setObject(activeColumnNum, value);
            activeColumnNum++;
        }
        return activeColumnNum;
    }

    public void execute(TaskContext tc) throws TaskExecuteException
    {
        // check all the conditionals
        if (this.conditionalActions != null)
        {
            int conditionalCount = this.conditionalActions.size();
            for (int i=0; i < conditionalCount; i++)
            {
                DmlStatementConditionalAction action = (DmlStatementConditionalAction) conditionalActions.get(i);
                if (!action.checkCondtionals(tc))
                    return;
            }
        }

        tc.registerTaskExecutionBegin(this);

        ServletContext context = tc.getServletContext();
        DatabaseContext dbContext = DatabaseContextFactory.getContext(tc.getRequest(), context);
        String dataSourceId = this.getDataSource() != null ? this.getDataSource().getValue(tc) : null;
        Connection conn = null;
        boolean connIsShared = false;  // conn being shared within a transaction? if so, we don't want to close when we're done
        PreparedStatement stmt = null;

        DatabasePolicy databasePolicy = null;
        String autoIncSeqOrTableName = tableName;
        String autoIncColName = autoIncDefn;
        Object autoIncColValue = null;

        DmlStatement dml = null;
        List columnNames = new ArrayList();
        List columnValues = new ArrayList();

        try
        {
            populateFieldsDmlItems(tc, columnNames, columnValues);
            populateColumnsDmlItems(tc, columnNames, columnValues);

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
                        tc.addResultMessage("checking insert-or-update -- insertchk '" + insertCheckValueSource.getId() + "' = " + value + "<p>");
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
                        tc.addResultMessage("checking insert-or-update -- updatechk '" + updateCheckValueSource.getId() + "' = " + value + "<p>");
                    }

                }
                else
                {
                    throw new TaskExecuteException("Either insert-check or update-check value source must be provided.");
                }
            }

            conn = dbContext.getSharedConnection(tc, dataSourceId);
            if(conn == null)
                conn = dbContext.getConnection(tc, dataSourceId);
            else
                connIsShared = true;

            switch(tempCmd)
            {
                case DMLCMD_INSERT:
                    if(autoIncDefn != null)
                    {
                        databasePolicy = DatabaseContextFactory.getDatabasePolicy(conn);
                        int tokenSepPos = autoIncDefn.indexOf(",");
                        if(tokenSepPos != -1)
                        {
                            autoIncColName = autoIncDefn.substring(0, tokenSepPos);
                            autoIncSeqOrTableName = autoIncDefn.substring(tokenSepPos + 1);
                        }
                        autoIncColValue = databasePolicy.handleAutoIncPreDmlExecute(conn, tc, autoIncSeqOrTableName, autoIncColName, columnNames, columnValues);
                    }

                    dml = new DmlStatement(tableName, columnNames, columnValues);
                    break;

                case DMLCMD_INSERT_OR_UPDATE:
                    throw new TaskExecuteException("Should never get here!.");

                case DMLCMD_UPDATE:
                    if(whereCond == null)
                        throw new TaskExecuteException("No 'where' attribute provided.");
                    dml = new DmlStatement(tableName, columnNames, columnValues, whereCond.getValue(tc));
                    break;

                case DMLCMD_REMOVE:
                    if(whereCond == null)
                        throw new TaskExecuteException("No 'where' attribute provided.");
                    dml = new DmlStatement(tableName, whereCond.getValue(tc));
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

            stmt = conn.prepareStatement(dml.getSql());

            int columnNum = 1;
            boolean[] bindValues = dml.getBindValues();
            if(bindValues != null)
            {
                for(int c = 0; c < bindValues.length; c++)
                {
                    if(bindValues[c])
                    {
                        stmt.setObject(columnNum, columnValues.get(c));
                        columnNum++;
                    }
                }
            }

            bindWhereCondParams(tc, stmt, columnNum);
            stmt.execute();

            if(tempCmd == DMLCMD_INSERT && autoIncDefn != null)
            {
                autoIncColValue = databasePolicy.handleAutoIncPostDmlExecute(conn, tc, autoIncSeqOrTableName, autoIncColName, autoIncColValue);
                if(autoIncStoreValueSource != null)
                    autoIncStoreValueSource.setValue(tc, autoIncColValue);
            }
        }
        catch(SQLException e)
        {
            throw new TaskExecuteException(e, dml.toString());
        }
        catch(NamingException e)
        {
            throw new TaskExecuteException(e);
        }
        finally
        {
            try
            {
                if(stmt != null) stmt.close();
                if(conn != null && ! connIsShared)
                {
                        conn.close();
                        AppServerLogger.getLogger(LogManager.DEBUG_SQL).debug(((HttpServletRequest) tc.getRequest()).getServletPath() + " closing connection: " + conn);
                }
            }
            catch(SQLException e)
            {
                throw new TaskExecuteException(e);
            }
        }

        tc.registerTaskExecutionEnd(this);
    }
}