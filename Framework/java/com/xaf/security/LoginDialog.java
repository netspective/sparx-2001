package com.xaf.security;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class LoginDialog extends Dialog
{
	static public final String FIELDNAME_USERID = "user_id";
	static public final String COOKIENAME_USERID = "xaf_user_id_01";
	static public final String ATTRNAME_USERINFO = "authenticated-user";

	private TextField userIdField;
	private TextField passwordField;
	private String loginImageSrc;
	private String userNameCookieName;
	private String userInfoSessionAttrName;

    public LoginDialog()
    {
		super("Login", "Please Login");

		userIdField = new TextField(FIELDNAME_USERID, "User ID");
		userIdField.setFlag(DialogField.FLDFLAG_REQUIRED);

		passwordField = new TextField("password", "Password");
		passwordField.setFlag(DialogField.FLDFLAG_REQUIRED | TextField.FLDFLAG_MASKENTRY);

		addField(userIdField);
		addField(passwordField);
		addField(new DialogDirector());

		userNameCookieName = COOKIENAME_USERID;
		userInfoSessionAttrName = ATTRNAME_USERINFO;
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

	public String execute(DialogContext dc)
	{
		AuthenticatedUser userInfo = new BasicAuthenticatedUser(
			dc.getValue(userIdField), dc.getValue(userIdField), dc.getValue(passwordField));
		((HttpServletRequest) dc.getRequest()).getSession(true).setAttribute(userInfoSessionAttrName, userInfo);

		Cookie cookie = new Cookie(userNameCookieName, dc.getValue(userIdField));
		cookie.setPath("/");
		((HttpServletResponse) dc.getResponse()).addCookie(cookie);
		return null;
	}

	public void logout(ValueContext vc)
	{
		((HttpServletRequest) vc.getRequest()).getSession(true).removeAttribute(userInfoSessionAttrName);
		Cookie cookie = new Cookie(userNameCookieName, "");
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		((HttpServletResponse) vc.getResponse()).addCookie(cookie);
	}
}