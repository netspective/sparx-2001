/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Dec 4, 2001
 * Time: 10:27:00 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.sql.query;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ListValueSource;
import com.xaf.value.ValueContext;

import java.util.ArrayList;
import java.util.List;

public class QueryConditions extends ArrayList
{
    private QueryCondition parentCondition;
    private boolean haveAnyDynamicConditions;

    public QueryConditions(QueryCondition parent)
    {
        parentCondition = parent;
    }

    /**
     * Return true if any of the conditions in this list are dynamic -- that they should be removed if their
     * bind value happens to be null;
     */
    public boolean hasAnyDynamicConditions()
    {
        return haveAnyDynamicConditions;
    }

    /**
     * Insert a new condition into the list
     * @param condition the condition to insert
     */
    public void add(QueryCondition condition)
    {
        super.add((Object) condition);
        if(! haveAnyDynamicConditions && condition.removeIfValueIsNull())
            haveAnyDynamicConditions = true;
    }

    public void registerDynamicConditions()
    {
        for(int i = 0; i < size(); i++)
            if(! haveAnyDynamicConditions && ((QueryCondition) get(i)).removeIfValueIsNull())
                haveAnyDynamicConditions = true;
    }

    /**
     * Return the list of query conditions that were "used" or not removed because the condition is specified as
     * removeIfValueIsNull() and value of the bind parameter of the condition was null. While we are checking for
     * used conditions, we will use the QueryCondition.keepCondition method which will automatically process
     * child (nested) conditions. Also, while processing we will call the SelectStmtGenerator.addJoin method to
     * add the joins for each of the fields we're going to put into the used conditions list.
     * @param stmtGen the active SelectStmtGenerator
     * @param vc the active ValueContext
     */
    public QueryConditions getUsedConditions(SelectStmtGenerator stmtGen, ValueContext vc)
    {
        // if we don't have any dynamic conditions, all the conditions will be used :)
        if(! haveAnyDynamicConditions)
            return this;

        // if we get to here, it means only some of the query conditions will be used
        // we we need to keep track of them
        QueryConditions usedConditions = new QueryConditions(parentCondition);

		int allCondsCount = size();
		for(int c = 0; c < allCondsCount; c++)
		{
			QueryCondition cond = (QueryCondition) get(c);
            cond.useCondition(stmtGen, vc, usedConditions);
		}

        return usedConditions;
    }

    public void createSql(SelectStmtGenerator stmtGen, ValueContext vc, QueryConditions usedConditions, StringBuffer sql)
    {
        QuerySelect select = stmtGen.getQuerySelect();
        int usedCondsCount = usedConditions.size();
        int condsUsedLast = usedCondsCount-1;
        for(int c = 0; c < usedCondsCount; c++)
        {
            Object condObj = usedConditions.get(c);
            if(condObj instanceof QueryConditions)
            {
                sql.append(" (");
                createSql(stmtGen, vc, (QueryConditions) condObj, sql);
                sql.append(" )");
                if(c != condsUsedLast)
                    sql.append(parentCondition.getConnectorSql());
            }
            else
            {
                QueryCondition cond = (QueryCondition) usedConditions.get(c);
                sql.append(" (" + cond.getWhereCondExpr(vc, select, stmtGen) + ")");
                if(c != condsUsedLast)
                    sql.append(cond.getConnectorSql());
            }
            sql.append("\n");
        }
    }
}
