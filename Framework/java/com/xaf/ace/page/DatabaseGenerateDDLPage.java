package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.db.generate.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DatabaseGenerateDDLPage extends AceServletPage
{
	public final String getName() { return "generate-ddl"; }
	public final String getCaption(PageContext pc) { return "Generate DDL"; }
	public final String getHeading(PageContext pc) { return "Generate SQL Data Definition"; }

	private SchemaGeneratorDialog dialog;

	public class GenerateDDLOptions
	{
		String[] tableNames;
		int generatingItems;
		boolean[] generate = new boolean[SchemaGeneratorSkin.GENERATE_ITEMS_COUNT];

		public GenerateDDLOptions(DialogContext dc, SchemaDocument schema)
		{
			int tablesOption = Integer.parseInt(dc.getValue("tables_option"));
			if(tablesOption == 0)
				tableNames = schema.getTableNames(false);
			else
				tableNames = dc.getValues("tables");

			String[] GenerateDDLOptions = dc.getValues("generate_options");
			if(GenerateDDLOptions != null)
			{
				generatingItems = GenerateDDLOptions.length;
				for(int i = 0; i < generatingItems; i++)
				{
					int option = Integer.parseInt(GenerateDDLOptions[i]);
					generate[option] = true;
				}
			}
		}
	}

	public SchemaDocument getSchemaDocument(PageContext pc)
	{
		Configuration appConfig = ((PageControllerServlet) pc.getServlet()).getAppConfig();
		return SchemaDocFactory.getDoc(appConfig.getValue(pc, "app.schema.source-file"));
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
        PrintWriter out = pc.getResponse().getWriter();
		if(dialog == null)
			dialog = new SchemaGeneratorDialog();

		ServletContext context = pc.getServletContext();
		SchemaDocument schema = getSchemaDocument(pc);

		out.write("<table class='heading' border='0' cellspacing='0' cellpadding='5'><tr class='heading'><td class='heading'>Generate DDL</td></tr>");
		out.write("<tr class='heading_rule'><td height='1' colspan='2'></td></tr><table>");

		DialogContext dc = new DialogContext(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), dialog, SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
			return;
		}

		GenerateDDLOptions GenerateDDLOptions = new GenerateDDLOptions(dc, schema);
		if(GenerateDDLOptions.tableNames == null || GenerateDDLOptions.tableNames.length == 0)
		{
			out.write("No tables selected.");
			return;
		}
		if(GenerateDDLOptions.generatingItems == 0)
		{
			out.write("No generate items selected.");
			return;
		}

		out.write("<pre>");
		SchemaGeneratorSkin generator = new com.xaf.db.generate.StandardSchemaGeneratorSkin();

		for(int generateItem = 0; generateItem < SchemaGeneratorSkin.GENERATE_ITEMS_COUNT; generateItem++)
		{
			if(! GenerateDDLOptions.generate[generateItem])
				continue;

			String[] tableNames = GenerateDDLOptions.tableNames;
			Map tableElems = schema.getTables();
			for(int i = 0; i < tableNames.length; i++)
			{
				String tableName = tableNames[i].toUpperCase();
				Element tableElem = (Element) tableElems.get(tableName);
				if(tableElem != null)
				{
					generator.generate(out, tableElem, generateItem);
				}
				else
					throw new RuntimeException("Table '" + tableName + "' not found in schema.");
			}
		}
		out.write("</pre>");
    }
}
