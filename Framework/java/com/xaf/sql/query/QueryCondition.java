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
import org.w3c.dom.*;

import com.xaf.value.*;

public class QueryCondition
{
	static public final int CONNECT_AND = 0;
	static public final int CONNECT_OR  = 1;
	static public final String[] CONNECTOR_SQL = new String[] { " and ", " or " };

	private QueryField field;
	private SqlComparison comparison;
	private SingleValueSource value;
	private int connector = CONNECT_AND;
    private boolean removeIfValueNull;
    private String bindExpression;
    private List nestedConditions;

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

    public String getBindExpression() { return bindExpression; }
	public QueryField getField() { return field; }
	public SqlComparison getComparison() { return comparison; }
	public SingleValueSource getValue() { return value; }
	public String getConnectorSql() { return QueryCondition.CONNECTOR_SQL[connector]; }
    public boolean removeIfValueIsNull() { return removeIfValueNull; }
    public boolean isNested() { return nestedConditions != null; }
    public boolean isNotNested() { return nestedConditions == null; }

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator stmt)
	{
        if(nestedConditions == null)
            return comparison.getWhereCondExpr(vc, select, stmt, this);

        StringBuffer sql = new StringBuffer();
        int lastNestedCond = nestedConditions.size()-1;
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
            nestedConditions = new ArrayList();
        nestedConditions.add(cond);
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
            else if(! haveNested)
                queryDefn.addError("condition-field", "field '"+ fieldName +"' not found");
        }

		String compName = elem.getAttribute("comparison");
		comparison = SqlComparisonFactory.getComparison(compName);
		if(comparison == null)
        {
            if(parentCond != null)
                comparison = parentCond.getComparison();
            else if(! haveNested)
                queryDefn.addError("condition-comparison", "comparison id '"+ compName +"' not found");
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
        if (bindExpr != null && bindExpr.length() > 0)
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