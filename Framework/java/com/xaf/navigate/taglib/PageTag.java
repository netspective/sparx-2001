package com.xaf.navigate.taglib;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.security.*;

public class PageTag extends TagSupport
{
	static public final String PAGE_SECURITY_MESSAGE_ATTRNAME = "security-message";

	private String title;
	private String heading;
	private String[] permissions;

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
		if(value.indexOf(",") == -1)
		{
			permissions = new String[] { value };
		}
		else
		{
			List perms = new ArrayList();
			StringTokenizer st = new StringTokenizer(value, ",");
			while(st.hasMoreTokens())
			{
				perms.add(st.nextToken());
			}
			permissions = (String[]) perms.toArray(new String[perms.size()]);
		}
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

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}
}
