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
import com.xaf.report.column.*;
import com.xaf.sql.*;
import com.xaf.value.*;

public class ReportContext extends ServletValueContext
{
	static public final String REQUESTATTRNAME_LISTENER = "ReportContext.DefaultListener";

	public class ColumnState
	{
		protected ReportColumn column;
		protected String heading;
		protected ColumnDataCalculator calc;
		protected String dialogFieldId;
		protected String dialogFieldValueTemplate;
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
					url = column.resolvePattern(column.getUrl().getValue(ReportContext.this));
			}

			heading = column.getHeading().getValue(ReportContext.this);

			if(column instanceof DialogFieldColumn)
			{
				DialogFieldColumn dfc = (DialogFieldColumn) column;
				dialogFieldId = dfc.getFieldId();
				dialogFieldValueTemplate = dfc.getFieldValue();
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

		public final String getHeading() { return heading; }
		public final String getOutputFormat() { return outputFormat; }
		public final String getUrl() { return url; }
		public final String getFieldId() { return dialogFieldId; }
		public final String getFieldValueTemplate() { return dialogFieldValueTemplate; }
	}

	private List listeners = new ArrayList();
	private ColumnState[] states;
    private Report reportDefn;
    private int calcsCount;
	private int visibleColsCount;
	private ReportSkin skin;
	private ResultSetScrollState scrollState;
	private int rowCurrent, rowStart, rowEnd;

    public NumberFormat generalNumberFmt;

    public ReportContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response, Report reportDefn, ReportSkin skin)
    {
        super(context, servlet, request, response);
        this.reportDefn = reportDefn;
		this.skin = skin;
        this.generalNumberFmt = NumberFormat.getNumberInstance();
		this.rowStart = 0;
		this.rowEnd = 0;
		this.rowCurrent = 0;
		this.visibleColsCount = -1; // calculate on first-call (could change)

		if(servlet instanceof ReportContextListener)
			listeners.add(servlet);

		Object listener = request.getAttribute(REQUESTATTRNAME_LISTENER);
		if(listener != null)
			listeners.add(listener);

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

	/**
	 * Returns a string useful for displaying a unique Id for this DialogContext
	 * in a log or monitor file.
	 */
	public String getLogId()
	{
		String result = reportDefn.toString();
		if(result == null)
			return Integer.toString(reportDefn.getColumns().size());
		return result;
	}

	public ReportContext(ValueContext vc, Report reportDefn, ReportSkin skin)
	{
		this(vc.getServletContext(), vc.getServlet(), vc.getRequest(), vc.getResponse(), reportDefn, skin);
	}

	public List getListeners() { return listeners; }
	public void addListener(ReportContextListener listener)
	{
		listeners.add(listener);
	}

	public final void setResultsScrolling(ResultSetScrollState scrollState)
	{
		this.scrollState = scrollState;
		this.rowCurrent = 0; // rowStart;
		this.rowStart = 0; // rowStart;
		this.rowEnd = rowStart + scrollState.getRowsPerPage(); //rowStart + pageSize;
		//this.pageSize = scrollState.getRowsPerPage(); //pageSize;
	}

    public final Report getReport() { return reportDefn; }
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

	public final ResultSetScrollState getScrollState() { return scrollState; }
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
}