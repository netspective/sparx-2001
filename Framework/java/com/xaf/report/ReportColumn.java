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
import java.text.*;
import java.sql.*;

import org.w3c.dom.*;
import com.xaf.value.SingleValueSource;

public interface ReportColumn
{
	static public final long COLFLAG_HIDDEN = 1;
    static public final long COLFLAG_HASPLACEHOLDERS = COLFLAG_HIDDEN * 2;
    static public final long COLFLAG_HASOUTPUTPATTERN = COLFLAG_HASPLACEHOLDERS * 2;
	static public final long COLFLAG_WRAPURL = COLFLAG_HASOUTPUTPATTERN * 2;
	static public final long COLFLAG_CUSTOMSTART = COLFLAG_WRAPURL * 2;

	static public final int ALIGN_LEFT   = 0;
	static public final int ALIGN_CENTER = 1;
	static public final int ALIGN_RIGHT  = 2;

    static public final String PLACEHOLDER_COLDATA = "${.}";
    static public final String PLACEHOLDER_OPEN = "${";
    static public final String PLACEHOLDER_CLOSE = "}";

	public int getDataType();
	public void setDataType(int value);

	public int getColIndexInResultSet();
	public int getColIndexInArray();
	public void setColIndexInArray(int value);

	public SingleValueSource getHeading();
	public void setHeading(String value);

	public SingleValueSource getUrl();
	public void setUrl(String value);

	public int getWidth();
	public void setWidth(int value);

	public int getAlignStyle();
	public void setAlignStyle(int value);

	public long getFlags();
	public boolean flagIsSet(long flag);
	public void setFlag(long flag);
	public void clearFlag(long flag);
	public void updateFlag(long flag, boolean set);

    public String getCalcCmd();
    public void setCalcCmd(String value);

    public Format getFormatter();
    public void setFormatter(Format value);
    public void setFormat(String value);

    public String getOutput();
    public void setOutput(String value);

    public String resolvePattern(String srcStr);

	public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc);
	public String getFormattedData(ReportContext rc, ColumnDataCalculator calc);

	public void importFromColumn(ReportColumn rc);
	public void importFromXml(Element elem);
	public void finalizeContents(Report report);
}