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

public class ValueSourceProperty extends AbstractProperty
{
	private SingleValueSource value;

	public ValueSourceProperty()
	{
	}

    public ValueSourceProperty(String name, SingleValueSource value)
    {
		setName(name);
		this.value = value;
    }

	public boolean isDynamic()
	{
		return true;
	}

	public String getValue(ValueContext vc)
	{
		return value.getValue(vc);
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);
		value = ValueSourceFactory.getSingleOrStaticValueSource(elem.getAttribute("value-source"));
		setExpression(value.getId());
	}
}