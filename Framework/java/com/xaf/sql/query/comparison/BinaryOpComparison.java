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
import com.xaf.value.*;

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

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator statement, QueryCondition cond)
	{
        String retString = "";
		statement.addParam(cond.getValue());
        String bindExpression = cond.getBindExpression();

        if (bindExpression != null && bindExpression.length() > 0)
            retString = cond.getField().getWhereClauseExpr() + " "+ sqlExpr + " " + bindExpression;
        else
            retString = cond.getField().getWhereClauseExpr() + " "+ sqlExpr +" ?";
		return retString;
	}

}