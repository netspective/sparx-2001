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
 * $Id: AppComponentsExplorerServlet.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.netspective.sparx.ace.page.AppAccessControlListPage;
import com.netspective.sparx.ace.page.AppConfigurationPage;
import com.netspective.sparx.ace.page.AppDialogsPage;
import com.netspective.sparx.ace.page.AppFactoryPage;
import com.netspective.sparx.ace.page.AppInitParamsPage;
import com.netspective.sparx.ace.page.AppMetricsPage;
import com.netspective.sparx.ace.page.DataSourcesPage;
import com.netspective.sparx.ace.page.DatabaseGenerateDDLPage;
import com.netspective.sparx.ace.page.DatabaseGenerateJavaPage;
import com.netspective.sparx.ace.page.DatabaseMetaDataPage;
import com.netspective.sparx.ace.page.DatabasePage;
import com.netspective.sparx.ace.page.DatabaseQueryDefnPage;
import com.netspective.sparx.ace.page.DatabaseSchemaDocPage;
import com.netspective.sparx.ace.page.DatabaseSqlPage;
import com.netspective.sparx.ace.page.DocumentsPage;
import com.netspective.sparx.ace.page.HomePage;
import com.netspective.sparx.ace.page.MonitorLogPage;
import com.netspective.sparx.ace.page.SystemPropertiesPage;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.xaf.html.Component;
import com.netspective.sparx.xaf.html.component.HierarchicalMenu;
import com.netspective.sparx.xaf.page.PageControllerServlet;
import com.netspective.sparx.xaf.page.RedirectPage;
import com.netspective.sparx.xaf.page.ServletPage;
import com.netspective.sparx.xaf.page.VirtualPath;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

public class AppComponentsExplorerServlet extends PageControllerServlet
{
    private Hashtable styleSheetParams = new Hashtable();
    private Component[] menus;
    private VirtualPath homePath;

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        List menuBar = new ArrayList();

        List mainMenu = getPagesPath().getChildrenList();
        int menuNum = 1;
        for(Iterator i = mainMenu.iterator(); i.hasNext();)
        {
            VirtualPath path = (VirtualPath) i.next();
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

    public VirtualPath getHomePath()
    {
        return homePath;
    }

    public void registerPages(ServletConfig config) throws ServletException
    {
        VirtualPath pagesPath = getPagesPath();

        ServletPage homePage = new HomePage();
        homePath = pagesPath.registerPage("/", homePage);
        pagesPath.registerPage("/home", homePage);

        pagesPath.registerPage("/application", new RedirectPage("application", "Application", null));
        pagesPath.registerPage("/application/dialogs", new AppDialogsPage());
        pagesPath.registerPage("/application/config", new AppConfigurationPage());
        pagesPath.registerPage("/application/servlet-context", new AppInitParamsPage());
        pagesPath.registerPage("/application/acl", new AppAccessControlListPage());
        pagesPath.registerPage("/application/system-properties", new SystemPropertiesPage());
        pagesPath.registerPage("/application/metrics", new AppMetricsPage());

        pagesPath.registerPage("/application/factory", new AppFactoryPage());
        pagesPath.registerPage("/application/factory/value-sources", new AppFactoryPage("value-sources", "Value Sources", AppFactoryPage.FACTORY_VALUESOURCE));
        pagesPath.registerPage("/application/factory/dialog-fields", new AppFactoryPage("dialog-fields", "Dialog Fields", AppFactoryPage.FACTORY_DIALOG_FIELD));
        pagesPath.registerPage("/application/factory/report-comps", new AppFactoryPage("report-comps", "Report Components", AppFactoryPage.FACTORY_REPORT_COMPS));
        pagesPath.registerPage("/application/factory/tasks", new AppFactoryPage("tasks", "Tasks", AppFactoryPage.FACTORY_TASK));
        pagesPath.registerPage("/application/factory/skins", new AppFactoryPage("skins", "Skins", AppFactoryPage.FACTORY_SKIN));
        pagesPath.registerPage("/application/factory/sql-comparisons", new AppFactoryPage("sql-comparisons", "SQL Comparisons", AppFactoryPage.FACTORY_SQL_COMPARE));

        pagesPath.registerPage("/database", new DatabasePage());
        pagesPath.registerPage("/database/sql", new DatabaseSqlPage());
        pagesPath.registerPage("/database/query-defn", new DatabaseQueryDefnPage());
        pagesPath.registerPage("/database/schema", new DatabaseSchemaDocPage());
        pagesPath.registerPage("/database/generate-ddl", new DatabaseGenerateDDLPage());
        pagesPath.registerPage("/database/generate-java", new DatabaseGenerateJavaPage());
        pagesPath.registerPage("/database/data-sources", new DataSourcesPage());
        pagesPath.registerPage("/database/meta-data", new DatabaseMetaDataPage());

        Configuration appConfig = getAppConfig();
        ValueContext vc = new ServletValueContext(config.getServletContext(), this, null, null);

        pagesPath.registerPage("/application/monitor", new MonitorLogPage());
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
                    pagesPath.registerPage("/application/monitor/" + page.getName(), page);
                }
            }
        }

        pagesPath.registerPage("/documents", new DocumentsPage());
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
                    pagesPath.registerPage("/documents/" + page.getName(), page);
                }
            }
        }
    }
}