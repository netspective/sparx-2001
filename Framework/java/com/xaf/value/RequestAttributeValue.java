package com.xaf.value;

import com.xaf.db.*;
import com.xaf.form.*;

public class RequestAttributeValue extends ValueSource
{
    public RequestAttributeValue()
    {
		super();
    }

    public String getValue(ValueContext vc)
    {
		return (String) vc.getRequest().getAttribute(valueKey);
    }

	public Object getObjectValue(ValueContext vc)
    {
        return vc.getRequest().getAttribute(valueKey);
    }

	public boolean supportsSetValue()
	{
		return true;
	}

	public void setValue(ValueContext vc, Object value)
	{
		vc.getRequest().setAttribute(valueKey, value);
	}
}