package com.xaf.navigate.taglib;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.form.*;
import com.xaf.security.*;
import com.xaf.log.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class PageTag extends TagSupport
{
	static public final String PAGE_COMMAND_REQUEST_PARAM_NAME = "page-cmd";
	static public final String PAGECMD_DLG_DIALOGNAME_PARAM_NAME = "dlg-name";
	static public final String PAGECMD_DLG_DIALOGSKIN_PARAM_NAME = "dlg-skin";
	static public final String PAGE_SECURITY_MESSAGE_ATTRNAME = "security-message";
	static public final String PAGE_DEFAULT_LOGIN_DIALOG_CLASS = "com.xaf.security.LoginDialog";

	static public final String[] DIALOG_COMMAND_RETAIN_PARAMS =
	{
		PAGE_COMMAND_REQUEST_PARAM_NAME,
		PAGECMD_DLG_DIALOGNAME_PARAM_NAME,
		PAGECMD_DLG_DIALOGSKIN_PARAM_NAME
	};

	static private LoginDialog loginDialog;

	private String title;
	private String heading;
	private String[] permissions;
    private long startTime;

	public void release()
	{
		super.release();
		title = null;
		heading = null;
		permissions = null;
	}

	public final String getTitle() { return title; }
	public final String getHeading() { return heading; }

	public void setTitle(String value) { title = value; }
	public void setHeading(String value) { heading = value;	}

	public final String[] getPermissions() { return permissions; }
	public void setPermission(String value)
	{
		if(value == null || value.length() == 0)
			return;

		List perms = new ArrayList();
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens())
		{
			perms.add(st.nextToken());
		}
		permissions = (String[]) perms.toArray(new String[perms.size()]);
	}

	public String getLoginDialogClassName()
	{
		return PAGE_DEFAULT_LOGIN_DIALOG_CLASS;
	}

	public String getLoginDialogSkinName()
	{
		return null; // the "default" skin
 	}

	protected boolean doLogin(ServletContext servletContext, Servlet page, HttpServletRequest req, HttpServletResponse resp) throws IOException, JspException
	{
		if(loginDialog == null)
		{
			String className = getLoginDialogClassName();
			try
			{
			    Class loginDialogClass = Class.forName(className);
			    loginDialog = (LoginDialog) loginDialogClass.newInstance();
			}
			catch(ClassNotFoundException e)
			{
				throw new JspException("Login dialog class '"+ className +"' not found in classpath.");
			}
			catch(IllegalAccessException e)
			{
				throw new JspException("Unable to access login dialog class '"+ className +"'.");
			}
			catch(InstantiationException e)
			{
				throw new JspException("Unable to instantiate login dialog class '"+ className +"'.");
			}
			loginDialog.initialize();
		}

		String logout = req.getParameter("_logout");
		if(logout != null)
		{
			ValueContext vc = new ServletValueContext(servletContext, page, req, resp);
			loginDialog.logout(vc);

			/** If the logout parameter included a non-zero length value, then
			 *  we'll redirect to the value provided.
			 */
			if(logout.length() == 0 || logout.equals("1") || logout.equals("yes"))
				resp.sendRedirect(req.getContextPath());
			else
				resp.sendRedirect(logout);
			return true;
		}

		if(! loginDialog.accessAllowed(servletContext, req, resp))
		{
			String skinName = getLoginDialogSkinName();
			DialogContext dc = loginDialog.createContext(servletContext, page, req, resp, skinName == null ? SkinFactory.getDialogSkin() : SkinFactory.getDialogSkin(skinName));
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

	public boolean hasPermission()
	{
		if(permissions == null)
			return true;

		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());

		AuthenticatedUser user = (AuthenticatedUser) request.getSession(true).getAttribute("authenticated-user");
		if(user == null)
		{
			request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "No user identified.");
			return false;
		}

		AccessControlList acl = AccessControlListFactory.getACL(pageContext.getServletContext());
		if(acl == null)
		{
			request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "No ACL defined.");
			return false;
		}

		if(! user.hasAnyPermission(acl, permissions))
		{
			request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "Permission denied.");
			return false;
		}

		return true;
	}

	public void handleDialogInBody() throws JspException
	{
		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
		String dialogName = request.getParameter(PAGECMD_DLG_DIALOGNAME_PARAM_NAME);
		String skinName = request.getParameter(PAGECMD_DLG_DIALOGSKIN_PARAM_NAME);

		try
		{
			JspWriter out = pageContext.getOut();
			ServletContext context = pageContext.getServletContext();
			DialogManager manager = DialogManagerFactory.getManager(context);
			if(manager == null)
			{
				out.write("DialogManager not found in ServletContext");
				return;
			}

			Dialog dialog = manager.getDialog(dialogName);
			if(dialog == null)
			{
				out.write("Dialog '"+dialogName+"' not found in manager '"+manager+"'.");
				return;
			}

			DialogSkin skin = skinName == null ? SkinFactory.getDialogSkin() : SkinFactory.getDialogSkin(skinName);
			if(skin == null)
			{
				out.write("DialogSkin '"+skinName+"' not found in skin factory.");
				return;
			}

			DialogContext dc = dialog.createContext(context, (Servlet) pageContext.getPage(), (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), skin);
			dc.setRetainRequestParams(DIALOG_COMMAND_RETAIN_PARAMS);
			dialog.prepareContext(dc);

			if(dc.inExecuteMode())
			{
				String html = dialog.execute(dc);
				if(! dc.executeStageHandled())
				{
					out.write("Dialog '"+dialogName+"' did not handle the execute mode.<p>");
					out.write(dc.getDebugHtml());
				}
				else if(html != null)
					out.write(html);
			}
			else
				out.write(dialog.getHtml(dc, true));
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
	}

	public boolean handleDefaultBodyItem() throws JspException
	{
		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
		String pageCmd = request.getParameter(PAGE_COMMAND_REQUEST_PARAM_NAME);
		if(pageCmd == null)
			return false;

		// a "standard" page command needs to be handled
		if(pageCmd.equals("dialog"))
		    handleDialogInBody();

		return true;
	}

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}

    /**
     * Records the start time when the page is loaded
     */
    public void doPageBegin()
    {
        startTime = new Date().getTime();
        HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
        org.apache.log4j.NDC.push(request.getSession(true).getId());
    }

    /**
     * Records the total time when the page is finished loading
     */
    public void doPageEnd()
    {
        HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
        com.xaf.log.LogManager.recordAccess(request, null, pageContext.getPage().getClass().getName(), request.getRequestURI(), startTime);
        org.apache.log4j.NDC.pop();
    }
}
