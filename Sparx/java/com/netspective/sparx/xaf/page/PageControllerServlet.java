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
 * $Id: PageControllerServlet.java,v 1.7 2002-11-03 23:26:42 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.page;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.Servlet;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;

import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.security.LoginDialog;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

/**
 * A servlet that dispatches control of execution to various Task objects. The
 * following configuration properties are used from the default configuration:
 *
 *   <ol>
 *   <li>app.controller (this prefix can be controlled by getConfigItemsPrefix())
 *   <li><code>app.controller.dialog-class</code> is a class name
 *   <li><code>app.controller.login.user.session-attr-name</code> is the name of
 *   the session variable used to store user information (AuthenticatedUser)
 *   <li><code>app.controller.login.dialog-skin-name</code> is the name of the
 *   skin used to display the login dialog.
 *   <li><code>app.controller.logout.param-name</code> is the name of the request
 *   parameter that will signify that a logout should be performed.
 *   <li><code>app.controller.logout.redirect</code> is the URL of the page that
 *   the user should be redirected immediately after logout
 *   </ol>
 *
 */

public class PageControllerServlet extends HttpServlet implements FilenameFilter
{
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private static final String CONFIGITEM_DEFAULT_PREFIX = "app.controller.";
    private static final String CONFIGITEM_DISCOVER_REDISCOVER = "discover.rediscover-param-name";
    private static final String CONFIGITEM_DISCOVER_ROOTPATH = "discover.root-path";
    private static final String CONFIGITEM_DISCOVER_ROOTPKG = "discover.root-package";
    private static final String CONFIGITEM_LOGIN_DIALOGCLASS = "login.dialog-class";
    private static final String CONFIGITEM_LOGIN_DIALOGIMAGE = "login.dialog-image";
    private static final String CONFIGITEM_LOGIN_DIALOGSKIN = "login.dialog-skin-name";
    private static final String CONFIGITEM_LOGIN_SESSATTR = "login.user.session-attr-name";
    private static final String CONFIGITEM_LOGOUT_PARAMNAME = "logout.param-name";
    private static final String CONFIGITEM_LOGOUT_REDIRECT = "logout.redirect";

    private static final String DEFAULT_LOGOUT_PARAMNAME = "_logout";
    private static final String DEFAULT_REDISCOVER_PARAMNAME = "_rediscover";

    private LoginDialog loginDialog;
    private String loginDialogSkinName;
    private String logoutParamName;
    private String logoutRedirect;

    private String rediscoverParamName;
    private VirtualPath pagesPath = new VirtualPath();

    protected AppServerLogger debugLog;
    protected AppServerLogger monitorLog;

    protected boolean firstRequest = true;
    protected ConfigurationManager manager;
    protected Configuration appConfig;
    protected String sharedImagesRootURL;
    protected String sharedScriptsRootURL;
    protected String sharedCssRootURL;

    public AppServerLogger getDebugLog()
    {
        return debugLog;
    }

    public AppServerLogger getMonitorLog()
    {
        return monitorLog;
    }

    public void createLogs()
    {
        debugLog = (AppServerLogger) AppServerLogger.getLogger(LogManager.DEBUG_PAGE);
        monitorLog = (AppServerLogger) AppServerLogger.getLogger(LogManager.MONITOR_PAGE);
    }

    public String getConfigItemsPrefix()
    {
        return CONFIGITEM_DEFAULT_PREFIX;
    }

    public String getDefaultContentType()
    {
        return DEFAULT_CONTENT_TYPE;
    }

    public final VirtualPath getPagesPath()
    {
        return pagesPath;
    }

    public final Configuration getAppConfig()
    {
        return appConfig;
    }

    public final String getSharedScriptsRootURL()
    {
        return sharedScriptsRootURL;
    }

    public final String getSharedImagesRootURL()
    {
        return sharedImagesRootURL;
    }

    public final String getSharedCssRootURL()
    {
        return sharedCssRootURL;
    }

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        createLogs();

        ServletContext context = config.getServletContext();
        manager = ConfigurationManagerFactory.getManager(context);
        if(manager == null)
            throw new ServletException("Unable to obtain a ConfigurationManager");
        appConfig = manager.getDefaultConfiguration();
        if(appConfig == null)
            throw new ServletException("Unable to obtain the default Configuration");

