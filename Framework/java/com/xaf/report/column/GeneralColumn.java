package com.xaf.report.column;

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
import java.text.*;
import java.sql.*;
import org.w3c.dom.*;

import com.xaf.report.*;

public class GeneralColumn implements ReportColumn
{
	static public final long COLFLAG_INVISIBLE = 1;
    static public final long COLFLAG_HASPLACEHOLDERS = COLFLAG_INVISIBLE * 2;
    static public final long COLFLAG_HASOUTPUTPATTERN = COLFLAG_HASPLACEHOLDERS * 2;
	static public final long COLFLAG_WRAPURL = COLFLAG_HASOUTPUTPATTERN * 2;
	static public final long COLFLAG_CUSTOMSTART = COLFLAG_WRAPURL * 2;

	static public final int ALIGN_LEFT   = 0;
	static public final int ALIGN_CENTER = 1;
	static public final int ALIGN_RIGHT  = 2;

    static public final String PLACEHOLDER_COLDATA = "{.}";
    static public final String PLACEHOLDER_OPEN = "{";
    static public final String PLACEHOLDER_CLOSE = "}";

	private int dataType;
	private int alignStyle;
	private int colIndexInArray;
    private int colIndexInResultSet;
	private String heading;
	private String url;
    private String calcCmd;
    private Format formatter;
    private String outputPattern;
	private int width;
	private long flags;

    public GeneralColumn()
    {
		this(-1, null, null);
	}

    public GeneralColumn(int colIndex, String colHeading)
    {
		this(colIndex, colHeading, null);
    }

    public GeneralColumn(int colIndex, String colHeading, String colURL)
    {
        flags = 0;
        colIndexInArray = colIndex;
        colIndexInResultSet = colIndex+1;
		setHeading(colHeading);
		setUrl(colURL);
        dataType = Types.VARCHAR;
        alignStyle = ALIGN_LEFT;
	}

	public final int getDataType() { return dataType; }
	public final void setDataType(int value) { dataType = value; }

	public final int getColIndexInResultSet() { return colIndexInResultSet; }
	public final int getColIndexInArray() { return colIndexInArray; }
	public final void setColIndexInArray(int value)
    {
        colIndexInArray = value;
        colIndexInResultSet = value + 1;
    }

	public final String getHeading() { return heading; }
	public final void setHeading(String value) { heading = value; }

	public final String getUrl() { return url; }
	public final void setUrl(String value)
    {
        url = value;
        if(url != null)
            setFlag(COLFLAG_WRAPURL);
        else
            clearFlag(COLFLAG_WRAPURL);
    }

	public final int getWidth() { return width; }
	public final void setWidth(int value) { width = value; }

	public final int getAlignStyle() { return alignStyle; }
	public final void setAlignStyle(int value) { alignStyle = value; }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }
	public final void updateFlag(long flag, boolean set) { if(set) flags |= flag; else flags &= ~flag; }

    public final String getCalcCmd() { return calcCmd; }
    public final void setCalcCmd(String value) { calcCmd = value; }

    public final Format getFormatter() { return formatter; }
    public void setFormatter(Format value) { formatter = value; }
    public void setFormat(String value) { formatter = ReportColumnFactory.getFormat(value); }

    public final String getOutput() { return outputPattern; }
    public final void setOutput(String value)
    {
        outputPattern = value;
        if(outputPattern != null)
        {
            outputPattern = value;
            setFlag(COLFLAG_HASOUTPUTPATTERN);
        }
		else
			clearFlag(COLFLAG_HASOUTPUTPATTERN);
    }

    public String resolvePattern(String srcStr)
    {
        // find all occurrences of ${.} and replace with ${x} where x is the col index (array)

        int findLoc = srcStr.indexOf(PLACEHOLDER_COLDATA);
        if(findLoc == -1)
            return srcStr;

        setFlag(COLFLAG_HASPLACEHOLDERS);

        String replacedIn = srcStr;
        String replaceWith = PLACEHOLDER_OPEN + colIndexInArray + PLACEHOLDER_CLOSE;
        while(findLoc >= 0)
        {
            StringBuffer sb = new StringBuffer(replacedIn);
            sb.replace(findLoc, findLoc + PLACEHOLDER_COLDATA.length(), replaceWith);
            replacedIn = sb.toString();
            findLoc = replacedIn.indexOf(PLACEHOLDER_COLDATA);
        }
        return replacedIn;
    }

	public String getFormattedData(ReportContext rc, Object[] rowData, boolean doCalc)
	{
        Object oData = rowData[getColIndexInArray()];
        String data = oData == null ? "" : oData.toString();
        if(doCalc)
        {
            ColumnDataCalculator calc = rc.getCalc(getColIndexInArray());
            if(calc != null)
                calc.addValue(data);
        }
		return data;
	}

	public String getFormattedData(ReportContext rc, ColumnDataCalculator calc)
	{
        if(calc != null)
        {
            if(formatter != null)
                return formatter.format(new Double(calc.getValue()));
            else
                return rc.generalNumberFmt.format(calc.getValue());
        }
        else
            return "";
	}

	public void importFromColumn(ReportColumn rc)
	{
		flags = rc.getFlags();

		setHeading(rc.getHeading());
		setUrl(rc.getUrl());
		setAlignStyle(rc.getAlignStyle());
		setWidth(rc.getWidth());
		setCalcCmd(rc.getCalcCmd());
		setFormatter(rc.getFormatter());
		setOutput(rc.getOutput());
	}

	public void importFromXml(Element elem)
	{
		String value = elem.getAttribute("heading");
		if(value.length() > 0)
			setHeading(value);

		value = elem.getAttribute("url");
		if(value.length() > 0)
			setUrl(value);

		value = elem.getAttribute("align");
		if(value.length() > 0)
		{
			if(value.equals("right"))
				setAlignStyle(ReportColumn.ALIGN_RIGHT);
			else if(value.equals("center"))
				setAlignStyle(ReportColumn.ALIGN_CENTER);
			else
				setAlignStyle(ReportColumn.ALIGN_LEFT);
		}

		value = elem.getAttribute("width");
		if(value.length() > 0)
			setWidth(Integer.parseInt(value));

		value = elem.getAttribute("display");
		if(value.length() > 0 && value.equals("no"))
			setFlag(ReportColumn.COLFLAG_INVISIBLE);

		value = elem.getAttribute("calc");
		if(value.length() > 0)
			setCalcCmd(value);

		value = elem.getAttribute("format");
		if(value.length() > 0)
			setFormat(value);

		value = elem.getAttribute("output");
		if(value.length() > 0)
			setOutput(value);
	}
}