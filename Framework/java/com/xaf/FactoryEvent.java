package com.xaf;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

public class FactoryEvent
{
	private Class factoryClass;
	private Object factory;

    public FactoryEvent(Class factoryClass)
    {
		this.factoryClass = factoryClass;
    }

    public FactoryEvent(Object factory)
    {
		this.factory = factory;
    }

	public Class getFactoryClass() { return factoryClass; }
	public Object getFactory() { return factory; }
}