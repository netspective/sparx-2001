package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.config.*;
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
		propertiesElem.setAttribute("name", "Execution Environment");
		rootElem.appendChild(propertiesElem);

		Element propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "Is Production Environment");
		addText(propertyElem, "value", ConfigurationManagerFactory.isProductionEnvironment(context) ? "Yes" : "No");
		propertiesElem.appendChild(propertyElem);

		propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "Is Production or Testing Environment");
		addText(propertyElem, "value", ConfigurationManagerFactory.isProductionOrTestEnvironment(context) ? "Yes" : "No");
		propertiesElem.appendChild(propertyElem);

		propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "Is Testing Environment");
		addText(propertyElem, "value", ConfigurationManagerFactory.isTestEnvironment(context) ? "Yes" : "No");
		propertiesElem.appendChild(propertyElem);

		propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "Is Development Environment");
		addText(propertyElem, "value", ConfigurationManagerFactory.isDevelopmentEnvironment(context) ? "Yes" : "No");
		propertiesElem.appendChild(propertyElem);

		propertiesElem = doc.createElement("properties");
		propertiesElem.setAttribute("name", "Init Parameters");
		rootElem.appendChild(propertiesElem);

		for(Enumeration e = context.getInitParameterNames(); e.hasMoreElements(); )
		{
			propertyElem = doc.createElement("property");
			String paramName = (String) e.nextElement();
			addText(propertyElem, "name", paramName);
			addText(propertyElem, "value", context.getInitParameter(paramName));
			propertiesElem.appendChild(propertyElem);
		}

		propertiesElem = doc.createElement("properties");
		propertiesElem.setAttribute("name", "Libraries");
		rootElem.appendChild(propertiesElem);

		propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "XML Document Builder Factory");
		addText(propertyElem, "value", javax.xml.parsers.DocumentBuilderFactory.newInstance().getClass().getName());
		propertiesElem.appendChild(propertyElem);

		propertyElem = doc.createElement("property");
		addText(propertyElem, "name", "XSLT Transformer Factory");
		addText(propertyElem, "value", javax.xml.transform.TransformerFactory.newInstance().getClass().getName());
		propertiesElem.appendChild(propertyElem);

		transform(pc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
	}
}
