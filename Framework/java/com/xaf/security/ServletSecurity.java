package com.xaf.security;

import javax.servlet.*;
import javax.servlet.http.*;
import com.xaf.form.*;

public interface ServletSecurity
{
	static public final String COOKIENAME_USERID = "xaf_user_id";

	public boolean accessAllowed(HttpServletRequest request, HttpServletResponse response, ServletContext context);
	public String getLoginSkinPage(HttpServletRequest request, HttpServletResponse response, ServletContext context);
}