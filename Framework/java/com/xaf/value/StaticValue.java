package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.db.*;
import com.xaf.form.*;

public class StaticValue extends ValueSource
{
    public StaticValue()
    {
    }

    public StaticValue(String v)
    {
		valueKey = v;
    }

	public void initializeSource(String strParams)
	{
		valueKey = strParams;
	}

	public String getValue(ValueContext vc)
    {
		return valueKey;
    }

}