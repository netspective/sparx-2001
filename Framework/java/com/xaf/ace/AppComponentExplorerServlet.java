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

import com.xaf.*;
import com.xaf.config.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.navigate.*;
import com.xaf.report.*;
import com.xaf.skin.*;
import com.xaf.sql.*;
import com.xaf.sql.query.*;
import com.xaf.transform.*;
import com.xaf.value.*;

public class AppComponentExplorerServlet extends HttpServlet
{
	private static final String[] APP_AREAS = { "Home", "", "Config", "config", "Schema", "schema", "DDL", "ddl", "UI", "ui", "SQL", "sql", "Documents", "documents", "Factories", "factories", "TagDoc", "tagdoc", "JavaDoc", "javadoc" };
    private static final String CONTENT_TYPE = "text/html";

	private Map appAreaElemsMap = new Hashtable();
	private ConfigurationManager manager;
	private Configuration appConfig;
	private Document appComponents;
	private Element rootElem;
	private Element contextElem;
	private Hashtable styleSheetParams = new Hashtable();
	private String aceHomeStyleSheet;
	private SchemaGeneratorDialog dialog;
	private FileSystemContext projectFSContext;

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

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

		ServletContext context = config.getServletContext();
		manager = ConfigurationManagerFactory.getManager(context);
		if(manager == null)
			throw new ServletException("Unable to obtain a ConfigurationManager");
		appConfig = manager.getDefaultConfiguration();
		if(appConfig == null)
			throw new ServletException("Unable to obtain the default Configuration");

		ValueContext vc = new ServletValueContext(context, null, null, null);
		aceHomeStyleSheet = appConfig.getValue(vc, "app.ace.browse-xsl");
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

    public void doGenerateDDL(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		preparePage(request, response, "/ddl");

        PrintWriter out = response.getWriter();
		if(dialog == null)
			dialog = new SchemaGeneratorDialog();

		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);
		SchemaDocument schema = SchemaDocFactory.getDoc(appConfig.getValue(vc, "app.schema.source-file"));

		out.write("<table class='heading' border='0' cellspacing='0' cellpadding='5'><tr class='heading'><td class='heading'>Generate DDL</td></tr>");
		out.write("<tr class='heading_rule'><td height='1' colspan='2'></td></tr><table>");

		DialogContext dc = new DialogContext(context, this, request, response, dialog, SkinFactory.getDialogSkin());
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

