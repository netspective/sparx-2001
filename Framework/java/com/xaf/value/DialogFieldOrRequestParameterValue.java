package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.form.*;

public class DialogFieldOrRequestParameterValue extends ValueSource
{
    public DialogFieldOrRequestParameterValue()
    {
        super();
    }

    public String getValue(ValueContext vc)
    {
		String value = vc.getRequest().getParameter(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        if(value == null)
            value = vc.getRequest().getParameter(valueKey);
        return value;
    }
}