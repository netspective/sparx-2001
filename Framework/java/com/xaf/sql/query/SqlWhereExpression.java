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

public class SqlWhereExpression
{
	static public final int CONNECT_AND = 0;
	static public final int CONNECT_OR  = 1;
	static public final String[] CONNECTOR_SQL = new String[] { " and ", " or " };

	private String expression;
	private SingleValueSource value;
	private int connector = CONNECT_AND;

    public SqlWhereExpression()
    {
    }

    public SqlWhereExpression(String expr, String connector)
    {
		this.expression = expr;
		setConnector(connector);
    }

	public SingleValueSource getValue() { return value; }
	public String getConnectorSql() { return QueryCondition.CONNECTOR_SQL[connector]; }

	public void setConnector(String connect)
	{
		if("and".equals(connect))
			connector = CONNECT_AND;
		else if("or".equals(connect))
			connector = CONNECT_OR;
	}

	public String getWhereCondExpr(SelectStmtGenerator stmt)
	{
		return expression;
	}

	public void importFromXml(Element elem)
	{
		this.expression = elem.getAttribute("value");
		setConnector(elem.getAttribute("connector"));
	}
}