package com.xaf.db;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import com.xaf.config.*;
import com.xaf.value.*;

public class SchemaDocFactory
{
	static final String ATTRNAME_SCHEMADOC = "framework.schema-doc";
	static Map docs = new Hashtable();

	public static SchemaDocument getDoc(String file)
	{
		SchemaDocument schemaDoc = (SchemaDocument) docs.get(file);
		if(schemaDoc == null)
		{
			schemaDoc = new SchemaDocument(new File(file));
			docs.put(file, schemaDoc);
		}
		return schemaDoc;
	}

	public static SchemaDocument getDoc(ValueContext vc, String dataSourceId, String catalog, String schemaPattern) throws ParserConfigurationException, NamingException, SQLException
	{
		SchemaDocument schemaDoc = (SchemaDocument) docs.get(dataSourceId);
		if(schemaDoc == null)
		{
			DatabaseContext dbc = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
			schemaDoc = new SchemaDocument(dbc.getConnection(vc, dataSourceId), catalog, schemaPattern);
			docs.put(dataSourceId, schemaDoc);
		}
		return schemaDoc;
	}

	public static SchemaDocument getDoc(ServletContext context)
	{
		SchemaDocument doc = (SchemaDocument) context.getAttribute(ATTRNAME_SCHEMADOC);
		if(doc != null)
			return doc;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(context, null, null, null);
		doc = getDoc(appConfig.getValue(vc, "app.schema.source-file"));
		context.setAttribute(ATTRNAME_SCHEMADOC, doc);
		return doc;
	}
}
