package com.netspective.sparx.xaf.requirement;

import com.netspective.sparx.util.xml.XmlSource;

import java.io.File;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 30, 2003
 * Time: 11:24:17 AM
 * To change this template use Options | File Templates.
 */
public class JavadocTreeManager extends XmlSource
{
	public JavadocTreeManager(File file)
	{
		loadDocument(file);
	}

	public void catalogNodes()
	{
		try
		{
			NodeList classNodes = null;
			classNodes = xmlDoc.getElementsByTagName("class");

			for(int i=0; i<classNodes.getLength(); i++)
			{
				Element classElem = (Element) classNodes.item(i);
				String className = classElem.getAttribute("name");
				defineClassAttributes(classElem, Class.forName(className), "_");
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
