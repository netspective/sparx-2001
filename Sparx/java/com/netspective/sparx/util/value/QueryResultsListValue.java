/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: QueryResultsListValue.java,v 1.4 2002-02-09 13:02:12 snshah Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;

import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.sql.StatementExecutionException;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

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
                "Executes a query and returns the results of the query as rows. If just a statement-name is provided, then " +
                "the statement must exist in the default statement manager. If a statement manager file name is provided " +
                "then the statement must exist in the provided file. Optionally, a list of bind parameters may be supplied " +
                "as single value source specification of the form <code><u>vs:params</u></code>. Basically, any value source " +
                "may be used in the bind parameters. If the query is being used to supply a select field with choices, then " +
                "the first column is expected to be the caption to display and the second column, if any, will be used as the " +
                "id column.",
                new String[]{"statement-name", "stmt-mgr-file/statement-name", "statement-name?bindVS1,2,..."}
        );
    }

    public void initializeSource(String srcParams)
    {
        super.initializeSource(srcParams);
        int delimPos = srcParams.indexOf('/');
        if(delimPos >= 0)
        {
            stmtMgrName = srcParams.substring(0, delimPos);
            stmtName = srcParams.substring(delimPos + 1);
        }
        else
            stmtName = srcParams;

        delimPos = stmtName.indexOf('?');
        if(delimPos >= 0)
        {
            String queryParamsStr = srcParams.substring(delimPos + 1);
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

    public StatementManager.ResultInfo getResultInfo(ValueContext vc) throws NamingException, StatementNotFoundException, SQLException, StatementExecutionException
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

        return ri;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        SelectChoicesList choices = new SelectChoicesList();

        StatementManager.ResultInfo ri = null;
        try
        {
            ri = getResultInfo(vc);
            ResultSet rs = ri.getResultSet();
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
            else if(numColumns > 1)
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
                if(ri != null)
                    ri.close();
                ri = null;
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
        StatementManager.ResultInfo ri = null;
        try
        {
            ri = getResultInfo(vc);
            String[] result = StatementManager.getResultSetRowsAsStrings(ri.getResultSet());
            if(result == null)
                return new String[]{""};
            else
                return result;
        }
        catch(Exception e)
        {
            return new String[]{e.toString()};
        }
        finally
        {
            try
            {
                if(ri != null)
                    ri.close();
                ri = null;
            }
            catch(Exception e)
            {
                return new String[]{e.toString()};
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
        StatementManager.ResultInfo ri = null;
        try
        {
            ri = getResultInfo(vc);
            return StatementManager.getResultSetSingleColumn(ri.getResultSet());
        }
        catch(Exception e)
        {
            return e.toString();
        }
        finally
        {
            try
            {
                if(ri != null)
                    ri.close();
                ri = null;
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
