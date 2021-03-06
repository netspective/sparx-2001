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
 * $Id: QueryJoin.java,v 1.3 2002-08-31 00:18:04 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

public class QueryJoin
{
    private String name;
    private String tableName;
    private String fromClauseExpr;
    private String criteria;
    private int ruleWeight;
    private boolean autoInclude;
    private String implyJoinsStr;
    private QueryJoin[] implyJoins;

    public QueryJoin()
    {
    }

    public String getName()
    {
        return name;
    }

    public String getTableName()
    {
        return tableName;
    }

    public String getCriteria()
    {
        return criteria;
    }

    public String getFromClauseExpr()
    {
        return fromClauseExpr;
    }

    public QueryJoin[] getImpliedJoins()
    {
        return implyJoins;
    }

    public boolean shouldAutoInclude()
    {
        return autoInclude;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public void setFromClauseExpr(String fromClauseExpr)
    {
        this.fromClauseExpr = fromClauseExpr;
    }

    public void setCriteria(String criteria)
    {
        this.criteria = criteria;
    }

    public void setRuleWeight(int ruleWeight)
    {
        this.ruleWeight = ruleWeight;
    }

    public void setAutoInclude(boolean autoInclude)
    {
        this.autoInclude = autoInclude;
    }

    public void setImplyJoinsStr(String implyJoinsStr)
    {
        this.implyJoinsStr = implyJoinsStr;
    }

    public void setImplyJoins(QueryJoin[] implyJoins)
    {
        this.implyJoins = implyJoins;
    }

    public void finalizeDefn(QueryDefinition queryDefn)
    {
        if(fromClauseExpr == null)
        {
            if(tableName.equals(name))
                fromClauseExpr = tableName;
            else
                fromClauseExpr = tableName + " " + name;
        }

        if(implyJoinsStr != null)
        {
            StringTokenizer st = new StringTokenizer(implyJoinsStr, ",");
            List implyJoinsList = new ArrayList();
            while(st.hasMoreTokens())
            {
                String join = st.nextToken();
                QueryJoin joinDefn = queryDefn.getJoin(join);
                if(joinDefn == null)
                {
                    queryDefn.addError("field-join", "join '" + join + "' not found in imply-joins for join '" + name + "'");
                }
                else
                {
                    implyJoinsList.add(joinDefn);
                }
            }
            implyJoins = (QueryJoin[]) implyJoinsList.toArray(new QueryJoin[implyJoinsList.size()]);
        }
        else
            implyJoins = null;
    }

    public void importFromXml(Element elem)
    {
        name = elem.getAttribute("id");
        tableName = elem.getAttribute("table");

        fromClauseExpr = elem.getAttribute("from-expr");
        if(fromClauseExpr.length() == 0)
            fromClauseExpr = null;

        criteria = elem.getAttribute("condition");
        if(criteria.length() == 0)
            criteria = null;

        String value = elem.getAttribute("auto-include");
        if(value != null && value.equals("yes"))
            autoInclude = true;

        value = elem.getAttribute("weight");
        if(value.length() > 0)
            ruleWeight = Integer.parseInt(value);

        implyJoinsStr = elem.getAttribute("imply-join");
        if(implyJoinsStr.length() == 0)
            implyJoinsStr = null;
    }
}