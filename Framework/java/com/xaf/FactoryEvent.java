package com.xaf;

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