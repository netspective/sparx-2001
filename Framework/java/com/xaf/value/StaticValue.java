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

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Used to explicitly specify a value instead of obtaining the value from a parameter name. For example, " +
            "if the <code>static-string</code> is <code><u>myfile.xml</u></code> then this string is literally taken " +
            "as the value instead of searching for a parameter.",
            "static-string"
        );
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