package com.xaf.ace;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.naming.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.config.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.skin.*;
import com.xaf.sql.*;
import com.xaf.sql.query.*;
import com.xaf.transform.*;
import com.xaf.value.*;

public class AppComponentExplorerServlet extends HttpServlet
{
	private static final String[] APP_AREAS = { "Home", "", "Schema", "schema", "DDL", "ddl", "UI", "ui", "SQL", "sql", "Project", "project", "TagDoc", "tagdoc", "JavaDoc", "javadoc" };
    private static final String CONTENT_TYPE = "text/html";

	private Map appAreaElemsMap = new Hashtable();
	private Document appComponents;
	private Element rootElem;
	private Element contextElem;
	private Hashtable styleSheetParams = new Hashtable();
	private String aceStyleSheet;
	private SchemaGeneratorDialog dialog;

	public class GenerateOptions
	{
		String[] tableNames;
		int generatingItems;
		boolean[] generate = new boolean[SchemaGeneratorSkin.GENERATE_ITEMS_COUNT];

		public GenerateOptions(DialogContext dc, SchemaDocument schema)
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

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
		ServletContext context = config.getServletContext();
		aceStyleSheet = context.getInitParameter("ace.browser-xsl");

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			appComponents = builder.newDocument();
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}

		rootElem = appComponents.createElement("xaf");
		appComponents.appendChild(rootElem);

		contextElem = appComponents.createElement("context");
		rootElem.appendChild(contextElem);

		addText(contextElem, "app-name", null);
		addText(contextElem, "app-path", context.getRealPath(""));

		Element appAreasElem = appComponents.createElement("app-areas");
		rootElem.appendChild(appAreasElem);

		for(int i = 0; i < APP_AREAS.length; i += 2)
		{
			Element appAreaElem = appComponents.createElement("app-area");
			appAreaElemsMap.put(APP_AREAS[i+1], appAreaElem);
			appAreaElem.setAttribute("caption", APP_AREAS[i]);
			appAreaElem.setAttribute("url", APP_AREAS[i+1]);
			appAreaElem.setAttribute("active", "no");
			appAreasElem.appendChild(appAreaElem);
		}

		Element contextParamsElem = appComponents.createElement("params");
		contextElem.appendChild(contextParamsElem);
		for(Enumeration e = context.getInitParameterNames(); e.hasMoreElements(); )
		{
			Element paramElem = appComponents.createElement("param");
			String paramName = (String) e.nextElement();
			addText(paramElem, "name", paramName);
			addText(paramElem, "value", context.getInitParameter(paramName));
			contextParamsElem.appendChild(paramElem);
		}

		Element configItemsElem = appComponents.createElement("config-items");
		contextElem.appendChild(configItemsElem);
		ConfigurationManager manager = ConfigurationManagerFactory.getManager(context);
		Element itemElem = appComponents.createElement("config-item");
		addText(itemElem, "name", "source-file");
		addText(itemElem, "value", manager.getSourceDocument().getFile().getAbsolutePath());
		configItemsElem.appendChild(itemElem);

