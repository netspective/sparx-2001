package com.xaf.ace.page;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DataSourcesPage extends AceServletPage
{
	public final String getName() { return "data-sources"; }
	public final String getCaption(PageContext pc) { return "Data Sources"; }
	public final String getHeading(PageContext pc) { return "Application Data Sources"; }

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

		try
		{
			DatabaseContextFactory.createCatalog(context, pc.getRequest(), rootElem);
	    	transform(pc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
		}
		catch(NamingException e)
		{
			PrintWriter out = pc.getResponse().getWriter();
			out.write(e.toString());
			e.printStackTrace(out);
		}
	}
}
