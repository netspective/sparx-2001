package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import javax.servlet.*;

public interface ValueContext
{
	public Servlet getServlet();
	public ServletRequest getRequest();
	public ServletResponse getResponse();
	public ServletContext getServletContext();
}