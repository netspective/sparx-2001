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
 * $Id: ReportContext.java,v 1.3 2002-12-26 19:38:31 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.report;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.netspective.sparx.xaf.report.column.DialogFieldColumn;
import com.netspective.sparx.xaf.sql.ResultSetScrollState;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.querydefn.QuerySelect;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

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
        protected String urlAnchorAttrs;

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

                if(flagIsSet(ReportColumn.COLFLAG_HAVEANCHORATTRS))
                    urlAnchorAttrs = column.resolvePattern(column.getUrlAnchorAttrs().getValue(ReportContext.this));
                else
                    urlAnchorAttrs = "";
            }

            heading = column.getHeading().getValue(ReportContext.this);

            if(column instanceof DialogFieldColumn)
            {
                DialogFieldColumn dfc = (DialogFieldColumn) column;
                dialogFieldId = dfc.getFieldId();
                dialogFieldValueTemplate = dfc.getFieldValue();
            }

            if(flagIsSet(ReportColumn.COLFLAG_HAVECONDITIONALS))
            {
                ReportColumnConditionalState[] conditionals = column.getConditionalStates();
                for(int i = 0; i < conditionals.length; i++)
                    conditionals[i].makeStateChanges(ReportContext.this, this);
            }
        }

        public final boolean isVisible()
        {
            return (flags & ReportColumn.COLFLAG_HIDDEN) == 0 ? true : false;
        }

        public final boolean isHidden()
        {
            return (flags & ReportColumn.COLFLAG_HIDDEN) == 0 ? false : true;
        }

        public final boolean haveCalc()
        {
            return calc != null;
        }

        public final ColumnDataCalculator getCalc()
        {
            return calc;
        }

        public final long getFlags()
        {
            return flags;
        }

        public final boolean flagIsSet(long flag)
        {
            return (flags & flag) == 0 ? false : true;
        }

        public final void setFlag(long flag)
        {
            flags |= flag;
        }

        public final void clearFlag(long flag)
        {
            flags &= ~flag;
        }

        public final void updateFlag(long flag, boolean set)
        {
            if(set) flags |= flag; else flags &= ~flag;
        }

        public final String getHeading()
        {
            return heading;
        }

        public final String getOutputFormat()
        {
            return outputFormat;
        }

        public final String getUrl()
        {
            return url;
        }

        public final String getUrlAnchorAttrs()
        {
            return urlAnchorAttrs;
        }

        public final String getFieldId()
        {
            return dialogFieldId;
        }

        public final String getFieldValueTemplate()
        {
            return dialogFieldValueTemplate;
        }

        public final void setHeading(String value)
        {
            heading = value;
        }

        public final void setOutputFormat(String value)
        {
            outputFormat = value;
        }

        public final void setUrl(String value)
        {
            url = value;

            if(value != null)
            {
                setFlag(ReportColumn.COLFLAG_WRAPURL);
                url = column.resolvePattern(value);
            }
            else
                url = null;
        }

        public final void setUrlAnchorAttrs(String value)
        {
            urlAnchorAttrs = value;
            if(value != null)
            {
                setFlag(ReportColumn.COLFLAG_HAVEANCHORATTRS);
                urlAnchorAttrs = column.resolvePattern(value);
            }
            else
                urlAnchorAttrs = "";

        }

        public final void setFieldId(String value)
        {
            dialogFieldId = value;
        }

        public final void setFieldValueTemplate(String value)
        {
            dialogFieldValueTemplate = value;
        }
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

    public ReportContext(ValueContext vc, Report reportDefn, ReportSkin skin)
    {
        this(vc.getServletContext(), vc.getServlet(), vc.getRequest(), vc.getResponse(), reportDefn, skin);
    }

    public ReportContext(QuerySelect select, DialogContext dc, Report reportDefn, ReportSkin skin)
    {
        this(dc.getServletContext(), dc.getServlet(), dc.getRequest(), dc.getResponse(), reportDefn, skin);
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

    public List getListeners()
    {
        return listeners;
    }

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

    public final Report getReport()
    {
        return reportDefn;
    }

    public final ReportSkin getSkin()
    {
        return skin;
    }

    public final ColumnState[] getStates()
    {
        return states;
    }

    public final ColumnState getState(int col)
    {
        return states[col];
    }

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

    public final ReportColumnsList getColumns()
    {
        return reportDefn.getColumns();
    }

    public final ColumnDataCalculator getCalc(int col)
    {
        return states[col].calc;
    }

    public final int getCalcsCount()
    {
        return calcsCount;
    }

    public final ResultSetScrollState getScrollState()
    {
        return scrollState;
    }

    public final boolean endOfPage()
    {
        rowCurrent++;
        return rowCurrent >= rowEnd;
    }

    public final int getRowStart()
    {
        return rowStart;
    }

    public final int getRowEnd()
    {
        return rowEnd;
    }

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