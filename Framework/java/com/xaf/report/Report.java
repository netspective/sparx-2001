package com.xaf.report;

/**
 * Title:        The eXtensible Application Framework
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

public interface Report
{
    public String getName();
	public ReportFrame getFrame();
	public ReportBanner getBanner();

	public ReportColumnsList getColumns();
	public ReportColumn getColumn(int i);

	public long getFlags();
	public boolean flagIsSet(long flag);
	public void setFlag(long flag);
	public void clearFlag(long flag);
	public void updateFlag(long flag, boolean set);

	public void initialize(ResultSet rs, Element defnElem) throws SQLException;
    public void initialize(ReportColumn[] cols, Element defnElem);
	public void importFromXml(Element elem);
    public String replaceOutputPatterns(ReportContext rc, Object[] rowData, String row);

	public void makeStateChanges(ReportContext rc, ResultSet rs);
	public void makeStateChanges(ReportContext rc, Object[][] data);
	/*
	public void produceReport(Writer writer, ResultSet rs, ReportContext rc) throws SQLException, IOException;
	public void produceReport(Writer writer, Object[][] data, ReportContext rc) throws IOException;
	public void produceReport(Writer writer, ResultSet rs, ReportSkin skin) throws SQLException, IOException;
	public void produceReport(Writer writer, Object[][] data, ReportSkin skin) throws IOException;
	*/
}