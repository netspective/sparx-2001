package com.xaf.config;

import java.util.*;
import org.w3c.dom.*;

public class PropertiesList extends ArrayList implements PropertiesCollection
{
	private String name;

	public String getName()
	{
		return name;
	}

	public Collection getCollection()
	{
		return this;
	}

	public void importFromXml(Element elem, ConfigurationManager manager, Configuration config)
	{
		name = elem.getAttribute("name");

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
					add(prop);
				}
				else
				{
					manager.addError("Unknown property type '"+propType+"'");
				}
			}
			else if(childName.equals("properties"))
			{
				Element propertiesElem = (Element) childNode;
				String propType = propertiesElem.getAttribute("type");
				if(propType.length() == 0 || propType.equals("list"))
				{
					PropertiesCollection propColl = new PropertiesList();
					propColl.importFromXml(propertiesElem, manager, config);
					add(propColl);
				}
				else
				{
					manager.addError("Unknown properties type '"+propType+"'");
				}
			}
		}
	}
}