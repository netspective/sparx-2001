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
 * $Id: QueryConditions.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.ArrayList;

import com.netspective.sparx.util.value.ValueContext;

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
        if(!haveAnyDynamicConditions && condition.removeIfValueIsNull())
            haveAnyDynamicConditions = true;
    }

    public void registerDynamicConditions()
    {
        for(int i = 0; i < size(); i++)
            if(!haveAnyDynamicConditions && ((QueryCondition) get(i)).removeIfValueIsNull())
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
        if(!haveAnyDynamicConditions)
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
        int condsUsedLast = usedCondsCount - 1;
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
