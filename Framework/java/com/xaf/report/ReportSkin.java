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

public interface ReportSkin
{
	public String getFileExtension();

	public void produceReport(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException;
	public void produceReport(Writer writer, ReportContext rc, Object[][] data) throws IOException;
}