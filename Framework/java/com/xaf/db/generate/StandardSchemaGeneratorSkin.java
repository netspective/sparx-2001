package com.xaf.db.generate;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.w3c.dom.*;
import com.xaf.db.*;
import com.xaf.sql.*;

public class StandardSchemaGeneratorSkin implements SchemaGeneratorSkin
{
	public class Index
	{
		private String name;    // name of index
		private String type;    // type of index (unique, etc)
		private String columnsStr; // comma-separated list
		private String[] columns;

		public Index(Element elem)
		{
			name = elem.getAttribute("name");
		    if(elem.getAttribute("unique").equals("yes"))
				type = "unique";

			StringBuffer columnsStr = new StringBuffer();
			NodeList columnElems = elem.getElementsByTagName("column");
			int columnsCount = columnElems.getLength();
			columns = new String[columnsCount];

			for(int c = 0; c < columnsCount; c++)
			{
				String colName = ((Element) columnElems.item(c)).getAttribute("name");
				columns[c] = colName;
				if(c > 0)
					columnsStr.append(", ");
				columnsStr.append(colName);
			}

			this.columnsStr = columnsStr.toString();
		}

		public String getName() { return name; }
		public String getType() { return type; }
		public String getColumnsStr() { return columnsStr; }
		public String[] getColumns() { return columns; }
	}

	public class Table
	{
		public Element table;
		public String name;
		public String abbrev;
		public Column[] columns;
		public Index[] indexes;
		public int longestColumnNameLen;

		public Table(Element elem)
		{
			table = elem;
			name = table.getAttribute("name");
			abbrev = table.getAttribute("abbrev");
			if(abbrev.length() == 0)
				abbrev = name;

			NodeList columnElems = table.getElementsByTagName("column");
			int columnsCount = columnElems.getLength();
			columns = new Column[columnsCount];

			for(int c = 0; c < columnsCount; c++)
			{
				Column column = new Column((Element) columnElems.item(c));
				columns[c] = column;

				if(column.name.length() > longestColumnNameLen)
					longestColumnNameLen = column.name.length();
			}

			NodeList indexElems = table.getElementsByTagName("index");
			int indexesCount = indexElems.getLength();
			indexes = new Index[indexesCount];

			for(int i = 0; i < indexesCount; i++)
			{
				Element indexElem = (Element) indexElems.item(i);
				indexes[i] = new Index(indexElem);
			}
		}
	}

	public class Column
	{
		public Element column;
		public String name;
		public String type;
		public boolean primaryKey;
		public boolean required;
		public String defaultValue;

		public Column(Element elem)
		{
			column = elem;
			name = column.getAttribute("name");
			type = findElementOrAttrValue(column, "sqldefn");
			if(type == null)
				type = "NO_SQLDEFN_PROVIDED";
			else
				type = type.toUpperCase();

			defaultValue = column.getAttribute("default");
			if(defaultValue.length() == 0)
				defaultValue = null;
			String value = column.getAttribute("required");
			if(value.equals("yes"))
				required = true;

			value = column.getAttribute("primarykey");
			if(value.equals("yes"))
				primaryKey = true;
		}
	}

	private Map tables = new Hashtable();
	private String statementTerminator = ";";

    public StandardSchemaGeneratorSkin()
    {
    }

	public Table getTable(Element tableElem)
	{
		Table result = (Table) tables.get(tableElem.getAttribute("name"));
		if(result == null)
			result = new Table(tableElem);
		return result;
	}

