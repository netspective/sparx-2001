package com.xaf.db;

import java.io.*;
import java.sql.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.skin.*;
import com.xaf.transform.*;

/**
 * Provides a reference implementation for a detailed, fully-interactive,
 * ERD-style SchemaDocument browser that could replace printed database design
 * documents in favor of a dynamic one.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class SchemaBrowserServlet extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html";

	public class GenerateOptions
	{
		String[] tableNames;
		int generatingItems;
		boolean[] generate = new boolean[SchemaGeneratorSkin.GENERATE_ITEMS_COUNT];

		public GenerateOptions(DialogContext dc)
		{
			int tablesOption = Integer.parseInt(dc.getValue("tables_option"));
			if(tablesOption == 0)
				tableNames = schema.getTableNames(false);
			else
				tableNames = dc.getValues("tables");

			String[] generateOptions = dc.getValues("generate_options");
			if(generateOptions != null)
			{
				generatingItems = generateOptions.length;
				for(int i = 0; i < generatingItems; i++)
				{
					int option = Integer.parseInt(generateOptions[i]);
					generate[option] = true;
				}
			}
		}
	}

	SchemaDocument schema;
	Hashtable styleSheetParams = new Hashtable();
	String contentsStyleSheet;
	SchemaGeneratorDialog dialog;

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
		ServletContext context = config.getServletContext();
		schema = SchemaDocFactory.getDoc(context.getInitParameter("schema.file"));
		contentsStyleSheet = context.getInitParameter("schema.browser-xsl");
    }

    public void doGenerate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
		if(dialog == null)
			dialog = new SchemaGeneratorDialog();

		DialogContext dc = new DialogContext(request, response, getServletContext(), dialog, SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
			return;
		}

		GenerateOptions generateOptions = new GenerateOptions(dc);
		if(generateOptions.tableNames == null || generateOptions.tableNames.length == 0)
		{
			out.write("No tables selected.");
			return;
		}
		if(generateOptions.generatingItems == 0)
		{
			out.write("No generate items selected.");
			return;
		}

		out.write("<pre>");
		SchemaGeneratorSkin generator = new com.xaf.db.generate.StandardSchemaGeneratorSkin();

		for(int generateItem = 0; generateItem < SchemaGeneratorSkin.GENERATE_ITEMS_COUNT; generateItem++)
		{
			if(! generateOptions.generate[generateItem])
				continue;

			String[] tableNames = generateOptions.tableNames;
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();

		String command = null, commandParam = null, commandSubParam = null;
		String styleSheet = contentsStyleSheet;
		styleSheetParams.clear();
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath());

		String pathInfo = request.getPathInfo();
		if(pathInfo != null)
		{
			StringTokenizer st = new StringTokenizer(pathInfo, "/");
			try
			{
				command = st.nextToken();
				styleSheetParams.put("detail-type", command);

				commandParam = st.nextToken();
	    		styleSheetParams.put("detail-name", commandParam);

				commandSubParam = st.nextToken();
		    	styleSheetParams.put("sub-detail-name", commandSubParam);
			}
			catch(Exception e)
			{
				// we don't do anything here because if a token is not found,
				// it just won't set the styleSheetParam
			}
		}

		out.print(Transform.nodeToString(styleSheet, schema.getDocument(), styleSheetParams));
		if("generate".equals(command))
			doGenerate(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		doGet(request, response);
    }
}