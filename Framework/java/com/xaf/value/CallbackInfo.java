package com.xaf.value;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

import java.lang.reflect.*;

public class CallbackInfo
{
	private Object owner;
	private Method method;

	public CallbackInfo(Object owner, String methodName, Class[] paramTypes)
	{
		this.owner = owner;
		try
		{
			method = owner.getClass().getMethod(methodName, paramTypes);
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException(e.toString() + ": " + owner + " -- " + methodName + " " + paramTypes);
		}
		catch(SecurityException e)
		{
			method = null;
		}
	}

	public boolean haveMethod()
	{
		return method != null;
	}

	public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException
	{
		return method.invoke(owner, args);
	}
}

