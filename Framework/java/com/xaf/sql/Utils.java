package com.xaf.sql;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Utils
{
	static public Document resultSetToXML(ResultSet rs) throws SQLException, ParserConfigurationException
	{
		ResultSetMetaData rsmd = rs.getMetaData();

		Document dataDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element tableElem = (Element) dataDoc.appendChild(dataDoc.createElement("data-table"));
		Element headElem = (Element) tableElem.appendChild(dataDoc.createElement("data-table-head"));
		Element rowSetElem = (Element) tableElem.appendChild(dataDoc.createElement("data-table-row-set"));

		int numColumns = rsmd.getColumnCount();
		for(int i = 1; i <= numColumns; i++)
		{
			Element headCol = (Element) headElem.appendChild(dataDoc.createElement(rsmd.getColumnName(i)));
			headCol.setAttribute("index", new Integer(i).toString());
			headCol.setAttribute("heading", rsmd.getColumnLabel(i).replace('_', ' '));
			int colType = rsmd.getColumnType(i);
			if(colType == Types.BIGINT ||
				colType == Types.NUMERIC ||
				colType == Types.DECIMAL ||
				colType == Types.FLOAT ||
				colType == Types.INTEGER)
				headCol.setAttribute("data-type", "number");
			else if(rsmd.isCurrency(i))
				headCol.setAttribute("data-type", "currency");
			else if(colType == Types.DATE)
				headCol.setAttribute("data-type", "date");
			else if(colType == Types.TIME)
				headCol.setAttribute("data-type", "time");
			else if(colType == Types.TIMESTAMP)
				headCol.setAttribute("data-type", "timestamp");
		}

		int rowNum = 0;
		while(rs.next())
		{
			Element row = (Element) rowSetElem.appendChild(dataDoc.createElement("data-table-row"));
			row.setAttribute("num", new Integer(rowNum).toString());

			for(int i = 1; i <= numColumns; i++)
			{
				Element dataCol = (Element) row.appendChild(dataDoc.createElement(rsmd.getColumnName(i)));
				dataCol.appendChild(dataDoc.createTextNode(rs.getString(i)));
			}
		}

		return dataDoc;
	}
}