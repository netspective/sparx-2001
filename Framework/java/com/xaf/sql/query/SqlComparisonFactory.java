package com.xaf.sql.query;

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
import com.xaf.sql.query.comparison.*;

public class SqlComparisonFactory
{
	static List comparisonsList = new ArrayList();
	static Map comparisonsMap = new Hashtable();
	static boolean defaultsAvailable = false;

	static public void addComparison(SqlComparison comp, String[] aliases)
	{
		comparisonsList.add(comp);
		comparisonsMap.put(comp.getName(), comp);

		if(aliases != null)
		{
			for(int i = 0; i < aliases.length; i++)
				comparisonsMap.put(aliases[i], comp);
		}
	}

	static public void setupDefaults()
	{
		addComparison(new BinaryOpComparison("equals", "equals", "general", "="), new String[] { "is", "=" });
		addComparison(new StartsWithComparison(), null);
		addComparison(new ContainsComparison(), null);
		addComparison(new EndsWithComparison(), null);
   		addComparison(new InComparison(), null);
		addComparison(new IsDefinedComparison(), null);
		addComparison(new BinaryOpComparison("greater-than", "greater than", "general", ">"), new String[] { "gt", ">" });
		addComparison(new BinaryOpComparison("greater-than-equal", "greater than or equal to", "general", ">="), new String[] { "gte", ">=" });
		addComparison(new BinaryOpComparison("less-than", "less than", "general", "<"), new String[] { "lt", "<" });
		addComparison(new BinaryOpComparison("less-than-equal", "less than or equal to", "general", "<="), new String[] { "lte", "<=" });
		defaultsAvailable = true;
	}

	static public List getComparisonsList()
	{
		if(! defaultsAvailable)
			setupDefaults();

		return comparisonsList;
	}

	static public SqlComparison getComparison(String name)
	{
		if(! defaultsAvailable)
			setupDefaults();

		return (SqlComparison) comparisonsMap.get(name);
	}

	public static void createCatalog(Element parent)
	{
		if(! defaultsAvailable) setupDefaults();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "SQL Comparisons");
		factoryElem.setAttribute("class", SqlComparisonFactory.class.getName());
		for(Iterator i = comparisonsMap.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("dialog-field");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((SqlComparison) entry.getValue()).getClass().getName());
			factoryElem.appendChild(childElem);
		}
	}

}