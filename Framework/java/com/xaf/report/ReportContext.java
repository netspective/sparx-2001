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

import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.value.*;

public class ReportContext implements ValueContext
{
    private Report reportDefn;
    private int calcsCount;
    private ColumnDataCalculator[] calcs;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;
	private ReportSkin skin;
	private int pageSize, rowCurrent, rowStart, rowEnd, rowsTotal;

    public NumberFormat generalNumberFmt;

    public ReportContext(HttpServletRequest request, HttpServletResponse response, ServletContext scontext, Report reportDefn, ReportSkin skin)
    {
		this.request = request;
		this.response = response;
		this.servletContext = scontext;
        this.reportDefn = reportDefn;
		this.skin = skin;
        this.generalNumberFmt = NumberFormat.getNumberInstance();
		this.rowStart = 0;
		this.rowEnd = 0;
		this.pageSize = 0;
		this.rowsTotal = 0;
		this.rowCurrent = 0;

        ReportColumnsList columns = reportDefn.getColumns();
        int columnsCount = columns.size();

        calcsCount = 0;
        calcs = new ColumnDataCalculator[columnsCount];
        for(int i = 0; i < columns.size(); i++)
        {
            ReportColumn column = columns.getColumn(i);
            String calcCmd = column.getCalcCmd();
            if(calcCmd != null)
            {
                calcs[i] = ColumnDataCalculatorFactory.createDataCalc(calcCmd);
                if(calcs[i] != null)
                    calcsCount++;
            }
        }
    }

	public ReportContext(DialogContext dc, Report reportDefn, ReportSkin skin)
	{
		this(dc.getRequest(), dc.getResponse(), dc.getServletContext(), reportDefn, skin);
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
	public final HttpServletRequest getRequest() { return request; }
	public final HttpServletResponse getResponse() { return response; }
	public final ReportSkin getSkin() { return skin; }

    public final ReportColumnsList getColumns() { return reportDefn.getColumns(); }
    public final int getCalcsCount() { return calcsCount; }
    public final ColumnDataCalculator[] getCalcs() { return calcs; }
    public final ColumnDataCalculator getCalc(int col) { return calcs[col]; }

	public final boolean scrollingResults() { return pageSize > 0; }
	public final boolean endOfPage() { rowCurrent++; return rowCurrent >= rowEnd; }
	public final int getRowStart() { return rowStart; }
	public final int getRowEnd() { return rowEnd; }
}