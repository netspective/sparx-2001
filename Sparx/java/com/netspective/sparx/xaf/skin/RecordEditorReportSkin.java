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
 * $Id: RecordEditorReportSkin.java,v 1.2 2003-01-24 14:15:22 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportFrame;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.util.value.SingleValueSource;

public class RecordEditorReportSkin extends HtmlReportSkin
{
    public RecordEditorReportSkin(boolean fullWidth)
    {
        super(fullWidth);
    }

    protected int getRowDecoratorAppendColsCount(ReportContext rc)
    {
        return (rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_DELETE) != 0 ? 1 : 0;
    }

    public void produceHeadingRowDecoratorAppend(Writer writer, ReportContext rc) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_DELETE) == 0)
            return;

        writer.write("<td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">");
        writer.write("&nbsp;");
        writer.write("</font></td><td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">&nbsp;&nbsp;</font></td>");
    }

    public void produceDataRowDecoratorAppend(Writer writer, ReportContext rc, int rowNum, Object[] rowData, boolean isOddRow) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_DELETE) == 0)
            return;

        SingleValueSource deleteRecordUrl = getReportFrame(rc).getRecordDeleteUrlFormat();
        if(deleteRecordUrl != null)
        {
            Report defn = rc.getReport();
            writer.write("<td><font " + dataFontAttrs + ">");
            writer.write("<a href='");
            writer.write(defn.replaceOutputPatterns(rc, rowNum, rowData, deleteRecordUrl.getValue(rc)));
            writer.write("'>");
            writer.write(deleteDataText.getValue(rc));
            writer.write("</a>");
            writer.write("</font></td><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</font></td>");
        }
    }

    protected int getRowDecoratorPrependColsCount(ReportContext rc)
    {
        return (rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) != 0 ? 1 : 0;
    }

    public void produceHeadingRowDecoratorPrepend(Writer writer, ReportContext rc) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) == 0)
            return;

        writer.write("<td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">");
        writer.write("&nbsp;");
        writer.write("</font></td><td "+ dataHdCellAttrs +"><font " + dataHdFontAttrs + ">&nbsp;&nbsp;</font></td>");
    }

    public void produceDataRowDecoratorPrepend(Writer writer, ReportContext rc, int rowNum, Object[] rowData, boolean isOddRow) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) == 0)
            return;

        SingleValueSource editRecordUrl = getReportFrame(rc).getRecordEditUrlFormat();
        if(editRecordUrl != null)
        {
            Report defn = rc.getReport();
            writer.write("<td><font " + dataFontAttrs + ">");
            writer.write("<a href='");
            writer.write(defn.replaceOutputPatterns(rc, rowNum, rowData, editRecordUrl.getValue(rc)));
            writer.write("'>");
            writer.write(editDataText.getValue(rc));
            writer.write("</a>");
            writer.write("</font></td><td><font " + dataFontAttrs + ">&nbsp;&nbsp;</font></td>");
        }
    }
}
