package com.xaf.sql.taglib;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.task.*;
import com.xaf.task.sql.*;

public class QueryTag extends TagSupport
{
	private StatementTask task = new StatementTask();

	public void release()
	{
		super.release();
		task.reset();
	}

	public void setDebug(String value) { if(value.equals("yes")) task.setFlag(Task.TASKFLAG_DEBUG); }
	public void setName(String value) {	task.setStmtName(value); }
	public void setStmtSource(String value) { task.setStmtSource(value); }
	public void setDataSource(String value) { task.setDataSource(value); }
	public void setReport(String value) { task.setReport(value); }
	public void setSkin(String value) { task.setSkin(value); }
	public void setStore(String value) { task.setStore(value); }
	public void setStoreType(String value) { task.setStoreType(value); }

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		TaskContext tc = new TaskContext((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext.getServletContext());
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
		}
		catch(TaskExecuteException e)
		{
			try{ out.write(e.getDetailedMessage()); } catch(IOException ie) { throw new JspException(ie.getMessage()); }
			return SKIP_PAGE;
		}

		return EVAL_PAGE;
	}
}