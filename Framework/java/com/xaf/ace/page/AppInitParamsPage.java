package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.form.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class AppInitParamsPage extends AceServletPage
{
	public final String getName() { return "servlet-context"; }
	public final String getPageIcon() { return "servlet_context.gif"; }
	public final String getCaption(PageContext pc) { return "Servlet Context"; }
	public final String getHeading(PageContext pc) { return "Application Servlet Context"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		Document doc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}

		Element rootElem = doc.createElement("xaf");
		doc.appendChild(rootElem);

		Element propertiesElem = doc.createElement("properties");
		propertiesElem.setAttribute("name", "Init Parameters");
		rootElem.appendChild(propertiesElem);

		for(Enumeration e = context.getInitParameterNames(); e.hasMoreElements(); )
		{
			Element propertyElem = doc.createElement("property");
			String paramName = (String) e.nextElement();
			addText(propertyElem, "name", paramName);
			addText(propertyElem, "value", context.getInitParameter(paramName));
			propertiesElem.appendChild(propertyElem);
		}

		transform(pc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
	}
}
