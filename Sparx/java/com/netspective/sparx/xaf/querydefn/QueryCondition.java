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
 * $Id: QueryCondition.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class QueryCondition
{
    static public final int CONNECT_AND = 0;
    static public final int CONNECT_OR = 1;
    static public final String[] CONNECTOR_SQL = new String[]{" and ", " or "};

    private QueryField field;
    private SqlComparison comparison;
    private SingleValueSource value;
    private int connector = CONNECT_AND;
    private boolean removeIfValueNull;
    private boolean removeIfValueNullChildren;
    private String bindExpression;
    private QueryConditions nestedConditions;

    public QueryCondition()
    {
    }

    public QueryCondition(QueryField field, SqlComparison comparison, String valStr, String connect)
    {
        this.field = field;
        this.comparison = comparison;

        if(valStr != null && valStr.length() > 0)
            value = ValueSourceFactory.getSingleOrStaticValueSource(valStr);

        if("and".equals(connect))
            connector = CONNECT_AND;
        else if("or".equals(connect))
            connector = CONNECT_OR;
    }

    public String getBindExpression()
    {
        return bindExpression;
    }

    public QueryField getField()
    {
        return field;
    }

    public SqlComparison getComparison()
    {
        return comparison;
    }

    public SingleValueSource getValue()
    {
        return value;
    }

    public String getConnectorSql()
    {
        return QueryCondition.CONNECTOR_SQL[connector];
    }

    public boolean isNested()
    {
        return nestedConditions != null;
    }

    public boolean isNotNested()
    {
        return nestedConditions == null;
    }

    public boolean removeIfValueIsNull()
    {
        return removeIfValueNull || removeIfValueNullChildren;
    }

    public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator stmt)
    {
        if(nestedConditions == null)
            return comparison.getWhereCondExpr(vc, select, stmt, this);

        StringBuffer sql = new StringBuffer();
        int lastNestedCond = nestedConditions.size() - 1;
        for(int c = 0; c <= lastNestedCond; c++)
        {
            QueryCondition cond = (QueryCondition) nestedConditions.get(c);
            stmt.addJoin(cond.getField());
            sql.append(" (" + cond.getWhereCondExpr(vc, select, stmt) + ")");
            if(c != lastNestedCond)
                sql.append(cond.getConnectorSql());
        }
        return sql.toString();
    }

    public void addCondition(QueryCondition cond)
    {
        if(nestedConditions == null)
            nestedConditions = new QueryConditions(this);
        nestedConditions.add(cond);
        if(cond.removeIfValueIsNull())
            removeIfValueNullChildren = true;
    }

    /**
     * Return true if this condition should be kept when dynamically generating the where clause. One of the
     * reasons to not keep the condition would be because the value is null and we don't want the where clause
     * element to have any items with nulls. If this condition is a nested condition, we will check to see if
     * any of our nested conditions are used; if none of the nested conditions are used, then we will not keep
     * the condition.
     */
    public boolean useCondition(SelectStmtGenerator stmtGen, ValueContext vc, QueryConditions usedConditions)
    {
        if(nestedConditions != null)
        {
            QueryConditions nestedUsedConditions = nestedConditions.getUsedConditions(stmtGen, vc);
            if(nestedUsedConditions.size() == 0)
                return false;

            usedConditions.add(nestedUsedConditions);
            return true;
        }
        else
        {
            SingleValueSource vs = getValue();
            if(vs instanceof ListValueSource)
            {
                String[] values = ((ListValueSource) vs).getValues(vc);
                if(values == null || values.length == 0 || (values.length == 1 && (values[0] == null || values[0].length() == 0)))
                    return false;
            }
            else
            {
                String value = vs.getValue(vc);
                if(value == null || value.length() == 0)
                    return false;
            }

            usedConditions.add(this);
            stmtGen.addJoin(field);
            return true;
        }
    }

    public void importFromXml(QueryDefinition queryDefn, QueryCondition parentCond, Element elem)
    {
        // see if we have any nested conditions
        NodeList nested = elem.getElementsByTagName("condition");
        boolean haveNested = nested.getLength() > 0;

        String fieldName = elem.getAttribute("field");
        field = queryDefn.getField(fieldName);
        if(field == null)
        {
            if(parentCond != null)
                field = parentCond.getField();
            else if(!haveNested)
                queryDefn.addError("condition-field", "field '" + fieldName + "' not found");
        }

        String compName = elem.getAttribute("comparison");
        comparison = SqlComparisonFactory.getComparison(compName);
        if(comparison == null)
        {
            if(parentCond != null)
                comparison = parentCond.getComparison();
            else if(!haveNested)
                queryDefn.addError("condition-comparison", "comparison id '" + compName + "' not found");
        }

        String valStr = elem.getAttribute("value");
        if(valStr != null && valStr.length() > 0)
            value = ValueSourceFactory.getSingleOrStaticValueSource(valStr);
        else if(parentCond != null)
            value = parentCond.getValue();

        String connect = elem.getAttribute("connector");
        if("and".equals(connect))
            connector = CONNECT_AND;
        else if("or".equals(connect))
            connector = CONNECT_OR;

        if(elem.getAttribute("allow-null").equals("no"))
            removeIfValueNull = true;
        else if(parentCond != null)
            removeIfValueNull = parentCond.removeIfValueNull;

        // right now we're not allowing nested conditions to have dynamic include/exclude capability
        if(parentCond != null && removeIfValueNull)
            queryDefn.addError("netsted-condition", "Nested conditions can not have attribute 'allow-null=\"no\"'");

        /* check if user explicitly stated what the bind expression should be */
        String bindExpr = elem.getAttribute("bind-expr");
        if(bindExpr != null && bindExpr.length() > 0)
            this.bindExpression = bindExpr;
        else if(parentCond != null)
            this.bindExpression = parentCond.getBindExpression();

        if(haveNested)
        {
            for(int n = 0; n < nested.getLength(); n++)
            {
                Element node = (Element) nested.item(n);
                QueryCondition cond = new QueryCondition();
                cond.importFromXml(queryDefn, (Element) node);
                addCondition(cond);
            }
        }
    }

    public void importFromXml(QueryDefinition queryDefn, Element elem)
    {
        importFromXml(queryDefn, null, elem);
    }
}