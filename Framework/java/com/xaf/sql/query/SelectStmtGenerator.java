package com.xaf.sql.query;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import com.xaf.db.*;
import com.xaf.value.*;

public class SelectStmtGenerator
{
	private QueryDefinition queryDefn;
	private QuerySelect select;
	private HashSet joins = new HashSet();
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

	public QueryDefinition getQueryDefn() { return queryDefn; }
	public QuerySelect getQuerySelect() { return select; }
	public List getBindParams() { return bindParams; }
	public boolean isValid() { return valid; }

	public void addJoin(QueryField field)
	{
		if(field == null)
			throw new RuntimeException("Null field");

		QueryJoin join = field.getJoin();
		if(join != null && ! joins.contains(join))
		{
			fromClause.add(join.getFromClauseExpr());
			String whereCriteria = join.getCriteria();
			if(whereCriteria != null)
				whereJoinClause.add(whereCriteria);
			joins.add(join);
		}
	}

	public void addParam(SingleValueSource bindParam)
	{
		bindParams.add(bindParam);
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

		List conds = select.getConditions();
		int condCount = conds.size();
		for(int c = 0; c < condCount; c++)
		{
			QueryCondition cond = (QueryCondition) conds.get(c);
            if(cond.removeIfValueIsNull())
            {
                String value = cond.getValue().getValue(vc);
                if(value == null || value.length() == 0)
                    continue;
            }

			QueryField field = cond.getField();
			if(field != null)
				addJoin(field);
			else
				return "Condition '"+c+"' has no field.";
		}

		StringBuffer sql = new StringBuffer();

		int selectCount = selectClause.size();
		int selectLast = selectCount-1;
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
		int fromLast = fromCount-1;
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
		int whereLast = whereCount-1;
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
		int condLast = condCount-1;
		if(condCount > 0)
		{
			if(haveJoinWheres)
				sql.append(" and (\n");
			else
				sql.append("where\n  (\n");

			for(int c = 0; c < condCount; c++)
			{
				QueryCondition cond = (QueryCondition) conds.get(c);
                if(cond.removeIfValueIsNull())
                {
                    String value = cond.getValue().getValue(vc);
                    if(value == null || value.length() == 0)
                        continue;
                }
				addJoin(cond.getField());
				sql.append("  (" + cond.getWhereCondExpr(this) + ")");
				if(c != condLast)
					sql.append(cond.getConnectorSql());
				sql.append("\n");
			}

			sql.append("  )\n");
			haveCondWheres = true;
		}

		List whereExprs = select.getWhereExpressions();
		if(whereExprs != null && whereExprs.size() > 0)
		{
			int whereExprsLast = whereExprs.size() - 1;
			boolean first = false;
			if(! haveJoinWheres && ! haveCondWheres)
			{
				sql.append("where\n  (\n");
				first = true;
			}

			for(int we = 0; we < condCount; we++)
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

		List orderBys = select.getOrderBy();
		int orderBysCount = orderBys.size();
		int orderBysLast = orderBysCount-1;
		if(orderBysCount > 0)
		{
			sql.append("order by\n");
			for(int ob = 0; ob < orderBysCount; ob++)
			{
				sql.append("  " + ((QueryField) orderBys.get(ob)).getQualifiedColName());
				if(ob != orderBysLast)
					sql.append(", ");
				sql.append("\n");
			}
		}

		/*
		int bindCount = bindParams.size();
		int bindLast = bindCount-1;
		if(bindCount > 0)
		{
			sql.append("bind \n");
			for(int bp = 0; bp < bindCount; bp++)
			{
				Object bindParam = bindParams.get(bp);
				if(bindParam instanceof BindParameter)
				{
					sql.append("  (BindParameter) '" + ((BindParameter) bindParam).getValue(null) + "'");
				}
				else
				{
					sql.append("  (SingleValueSource) '" + ((SingleValueSource) bindParam).getValue((DatabaseContext) null) + "'");
				}
				if(bp != bindLast)
					sql.append(", ");
				sql.append("\n");
			}
		}
		*/

		valid = true;
		return sql.toString();
	}
}
