package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DatabaseSchemaDocPage extends AceServletPage
{
	public final String getName() { return "schema-doc"; }
	public final String getPageIcon() { return "schema.gif"; }
	public final String getCaption(PageContext pc) { return "Schema (XML)"; }
	public final String getHeading(PageContext pc) { return "Database Schema (XML Source)"; }

	public SchemaDocument getSchemaDocument(PageContext pc)
	{
		Configuration appConfig = ((PageControllerServlet) pc.getServlet()).getAppConfig();
		return SchemaDocFactory.getDoc(appConfig.getValue(pc, "app.schema.source-file"));
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		SchemaDocument schema = getSchemaDocument(pc);
		transform(pc, schema.getDocument(), ACE_CONFIG_ITEMS_PREFIX + "schema-browser-xsl");
	}
}
