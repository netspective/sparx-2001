package com.netspective.sparx.xaf.querydefn.comparison;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.querydefn.QueryCondition;
import com.netspective.sparx.xaf.querydefn.QuerySelect;
import com.netspective.sparx.xaf.querydefn.SelectStmtGenerator;
import com.netspective.sparx.xaf.querydefn.comparison.BinaryOpComparison;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 21, 2003
 * Time: 1:25:45 PM
 * To change this template use Options | File Templates.
 */
public class IndexedLobContainsComparison extends BinaryOpComparison {

	// Constructors ...
	public IndexedLobContainsComparison() {
		super("indexed-lob-contains", "indexed clob contains", "string");
	}

	public String getWhereCondExpr(ValueContext valueContext, QuerySelect querySelect, SelectStmtGenerator statement, QueryCondition cond) {
		statement.addParam(cond.getValue());

		String bindExpression = (cond.getBindExpression() != null && cond.getBindExpression().length() > 0) ? cond.getBindExpression() : "?";

		String retString = "contains (" + cond.getField().getWhereClauseExpr() + ", " + bindExpression + ") > 0";

		return retString;
	}

}
