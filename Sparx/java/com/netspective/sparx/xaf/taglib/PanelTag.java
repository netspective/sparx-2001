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
 * $Id: PanelTag.java,v 1.1 2002-08-25 20:38:17 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.Servlet;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.xaf.report.ReportFrame;
import com.netspective.sparx.xaf.report.ReportBanner;

public class PanelTag extends TagSupport
{
    private String containerTableAttrs = "cellspacing=0 cellpadding=0 border=0";
    private String frameHdTableAttrs = "cellspacing=0 cellpadding=0 width='100%' border=0";
    private String frameHdRowAttrs = "bgcolor='#666666' height=15";
    private String frameHdRowSpacerAttrs = "bgcolor='#666666' height=2";
    private String frameHdCellAttrs = "bgcolor='#666666' width='40%'";
    private String frameHdInfoCellAttrs = "bgcolor='white' align='right'";
    private String frameHdFontAttrs = "face='tahoma,arial,helvetica' size=1 color=white";
    private String frameHdInfoFontAttrs = "face='tahoma,arial,helvetica' size=1";
    private String innerTableAttrs = "border=0 cellspacing=1 cellpadding=4 bgcolor='#666666' width='100%' ";
    private String bodyStartHtml = "<font face='tahoma,arial,helvetica' size=2>";
    private String bodyEndHtml = "</font>";
    private SingleValueSource frameHdTabImgSrcValueSource = ValueSourceFactory.getSingleValueSource("config-expr:${sparx.shared.images-url}/tabs/transparent-triangle.gif");
    private SingleValueSource frameHdSpacerImgSrcValueSource = ValueSourceFactory.getSingleValueSource("config-expr:${sparx.shared.images-url}/tabs/black-on-lgray/spacer.gif");

    private String heading;
    private String headingExtra;
    private String bodyColor = "white";

    public void release()
    {
        containerTableAttrs = "cellspacing=0 cellpadding=0 border=0";
        frameHdRowAttrs = "bgcolor='#666666' height=15";
        frameHdRowSpacerAttrs = "bgcolor='#666666' height=2";
        frameHdCellAttrs = "bgcolor='#666666' width='40%'";
        frameHdInfoCellAttrs = "bgcolor='white' align='right'";
        frameHdFontAttrs = "face='tahoma,arial,helvetica' size=1 color=white";
        frameHdInfoFontAttrs = "face='tahoma,arial,helvetica' size=1";
        innerTableAttrs = "border=0 cellspacing=1 cellpadding=4 bgcolor='#666666' width='100%' ";
        bodyStartHtml = "<font face='tahoma,arial,helvetica' size=2>";
        bodyEndHtml = "</font>";

        heading = null;
        headingExtra = null;
        bodyColor = "white";
    }

    public void setHeading(String value)
    {
        heading = value;
    }

    public void setHeadingExtra(String value)
    {
        headingExtra = value;
    }

    public void setFrameColor(String value)
    {
        frameHdRowAttrs = "bgcolor='"+ value +"' height=15";
        frameHdRowSpacerAttrs = "bgcolor='"+ value +"' height=2";
        frameHdCellAttrs = "bgcolor='"+ value +"' width='40%'";
        innerTableAttrs = "border=0 cellspacing=1 cellpadding=2 bgcolor='"+ value +"' width='100%' ";
    }

    public void setBodyColor(String value)
    {
        bodyColor = value;
    }

    public void setExtraCellAttrs(String value)
    {
        frameHdInfoCellAttrs = value;
    }

    public void setFrameFontAttrs(String value)
    {
        frameHdFontAttrs = value;
    }

    public void setBodyStartHtml(String value)
    {
        bodyStartHtml = value;
    }

    public void setBodyEndHtml(String value)
    {
        bodyEndHtml = value;
    }

    public int doStartTag() throws JspException
    {
        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        ServletValueContext svc = new ServletValueContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
        try
        {
            if(headingExtra == null) headingExtra = "&nbsp;";

            String frameHdTabImgSrc = frameHdTabImgSrcValueSource.getValue(svc);
            String frameHdSpacerImgSrc = frameHdSpacerImgSrcValueSource.getValue(svc);

            out.println("<table "+ containerTableAttrs +"><tr><td>");

            out.println("<table "+ frameHdTableAttrs +">");
            out.println("<tr " + frameHdRowAttrs + "><td " + frameHdCellAttrs + "><nobr><font " + frameHdFontAttrs + ">&nbsp;<b>" + heading + "</b>&nbsp;</nobr></font></td><td width=14><font " + frameHdFontAttrs + "><img src='"+ frameHdTabImgSrc +"'></font></td><td "+ frameHdInfoCellAttrs +"><font " + frameHdInfoFontAttrs + "><nobr>"+ headingExtra +"</nobr></font></td></tr>");
            out.println("<tr " + frameHdRowSpacerAttrs +"><td colspan=3><img src='"+ frameHdSpacerImgSrc +"' height=2></td></tr>");
            out.println("</table>");

            out.println("<table " + innerTableAttrs + ">");
            out.println("<tr><td bgcolor='"+ bodyColor +"'>" + bodyStartHtml);
        }
        catch(IOException e)
        {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException
    {
        javax.servlet.jsp.JspWriter out = pageContext.getOut();

        try
        {
            out.println(bodyEndHtml + "</td></tr></table></td></tr></table>");
        }
        catch(IOException e)
        {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }
}
