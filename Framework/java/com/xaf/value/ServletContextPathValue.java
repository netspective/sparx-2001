package com.xaf.value;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

public class ServletContextPathValue extends ValueSource
{
	private boolean root;

    public ServletContextPathValue()
    {
		super();
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
		if(srcParams.equals("/"))
			root = true;
    }

    public String getValue(ValueContext vc)
    {
		if(root)
	        return vc.getServletContext().getRealPath(valueKey);
		else
			return vc.getServletContext().getRealPath(null);
    }
}