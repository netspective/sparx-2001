package com.xaf.value;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

public class ServletContextInitParamValue extends ValueSource
{

    public ServletContextInitParamValue()
    {
		super();
    }

    public String getValue(ValueContext vc)
    {
		return vc.getServletContext().getInitParameter(valueKey);
    }

}