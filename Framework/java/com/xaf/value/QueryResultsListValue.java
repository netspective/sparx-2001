package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.db.*;
import com.xaf.sql.*;

public class QueryResultsListValue extends ListSource
{
	private String stmtMgrName;
	private String dataSourceId;
	private String stmtName;

    public QueryResultsListValue()
    {
    }

	public QueryResultsListValue(String stmtMgrName, String dataSourceId, String queryName)
	{
		this.stmtMgrName = stmtMgrName;
		this.dataSourceId = dataSourceId;
		this.stmtName = queryName;
	}

    public void initializeSource(String srcParams)
    {
        int delimPos = srcParams.indexOf('/');
        if(delimPos >= 0)
        {
            stmtMgrName = srcParams.substring(0, delimPos);
            stmtName = srcParams.substring(delimPos+1);
        }
        else
            this.stmtName = srcParams;
    }

    public ResultSet getResultSet(DialogContext dc) throws NamingException, StatementNotFoundException, SQLException, StatementExecutionException
    {
        StatementManager stmtMgr = stmtMgrName == null ? StatementManagerFactory.getManager(dc.getServletContext()) : StatementManagerFactory.getManager(stmtMgrName);
        DatabaseContext dbContext = DatabaseContextFactory.getContext(dc);

        StatementManager.ResultInfo ri = stmtMgr.execute(dbContext, dc, dataSourceId, stmtName);
        return ri.getResultSet();
    }

    public SelectChoicesList getSelectChoices(DialogContext dc, String key)
    {
		if(choices == null) choices = new SelectChoicesList(); else choices.clear();

		ResultSet rs = null;
		try
		{
			rs = getResultSet(dc);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			if(numColumns == 1)
			{
				while(rs.next())
				{
					String columnData = rs.getString(1);
					choices.add(new SelectChoice(columnData, columnData));
				}
			}
			else if (numColumns > 1)
			{
				while(rs.next())
				{
					choices.add(new SelectChoice(rs.getString(1), rs.getString(2)));
				}
			}
		}
		catch(Exception e)
		{
			choices.add(new SelectChoice(e.toString()));
		}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				choices.add(new SelectChoice(e.toString()));
			}
		}

        return choices;
	}
}
