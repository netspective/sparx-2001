package com.xaf.report;

import java.util.*;
import java.sql.*;

/**
 * Provide default so that new listeners can extend this one and not have to
 * worry about providing all of the method implementations.
 */

public class DefaultReportContextListener implements ReportContextListener
{
    public DefaultReportContextListener()
    {
    }

	public void makeReportStateChanges(ReportContext rc, ResultSet rs)
	{
	}

	public void makeReportStateChanges(ReportContext rc, Object[][] data)
	{
	}
}