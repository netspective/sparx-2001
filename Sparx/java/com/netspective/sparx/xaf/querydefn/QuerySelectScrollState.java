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
 * $Id: QuerySelectScrollState.java,v 1.5 2002-12-03 15:49:56 aye.thu Exp $
 */

package com.netspective.sparx.xaf.querydefn;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnsList;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportFrame;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.StandardReport;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.ResultSetScrollState;
import com.netspective.sparx.util.value.ValueContext;

public class QuerySelectScrollState extends ResultSetScrollState
{
    private DatabaseContext dbContext;
    private ValueContext valueContext;
    private QuerySelect select;
    private Report reportDefn;
    private ReportSkin skin;
    private String dialogData;
    private boolean resultSetValid;

    private QueryDefinition.QueryFieldSortInfo sortFieldInfo;
    private int primaryOrderByColIndex = -1;

    public QuerySelectScrollState(DatabaseContext dc, ValueContext vc, QuerySelect select, int rowsPerPage, int scrollType) throws NamingException, SQLException
    {
        super(select.execute(dc, vc), rowsPerPage, scrollType);

        try
        {
            if(vc instanceof DialogContext)
                this.dialogData = ((DialogContext) vc).getAsXml();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        this.valueContext = vc;
        this.dbContext = dc;
        this.select = select;

        ResultSet rs = getResultSet();
        if(rs != null)
        {
            this.reportDefn = select.createReport();
            this.reportDefn.initialize(rs, null);

            String heading = select.getCaption();
            if(heading != null && heading.length() > 0)
            {
                ReportFrame frame = new ReportFrame();
                frame.setHeading(heading);
                this.reportDefn.setFrame(frame);
            }

            reportDefn.setFrame(select.getFrame());
            reportDefn.setBanner(select.getBanner());

            ReportColumnsList rcl = this.reportDefn.getColumns();
            List selectFields = select.getReportFields();

            List orderByFieldsList = select.getOrderBy();
            if(orderByFieldsList != null && orderByFieldsList.size() == 1)
            {
                QuerySortFieldRef sortRef = (QuerySortFieldRef) orderByFieldsList.get(0);
                QueryDefinition.QueryFieldSortInfo[] orderByFields = sortRef.getFields(vc);
                if(orderByFields != null && orderByFields.length == 1)
                    sortFieldInfo = orderByFields[0];
            }

            for(int i = 0; i < rcl.size(); i++)
            {
                QueryField field = (QueryField) selectFields.get(i);
                ReportColumn rc = field.getReportColumn();
                if(rc != null)
                    rcl.getColumn(i).importFromColumn(rc);
                if(sortFieldInfo != null && field == sortFieldInfo.getField())
                    primaryOrderByColIndex = i;
            }

            this.skin = SkinFactory.getReportSkin("report");
            this.resultSetValid = true;
        }
        else
            throw new SQLException("Unable to execute SQL: " + select.getErrorSql());
    }

    public QueryDefinition.QueryFieldSortInfo getSortFieldInfo()
    {
        return sortFieldInfo;
    }

    public int getPrimaryOrderByColIndex()
    {
        return primaryOrderByColIndex;
    }

    public QuerySelect getSelect()
    {
        return select;
    }

    public String getSelectSql()
    {
        return select.getSql(null);
    }

    public String getErrorMsg()
    {
        return select.getErrorSql();
    }

    public boolean isValid()
    {
        return resultSetValid;
    }

    public ReportSkin getSkin()
    {
        return skin;
    }

    public void populateData(DialogContext dc) throws ParserConfigurationException
    {
        try
        {
            if(dialogData != null)
                 dc.setFromXml(dialogData);
         }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    public void produceReport(Writer writer, DialogContext dc) throws SQLException, NamingException, IOException
    {
        if(!isScrollable())
            scrollToActivePage(select.execute(dbContext, dc));
        else
            scrollToActivePage();

        ReportContext rc = new ReportContext(select, dc, reportDefn, skin);
        rc.setResultsScrolling(this);

        if(primaryOrderByColIndex != -1)
            rc.getState(primaryOrderByColIndex).setFlag(sortFieldInfo.isDescending() ? ReportColumn.COLFLAG_SORTED_DESCENDING : ReportColumn.COLFLAG_SORTED_ASCENDING);
        rc.produceReport(writer, getResultSet());
    }
}