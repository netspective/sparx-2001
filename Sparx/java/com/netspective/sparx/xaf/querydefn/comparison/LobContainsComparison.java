package com.netspective.sparx.xaf.querydefn.comparison;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.querydefn.QuerySelect;
import com.netspective.sparx.xaf.querydefn.SelectStmtGenerator;
import com.netspective.sparx.xaf.querydefn.QueryCondition;

/**
 * $Id: LobContainsComparison.java,v 1.1 2003-04-22 04:39:20 shahbaz.javeed Exp $
 */
public class LobContainsComparison extends BinaryOpComparison
{
    public LobContainsComparison()
    {
        super("lob-contains", "LOB contains", "string");
    }

    public String getWhereCondExpr(ValueContext vc, QuerySelect select, SelectStmtGenerator statement, QueryCondition cond)
    {
        statement.addParam(cond.getValue());
        String retString = "";

        String bindExpression = (null != cond.getBindExpression() && 0 < cond.getBindExpression().length()) ? cond.getBindExpression() : "?";
        retString = "DBMS_LOB.instr(" + cond.getField().getWhereClauseExpr() + ", " + bindExpression + ", 1, 1) > 0";

        return retString;
    }
}
