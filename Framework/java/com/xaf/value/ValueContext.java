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

public interface ValueContext
{
	public HttpServletRequest getRequest();
	public HttpServletResponse getResponse();
	public ServletContext getServletContext();
}