/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: ColumnDataCalculatorFactory.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.report;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.report.calc.ColumnCountCalculator;
import com.netspective.sparx.xaf.report.calc.ColumnSumCalculator;

public class ColumnDataCalculatorFactory implements Factory
{
    static private Map calcsClasses = new HashMap();

    static
    {
        addColumnDataCalc("sum", ColumnSumCalculator.class);
        addColumnDataCalc("count", ColumnCountCalculator.class);
    }

    static public void addColumnDataCalc(String name, Class cls)
    {
        calcsClasses.put(name, cls);
    }

    static public void addColumnDataCalc(String name, String className) throws ClassNotFoundException
    {
        addColumnDataCalc(name, Class.forName(className));
    }

    /**
     * Returns a freshly instantiated ColumnDataCalculator named "cmd". If "cmd" is an already-existing named
     * class it will return a newInstance() of that class. If the "cmd" is a class name, a newInstance() of that
     * particular class will be created and the class will be cached in the calcsClasses Map.
     */

    static public ColumnDataCalculator createDataCalc(String cmd)
    {
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
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Column Data Calculators");
        factoryElem.setAttribute("class", ColumnDataCalculatorFactory.class.getName());

        for(Iterator i = calcsClasses.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("calculator");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((Class) entry.getValue()).getName());
            factoryElem.appendChild(childElem);
        }
    }

}