package com.xaf.log;

import java.util.*;
import javax.servlet.http.*;

public class LogManager
{
	public static String MONITOR_ENTRY_FIELD_SEPARATOR = "\t";

	public static final String DEBUG_PAGE       = "xaf.debug.page";
	public static final String DEBUG_SQL        = "xaf.debug.sql";
	public static final String DEBUG_SECURITY   = "xaf.debug.security";

	public static final String MONITOR_PAGE     = "xaf.monitor.page";
	public static final String MONITOR_SECURITY = "xaf.monitor.security";
	public static final String MONITOR_SQL      = "xaf.monitor.sql";

	public static final String TRACE_PAGE       = "xaf.trace.page";

	public static void recordAccess(HttpServletRequest req, AppServerCategory cat, String objName, String id, long startTime)
	{
		if(cat == null) cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.MONITOR_PAGE);
		if(! cat.isInfoEnabled())
			return;

		long renderTime = (new Date().getTime()) - startTime;
		StringBuffer info = new StringBuffer();
		info.append(objName);
		info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		info.append(id);
		info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		info.append(renderTime);
		info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
		String queryString = req.getQueryString();
		if(queryString != null)
			info.append(req.getRequestURI() + "?" + queryString);
		else
			info.append(req.getRequestURI());

		cat.info(info.toString());
	}

}