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
 * $Id: SelectStmtGenerator.java,v 1.3 2002-11-18 16:15:27 aye.thu Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;

public class SelectStmtGenerator
{
    private QueryDefinition queryDefn;
    private QuerySelect select;
    private Set joins = new HashSet();
    private List selectClause = new ArrayList();
    private List fromClause = new ArrayList();
    private List whereJoinClause = new ArrayList();
    private List orderByClause = new ArrayList();
    private List bindParams = new ArrayList();
    private boolean valid;

    public SelectStmtGenerator(QuerySelect select)
    {
        this.queryDefn = select.getQueryDefn();
        this.select = select;
    }

    public QueryDefinition getQueryDefn()
    {
        return queryDefn;
    }

    public QuerySelect getQuerySelect()
    {
        return select;
    }

    public List getBindParams()
    {
        return bindParams;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void addJoin(QueryField field)
    {
        if(field == null)
            throw new RuntimeException("Null field");

        QueryJoin join = field.getJoin();
        this.addJoin(join);
    }

    /**
     * Adds the "from" and "where" clauses related to the QueryJoin field
     *
     * @param join Query join field
     * @since [Version 1.2.8 Build 23]
     */
    public void addJoin(QueryJoin join)
    {
        if(join == null || joins.contains(join))
            return;

        fromClause.add(join.getFromClauseExpr());
        String whereCriteria = join.getCriteria();
        if(whereCriteria != null)
            whereJoinClause.add(whereCriteria);
        joins.add(join);

        QueryJoin[] impliedJoins = join.getImpliedJoins();
        if(impliedJoins != null && impliedJoins.length > 0)
        {
            for(int i = 0; i < impliedJoins.length; i++)
                addJoin(impliedJoins[i]);
        }
    }

    public void addParam(SingleValueSource bindParam)
    {
        bindParams.add(bindParam);
    }

    public void addParam(ListValueSource bindParamList)
    {
        bindParams.add(bindParamList);
    }

    public String toString(ValueContext vc)
    {
        valid = false;
        if(queryDefn == null)
            return "Query Definition is NULL";

        StringBuffer errorMsg = new StringBuffer();
        if(queryDefn.getErrors() != null)
        {
            List errors = queryDefn.getErrors();
            for(int i = 0; i < errors.size(); i++)
                errorMsg.append(errors.get(i) + ".\n");
        }
        if(select == null)
        {
            errorMsg.append("Query select is NULL.");
        }
        else
        {
            if(select.getErrors() != null)
            {
                List errors = select.getErrors();
                for(int i = 0; i < errors.size(); i++)
                    errorMsg.append(errors.get(i) + ".\n");
            }
        }
        if(errorMsg.length() > 0)
            return errorMsg.toString();

        List showFields = select.getReportFields();
        int showFieldsCount = showFields.size();
        for(int sf = 0; sf < showFieldsCount; sf++)
        {
            QueryField field = (QueryField) showFields.get(sf);
            String selClauseAndLabel = field.getSelectClauseExprAndLabel();
            if(selClauseAndLabel != null)
                selectClause.add(field.getSelectClauseExprAndLabel());
            addJoin(field);
        }

        QueryConditions allSelectConditions = select.getConditions();
        QueryConditions usedSelectConditions = allSelectConditions.getUsedConditions(this, vc);

        // add join tables which have the auto-include flag set and their respective conditions to the
        // from and where clause lists. If the join is already in the 'joins' list, no need to add it in.
        List autoIncJoinList = this.queryDefn.getAutoIncJoins();
        for(Iterator it = autoIncJoinList.iterator(); it.hasNext();)
        {
            this.addJoin((QueryJoin) it.next());
        }


        StringBuffer sql = new StringBuffer();

        int selectCount = selectClause.size();
        int selectLast = selectCount - 1;
        sql.append("select ");
        if(select.distinctRowsOnly())
            sql.append("distinct \n");
        else
            sql.append("\n");
        for(int sc = 0; sc < selectCount; sc++)
        {
            sql.append("  " + selectClause.get(sc));
            if(sc != selectLast)
                sql.append(", ");
            sql.append("\n");
        }

        int fromCount = fromClause.size();
        int fromLast = fromCount - 1;
        sql.append("from \n");
        for(int fc = 0; fc < fromCount; fc++)
        {
            sql.append("  " + fromClause.get(fc));
            if(fc != fromLast)
                sql.append(", ");
            sql.append("\n");
        }

        boolean haveJoinWheres = false;
        int whereCount = whereJoinClause.size();
        int whereLast = whereCount - 1;
        if(whereCount > 0)
        {
            sql.append("where\n  (\n");
            for(int wc = 0; wc < whereCount; wc++)
            {
                sql.append("  " + whereJoinClause.get(wc));
                if(wc != whereLast)
                    sql.append(" and ");
                sql.append("\n");
            }
            sql.append("  )");
            haveJoinWheres = true;
        }

        boolean haveCondWheres = false;
        int usedCondsCount = usedSelectConditions.size();
        if(usedCondsCount > 0)
        {
            String conditionSql = usedSelectConditions.createSql(this, vc, usedSelectConditions);
            if (conditionSql != null && conditionSql.length() > 0)
            {
                if (haveJoinWheres)
                {
                    sql.append(" and (\n");
                }
                else
                {
                    sql.append("where\n  (\n");
                }
                sql.append(conditionSql + "  )\n");
            }
            haveCondWheres = true;
        }

        List whereExprs = select.getWhereExpressions();
        if(whereExprs != null && whereExprs.size() > 0)
        {
            int whereExprsLast = whereExprs.size() - 1;
            boolean first = false;
            if(!haveJoinWheres && !haveCondWheres)
            {
                sql.append("where\n  (\n");
                first = true;
            }

            int whereExprsCount = whereExprs.size();
            for(int we = 0; we < whereExprsCount; we++)
            {
                SqlWhereExpression expr = (SqlWhereExpression) whereExprs.get(we);
                if(first)
                    first = false;
                else
                    sql.append(expr.getConnectorSql());

                sql.append(" (");
                sql.append(expr.getWhereCondExpr(this));
                sql.append("  )\n");
            }
        }
        List groupBys = select.getGroupBy();
        int groupBysCount = groupBys.size();
        if(groupBysCount > 0)
        {
            int groupByLast = groupBysCount - 1;
            sql.append("group by\n");
            for(int gb = 0; gb < groupBysCount; gb++)
            {
                QueryField field = (QueryField) groupBys.get(gb);
                sql.append("  " + field.getQualifiedColName());
                if(gb != groupByLast)
                {
                    sql.append(", ");
                }
                sql.append("\n");
            }

        }

        List orderBys = select.getOrderBy();
        int orderBysCount = orderBys.size();
        int orderBysLast = orderBysCount - 1;
        if(orderBysCount > 0)
        {
            sql.append("order by\n");
            for(int ob = 0; ob < orderBysCount; ob++)
            {
                QuerySortFieldRef sortRef = (QuerySortFieldRef) orderBys.get(ob);
                QueryDefinition.QueryFieldSortInfo[] fields = sortRef.getFields(vc);
                if(fields == null)
                {
                    return "Order by field '" + sortRef.getFieldName().getId() + "' did not evaluate to an appropriate QueryField.\n";
                }
                else
                {
                    int lastField = fields.length - 1;
                    for(int i = 0; i < fields.length; i++)
                    {
                        QueryDefinition.QueryFieldSortInfo fieldSortInfo = fields[i];
                        if(fieldSortInfo == null)
                            return "Order by field [" + i + "] in '" + sortRef.getFieldName().getId() + "' did not evaluate to an appropriate QueryField.\n";

                        sql.append("  " + fieldSortInfo.getField().getOrderByClauseExpr());
                        if(fieldSortInfo.isDescending())
                            sql.append(" desc");

                        if(i != lastField)
                        {
                            sql.append(", ");
                            sql.append("\n");
                        }
                    }
                }

                if(ob != orderBysLast)
                    sql.append(", ");
                sql.append("\n");
            }
        }

        valid = true;
        return sql.toString();
    }
}
