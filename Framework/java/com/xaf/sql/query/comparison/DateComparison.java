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

public class DateComparison extends BinaryOpComparison
{
	private String sqlExpr;
	public DateComparison(String nameCaption, String sqlExpr)
	{
		super(nameCaption, nameCaption, "date", sqlExpr);
		this.sqlExpr = sqlExpr;
	}

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator statement, QueryCondition cond)
	{
		//statement.addParam(new ConcatValueSource(cond.getValue(), "'", "'"));
		statement.addParam(new ConcatValueSource(cond.getValue(), null, null));
        String retString = "";
        String bindExpression = cond.getBindExpression();
        if (bindExpression != null && bindExpression.length() > 0)
            retString = " TO_DATE(" + cond.getField().getWhereClauseExpr() + " , 'MM/DD/YYYY')" + sqlExpr + " TO_DATE(" + bindExpression + ",'MM/DD/YYYY')";
        else
            retString = cond.getField().getWhereClauseExpr() + " like ?";
		return retString;
	}
}