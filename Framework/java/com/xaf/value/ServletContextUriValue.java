package com.xaf.value;

import javax.servlet.http.*;

public class ServletContextUriValue extends ValueSource
{
	public final int URITYPE_ROOT = 0;
	public final int URITYPE_ACTIVE_SERVLET = 1;
	public final int URITYPE_CUSTOM = 2;

	private int type;

    public ServletContextUriValue()
    {
		super();
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
		type = URITYPE_ROOT;
		if(srcParams.equals("/"))
		    type = URITYPE_ROOT;
		else if(srcParams.equals("active-servlet"))
			type = URITYPE_ACTIVE_SERVLET;
		else
			type = URITYPE_CUSTOM;
    }

    public String getValue(ValueContext vc)
    {
		HttpServletRequest request = vc.getRequest();
		if(request == null)
			return "ValueContext.getRequest() is NULL in " + getId();

		String contextPath = request.getContextPath();
		switch(type)
		{
			case URITYPE_ROOT:
				return contextPath;

			case URITYPE_ACTIVE_SERVLET:
				return contextPath + request.getServletPath();

			case URITYPE_CUSTOM:
				return contextPath + request.getServletPath() + valueKey;
		}

		return contextPath;
    }
}