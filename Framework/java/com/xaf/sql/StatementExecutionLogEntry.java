package com.xaf.sql;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.value.*;
import com.xaf.log.*;

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
		{
			HttpServletRequest httpReq = (HttpServletRequest) req;
			source = httpReq.getRequestURI() + "?" + httpReq.getQueryString();
		}

		statementName = si.getId();
		initDate = new Date();

		AppServerCategory cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.DEBUG_SQL);
		if(cat.isDebugEnabled())
		{
			cat.debug(statementName + LogManager.MONITOR_ENTRY_FIELD_SEPARATOR + source + LogManager.MONITOR_ENTRY_FIELD_SEPARATOR + si.getSql(vc));
			StatementParameter[] params = si.getParams();
			if(params != null)
			{
				for(int i = 1; i <= params.length; i++)
				{
					StatementParameter param = params[i-1];
					if(param.isListType())
					{
						String[] values = param.getListSource().getValues(vc);
						if(values != null)
						{
							for(int v = 0; v < values.length; v++)
								cat.debug("Bind "+ statementName +" ["+ i +"]["+ v +"] {string}: " + values[v] + " (list)");
						}
						else
						{
							cat.debug("Bind "+ statementName +" ["+ i +"]: NULL (list)");
						}
					}
					else
					{
						String type = StatementManager.getTypeNameForId(param.getParamType());
						SingleValueSource vs = param.getValueSource();
						cat.debug("Bind "+ statementName +" ["+ i +"] {"+ vs.getId() +"}: " + vs.getValue(vc) + " (" + type + ")");
					}
				}
			}
		}
    }

	public String getSource()
	{
		return source;
	}

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

	public void finalize(ValueContext vc)
	{
		AppServerCategory cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.MONITOR_SQL);
		if(! cat.isInfoEnabled())
			return;

		StringBuffer info = new StringBuffer();
		info.append(statementName);
		info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		info.append(successful ? 1 : 0);
		info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		if(successful)
		{
			info.append(getConnEndDate.getTime() - getConnStartDate.getTime());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(bindParamsEndDate.getTime() - bindParamsStartDate.getTime());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(execSqlEndDate.getTime() - execSqlStartDate.getTime());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(execSqlEndDate.getTime() - initDate.getTime());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		}
		else
		{
			info.append(-1);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(-1);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(-1);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(-1);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		}
		info.append(source);

		cat.info(info.toString());
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