package com.xaf.value;

import java.lang.reflect.*;
import java.util.*;

public class CallbackManager extends HashMap
{
    public CallbackManager()
    {
		super();
    }

	public CallbackInfo getCallbackMethod(String callbackId)
	{
		return (CallbackInfo) get(callbackId);
	}

	public void setCallbackMethod(String callbackId, Object owner, String methodName, Class[] paramTypes)
	{
		CallbackInfo callbackInfo = (CallbackInfo) get(callbackId);
		if(callbackInfo != null)
		{
			callbackInfo = new CallbackInfo(owner, methodName, paramTypes);
			if(callbackInfo.haveMethod())
				put(callbackId, callbackInfo);
		}
	}

	public Object invoke(String callbackId, Object[] args) throws InvocationTargetException, IllegalAccessException
	{
		CallbackInfo ci = (CallbackInfo) get(callbackId);
		if(ci != null && ci.haveMethod())
			return ci.invoke(args);
		else
			return null;
	}
}