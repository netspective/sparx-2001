package com.xaf.sql.query.comparison;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.sql.query.*;

public class BinaryOpComparison implements SqlComparison
{
	private String name;
	private String caption;
	private String groupName;
	private String sqlExpr;

    public BinaryOpComparison(String name, String caption, String group)
    {
		this.name = name;
		this.caption = caption;
		this.groupName = group;
    }

    public BinaryOpComparison(String name, String caption, String group, String sqlExpr)
    {
		this.name = name;
		this.caption = caption;
		this.groupName = group;
		this.sqlExpr = sqlExpr;
    }

	public final String getName() { return name; }
	public final String getCaption() { return caption; }
	public final String getGroupName() { return groupName; }

	public String getWhereCondExpr(SelectStmtGenerator statement, QueryCondition cond)
	{
		statement.addParam(cond.getValue());
		return cond.getField().getWhereClauseExpr() + " "+ sqlExpr +" ?";
	}

}