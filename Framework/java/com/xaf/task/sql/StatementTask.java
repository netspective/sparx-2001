package com.xaf.task.sql;

import java.io.*;
import java.sql.*;
import java.util.*;

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

public class StatementTask extends BasicTask
{
    static public final String DEFAULT_REPORTSKINID = "report";

    private StatementInfo statementInfo;
	private String stmtName;
	private String stmtSourceId;
    private SingleValueSource dataSourceValueSource;
	private String reportId;
	private String storeValueName;
	private SingleValueSource skinValueSource = new StaticValue(DEFAULT_REPORTSKINID);
	private SingleValueSource storeValueSource;
	private SingleValueSource reportDestValueSource;
	private boolean produceReport = true;
	private int storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWMAP;

    public StatementTask()
    {
		super();
    }

	public void reset()
	{
		super.reset();
		statementInfo = null;
		stmtName = null;
		stmtSourceId = null;
		dataSourceValueSource = null;
		reportId = null;
		skinValueSource = new StaticValue(DEFAULT_REPORTSKINID);
		storeValueName = null;
		storeValueSource = null;
		reportDestValueSource = null;
		produceReport = true;
		storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWMAP;
	}

	public SingleValueSource getReportDestSource() { return reportDestValueSource; }
	public void setReportDestId(String value)
	{
		if(value == null || value.length() == 0)
			reportDestValueSource = null;
		else
			reportDestValueSource = ValueSourceFactory.getSingleOrStaticValueSource(value);
	}

	public String getStmtName() { return stmtName; }
	public void setStmtName(String value) {	stmtName = value; }

	public String getStmtSource() { return stmtSourceId; }
	public void setStmtSource(String value) { stmtSourceId = value; }

