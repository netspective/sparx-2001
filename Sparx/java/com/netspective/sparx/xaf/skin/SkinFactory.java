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
 * $Id: SkinFactory.java,v 1.11 2003-02-26 07:54:14 aye.thu Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.navigate.NavigationPathSkin;
import com.netspective.sparx.xaf.theme.Theme;
import com.netspective.sparx.xaf.theme.ThemeStyle;

public class SkinFactory implements Factory
{
    private Map reportSkins = new HashMap();
    private Map dialogSkins = new HashMap();
    private Map navigationSkins = new HashMap();

    public static String DEFAULT_DIALOG_SKIN_NAME = "default";
    public static String DEFAULT_REPORT_SKIN_NAME = "report-compressed";
    public static String DEFAULT_NAVIGATION_SKIN_NAME = "default";

    public static final String DEFAULT_THEME_NAME = "sparx";
    public static final String DEFAULT_THEME_STYLE_NAME = "default";

    private static SkinFactory factory;
    private Map resources;
    private static boolean themeSkins = false;
    private Map themes;
    private boolean contextLoaded;
    private String currentTheme;

    /**
     * Private constructor
     */
    private SkinFactory()
    {
        themes = new HashMap();
        contextLoaded = false;
    }

    /**
     * Get the SINGLETON instance of the factory. There is only one theme factory object created for the servlet context.
     * @return ThemeFactory
     */
    public static SkinFactory getInstance()
    {
        if (factory == null)
        {
            // At this execution point, there is no servlet context
            // so look for THEME setups in the System environment (JVM properties). If they are found then
            // the theme-oriented skins are registered; else the default skins are registered.
            factory = new SkinFactory();
            // see if the JVM has a theme and style property defined
            String selectedTheme = null;
            String selectedStyle = null;
            selectedTheme = System.getProperty("app.ui.theme");
            selectedStyle = System.getProperty("app.ui.theme.style");
            String appRootPath = System.getProperty("app.site-root-path");
            System.out.println(selectedTheme + " " +  selectedStyle + " " + appRootPath);
            if (selectedTheme != null && selectedStyle != null && appRootPath != null)
            {
                factory.discoverResources(appRootPath, "/sparx/resources/theme");
                factory.discoverResources(appRootPath, "/resources/theme");
                // see if the theme exist
                Theme theme = factory.getTheme(selectedTheme);
                if (theme != null)
                {
                    // theme exists.. so set the default style and save the theme name as the current one
                    theme.setCurrentStyle(selectedStyle);
                    factory.setCurrentTheme(selectedTheme);
                    factory.activateThemeSkins(); // IMPORTANT
                }
            }
            // register all the skins now
            factory.registerReportSkins();
            factory.registerDialogSkins();
            factory.registerNavigationSkins();
        }
        return factory;
    }

    /**
     * Checks to see is the servlet context has been checked for theme configurations
     * @return
     */
    public boolean isContextLoaded()
    {
        return contextLoaded;
    }

    /**
     *
     * @param load
     */
    public void setContextLoaded(boolean load)
    {
        contextLoaded = load;
    }

    /**
     * Set the current theme
     * @param theme
     */
    public void setCurrentTheme(String theme)
    {
        currentTheme = theme;
    }

    /**
     * Get the current selected theme
     * @return
     */
    public Theme getCurrentTheme()
    {
        return getCurrentTheme(null);
    }

    /**
     * Get the current selected theme. This method ensures that themes configured in the servlet context are
     * searched.
     * @param vc
     * @return
     */
    public Theme getCurrentTheme(ValueContext vc)
    {
        checkThemeStatus(vc);
        return currentTheme != null ? (Theme) themes.get(currentTheme) : null;
    }

