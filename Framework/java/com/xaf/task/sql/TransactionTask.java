/*
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author ThuA
 * @version 
 * Created on: Jul 26, 2001 2:49:27 PM
 */
package com.xaf.task.sql;

import com.xaf.task.AbstractTask;
import com.xaf.task.TaskInitializeException;
import com.xaf.task.TaskContext;
import com.xaf.task.TaskExecuteException;
import com.xaf.form.DialogContext;
import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueSourceFactory;
import com.xaf.db.DatabaseContext;
import com.xaf.db.DatabaseContextFactory;
import org.w3c.dom.Element;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import java.sql.Connection;

public class TransactionTask extends AbstractTask
{
    static public final int COMMAND_UNKNOWN = 0;
    static public final int COMMAND_BEGIN = 1;
    static public final int COMMAND_END   = 2;

    private SingleValueSource dataSourceValueSource;
    private int command;

    public TransactionTask()
    {
		super();
    }

    /**
     *
     */
    public void reset()
    {
		super.reset();
        command = COMMAND_UNKNOWN;
        dataSourceValueSource = null;
    }

	public SingleValueSource getDataSource() { return dataSourceValueSource; }
	public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public void setCommand(int value)
    {
        command = value;
    }
    public void setCommand(String value)
    {
        if("begin".equals(value))
			setCommand(COMMAND_BEGIN);
        else if ("end".equals(value))
            setCommand(COMMAND_END);
        else
            setCommand(COMMAND_UNKNOWN);
    }
    public int getCommand()
    {
        return command;
    }
    /**
     *
     */
    public void initialize(Element elem) throws TaskInitializeException
    {
		super.initialize(elem);
		setDataSource(elem.getAttribute("data-src"));
        setCommand(elem.getAttribute("command"));
    }

    /**
     * Executes the task representing the <xaf:transaction> tag
     * @param tc TaskContext
     */
    public void execute(TaskContext tc) throws TaskExecuteException
    {
        try
        {
            ServletContext context = tc.getServletContext();
            ServletRequest request = tc.getRequest();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(request, context);
            String dataSourceId = this.getDataSource() != null ?this.getDataSource().getValue(tc) : null;
            dataSourceId = dbContext.translateDataSourceId(tc, dataSourceId);
            int command = this.getCommand();

            if (command == COMMAND_BEGIN)
            {
                // get a connection and bind it to the request so that DMLs after this
                // can use the connection
                Connection conn = dbContext.getConnection(tc, dataSourceId);
                conn.setAutoCommit(false);
                request.setAttribute(dataSourceId, conn);
            }
            else if (command == COMMAND_END)
            {
                Connection conn = (Connection) request.getAttribute(dataSourceId);
                // commit and close the connection
                conn.commit();
                conn.setAutoCommit(true);
                request.removeAttribute(dataSourceId);
            }
            else
            {
                // unknown command
                throw new TaskExecuteException("No appropriate Transaction command provided.");
            }
        }
        catch (Exception e)
        {
            throw new TaskExecuteException(e);
        }

    }
}
