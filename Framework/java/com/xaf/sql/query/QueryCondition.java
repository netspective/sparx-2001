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
    private String bindExpression=null;

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

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator stmt)
	{
		return comparison.getWhereCondExpr(vc, select, stmt, this);
	}

	public void importFromXml(QueryDefinition queryDefn, Element elem)
	{
		String fieldName = elem.getAttribute("field");
		field = queryDefn.getField(fieldName);
		if(field == null)
			queryDefn.addError("condition-field", "field '"+ fieldName +"' not found");

		String compName = elem.getAttribute("comparison");
		comparison = SqlComparisonFactory.getComparison(compName);
		if(comparison == null)
			queryDefn.addError("condition-comparison", "comparison id '"+ compName +"' not found");

		String valStr = elem.getAttribute("value");
		if(valStr != null && valStr.length() > 0)
			value = ValueSourceFactory.getSingleOrStaticValueSource(valStr);

		String connect = elem.getAttribute("connector");
		if("and".equals(connect))
			connector = CONNECT_AND;
		else if("or".equals(connect))
			connector = CONNECT_OR;

        if(elem.getAttribute("allow-null").equals("no"))
            removeIfValueNull = true;

        /* check if user explicitly stated what the bind expression should be */
        String bindExpr = elem.getAttribute("bind-expr");
        if (bindExpr != null && bindExpr.length() > 0)
            this.bindExpression = bindExpr;
	}
}