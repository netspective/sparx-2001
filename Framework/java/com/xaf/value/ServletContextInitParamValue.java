package com.xaf.value;

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