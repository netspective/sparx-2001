package com.xaf.sql;

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

public class ResultSetScrollState
{
	private ResultSet resultSet;
	private boolean resultSetScrollable;
	private int rowsPerPage;         // used for both scrollable and non-scrollable result sets
	private int activePage;          // used for both scrollable and non-scrollable
	private int activePageRowStart;  // used for both scrollable and non-scrollable
	private int totalPages;          // used only for scrollable
	private int totalRows;           // used only for scrollable
	private boolean haveMoreRows;    // used only for non-scrollable
	private int rowsProcessed;       // used only for non-scrollable
	private boolean reachedEndOnce;  // used only for non-scrollable

    public ResultSetScrollState(ResultSet rs, int rowsPerPage) throws SQLException
    {
		this.resultSet = rs;
		this.rowsPerPage = rowsPerPage;
		this.resultSetScrollable = rs != null ? (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) : false;
		this.totalRows = -1;
		this.totalPages = -1;
		this.activePage = 1;
		this.activePageRowStart = 1;
		this.rowsProcessed = 0;
		this.haveMoreRows = true;
		this.reachedEndOnce = false;

		if(resultSetScrollable && resultSet != null)
		{
			rs.last();

			totalRows = rs.getRow();
			totalPages = totalRows / rowsPerPage;
			if((totalPages * rowsPerPage) < totalRows)
				totalPages++;

			rs.first();
		}
    }

	public final ResultSet getResultSet() { return resultSet; }
	public final int getActivePage() { return activePage; }
	public final int getActivePageRowStart() { return activePageRowStart; }
	public final int getRowsPerPage() { return rowsPerPage; }
	public final int getTotalRows() { return totalRows; }
	public final int getTotalPages() { return totalPages; }
	public final int getRowsProcessed() { return rowsProcessed; }
	public final boolean isScrollable() { return resultSetScrollable; }

	public final void accumulateRowsProcessed(int rowsProcessed)
	{
		if(! resultSetScrollable && ! reachedEndOnce)
			this.rowsProcessed += rowsProcessed;
	}

	public final void setNoMoreRows()
	{
		if(! resultSetScrollable)
		{
			haveMoreRows = false;
			reachedEndOnce = true;
		}
	}

	public final boolean hasMoreRows() throws SQLException
	{
		return resultSetScrollable ? (! resultSet.isAfterLast()) : haveMoreRows;
	}

	public void scrollToActivePage() throws SQLException
	{
		activePageRowStart = ((activePage - 1) * rowsPerPage) + 1;
   		resultSet.absolute(activePageRowStart);
        resultSet.previous();
	}

	public void scrollToActivePage(ResultSet rs) throws SQLException
	{
		if(resultSetScrollable)
			throw new RuntimeException("No need to call scrollToActivePage(ResultSet rs) for scrollable cursors. Call scrollToActivePage() instead.");

		resultSet = rs;
		activePageRowStart = ((activePage - 1) * rowsPerPage);
		haveMoreRows = true;

		if(activePageRowStart > 1)
		{
			int atRow = 1;
			while(resultSet.next())
			{
				if(atRow >= activePageRowStart)
					break;

				atRow++;
			}
		}
	}

	public void setPage(int newPage)
	{
		activePage = newPage;
		if(activePage < 1)
			activePage = 1;
		else if(resultSetScrollable)
		{
			if(activePage > totalPages)
				activePage = totalPages;
		}
	}

	public void setPageDelta(int delta)
	{
		setPage(activePage + delta);
	}

    public void close() throws SQLException
    {
        Statement stmt = resultSet.getStatement();
        Connection conn = stmt.getConnection();
        resultSet.close();
        stmt.close();
        if (conn.getAutoCommit() == true)
            conn.close();
    }

    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }
}