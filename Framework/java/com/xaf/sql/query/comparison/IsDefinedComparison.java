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

public class IsDefinedComparison extends BinaryOpComparison
{
	public IsDefinedComparison()
	{
		super("is-defined", "is defined", "general");
	}

	public String getWhereCondExpr(SelectStmtGenerator statement, QueryCondition cond)
	{
		return cond.getField().getWhereClauseExpr() + " is not null";
	}
}