	public SingleValueSource getDataSource() { return dataSourceValueSource; }
	public void setDataSource(String value)
    {
        dataSourceValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public String getReport() { return reportId; }
	public void setReport(String value) { reportId = value; if("none".equals(value)) produceReport = false; }

	public SingleValueSource getSkin() { return skinValueSource; }
	public void setSkin(String value) { skinValueSource = ValueSourceFactory.getSingleOrStaticValueSource(value); }

	public String getStore() { return storeValueName; }
	public void setStore(String value) { storeValueName = value; }

	public String getStoreType() { return Integer.toString(storeValueType); }
	public void setStoreType(String value)
	{
        if(value.length() == 0)
        {
            storeValueType = -1;
            return;
        }

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

    public void initialize(Element elem) throws TaskInitializeException
    {
		super.initialize(elem);

        stmtSourceId = elem.getAttribute("stmt-src");
        if(stmtSourceId.length() == 0) stmtSourceId = null;

        setDataSource(elem.getAttribute("data-src"));

        if(elem.getChildNodes().getLength() > 0)
        {
            if(elem.getAttribute("name").length() == 0)
                elem.setAttribute("name", "SqlExecuteAction-" + getTaskNum());
            statementInfo = new StatementInfo();
            statementInfo.importFromXml(elem, "Task", null);
        }
        else
        {
            stmtName = elem.getAttribute("name");
        }

        reportId = elem.getAttribute("report");
        if(reportId.length() == 0) reportId = null;

        String reportSkinId = elem.getAttribute("skin");
        if(reportSkinId.length() == 0) reportSkinId = DEFAULT_REPORTSKINID;
		setSkin(reportSkinId);

        storeValueName = elem.getAttribute("store");
        if(storeValueName.length() == 0) storeValueName = null;

        if(reportId != null && reportId.equals("none"))
            produceReport = false;

		if(storeValueName != null)
		{
            setStoreType(elem.getAttribute("store-type"));
		}

		setReportDestId(elem.getAttribute("destination"));
    }

    public void execute(TaskContext tc) throws TaskExecuteException
    {
        tc.registerTaskExecutionBegin(this);

		if(storeValueName != null && storeValueSource == null)
		{
			storeValueSource = ValueSourceFactory.getStoreValueSource(storeValueName);
			if(storeValueSource == null)
				throw new TaskExecuteException("SingleValueSource '"+ storeValueName +"' not found");
			if(! storeValueSource.supportsSetValue())
				throw new TaskExecuteException("SingleValueSource '"+ storeValueName +"' does not support value storage.");

			if(storeValueSource instanceof DialogFieldValue)
				storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWFORMFLD;

			if(storeValueType == -1)
				throw new TaskExecuteException("store-type must be one of "+SingleValueSource.RESULTSET_STORETYPES.toString());
		}

		ServletContext context = tc.getServletContext();
		StatementManager stmtManager = stmtSourceId == null ? StatementManagerFactory.getManager(context) : StatementManagerFactory.getManager(stmtSourceId);
		DatabaseContext dbContext = DatabaseContextFactory.getContext(tc.getRequest(), context);

        if(stmtManager == null)
		{
            tc.addErrorMessage("StatementManager file '" + stmtSourceId + "' not found (specified in ServletContext config init parameter 'sql-statements-file'", false);
			return;
		}

        if(flagIsSet(TASKFLAG_DEBUG))
        {
            StringBuffer debugMessage = new StringBuffer();
            StatementInfo si;
            if(statementInfo != null)
                si = statementInfo;
            else
                si = stmtManager.getStatement(stmtName);

            if(si == null)
                debugMessage.append("SQL: statement '"+stmtName+"' doesn't exist");
            else
            {
				debugMessage.append("<p>SQL: statement '");
				debugMessage.append(si.getId());
				debugMessage.append("</p></pre>");
				debugMessage.append(si.getDebugHtml(tc));
				debugMessage.append("</pre>");
            }
			tc.addErrorMessage(debugMessage.toString(), false);
            return;
        }

		int reportDestId = ReportDestination.DEST_BROWSER_SINGLE_PAGE;
		if(reportDestValueSource != null)
		{
			String reportDestName = reportDestValueSource.getValue(tc);
			reportDestId = ReportDestination.getDestIdFromName(reportDestName);
			if(reportDestId == -1 || reportDestId == ReportDestination.DEST_BROWSER_MULTI_PAGE)
				throw new TaskExecuteException("ReportDestination '"+ reportDestName +"' not supported. Use 'browser', 'file' or 'email'");
		}

		Writer out = null;
		ReportDestination reportDest = null;
        ReportSkin reportSkin = SkinFactory.getReportSkin(skinValueSource.getValue(tc));
        if(reportSkin == null)
		{
			tc.addErrorMessage("ReportSkin '"+skinValueSource.getId()+"' not found.", false);
            return;
		}

        try
        {
			if(reportDestId == ReportDestination.DEST_FILE_DOWNLOAD || reportDestId == ReportDestination.DEST_FILE_EMAIL)
			{
				reportDest = new ReportDestination(reportDestId, tc, reportSkin);
				out = reportDest.getWriter();
			}
			else
				out = new StringWriter();
            String dataSourceId = this.getDataSource() != null ?this.getDataSource().getValue(tc) : null;
            if(produceReport && storeValueSource == null)
            {
                if(statementInfo != null)
                    stmtManager.produceReport(out, dbContext, tc, dataSourceId, reportSkin, statementInfo, null, reportId);
                else
                    stmtManager.produceReport(out, dbContext, tc, dataSourceId, reportSkin, stmtName, null, reportId);
            }
            else if(!produceReport && storeValueSource != null)
            {
                if(statementInfo != null)
                    stmtManager.executeAndStore(dbContext, tc, dataSourceId, statementInfo, storeValueSource, storeValueType);
                else
                    stmtManager.executeAndStore(dbContext, tc, dataSourceId, stmtName, storeValueSource, storeValueType);
            }
            else if(produceReport && storeValueSource != null)
            {
                if(statementInfo != null)
                    stmtManager.produceReportAndStoreResultSet(out, dbContext, tc, dataSourceId, reportSkin, statementInfo, null, reportId, storeValueSource, storeValueType);
                else
                    stmtManager.produceReportAndStoreResultSet(out, dbContext, tc, dataSourceId, reportSkin, stmtName, null, reportId, storeValueSource, storeValueType);
            }

			out.close();
        }
        catch(IOException e)
        {
            throw new TaskExecuteException(e);
        }
        catch(SQLException e)
        {
			StringBuffer errorMsg = new StringBuffer();
            StatementInfo si = statementInfo != null ? statementInfo : stmtManager.getStatement(stmtName);

			StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

			errorMsg.append("<pre>");
			errorMsg.append(si.getDebugHtml(tc));
			errorMsg.append("\n\n");
			errorMsg.append(stack.toString());
			errorMsg.append("</pre>");

			tc.addErrorMessage(errorMsg.toString(), false);
            return;
        }
        catch(StatementNotFoundException e)
        {
            throw new TaskExecuteException(e);
        }
        catch(NamingException e)
        {
            throw new TaskExecuteException(e);
        }

		if(reportDest != null)
			tc.addResultMessage(reportDest.getUserMessage());
		else
			tc.addResultMessage(out.toString());

        tc.registerTaskExecutionEnd(this);
    }
}