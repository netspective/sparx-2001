package com.xaf.sql.query.comparison;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import com.xaf.sql.query.*;
import com.xaf.value.*;

public class ContainsComparison extends BinaryOpComparison
{
	public ContainsComparison()
	{
		super("contains", "contains", "string");
	}

	public String getWhereCondExpr(SelectStmtGenerator statement, QueryCondition cond)
	{
		statement.addParam(new ConcatValueSource(cond.getValue(), "%", "%"));
		return cond.getField().getWhereClauseExpr() + " like ?";
	}
}