    /**
     *
     * @param vc
     */
    private void checkThemeStatus(ValueContext vc)
    {
        if (currentTheme == null && vc != null && !this.isContextLoaded())
        {
            // THEMES are not active so check to see if they've been turned on
            //  in the servlet context. The 'isContextLoaded()' check is to make sure we only check the servlet
            // context once for theme configurations.
            Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
            factory.setContextLoaded(true);
            try
            {
                String appRootPath = config.getTextValue(vc, "app.site-root-path");
                if (appRootPath != null && appRootPath.length() > 0)
                {
                    // look for themes under the SPARX shared resources directory and the application resources directory
                    // and create a map of all available themes
                    discoverResources(appRootPath, "/sparx/resources/theme");
                    discoverResources(appRootPath, "/resources/theme");
                }
                // extract the desired theme information from the application config file
                String selectedTheme = config.getTextValue(vc, "app.ui.theme");
                String selectedStyle = config.getTextValue(vc, "app.ui.theme.style");
                if (selectedTheme != null &&  selectedStyle != null)
                {
                    // see if the theme exist
                    Theme theme = getTheme(selectedTheme);
                    if (theme != null)
                    {
                        // theme exists.. so set the default style and save the theme name as the current one
                        theme.setCurrentStyle(selectedStyle);
                        setCurrentTheme(selectedTheme);
                        activateThemeSkins(); // IMPORTANT
                    }
                }
            }
            catch (Exception e)
            {
                // failed to extract the theme information from the application config file
                e.printStackTrace();
            }
        }
    }
    /**
     * Get a theme object
     * @param name
     * @return
     */
    public Theme getTheme(String name)
    {
        return (Theme) themes.get(name);
    }

    /**
     * Checks to see of Theme based skins are being used
     * @return
     */
    public boolean usingThemeSkins()
    {
        return currentTheme != null ? true : false;
    }
    /**
     * Activate THEME based skins for dialogs, reports, and navigation
     */
    public void activateThemeSkins()
    {
        themeSkins = true;
        registerReportSkins();
        registerDialogSkins();
        registerNavigationSkins();
    }

    /**
     * Register navigation skins
     */
    private void registerNavigationSkins()
    {
        if (currentTheme != null)
        {
            addNavigationSkin("default", new com.netspective.sparx.xaf.theme.HtmlTabbedNavigationSkin());
        }
        else
        {
            addNavigationSkin("default", new com.netspective.sparx.xaf.skin.HtmlTabbedNavigationSkin());
        }
    }

