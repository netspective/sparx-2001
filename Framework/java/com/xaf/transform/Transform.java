package com.xaf.transform;

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
import com.caucho.xml.*;
import com.caucho.xsl.*;
import com.caucho.transform.*;

public class Transform
{
	static public String nodeToString(String styleSheet, Node node, Hashtable params)
	{
		//OutputStream os = new ByteArrayOutputStream();
		//XmlPrinter xp = new XmlPrinter(os);
		//xp.printPrettyXml(node);
		//return os.toString();

		try
		{
			StringTransformer transformer = new Xsl().newStylesheet(styleSheet).newStringTransformer();
			if(params != null)
			{
				Enumeration e = params.keys();
				while(e.hasMoreElements())
				{
					String paramName = (String) e.nextElement();
					transformer.setParameter(paramName, params.get(paramName));
				}
			}
	    	return transformer.transform(node);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}

	static public String nodeToString(String styleSheet, Node node)
	{
		return nodeToString(styleSheet, node, null);
	}
}