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
	private long flags;
	private String name;
	private String expression;
	private String description;

    public AbstractProperty()
    {
    }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }

	public boolean hasReplacements()
	{
		return (flags & PROPFLAG_HAS_REPLACEMENTS) == 0 ? false : true;
	}

	public void setFinalValue(String value)
	{
		setFlag(PROPFLAG_IS_FINAL);
		clearFlag(PROPFLAG_HAS_REPLACEMENTS);
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

	public String getDescription()
	{
		return description;
	}

	abstract public String getValue(ValueContext vc);

	public void importFromXml(Element elem)
	{
		name = elem.getAttribute("name");
		if(elem.getAttribute("final").equals("yes"))
			setFlag(PROPFLAG_FINALIZE_ON_FIRST_GET);
		String descr = elem.getAttribute("descr");
		if(descr.length() > 0)
			description = descr;
	}
}