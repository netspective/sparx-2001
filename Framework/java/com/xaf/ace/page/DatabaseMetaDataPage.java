package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.db.generate.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DatabaseMetaDataPage extends AceServletPage
{
	public final String getName() { return "meta-data"; }
	public final String getPageIcon() { return "schema.gif"; }
	public final String getCaption(PageContext pc) { return "DB Meta Data"; }
	public final String getHeading(PageContext pc) { return "Database Meta Data"; }

	public Document createDocument(PageContext pc, String dataSourceId) throws ParserConfigurationException, NamingException, SQLException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();

		Element root = doc.createElement("schema");
		root.setAttribute("name", dataSourceId);
		doc.appendChild(root);

		DatabaseContext dbc = DatabaseContextFactory.getContext(pc.getRequest(), pc.getServletContext());
		Connection conn = dbc.getConnection(pc, dataSourceId);

		DatabaseMetaData dbmd = conn.getMetaData();
		Map types = new HashMap();
		ResultSet typesRS = dbmd.getTypeInfo();
		while(typesRS.next())
		{
			types.put(typesRS.getString(2), typesRS.getString(1));
		}
		typesRS.close();

		ResultSet tables = dbmd.getTables(null, null, null, new String[] {"TABLE", "VIEW"});
		while(tables.next())
		{
			String tableName = tables.getString(3);

			Element table = doc.createElement("table");
			table.setAttribute("name", tableName);
			root.appendChild(table);

			Map primaryKeys = new HashMap();
			try
			{
				ResultSet pkRS = dbmd.getPrimaryKeys(null, null, tableName);
				while(pkRS.next())
				{
					primaryKeys.put(pkRS.getString(4), pkRS.getString(5));
				}
			}
			catch(Exception e)
			{
				// driver may not support this function
			}

			ResultSet columns = dbmd.getColumns(null, null, tableName, null);
			while(columns.next())
			{
				String columnName = columns.getString(4);
				Element column = doc.createElement("column");
				try
				{
					column.setAttribute("name", columnName);
					column.setAttribute("type", columns.getString(6));
					if(primaryKeys.containsKey(columnName))
						column.setAttribute("primarykey", "yes");

					column.setAttribute("sqldefn", types.get(columns.getString(5)) + " " + columns.getString(7));
					column.setAttribute("descr", columns.getString(12));
					column.setAttribute("default", columns.getString(13));
				}
				catch(Exception e)
				{
				}

				table.appendChild(column);
			}
		    columns.close();
		}
		tables.close();
		conn.close();

		return doc;
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		try
		{
			String dataSource = pc.getRequest().getParameter("data-src");
			if(dataSource != null)
			    transform(pc, createDocument(pc, dataSource), ACE_CONFIG_ITEMS_PREFIX + "schema-browser-xsl");
		}
		catch(NamingException e)
		{
			pc.getResponse().getWriter().write(e.toString());
		}
		catch(SQLException e)
		{
			pc.getResponse().getWriter().write(e.toString());
		}
		catch(ParserConfigurationException e)
		{
			pc.getResponse().getWriter().write(e.toString());
		}
	}
}