	public String findElementOrAttrValue(Element elem, String nodeName)
	{
		String attrValue = elem.getAttribute(nodeName);
		if(attrValue.length() > 0)
			return attrValue;

		NodeList children = elem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeName().equals(nodeName))
				return node.getFirstChild().getNodeValue();
		}

		return null;
	}

	public void generate(Writer writer, Element tableElem, int item) throws IOException
	{
		switch(item)
		{
			case GENERATE_DROP_TABLE_DEFN:
			    generateDropTableDefn(writer, tableElem);
				break;

			case GENERATE_TABLE_DEFN:
			    generateTableDefn(writer, tableElem);
				break;

			case GENERATE_FKEY_CONSTRAINTS:
			    generateTableContraints(writer, tableElem);
				break;

			case GENERATE_TRIGGER_CODE:
			    generateTableCode(writer, tableElem);
				break;

			case GENERATE_INDEXES:
			    generateTableIndexes(writer, tableElem);
				break;

			case GENERATE_TABLE_DATA:
			    generateTableData(writer, tableElem);
				break;
		}
	}

	public void generateDropTableDefn(Writer writer, Element tableElem) throws IOException
	{
		writer.write("drop table " + tableElem.getAttribute("name") + statementTerminator + "\n");
	}

	public void generateTableDefn(Writer writer, Element tableElem) throws IOException
	{
		Table table = getTable(tableElem);
		int columnsCount = table.columns.length;
		if(columnsCount == 0)
			return;

		writer.write("create table " + table.name + "\n(\n");

		for(int c = 0; c < columnsCount; c++)
		{
			String columnDefn = getColumnDefnSql(table, c);
			writer.write("\t" + columnDefn + (c == (columnsCount-1) ? "\n" : ",\n"));
		}

		writer.write(")"+statementTerminator +"\n");
	}

	public String getColumnDefnSql(Table table, int columnNum)
	{
		Column column = table.columns[columnNum];
		int padColLen = table.longestColumnNameLen+1;

		StringBuffer sql = new StringBuffer();

		sql.append(column.name);
		for(int i = column.name.length(); i < padColLen; i++)
			sql.append(" ");

		sql.append(column.type);

		if(column.primaryKey)
		{
			sql.append(" PRIMARY KEY");
		}
		else
		{
			if(column.required)
	    		sql.append(" NOT NULL");
		}

		if(column.defaultValue != null)
		{
			sql.append(" DEFAULT ");
			sql.append(column.defaultValue);
		}

		return sql.toString();
	}

	public void generateTableContraints(Writer writer, Element tableElem) throws IOException
	{
		Table table = getTable(tableElem);
		for(int c = 0; c < table.columns.length; c++)
		{
			Column column = table.columns[c];
			Element columnElem = column.column;
			if(columnElem.getAttribute("reftype").length() > 0)
			{
				writer.write("alter table ");
				writer.write(table.name);
				writer.write(" add (constraint ");
				writer.write(table.abbrev.toUpperCase());
				writer.write("_");
				writer.write(column.name.toUpperCase());
				writer.write("_FK foreign key (");
				writer.write(column.name);
				writer.write(") references ");
				writer.write(columnElem.getAttribute("reftbl"));
				writer.write("(");
				writer.write(columnElem.getAttribute("refcol"));
				writer.write("))");
				writer.write(statementTerminator);
				writer.write("\n");
			}
		}
	}

	public void generateTableIndexes(Writer writer, Element tableElem) throws IOException
	{
		Table table = getTable(tableElem);
		if(table.indexes.length > 0)
		{
			for(int i = 0; i < table.indexes.length; i++)
			{
				Index index = table.indexes[i];
				String type = index.getType();

				writer.write("create ");
				if(type != null)
				{
					writer.write(type);
					writer.write(" ");
				}
				writer.write("index ");
				writer.write(table.abbrev.toUpperCase());
				writer.write("_");
				writer.write(index.getName().toUpperCase());
				writer.write(" on ");
				writer.write(table.name);
				writer.write(" (");
				writer.write(index.getColumnsStr());
				writer.write(")"+statementTerminator+"\n");
			}
		}
	}

	public void generateTableCode(Writer writer, Element tableElem) throws IOException
	{
	}

	public void generateTableData(Writer writer, Element tableElem) throws IOException
	{
		Table table = getTable(tableElem);

		NodeList enumElems = tableElem.getElementsByTagName("enum");
		int enumsCount = enumElems.getLength();
		if(enumsCount > 0)
		{
			for(int e = 0; e < enumsCount; e++)
			{
				Element enumElem = (Element) enumElems.item(e);
				String id = enumElem.getAttribute("id");
				if(id.length() == 0)
					id = Integer.toString(e);
				String abbrev = enumElem.getAttribute("abbrev");
				if(abbrev.length() == 0)
					abbrev = null;
			    String caption = enumElem.getFirstChild().getNodeValue();

				String sql;
				if(abbrev == null)
					sql = "insert into " + table.name + " (id, caption) values (" +
						    id + ", '" + caption + "')" + statementTerminator + "\n";
				else
					sql = "insert into " + table.name + " (id, caption, abbrev) values (" +
						    id + ", '" + caption + "', '"+ abbrev +"')" + statementTerminator + "\n";

				writer.write(sql);
			}
		}
	}

}