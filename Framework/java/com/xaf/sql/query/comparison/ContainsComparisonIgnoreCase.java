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

public class ContainsComparisonIgnoreCase extends BinaryOpComparison
{
	public ContainsComparisonIgnoreCase()
	{
		super("contains-ignore-case", "contains-ignore-case", "string");
	}

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator statement, QueryCondition cond)
	{
		statement.addParam(new ConcatValueSource(cond.getValue(), "%", "%"));
        String retString = "";
        String bindExpression = cond.getBindExpression();
        if (bindExpression != null && bindExpression.length() > 0)
            retString = "UPPER(" + cond.getField().getWhereClauseExpr() + ") like UPPER(" + bindExpression + ")";
        else
            retString = cond.getField().getWhereClauseExpr() + " like ?";
		return retString;
	}
}