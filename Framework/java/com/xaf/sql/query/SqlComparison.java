package com.xaf.sql.query;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public interface SqlComparison
{
	public String getName();
	public String getCaption();
	public String getGroupName();
	public String getWhereCondExpr(SelectStmtGenerator statement, QueryCondition cond);
}