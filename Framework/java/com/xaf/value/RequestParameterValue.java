package com.xaf.value;

import com.xaf.db.*;
import com.xaf.form.*;

public class RequestParameterValue extends ValueSource
{
    public RequestParameterValue()
    {
		super();
    }

    public String getValue(ValueContext vc)
    {
		return vc.getRequest().getParameter(valueKey);
    }
}