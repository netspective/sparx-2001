package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;

import org.w3c.dom.*;

public class StandardReport implements Report
{
    static public final int REPORTFLAG_INITIALIZED      = 1;
    static public final int REPORTFLAG_HASPLACEHOLDERS = REPORTFLAG_INITIALIZED * 2;

    private String name;
	private ReportColumnsList columns = new ReportColumnsList();
    private ReportFrame frame = null;
    private ReportBanner banner = null;
	private int visibleColsCount = -1;
    private int flags;

    public StandardReport()
    {
    }

    public String getName() { return name; }
	public ReportFrame getFrame() { return frame; }
	public ReportBanner getBanner() { return banner; }

	public ReportColumnsList getColumns() { return columns; }
	public ReportColumn getColumn(int i) { return columns.getColumn(i); }
	public int getVisibleColsCount() { return visibleColsCount < 0 ? calcVisibleColsCount() : visibleColsCount; }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }
	public final void updateFlag(long flag, boolean set) { if(set) flags |= flag; else flags &= ~flag; }

	public void initialize(ResultSet rs, Element defnElem) throws SQLException
	{
		if(flagIsSet(REPORTFLAG_INITIALIZED)) return;

		ResultSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();

		columns.clear();
		for(int c = 1; c <= numColumns; c++)
		{
			ReportColumn colDefn = ReportColumnFactory.createReportColumn(rsmd, c);
            columns.add(colDefn);
		}

		if(defnElem != null)
			importFromXml(defnElem);

        calcVisibleColsCount();
		setFlag(REPORTFLAG_INITIALIZED);
	}

    private int calcVisibleColsCount()
    {
		visibleColsCount = 0;
		for(int c = 0; c < columns.size(); c++)
		{
			ReportColumn colDefn = columns.getColumn(c);
			if(! colDefn.flagIsSet(ReportColumn.COLFLAG_INVISIBLE))
				visibleColsCount++;

            if(colDefn.flagIsSet(ReportColumn.COLFLAG_HASPLACEHOLDERS))
                setFlag(this.REPORTFLAG_HASPLACEHOLDERS);
		}
        return visibleColsCount;
    }

    public void initialize(ReportColumn[] cols, Element defnElem)
    {
        if(cols != null)
        {
            for(int c = 0; c <= cols.length; c++)
                columns.add(cols[c]);
        }

		if(defnElem != null)
			importFromXml(defnElem);
    }

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("name");
		if(name.length() == 0)
			name = "default";

		String heading = elem.getAttribute("heading");
		if(heading.length() > 0)
		{
			if(frame == null) frame = new ReportFrame();
			frame.setHeading(heading);
		}

		NodeList children = elem.getChildNodes();
		int columnIndex = 0;

		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String childName = node.getNodeName();
			if(childName.equals("column"))
			{
				Element columnElem = (Element) node;

				String value = columnElem.getAttribute("index");
				if(value.length() > 0)
					columnIndex = Integer.parseInt(value);

                if(columns.size() <= columnIndex)
                {
                    String colType = columnElem.getAttribute("type");
                    if(colType.length() == 0)
                        colType = null;

                    ReportColumn column = ReportColumnFactory.createReportColumn(colType);
                    column.importFromXml(columnElem);
                    column.setColIndexInArray(columnIndex);
                    columns.add(column);
                }
                else
                {
    				ReportColumn column = columns.getColumn(columnIndex);
	    			column.importFromXml(columnElem);
                }

				columnIndex++;
			}
            else if(childName.equals("banner-item"))
            {
				Element bannerItemElem = (Element) node;

				String caption = bannerItemElem.getAttribute("caption");
				String url = bannerItemElem.getAttribute("url");

                if(banner == null)
                    banner = new ReportBanner();

                banner.addItem(new ReportBanner.Item(caption, url));
            }
		}
	}

    public String replaceOutputPatterns(ReportContext rc, Object[] rowData, String row)
    {
        int plhOpenPos = row.indexOf(ReportColumn.PLACEHOLDER_OPEN);
        if(plhOpenPos == -1)
            return row;

        int plhOpenLen = ReportColumn.PLACEHOLDER_OPEN.length();
        int plhCloseLen = ReportColumn.PLACEHOLDER_CLOSE.length();

        StringBuffer replacedIn = new StringBuffer(row);
        boolean done = false;

        while(! done)
        {
            int plhStartPos = plhOpenPos + plhOpenLen;
            int plhEndPos = plhStartPos;

            int strLen = replacedIn.length();
            while(plhEndPos < strLen && Character.isDigit(replacedIn.charAt(plhEndPos)))
                plhEndPos++;

            if(plhEndPos >= strLen)
            {
                done = true;
                continue;
            }

			try
			{
				int colIndex = Integer.parseInt(replacedIn.substring(plhStartPos, plhEndPos));
				String colValue = columns.getColumn(colIndex).getFormattedData(rc, rowData, false);
				replacedIn.replace(plhOpenPos, plhEndPos + plhCloseLen, colValue);
			}
			catch(NumberFormatException e)
			{
				replacedIn.replace(plhOpenPos, plhEndPos + plhCloseLen, "Invalid: " + replacedIn.substring(plhOpenPos, plhEndPos+1));
				done = true;
			}

            String newStr = replacedIn.toString();
            plhOpenPos = newStr.indexOf(ReportColumn.PLACEHOLDER_OPEN);
            if(plhOpenPos == -1)
                done = true;
        }

        return replacedIn.toString();
    }

	public void produceReport(Writer writer, ResultSet rs, ReportSkin skin) throws SQLException, IOException
	{
        ReportContext rc = new ReportContext(null, null, null, this, skin);
		skin.produceReport(writer, rc, rs);
	}

	public void produceReport(Writer writer, Object[][] data, ReportSkin skin) throws IOException
	{
        ReportContext rc = new ReportContext(null, null, null, this, skin);
		skin.produceReport(writer, rc, data);
	}
}