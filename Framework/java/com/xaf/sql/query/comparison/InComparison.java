package com.xaf.sql.query.comparison;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       A. Thu
 * @version 1.0
 */
import java.util.*;
import com.xaf.sql.query.*;
import com.xaf.value.*;

public class InComparison extends BinaryOpComparison
{

    public InComparison()
    {
        super("in", "in", "string");
    }

	public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator statement, QueryCondition cond)
	{

        select.setAlwaysDirty(true);
        int bindCount = 0;
        SingleValueSource vs = cond.getValue();
        if (vs instanceof ListValueSource)
        {
            String[] values = ((ListValueSource)vs).getValues(vc);
            if (values == null || values.length == 0)
            {
                return null;
            }
            bindCount = values.length;
    		statement.addParam((ListValueSource)vs);
        }
        else
        {
    		statement.addParam(vs);
        }


        StringBuffer retString = new StringBuffer(cond.getField().getWhereClauseExpr() + " in (");

        String bindExpression = cond.getBindExpression();
        if (bindExpression == null || bindExpression.length() == 0)
        {
            bindExpression = "?";
        }
        for (int i=0; i < bindCount; i++)
        {
            if (i != 0)
                retString.append(", ");
            retString.append(bindExpression);
        }

        retString.append(")");
		return retString.toString();
	}
}