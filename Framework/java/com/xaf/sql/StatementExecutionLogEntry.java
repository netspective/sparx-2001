package com.xaf.sql;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.value.*;

public class StatementExecutionLogEntry
{
	private boolean successful;
	private String source;
	private String statementName;
	private Date initDate;
	private Date getConnStartDate;
	private Date getConnEndDate;
	private Date bindParamsStartDate;
	private Date bindParamsEndDate;
	private Date execSqlStartDate;
	private Date execSqlEndDate;

    public StatementExecutionLogEntry(ValueContext vc, StatementInfo si)
    {
		ServletRequest req = vc.getRequest();
		if(req instanceof HttpServletRequest)
			source = ((HttpServletRequest) req).getRequestURI();

		statementName = si.getId();
		initDate = new Date();
    }

	public String getSource()
	{
		return source;
	}

	/**
	 * Success is defined when trackExecSql(false) is called.
	 */
	public boolean wasSuccessful()
	{
		return successful;
	}

	public void registerGetConnectionBegin()
	{
	    getConnStartDate = new Date();
	}

	public void registerGetConnectionEnd(java.sql.Connection conn)
	{
		getConnEndDate = new Date();
	}

	public void registerBindParamsBegin()
	{
	    bindParamsStartDate = new Date();
	}

	public void registerBindParamsEnd()
	{
		bindParamsEndDate = new Date();
	}

	public void registerExecSqlBegin()
	{
	    execSqlStartDate = new Date();
	}

	public void registerExecSqlEndSuccess()
	{
		execSqlEndDate = new Date();
		successful = true;
	}

	public void registerExecSqlEndFailed()
	{
		execSqlEndDate = new Date();
	}

	public Date getInitDate()
	{
		return initDate;
	}

	public long getTotalExecutionTime()
	{
		return execSqlEndDate.getTime() - initDate.getTime();
	}

	public long getConnectionEstablishTime()
	{
		return getConnEndDate.getTime() - getConnStartDate.getTime();
	}

	public long getBindParamsBindTime()
	{
		return bindParamsEndDate.getTime() - bindParamsStartDate.getTime();
	}

	public long getSqlExecTime()
	{
		return execSqlEndDate.getTime() - execSqlStartDate.getTime();
	}
}