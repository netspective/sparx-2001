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
 * $Id: TextReportSkin.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnsList;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.StandardReport;

public class TextReportSkin implements ReportSkin
{
    private String fileExtn;
    private String delimiter;
    private String textQualifier;
    private boolean firstRowContainsFieldNames;

    public TextReportSkin(String fileExtn, String delimiter, String textQualifier, boolean firstRowContainsFieldNames)
    {
        this.fileExtn = fileExtn;
        this.delimiter = delimiter;
        this.textQualifier = textQualifier;
        this.firstRowContainsFieldNames = firstRowContainsFieldNames;
    }

    public String getFileExtension()
    {
        return fileExtn;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public String getTextQualifier()
    {
        return textQualifier;
    }

    public boolean firstRowContainsFieldNames()
    {
        return firstRowContainsFieldNames;
    }

    public void produceReport(Writer writer, ReportContext rc, ResultSet rs, Object[][] data) throws SQLException, IOException
    {
        int startDataRow = 0;
        if(firstRowContainsFieldNames)
        {
            if(!rc.getReport().flagIsSet(StandardReport.REPORTFLAG_FIRST_DATA_ROW_HAS_HEADINGS))
            {
                produceHeadingRow(writer, rc, (Object[]) null);
            }
            else
            {
                if(rs != null)
                    produceHeadingRow(writer, rc, rs);
                else if(data.length > 0)
                {
                    produceHeadingRow(writer, rc, data[0]);
                    startDataRow = 1;
                }
            }
        }
        if(rs != null)
        {
            produceDataRows(writer, rc, rs);
        }
        else
        {
            produceDataRows(writer, rc, data, startDataRow);
        }
    }

    public void produceReport(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
    {
        produceReport(writer, rc, rs, null);
    }

    public void produceReport(Writer writer, ReportContext rc, Object[][] data) throws IOException
    {
        try
        {
            produceReport(writer, rc, null, data);
        }
        catch(SQLException e)
        {
            throw new RuntimeException("This should never happen.");
        }
    }

    public void writeText(Writer writer, String text, boolean delimiterColumn) throws IOException
    {
        if(textQualifier != null)
        {
            writer.write(textQualifier);
            if(text != null)
                writer.write(text);
            writer.write(textQualifier);
        }
        else if(text != null)
            writer.write(text);

        if(delimiterColumn)
            writer.write(delimiter);
        else
            writer.write("\n");
    }

    public void produceHeadingRow(Writer writer, ReportContext rc, Object[] headings) throws IOException
    {
        ReportColumnsList columns = rc.getColumns();
        ReportContext.ColumnState[] states = rc.getStates();
        int dataColsCount = columns.size();
        int lastDataCol = dataColsCount - 1;

        if(headings == null)
        {
            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn rcd = columns.getColumn(i);
                if(!states[i].isHidden())
                    writeText(writer, rcd.getHeading().getValue(rc), i < lastDataCol);
            }
        }
        else
        {
            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn rcd = columns.getColumn(i);
                if(states[i].isHidden())
                    continue;

                Object heading = headings[rcd.getColIndexInArray()];
                if(heading != null)
                    writeText(writer, heading.toString(), i < lastDataCol);
                else
                    writeText(writer, null, i < lastDataCol);
            }
        }
    }

    public void produceHeadingRow(Writer writer, ReportContext rc, ResultSet rs) throws IOException, SQLException
    {
        ReportColumnsList columns = rc.getColumns();
        ReportContext.ColumnState[] states = rc.getStates();
        int dataColsCount = columns.size();
        int lastDataCol = dataColsCount - 1;

        if(!rs.next()) return;

        for(int i = 0; i < dataColsCount; i++)
        {
            ReportColumn rcd = columns.getColumn(i);
            if(states[i].isHidden())
                continue;

            Object heading = rs.getString(rcd.getColIndexInResultSet());
            if(heading != null)
                writeText(writer, heading.toString(), i < lastDataCol);
            else
                writeText(writer, null, i < lastDataCol);
        }
    }

    /*
	  This method and the next one (produceDataRows with Object[][] data) are almost
	  identical except for their data sources (ResultSet vs. Object[][]). Be sure to
	  modify that method when this method changes, too
	*/

    public void produceDataRows(Writer writer, ReportContext rc, ResultSet rs) throws SQLException, IOException
    {
        Report defn = rc.getReport();
        ReportColumnsList columns = rc.getColumns();
        ReportContext.ColumnState[] states = rc.getStates();

        int rowsWritten = 0;
        int dataColsCount = columns.size();
        int lastDataCol = dataColsCount - 1;

        ResultSetMetaData rsmd = rs.getMetaData();
        int resultSetColsCount = rsmd.getColumnCount();

        while(rs.next())
        {
            // the reason why we need to copy the objects here is that
            // most JDBC drivers will only let data be ready one time; calling
            // the resultSet.getXXX methods more than once is problematic
            //
            Object[] rowData = new Object[resultSetColsCount];
            for(int i = 1; i <= resultSetColsCount; i++)
                rowData[i - 1] = rs.getObject(i);

            for(int i = 0; i < dataColsCount; i++)
            {
                int rowNum = rs.getRow();
                ReportColumn column = columns.getColumn(i);
                ReportContext.ColumnState state = states[i];

                if(!state.isHidden())
                    writeText(writer, column.getFormattedData(rc, rowNum, rowData, true), i < lastDataCol);
            }
        }
    }

    /*
	  This method and the previous one (produceDataRows with ResultSet) are almost
	  identical except for their data sources (Object[][] vs. ResultSet). Be sure to
	  modify that method when this method changes, too.
	*/

    public void produceDataRows(Writer writer, ReportContext rc, Object[][] data, int startDataRow) throws IOException
    {
        Report defn = rc.getReport();
        ReportColumnsList columns = rc.getColumns();
        ReportContext.ColumnState[] states = rc.getStates();

        int rowsWritten = 0;
        int dataColsCount = columns.size();
        int lastDataCol = dataColsCount - 1;

        for(int row = startDataRow; row < data.length; row++)
        {
            Object[] rowData = data[row];
            int rowNum = row - startDataRow;

            for(int i = 0; i < dataColsCount; i++)
            {
                ReportColumn column = columns.getColumn(i);
                ReportContext.ColumnState state = states[i];

                if(!state.isHidden())
                    writeText(writer, column.getFormattedData(rc, rowNum, rowData, true), i < lastDataCol);
            }
        }
    }
}