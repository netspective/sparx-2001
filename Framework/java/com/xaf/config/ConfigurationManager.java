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
		}
	}
}