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
 * $Id: SkinFactory.java,v 1.10 2003-02-24 03:46:05 aye.thu Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.report.ReportSkin;
import com.netspective.sparx.xaf.navigate.NavigationPathSkin;
import com.netspective.sparx.xaf.theme.ThemeDialogSkin;
import com.netspective.sparx.xaf.theme.ThemeFactory;
import com.netspective.sparx.xaf.theme.Theme;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;

public class SkinFactory implements Factory
{
    private static Map reportSkins = new HashMap();
    private static Map dialogSkins = new HashMap();
    private static Map navigationSkins = new HashMap();

    private static Map contextReportSkins = new HashMap();
    private static Map contextDialogSkins = new HashMap();
    private static Map contextNavigationSkins = new HashMap();

    public static String DEFAULT_DIALOG_SKIN_NAME = "default";
    public static String DEFAULT_REPORT_SKIN_NAME = "report-compressed";
    public static String DEFAULT_NAVIGATION_SKIN_NAME = "default";

    static
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

        addDialogSkin("default", new StylizedDialogSkin());
        addDialogSkin("standard", new StandardDialogSkin());
        addDialogSkin("hand-held", new HandHeldDialogSkin());
        addDialogSkin("theme", new ThemeDialogSkin());

        addNavigationSkin("default", new HtmlTabbedNavigationSkin());
    }

    /**
     * Register navigation skins for this servlet context
     * @param sc
     */
    public static void registerContextNavigationSkins(ServletContext sc)
    {
        ServletValueContext svc = new ServletValueContext(sc, null, null, null);
        ThemeFactory tf = ThemeFactory.getInstance(svc);
        Theme theme = tf.getCurrentTheme();
        if (theme != null)
        {
            addNavigationSkin(sc, "default", new com.netspective.sparx.xaf.theme.HtmlTabbedNavigationSkin());
        }
        else
        {
            addNavigationSkin(sc, "default", new com.netspective.sparx.xaf.skin.HtmlTabbedNavigationSkin());
        }
    }

    /**
     * Registers the dialog skins available for this servlet context
     * @param sc
     */
    public static void registerContextDialogSkins(ServletContext sc)
    {
        ServletValueContext svc = new ServletValueContext(sc, null, null, null);
        ThemeFactory tf = ThemeFactory.getInstance(svc);
        Theme theme = tf.getCurrentTheme();
        if (theme != null)
        {
            addDialogSkin(sc, "default", new com.netspective.sparx.xaf.theme.ThemeDialogSkin());
            addDialogSkin(sc, "stylized", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
            addDialogSkin(sc, "standard", new com.netspective.sparx.xaf.skin.StandardDialogSkin());
            addDialogSkin(sc, "hand-held", new com.netspective.sparx.xaf.skin.HandHeldDialogSkin());
            addDialogSkin(sc, "login", new com.netspective.sparx.xaf.theme.LoginDialogSkin());
        }
        else
        {
            addDialogSkin(sc, "default", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
            addDialogSkin(sc, "standard", new com.netspective.sparx.xaf.skin.StandardDialogSkin());
            addDialogSkin(sc, "hand-held", new com.netspective.sparx.xaf.skin.HandHeldDialogSkin());
            addDialogSkin(sc, "login", new com.netspective.sparx.xaf.skin.StylizedDialogSkin());
        }
    }

    /**
     * Registers the report skins available for this context
     * @param sc
     */
    public static void registerContextReportSkins(ServletContext sc)
    {
        ServletValueContext svc = new ServletValueContext(sc, null, null, null);
        ThemeFactory tf = ThemeFactory.getInstance(svc);
        Theme theme = tf.getCurrentTheme();
        if (theme != null)
        {
            addReportSkin(sc, "report", new com.netspective.sparx.xaf.theme.HtmlReportSkin(true));
            addReportSkin(sc, "report-compressed", new com.netspective.sparx.xaf.theme.HtmlReportSkin(false));
            addReportSkin(sc, "record-viewer", new com.netspective.sparx.xaf.theme.RecordViewerReportSkin(true));
            addReportSkin(sc, "record-viewer-compressed", new com.netspective.sparx.xaf.theme.RecordViewerReportSkin(false));
            addReportSkin(sc, "record-editor", new com.netspective.sparx.xaf.theme.RecordEditorReportSkin(true));
            addReportSkin(sc, "record-editor-compressed", new com.netspective.sparx.xaf.theme.RecordEditorReportSkin(false));
            addReportSkin(sc, "detail", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(true, 1, true));
            addReportSkin(sc, "detail-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(false, 1, true));
            addReportSkin(sc, "detail-2col", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(true, 2, true));
            addReportSkin(sc, "detail-2col-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportSkin(false, 2, true));
            addReportSkin(sc, "data-only", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportNoCaptionSkin(true, 1, true));
            addReportSkin(sc, "data-only-compressed", new com.netspective.sparx.xaf.theme.HtmlSingleRowReportNoCaptionSkin(false, 1, true));
            addReportSkin(sc, "text-csv", new com.netspective.sparx.xaf.skin.TextReportSkin(".csv", ",", "\"", true));
            addReportSkin(sc, "text-tab", new com.netspective.sparx.xaf.skin.TextReportSkin(".txt", "  ", null, true));
        }

    }

    public static Map getDialogSkins()
    {
        return dialogSkins;
    }

    public static Map getDialogSkins(ServletContext sc)
    {
        if (contextDialogSkins == null)
            registerContextDialogSkins(sc);
        return dialogSkins;
    }


    public static Map getNavigationSkins()
    {
        return navigationSkins;
    }

    public static Map getReportSkins()
    {
        return reportSkins;
    }

    /**
     *
     * @param id
     * @param skin
     */
    public static void addReportSkin(String id, ReportSkin skin)
    {
        addReportSkin(null, id, skin);
    }

    /**
     *
     * @param sc
     * @param id
     * @param skin
     */
    public static void addReportSkin(ServletContext sc, String id, ReportSkin skin)
    {
        if (sc != null)
            contextReportSkins.put(id, skin);
        else
            reportSkins.put(id, skin);
    }


    public static void addReportSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addReportSkin(id, (ReportSkin) skinClass.newInstance());
    }

    public static ReportSkin getReportSkin(ServletContext sc, String id)
    {
        if (sc != null)
        {
            if (contextReportSkins == null || contextReportSkins.size() == 0)
                registerContextReportSkins(sc);
            return (ReportSkin) contextReportSkins.get(id);
        }
        else
            return (ReportSkin) reportSkins.get(id);
    }

    public static ReportSkin getReportSkin(String id)
    {
        return getReportSkin(null, id);
    }

    public static ReportSkin getDefaultReportSkin()
    {
        return getReportSkin(DEFAULT_REPORT_SKIN_NAME);
    }

    public static ReportSkin getDefaultReportSkin(ServletContext sc)
    {
        return getReportSkin(sc, DEFAULT_REPORT_SKIN_NAME);
    }


    /**
     * Add a dialog skin without a servlet context
     * @param id
     * @param skin
     */
    public static void addDialogSkin(String id, DialogSkin skin)
    {
        addDialogSkin(null, id, skin);
    }

    /**
     * Add a dialog skin for this context
     * @param id
     * @param skin
     */
    public static void addDialogSkin(ServletContext sc, String id, DialogSkin skin)
    {
        if (sc != null)
            contextDialogSkins.put(id, skin);
        else
            dialogSkins.put(id, skin);

    }

    /**
     * Add a dialog skin without a servlet context
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addDialogSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        addDialogSkin(null, id, className);
    }

    /**
     * Add a dialog skin with a servlet context
     * @param sc
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addDialogSkin(ServletContext sc, String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addDialogSkin(sc, id, (DialogSkin) skinClass.newInstance());
    }

    /**
     * Get a dialog skin with respect to the current context
     * @param id
     * @return
     */
    public static DialogSkin getDialogSkin(ServletContext sc, String id)
    {
        if (sc == null)
        {
            // get dialog skins  registered without a context
            return (DialogSkin) dialogSkins.get(id);
        }
        else
        {
            if (contextDialogSkins == null || contextDialogSkins.size() == 0)
                registerContextDialogSkins(sc);
            return (DialogSkin) contextDialogSkins.get(id);
        }
    }

    /**
     * Get a dialog skin without a context
     * @param id
     * @return
     */
    public static DialogSkin getDialogSkin(String id)
    {
        return getDialogSkin(null, id);
    }

    /**
     * Get the default dialog skin for this servlet context
     * @return
     */
    public static DialogSkin getDialogSkin(ServletContext vc)
    {
        return getDialogSkin(vc, DEFAULT_DIALOG_SKIN_NAME);
    }

    /**
     * Get the default dialog skin
     * @return
     */
    public static DialogSkin getDialogSkin()
    {
        return getDialogSkin(DEFAULT_DIALOG_SKIN_NAME);
    }

    /**
     * Add a navigation skin without a servlet context
     * @param id
     * @param skin
     */
    public static void addNavigationSkin(String id, NavigationPathSkin skin)
    {
        addNavigationSkin(null, id, skin);
    }

    /**
     * Add a navigation skin for this servlet context
     * @param sc servlet context
     * @param id
     * @param skin
     */
    public static void addNavigationSkin(ServletContext sc, String id, NavigationPathSkin skin)
    {
        if (sc != null)
        {
            contextNavigationSkins.put(id, skin);
        }
        else
        {
            navigationSkins.put(id, skin);
        }
    }

    /**
     *Add a navigation context for this servlet context
     * @param sc servlet context
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addNavigationSkin(ServletContext sc, String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addNavigationSkin(sc, id, (NavigationPathSkin) skinClass.newInstance());
    }

    /**
     * Add a navigation skin
     * @param id
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addNavigationSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addNavigationSkin(id, (NavigationPathSkin) skinClass.newInstance());
    }

    /**
     * Get a navigation skin
     * @param id
     * @return
     */
    public static NavigationPathSkin getNavigationSkin(String id)
    {
        return getNavigationSkin(null, id);
    }

    /**
     * Get a navigation skin for this servlet context
     * @param vc
     * @param id
     * @return
     */
    public static NavigationPathSkin getNavigationSkin(ServletContext vc, String id)
    {
        if (vc == null)
        {
            return (NavigationPathSkin) navigationSkins.get(id);
        }
        else
        {
            if (contextNavigationSkins == null || contextNavigationSkins.size() == 0)
                registerContextNavigationSkins(vc);
            return (NavigationPathSkin) contextNavigationSkins.get(id);
        }
    }

    /**
     * Get the default navigation skin for this servlet context
     * @param vc
     * @return
     */
    public static NavigationPathSkin getNavigationSkin(ServletContext vc)
    {
        return getNavigationSkin(vc, DEFAULT_NAVIGATION_SKIN_NAME);
    }

    /**
     * Get the default navigation skin
     * @return
     */
    public static NavigationPathSkin getNavigationSkin()
    {
        return getNavigationSkin(DEFAULT_NAVIGATION_SKIN_NAME);
    }

    public static void createCatalog(Element parent)
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

}