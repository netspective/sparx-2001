package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.value.*;

public class AppConfigurationPage extends AceServletPage
{
	public final String getName() { return "config"; }
	public final String getPageIcon() { return "configuration.gif"; }
	public final String getCaption(PageContext pc) { return "Configuration"; }
	public final String getHeading(PageContext pc) { return "Application Configuration"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		Document configDoc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			configDoc = builder.newDocument();
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}

		Element configRootElem = configDoc.createElement("xaf");
		configDoc.appendChild(configRootElem);

		Element configItemsElem = configDoc.createElement("config-items");
		configRootElem.appendChild(configItemsElem);

		ConfigurationManager manager = ConfigurationManagerFactory.getManager(context);
		configItemsElem.setAttribute("source-file", manager.getSourceDocument().getFile().getAbsolutePath());

		Configuration defaultConfig = manager.getDefaultConfiguration();
		for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext(); )
		{
			Element itemElem = configDoc.createElement("config-item");
			Map.Entry configEntry = (Map.Entry) i.next();
			itemElem.setAttribute("name", (String) configEntry.getKey());

			if(configEntry.getValue() instanceof Property)
			{
				Property property = (Property) configEntry.getValue();
				String expression = property.getExpression();
				String value = defaultConfig.getValue(pc, property.getName());
				itemElem.setAttribute("value", value);
				if(! expression.equals(value))
				{
					itemElem.setAttribute("expression", expression);
					if(! property.flagIsSet(Property.PROPFLAG_IS_FINAL))
						itemElem.setAttribute("final", "no");
				}
				if(property.getDescription() != null)
					itemElem.setAttribute("description", property.getDescription());
			}
			else if(configEntry.getValue() instanceof PropertiesCollection)
			{
				PropertiesCollection propColl = (PropertiesCollection) configEntry.getValue();
				Collection coll = propColl.getCollection();
				itemElem.setAttribute("expression", "list of " + coll.size() + " item(s)");
			}
			configItemsElem.appendChild(itemElem);
		}

		transform(pc, configDoc, ACE_CONFIG_ITEMS_PREFIX + "config-browser-xsl");
	}
}
