package com.xaf.value;

import javax.servlet.http.*;
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
		return (String) ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
    }

	public Object getObjectValue(ValueContext vc)
    {
        return ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
    }

	public boolean supportsSetValue()
	{
		return true;
	}

	public void setValue(ValueContext vc, Object value)
	{
		if(value == null)
			((HttpServletRequest) vc.getRequest()).getSession().removeAttribute(valueKey);
		else
			((HttpServletRequest) vc.getRequest()).getSession().setAttribute(valueKey, value);
	}
}