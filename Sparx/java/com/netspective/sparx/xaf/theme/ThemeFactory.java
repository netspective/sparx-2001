package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.Globals;
import com.netspective.sparx.xaf.navigate.NavigationPath;

import javax.servlet.Servlet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.File;

/**
 * The ThemeFactory class automatically reads the sparx resources directory and the application resources directory
 * to register all the available themes and their respective styles for the application context.
 *
 * @author Aye Thu
 * Created on Feb 16, 2003 9:32:03 PM
 */
public class ThemeFactory implements Factory
{
    public static final String DEFAULT_THEME_NAME = "sparx";
    public static final String DEFAULT_THEME_STYLE_NAME = "default";

    private Map themes;
    private String currentTheme;
    private static ThemeFactory factory;
    private Map resources;

    private ThemeFactory()
    {
        themes = new HashMap();
    }

    public void setCurrentTheme(String theme)
    {
        System.out.println(theme);
        currentTheme = theme;
    }

    /**
     * Get the current selected theme
     * @return
     */
    public Theme getCurrentTheme()
    {
        System.out.println("----------CURRENT THEME ----- " + currentTheme + " " + themes.size());
        return currentTheme != null ? (Theme) themes.get(currentTheme) : null;
    }

    public Map getThemes()
    {
       return themes;
    }

    /**
     * Get theme
     * @param theme
     * @return
     */
    public Theme getTheme(String theme)
    {
        return theme != null ? (Theme) themes.get(theme) : null;
    }
    /**
     * Get an instance of the Theme factory. There is only one theme factory object created for the servlet context.
     * @param vc
     * @return ThemeFactory
     */
    public static ThemeFactory getInstance(ValueContext vc)
    {
        if (factory == null)
        {
            factory = new ThemeFactory();

            Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
            // look for themes under the SPARX shared resources directory and the application resources directory
            String appRootPath = config.getTextValue(vc, "app.site-root-path");

            // get all the resources related to the themes
            factory.discoverResources(appRootPath, "/sparx/resources/theme");
            factory.discoverResources(appRootPath, "/resources/theme");

            String selectedTheme = null;
            String selectedStyle = null;
            try
            {
                selectedTheme = config.getTextValue(vc, "app.ui.theme");
                selectedStyle = config.getTextValue(vc, "app.ui.theme.style");

                if (selectedTheme != null)
                    factory.setCurrentTheme(selectedTheme);
                if (selectedStyle != null)
                    factory.getCurrentTheme().setCurrentStyle(selectedStyle);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return factory;
    }

    /**
     * Get all theme resources
     * @return Map
     */
    public Map getResources()
    {
        return this.resources;
    }

    /**
     *
     * @param appRoot
     * @param rootUrl
     */
    public void discoverResources(String appRoot, String rootUrl)
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
                            System.out.println(imgPath + " " + imageResources);
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
