package com.xaf.report;

import java.util.*;
import java.sql.*;

public interface ReportContextListener extends EventListener
{
    /**
     *  Fired after the report has retrieved data from a resultSet and is ready
	 *  to output the data.
     */
	public void makeReportStateChanges(ReportContext rc, ResultSet rs);

    /**
     *  Fired after the report has retrieved data from a resultSet and is ready
	 *  to output the data.
     */
	public void makeReportStateChanges(ReportContext rc, Object[][] data);
}