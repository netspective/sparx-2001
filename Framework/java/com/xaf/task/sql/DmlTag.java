package com.xaf.task.sql;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.task.*;

public class DmlTag extends TagSupport
{
	private DmlTask task = new DmlTask();

	public void release()
	{
		super.release();
		task.reset();
	}

	public void setDebug(String value) { if(value.equals("yes")) task.setFlag(Task.TASKFLAG_DEBUG); }
    public void setDataCmd(String value) { task.setDataCmdCondition(value); }
	public void setCommand(String value) { task.setCommand(value); }
	public void setTable(String value) { task.setTable(value); }
    public void setAutoInc(String value) { task.setAutoInc(value); }
    public void setAutoIncStore(String value) { task.setAutoIncStore(value); }
	public void setDataSource(String value) { task.setDataSource(value); }
	public void setFields(String value) { task.setFields(value); }
	public void setWhere(String value) { task.setWhereCond(value); }
    public void setWhereBind(String value) { task.setWhereCondBindParams(value); }
	public void setColumns(String value) { task.setColumns(value); }
    public void setContext(String value) { task.setDialogContextAttrName(value); }
    public void setInsertchk(String value) { task.setInsertCheckValueSource(value); }
    public void setUpdatechk(String value) { task.setUpdateCheckValueSource(value); }

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		TaskContext tc = new TaskContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
		try
		{
			task.execute(tc);
			if(tc.hasError())
				out.write(tc.getErrorMessage());
			else if(tc.hasResultMessage())
				out.write(tc.getResultMessage());
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
		catch(TaskExecuteException e)
		{
			try{ out.write(e.getDetailedMessage()); } catch(IOException ie) { throw new JspException(ie.getMessage()); }
			return SKIP_PAGE;
		}

		return EVAL_PAGE;
	}

}