package com.xaf.ace;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.html.*;
import com.xaf.html.component.*;
import com.xaf.page.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.transform.*;
import com.xaf.value.*;

public class AceServletPage extends AbstractServletPage
{
	static public final String ACE_TESTITEM_ATTRNAME = "ace-test-item";
	static public final String ACE_CONFIG_ITEMS_PREFIX = "framework.ace.";
	static public final String ACE_CONFIG_ITEM_PROPBROWSERXSL = ACE_CONFIG_ITEMS_PREFIX + "properties-browser-xsl";

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

	public void transform(PageContext pc, Document doc, String styleSheetConfigName) throws IOException
	{
		AppComponentsExplorerServlet servlet = ((AppComponentsExplorerServlet) pc.getServlet());
		Hashtable styleSheetParams = servlet.getStyleSheetParams();

		if(styleSheetParams.get("config-items-added") == null)
		{
			Configuration appConfig = ((PageControllerServlet) pc.getServlet()).getAppConfig();
			for(Iterator i = appConfig.entrySet().iterator(); i.hasNext(); )
			{
				Map.Entry configEntry = (Map.Entry) i.next();

				if(configEntry.getValue() instanceof Property)
				{
					Property property = (Property) configEntry.getValue();
					String propName = property.getName();
					styleSheetParams.put(propName, appConfig.getValue(pc, propName));
				}
			}
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

		String styleSheet = servlet.getAppConfig().getValue(pc, styleSheetConfigName);
        PrintWriter out = pc.getResponse().getWriter();
		out.write(Transform.nodeToString(styleSheet, doc, styleSheetParams));
	}

	public void handlePageMetaData(PageContext pc) throws ServletException, IOException
	{
		if(getTestCommandItem(pc) != null)
			return;

		String sharedScriptsRootURL = ((PageControllerServlet) pc.getServlet()).getSharedScriptsRootURL();
		String sharedCssRootURL = ((PageControllerServlet) pc.getServlet()).getSharedCssRootURL();
		Component menu = ((AppComponentsExplorerServlet) pc.getServlet()).getMenuComponent();
		try
		{
			PrintWriter out = pc.getResponse().getWriter();
			out.print("<head>\n");
			out.print("<title>");
			out.print(getTitle(pc));
			out.print("</title>\n");
			out.print("<link rel='stylesheet' href='"+ sharedCssRootURL+ "/ace.css'>\n");
			menu.printHtml(pc, out);
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

		String sharedImagesRootURL = ((PageControllerServlet) pc.getServlet()).getSharedImagesRootURL();
		try
		{
			PrintWriter out = pc.getResponse().getWriter();
			out.print("<body><table width=100%><tr><td width=25><img src='");
			out.print(sharedImagesRootURL);
			out.print("/ace/ace-logo.gif'></td>");
			out.print("<td align=right><img src='");
			out.print(sharedImagesRootURL);
			out.print("/design/sparx-logo-sm.gif'></td>");
			out.print("</tr></table>");
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