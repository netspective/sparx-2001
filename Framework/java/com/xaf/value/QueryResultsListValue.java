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

public class QueryResultsListValue extends ListSource implements SingleValueSource
{
	private String stmtMgrName;
	private String dataSourceId;
	private String stmtName;
    private SingleValueSource[] queryParams;

    public QueryResultsListValue()
    {
    }

	public QueryResultsListValue(String stmtMgrName, String dataSourceId, String queryName)
	{
		this.stmtMgrName = stmtMgrName;
		this.dataSourceId = dataSourceId;
		this.stmtName = queryName;
	}

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Executes a query and returns the results of the query as rows. If just a statement-name is provided, then "+
            "the statement must exist in the default statement manager. If a statement manager file name is provided "+
            "then the statement must exist in the provided file. Optionally, a list of bind parameters may be supplied "+
            "as single value source specification of the form <code><u>vs:params</u></code>. Basically, any value source "+
            "may be used in the bind parameters. If the query is being used to supply a select field with choices, then "+
            "the first column is expected to be the caption to display and the second column, if any, will be used as the "+
            "id column.",
            new String[] { "statement-name", "stmt-mgr-file/statement-name", "statement-name?bindVS1,2,..." }
        );
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
        int delimPos = srcParams.indexOf('/');
        if(delimPos >= 0)
        {
            stmtMgrName = srcParams.substring(0, delimPos);
            stmtName = srcParams.substring(delimPos+1);
        }
        else
            stmtName = srcParams;

        delimPos = stmtName.indexOf('?');
        if(delimPos >= 0)
        {
            String queryParamsStr = srcParams.substring(delimPos+1);
            stmtName = stmtName.substring(0, delimPos);

            if(queryParamsStr.length() > 0)
            {
                List queryParamsList = new ArrayList();
                StringTokenizer st = new StringTokenizer(queryParamsStr, ",");
                while(st.hasMoreTokens())
                    queryParamsList.add(ValueSourceFactory.getSingleOrStaticValueSource(st.nextToken()));
                queryParams = (SingleValueSource[]) queryParamsList.toArray(new SingleValueSource[queryParamsList.size()]);
            }
        }
    }

    public ResultSet getResultSet(ValueContext vc) throws NamingException, StatementNotFoundException, SQLException, StatementExecutionException
    {
        StatementManager stmtMgr = stmtMgrName == null ? StatementManagerFactory.getManager(vc.getServletContext()) : StatementManagerFactory.getManager(stmtMgrName);
        DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
        StatementManager.ResultInfo ri = null;

        if(queryParams == null)
            ri = stmtMgr.execute(dbContext, vc, dataSourceId, stmtName);
        else
        {
            Object[] parameters = new Object[queryParams.length];
            for(int p = 0; p < queryParams.length; p++)
                parameters[p] = queryParams[p].getObjectValue(vc);
            ri = stmtMgr.execute(dbContext, vc, dataSourceId, stmtName, parameters);
        }

        return ri.getResultSet();
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();

		ResultSet rs = null;
		try
		{
			rs = getResultSet(vc);
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
                {
                    Connection con = rs.getStatement().getConnection();
					rs.close();
                    con.close();
                }

			}
			catch(Exception e)
			{
				choices.add(new SelectChoice(e.toString()));
			}
		}

        return choices;
	}

    public String[] getValues(ValueContext vc)
    {
		ResultSet rs = null;
		try
		{
			String[] result = StatementManager.getResultSetRowsAsStrings(getResultSet(vc));
			if(result == null)
				return new String[] { "" };
			else
				return result;
		}
		catch(Exception e)
		{
			return new String[] { e.toString() };
		}
		finally
		{
			try
			{
				if(rs != null)
                {
                    Connection con = rs.getStatement().getConnection();
					rs.close();
                    con.close();
                }

			}
			catch(Exception e)
			{
				return new String[] { e.toString() };
			}
		}
	}

	/* implemenations for SingleValueSource interface */
	public String getValue(ValueContext vc)
	{
		return (String) getObjectValue(vc);
	}

	public Object getObjectValue(ValueContext vc)
	{
		ResultSet rs = null;
		try
		{
			return StatementManager.getResultSetSingleColumn(getResultSet(vc));
		}
		catch(Exception e)
		{
			return e.toString();
		}
		finally
		{
			try
			{
				if(rs != null)
                {
                    Connection con = rs.getStatement().getConnection();
					rs.close();
                    con.close();
                }

			}
			catch(Exception e)
			{
				return e.toString();
			}
		}
	}

	public int getIntValue(ValueContext vc)
	{
		return ((Integer) getObjectValue(vc)).intValue();
	}

	public double getDoubleValue(ValueContext vc)
	{
		return ((Double) getObjectValue(vc)).doubleValue();
	}

	public String getValueOrBlank(ValueContext vc)
	{
		String value = getValue(vc);
		return value == null ? "" : value;
	}

	public boolean supportsSetValue()
	{
		return false;
	}

	public void setValue(ValueContext vc, Object value)
	{
	}

	public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
	{
	}

	public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
	{
	}

	public void setValue(ValueContext vc, String value)
	{
	}
}
