/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: QueryField.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnFactory;

public class QueryField
{
    private String name;
    private String caption;
    private String columnName;
    private String qualifiedColName;
    private String selectClauseExpr;
    private String selectClauseExprAndLabel;
    private String whereClauseExpr;
    private String orderByClauseExpr;
    private String join;
    private QueryJoin joinDefn;
    private ReportColumn reportColumn;
    private boolean hideDisplay;

    public QueryField()
    {
    }

    public String getName()
    {
        return name;
    }

    public String getCaption()
    {
        return caption;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public String getColumnAlias()
    {
        return name;
    }

    public String getColumnLabel()
    {
        return caption == null ? name : caption;
    }

    public String getQualifiedColName()
    {
        return qualifiedColName;
    }

    public String getTableName()
    {
        return joinDefn != null ? joinDefn.getTableName() : null;
    }

    public String getTableAlias()
    {
        return joinDefn != null ? joinDefn.getName() : null;
    }

    public ReportColumn getReportColumn()
    {
        return reportColumn;
    }

    public String getSelectClauseExprAndLabel()
    {
        return hideDisplay ? null : selectClauseExprAndLabel;
    }

    public String getSelectClauseExpr()
    {
        return hideDisplay ? null : selectClauseExpr;
    }

    public String getWhereClauseExpr()
    {
        return whereClauseExpr;
    }

    public String getOrderByClauseExpr()
    {
        return orderByClauseExpr;
    }

    public QueryJoin getJoin()
    {
        return joinDefn;
    }

    public void finalizeDefn(QueryDefinition queryDefn)
    {
        if(join != null && join.length() > 0)
        {
            joinDefn = queryDefn.getJoin(join);
            if(joinDefn == null)
            {
                queryDefn.addError("field-join", "join '" + join + "' not found");
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
        if(orderByClauseExpr == null)
            orderByClauseExpr = selectClauseExpr;
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

        orderByClauseExpr = elem.getAttribute("order-by-expr");
        if(orderByClauseExpr.length() == 0)
            orderByClauseExpr = null;

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
                    throw new RuntimeException("Unable to create ReportColumn for type '" + type + "'");

                reportColumn.setHeading(caption);
                reportColumn.importFromXml(report);
            }
        }
    }
}