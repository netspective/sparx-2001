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

abstract public class AbstractProperty implements Property
{
	private String name;
	private String expression;
	private boolean hasReplacements;

    public AbstractProperty()
    {
    }

	public boolean isDynamic()
	{
		return false;
	}

	public boolean hasReplacements()
	{
		return hasReplacements;
	}

	public void setHasReplacements(boolean value)
	{
		hasReplacements = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expr)
	{
		expression = expr;
	}

	abstract public String getValue(ValueContext vc);

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("name");
	}
}