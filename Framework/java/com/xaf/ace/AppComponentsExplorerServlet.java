package com.xaf.ace;

import java.io.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.ace.page.*;
import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.html.*;
import com.xaf.html.component.*;
import com.xaf.page.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class AppComponentsExplorerServlet extends PageControllerServlet
{
	protected static final String CONFIGITEM_DEFAULT_PREFIX = "app.ace.controller.";
	private Hashtable styleSheetParams = new Hashtable();
	private Component menu;

    public void init(ServletConfig config) throws ServletException
	{
        super.init(config);
		menu = new HierarchicalMenu(100, 90, 22, getPagesPath(), getSharedScriptsRootURL());
	}

	public Component getMenuComponent()
	{
		return menu;
	}

	public Hashtable getStyleSheetParams()
	{
		return styleSheetParams;
	}

	public String getConfigItemsPrefix()
	{
		return CONFIGITEM_DEFAULT_PREFIX;
	}

	public void registerPages(ServletConfig config) throws ServletException
	{
		VirtualPath pagesPath = getPagesPath();

		ServletPage homePage = new HomePage();
		pagesPath.registerPage("/", homePage);
		pagesPath.registerPage("/home", homePage);

		pagesPath.registerPage("/application", new RedirectPage("application", "Application", null));
		pagesPath.registerPage("/application/init-params", new AppInitParamsPage());
		pagesPath.registerPage("/application/config", new AppConfigurationPage());
		pagesPath.registerPage("/application/dialogs", new AppDialogsPage());

		pagesPath.registerPage("/database", new DatabasePage());
		pagesPath.registerPage("/database/sql", new DatabaseSqlPage());
		pagesPath.registerPage("/database/schema", new DatabaseSchemaDocPage());
		pagesPath.registerPage("/database/generate-ddl", new DatabaseGenerateDDLPage());
		pagesPath.registerPage("/database/data-sources", new DataSourcesPage());

		pagesPath.registerPage("/documents", new DocumentsPage());
		Configuration appConfig = getAppConfig();
		ValueContext vc = new ServletValueContext(config.getServletContext(), this, null, null);

		Collection bookmarks = appConfig.getValues(vc, "framework.ace.bookmarks");
		if(bookmarks != null)
		{
			for(Iterator i = bookmarks.iterator(); i.hasNext(); )
			{
				Object entry = i.next();
				if(entry instanceof Property)
				{
					Property bookmark = (Property) entry;
					String info = bookmark.getName();
					String dest = appConfig.getValue(vc, bookmark, null);
					DocumentsPage page = new DocumentsPage(info, dest);
					pagesPath.registerPage("/documents/" + page.getName(), page);
				}
			}
		}
	}
}