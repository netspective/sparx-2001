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
 * $Id: ReportColumnFactory.java,v 1.3 2003-04-30 20:24:50 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xaf.report;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.report.column.CheckBoxFieldColumn;
import com.netspective.sparx.xaf.report.column.DecimalColumn;
import com.netspective.sparx.xaf.report.column.GeneralColumn;
import com.netspective.sparx.xaf.report.column.NumericColumn;
import com.netspective.sparx.xaf.report.column.ClobSummaryColumn;
import com.netspective.sparx.xaf.report.column.ClobStreamColumn;
import com.netspective.sparx.xif.SchemaDocument;

public class ReportColumnFactory implements Factory
{
    static private Map columnClasses = new HashMap();
    static private Map formats = new HashMap();

    static
    {
        NumberFormat plainFmt = (NumberFormat) NumberFormat.getNumberInstance().clone();
        plainFmt.setGroupingUsed(false);
        formats.put("plain", plainFmt);
        formats.put("general", NumberFormat.getNumberInstance());
        formats.put("decimal", DecimalFormat.getNumberInstance());
        formats.put("currency", NumberFormat.getCurrencyInstance());
        formats.put("percentage", NumberFormat.getPercentInstance());
        formats.put("date", DateFormat.getDateInstance());
        formats.put("datetime", DateFormat.getDateTimeInstance());
        formats.put("time", DateFormat.getInstance());

        columnClasses.put("default", GeneralColumn.class);
        columnClasses.put("numeric", NumericColumn.class);
        columnClasses.put("decimal", DecimalColumn.class);
        columnClasses.put("checkbox", CheckBoxFieldColumn.class);

	// Custom Report Columns - These might not be part of the main Sparx distribution
	columnClasses.put("clob-summary", ClobSummaryColumn.class);
	columnClasses.put("clob-detail", ClobStreamColumn.class);
    }

    public static void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Report Columns");
        factoryElem.setAttribute("class", ReportColumnFactory.class.getName());
        for(Iterator i = columnClasses.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("report-column");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((Class) entry.getValue()).getName());
            factoryElem.appendChild(childElem);
        }

        factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Report Column Formats");
        factoryElem.setAttribute("class", ReportColumnFactory.class.getName());
        for(Iterator i = formats.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("report-column-format");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", (entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }
    }

    public static ReportColumn createReportColumn(String type)
    {
        Class rcClass = (Class) columnClasses.get(type == null ? "default" : type);
        try
        {
            if(rcClass != null)
                return (ReportColumn) rcClass.newInstance();
            else
            {
                try
                {
                    // see if the type is a class name instead
                    rcClass = Class.forName(type);
                    return (ReportColumn) rcClass.newInstance();
                }
                catch(ClassNotFoundException cnfe)
                {
                    return null;
                }
            }
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static ReportColumn createReportColumn()
    {
        return createReportColumn(null);
    }

    public static ReportColumn createReportColumn(ResultSetMetaData rsmd, int resultSetColIndex) throws SQLException
    {
        ReportColumn column = null;

        int dataType = rsmd.getColumnType(resultSetColIndex);
        switch(dataType)
        {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.BIGINT:
            case Types.TINYINT:
            case Types.BIT:
                column = new NumericColumn();
                break;

            case Types.FLOAT:
            case Types.REAL:
                column = new DecimalColumn();
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                if(rsmd.getScale(resultSetColIndex) > 0)
                    column = new DecimalColumn();
                else
                    column = new NumericColumn();
                break;

            default:
                column = new GeneralColumn();
                break;
        }

        column.setColIndexInArray(resultSetColIndex - 1);
        column.setHeading(SchemaDocument.sqlIdentifierToText(rsmd.getColumnName(resultSetColIndex), true));
        column.setDataType(dataType);
        column.setWidth(rsmd.getColumnDisplaySize(resultSetColIndex));

        return column;
    }

    public static void addFormat(String fmtSpec, Format fmt)
    {
        formats.put(fmtSpec, fmt);
    }

    public static Format getFormat(String fmtSpec)
    {
        return (Format) formats.get(fmtSpec);
    }
}
