package com.xaf.config;

import java.util.*;
import org.w3c.dom.*;
import com.xaf.value.*;

public class Configuration extends HashMap
{
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
     */
    public String replaceProperties(ValueContext vc, String value)
    {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        int prev = 0;

        int pos;
        while((pos=value.indexOf( "$", prev )) >= 0)
		{
            if(pos>0)
			{
                sb.append(value.substring( prev, pos ));
            }
            if( pos == (value.length() - 1))
			{
                sb.append('$');
                prev = pos + 1;
            }
            else if (value.charAt( pos + 1 ) != '{')
			{
                sb.append( value.charAt( pos + 1 ) );
                prev=pos+2;
            }
			else
			{
                int endName=value.indexOf( '}', pos );
                if( endName < 0 )
				{
                    throw new RuntimeException("Syntax error in prop: " + value);
                }
                String expression = value.substring(pos+2, endName);
				Property property = (Property) get(expression);
				if(property != null)
				{
                    sb.append(property.hasReplacements() ? replaceProperties(vc, property.getValue(vc)) : property.getValue(vc));
				}
				else
				{
					SingleValueSource vs = ValueSourceFactory.getSingleValueSource(expression);
					if(vs != null)
						sb.append(vs.getValueOrBlank(vc));
					else
		                sb.append("${" + expression + "}");
				}

                prev=endName+1;
            }
        }

        if(prev < value.length()) sb.append(value.substring(prev));
        return sb.toString();
    }

	public String getValue(ValueContext vc, String name)
	{
		Property property = (Property) get(name);
		if(property != null)
		{
		    String value = property.getValue(vc);
			if(property.hasReplacements())
				return replaceProperties(vc, value);
			else
				return value;
		}
		else
			return null;
	}

	public void importFromXml(Element elem, ConfigurationManager manager)
	{
		if(name == null)
		{
			String name = elem.getAttribute("package");
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

				Property prop = null;
				if(propertyElem.getAttribute("value").length() > 0)
				    prop = new StaticProperty();
				else if(propertyElem.getAttribute("value-source").length() > 0)
					prop = new ValueSourceProperty();

				prop.importFromXml(propertyElem);
				put(prop.getName(), prop);
			}
		}
	}
}