        String configItemsPrefix = getConfigItemsPrefix();
        ValueContext vc = new ServletValueContext(context, this, null, null);
        String loginDialogClassName = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGCLASS);

        if(loginDialogClassName != null)
        {
            String loginDialogUserInfoAttrName = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_SESSATTR, null);
            loginDialogSkinName = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGSKIN, null);
            logoutParamName = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_LOGOUT_PARAMNAME, DEFAULT_LOGOUT_PARAMNAME);

            try
            {
                Class loginDialogClass = Class.forName(loginDialogClassName);
                loginDialog = (LoginDialog) loginDialogClass.newInstance();
                loginDialog.initialize();
                if(loginDialogUserInfoAttrName != null)
                    loginDialog.setUserInfoSessionAttrName(loginDialogUserInfoAttrName);
                String imageSrc = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGIMAGE, null);
                if(imageSrc != null)
                    loginDialog.setImageSrc(imageSrc);
            }
            catch(Exception e)
            {
                LogManager.recordException(this.getClass(), "init", "Unable to instantiate login dialog class '" + loginDialogClassName + "' not found", e);
                throw new ServletException(e);
            }
        }

        rediscoverParamName = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_REDISCOVER, DEFAULT_REDISCOVER_PARAMNAME);
        registerPages(config);
    }

    public void registerPages(ServletConfig config) throws ServletException
    {
        discoverPages(null, null);
    }

    public boolean accept(File dir, String name)
    {
        if(name.equals("CVS"))
            return false;

        return
                name.indexOf(".") == -1 || // these will be directories (files with no extensions)
                name.endsWith(".java") ||
                name.endsWith(".class");
    }

    protected void discoverPages(List taskClasses, String pkgName, File dir) throws ServletException
    {
        String classPackage = (pkgName != null && pkgName.length() > 0) ? (pkgName + ".") : "";

        ServletContext sc = getServletContext();
        Set handled = new HashSet();

        sc.log("[discover] Entering " + dir.getAbsolutePath());
        File[] files = dir.listFiles(this);
        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];
            if(file.isDirectory())
                discoverPages(taskClasses, classPackage + file.getName(), file);
            else
            {
                String fileName = file.getName();
                int extnIndex = fileName.lastIndexOf(".");
                String justNameNoExtn = extnIndex == -1 ? fileName : fileName.substring(0, extnIndex);

                /* because we're processing both .java and .class files, lets
				   not try and handle them twice in case they're in the same
				   directory
				*/
                if(handled.contains(justNameNoExtn))
                    continue;

                String className = classPackage + justNameNoExtn;
                handled.add(justNameNoExtn);
                try
                {
                    taskClasses.add(Class.forName(className));
                    sc.log("[discover] Found " + className);
                }
                catch(ClassNotFoundException e)
                {
                    sc.log("[discover] ** Unable to load class '" + className + "'");
                }
            }
        }
    }

    /**
     * Called when the first request is made to this servlet
     */
    protected void initFirstRequest(HttpServletRequest req, HttpServletResponse resp)
    {
        ValueContext vc = new ServletValueContext(getServletContext(), this, req, resp);
        sharedImagesRootURL = appConfig.getTextValue(vc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "images-url");
        sharedScriptsRootURL = appConfig.getTextValue(vc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "scripts-url");
        sharedCssRootURL = appConfig.getTextValue(vc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "css-url");
    }

    protected void discoverPages(HttpServletRequest req, HttpServletResponse resp) throws ServletException
    {
        ServletContext sc = getServletContext();
        ValueContext vc = new ServletValueContext(sc, this, req, resp);

        String configItemsPrefix = getConfigItemsPrefix();
        String rootPath = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPATH);
        String rootPkg = appConfig.getTextValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPKG);

        sc.log("[discover] rootPath = " + rootPath + " (" + configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPATH + ")");
        sc.log("[discover] rootPkg = " + rootPkg + " (" + configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPKG + ")");

        if(rootPath != null)
        {
            List tasks = new ArrayList();
            discoverPages(tasks, rootPkg, new File(rootPath));

            for(Iterator i = tasks.iterator(); i.hasNext();)
            {
                Class cls = (Class) i.next();
                if(!cls.isInterface() && ServletPage.class.isAssignableFrom(cls))
                {
                    sc.log("[discover] found ServletPage " + cls.getName());
                    try
                    {
                        ServletPage page = (ServletPage) cls.newInstance();
                        page.registerPage(this, pagesPath);
                    }
                    catch(InstantiationException e)
                    {
                        sc.log("[discover] " + e.toString());
                    }
                    catch(IllegalAccessException e)
                    {
                        sc.log("[discover] " + e.toString());
                    }
                }
                else
                {
                    sc.log("[discover] " + cls.getName() + " is not a ServletPage");
                }
            }
        }

        sc.log("[discover] pagesPath has " + pagesPath.getChildrenMap().size() + " children");
        for(Iterator i = pagesPath.getAbsolutePathsMap().entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            VirtualPath path = (VirtualPath) entry.getValue();
            sc.log("[discover] mapped '" + entry.getKey().toString() + "' to " + path.toString() + " [" + path.getChildrenMap().size() + " children]" + (path.getPage() != null ? " (has page)" : ""));
        }
    }

    protected boolean doLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        if(loginDialog == null)
            return false;

        String logout = req.getParameter(logoutParamName);
        if(logout != null)
        {
            ValueContext vc = new ServletValueContext(getServletContext(), this, req, resp);
            loginDialog.logout(vc);

            /** If the logout parameter included a non-zero length value, then
             *  we'll redirect to the value provided. Otherwise, we'll try to
             *  find the redirect URL in the default configuration. If neither
             *  a value in the logout parameter nor a configuration item was
             *  found, then the logout will redirect back to this servlet.
             */
            if(logout.length() == 0 || logout.equals("1") || logout.equals("yes"))
            {
                String redirect = appConfig.getTextValue(vc, getConfigItemsPrefix() + CONFIGITEM_LOGOUT_REDIRECT);
                if(redirect != null)
                    resp.sendRedirect(redirect);
                else
                    resp.sendRedirect(req.getContextPath() + req.getServletPath());
            }
            else
            {
                resp.sendRedirect(logout);
            }
            return true;
        }

        ServletContext servletContext = getServletContext();
        if(!loginDialog.accessAllowed(servletContext, req, resp))
        {
            DialogContext dc = loginDialog.createContext(servletContext, this, req, resp, loginDialogSkinName == null ? loginDialog.getSkin() : SkinFactory.getDialogSkin(loginDialogSkinName));
            loginDialog.prepareContext(dc);
            if(dc.inExecuteMode())
            {
                loginDialog.execute(dc);
            }
            else
            {
                loginDialog.producePage(resp.getWriter(), dc);
                return true;
            }
        }

        return false;
    }

    public static boolean handleDefaultBodyItem(ServletContext context, Servlet servlet, ServletRequest req, ServletResponse resp) throws IOException, StatementNotFoundException, NamingException, SQLException
    {
        String pageCmdReqParam = req.getParameter(DialogManagerFactory.DialogCommands.PAGE_COMMAND_REQUEST_PARAM_NAME);
        if(pageCmdReqParam == null)
            return false;

        String pageCmd = "unknown";
        String pageCmdParam = null;
        int cmdDelimPos = pageCmdReqParam.indexOf(",");
        if(cmdDelimPos != -1)
        {
            pageCmd = pageCmdReqParam.substring(0, cmdDelimPos);
            pageCmdParam = pageCmdReqParam.substring(cmdDelimPos+1);
        }

        // a "standard" page command needs to be handled
        if(pageCmd.equals("dialog"))
        {
            DialogManagerFactory.DialogCommands dcmd = DialogManagerFactory.getCommands(pageCmdParam);
            dcmd.handleDialog(new ServletValueContext(context, servlet, req, resp));
        }
        else if(pageCmd.equals("qd-dialog"))
        {
            StatementManagerFactory.QuerySelectDialogCommands dcmd = StatementManagerFactory.getQuerySelectDialogCommands(pageCmdParam);
            dcmd.handleDialog(new ServletValueContext(context, servlet, req, resp));
        }
        else if(pageCmd.equals("statement"))
        {
            StatementManagerFactory.StatementCommands scmd = StatementManagerFactory.getStatementCommands(pageCmdParam);
            scmd.handleStatement(new ServletValueContext(context, servlet, req, resp));
        }
        else
        {
            resp.getWriter().write("Page command '" + pageCmd + "' not recognized.");
            return false;
        }

        return true;
    }

    /**
     * Return the active path that should be located by the VirtualPath.findResults method in PageContext constructor.
     * This is provided as a method to allow overriding by children that may want to prepend servlets or contexts when
     * searching for active Path.
     */
    public String getActivePathToFind(HttpServletRequest req)
    {
        return req.getPathInfo();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if(firstRequest)
        {
            initFirstRequest(req, resp);
            firstRequest = false;
        }

        long startTime = 0;
        if(monitorLog.isInfoEnabled())
            startTime = new Date().getTime();

        org.apache.log4j.NDC.push(req.getSession(true).getId());

        String rediscover = req.getParameter(rediscoverParamName);
        if(rediscover != null)
            discoverPages(req, resp);

        resp.setContentType(getDefaultContentType());

        PageContext pc = new PageContext(this, req, resp);
        VirtualPath.FindResults activePathResults = pc.getActivePath();
        VirtualPath activePath = activePathResults.getMatchedPath();
        ServletPage page = null;
        if(activePath != null)
        {
            page = activePath.getPage();
            if(page == null)
            {
                resp.getWriter().print("Found a path object for '" + activePath.getAbsolutePath() + "' but could not find a ServletPage.");
                org.apache.log4j.NDC.pop();
                return;
            }

            if(page.requireLogin(pc))
            {
                if(doLogin(req, resp))
                {
                    org.apache.log4j.NDC.pop();
                    return;
                }
            }

            page.handlePage(pc);
        }
        else
        {
            resp.getWriter().print("Unable to find a ServletPage to match this URL path. ("+ activePathResults.getSearchedForPath() +")");
            if(ConfigurationManagerFactory.isDevelopmentEnvironment(getServletContext()))
                resp.getWriter().print(pagesPath.getDebugHtml(pc));
        }

        LogManager.recordAccess(req, monitorLog, page != null ? page.getClass().getName() : this.getClass().getName(), req.getRequestURI(), startTime);
        org.apache.log4j.NDC.pop();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req, resp);
    }

}