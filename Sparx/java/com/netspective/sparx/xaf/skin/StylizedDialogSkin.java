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
 * $Id: StylizedDialogSkin.java,v 1.3 2002-12-16 23:53:03 aye.thu Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.io.Writer;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class StylizedDialogSkin extends StandardDialogSkin
{
    private String headColor;
    private SingleValueSource tabImagesValueSource;

    public StylizedDialogSkin()
    {
        super();
        headColor = "#EEEEEE";
        outerTableAttrs = "";
        innerTableAttrs = "width='100%' cellspacing='0' cellpadding='4' bgcolor='#EEEEEE' style='border:1px solid black'";
        tabImagesValueSource = ValueSourceFactory.getSingleValueSource("config-expr:${sparx.shared.images-url}/tabs/black-on-lgray");
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            Element nodeElem = (Element) node;
            Node firstChild = node.getFirstChild();
            String nodeText = firstChild != null ? firstChild.getNodeValue() : null;

            if(nodeName.equals("head-color") && nodeText != null)
                headColor = nodeText;
            else if(nodeName.equals("tab-images-vs") && nodeText != null)
                tabImagesValueSource = ValueSourceFactory.getSingleOrStaticValueSource(nodeText);
        }
    }

    public void renderTab(Writer writer, DialogContext dc, String heading) throws IOException
    {
        String imagesUrl = tabImagesValueSource.getValue(dc);
        writer.write("<table border='0' cellspacing='0' cellpadding='0'>\n");
        writer.write("	<tr>\n");
        writer.write("		<td valign='top' width='3' height='17'><img src='"+ imagesUrl +"/arrow-top.gif'/></td>\n");
        writer.write("		<td height='17' bgcolor='"+ headColor +"' style='border-top:1px solid black; border-right:1px solid black; '><font face='tahoma,arial,helvetica' size='2' style='font-size:8pt'>\n");
        writer.write("			<nobr>\n");
        writer.write("				&nbsp;\n");
        writer.write("				<b>"+ heading +"</b>\n");
        writer.write("				&nbsp;&nbsp;\n");
        writer.write("			</nobr>\n");
        writer.write("		</font></td>\n");
        writer.write("	</tr>\n");
        writer.write("</table>\n");
        writer.write("<table height='6' width='100%' border='0' cellspacing='0' cellpadding='0'>\n");
        writer.write("	<tr height='6'>\n");
        writer.write("		<td width='12'><img src='"+ imagesUrl +"/arrow-bottom.gif'/></td>\n");
        writer.write("		<td background='"+ imagesUrl +"/horiz-bar.gif' bgcolor='"+ headColor +"'><img src='"+ imagesUrl +"/spacer.gif'/></td>\n");
        writer.write("		<td width='6'><img src='"+ imagesUrl +"/horiz-bar-end.gif'/></td>\n");
        writer.write("	</tr>\n");
        writer.write("</table>\n");
    }

    public void renderContentsHtml(Writer writer, DialogContext dc, Configuration appConfig, String dialogName, String actionURL, String encType, String heading, int dlgTableColSpan, StringBuffer errorMsgsHtml, StringBuffer fieldsHtml) throws IOException
    {
        writer.write(
                "<table " + outerTableAttrs + ">\n" +
                "<tr><td>\n");

        if(heading != null && ! dc.getDialog().hideHeading(dc))
            renderTab(writer, dc, heading);

        writer.write(
                "<table " + innerTableAttrs + ">\n<tr><td>\n");

        if(summarizeErrors)
            writer.write(errorMsgsHtml.toString());

        writer.write(
                "<form id='" + dialogName + "' name='" + dialogName + "' action='" + actionURL + "' method='post' " + encType + " onsubmit='return(activeDialog.isValid())'>\n" +
                dc.getStateHiddens() + "\n" +
                fieldsHtml +
                "</form>\n");

        writer.write(
                "</td></tr>\n</table>\n</td></tr>\n</table>");
    }

}
