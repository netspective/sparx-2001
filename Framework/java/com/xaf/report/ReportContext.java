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
import java.lang.reflect.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.value.*;

public class ReportContext implements ValueContext
{
	static public final String REPORTCTX_CALLBACKID_MAKE_SC = "ReportContext.onMakeStateChanges";

	public class ColumnState
	{
		protected ReportColumn column;
		protected ColumnDataCalculator calc;
		protected long flags;
		protected String outputFormat;
		protected String url;

		ColumnState(ReportColumn column)
		{
			this.column = column;

            String calcCmd = column.getCalcCmd();
            if(calcCmd != null)
                calc = ColumnDataCalculatorFactory.createDataCalc(calcCmd);

			flags = column.getFlags();

			if((flags & ReportColumn.COLFLAG_HIDDEN) == 0)
			{
				if(flagIsSet(ReportColumn.COLFLAG_HASOUTPUTPATTERN))
					outputFormat = column.resolvePattern(column.getOutput());

				if(flagIsSet(ReportColumn.COLFLAG_WRAPURL))
					url = column.resolvePattern(column.getUrl());
			}
		}

		public final boolean isVisible() { return (flags & ReportColumn.COLFLAG_HIDDEN) == 0 ? true : false; }
		public final boolean isHidden() { return (flags & ReportColumn.COLFLAG_HIDDEN) == 0 ? false : true; }
		public final boolean haveCalc() { return calc != null; }
		public final ColumnDataCalculator getCalc() { return calc; }

		public final long getFlags() { return flags; }
		public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
		public final void setFlag(long flag) { flags |= flag; }
		public final void clearFlag(long flag) { flags &= ~flag; }
		public final void updateFlag(long flag, boolean set) { if(set) flags |= flag; else flags &= ~flag; }

		public final String getOutputFormat() { return outputFormat; }
		public final String getUrl() { return url; }
	}

	private CallbackManager callbacks;
	private ColumnState[] states;
    private Report reportDefn;
    private int calcsCount;
	private int visibleColsCount;
	private ServletContext servletContext;
	private Servlet servlet;
	private ServletRequest request;
	private ServletResponse response;
	private ReportSkin skin;
	private int pageSize, rowCurrent, rowStart, rowEnd, rowsTotal;

    public NumberFormat generalNumberFmt;

    public ReportContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response, Report reportDefn, ReportSkin skin)
    {
		this.servletContext = context;
		this.servlet = servlet;
		this.request = request;
		this.response = response;
        this.reportDefn = reportDefn;
		this.skin = skin;
        this.generalNumberFmt = NumberFormat.getNumberInstance();
		this.rowStart = 0;
		this.rowEnd = 0;
		this.pageSize = 0;
		this.rowsTotal = 0;
		this.rowCurrent = 0;
		this.visibleColsCount = -1; // calculate on first-call (could change)

        ReportColumnsList columns = reportDefn.getColumns();
        int columnsCount = columns.size();

        calcsCount = 0;
		states = new ColumnState[columnsCount];
        for(int i = 0; i < columns.size(); i++)
        {
			ColumnState state = new ColumnState(columns.getColumn(i));
			if(state.haveCalc())
				calcsCount++;
			states[i] = state;
        }
    }

	public ReportContext(ValueContext vc, Report reportDefn, ReportSkin skin)
	{
		this(vc.getServletContext(), vc.getServlet(), vc.getRequest(), vc.getResponse(), reportDefn, skin);
		CallbackManager cm = vc.getCallbacks();
		if(cm != null)
			callbacks = (CallbackManager) cm.clone();
	}

	public CallbackManager getCallbacks()
	{
		return callbacks;
	}

	public CallbackInfo getCallbackMethod(String callbackId)
	{
		if(callbacks == null)
			return null;
		return callbacks.getCallbackMethod(callbackId);
	}

	public void setCallbackMethod(String callbackId, Object owner, String methodName, Class[] paramTypes)
	{
		if(callbacks == null) callbacks = new CallbackManager();
		callbacks.setCallbackMethod(callbackId, owner, methodName, paramTypes);
	}

	public final void setResultsScrolling(int rowStart, int pageSize)
	{
		this.rowCurrent = rowStart;
		this.rowStart = rowStart;
		this.rowEnd = rowStart + pageSize;
		this.pageSize = pageSize;
	}

    public final Report getReport() { return reportDefn; }
	public final ServletContext getServletContext() { return servletContext; }
	public final Servlet getServlet() { return servlet; }
	public final ServletRequest getRequest() { return request; }
	public final ServletResponse getResponse() { return response; }
	public final ReportSkin getSkin() { return skin; }

	public final ColumnState[] getStates() { return states; }
	public final ColumnState getState(int col) { return states[col]; }

    public final int getVisibleColsCount()
	{
		if(visibleColsCount != -1)
			return visibleColsCount;

        ReportColumnsList columns = reportDefn.getColumns();
        int columnsCount = columns.size();

		visibleColsCount = 0;
        for(int i = 0; i < columnsCount; i++)
        {
			if(states[i].isVisible())
				visibleColsCount++;
        }
		return visibleColsCount;
	}

    public final ReportColumnsList getColumns() { return reportDefn.getColumns(); }
    public final ColumnDataCalculator getCalc(int col) { return states[col].calc; }
    public final int getCalcsCount() { return calcsCount; }

	public final boolean scrollingResults() { return pageSize > 0; }
	public final boolean endOfPage() { rowCurrent++; return rowCurrent >= rowEnd; }
	public final int getRowStart() { return rowStart; }
	public final int getRowEnd() { return rowEnd; }

	public void produceReport(Writer writer, ResultSet rs) throws SQLException, IOException
	{
		reportDefn.makeStateChanges(this, rs);
		skin.produceReport(writer, this, rs);
	}

	public void produceReport(Writer writer, Object[][] data) throws IOException
	{
		reportDefn.makeStateChanges(this, data);
		skin.produceReport(writer, this, data);
	}

	public void callOnMakeStateChanges(ResultSet rs)
	{
		if(request == null)
			return;

		String methodName = (String) request.getAttribute(REPORTCTX_CALLBACKID_MAKE_SC);
		servletContext.log("Method: " + methodName);
		if(methodName != null)
		{
			try
			{
				CallbackInfo ci = new CallbackInfo(servlet, methodName, new Class[] { ReportContext.class, ResultSet.class });
				servletContext.log("Method: " + ci.haveMethod());
				if(ci.haveMethod())
					ci.invoke(new Object[] { this, rs });
			}
			catch(InvocationTargetException e)
			{
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e.toString());
			}
		}
	}

	public void callOnMakeStateChanges(Object[][] data)
	{
		if(request == null)
			return;

		String methodName = (String) request.getAttribute(REPORTCTX_CALLBACKID_MAKE_SC);
		servletContext.log("Method: " + methodName);
		if(methodName != null)
		{
			try
			{
				CallbackInfo ci = new CallbackInfo(servlet, methodName, new Class[] { ReportContext.class, Object[][].class });
				servletContext.log("Method: " + ci.haveMethod());
				if(ci.haveMethod())
					ci.invoke(new Object[] { this, data });
			}
			catch(InvocationTargetException e)
			{
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e.toString());
			}
		}
	}
}