		Configuration defaultConfig = manager.getDefaultConfiguration();
		ValueContext vc = new ServletValueContext(null, null, context);
		for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext(); )
		{
			itemElem = appComponents.createElement("config-item");
			Map.Entry configEntry = (Map.Entry) i.next();
			addText(itemElem, "name", (String) configEntry.getKey());
			Property property = (Property) configEntry.getValue();
			String expression = property.getExpression();
			String value = defaultConfig.getValue(vc, property.getName());
			if(expression.equals(value))
				addText(itemElem, "value", value);
			else
				addText(itemElem, "value", value + " (" + expression + ")");
			configItemsElem.appendChild(itemElem);
		}

		Element directoryParamsElem = appComponents.createElement("data-sources");
		contextElem.appendChild(directoryParamsElem);
		try
		{
			Context env = (Context) new InitialContext().lookup("java:comp/env/jdbc");
			for(NamingEnumeration e = env.list(""); e.hasMore(); )
			{
				Element entryElem = appComponents.createElement("entry");
				NameClassPair entry = (NameClassPair) e.nextElement();
				addText(entryElem, "name", "jdbc/" + entry.getName());

				try
				{
					DataSource source = (DataSource) env.lookup(entry.getName());
					DatabaseMetaData dbmd = source.getConnection().getMetaData();
					addText(entryElem, "driver-name", dbmd.getDriverName());
					addText(entryElem, "driver-version", dbmd.getDriverVersion());
					addText(entryElem, "url", dbmd.getURL());
					addText(entryElem, "user-name", dbmd.getUserName());
				}
				catch(Exception ex)
				{
					addText(entryElem, "driver-name", ex.toString());
				}
				directoryParamsElem.appendChild(entryElem);
			}
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
    }

	public void addText(Element parent, String elemName, String text)
	{
		Element elemNode = appComponents.createElement(elemName);
		Text textNode = appComponents.createTextNode(text);
		elemNode.appendChild(textNode);
		parent.appendChild(elemNode);
	}

    public void doGenerate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
		if(dialog == null)
			dialog = new SchemaGeneratorDialog();

		ServletContext context = getServletContext();
		SchemaDocument schema = SchemaDocFactory.getDoc(context.getInitParameter("schema.file"));

		out.write("<table class='heading' border='0' cellspacing='0' cellpadding='5'><tr class='heading'><td class='heading'>Generate DDL</td></tr>");
		out.write("<tr class='heading_rule'><td height='1' colspan='2'></td></tr><table>");

		DialogContext dc = new DialogContext(request, response, getServletContext(), dialog, SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
			return;
		}

		GenerateOptions generateOptions = new GenerateOptions(dc, schema);
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

	public void doSchema(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		SchemaDocument schema = SchemaDocFactory.getDoc(context.getInitParameter("schema.file"));
		String styleSheet = context.getInitParameter("ace.schema-browser-xsl");

        PrintWriter out = response.getWriter();
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath() + "/schema");
		out.write(Transform.nodeToString(styleSheet, schema.getDocument(), styleSheetParams));
	}

	public void doUI(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		String styleSheet = context.getInitParameter("ace.ui-browser-xsl");

        PrintWriter out = response.getWriter();
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath() + "/ui");
		out.write(Transform.nodeToString(styleSheet, manager.getDocument(), styleSheetParams));
	}

	public void doSql(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);
		String styleSheet = context.getInitParameter("ace.sql-browser-xsl");

        PrintWriter out = response.getWriter();
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath() + "/sql");
		out.write(Transform.nodeToString(styleSheet, manager.getDocument(), styleSheetParams));
	}

	public void doTestDialog(HttpServletRequest request, HttpServletResponse response, String dialogId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		Dialog dialog = manager.getDialog(dialogId);

        PrintWriter out = response.getWriter();
		out.write("<h1>Dialog: "+dialogId+"</h1>");
		out.write("<p>&nbsp;<center>");
		out.write(dialog.getHtml((HttpServletRequest) request, (HttpServletResponse) response, context, SkinFactory.getDialogSkin()));
		out.write("</center>");
	}

	public void doTestSql(HttpServletRequest request, HttpServletResponse response, String stmtId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = response.getWriter();
		DatabaseContext dbc = DatabaseContextFactory.getContext(request, context);
		ValueContext vc = new ServletValueContext((HttpServletRequest) request, (HttpServletResponse) response, context);

		out.write("<h1>SQL: "+stmtId+"</h1>");
		try
		{
			StatementManager.StatementInfo si = manager.getStatement(stmtId);
			out.write(si.getDebugHtml(vc));
			manager.produceReport(out, dbc, vc, SkinFactory.getReportSkin("report"), stmtId, null, null);
		}
		catch(Exception e)
		{
			StringWriter msg = new StringWriter();
			msg.write(e.toString());

			PrintWriter pw = new PrintWriter(msg);
			e.printStackTrace(pw);
			out.write("<pre>");
			out.write(msg.toString());
			out.write("</pre>");
		}
	}

	public void doTestQueryDefn(HttpServletRequest request, HttpServletResponse response, String queryDefnId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = response.getWriter();

		out.write("<h1>Query Definition: "+queryDefnId+"</h1>");

		QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
		QueryBuilderDialog dialog = queryDefn.getBuilderDialog();
		out.print(dialog.getHtml(request, response, context, SkinFactory.getDialogSkin()));
	}

	public void doTestQueryDefnSelectDialog(HttpServletRequest request, HttpServletResponse response, String queryDefnId, String dialogId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = response.getWriter();

		out.write("<h1>Query Definition: "+queryDefnId+", Dialog: "+ dialogId +"</h1>");

		QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
		QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogId);
		out.print(dialog.getHtml(request, response, context, SkinFactory.getDialogSkin()));
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
		ServletContext context = getServletContext();

		String area = null, command = null, commandParam = null, commandSubParam = null;
		String styleSheet = aceStyleSheet;
		styleSheetParams.clear();
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath());
		styleSheetParams.put("test-url", request.getContextPath() + request.getServletPath() + "/test");

		String pathInfo = request.getPathInfo();
		if(pathInfo != null)
		{
			StringTokenizer st = new StringTokenizer(pathInfo, "/");
			try
			{
				area = st.nextToken();
				styleSheetParams.put("app-area", area);

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

		for(Iterator i = appAreaElemsMap.keySet().iterator(); i.hasNext(); )
		{
			String key = (String) i.next();
			Element elem = (Element) appAreaElemsMap.get(key);
			elem.setAttribute("active", key.equals(area) ? "yes" : "no");
		}
		if(area == null)
		{
			Element elem = (Element) appAreaElemsMap.get("");
			elem.setAttribute("active", "yes");
			styleSheetParams.put("app-area", "home");
		}

		if("test".equals(area))
		{
			if("dialog".equals(command))
			{
				doTestDialog(request, response, commandParam);
			}
			else if("statement".equals(command))
			{
				try
				{
					doTestSql(request, response, commandParam);
				}
				catch(Exception e)
				{
					throw new ServletException(e);
				}
			}
			else if("query-defn".equals(command))
			{
				doTestQueryDefn(request, response, commandParam);
			}
			else if("query-defn-dlg".equals(command))
			{
				doTestQueryDefnSelectDialog(request, response, commandParam, commandSubParam);
			}
			else
			{
				out.write("Unable to perform command "+ command);
			}
		}
		else
		{
			out.write(Transform.nodeToString(styleSheet, appComponents, styleSheetParams));
			if("javadoc".equals(area))
				response.sendRedirect(context.getInitParameter("ace.javadoc-url"));
			else if("tagdoc".equals(area))
				response.sendRedirect(context.getInitParameter("ace.tagdoc-url"));
			else if("schema".equals(area))
				doSchema(request, response);
			else if("ddl".equals(area))
				doGenerate(request, response);
			else if("ui".equals(area))
				doUI(request, response);
			else if("sql".equals(area))
				doSql(request, response);
		}
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		doGet(request, response);
    }
}