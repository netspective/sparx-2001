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
 * $Id: ListSource.java,v 1.5 2003-02-26 07:54:13 aye.thu Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.report.*;
import com.netspective.sparx.xaf.skin.SkinFactory;

public class ListSource implements ListValueSource, SingleValueSource
{
    private SelectChoicesList choices;
    private String[] values;
    protected String valueKey;

    public void initializeSource(String srcParams)
    {
        valueKey = srcParams;
    }

    public String getId()
    {
        return getClass().getName() + ":" + valueKey;
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return null;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        return choices;
    }

    // TODO: needs to be optimized to not grab the entire list each time -- very important performance
    public String getAdjacentCaptionForValue(ValueContext vc, String id)
    {
        SelectChoicesList scl = getSelectChoices(vc);
        if(scl != null)
        {
            SelectChoice choice = scl.get(id);
            return choice != null ? choice.getCaption() : null;
        }
        else
            return null;
    }

    public String[] getValues(ValueContext vc)
    {
        if(values != null)
            return values;

        SelectChoicesList scl = getSelectChoices(vc);
        if(scl != null)
            return scl.getCaptions();

        return null;
    }

    public void setChoices(SelectChoicesList choices)
    {
        this.choices = choices;
    }

    public void setValues(String[] values)
    {
        this.values = values;
    }

    /* implemenations for SingleValueSource interface */
    public String getValue(ValueContext vc)
    {
        return (String) getObjectValue(vc);
    }

    public Object getObjectValue(ValueContext vc)
    {
        String[] vals = getValues(vc);
        if(vals != null)
            return vals[0];
        else
            return null;
    }

    public int getIntValue(ValueContext vc)
    {
        return ((Integer) getObjectValue(vc)).intValue();
    }

    public double getDoubleValue(ValueContext vc)
    {
        return ((Double) getObjectValue(vc)).doubleValue();
    }

    public String getValueOrBlank(ValueContext vc)
    {
        String value = getValue(vc);
        return value == null ? "" : value;
    }

    public boolean supportsSetValue()
    {
        return false;
    }

    public void setValue(ValueContext vc, Object value)
    {
    }

    public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
    {
    }

    public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
    {
    }

    public void setValue(ValueContext vc, String value)
    {
    }

    public Report getReport()
    {
        return SelectChoicesList.selectChoicesReport;
    }

    public ReportContext getReportContext(ValueContext vc, ReportSkin skin)
    {
        return new ReportContext(vc, getReport(), skin == null ? SkinFactory.getInstance().getDefaultReportSkin(vc) : skin);
    }

    public void renderItemsHtml(ValueContext vc, Writer writer, String[] urlFormats, ReportSkin skin, boolean isPopup) throws IOException
    {
        SelectChoicesList scl = getSelectChoices(vc);
        if(scl != null)
        {
            ReportContext rc = scl.getReportContext(vc, getReport(), skin);
            scl.renderChoicesHtml(writer, rc, urlFormats, isPopup);
        }
        else
            writer.write("No choices.");
    }
}