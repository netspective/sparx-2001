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
 * $Id: ConfigurationManager.java,v 1.3 2002-11-30 16:37:57 shahid.shah Exp $
 */

package com.netspective.sparx.util.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.util.ClassPath;

public class ConfigurationManager extends XmlSource
{
    public final static String DEFAULT_CONFIG_NAME = "default";
    private Map configurations = new HashMap();
    private Configuration defaultConfig = new Configuration(DEFAULT_CONFIG_NAME);

    public ConfigurationManager(File file)
    {
        loadDocument(file);
    }

    public Configuration getDefaultConfiguration()
    {
        reload();
        return defaultConfig;
    }

    public Configuration getConfiguration(String name)
    {
        reload();
        return (Configuration) configurations.get(name);
    }

    public String getTextValue(ValueContext vc, String propertyName)
    {
        reload();
        return defaultConfig.getTextValue(vc, propertyName);
    }

    public String[] getDelimitedValues(ValueContext vc, String propertyName, String[] defaultValue, String delim)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;

        List values = new ArrayList();
        StringTokenizer st = new StringTokenizer(value, delim);
        while(st.hasMoreTokens())
            values.add(st.nextToken());

        return (String[]) values.toArray(new String[values.size()]);
    }

    public boolean getBooleanValue(ValueContext vc, String propertyName, boolean defaultValue)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;
        return value.equalsIgnoreCase("yes") || value.equals("1") || value.equalsIgnoreCase("true");
    }

    public int getIntValue(ValueContext vc, String propertyName, int defaultValue)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;
        return Integer.parseInt(value);
    }

    public long getLongValue(ValueContext vc, String propertyName, long defaultValue)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;
        return Long.parseLong(value);
    }

    public double getDoubleValue(ValueContext vc, String propertyName, double defaultValue)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;
        return Double.parseDouble(value);
    }

    public float getFloatValue(ValueContext vc, String propertyName, float defaultValue)
    {
        String value = getTextValue(vc, propertyName);
        if(value == null) return defaultValue;
        return Float.parseFloat(value);
    }

    public String[] getCatalogedNodeIdentifiers()
    {
        return (String[]) defaultConfig.keySet().toArray(new String[defaultConfig.size()]);
    }

    public void catalogNodes()
    {
        defaultConfig.clear();
        configurations.clear();
        configurations.put(DEFAULT_CONFIG_NAME, defaultConfig);

        if(xmlDoc == null)
            return;

        NodeList children = xmlDoc.getDocumentElement().getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            if(nodeName.equals("configuration"))
            {
                Element configElem = (Element) node;
                String name = configElem.getAttribute("name");

                Configuration newConfig = null;
                if(name.length() != 0)
                {
                    newConfig = (Configuration) configurations.get(name);
                    if(newConfig == null)
                    {
                        ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(configElem.getAttribute("class"), Configuration.class, true);
                        newConfig = (Configuration) instanceGen.getInstance();
                        newConfig.importFromXml(configElem, this);
                        configurations.put(name, newConfig);
                    }
                    else
                    {
                        newConfig.importFromXml(configElem, this);
                    }
                }
                else
                {
                    defaultConfig.importFromXml(configElem, this);
                }
            }
            else if(nodeName.equals("register-value-source"))
            {
                Element typeElem = (Element) node;
                String className = typeElem.getAttribute("class");
                try
                {
                    Class cls = Class.forName(className);
                    ValueSourceFactory.addValueSourceClass(typeElem.getAttribute("name"), cls);
                }
                catch(ClassNotFoundException e)
                {
                    errors.add("Value Source class '" + className + "' not found: " + e.toString());
                }
            }
        }

        addMetaInformation();
    }
}