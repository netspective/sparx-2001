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

public class QueryJoin
{
	private String name;
	private String tableName;
	private String fromClauseExpr;
	private String criteria;
	private int ruleWeight;
	private boolean autoInclude;

    public QueryJoin()
    {
    }

	public String getName() { return name; }
	public String getTableName() { return tableName; }
	public String getCriteria() { return criteria; }
	public String getFromClauseExpr() { return fromClauseExpr; }
	public boolean shouldAutoInclude() { return autoInclude; }

	public void finalizeDefn(QueryDefinition queryDefn)
	{
		if(tableName.equals(name))
			fromClauseExpr = tableName;
		else
			fromClauseExpr = tableName + " " + name;
	}

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("id");
		tableName = elem.getAttribute("table");

		criteria = elem.getAttribute("condition");
		if(criteria.length() == 0)
			criteria = null;

		String value = elem.getAttribute("auto-include");
		if(value != null && value.equals("yes"))
			autoInclude = true;

		value = elem.getAttribute("weight");
		if(value.length() > 0)
			ruleWeight = Integer.parseInt(value);
	}
}