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
 * $Id: DebugField.java,v 1.2 2003-02-26 07:54:13 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumnsList;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.report.StandardReport;
import com.netspective.sparx.xaf.report.column.GeneralColumn;
import com.netspective.sparx.xaf.skin.SkinFactory;

public class DebugField extends DialogField
{
    static private Report report;
    static private ReportSkin reportSkin;

    public DebugField()
    {
        super("debug", "Debug");
    }

    //public boolean isVisible(DialogContext dc)
    //{
    //	String debug = dc.getRequest().getParameter("debug");
    //	return debug != null && debug.indexOf('d');
    //}

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        if(report == null)
        {
            report = new StandardReport();
            ReportColumnsList columns = report.getColumns();
            columns.add(new GeneralColumn(0, "Dialog Class"));
            columns.add(new GeneralColumn(1, "Context Class"));
            columns.add(new GeneralColumn(2, "Skin Class"));
            columns.add(new GeneralColumn(3, "Trans ID"));
            columns.add(new GeneralColumn(4, "Seq"));
            columns.add(new GeneralColumn(5, "Mode"));
            columns.add(new GeneralColumn(6, "Referer", "${.}"));
            columns.add(new GeneralColumn(7, "Data command"));
            columns.add(new GeneralColumn(8, "Populate tasks"));
            columns.add(new GeneralColumn(9, "Execute tasks"));

            reportSkin = SkinFactory.getInstance().getReportSkin(dc, "detail");
        }

        Object[][] data = new Object[][]
        {
            {
                dc.getDialog().getClass().getName(),
                dc.getClass().getName(),
                dc.getSkin().getClass().getName(),
                dc.getTransactionId(),
                new String("Run: " + dc.getRunSequence() + ", Exec: " + dc.getExecuteSequence()),
                new String("Active: " + dc.getActiveMode() + ", Next: " + dc.getNextMode()),
                dc.getOriginalReferer(),
                DialogContext.getDataCmdTextForCmdId(dc.getDataCommand()),
                dc.getDialog().getPopulateTasks() == null ? "none" : dc.getDialog().getPopulateTasks().getDebugHtml(dc),
                dc.getDialog().getExecuteTasks() == null ? "none" : dc.getDialog().getExecuteTasks().getDebugHtml(dc),
            }
        };

        ReportContext rc = new ReportContext(dc, report, reportSkin);
        rc.produceReport(writer, data);
    }
}