package com.xaf.navigate;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class NavigateFileSystemServlet extends HttpServlet implements FilenameFilter
{
    private static final String CONTENT_TYPE = "text/html";

	protected ConfigurationManager manager;
	protected Configuration appConfig;
	protected String skinJspPageName;
	protected String rootPath;
	protected String rootURL;
	protected String rootCaption;
	protected HashSet excludeEntryNames = new HashSet();
	protected Hashtable fileTypeIcons = new Hashtable();
	protected LoginDialog loginDialog;

    public void init(ServletConfig config) throws javax.servlet.ServletException
	{
        super.init(config);

		loginDialog = new LoginDialog();

		ServletContext context = config.getServletContext();
		manager = ConfigurationManagerFactory.getManager(context);
		if(manager == null)
			throw new ServletException("Unable to obtain a ConfigurationManager");
		appConfig = manager.getDefaultConfiguration();
		if(appConfig == null)
			throw new ServletException("Unable to obtain the default Configuration");

		ValueContext vc = new ServletValueContext(context, null, null, null);
		skinJspPageName = appConfig.getValue(vc, "app.navigate.skin-jsp");
		rootURL = appConfig.getValue(vc, "app.navigate.root-url");
		rootPath = appConfig.getValue(vc, "app.navigate.root-path");
		rootCaption = appConfig.getValue(vc, "app.navigate.root-caption");

		excludeEntryNames.add(skinJspPageName);
		excludeEntryNames.add("WEB-INF");
		excludeEntryNames.add("resources");
		excludeEntryNames.add("index.jsp");

		//fileTypeIcons.put("*", "");
    }

	/* called in FileSystemContext when Filenames need to be filtered */

	public boolean accept(File dir, String name)
	{
		return ! excludeEntryNames.contains(name);
	}

	public String getParentsHtml(HttpServletRequest req, FileSystemContext fsContext)
	{
		StringBuffer parentsHtml = new StringBuffer();
		String rootURI = fsContext.getRootURI();
		ArrayList parentList = fsContext.getActivePath().getParents();
		if(parentList != null)
		{
			Iterator i = parentList.iterator();
			while(i.hasNext())
			{
				FileSystemEntry entry = (FileSystemEntry) i.next();
				if(entry.isRoot())
					parentsHtml.append("<a class='nfs_parent' href='"+ rootURI + entry.getEntryURI() +"'><img src='/shared/resources/images/navigate/home-sm.gif' border='0'></a> <a href='"+ rootURI + entry.getEntryURI() +"'>Home</a>");
				else
					parentsHtml.append("<a class='nfs_parent' href='"+ rootURI + entry.getEntryURI() +"'>"+ entry.getEntryCaption() +"</a>");

				if(i.hasNext())
					parentsHtml.append("&nbsp;<img src='/shared/resources/images/navigate/parent-separator.gif'>&nbsp;");
			}
		}

		return parentsHtml.toString();
	}

	public String getChildrenHtml(HttpServletRequest req, FileSystemContext fsContext)
	{
		StringBuffer folderRows = new StringBuffer();
		StringBuffer fileRows = new StringBuffer();
		String rootFolderURI = req.getContextPath() + req.getServletPath();
		String rootFileURI = req.getContextPath();

		File[] entries = fsContext.getActivePath().listFiles(this);
		if(entries != null)
		{
			for(int i = 0; i < entries.length; i++)
			{
				FileSystemEntry entry = new FileSystemEntry(fsContext.getRootPath(), entries[i].getAbsolutePath());
				if(entry.isDirectory())
					folderRows.append("<tr><td><img src='/shared/resources/images/navigate/folder-orange-closed.gif' border='0'></td><td class='nfs_child_folder_caption'><a class='nfs_child_folder' href='"+ rootFolderURI + entry.getEntryURI() +"'>"+ entry.getEntryCaption() +"</a></td></tr>");
				else
				{
					String icon = (String) fileTypeIcons.get(entry.getEntryType());
					if(icon == null) icon = "/shared/resources/images/navigate/page-yellow.gif";
					fileRows.append("<tr><td><img src='"+ icon +"' border='0'></td><td class='nfs_child_file_caption'><a class='nfs_child_file' href='"+ rootFolderURI + entry.getEntryURI() +"'>"+ entry.getEntryCaption() +"</a></td></tr>");
				}
			}
		}

		return "<table>" + folderRows.toString() + fileRows.toString() + "</table>";
	}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
		resp.setContentType(CONTENT_TYPE);

		String relativePath = req.getPathInfo();
		FileSystemContext fsContext = new FileSystemContext(req.getContextPath() + req.getServletPath(), rootPath, rootCaption, relativePath);

		req.getSession().setAttribute("NavigateFileSystemServlet", this);
		req.getSession().setAttribute("NavigateFileSystemServlet.fsContext", fsContext);

		ServletContext servletContext = getServletContext();
		if(! loginDialog.accessAllowed(req, resp, servletContext))
		{
			DialogContext dc = new DialogContext(servletContext, this, req, resp, loginDialog, SkinFactory.getDialogSkin());
			loginDialog.prepareContext(dc);
		    if(dc.inExecuteMode())
			{
				loginDialog.execute(dc);
			}
			else
			{
				loginDialog.producePage(dc, resp.getWriter());
				return;
			}
		}

		FileSystemEntry activeEntry = fsContext.getActivePath();
		if(activeEntry.isDirectory())
		{
			PrintWriter out = resp.getWriter();
			String skinJspPage = fsContext.getActivePath().findInPath(skinJspPageName);
			if(skinJspPage == null)
			{
				out.println("<b>NavigateFileSystem page processor '" + skinJspPageName + "' (specified as 'navigate.skin-jsp' initParameter) not found in '"+ fsContext.getActivePath().getAbsolutePath() + "' or any of its parent directories.</b> Showing servlet defaults instead.<p>");
				out.println("<h1>"+ activeEntry.getEntryCaption() +"</h1>" + getParentsHtml(req, fsContext) + "<p>" + getChildrenHtml(req, fsContext));
				return;
			}

			// since the skinJspPage is an absolute path/file we need to convert it to a URL relative to the application
			skinJspPage = skinJspPage.substring(fsContext.getRootPath().getAbsolutePath().length()).replace('\\', '/');

			RequestDispatcher rd = getServletContext().getRequestDispatcher(skinJspPage);
			rd.include(req, resp);
		}
		else
		{
			resp.sendRedirect(req.getContextPath() + activeEntry.getEntryURI());
		}
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
		doGet(req, resp);
    }
}

