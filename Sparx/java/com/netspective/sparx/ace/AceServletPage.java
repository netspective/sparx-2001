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
 * $Id: AceServletPage.java,v 1.3 2002-08-31 00:18:03 shahid.shah Exp $
 */

package com.netspective.sparx.ace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.xaf.html.Component;
import com.netspective.sparx.xaf.html.component.HierarchicalMenu;
import com.netspective.sparx.xaf.page.AbstractServletPage;
import com.netspective.sparx.xaf.page.PageContext;
import com.netspective.sparx.xaf.page.PageControllerServlet;
import com.netspective.sparx.xaf.page.VirtualPath;
import com.netspective.sparx.ace.page.DatabaseGenerateJavaDialog;

public class AceServletPage extends AbstractServletPage
{
    static public final String ACE_TESTITEM_ATTRNAME = "ace-test-item";
    static public final String ACE_CONFIG_ITEM_PROPBROWSERXSL = com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "properties-browser-xsl";

    public String getTitle(PageContext pc)
    {
        return "ACE - " + getHeading(pc);
    }

    public String getTestCommandItem(PageContext pc)
    {
        return (String) pc.getRequest().getAttribute(ACE_TESTITEM_ATTRNAME);
    }

    public void addText(Element parent, String elemName, String text)
    {
        Document doc = parent.getOwnerDocument();
        Element elemNode = doc.createElement(elemName);
        Text textNode = doc.createTextNode(text);
        elemNode.appendChild(textNode);
        parent.appendChild(elemNode);
    }

    public void transform(PageContext pc, Document doc, String styleSheetConfigName, String outputFileName) throws IOException
    {
        AppComponentsExplorerServlet servlet = ((AppComponentsExplorerServlet) pc.getServlet());
        Hashtable styleSheetParams = servlet.getStyleSheetParams();

        /**
         * Add all of the entries from WEB-INF/conf/sparx.xml into the StyleSheet
         * parameters. This will allow stylesheets to use the configuration
         * properties as well.
         */
        if(styleSheetParams.get("config-items-added") == null)
        {
            Configuration appConfig = ((PageControllerServlet) pc.getServlet()).getAppConfig();
            for(Iterator i = appConfig.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry configEntry = (Map.Entry) i.next();

                if(configEntry.getValue() instanceof Property)
                {
                    Property property = (Property) configEntry.getValue();
                    String propName = property.getName();
                    styleSheetParams.put(propName, appConfig.getTextValue(pc, propName));
                }
            }
            if(appConfig.getTextValue(pc, DatabaseGenerateJavaDialog.DAL_SCHEMA_CONFIG_PARAM_NAME, null) == null)
                styleSheetParams.put(DatabaseGenerateJavaDialog.DAL_SCHEMA_CONFIG_PARAM_NAME, DatabaseGenerateJavaDialog.DAL_SCHEMA_DEFAULT_CLASS_NAME);
            styleSheetParams.put("config-items-added", new Boolean(true));
        }

        styleSheetParams.put("root-url", pc.getActivePath().getMatchedPath().getAbsolutePath(pc));
        styleSheetParams.put("page-heading", getHeading(pc));

        styleSheetParams.remove("detail-type");
        styleSheetParams.remove("detail-name");
        styleSheetParams.remove("sub-detail-name");

        VirtualPath.FindResults results = pc.getActivePath();
        String[] unmatchedItems = results.unmatchedPathItems();
        if(unmatchedItems != null)
        {
            if(unmatchedItems.length > 0)
                styleSheetParams.put("detail-type", unmatchedItems[0]);
            if(unmatchedItems.length > 1)
                styleSheetParams.put("detail-name", unmatchedItems[1]);
            if(unmatchedItems.length > 2)
                styleSheetParams.put("sub-detail-name", unmatchedItems[2]);
        }

        String styleSheet = servlet.getAppConfig().getTextValue(pc, styleSheetConfigName);
        PrintWriter out = pc.getResponse().getWriter();

        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource(styleSheet));

            for(Iterator i = styleSheetParams.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();
                transformer.setParameter((String) entry.getKey(), entry.getValue());
            }

