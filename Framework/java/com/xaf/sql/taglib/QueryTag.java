package com.xaf.sql.taglib;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.db.*;
import com.xaf.report.*;
import com.xaf.sql.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class QueryTag extends TagSupport
{
    static public final String DEFAULT_REPORTSKINID = "report";

    private String debug;
	private String name;
	private String stmtSource;
	private StatementManager stmtManager;
	private String dataSourceId;
	private String reportId;
	private String reportSkinId = DEFAULT_REPORTSKINID;
	private DatabaseContext dbContext;
	private ValueContext valueContext;
	private String storeValueName;
	private SingleValueSource storeValueSource;
	private boolean produceReport = true;
	private int storeValueType;

	public void release()
	{
		super.release();
        debug = null;
		name = null;
		stmtSource = null;
		dataSourceId = null;
		stmtManager = null;
		reportId = null;
		reportSkinId = DEFAULT_REPORTSKINID;
		dbContext = null;
		storeValueName = null;
		storeValueSource = null;
		produceReport = true;
		storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWMAP;
	}

   	public String getDebug() { return debug; }
	public void setDebug(String value) { debug = value; }

	public String getName() { return name; }
	public void setName(String value) {	name = value; }

	public String getStmtSource() { return stmtSource; }
	public void setStmtSource(String value) { stmtSource = value; }

	public String getDataSource() { return dataSourceId; }
	public void setDataSource(String value) { dataSourceId = value; }

	public String getReport() { return reportId; }
	public void setReport(String value) { reportId = value; if("none".equals(value)) produceReport = false; }

	public String getSkin() { return reportSkinId; }
	public void setSkin(String value) { reportSkinId = value; }

	public String getStore() { return storeValueName; }
	public void setStore(String value) { storeValueName = value; }

	public String getStoreType() { return Integer.toString(storeValueType); }
	public void setStoreType(String value)
	{
		String[] typeNames = SingleValueSource.RESULTSET_STORETYPES;
		int typeCount = typeNames.length;
		for(int i = 0; i < typeCount; i++)
		{
			if(typeNames[i].equals(value))
			{
				storeValueType = i;
				return;
			}
		}
		storeValueType = -1;
	}

	public String getProduceReport() { return new Boolean(produceReport).toString(); }
	public void setProduceReport(String value) { produceReport = "yes".equals(value); }

	public int doStartTag() throws JspException
	{
		ServletContext context = pageContext.getServletContext();
		stmtManager = stmtSource == null ? StatementManagerFactory.getManager(context) : StatementManagerFactory.getManager(stmtSource);
		dbContext = DatabaseContextFactory.getContext(pageContext.getRequest(), context);
		valueContext = new ServletValueContext((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), context);
		if(storeValueName != null)
		{
			storeValueSource = ValueSourceFactory.getStoreValueSource(storeValueName);
			if(storeValueSource == null)
				throw new JspException("SingleValueSource '"+ storeValueName +"' not found");
			if(! storeValueSource.supportsSetValue())
				throw new JspException("SingleValueSource '"+ storeValueName +"' does not support value storage.");

			if(storeValueSource instanceof DialogFieldValue)
				storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWFORMFLD;

			if(storeValueType == -1)
				throw new JspException("store-type must be one of "+SingleValueSource.RESULTSET_STORETYPES.toString());
		}

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		try
		{
			if(stmtManager == null)
				out.print("StatementManager file '" + stmtSource + "' not found (specified in ServletContext config init parameter 'sql-statements-file'");
			else
			{
                if("yes".equals(debug))
                {
                    StatementManager.StatementInfo si = stmtManager.getStatement(name);
                    if(si == null)
                        out.print("SQL: statement '"+name+"' doesn't exist");
                    else
                    {
                        out.print("<p>SQL: statement '"+name+"'</p><pre>" + si.getSql());
                        SingleValueSource[] params = si.getParams();
                        int[] paramTypes = si.getParamTypes();
                        if(params != null && params.length > 0)
                        {
                            out.print("\nBIND:\n");
                            for(int i = 0; i < params.length; i++)
                            {
                                SingleValueSource vs = (SingleValueSource) params[i];
                                out.print(Integer.toString(i+1));
                                out.print(": ");
                                out.print(vs.getValue(valueContext));
                                out.print(" (type = '");
                                out.print(StatementManager.getTypeNameForId(paramTypes[i]));
                                out.print("')");
                            }
                        }
                        out.print("</pre>");
                    }
                    return EVAL_PAGE;
                }

				if(produceReport && storeValueSource == null)
				{
					ReportSkin reportSkin = SkinFactory.getReportSkin(reportSkinId);
					if(reportSkin == null)
						out.print("ReportSkin '"+reportSkinId+"' not found.");
					else
						stmtManager.produceReport(out, dbContext, valueContext, reportSkin, name, null, reportId);
				}
				else if(!produceReport && storeValueSource != null)
				{
					stmtManager.executeAndStore(dbContext, valueContext, dataSourceId, name, storeValueSource, storeValueType);
				}
				else if(produceReport && storeValueSource != null)
				{
					ReportSkin reportSkin = SkinFactory.getReportSkin(reportSkinId);
					if(reportSkin == null)
						out.print("ReportSkin '"+reportSkinId+"' not found.");
					else
						stmtManager.produceReportAndStoreResultSet(out, dbContext, valueContext, reportSkin, name, null, reportId, storeValueSource, storeValueType);
				}
			}
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
		catch(StatementNotFoundException e)
		{
			throw new JspException(e.toString());
		}
		catch(NamingException e)
		{
			throw new JspException(e.toString());
		}
		catch(SQLException e)
		{
            StatementManager.StatementInfo si = stmtManager.getStatement(name);
			try
			{
	            StringWriter stack = new StringWriter();
                e.printStackTrace(new PrintWriter(stack));

				out.print("<h1>SQL Error</h1>");
				out.print("<pre>");
				out.print(si.getDebugHtml(valueContext));
				out.print("\n\n");
				out.print(stack.toString());
				out.print("</pre>");
			}
			catch(IOException ex)
			{
			}

			return SKIP_PAGE;
			//throw new JspException("<pre>" + si.getDebugHtml(valueContext) + "</pre><p>" + e.toString());
		}
		return EVAL_PAGE;
	}
}