package com.xaf.config;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

import org.w3c.dom.*;
import com.xaf.value.*;

public class StaticProperty extends AbstractProperty
{
	private String value;

	public StaticProperty()
	{
	}

    public StaticProperty(String name, String value)
    {
		setName(name);
		setExpression(value);
		this.value = value;
    }

	public String getValue(ValueContext vc)
	{
		return value;
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);
		value = elem.getAttribute("value");
		setExpression(value);
		if(value.indexOf(Configuration.REPLACEMENT_PREFIX) != -1)
			setHasReplacements(true);
	}
}