	public void preparePage(HttpServletRequest request, HttpServletResponse response, String rootUrl) throws IOException
	{
		styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath());

		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);

		for(Iterator i = appConfig.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry configEntry = (Map.Entry) i.next();

			if(configEntry.getValue() instanceof Property)
			{
				Property property = (Property) configEntry.getValue();
				String propName = property.getName();
				styleSheetParams.put(propName, appConfig.getValue(vc, propName));
			}
		}

        response.setContentType(CONTENT_TYPE);
		response.getWriter().write(Transform.nodeToString(aceHomeStyleSheet, appComponents, styleSheetParams));

		if(rootUrl != null)
			styleSheetParams.put("root-url", request.getContextPath() + request.getServletPath() + rootUrl);
	}

	public void doConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		preparePage(request, response, "/config");

		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);

		Document configDoc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			configDoc = builder.newDocument();
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}

		Element configRootElem = configDoc.createElement("xaf");
		configDoc.appendChild(configRootElem);

		Element configItemsElem = configDoc.createElement("config-items");
		configRootElem.appendChild(configItemsElem);

		ConfigurationManager manager = ConfigurationManagerFactory.getManager(context);
		configItemsElem.setAttribute("source-file", manager.getSourceDocument().getFile().getAbsolutePath());

		Configuration defaultConfig = manager.getDefaultConfiguration();
		for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext(); )
		{
			Element itemElem = configDoc.createElement("config-item");
			Map.Entry configEntry = (Map.Entry) i.next();
			itemElem.setAttribute("name", (String) configEntry.getKey());

			if(configEntry.getValue() instanceof Property)
			{
				Property property = (Property) configEntry.getValue();
				String expression = property.getExpression();
				String value = defaultConfig.getValue(vc, property.getName());
				itemElem.setAttribute("value", value);
				if(! expression.equals(value))
				{
					itemElem.setAttribute("expression", expression);
					if(! property.flagIsSet(Property.PROPFLAG_IS_FINAL))
						itemElem.setAttribute("final", "no");
				}
				if(property.getDescription() != null)
					itemElem.setAttribute("description", property.getDescription());
			}
			else if(configEntry.getValue() instanceof PropertiesCollection)
			{
				PropertiesCollection propColl = (PropertiesCollection) configEntry.getValue();
				Collection coll = propColl.getCollection();
				itemElem.setAttribute("expression", "list of " + coll.size() + " item(s)");
			}
			configItemsElem.appendChild(itemElem);
		}

		String styleSheet = appConfig.getValue(vc, "app.ace.config-browser-xsl");

        PrintWriter out = response.getWriter();
		out.write(Transform.nodeToString(styleSheet, configDoc, styleSheetParams));
	}

	public void doFactories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		preparePage(request, response, "/factories");

		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);

		Document facDoc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			facDoc = builder.newDocument();
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}

		Element facRootElem = facDoc.createElement("xaf");
		facDoc.appendChild(facRootElem);

		Element factoriesElem = facDoc.createElement("factories");
		facRootElem.appendChild(factoriesElem);

		ValueSourceFactory.createCatalog(factoriesElem);
		DialogFieldFactory.createCatalog(factoriesElem);
		ReportColumnFactory.createCatalog(factoriesElem);

		String styleSheet = appConfig.getValue(vc, "app.ace.factories-browser-xsl");

        PrintWriter out = response.getWriter();
		out.write(Transform.nodeToString(styleSheet, facDoc, styleSheetParams));
	}

	public void doSchema(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		preparePage(request, response, "/schema");

		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);
		SchemaDocument schema = SchemaDocFactory.getDoc(appConfig.getValue(vc, "app.schema.source-file"));
		String styleSheet = appConfig.getValue(vc, "app.ace.schema-browser-xsl");

        PrintWriter out = response.getWriter();
		out.write(Transform.nodeToString(styleSheet, schema.getDocument(), styleSheetParams));
	}

	public void doUI(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		preparePage(request, response, "/ui");

		ServletContext context = getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		ValueContext vc = new ServletValueContext(context, this, request, response);
		String styleSheet = appConfig.getValue(vc, "app.ace.ui-browser-xsl");

        PrintWriter out = response.getWriter();
		out.write(Transform.nodeToString(styleSheet, manager.getDocument(), styleSheetParams));
	}

	public void doSql(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		preparePage(request, response, "/sql");

		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);
		ValueContext vc = new ServletValueContext(context, this, request, response);
		String styleSheet = appConfig.getValue(vc, "app.ace.sql-browser-xsl");

        PrintWriter out = response.getWriter();
		out.write(Transform.nodeToString(styleSheet, manager.getDocument(), styleSheetParams));
	}

	public void doProject(HttpServletRequest request, HttpServletResponse response, String relativePath) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		ValueContext vc = new ServletValueContext(context, this, request, response);
		if(projectFSContext == null)
		{
			projectFSContext = new FileSystemContext(
				appConfig.getValue(vc, "app.ace.project.navigate.root-url"),
				appConfig.getValue(vc, "app.ace.project.navigate.root-path"),
				appConfig.getValue(vc, "app.ace.project.navigate.root-caption"),
				relativePath);
		}
		else
			projectFSContext.setRelativePath(relativePath);

		FileSystemEntry activeEntry = projectFSContext.getActivePath();
		if(activeEntry.isDirectory())
		{
			Document navgDoc = null;
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				navgDoc = builder.newDocument();
			}
			catch(Exception e)
			{
				throw new ServletException(e);
			}

			Element navgRootElem = navgDoc.createElement("xaf");
			navgDoc.appendChild(navgRootElem);

			projectFSContext.addXML(navgRootElem, projectFSContext);
			String styleSheet = appConfig.getValue(vc, "app.ace.project-browser-xsl");

			PrintWriter out = response.getWriter();
			preparePage(request, response, "/documents");
			out.write(Transform.nodeToString(styleSheet, navgDoc, styleSheetParams));
		}
		else
		{
			activeEntry.send(response);
		}
	}

	public void doTestDialog(HttpServletRequest request, HttpServletResponse response, String dialogId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		Dialog dialog = manager.getDialog(dialogId);

        PrintWriter out = response.getWriter();
		out.write("<h1>Dialog: "+dialogId+"</h1>");
		out.write("<p>&nbsp;<center>");
		out.write(dialog.getHtml(context, this, (HttpServletRequest) request, (HttpServletResponse) response, SkinFactory.getDialogSkin()));
		out.write("</center>");
	}

	public void doTestSql(HttpServletRequest request, HttpServletResponse response, String stmtId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = response.getWriter();
		DatabaseContext dbc = DatabaseContextFactory.getContext(request, context);
		ValueContext vc = new ServletValueContext(context, this, request, response);

		out.write("<h1>SQL: "+stmtId+"</h1>");
		try
		{
			StatementInfo si = manager.getStatement(stmtId);
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
		out.print(dialog.getHtml(context, this, request, response, SkinFactory.getDialogSkin()));
	}

	public void doTestQueryDefnSelectDialog(HttpServletRequest request, HttpServletResponse response, String queryDefnId, String dialogId) throws ServletException, IOException
	{
		ServletContext context = getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = response.getWriter();

		out.write("<h1>Query Definition: "+queryDefnId+", Dialog: "+ dialogId +"</h1>");

		QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
		QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogId);
		out.print(dialog.getHtml(context, this, request, response, SkinFactory.getDialogSkin()));
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
		ServletContext context = getServletContext();

		String area = null, command = null, commandParam = null, commandSubParam = null;
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
			ValueContext vc = new ServletValueContext(context, this, request, response);
			if("javadoc".equals(area))
				response.sendRedirect(appConfig.getValue(vc, "app.ace.javadoc-url"));
			else if("tagdoc".equals(area))
				response.sendRedirect(appConfig.getValue(vc, "app.ace.tagdoc-url"));
			else if("config".equals(area))
				doConfig(request, response);
			else if("schema".equals(area))
				doSchema(request, response);
			else if("ddl".equals(area))
				doGenerateDDL(request, response);
			else if("ui".equals(area))
				doUI(request, response);
			else if("sql".equals(area))
				doSql(request, response);
			else if("documents".equals(area))
				doProject(request, response, pathInfo.substring("documents".length()+1));
			else if("factories".equals(area))
				doFactories(request, response);
			else
			{
				preparePage(request, response, null);
				out.write("<p><br>&nbsp;&nbsp;<b><font color='red'>" + BuildConfiguration.getProductBuild() + "</font>");
			}
		}
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		doGet(request, response);
    }
}