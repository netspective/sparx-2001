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

public class StringProperty extends AbstractProperty
{
	private String value;

	public StringProperty()
	{
	}

    public StringProperty(String name, String value)
    {
		setName(name);
		setExpression(value);
		this.value = value;
    }

	public String getValue(ValueContext vc)
	{
		return value;
	}

	public void setFinalValue(String value)
	{
		super.setFinalValue(value);
		this.value = value;
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);
		value = elem.getAttribute("value");
		setExpression(value);
		if(value.indexOf(Configuration.REPLACEMENT_PREFIX) != -1)
			setFlag(PROPFLAG_HAS_REPLACEMENTS);
		else
			setFinalValue(value);
	}
}