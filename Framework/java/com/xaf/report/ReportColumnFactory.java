package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.text.*;

import org.w3c.dom.*;
import com.xaf.report.column.*;

public class ReportColumnFactory
{
	static private Map columnClasses = new Hashtable();
    static private Map formats = new Hashtable();
    static boolean haveDefaultColClasses = false;
    static boolean haveDefaultFormats = false;

    static void setupDefaultFormats()
    {
        formats.put("general", NumberFormat.getNumberInstance());
        formats.put("decimal", DecimalFormat.getNumberInstance());
        formats.put("currency", NumberFormat.getCurrencyInstance());
        formats.put("percentage", NumberFormat.getPercentInstance());
        formats.put("date", DateFormat.getDateInstance());
        formats.put("datetime", DateFormat.getDateTimeInstance());
        formats.put("time", DateFormat.getInstance());
        haveDefaultFormats = true;
    }

	static void setupDefaultColClasses()
	{
		columnClasses.put("default", GeneralColumn.class);
		columnClasses.put("numeric", NumericColumn.class);
		columnClasses.put("decimal", DecimalColumn.class);
		haveDefaultColClasses = true;
	}

	public static void createCatalog(Element parent)
	{
		if(! haveDefaultColClasses)	setupDefaultColClasses();
        if(! haveDefaultFormats) setupDefaultFormats();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Report Columns");
		factoryElem.setAttribute("class", ReportColumnFactory.class.getName());
		for(Iterator i = columnClasses.entrySet().iterator(); i.hasNext(); )
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
		for(Iterator i = formats.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("report-column-format");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((Format) entry.getValue()).getClass().getName());
			factoryElem.appendChild(childElem);
		}
	}

	public static ReportColumn createReportColumn(String type)
	{
		if(! haveDefaultColClasses)
			setupDefaultColClasses();

		Class rcClass = (Class) columnClasses.get(type == null ? "default" : type);
		try
		{
			if(rcClass != null)
				return (ReportColumn) rcClass.newInstance();
			else
				return null;
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

		StringBuffer heading = new StringBuffer(rsmd.getColumnName(resultSetColIndex).replace('_', ' '));
		heading.setCharAt(0, Character.toTitleCase(heading.charAt(0)));

        column.setColIndexInArray(resultSetColIndex-1);
        column.setHeading(heading.toString());
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
        if(! haveDefaultFormats)
            setupDefaultFormats();

        Format format = (Format) formats.get(fmtSpec);
        if(format == null)
        {
            format = new DecimalFormat(fmtSpec);
        }
        return format;
    }
}