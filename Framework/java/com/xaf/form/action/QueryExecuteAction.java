package com.xaf.form.action;

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
import com.xaf.value.*;

public class QueryExecuteAction extends BaseProcessAction
{
    static public final String DEFAULT_REPORTSKINID = "report";

    private StatementManager.StatementInfo statementInfo;
    private boolean debug;
	private String stmtName;
	private String stmtSourceId;
	private String dataSourceId;
	private String reportId;
	private String reportSkinId = DEFAULT_REPORTSKINID;
	private String storeValueName;
	private SingleValueSource storeValueSource;
	private boolean produceReport = true;
	private int storeValueType;

    public boolean isExecuteAction()
    {
        return true;
    }

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

    public void initializeProcessAction(Element elem) throws DialogProcessActionInitializeException
    {
        stmtSourceId = elem.getAttribute("stmt-src");
        if(stmtSourceId.length() == 0) stmtSourceId = null;

        dataSourceId = elem.getAttribute("data-src");
        if(dataSourceId.length() == 0) dataSourceId = null;

        if(elem.getChildNodes().getLength() > 0)
        {
            if(elem.getAttribute("name").length() == 0)
                elem.setAttribute("name", "SqlExecuteAction-" + getUniqueActionId());
            statementInfo = new StatementManager.StatementInfo();
            statementInfo.importFromXml(elem, "DialogProcessAction", null);
        }
        else
        {
            stmtName = elem.getAttribute("name");
        }

        debug = elem.getAttribute("debug").equals("yes");

        reportId = elem.getAttribute("report");
        if(reportId.length() == 0) reportId = null;

        reportSkinId = elem.getAttribute("skin");
        if(reportSkinId.length() == 0) reportSkinId = DEFAULT_REPORTSKINID;

        storeValueName = elem.getAttribute("store");
        if(storeValueName.length() == 0) storeValueName = null;

        if(reportId != null && reportId.equals("none"))
            produceReport = false;

		if(storeValueName != null)
		{
            setStoreType(elem.getAttribute("store-type"));

			storeValueSource = ValueSourceFactory.getStoreValueSource(storeValueName);
			if(storeValueSource == null)
				throw new DialogProcessActionInitializeException("SingleValueSource '"+ storeValueName +"' not found");
			if(! storeValueSource.supportsSetValue())
				throw new DialogProcessActionInitializeException("SingleValueSource '"+ storeValueName +"' does not support value storage.");

			if(storeValueSource instanceof DialogFieldValue)
				storeValueType = SingleValueSource.RESULTSET_STORETYPE_SINGLEROWFORMFLD;

			if(storeValueType == -1)
				throw new DialogProcessActionInitializeException("store-type must be one of "+SingleValueSource.RESULTSET_STORETYPES.toString());
		}
    }

    public String executeDialog(Dialog dialog, DialogContext dc)
    {
		ServletContext context = dc.getServletContext();
		StatementManager stmtManager = stmtSourceId == null ? StatementManagerFactory.getManager(context) : StatementManagerFactory.getManager(stmtSourceId);
		DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), context);

        if(stmtManager == null)
            return "StatementManager file '" + stmtSourceId + "' not found (specified in ServletContext config init parameter 'sql-statements-file'";

        if(debug)
        {
            StringBuffer debugMessage = new StringBuffer();
            StatementManager.StatementInfo si;
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
				debugMessage.append(si.getDebugHtml(dc));
				debugMessage.append("</pre>");
            }
            return debugMessage.toString();
        }

        StringWriter out = new StringWriter();

        try
        {
            if(produceReport && storeValueSource == null)
            {
                ReportSkin reportSkin = SkinFactory.getReportSkin(reportSkinId);
                if(reportSkin == null)
                    return "ReportSkin '"+reportSkinId+"' not found.";
                else
                {
                    if(statementInfo != null)
                        stmtManager.produceReport(out, dbContext, dc, reportSkin, statementInfo, null, reportId);
                    else
                        stmtManager.produceReport(out, dbContext, dc, reportSkin, stmtName, null, reportId);
                }
            }
            else if(!produceReport && storeValueSource != null)
            {
                if(statementInfo != null)
                    stmtManager.executeAndStore(dbContext, dc, dataSourceId, statementInfo, storeValueSource, storeValueType);
                else
                    stmtManager.executeAndStore(dbContext, dc, dataSourceId, stmtName, storeValueSource, storeValueType);
            }
            else if(produceReport && storeValueSource != null)
            {
                ReportSkin reportSkin = SkinFactory.getReportSkin(reportSkinId);
                if(reportSkin == null)
                    return "ReportSkin '"+reportSkinId+"' not found.";
                else
                {
                    if(statementInfo != null)
                        stmtManager.produceReportAndStoreResultSet(out, dbContext, dc, reportSkin, statementInfo, null, reportId, storeValueSource, storeValueType);
                    else
                        stmtManager.produceReportAndStoreResultSet(out, dbContext, dc, reportSkin, stmtName, null, reportId, storeValueSource, storeValueType);
                }
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e.toString());
        }
        catch(SQLException e)
        {
			StringBuffer errorMsg = new StringBuffer();
            StatementManager.StatementInfo si = stmtManager.getStatement(stmtName);

			StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));

			errorMsg.append("<pre>");
			errorMsg.append(si.getDebugHtml(dc));
			errorMsg.append("\n\n");
			errorMsg.append(stack.toString());
			errorMsg.append("</pre>");

			return errorMsg.toString();
        }
        catch(StatementNotFoundException e)
        {
            throw new RuntimeException(e.toString());
        }
        catch(NamingException e)
        {
            throw new RuntimeException(e.toString());
        }

        return out.toString();
    }
}