package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.report.calc.*;

public class ColumnDataCalculatorFactory
{
    static private Map calcsClasses = new HashMap();
    static private boolean haveDefaultCalcs = false;

    static public void addColumnDataCalc(String name, Class cls)
    {
        calcsClasses.put(name, cls);
    }

    static public void addColumnDataCalc(String name, String className) throws ClassNotFoundException
    {
        addColumnDataCalc(name, Class.forName(className));
    }

    static public void setupDefaultCalcs()
    {
        addColumnDataCalc("sum", ColumnSumCalculator.class);
        addColumnDataCalc("count", ColumnCountCalculator.class);
        haveDefaultCalcs = true;
    }

    /**
     * Returns a freshly instantiated ColumnDataCalculator named "cmd". If "cmd" is an already-existing named
     * class it will return a newInstance() of that class. If the "cmd" is a class name, a newInstance() of that
     * particular class will be created and the class will be cached in the calcsClasses Map.
     */

    static public ColumnDataCalculator createDataCalc(String cmd)
    {
        if(! haveDefaultCalcs)
            setupDefaultCalcs();

        Class cls = (Class) calcsClasses.get(cmd);
        if(cls == null)
        {
            try
            {
                cls = Class.forName(cmd);
                addColumnDataCalc(cmd, cls);
            }
            catch(ClassNotFoundException e)
            {
                return null;
            }
        }

        try
        {
            return (ColumnDataCalculator) cls.newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }

	public static void createCatalog(Element parent)
	{
        if(! haveDefaultCalcs) setupDefaultCalcs();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Column Data Calculators");
		factoryElem.setAttribute("class", ColumnDataCalculatorFactory.class.getName());

		for(Iterator i = calcsClasses.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("calculator");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((Class) entry.getValue()).getName());
			factoryElem.appendChild(childElem);
		}
	}

}