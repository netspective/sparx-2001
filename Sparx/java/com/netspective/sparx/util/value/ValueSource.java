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
 * $Id: ValueSource.java,v 1.3 2002-12-26 19:32:09 shahid.shah Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.report.*;
import com.netspective.sparx.xaf.report.column.GeneralColumn;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;

abstract public class ValueSource implements SingleValueSource
{
    static public final String BLANK_STRING = "";

    protected Report report;
    protected String valueKey;

    public String getId()
    {
        return getClass().getName() + ":" + valueKey;
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return null;
    }

    public void initializeSource(String srcParams)
    {
        valueKey = srcParams;
    }

    abstract public String getValue(ValueContext vc);

    public Object getObjectValue(ValueContext vc)
    {
        return getValue(vc);
    }

    public int getIntValue(ValueContext vc)
    {
        return Integer.parseInt(getValue(vc));
    }

    public double getDoubleValue(ValueContext vc)
    {
        return Double.parseDouble(getValue(vc));
    }

    public String getValueOrBlank(ValueContext vc)
    {
        String value = getValue(vc);
        return value == null ? BLANK_STRING : value;
    }

    public boolean supportsSetValue()
    {
        return false;
    }

    public void setValue(ValueContext vc, Object value)
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, Object)");
    }

    public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
    {
        switch(storeType)
        {
            case RESULTSET_STORETYPE_SINGLECOLUMN:
                setValue(vc, StatementManager.getResultSetSingleColumn(rs));
                break;

            case RESULTSET_STORETYPE_RESULTSET:
                setValue(vc, rs);
                break;

            case RESULTSET_STORETYPE_SINGLEROWMAP:
                setValue(vc, StatementManager.getResultSetSingleRowAsMap(rs));
                break;

            case RESULTSET_STORETYPE_SINGLEROWFORMFLD:
                throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSet, int) does not support storeType RESULTSET_STORETYPE_SINGLEROWFORMFLD");

            case RESULTSET_STORETYPE_MULTIROWMAP:
                setValue(vc, StatementManager.getResultSetRowsAsMapArray(rs));
                break;

            case RESULTSET_STORETYPE_SINGLEROWARRAY:
                setValue(vc, StatementManager.getResultSetSingleRowAsArray(rs));
                break;

            case RESULTSET_STORETYPE_MULTIROWMATRIX:
                setValue(vc, StatementManager.getResultSetRowsAsMatrix(rs));
                break;
        }
    }

    public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
    {
        switch(storeType)
        {
            case RESULTSET_STORETYPE_SINGLECOLUMN:
                setValue(vc, StatementManager.getResultSetSingleColumn(data));
                break;

            case RESULTSET_STORETYPE_RESULTSET:
                throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSetMetaData, Object[][], int) does not support RESULTSET_STORETYPE_RESULTSET (because ResultSet has already been exhausted/run).");

            case RESULTSET_STORETYPE_SINGLEROWMAP:
                setValue(vc, StatementManager.getResultSetSingleRowAsMap(rsmd, data));
                break;

            case RESULTSET_STORETYPE_SINGLEROWFORMFLD:
                throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSet, int) does not support storeType RESULTSET_STORETYPE_SINGLEROWFORMFLD (use DialogFieldValue.setValue instead)");

            case RESULTSET_STORETYPE_MULTIROWMAP:
                setValue(vc, StatementManager.getResultSetRowsAsMapArray(rsmd, data));
                break;

            case RESULTSET_STORETYPE_SINGLEROWARRAY:
                setValue(vc, (data.length > 0 ? data[0] : null));
                break;

            case RESULTSET_STORETYPE_MULTIROWMATRIX:
                setValue(vc, data);
                break;
        }
    }

    public void setValue(ValueContext vc, String value)
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, String)");
    }

    public Report getReport()
    {
        return ListSource.selectChoicesReport;
    }

    public ReportContext getReportContext(ValueContext vc, ReportSkin skin)
    {
        return new ReportContext(vc, getReport(), skin == null ? SkinFactory.getDefaultReportSkin() : skin);
    }

    public void renderChoicesHtml(ValueContext vc, Writer writer, String[] urlFormats, ReportSkin skin, boolean isPopup) throws IOException
    {
        if(this instanceof ListValueSource)
        {
            SelectChoicesList scl = ((ListValueSource) this).getSelectChoices(vc);
            if(scl != null)
            {
                ReportContext rc = getReportContext(vc, skin);
                rc.produceReport(writer, scl.getChoicesForReport());
            }
            else
                writer.write("No choices.");
        }
    }
}