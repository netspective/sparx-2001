package com.xaf.security;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.form.*;
import com.xaf.form.field.*;

public class LoginDialog extends Dialog
{
	static public final String FIELDNAME_USERID = "user_id";
	static public final String COOKIENAME_USERID = "xaf_user_id_01";

	private TextField userIdField;
	private TextField passwordField;

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
    }

	static public boolean accessAllowed(HttpServletRequest request, HttpServletResponse response, ServletContext context)
	{
		Cookie[] cookies = request.getCookies();
		for(int i = 0; i < cookies.length; i++)
		{
			if(cookies[i].getName().equals(COOKIENAME_USERID))
				return true;
		}

		return false;
	}

	public void producePage(DialogContext dc, Writer writer) throws IOException
	{
		writer.write("&nbsp;<p>&nbsp;<p>&nbsp;<p><center>");
		writer.write(getHtml(dc, true));
		writer.write("</center>");
	}

	public String execute(DialogContext dc)
	{
		Cookie cookie = new Cookie(COOKIENAME_USERID, dc.getValue(FIELDNAME_USERID));
		cookie.setPath("/");
		dc.getResponse().addCookie(cookie);
		return null;
	}
}