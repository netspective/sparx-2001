package com.netspective.sparx.util.value;

import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.sql.StatementInfo;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import javax.servlet.ServletContext;
import javax.naming.NamingException;
import java.util.Map;
import java.sql.SQLException;

/**
 * @author aye
 * $Id: CachedQueryResultsListValue.java,v 1.1 2003-04-23 21:05:00 aye.thu Exp $
 */
public class CachedQueryResultsListValue extends QueryResultsListValue
{
    public static final String CACHED_ATTRUBITE_PREFIX = "cached-query";

    public CachedQueryResultsListValue()
    {
        super();
    }

    public CachedQueryResultsListValue(String stmtMgrName, String dataSourceId, String queryName)
    {
        super(stmtMgrName, dataSourceId, queryName);
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Executes a query and returns the results of the query as rows. The result is then saved within the" +
                "servlet context using the statement-name so that when the next request comes in, the query is not" +
                "re-executed and the cached result from the servlet context is returned. If just a statement-name is provided, then " +
                "the statement must exist in the default statement manager. If a statement manager file name is provided " +
                "then the statement must exist in the provided file. Optionally, a list of bind parameters may be supplied " +
                "as single value source specification of the form <code><u>vs:params</u></code>. Basically, any value source " +
                "may be used in the bind parameters. If the query is being used to supply a select field with choices, then " +
                "the first column is expected to be the caption to display and the second column, if any, will be used as the " +
                "id column.",
                new String[]{"statement-name", "stmt-mgr-file/statement-name", "statement-name?bindVS1,2,..."}
        );
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        SelectChoicesList choices = new SelectChoicesList();
        Object[][] cachedResult = null;
        try
        {
            cachedResult = getQueryResult(vc);
        }
        catch (Exception e)
        {
            // if an exception was thrown, report it to the screen
            e.printStackTrace();
            choices.add(new SelectChoice(e.toString()));
        }

        // if the query result is not null, create the choices using the result
        if (cachedResult != null)
        {
            for (int i = 0; i < cachedResult.length; i++)
            {
                Object[] array = cachedResult[i];
                if (array.length == 1)
                {
                    choices.add(new SelectChoice(array[0].toString(), array[0].toString()));
                }
                else if (array.length > 1)
                {
                    choices.add(new SelectChoice(array[0].toString(), array[1].toString()));
                }
            }
        }

        return choices;
    }


    public String[] getValues(ValueContext vc)
    {
        String[] values = null;
        Object[][] cachedResult = null;
        try
        {
            cachedResult = getQueryResult(vc);
        }
        catch (Exception e)
        {
            // if an exception was thrown, report it to the screen
            e.printStackTrace();
            values = new String[] {e.toString()};
            return values;
        }

        if (cachedResult != null)
        {
            values = new String[cachedResult.length];
            for (int i = 0; i < cachedResult.length; i++)
            {
                Object[] array = cachedResult[i];
                values[i] = array[0].toString();
            }
        }
        else
        {
            // TODO: Return an array with zero length or return an array with one null member?
            values = new String[]{""};
        }
        return values;
    }

    public Object getObjectValue(ValueContext vc)
    {
        Object[][] cachedResult = null;
        try
        {
            cachedResult = getQueryResult(vc);
        }
        catch (Exception e)
        {
            // if an exception was thrown, report it to the screen
            e.printStackTrace();
            return e.toString();
        }
        // assume that the resultset only had one row
        if (cachedResult != null && cachedResult.length >= 1)
        {
            Object[] array = cachedResult[0];
            if (array.length >= 1)
                return array[0];
        }
        return null;
    }

    /**
     * Gets the cached query result's servlet context attribute name
     * @return
     */
    protected String getCachedAttributeName(ValueContext vc)
    {
        DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
        String dsName = dbContext.translateDataSourceId(vc, getDataSourceId());
        return dsName + "." + CACHED_ATTRUBITE_PREFIX + "." + getStatementName();
    }
    /**
     * Gets the query results from the servlet context and if it doesn't exist, then the query is executed to
     * retriee the results and saved to the servlet context.
     * @param vc
     * @return
     * @throws NamingException
     * @throws SQLException
     */
    private Object[][] getQueryResult(ValueContext vc)  throws StatementNotFoundException,  NamingException, SQLException
    {
        ServletContext sc = vc.getServletContext();
        Object[][] cachedResult = (Object[][])sc.getAttribute(getCachedAttributeName(vc));
        if (cachedResult == null)
        {
            String stmtMgrName = getStatementManagerName();
            StatementManager stmtMgr = stmtMgrName == null ? StatementManagerFactory.getManager(vc.getServletContext()) : StatementManagerFactory.getManager(stmtMgrName);
            DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
            // IMPORTANT: Don't use the executeStmtGetValuesMapArray() method which DOES NOT perserve the order of the
            // columns.
            cachedResult = stmtMgr.executeStmtGetValuesMatrix(dbContext, vc, getDataSourceId(), getStatementName(), getQueryParams());
            if (cachedResult != null)
            {
                sc.setAttribute(getCachedAttributeName(vc), cachedResult);
                System.out.println("Created new cached query result: " + getCachedAttributeName(vc));
            }
        }

        return cachedResult;
    }
}