            if(outputFileName == null)
            {
                transformer.transform
                        (new javax.xml.transform.dom.DOMSource(doc),
                                new javax.xml.transform.stream.StreamResult(out));
            }
            else
            {
                transformer.transform
                        (new javax.xml.transform.dom.DOMSource(doc),
                                new javax.xml.transform.stream.StreamResult(outputFileName));
            }
        }
        catch(TransformerConfigurationException e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
        catch(TransformerException e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
    }

    public void transform(PageContext pc, Document doc, String styleSheetConfigName) throws IOException
    {
        transform(pc, doc, styleSheetConfigName, null);
    }

    public void transform(PageContext pc, File xmlSourceFile, String xsltSourceFile) throws IOException
    {
        PrintWriter out = pc.getResponse().getWriter();

        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource(xsltSourceFile));

            transformer.setParameter("file-date", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(xmlSourceFile.lastModified())));

            transformer.transform
                    (new javax.xml.transform.stream.StreamSource(xmlSourceFile),
                            new javax.xml.transform.stream.StreamResult(out));
        }
        catch(TransformerConfigurationException e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
        catch(TransformerException e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
    }

    public void handlePageMetaData(PageContext pc) throws ServletException, IOException
    {
        if(getTestCommandItem(pc) != null)
            return;

        String sharedScriptsRootURL = ((PageControllerServlet) pc.getServlet()).getSharedScriptsRootURL();
        String sharedCssRootURL = ((PageControllerServlet) pc.getServlet()).getSharedCssRootURL();

        HierarchicalMenu.DrawContext dc = new HierarchicalMenu.DrawContext();
        pc.getRequest().setAttribute(HierarchicalMenu.DrawContext.class.getName(), dc);


        Component[] menus = ((AppComponentsExplorerServlet) pc.getServlet()).getMenuBar();
        try
        {
            PrintWriter out = pc.getResponse().getWriter();
            out.print("<head>\n");
            out.print("<title>");
            out.print(getTitle(pc));
            out.print("</title>\n");
            out.print("<link rel='stylesheet' href='" + sharedCssRootURL + "/ace.css'>\n");
            for(int i = 0; i < menus.length; i++)
            {
                dc.firstMenu = i == 0;
                dc.lastMenu = i == (menus.length - 1);
                menus[i].printHtml(pc, out);
            }
            out.print("</head>\n\n");
        }
        catch(IOException e)
        {
            throw new ServletException(e);
        }
    }

    public void handlePageHeader(PageContext pc) throws ServletException, IOException
    {
        if(getTestCommandItem(pc) != null)
            return;

        AppComponentsExplorerServlet servlet = (AppComponentsExplorerServlet) pc.getServlet();
        String sharedImagesRootURL = servlet.getSharedImagesRootURL();
        String homeUrl = servlet.getHomePath().getAbsolutePath(pc);

        try
        {
            PrintWriter out = pc.getResponse().getWriter();
            out.print("<body TOPMARGIN='0' LEFTMARGIN='0' MARGINWIDTH='0' MARGINHEIGHT='0' bgcolor='white'>");
            out.print("<map name='menu_map'>");
            out.print(" <area shape='rect' coords='14,19,76,41' href='" + homeUrl + "'>");
            out.print(" <area shape='rect' coords='79,20,140,41' onMouseOver=\"popUp('HM_Menu2',event)\" onMouseOut=\"popDown('HM_Menu2')\">");
            out.print(" <area shape='rect' coords='144,21,207,41' onMouseOver=\"popUp('HM_Menu3',event)\" onMouseOut=\"popDown('HM_Menu3')\">");
            out.print(" <area shape='rect' coords='211,21,275,41' onMouseOver=\"popUp('HM_Menu4',event)\" onMouseOut=\"popDown('HM_Menu4')\">");
            out.print("</map>");
            out.print("<table border='0' cellpadding='0' cellspacing='0' height='44'>");
            out.print("	<tr>");
            out.print("		<td><a href='" + homeUrl + "'><img src='" + sharedImagesRootURL + "/ace/masthead-logo.gif' width='158' height='44' border='0'></a></td>");
            out.print("		<td><img src='" + sharedImagesRootURL + "/ace/masthead.gif' width='642' height='44' border='0' usemap='#menu_map'></td>");
            out.print("	</tr>");
            out.print("</table>");
            out.print("<table border='0' cellpadding='0' cellspacing='0' height='38' width='100%'>");
            out.print("	<tr>");
            out.print("		<td width='107'><img src='" + sharedImagesRootURL + "/ace/masthead-icon-leader.gif' width='107' height='38' border='0'></td>");
            out.print("		<td width='32' bgcolor='white' valign='middle' align='center'><img src='" + sharedImagesRootURL + "/ace/icons/" + getPageIcon() + "' width='32' height='32' border='0'></td>");
            out.print("		<td width='18'><img src='" + sharedImagesRootURL + "/ace/masthead-curve.gif' width='18' height='38' border='0'></td>");
            out.print("		<td background='" + sharedImagesRootURL + "/ace/2tone.gif' class='subheads' valign='middle' align='left'><nobr>" + getHeading(pc) + "</nobr></td>");
            out.print("		<td background='" + sharedImagesRootURL + "/ace/2tone.gif' class='subheads' valign='middle' align='left' width='166'><img src='" + sharedImagesRootURL + "/ace/masthead-sparx.gif' width='166' height='38' border='0'></td>");
            out.print("	</tr>");
            out.print(" <tr bgcolor='#003366' height='2'><td bgcolor='#003366' height='2' colspan='5'></td></tr>");
            out.print("</table>");
        }
        catch(IOException e)
        {
            throw new ServletException(e);
        }
    }

    public void handlePage(PageContext pc) throws ServletException
    {
        VirtualPath.FindResults results = pc.getActivePath();
        String[] unmatchedItems = results.unmatchedPathItems();
        if(unmatchedItems != null && unmatchedItems.length >= 2 && unmatchedItems[0].equals("test"))
            pc.getRequest().setAttribute(ACE_TESTITEM_ATTRNAME, unmatchedItems[1]);

        super.handlePage(pc);
    }
}