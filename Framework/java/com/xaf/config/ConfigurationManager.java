package com.xaf.config;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

import com.xaf.xml.*;
import com.xaf.value.*;

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

	public String getValue(ValueContext vc, String propertyName)
	{
		reload();
		return defaultConfig.getValue(vc, propertyName);
	}

	public String[] getDelimitedValues(ValueContext vc, String propertyName, String[] defaultValue, String delim)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;

		List values = new ArrayList();
		StringTokenizer st = new StringTokenizer(value, delim);
		while(st.hasMoreTokens())
			values.add(st.nextToken());

		return (String[]) values.toArray(new String[values.size()]);
	}

	public boolean getBooleanValue(ValueContext vc, String propertyName, boolean defaultValue)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;
		return value.equalsIgnoreCase("yes") || value.equals("1") || value.equalsIgnoreCase("true");
	}

	public int getIntValue(ValueContext vc, String propertyName, int defaultValue)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;
		return Integer.parseInt(value);
	}

	public long getLongValue(ValueContext vc, String propertyName, long defaultValue)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;
		return Long.parseLong(value);
	}

	public double getDoubleValue(ValueContext vc, String propertyName, double defaultValue)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;
		return Double.parseDouble(value);
	}

	public float getFloatValue(ValueContext vc, String propertyName, float defaultValue)
	{
		String value = getValue(vc, propertyName);
		if(value == null) return defaultValue;
		return Float.parseFloat(value);
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
						newConfig = new Configuration();
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
                    errors.add("Value Source class '"+className+"' not found: " + e.toString());
                }
            }
		}

        addMetaInformation();
	}
}