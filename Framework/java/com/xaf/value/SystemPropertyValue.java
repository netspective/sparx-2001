package com.xaf.value;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

import java.io.*;
import java.util.*;

public class SystemPropertyValue extends ValueSource
{
    public SystemPropertyValue()
    {
		super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to the system property indicated by the specified key. ",
            "property-name"
        );
    }

    public String getValue(ValueContext vc)
    {
		return System.getProperty(valueKey, null);
    }
}