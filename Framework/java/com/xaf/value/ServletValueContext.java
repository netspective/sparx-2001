package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import javax.servlet.*;
import javax.servlet.http.*;

public class ServletValueContext implements ValueContext
{
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext sc;

	public ServletValueContext(HttpServletRequest request, HttpServletResponse response, ServletContext sc)
	{
		this.request = request;
		this.response = response;
		this.sc = sc;
	}

	public final HttpServletRequest getRequest() { return request; }
	public final HttpServletResponse getResponse() { return response; }
	public final ServletContext getServletContext() { return sc; }
}