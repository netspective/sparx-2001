package com.xaf.security;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.log.*;
import com.xaf.skin.*;
import com.xaf.value.*;

public class LoginDialog extends Dialog
{
	static public final String DEFAULT_COOKIENAME_USERID = "xaf_user_id_01";
	static public final String DEFAULT_ATTRNAME_USERINFO = "authenticated-user";

	private TextField userIdField;
	private TextField passwordField;
	private String loginImageSrc;
	private String userNameCookieName;
	private String userInfoSessionAttrName;

    public LoginDialog()
    {
		super();

		userNameCookieName = DEFAULT_COOKIENAME_USERID;
		userInfoSessionAttrName = DEFAULT_ATTRNAME_USERINFO;

		setRetainAllRequestParams(true);
    }

	public DialogSkin getSkin()
	{
		return SkinFactory.getDialogSkin();
	}

	public TextField createUserIdField()
	{
		TextField result = new TextField("user_id", "User ID");
		result.setFlag(DialogField.FLDFLAG_REQUIRED);
		result.setFlag(DialogField.FLDFLAG_INITIAL_FOCUS);
		return result;
	}

	public TextField createPasswordField()
	{
		TextField result = new TextField("password", "Password");
		result.setFlag(DialogField.FLDFLAG_REQUIRED | TextField.FLDFLAG_MASKENTRY);
		return result;
	}

	public void initialize()
	{
		this.setHeading("Please Login");
		this.setName("login");

		userIdField = createUserIdField();
		passwordField = createPasswordField();

		addField(userIdField);
		addField(passwordField);
		addField(new DialogDirector());
	}

	public String getUserNameCookieName() { return userNameCookieName; }
	public void setUserNameCookieName(String value) { userNameCookieName = value; }

	public String getUserInfoSessionAttrName() { return userInfoSessionAttrName; }
	public void setUserInfoSessionAttrName(String value) { userInfoSessionAttrName = value; }

	public String getImageSrc() { return loginImageSrc; }
	public void setImageSrc(String value) { loginImageSrc = value; }

	public boolean accessAllowed(ServletContext context, HttpServletRequest request, HttpServletResponse response)
	{
		return request.getSession(true).getAttribute(userInfoSessionAttrName) != null;
	}

	public void producePage(DialogContext dc, Writer writer) throws IOException
	{
		writer.write("&nbsp;<p>&nbsp;<p><center>");
		if(loginImageSrc != null)
		    writer.write("<img src='"+loginImageSrc+"'><p>");
		writer.write(getHtml(dc, true));
		writer.write("</center>");
	}

	public AuthenticatedUser getActiveUser(ValueContext vc)
	{
		return (AuthenticatedUser) ((HttpServletRequest) vc.getRequest()).getSession(true).getAttribute(userInfoSessionAttrName);
	}

	public AuthenticatedUser createUserData(DialogContext dc)
	{
		return new BasicAuthenticatedUser(dc.getValue(userIdField), dc.getValue(userIdField));
	}

	public void applyAccessControls(DialogContext dc, AuthenticatedUser user)
	{
		AccessControlList acl = AccessControlListFactory.getACL(dc.getServletContext());
		user.setRoles(acl, new String[] { "/role/super-user" });
	}

	public void storeUserData(DialogContext dc, AuthenticatedUser user)
	{
		HttpServletRequest req = (HttpServletRequest) dc.getRequest();
		req.getSession(true).setAttribute(userInfoSessionAttrName, user);

		Cookie cookie = new Cookie(userNameCookieName, dc.getValue(userIdField));
		cookie.setPath("/");
		((HttpServletResponse) dc.getResponse()).addCookie(cookie);

		AppServerCategory cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.MONITOR_SECURITY);
		if(cat.isInfoEnabled())
		{
			String userId = user.getUserId();
			StringBuffer info = new StringBuffer();
			info.append("login");
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(userId);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(user.getUserOrgId());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteUser());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteHost());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteAddr());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			BitSet perms = user.getUserPermissions();
			info.append(perms != null ? user.getUserPermissions().toString() : "{}");
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			String[] roles = user.getUserRoles();
			if(roles != null)
			{
				for(int r = 0; r < roles.length; r++)
				{
					if(r > 0)
						info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
					info.append(roles[r]);
				}
			}
			cat.info(info);
		}

		cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.DEBUG_SECURITY);
		if(cat.isDebugEnabled())
		{
			String userId = user.getUserId();
			cat.debug("User '"+ userId +"' ("+ user.getUserName() +") is now authenticated for Session ID '"+ req.getSession(true).getId() +"'");

			BitSet perms = user.getUserPermissions();
			if(perms != null)
				cat.debug("User '"+ userId +"' has permissions " + user.getUserPermissions().toString());
			else
				cat.debug("User '"+ userId +" has no permissions.");

			String[] roles = user.getUserRoles();
			if(roles != null)
			{
				for(int r = 0; r < roles.length; r++)
					cat.debug("User '"+ userId +"' has role " + roles[r]);
			}
			else
				cat.debug("User '"+ userId +" has no roles.");
		}
	}

	public void clearUserData(ValueContext vc)
	{
		HttpServletRequest req = (HttpServletRequest) vc.getRequest();
		AuthenticatedUser user = (AuthenticatedUser) req.getSession(true).getAttribute(userInfoSessionAttrName);

		AppServerCategory cat = (AppServerCategory) AppServerCategory.getInstance(LogManager.MONITOR_SECURITY);
		if(user != null && cat.isInfoEnabled())
		{
			String userId = user.getUserId();
			StringBuffer info = new StringBuffer();
			info.append("logout");
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(userId);
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(user.getUserOrgId());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteUser());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteHost());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			info.append(req.getRemoteAddr());
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			BitSet perms = user.getUserPermissions();
			info.append(perms != null ? user.getUserPermissions().toString() : "{}");
			info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
			String[] roles = user.getUserRoles();
			if(roles != null)
			{
				for(int r = 0; r < roles.length; r++)
				{
					if(r > 0)
						info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
					info.append(roles[r]);
				}
			}
			cat.info(info);
		}

		req.getSession(true).removeAttribute(userInfoSessionAttrName);
		Cookie cookie = new Cookie(userNameCookieName, "");
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		((HttpServletResponse) vc.getResponse()).addCookie(cookie);
	}

	public String execute(DialogContext dc)
	{
		AuthenticatedUser user = createUserData(dc);
		applyAccessControls(dc, user);
		storeUserData(dc, user);
		return null;
	}

	public void logout(ValueContext vc)
	{
		clearUserData(vc);
	}
}