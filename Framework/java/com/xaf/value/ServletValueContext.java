package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ServletValueContext implements ValueContext
{
	private ServletContext servletContext;
	private Servlet servlet;
	private ServletRequest request;
	private ServletResponse response;

	public ServletValueContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
	{
		this.servletContext = context;
		this.request = request;
		this.response = response;
		this.servlet = servlet;
	}

	public final ServletRequest getRequest() { return request; }
	public final ServletResponse getResponse() { return response; }
	public final ServletContext getServletContext() { return servletContext; }
	public final Servlet getServlet() { return servlet; }
}