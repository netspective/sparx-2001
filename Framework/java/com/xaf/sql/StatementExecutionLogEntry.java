package com.xaf.sql;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

public class StatementExecutionLogEntry
{
	private boolean successful;
	private Object source;
	private String statementName;
	private Date initDate;
	private Date getConnStartDate;
	private Date getConnEndDate;
	private Date bindParamsStartDate;
	private Date bindParamsEndDate;
	private Date execSqlStartDate;
	private Date execSqlEndDate;

    public StatementExecutionLogEntry(Object source, StatementInfo si)
    {
		this.source = source;
		statementName = si.getId();
		initDate = new Date();
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

	public long getInitTime()
	{
		return initDate.getTime();
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