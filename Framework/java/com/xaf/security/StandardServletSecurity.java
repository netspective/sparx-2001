package com.xaf.security;

import javax.servlet.*;
import javax.servlet.http.*;
import com.xaf.form.*;

public class StandardServletSecurity implements ServletSecurity
{
	public boolean authenticate(DialogContext dc)
	{
		Cookie cookie = new Cookie(COOKIENAME_USERID, dc.getValue("user_id"));
		cookie.setPath("/");
		dc.getResponse().addCookie(cookie);
		return true;
	}

	public boolean accessAllowed(HttpServletRequest request, HttpServletResponse response, ServletContext context)
	{
		Cookie[] cookies = request.getCookies();
		for(int i = 0; i < cookies.length; i++)
		{
			if(cookies[i].getName().equals(COOKIENAME_USERID))
				return true;
		}

		return false;
	}

	public String getLoginSkinPage(HttpServletRequest request, HttpServletResponse response, ServletContext context)
	{
		return "/login-skin.jsp";
	}
}