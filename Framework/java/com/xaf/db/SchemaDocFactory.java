package com.xaf.db;

import java.io.*;
import java.util.*;
import javax.servlet.*;

public class SchemaDocFactory
{
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
		return getDoc(context.getInitParameter("schema.file"));
	}
}
