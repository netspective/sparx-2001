package com.xaf.task;

import java.util.*;
import org.w3c.dom.*;

public class TaskFactory
{
	static Map taskMap = new Hashtable();
	static boolean defaultsAvailable = false;

	public static void addTask(String tagName, Class cls)
	{
		taskMap.put(tagName, cls);
	}

	public static void setupDefaults()
	{
        addTask("exec-statement", com.xaf.task.sql.StatementTask.class);
        addTask("exec-dml", com.xaf.task.sql.DmlTask.class);
        addTask("exec-redirect", com.xaf.task.navigate.RedirectTask.class);
        defaultsAvailable = true;
    }

    public static Task getTask(Element elem, boolean throwExceptionIfNotFound) throws TaskInitializeException, IllegalAccessException, InstantiationException
    {
        if(! defaultsAvailable)
            setupDefaults();

        String name = elem.getNodeName();
        Class taskClass = (Class) taskMap.get(name);
        if(taskClass == null)
        {
            if(throwExceptionIfNotFound)
                throw new TaskInitializeException("DialogProcessAction class '"+name+"' not found");
            else
                return null;
        }

        Task task = (Task) taskClass.newInstance();
        task.initialize(elem);

        return task;
    }

	public static void createCatalog(Element parent)
	{
		if(! defaultsAvailable) setupDefaults();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Tasks");
		factoryElem.setAttribute("class", TaskFactory.class.getName());
		for(Iterator i = taskMap.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("task");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((Class) entry.getValue()).getName());
			factoryElem.appendChild(childElem);
		}
	}

}