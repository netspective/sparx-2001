/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: ResultSetScrollState.java,v 1.4 2002-03-23 21:43:36 snshah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ResultSetScrollState
{
    public final static int SCROLLTYPE_USERESULTSET  = 0;
    public final static int SCROLLTYPE_NONSCROLLABLE = 1;

    private Date lastAccessed;
    private ResultInfo resultInfo;
    private boolean resultSetScrollable;
    private int rowsPerPage;         // used for both scrollable and non-scrollable result sets
    private int activePage;          // used for both scrollable and non-scrollable
    private int activePageRowStart;  // used for both scrollable and non-scrollable
    private int totalPages;          // used only for scrollable
    private int totalRows;           // used only for scrollable
    private boolean haveMoreRows;    // used only for non-scrollable
    private int rowsProcessed;       // used only for non-scrollable
    private boolean reachedEndOnce;  // used only for non-scrollable

    public ResultSetScrollState(ResultInfo ri, int rowsPerPage, int scrollType) throws SQLException
    {
        ResultSet rs = ri.getResultSet();

        switch(scrollType)
        {
            case SCROLLTYPE_USERESULTSET:
                this.resultSetScrollable = rs != null ? (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) : false;
                break;

            case SCROLLTYPE_NONSCROLLABLE:
                this.resultSetScrollable = false;
        }

        this.resultInfo = ri;
        this.rowsPerPage = rowsPerPage;
        this.totalRows = -1;
        this.totalPages = -1;
        this.activePage = 1;
        this.activePageRowStart = 1;
        this.rowsProcessed = 0;
        this.haveMoreRows = true;
        this.reachedEndOnce = false;

        if(resultSetScrollable && resultInfo != null)
        {
            rs.last();

            totalRows = rs.getRow();
            totalPages = totalRows / rowsPerPage;
            if((totalPages * rowsPerPage) < totalRows)
                totalPages++;

            rs.first();
        }

        recordActivity();
    }

    public final Date getLastAccessed()
    {
        return lastAccessed;
    }

    public final void recordActivity()
    {
        lastAccessed = new Date();
    }

    /**
     * Return true if this scroll state was accessed within the timeout period, false if "inactive" based on timeout.
     * @param timeout number of milliseconds to use as timeout value
     */
    public final boolean isActive(int timeout)
    {
        Date current = new Date();
        return (current.getTime() - lastAccessed.getTime()) < timeout;
    }

    public final ResultSet getResultSet()
    {
        return resultInfo.getResultSet();
    }

    public final int getActivePage()
    {
        return activePage;
    }

    public final int getActivePageRowStart()
    {
        return activePageRowStart;
    }

    public final int getRowsPerPage()
    {
        return rowsPerPage;
    }

    public final int getTotalRows()
    {
        return totalRows;
    }

    public final int getTotalPages()
    {
        return totalPages;
    }

    public final int getRowsProcessed()
    {
        return rowsProcessed;
    }

    public final boolean isScrollable()
    {
        return resultSetScrollable;
    }

    public final void accumulateRowsProcessed(int rowsProcessed)
    {
        if(!resultSetScrollable && !reachedEndOnce)
            this.rowsProcessed += rowsProcessed;
    }

    public final void setNoMoreRows()
    {
        if(!resultSetScrollable)
        {
            haveMoreRows = false;
            reachedEndOnce = true;
        }
    }

    public final boolean hasMoreRows() throws SQLException
    {
        return resultSetScrollable ? (!resultInfo.getResultSet().isAfterLast()) : haveMoreRows;
    }

    public void scrollToActivePage() throws SQLException
    {
        recordActivity();

        activePageRowStart = ((activePage - 1) * rowsPerPage) + 1;
        ResultSet resultSet = resultInfo.getResultSet();
        resultSet.absolute(activePageRowStart);
        resultSet.previous();
    }

    public void scrollToActivePage(ResultInfo ri) throws SQLException
    {
        recordActivity();

        if(resultSetScrollable)
            throw new RuntimeException("No need to call scrollToActivePage(ResultSet rs) for scrollable cursors. Call scrollToActivePage() instead.");

        resultInfo = ri;
        activePageRowStart = ((activePage - 1) * rowsPerPage);
        haveMoreRows = true;

        ResultSet resultSet = ri.getResultSet();
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
        recordActivity();

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
        if(resultInfo != null)
            resultInfo.close();
        resultInfo = null;
    }

    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }
}