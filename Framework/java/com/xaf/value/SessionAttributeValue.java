package com.xaf.value;

import com.xaf.db.*;
import com.xaf.form.*;

public class SessionAttributeValue extends ValueSource
{
    public SessionAttributeValue()
    {
		super();
    }

    public String getValue(ValueContext vc)
    {
		return (String) vc.getRequest().getSession().getAttribute(valueKey);
    }

	public Object getObjectValue(ValueContext vc)
    {
        return vc.getRequest().getSession().getAttribute(valueKey);
    }

	public boolean supportsSetValue()
	{
		return true;
	}

	public void setValue(ValueContext vc, Object value)
	{
		if(value == null)
			vc.getRequest().getSession().removeAttribute(valueKey);
		else
			vc.getRequest().getSession().setAttribute(valueKey, value);
	}
}