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
 * $Id: SelectChoicesList.java,v 1.3 2002-12-31 19:45:30 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.StandardReport;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.column.GeneralColumn;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.util.value.ValueContext;

public class SelectChoicesList
{
    static public Report selectChoicesReport = new StandardReport();

    static
    {
        ReportColumn[] columns = new ReportColumn[]
        {
            new GeneralColumn(0, "ID"),
            new GeneralColumn(1, "Caption")
        };
        selectChoicesReport.initialize(columns, null);
        selectChoicesReport.setFlag(StandardReport.REPORTFLAG_HIDE_HEADING);
    }

    private ArrayList valueList = new ArrayList();
    private HashMap valueMap = new HashMap();
    private SelectChoice selectedChoice;

    public void add(SelectChoice choice)
    {
        valueList.add(choice);
        valueMap.put(choice.value, choice);
    }

    public SelectChoice get(String value)
    {
        return (SelectChoice) valueMap.get(value);
    }

    public void clear()
    {
        valueList.clear();
        valueMap.clear();
        selectedChoice = null;
    }

    public String[] getCaptions()
    {
        String[] result = new String[valueList.size()];
        int vIndex = 0;
        for(Iterator i = valueList.iterator(); i.hasNext();)
        {
            SelectChoice choice = (SelectChoice) i.next();
            result[vIndex] = choice.getCaption();
            vIndex++;
        }
        return result;
    }

    public String[] getValues()
    {
        String[] result = new String[valueList.size()];
        int vIndex = 0;
        for(Iterator i = valueList.iterator(); i.hasNext();)
        {
            SelectChoice choice = (SelectChoice) i.next();
            result[vIndex] = choice.getValue();
            vIndex++;
        }
        return result;
    }

    public Iterator getIterator()
    {
        return valueList.iterator();
    }

    public void calcSelections(DialogContext dc, SelectField field)
    {
        // make everthing "unselected" by default
        Iterator i = valueList.iterator();
        while(i.hasNext())
            ((SelectChoice) i.next()).selected = false;

        if(field.isMulti())
        {
            String[] values = dc.getValues(field);
            if(values != null)
            {
                for(int v = 0; v < values.length; v++)
                {
                    SelectChoice choice = (SelectChoice) valueMap.get(values[v]);
                    if(choice != null)
                        choice.selected = true;
                }
            }
        }
        else
        {
            String value = dc.getValue(field);
            if(value != null)
            {
                SelectChoice choice = (SelectChoice) valueMap.get(value);
                if(choice != null)
                {
                    choice.selected = true;
                    selectedChoice = choice;
                }
            }
        }
    }

    public SelectChoice getSelectedChoice()
    {
        return selectedChoice;
    }

    public int size()
    {
        return valueList.size();
    }

    public Object[][] getChoicesForReport()
    {
        if(valueList.size() == 0)
            return null;

        Object[][] rows = new Object[valueList.size()][2];
        for(int row = 0; row < valueList.size(); row++)
        {
            SelectChoice choice = (SelectChoice) valueList.get(row);
            rows[row][0] = choice.getValue();
            rows[row][1] = choice.getCaption();
        }

        return rows;
    }

    public ReportContext getReportContext(ValueContext vc, Report report, ReportSkin skin)
    {
        return new ReportContext(vc, report, skin == null ? SkinFactory.getDefaultReportSkin() : skin);
    }

    public void renderChoicesHtml(Writer writer, ReportContext rc, String[] urlFormats, boolean isPopup) throws IOException
    {
        ReportContext.ColumnState[] state = rc.getStates();
        if(isPopup)
        {
            state[0].setUrl("javascript:opener.activeDialogPopup.populateControls(\"${0}\", \"${1}\")");
            state[1].setUrl("javascript:opener.activeDialogPopup.populateControls(\"${0}\", \"${1}\")");
        }
        if(urlFormats != null)
        {
            for(int i = 0; i < urlFormats.length; i++)
                state[i].setUrl(urlFormats[i]);
        }
        rc.produceReport(writer, getChoicesForReport());
    }
}
