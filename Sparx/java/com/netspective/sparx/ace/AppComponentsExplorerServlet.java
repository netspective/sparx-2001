/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: AppComponentsExplorerServlet.java,v 1.7 2003-01-07 10:46:05 roque.hernandez Exp $
 */

package com.netspective.sparx.ace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.page.*;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.xaf.html.Component;
import com.netspective.sparx.xaf.html.component.HierarchicalMenu;
import com.netspective.sparx.xaf.page.PageControllerServlet;
import com.netspective.sparx.xaf.page.RedirectPage;
import com.netspective.sparx.xaf.navigate.NavigationPath;
import com.netspective.sparx.xaf.navigate.NavigationPage;
import com.netspective.sparx.xaf.navigate.NavigationTree;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

public class AppComponentsExplorerServlet extends PageControllerServlet
{
    private Hashtable styleSheetParams = new Hashtable();
    private Component[] menus;
    private NavigationPage homePage;

    protected void initFirstRequest(HttpServletRequest req, HttpServletResponse resp)
    {
        super.initFirstRequest(req, resp);

        List menuBar = new ArrayList();

        List mainMenu = getPagesTree().getChildrenList();
        int menuNum = 1;
        for(Iterator i = mainMenu.iterator(); i.hasNext();)
        {
            NavigationPath path = (NavigationPath) i.next();
            if(path.getChildrenList().size() > 0)
                menuBar.add(new HierarchicalMenu(menuNum, 171 + (66 * (menuNum - 1)), 110, 38, path, getSharedScriptsRootURL()));
            menuNum++;
        }

        menus = (Component[]) menuBar.toArray(new Component[menuBar.size()]);
    }

    public Component[] getMenuBar()
    {
        return menus;
    }

    public Hashtable getStyleSheetParams()
    {
        return styleSheetParams;
    }

    public String getConfigItemsPrefix()
    {
        return com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX;
    }

    public NavigationPath getHomePath()
    {
        return homePage;
    }

    public void registerPages(ServletConfig config) throws ServletException
    {
        NavigationTree pagesTree = new NavigationTree();
        pagesTree.setRoot(true);
        setPagesTree(pagesTree);

        homePage = new HomePage();
        pagesTree.registerPage("/", homePage);
        pagesTree.registerPage("/home", homePage);
        pagesTree.registerPage("/application", new RedirectPage("application", "Application", null));
        pagesTree.registerPage("/application/dialogs", new AppDialogsPage());
        pagesTree.registerPage("/application/navigation", new AppNavigationPage());
        pagesTree.registerPage("/application/config", new AppConfigurationPage());
        pagesTree.registerPage("/application/servlet-context", new AppInitParamsPage());
        pagesTree.registerPage("/application/acl", new AppAccessControlListPage());
        pagesTree.registerPage("/application/system-properties", new SystemPropertiesPage());
        pagesTree.registerPage("/application/metrics", new AppMetricsPage());

        pagesTree.registerPage("/application/factory", new AppFactoryPage());
        pagesTree.registerPage("/application/factory/value-sources", new AppFactoryPage("value-sources", "Value Sources", AppFactoryPage.FACTORY_VALUESOURCE));
        pagesTree.registerPage("/application/factory/dialog-fields", new AppFactoryPage("dialog-fields", "Dialog Fields", AppFactoryPage.FACTORY_DIALOG_FIELD));
        pagesTree.registerPage("/application/factory/report-comps", new AppFactoryPage("report-comps", "Report Components", AppFactoryPage.FACTORY_REPORT_COMPS));
        pagesTree.registerPage("/application/factory/tasks", new AppFactoryPage("tasks", "Tasks", AppFactoryPage.FACTORY_TASK));
        pagesTree.registerPage("/application/factory/skins", new AppFactoryPage("skins", "Skins", AppFactoryPage.FACTORY_SKIN));
        pagesTree.registerPage("/application/factory/sql-comparisons", new AppFactoryPage("sql-comparisons", "SQL Comparisons", AppFactoryPage.FACTORY_SQL_COMPARE));
        pagesTree.registerPage("/application/factory/component-commands", new AppFactoryPage("component-commands", "Component Commands", AppFactoryPage.FACTORY_COMPONENT_COMMANDS));

        pagesTree.registerPage("/database", new DatabasePage());
        pagesTree.registerPage("/database/sql", new DatabaseSqlPage());
        pagesTree.registerPage("/database/query-defn", new DatabaseQueryDefnPage());
        pagesTree.registerPage("/database/schema", new DatabaseSchemaDocPage());
        pagesTree.registerPage("/database/generate-ddl", new DatabaseGenerateDDLPage());
        pagesTree.registerPage("/database/generate-java", new DatabaseGenerateJavaPage());
        pagesTree.registerPage("/database/data-sources", new DataSourcesPage());
        pagesTree.registerPage("/database/import-data", new DatabaseImportData());
        pagesTree.registerPage("/database/meta-data", new DatabaseMetaDataPage());

        Configuration appConfig = getAppConfig();
        ValueContext vc = new ServletValueContext(config.getServletContext(), this, null, null);

        pagesTree.registerPage("/application/monitor", new MonitorLogPage());
        Collection logs = appConfig.getValues(vc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "monitor.logs");
        if(logs != null)
        {
            for(Iterator i = logs.iterator(); i.hasNext();)
            {
                Object entry = i.next();
                if(entry instanceof Property)
                {
                    Property logProperty = (Property) entry;
                    String logName = logProperty.getName();
                    String logStyle = appConfig.getValue(vc, logProperty, null);
                    MonitorLogPage page = new MonitorLogPage(logName, logStyle);
                    pagesTree.registerPage("/application/monitor/" + page.getName(), page);
                }
            }
        }

        pagesTree.registerPage("/documents", new DocumentsPage());
        Collection bookmarks = appConfig.getValues(vc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "bookmarks");
        if(bookmarks != null)
        {
            for(Iterator i = bookmarks.iterator(); i.hasNext();)
            {
                Object entry = i.next();
                if(entry instanceof Property)
                {
                    Property bookmark = (Property) entry;
                    String info = bookmark.getName();
                    String dest = appConfig.getValue(vc, bookmark, null);
                    DocumentsPage page = new DocumentsPage(info, dest);
                    pagesTree.registerPage("/documents/" + page.getName(), page);
                }
            }
        }
    }
}