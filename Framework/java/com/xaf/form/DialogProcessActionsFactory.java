package com.xaf.form;

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.action.*;

public class DialogProcessActionsFactory
{
	static Map actionClasses = new Hashtable();
	static boolean defaultsAvailable = false;

	public static void addExecuteAction(String tagName, Class cls)
	{
		actionClasses.put(tagName, cls);
	}

	public static void setupDefaults()
	{
        addExecuteAction("exec-query", QueryExecuteAction.class);
        defaultsAvailable = true;
    }

    public static DialogProcessAction getProcessAction(Element elem, boolean throwExceptionIfNotFound) throws DialogProcessActionInitializeException, IllegalAccessException, InstantiationException
    {
        if(! defaultsAvailable)
            setupDefaults();

        String name = elem.getNodeName();
        Class actionClass = (Class) actionClasses.get(name);
        if(actionClass == null)
        {
            if(throwExceptionIfNotFound)
                throw new DialogProcessActionInitializeException("DialogProcessAction class '"+name+"' not found");
            else
                return null;
        }

        DialogProcessAction action = (DialogProcessAction) actionClass.newInstance();
        action.initializeProcessAction(elem);

        return action;
    }
}