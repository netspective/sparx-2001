package com.xaf.db;

import java.io.*;
import java.util.*;
import javax.servlet.*;

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

	public static SchemaDocument getDoc(ServletContext context)
	{
		SchemaDocument doc = (SchemaDocument) context.getAttribute(ATTRNAME_SCHEMADOC);
		if(doc != null)
			return doc;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(null, null, context);
		doc = getDoc(appConfig.getValue(vc, "app.schema.source-file"));
		context.setAttribute(ATTRNAME_SCHEMADOC, doc);
		return doc;
	}
}
