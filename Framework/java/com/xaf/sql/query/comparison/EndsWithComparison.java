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

public class EndsWithComparison extends BinaryOpComparison
{
	public EndsWithComparison()
	{
		super("ends-with", "ends with", "string");
	}

	public String getWhereCondExpr(SelectStmtGenerator statement, QueryCondition cond)
	{
		statement.addParam(new ConcatValueSource(cond.getValue(), "%", null));
		return cond.getField().getWhereClauseExpr() + " like ?";
	}
}