    /**
     * Registers the dialog skins available
     */
    private void registerDialogSkins()
    {
        if (currentTheme != null)
        {
            addDialogSkin("default", new com.netspective.sparx.xaf.theme.ThemeDialogSkin());
            addDialogSkin("stylized", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
            addDialogSkin("standard", new com.netspective.sparx.xaf.skin.StandardDialogSkin());
            addDialogSkin("hand-held", new com.netspective.sparx.xaf.skin.HandHeldDialogSkin());
            addDialogSkin("login", new com.netspective.sparx.xaf.theme.LoginDialogSkin());
        }
        else
        {
            addDialogSkin("default", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
            addDialogSkin("standard", new com.netspective.sparx.xaf.skin.StandardDialogSkin());
            addDialogSkin("hand-held", new com.netspective.sparx.xaf.skin.HandHeldDialogSkin());
            addDialogSkin("login", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
        }
    }

    /**
     * Registers the report skins available for this context
     */
    private void registerReportSkins()
    {

        if (currentTheme != null)
        {
            addReportSkin("report", new com.netspective.sparx.xaf.theme.HtmlReportSkin(true));
            addReportSkin("report-compressed", new com.netspective.sparx.xaf.theme.HtmlReportSkin(false));
            addReportSkin("record-viewer", new com.netspective.sparx.xaf.theme.RecordViewerReportSkin(true));
            addReportSkin("record-viewer-compressed", new com.netspective.sparx.xaf.theme.RecordViewerReportSkin(false));
            addReportSkin("record-editor", new com.netspective.sparx.xaf.theme.RecordEditorReportSkin(true));
            addReportSkin("record-editor-compressed", new com.netspective.sparx.xaf.theme.RecordEditorReportSkin(false));
            addReportSkin("detail", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(true, 1, true));
            addReportSkin("detail-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(false, 1, true));
            addReportSkin("detail-2col", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(true, 2, true));
            addReportSkin("detail-2col-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(false, 2, true));
            addReportSkin("data-only", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportNoCaptionSkin(true, 1, true));
            addReportSkin("data-only-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportNoCaptionSkin(false, 1, true));
            addReportSkin("text-csv", new com.netspective.sparx.xaf.skin.TextReportSkin(".csv", ",", "\"", true));
            addReportSkin("text-tab", new com.netspective.sparx.xaf.skin.TextReportSkin(".txt", "  ", null, true));
        }
        else
        {
            addReportSkin("report", new HtmlReportSkin(true));
            addReportSkin("report-compressed", new HtmlReportSkin(false));

            addReportSkin("component", new HtmlComponentSkin(true));
            addReportSkin("component-compressed", new HtmlComponentSkin(false));

            addReportSkin("detail", new HtmlSingleRowReportSkin(true, 1, true));
            addReportSkin("detail-compressed", new HtmlSingleRowReportSkin(false, 1, true));
            addReportSkin("detail-2col", new HtmlSingleRowReportSkin(true, 2, true));
            addReportSkin("detail-2col-compressed", new HtmlSingleRowReportSkin(false, 2, true));
            addReportSkin("data-only", new HtmlSingleRowReportNoCaptionSkin(true, 1, true));
            addReportSkin("data-only-compressed", new HtmlSingleRowReportNoCaptionSkin(false, 1, true));

            addReportSkin("record-viewer", new RecordViewerReportSkin(true));
            addReportSkin("record-viewer-compressed", new RecordViewerReportSkin(false));
            addReportSkin("record-editor", new RecordEditorReportSkin(true));
            addReportSkin("record-editor-compressed", new RecordEditorReportSkin(false));

            addReportSkin("text-csv", new TextReportSkin(".csv", ",", "\"", true));
            addReportSkin("text-tab", new TextReportSkin(".txt", "  ", null, true));
        }

    }

    /**
     * Get the registered dialog skins
     * @return
     */
    public Map getDialogSkins()
    {
        return dialogSkins;
    }

    /**
     * Get the registered navigation skins
     * @return
     */
    public Map getNavigationSkins()
    {
        return navigationSkins;
    }

    /**
     * Get all registered skins
     * @return
     */
    public Map getReportSkins()
    {
        return reportSkins;
    }

    /**
     * Add a report skin.
     * @param id
     * @param skin
     */
    public void addReportSkin(String id, ReportSkin skin)
    {
        reportSkins.put(id, skin);
    }

    /**
     *
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void addReportSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addReportSkin(id, (ReportSkin) skinClass.newInstance());
    }

    /**
     * Get a report skin
     * @param vc
     * @param id
     * @return
     */
    public ReportSkin getReportSkin(ValueContext vc, String id)
    {
        if (!usingThemeSkins() && !isContextLoaded())
            checkThemeStatus(vc);
        return (ReportSkin) reportSkins.get(id);
    }

    /**
     * Get the default report skin
     * @return
     */
    public ReportSkin getDefaultReportSkin(ValueContext vc)
    {
        return getReportSkin(vc, DEFAULT_REPORT_SKIN_NAME);
    }


    /**
     * Add a dialog skin.
     * @param id
     * @param skin
     */
    public void addDialogSkin(String id, DialogSkin skin)
    {
        dialogSkins.put(id, skin);
    }

    /**
     * Add a dialog skin
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void addDialogSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        dialogSkins.put(id, (DialogSkin) skinClass.newInstance());
    }

    /**
     * Get a dialog skin
     * @param id
     * @return
     */
    public DialogSkin getDialogSkin(ValueContext vc, String id)
    {
        if (!usingThemeSkins() && !isContextLoaded())
            checkThemeStatus(vc);
        return (DialogSkin) dialogSkins.get(id);
    }

    /**
     * Get a dialog skin without a context
     * @param id
     * @return
     */
    public DialogSkin getDialogSkin(String id)
    {
        return (DialogSkin) dialogSkins.get(id);
    }

    /**
     * Get the default dialog skin for this servlet context
     * @return
     */
    public DialogSkin getDialogSkin(ValueContext vc)
    {
        if (!usingThemeSkins() && !isContextLoaded())
            checkThemeStatus(vc);
        return getDialogSkin(DEFAULT_DIALOG_SKIN_NAME);
    }

    /**
     * Get the default dialog skin
     * @return
     */
    public DialogSkin getDialogSkin()
    {
        return getDialogSkin(DEFAULT_DIALOG_SKIN_NAME);
    }

    /**
     * Add a navigation skin without a servlet context
     * @param id
     * @param skin
     */
    public void addNavigationSkin(String id, NavigationPathSkin skin)
    {
        navigationSkins.put(id, skin);
    }

    /**
     * Add a navigation skin
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void addNavigationSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addNavigationSkin(id, (NavigationPathSkin) skinClass.newInstance());
    }

    /**
     * Get a navigation skin
     * @param id
     * @return
     */
    public NavigationPathSkin getNavigationSkin(String id)
    {
        return (NavigationPathSkin) navigationSkins.get(id);
    }

    /**
     * Get a navigation skin for this servlet context
     * @param vc
     * @param id
     * @return
     */
    public NavigationPathSkin getNavigationSkin(ValueContext vc, String id)
    {
        if (!usingThemeSkins() && !isContextLoaded())
        {
            // see if we need to load theme skins
            checkThemeStatus(vc);
        }
        return (NavigationPathSkin) navigationSkins.get(id);
    }

    /**
     * Get the default navigation skin for this servlet context
     * @param vc
     * @return
     */
    public NavigationPathSkin getNavigationSkin(ValueContext vc)
    {
        return getNavigationSkin(vc, DEFAULT_NAVIGATION_SKIN_NAME);
    }

    /**
     * Get the default navigation skin
     * @return
     */
    public NavigationPathSkin getNavigationSkin()
    {
        return getNavigationSkin(DEFAULT_NAVIGATION_SKIN_NAME);
    }

    /**
     *
     * @param parent
     */
    public void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Dialog Skins");
        factoryElem.setAttribute("class", SkinFactory.class.getName());
        for(Iterator i = dialogSkins.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("dialog-skin");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", (entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }

        factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Report Skins");
        factoryElem.setAttribute("class", SkinFactory.class.getName());
        for(Iterator i = reportSkins.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("report-column-format");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", (entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }

        factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Navigation Skins");
        factoryElem.setAttribute("class", SkinFactory.class.getName());
        for(Iterator i = navigationSkins.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("navigation-skin");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", (entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }
    }

    /**
     *
     * @param appRoot
     * @param rootUrl
     */
    private void discoverResources(String appRoot, String rootUrl)
    {
        if (resources == null)
            resources = new HashMap();

        File dir = new File(appRoot + rootUrl);
        File[] files = dir.listFiles();

        for (int i = 0; files != null && i < files.length; i++)
        {
            File file = files[i];
            if (file.isDirectory())
            {
                // This is a THEME
                Theme newTheme = new Theme(file.getName());
                newTheme.setPath(rootUrl + file.getName());
                themes.put(file.getName(), newTheme);

                // Get all the STYLES for this THEME
                File[] styleList = file.listFiles();
                for (int j = 0; j < styleList.length; j++)
                {
                    File styleDir = styleList[j];
                    if (styleDir.isDirectory())
                    {
                        String styleName = styleDir.getName();
                        System.out.println("STYLE Created: " + styleName);
                        newTheme.addStyle(styleName);
                        ThemeStyle style = newTheme.getStyle(styleName);
                        style.setImagePath(rootUrl + "/" + file.getName() + "/" + styleName + "/images");

                        String cssPath = rootUrl + "/" + file.getName() + "/" + styleName + "/css";
                        String imgPath = rootUrl + "/" + file.getName() + "/" + styleName + "/images";

                        File cssDir = new File(appRoot + cssPath);
                        if (cssDir.exists() && cssDir.isDirectory())
                        {
                            Map cssResources = new HashMap();
                            cssResources = discoverThemeStyleResources(null, cssPath, "/", cssDir);
                            style.setCssResources(cssResources);
                            System.out.println(cssPath + " " + cssResources);
                        }

                        File imgDir = new File(appRoot + imgPath);
                        if (imgDir.exists() && imgDir.isDirectory())
                        {
                            Map imageResources = new HashMap();
                            imageResources = discoverThemeStyleResources(null, imgPath, "/", imgDir);
                            style.setImageResources(imageResources);
                        }
                    }
                }
                //discoverThemeStyleResources(inheritResources, rootUrl + file.getName(), "/", file);
            }
        }

        //discoverResources(inheritResources, rootUrl, "/", dir);
    }

    /**
     *
     * @param resources
     * @param rootUrl
     * @param currentPathId
     * @param dir
     */
    private Map discoverThemeStyleResources(Map resources, String rootUrl, String currentPathId, File dir)
    {
        // list all files and directories for the passed in directory
        File[] files = dir.listFiles();

        Map singlePathResources =
                (resources == null ? new HashMap() : (resources.get(currentPathId) == null ? new HashMap() : (Map) resources.get(currentPathId)));

        for (int i = 0; files != null && i < files.length; i++)
        {
            File file = files[i];
            if (file.isDirectory())
            {
                discoverThemeStyleResources(resources, rootUrl, currentPathId + (currentPathId.endsWith("/") ? "" : "/") + file.getName(), file);
            }
            else
            {
                String fileName = file.getName();
                int extnIndex = fileName.lastIndexOf(".");
                String justNameNoExtn = extnIndex == -1 ? fileName : fileName.substring(0, extnIndex);
                singlePathResources.put(justNameNoExtn, rootUrl + currentPathId + (currentPathId.endsWith("/") ? "" : "/") + fileName);
            }
        }
        //rootResource.put(currentPathId, singlePathResources);
        return singlePathResources;
    }
}