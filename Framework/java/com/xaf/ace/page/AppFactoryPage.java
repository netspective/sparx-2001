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
import com.xaf.form.*;
import com.xaf.page.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.sql.query.*;
import com.xaf.task.*;
import com.xaf.value.*;

public class AppFactoryPage extends AceServletPage
{
	static public final int FACTORY_VALUESOURCE = 0;
	static public final int FACTORY_DIALOG_FIELD = 1;
	static public final int FACTORY_REPORT_COMPS = 2;
	static public final int FACTORY_TASK = 3;
	static public final int FACTORY_SKIN = 4;
	static public final int FACTORY_SQL_COMPARE = 5;

	private String name;
	private String caption;
	private int factory;

	public AppFactoryPage()
	{
		super();
	}

	public AppFactoryPage(String name, String caption, int factory)
	{
		this();
		this.name = name;
		this.caption = caption;
		this.factory = factory;
	}

	public final String getName()
	{
		return name == null ? "factory" : name;
	}

	public final String getCaption(PageContext pc)
	{
		return caption == null ? "Factories" : caption;
	}

	public final String getHeading(PageContext pc)
	{
		return getCaption(pc);
	}

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

		switch(factory)
		{
			case FACTORY_VALUESOURCE:
				ValueSourceFactory.createCatalog(rootElem);
				break;

			case FACTORY_DIALOG_FIELD:
				DialogFieldFactory.createCatalog(rootElem);
				break;

			case FACTORY_REPORT_COMPS:
				ReportColumnFactory.createCatalog(rootElem);
				ColumnDataCalculatorFactory.createCatalog(rootElem);
				break;

			case FACTORY_TASK:
				TaskFactory.createCatalog(rootElem);
				break;

			case FACTORY_SKIN:
				SkinFactory.createCatalog(rootElem);
				break;

			case FACTORY_SQL_COMPARE:
				SqlComparisonFactory.createCatalog(rootElem);
				break;
		}

		transform(pc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
	}
}
