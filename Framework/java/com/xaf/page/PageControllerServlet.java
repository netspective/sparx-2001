package com.xaf.page;

import java.io.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.log.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;

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

	private static final String CONFIGITEM_DEFAULT_PREFIX      = "app.controller.";
	private static final String CONFIGITEM_DISCOVER_REDISCOVER = "discover.rediscover-param-name";
	private static final String CONFIGITEM_DISCOVER_ROOTPATH   = "discover.root-path";
	private static final String CONFIGITEM_DISCOVER_ROOTPKG    = "discover.root-package";
	private static final String CONFIGITEM_LOGIN_DIALOGCLASS   = "login.dialog-class";
	private static final String CONFIGITEM_LOGIN_DIALOGIMAGE   = "login.dialog-image";
	private static final String CONFIGITEM_LOGIN_DIALOGSKIN    = "login.dialog-skin-name";
	private static final String CONFIGITEM_LOGIN_SESSATTR      = "login.user.session-attr-name";
	private static final String CONFIGITEM_LOGOUT_PARAMNAME    = "logout.param-name";
	private static final String CONFIGITEM_LOGOUT_REDIRECT     = "logout.redirect";

	private static final String DEFAULT_LOGOUT_PARAMNAME       = "_logout";
	private static final String DEFAULT_REDISCOVER_PARAMNAME   = "_rediscover";

	private LoginDialog loginDialog;
	private String loginDialogSkinName;
	private String logoutParamName;
	private String logoutRedirect;

	private String rediscoverParamName;
	private VirtualPath pagesPath = new VirtualPath();

	protected AppServerCategory debugLog;
	protected AppServerCategory monitorLog;

	protected ConfigurationManager manager;
	protected Configuration appConfig;
	protected String sharedImagesRootURL;
	protected String sharedScriptsRootURL;
	protected String sharedCssRootURL;

	public AppServerCategory getDebugLog()
	{
		return debugLog;
	}

	public AppServerCategory getMonitorLog()
	{
		return monitorLog;
	}

	public void createLogs()
	{
		debugLog = (AppServerCategory) AppServerCategory.getInstance(LogManager.DEBUG_PAGE);
		monitorLog = (AppServerCategory) AppServerCategory.getInstance(LogManager.MONITOR_PAGE);
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
		String loginDialogClassName = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGCLASS);

		if(loginDialogClassName != null)
		{
			String loginDialogUserInfoAttrName = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_SESSATTR);
			loginDialogSkinName = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGSKIN);
	    	logoutParamName = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_LOGOUT_PARAMNAME, DEFAULT_LOGOUT_PARAMNAME);

			try
			{
				Class loginDialogClass = Class.forName(loginDialogClassName);
				loginDialog = (LoginDialog) loginDialogClass.newInstance();
				loginDialog.initialize();
				if(loginDialogUserInfoAttrName != null)
					loginDialog.setUserInfoSessionAttrName(loginDialogUserInfoAttrName);
				String imageSrc = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_LOGIN_DIALOGIMAGE);
				if(imageSrc != null)
					loginDialog.setImageSrc(imageSrc);
			}
			catch(Exception e)
			{
				debugLog.error("Unable to instantiate login dialog class '"+loginDialogClassName+"' not found", e);
				throw new ServletException(e);
			}
		}

		rediscoverParamName = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_REDISCOVER, DEFAULT_REDISCOVER_PARAMNAME);
		sharedImagesRootURL = appConfig.getValue(vc, "framework.shared.images-url");
		sharedScriptsRootURL = appConfig.getValue(vc, "framework.shared.scripts-url");
		sharedCssRootURL = appConfig.getValue(vc, "framework.shared.css-url");

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

	protected void discoverPages(HttpServletRequest req, HttpServletResponse resp) throws ServletException
	{
		ServletContext sc = getServletContext();
		ValueContext vc = new ServletValueContext(sc, this, req, resp);

		String configItemsPrefix = getConfigItemsPrefix();
    	String rootPath = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPATH);
	   	String rootPkg  = appConfig.getValue(vc, configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPKG);

		sc.log("[discover] rootPath = " + rootPath + " (" + configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPATH + ")");
		sc.log("[discover] rootPkg = " + rootPkg + " (" + configItemsPrefix + CONFIGITEM_DISCOVER_ROOTPKG + ")");

		if(rootPath != null)
		{
			List tasks = new ArrayList();
			discoverPages(tasks, rootPkg, new File(rootPath));

			for(Iterator i = tasks.iterator(); i.hasNext(); )
			{
				Class cls = (Class) i.next();
				if(! cls.isInterface() && ServletPage.class.isAssignableFrom(cls))
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

		sc.log("[discover] pagesPath has "+ pagesPath.getChildrenMap().size() +" children");
		for(Iterator i = pagesPath.getAbsolutePathsMap().entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();
			VirtualPath path = (VirtualPath) entry.getValue();
			sc.log("[discover] mapped '"+ entry.getKey().toString() +"' to " + path.toString() + " ["+ path.getChildrenMap().size() +" children]" + (path.getPage() != null ? " (has page)" : ""));
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
				String redirect = appConfig.getValue(vc, getConfigItemsPrefix() + CONFIGITEM_LOGOUT_REDIRECT);
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
		if(! loginDialog.accessAllowed(servletContext, req, resp))
		{
			DialogContext dc = new DialogContext(servletContext, this, req, resp, loginDialog, loginDialogSkinName == null ? loginDialog.getSkin() : SkinFactory.getDialogSkin(loginDialogSkinName));
			loginDialog.prepareContext(dc);
			if(dc.inExecuteMode())
			{
				loginDialog.execute(dc);
			}
			else
			{
				loginDialog.producePage(dc, resp.getWriter());
				return true;
			}
		}

		return false;
	}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
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
				resp.getWriter().print("Found a path object for '"+ activePath.getAbsolutePath() +"' but could not find a ServletPage.");
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
			resp.getWriter().print("Unable to find a ServletPage to match this URL path.");
		}

		LogManager.recordAccess(req, monitorLog, page != null ? page.getClass().getName() : this.getClass().getName(), req.getRequestURI(), startTime);
		org.apache.log4j.NDC.pop();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	doGet(req, resp);
    }

}