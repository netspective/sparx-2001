package com.xaf.sql.query;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.report.*;

public class QueryField
{
	private String name;
	private String caption;
	private String columnName;
	private String qualifiedColName;
	private String selectClauseExpr;
	private String selectClauseExprAndLabel;
	private String whereClauseExpr;
	private String join;
	private QueryJoin joinDefn;
	private ReportColumn reportColumn;
    private boolean hideDisplay;


    public QueryField()
    {
    }

	public String getName() { return name; }
	public String getCaption() { return caption; }

	public String getColumnName() { return columnName; }
	public String getColumnAlias() { return name; }
	public String getColumnLabel() { return caption == null ? name : caption; }
	public String getQualifiedColName() { return qualifiedColName; }
	public String getTableName() { return joinDefn != null ? joinDefn.getTableName() : null; }
	public String getTableAlias() { return joinDefn != null ? joinDefn.getName() : null; }
	public ReportColumn getReportColumn() { return reportColumn; }

	public String getSelectClauseExprAndLabel() { return hideDisplay ? null : selectClauseExprAndLabel; }
	public String getSelectClauseExpr() { return hideDisplay ? null : selectClauseExpr; }
	public String getWhereClauseExpr() { return whereClauseExpr; }
	public QueryJoin getJoin() { return joinDefn; }

	public void finalizeDefn(QueryDefinition queryDefn)
	{
		if(join != null && join.length() > 0)
		{
			joinDefn = queryDefn.getJoin(join);
			if(joinDefn == null)
			{
				queryDefn.addError("field-join", "join '"+ join +"' not found");
			}
			else
			{
				qualifiedColName = getTableAlias() + "." + columnName;
			}
		}
		else
			qualifiedColName = columnName;

        if(selectClauseExpr == null)
            selectClauseExpr = qualifiedColName;
        if(whereClauseExpr == null)
            whereClauseExpr = qualifiedColName;
        if(selectClauseExprAndLabel == null)
            selectClauseExprAndLabel = selectClauseExpr + " as \"" + getColumnLabel() + "\"";
	}

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("id");
		join = elem.getAttribute("join");

		columnName = elem.getAttribute("column");
		selectClauseExpr = elem.getAttribute("column-expr");
		if(selectClauseExpr.length() == 0)
			selectClauseExpr = null;

		whereClauseExpr = elem.getAttribute("where-expr");
		if(whereClauseExpr.length() == 0)
			whereClauseExpr = null;

		caption = elem.getAttribute("caption");
        String allowDisplay = elem.getAttribute("allow-display");
        if("no".equals(allowDisplay))
            hideDisplay = true;

		NodeList children = elem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String childName = node.getNodeName();
			if(childName.equals("report"))
			{
				Element report = (Element) node;
				String type = report.getAttribute("type");
				if(type.length() == 0)
					type = "default";

				reportColumn = ReportColumnFactory.createReportColumn(type);
				if(reportColumn == null)
					throw new RuntimeException("Unable to create ReportColumn for type '"+type+"'");

				reportColumn.setHeading(caption);
				reportColumn.importFromXml(report);
			}
		}
	}
}