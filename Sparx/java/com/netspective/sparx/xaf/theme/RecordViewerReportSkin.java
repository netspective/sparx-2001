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
 * $Id: RecordViewerReportSkin.java,v 1.3 2003-03-21 05:51:04 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportBanner;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ReportFrame;
import com.netspective.sparx.xaf.skin.SkinFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class RecordViewerReportSkin extends com.netspective.sparx.xaf.theme.HtmlReportSkin
{
    private String imgPath = null;

    public RecordViewerReportSkin(boolean fullWidth)
    {
        super(fullWidth);
		clearFlag(HTMLFLAG_SHOW_HEAD_ROW | HTMLFLAG_ADD_ROW_SEPARATORS);
    }

    public void produceHeadingExtras(Writer writer, ReportContext rc, ReportFrame frame) throws IOException
    {
        ArrayList items = frame.getItems();
        SingleValueSource addRecordUrl = frame.getRecordAddUrlFormat();
        if(addRecordUrl != null || (items!= null && items.size() > 0) )
        {
            if (imgPath == null)
            {
                Theme theme = SkinFactory.getInstance().getCurrentTheme(rc);
                imgPath = ((HttpServletRequest)rc.getRequest()).getContextPath() + theme.getCurrentStyle().getImagePath();
            }
            SingleValueSource addRecordCaption = frame.getRecordAddCaption();
            int colCount = 0;
            writer.write("<td nowrap>");
            writer.write("    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

            StringBuffer itemBuffer = new StringBuffer();
            for (int i=0; items != null && i < items.size(); i++)
            {
                if (i != 0)
                {
                    itemBuffer.append("            <td bgcolor=\"white\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"5\" height=\"5\"></td>");
                    colCount++;
                }
                ReportFrame.Item item = (ReportFrame.Item) items.get(i);
                SingleValueSource itemUrl = item.getUrl();
                SingleValueSource itemCaption = item.getCaption();
                SingleValueSource itemIcon = item.getIcon();
                if (itemIcon != null)
                {
                    // icon for this item is defined so use the passed in image INSTEAD of using the CSS based background image
                    itemBuffer.append("            <td class=\"panel-frame-action-item-output\"><img src=\"" + itemIcon.getValue(rc) + "\" height=\"14\" width=\"17\" border=\"0\"></td>");
                    colCount++;
                }
                else
                {
                    itemBuffer.append("            <td class=\"panel-frame-action-item-output\" width=\"17\"><img src=\"" + imgPath +
                        "/panel/output/spacer.gif\" alt=\"\" height=\"14\" width=\"17\" border=\"0\"></td>");
                    colCount++;
                }
                itemBuffer.append("            <td class=\"panel-frame-action-box-output\">" +
                        "<a class=\"panel-frame-action-output\" href=\""+ itemUrl.getValue(rc) + "\">&nbsp;" +
                        itemCaption.getValue(rc) + "&nbsp;</a></td>");
                colCount++;
            }
            if (addRecordUrl != null)
            {
                if (items != null && items.size() > 0)
                {
                    itemBuffer.append("            <td bgcolor=\"white\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"5\" height=\"5\"></td>");
                    colCount++;
                }
                itemBuffer.append("            <td class=\"panel-frame-action-add-output\" width=\"17\"><img src=\"" + imgPath +
                        "/panel/output/spacer.gif\" alt=\"\" height=\"14\" width=\"17\" border=\"0\"></td>");
                colCount++;
                itemBuffer.append("            <td class=\"panel-frame-action-box-output\" nowrap><a class=\"panel-frame-action-output\" " +
                        "href=\"" + addRecordUrl.getValue(rc) + "\">&nbsp;" + (addRecordCaption != null ? addRecordCaption.getValue(rc) : "Add") +
                        "&nbsp;</a></td>");
                colCount++;
            }
            writer.write("        <tr>\n");
            writer.write("            <td bgcolor=\"white\" width=\"100%\" colspan=\"" + colCount + "\">" +
                    "<img src=\"" + imgPath + "/login/spacer.gif\" height=\"5\"></td>\n");
            /*
            writer.write("            <td bgcolor=\"white\" width=\"17\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"17\" height=\"5\"></td>");
            writer.write("            <td bgcolor=\"white\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"100%\" height=\"5\"></td>");
            writer.write("            <td bgcolor=\"white\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"5\" height=\"5\"></td>");
            writer.write("            <td bgcolor=\"white\" width=\"17\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"17\" height=\"5\"></td>");
            writer.write("            <td bgcolor=\"white\"><img src=\"" + imgPath + "/login/spacer.gif\" width=\"5\" height=\"5\"></td>");
            */
            writer.write("        </tr>\n");
            if (itemBuffer.length() > 0)
            {
                writer.write("        <tr>\n");
                writer.write(itemBuffer.toString());
                writer.write("        </tr>\n");
            }


            writer.write("        </tr>  ");
            writer.write("    </table>");
            writer.write("</td>");
        }
    }

    /**
     * This method is overidden because a record viewer does not show a banner (only the record editor does)
     */
    public ReportBanner getReportBanner(ReportContext rc)
    {
        return null;
    }

    protected int getRowDecoratorPrependColsCount(ReportContext rc)
    {
        return (rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) != 0 ? 1 : 0;
    }

    public void produceHeadingRowDecoratorPrepend(Writer writer, ReportContext rc) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) == 0)
            return;

        writer.write("<td class=\"report-alternative\" nowrap width=\"10\"></td>");
    }

    public void produceDataRowDecoratorPrepend(Writer writer, ReportContext rc, int rowNum, Object[] rowData, boolean isOddRow) throws IOException
    {
        if((rc.getFrameFlags() & ReportFrame.RPTFRAMEFLAG_HAS_EDIT) == 0)
            return;

        SingleValueSource editRecordUrl = getReportFrame(rc).getRecordEditUrlFormat();
        if(editRecordUrl != null)
        {
            Theme theme = SkinFactory.getInstance().getCurrentTheme(rc);
            String imgPath = ((HttpServletRequest)rc.getRequest()).getContextPath() + theme.getCurrentStyle().getImagePath();

            Report defn = rc.getReport();
            writer.write("<td " + (isOddRow ? "class=\"report\"" : "class=\"report-alternative\"") + " width=\"10\">");
            writer.write("<a href='");
            writer.write(defn.replaceOutputPatterns(rc, rowNum, rowData, editRecordUrl.getValue(rc)));
            writer.write("'>");
            writer.write("<img src=\"" + imgPath + "/panel/output/content-action-edit.gif\" " +
                    "alt=\"\" height=\"10\" width=\"10\" border=\"0\">");
            writer.write("</a></td>");
        }
    }
}
