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
 * $Id: Configuration.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.config;

import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class Configuration extends HashMap
{
    static public class ReplacementInfo
    {
        public StringBuffer result = new StringBuffer();
        public int dynamicReplacementsCount;

        public boolean isFinal()
        {
            return dynamicReplacementsCount == 0 ? true : false;
        }
    }

    public final static String REPLACEMENT_PREFIX = "${";
    private String name;

    public Configuration()
    {
    }

    public Configuration(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /** Replace ${NAME} with the property value
     *  and keep track of whether there are any "dynamic" values that are within
     *  the property. As a property replacement value becomes final (only replacements
     *  with other static values) replace the expression with a specific value.
     */
    public ReplacementInfo replaceProperties(ValueContext vc, String value)
    {
        ReplacementInfo ri = new ReplacementInfo();

        StringBuffer sb = ri.result;
        int i = 0;
        int prev = 0;

        int pos;
        while((pos = value.indexOf("$", prev)) >= 0)
        {
            if(pos > 0)
            {
                sb.append(value.substring(prev, pos));
            }
            if(pos == (value.length() - 1))
            {
                sb.append('$');
                prev = pos + 1;
            }
            else if(value.charAt(pos + 1) != '{')
            {
                sb.append(value.charAt(pos + 1));
                prev = pos + 2;
            }
            else
            {
                int endName = value.indexOf('}', pos);
                if(endName < 0)
                {
                    throw new RuntimeException("Syntax error in prop: " + value);
                }
                String expression = value.substring(pos + 2, endName);
                Property property = (Property) get(expression);
                if(property != null)
                {
                    ReplacementInfo subRi = replaceProperties(vc, property.getValue(vc));
                    if(subRi.isFinal())
                        property.setFinalValue(subRi.result.toString());
                    else if(property.flagIsSet(Property.PROPFLAG_FINALIZE_ON_FIRST_GET))
                        property.setFinalValue(subRi.result.toString());
                    else
                        ri.dynamicReplacementsCount += subRi.dynamicReplacementsCount;
                    sb.append(subRi.result);
                }
                else
                {
                    SingleValueSource vs = ValueSourceFactory.getSingleValueSource(expression);
                    if(vs != null)
                        sb.append(vs.getValueOrBlank(vc));
                    else
                        sb.append("${" + expression + "}");
                    ri.dynamicReplacementsCount++;
                }

                prev = endName + 1;
            }
        }

        if(prev < value.length()) sb.append(value.substring(prev));
        return ri;
    }

    public String getValue(ValueContext vc, Property property, String defaultValue)
    {
        if(property != null)
        {
            String value = property.getValue(vc);
            if(property.hasReplacements())
            {
                ReplacementInfo ri = replaceProperties(vc, value);
                String result = ri.result.toString();
                if(ri.isFinal())
                    property.setFinalValue(result);
                else if(property.flagIsSet(Property.PROPFLAG_FINALIZE_ON_FIRST_GET))
                    property.setFinalValue(result);
                return result;
            }
            else
                return value;
        }
        else
            return defaultValue;
    }

    public String getTextValue(ValueContext vc, String name, String defaultValue)
    {
        return getValue(vc, (Property) get(name), defaultValue);
    }

    public String getTextValue(ValueContext vc, String name)
    {
        Property prop = (Property) get(name);
        if(prop == null)
            throw new RuntimeException("Configuration property '"+ name +"' not found -- use getTextValue(ValueContext, String, String) to eliminate this exception");

        String value = getValue(vc, (Property) get(name), null);
        if(value == null)
            throw new RuntimeException("Configuration property '"+ name +"' returned NULL value -- use getTextValue(ValueContext, String, String) to eliminate this exception");

        return value;
    }

    public Collection getValues(ValueContext vc, String name)
    {
        PropertiesCollection property = (PropertiesCollection) get(name);
        if(property == null)
            return null;
        return property.getCollection();
    }

    public void importFromXml(Element elem, ConfigurationManager manager)
    {
        if(name == null)
        {
            String name = elem.getAttribute("name");
            if(name.length() == 0)
                name = null;
        }

        NodeList children = elem.getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node childNode = children.item(c);
            if(childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = childNode.getNodeName();
            if(childName.equals("property"))
            {
                Element propertyElem = (Element) childNode;
                String propType = propertyElem.getAttribute("type");
                if(propType.length() == 0 || propType.equals("text"))
                {
                    Property prop = new StringProperty();
                    prop.importFromXml(propertyElem);
                    put(prop.getName(), prop);
                }
                else
                {
                    manager.addError("Unknown property type '" + propType + "'");
                }
            }
            else if(childName.equals("system-property"))
            {
                Element propertyElem = (Element) childNode;
                Property prop = new StringProperty();
                prop.importFromXml(propertyElem);

                System.setProperty(prop.getName(), prop.getValue(null));
            }
            else if(childName.equals("properties"))
            {
                Element propertiesElem = (Element) childNode;
                String propType = propertiesElem.getAttribute("type");
                if(propType.length() == 0 || propType.equals("list"))
                {
                    PropertiesCollection propColl = new PropertiesList();
                    propColl.importFromXml(propertiesElem, manager, this);
                    put(propColl.getName(), propColl);
                }
                else
                {
                    manager.addError("Unknown properties type '" + propType + "'");
                }
            }
        }
    }
}