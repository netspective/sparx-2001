package com.xaf.db;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

import com.xaf.xml.*;

/**
 * Provides the ability to fully describe an entire database
 * schema in a single or multiple XML files; complete support for data
 * dictionaries, column domains, and table inheritance is built-in.
 * These XML files can then be read in a run-time to provide complete
 * database schema information in a RDBMS-independent manner.
 * After a schema is read in, complete information about all tables,
 * columns, enumerations, foreign keys, indexes, etc are available for processing
 * or DDL generation. A reference implementation servlet is provided to allow to
 * be able to browse a SchemaDocument (providing a full online database
 * design document that could easily replace ERDs).
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class SchemaDocument extends XmlSource
{
	public static final String ATTRNAME_TYPE = "type";

	public static final String[] MACROSIN_COLUMNNODES = { "parentref", "lookupref", "selfref", "usetype", "cache", "sqldefn", "size" };
    public static final String[] MACROSIN_TABLENODES = { "name", "abbrev", "parent" };
    public static final String[] REFTYPE_NAMES = { "none", "parent", "lookup", "self", "usetype" };

	public static final int REFTYPE_NONE    = 0;
	public static final int REFTYPE_PARENT  = 1;
	public static final int REFTYPE_LOOKUP  = 2;
	public static final int REFTYPE_SELF    = 3;
	public static final int REFTYPE_USETYPE = 4;

    static HashSet replaceMacrosInColumnNodes = null;
    static HashSet replaceMacrosInTableNodes = null;

	private Hashtable dataTypeNodes = new Hashtable();
	private Hashtable tableTypeNodes = new Hashtable();
	private Hashtable indexTypeNodes = new Hashtable();
	private Hashtable tableNodes = new Hashtable();
	private ArrayList columnTableNodes = new ArrayList();
    private Hashtable tableParams = new Hashtable(); // key is table name, value is hash-table of key/value pairs

	public SchemaDocument()
	{
        if(replaceMacrosInColumnNodes == null)
        {
            replaceMacrosInColumnNodes = new HashSet();
            replaceMacrosInTableNodes = new HashSet();

            for(int i = 0; i < MACROSIN_COLUMNNODES.length; i++)
                replaceMacrosInColumnNodes.add(MACROSIN_COLUMNNODES[i]);
            for(int i = 0; i < MACROSIN_TABLENODES.length; i++)
                replaceMacrosInTableNodes.add(MACROSIN_TABLENODES[i]);
        }
    }

	public SchemaDocument(File file)
	{
        this();
		loadDocument(file);
 	}

    public void setUrl(String url)
    {
        loadDocument(new File(url));
    }

	public Map getDataTypes() { return dataTypeNodes;	}
	public Map getTableTypes() { return tableTypeNodes; }
	public Map getIndexTypes() { return indexTypeNodes; }

	public Map getTables() { return tableNodes; }

	public String[] getTableNames(boolean includeAudit)
	{
		ArrayList tableNames = new ArrayList();

		if(includeAudit)
		{
			for(Enumeration e = tableNodes.elements(); e.hasMoreElements(); )
			{
				Element table = (Element) e.nextElement();
				tableNames.add(table.getAttribute("name"));
			}
		}
		else
		{
			for(Enumeration e = tableNodes.elements(); e.hasMoreElements(); )
			{
				Element table = (Element) e.nextElement();
				if(! table.getAttribute("is-audit").equals("yes"))
					tableNames.add(table.getAttribute("name"));
			}
		}

		String[] result = new String[tableNames.size()];
		tableNames.toArray(result);
		Arrays.sort(result);
		return result;
	}

	public void inheritNodes(Element element, Hashtable sourcePool)
	{
		inheritNodes(element, sourcePool, ATTRNAME_TYPE);
	}

	public DocumentFragment getCompositeColumns(Element column, Element composite)
	{
		NodeList compNodes = composite.getChildNodes();
		String compositeName = column.getAttribute("name");
		DocumentFragment compColumns = composite.getOwnerDocument().createDocumentFragment();
		for(int c = 0; c < compNodes.getLength(); c++)
		{
			Node compNode = compNodes.item(c);
			if(compNode.getNodeName().equals("column"))
			{
				Node nameAttr = ((Element) compNode).getAttributeNode("name");
				replaceNodeValue(nameAttr, "$name$", compositeName);
		        fixupColumnElement((Element) compNode);
				((Element) compNode).setAttribute("descr", column.getAttribute("descr"));
				compColumns.appendChild(compNode);
			}
		}
		return compColumns;
	}

    public void fixupColumnElement(Element column)
    {
        inheritNodes(column, dataTypeNodes);

        NodeList colInfo = column.getChildNodes();
        Element sqlDefnElem = null;
        String size = null;

        size = column.getAttribute("size");
        if(size != null && size.length() == 0)
            size = null;

        for(int i = 0; i < colInfo.getLength(); i++)
        {
            Node childNode = colInfo.item(i);
            String nodeName = childNode.getNodeName();

            if(nodeName.equals("table"))
			{
				columnTableNodes.add(childNode);
                fixupTableElement((Element) childNode);
			}
            else if(nodeName.equals("sqldefn"))
                sqlDefnElem = (Element) childNode;
            else if (size == null && nodeName.equals("size"))
                size = childNode.getFirstChild().getNodeValue();
        }

        if(sqlDefnElem != null && size != null)
            replaceNodeValue(sqlDefnElem.getFirstChild(), "%size%", size);
    }

	public String getPrimaryKey(Element table)
	{
        NodeList columns = table.getChildNodes();
        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

			if(node.getNodeName().equals("column") && ((Element) node).getAttribute("primarykey").equals("yes"))
				return 	((Element) node).getAttribute("name");
        }

		return null;
	}

	public Element getParentConnectorColumn(Element table)
	{
        NodeList columns = table.getChildNodes();
        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

			if(node.getNodeName().equals("column") && ((Element) node).getAttribute("reftype").equals("parent"))
				return (Element) node;
        }

		return null;
	}

	public void resolveIndexes(Element table)
	{
        String tableName = table.getAttribute("name");
		List columnIndexes = new ArrayList();
		Document tableDoc = table.getOwnerDocument();

        NodeList columns = table.getChildNodes();
		Element colIndexElem = null;
		Element colNameElem = null;
        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String nodeName = node.getNodeName();
            if(nodeName.equals("column"))
			{
				Element column = (Element) node;
				String indexColName = column.getAttribute("name");
				if(column.getAttribute("unique").equals("yes"))
				{
					colIndexElem = tableDoc.createElement("index");
					colIndexElem.setAttribute("name", indexColName + "_unq");
					colIndexElem.setAttribute("type", "unique");
					colIndexElem.setAttribute("columns", indexColName);
					colNameElem = tableDoc.createElement("column");
					colNameElem.setAttribute("name", indexColName);
					colIndexElem.appendChild(colNameElem);
					columnIndexes.add(colIndexElem);
				}
				else if(column.getAttribute("indexed").equals("yes"))
				{
					colIndexElem = tableDoc.createElement("index");
					colIndexElem.setAttribute("name", indexColName);
					colIndexElem.setAttribute("columns", indexColName);
					colNameElem = tableDoc.createElement("column");
					colNameElem.setAttribute("name", indexColName);
					colIndexElem.appendChild(colNameElem);
					columnIndexes.add(colIndexElem);
				}

				/* the old Physia schema attributes 'indexgrp' and 'uniquegrp' are no longer supported */
				if(column.getAttribute("indexgrp").length() > 0)
					errors.add("The 'indexgrp' attribute is no longer supported in table '"+ tableName +"' column '"+ indexColName +"' (use 'index' tag instead)");
				if(column.getAttribute("uniquegrp").length() > 0)
					errors.add("The 'uniquegrp' attribute is no longer supported in table '"+ tableName +"' column '"+ indexColName +"' (use 'index' tag instead)");
			}
		    else if(nodeName.equals("index"))
			{
				Element index = (Element) node;
		        inheritNodes(index, indexTypeNodes);

				String indexName = index.getAttribute("name");
				NodeList columnElems = table.getElementsByTagName("column");
				int columnsCount = columnElems.getLength();

				String columnsList = index.getAttribute("columns");
				if(columnsList.length() > 0)
				{
					StringTokenizer st = new StringTokenizer(index.getAttribute("columns"), ",");
					while(st.hasMoreTokens())
					{
						String indexColName = st.nextToken().trim();
						boolean found = false;
						for(int ic = 0; ic < columnsCount; ic++)
						{
							Element columnElem = (Element) columnElems.item(ic);
							if(columnElem.getAttribute("name").equals(indexColName))
							{
								found = true;
								colNameElem = tableDoc.createElement("column");
								colNameElem.setAttribute("name", indexColName);
								index.appendChild(colNameElem);
							}
						}
						if(! found)
						{
							errors.add("Column '"+indexColName+"' not found in index '"+ indexName +"' of table '"+ tableName +"'");
						}
					}
				}
			}
        }

		if(columnIndexes.size() > 0)
		{
			for(Iterator i = columnIndexes.iterator(); i.hasNext(); )
			{
				table.appendChild((Element) i.next());
			}
		}
	}

    public void fixupTableElement(Element table)
    {
        Hashtable params = new Hashtable();
        String tableName = table.getAttribute("name");
        tableParams.put(tableName, params);
        inheritNodes(table, tableTypeNodes);

        NodeList columns = table.getChildNodes();
        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) node;
            String nodeName = node.getNodeName();

            if(nodeName.equals("column"))
                fixupColumnElement(childElem);
            else if (nodeName.equals("param"))
                params.put(childElem.getAttribute("name"), childElem.getFirstChild().getNodeValue());
        }

		Node[][] replaceCols = new Node[columns.getLength()][];
		int compIndex = 0;

        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if(node.getNodeName().equals("column"))
			{
				NodeList composites = ((Element) node).getElementsByTagName("composite");
				for(int cc = 0; cc < composites.getLength(); cc++)
				{
					DocumentFragment compCols = getCompositeColumns((Element) node, (Element) composites.item(cc));
					replaceCols[compIndex] = new Node[] { node, compCols };
					compIndex++;
				}
			}
        }

		for(int j = 0; j < compIndex; j++)
		{
			table.insertBefore(replaceCols[j][1], replaceCols[j][0]);
			table.removeChild(replaceCols[j][0]);
		}

        Element tableParentElem = (Element) table.getParentNode();
        if(tableParentElem != null && tableParentElem.getNodeName().equals("column"))
        {
            Element parentColumn = tableParentElem;
            Element parentColTable = (Element) parentColumn.getParentNode();

            params.put("parenttbl_name", parentColTable.getAttribute("name"));
            params.put("parenttbl_Name", ucfirst(parentColTable.getAttribute("name")));
            params.put("parenttbl_abbrev", parentColTable.getAttribute("abbrev"));

            String primaryKey = getPrimaryKey(parentColTable);
            if(primaryKey != null)
                params.put("parenttbl_prikey", primaryKey);

            params.put("parentcol_name", parentColumn.getAttribute("name"));
            params.put("parentcol_Name", ucfirst(parentColumn.getAttribute("name")));
            params.put("parentcol_abbrev", parentColumn.getAttribute("abbrev"));
            params.put("parentcol_short", parentColumn.getAttribute("abbrev"));
            params.put("parentcol_Short", parentColumn.getAttribute("abbrev"));
        }

        replaceNodeMacros((Node) table, replaceMacrosInTableNodes, params);

        columns = table.getChildNodes();
        for(int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String nodeName = node.getNodeName();
            if(nodeName.equals("column"))
                replaceNodeMacros(node, replaceMacrosInColumnNodes, params);
			else if(nodeName.equals("parent"))
			{
				if(table.getAttribute("parent").length() == 0)
					table.setAttribute("parent", node.getFirstChild().getNodeValue());
			}
            else if (nodeName.equals("extends"))
            {
                if(node.getFirstChild().getNodeValue().equals("Audit"))
                    table.setAttribute("audit", "yes");
            }
        }

		resolveIndexes(table);
    }

	class Reference
	{
		String reference;
		String tableName;
		String columnName;
		int type;

		Reference(String ref, int refType)
		{
			reference = ref;
			type = refType;
			StringTokenizer st = new StringTokenizer(ref, ".");
			tableName = st.nextToken();
			if(st.hasMoreTokens())
				columnName = st.nextToken();
			else
				columnName = "id";
		}
	}

	public Reference getRefInfo(Element elem, String attrName, int refType)
	{
		String attrValue = elem.getAttribute(attrName);
		if(attrValue.length() == 0)
			return null;
		else
			return new Reference(attrValue, refType);
	}

    public void resolveTableReferences(Element tableElem)
    {
        NodeList children = tableElem.getChildNodes();

        RESOLVE_REF_COLUMN:
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(! node.getNodeName().equals("column"))
                continue;

            Element column = (Element) node;
            Reference refInfo = getRefInfo(column, "lookupref", REFTYPE_LOOKUP);
            if(refInfo == null)
            {
                refInfo = getRefInfo(column, "parentref", REFTYPE_PARENT);
                if(refInfo != null)
                    tableElem.setAttribute("parent", refInfo.tableName);
                else
                {
                    refInfo = getRefInfo(column, "selfref", REFTYPE_SELF);
                    if(refInfo != null)
                        refInfo.tableName = tableElem.getAttribute("name");
                    else
                        refInfo = getRefInfo(column, "usetype", REFTYPE_USETYPE);
                }
            }

            if(refInfo == null)
                continue;

            Element refTable = (Element) tableNodes.get(refInfo.tableName.toUpperCase());
            if(refTable == null)
            {
                errors.add("Table '"+ refInfo.tableName +"' not found for "+ REFTYPE_NAMES[refInfo.type] +" reference '"+ refInfo.reference +"' (in table '"+ tableElem.getAttribute("name") +"' column '"+ column.getAttribute("name") +"')");
                continue RESOLVE_REF_COLUMN;
            }

            column.setAttribute("reftype", REFTYPE_NAMES[refInfo.type]);
            column.setAttribute("reftbl", refTable.getAttribute("name"));
            NodeList refTableColumns = refTable.getChildNodes();
            for(int rc = 0; rc < refTableColumns.getLength(); rc++)
            {
                Node refTableColumnNode = refTableColumns.item(rc);
                if(refTableColumnNode.getNodeName().equals("column"))
                {
                    Element refColumnElem = (Element) refTableColumnNode;
                    String refColumnName = refColumnElem.getAttribute("name");
                    if(refColumnName.equalsIgnoreCase(refInfo.columnName))
                    {
                        column.setAttribute("refcol", refColumnName);
						if(column.getAttribute("type").length() > 0)
		                    errors.add("In a reference column, 'type' and other related modifiers like 'size' should not be specified (in table '"+ tableElem.getAttribute("name") +"' column '"+ column.getAttribute("name") +"')");

                        String copyType = findElementOrAttrValue(refColumnElem, "copytype");
                        if(copyType != null && copyType.length() > 0)
                            column.setAttribute("type", copyType);
                        else
                            column.setAttribute("type", refColumnElem.getAttribute("type"));

                        fixupColumnElement(column);

                        Element refByElem = xmlDoc.createElement("referenced-by");
                        refColumnElem.appendChild(refByElem);
                        refByElem.setAttribute("type", REFTYPE_NAMES[refInfo.type]);
                        refByElem.setAttribute("table", tableElem.getAttribute("name"));
                        refByElem.setAttribute("column", column.getAttribute("name"));

                        continue RESOLVE_REF_COLUMN;
                    }
                }
            }

            errors.add("Column '" + refInfo.columnName + "' not found in Table '"+ refInfo.tableName +"' for "+ REFTYPE_NAMES[refInfo.type] +" reference '"+ refInfo.reference +"' (in table '"+ tableElem.getAttribute("name") +"' column '"+ column.getAttribute("name") +"')");
        }
    }

	public void resolveReferences()
	{
		Enumeration e = tableNodes.elements();
		while(e.hasMoreElements())
		{
			Element tableElem = (Element) e.nextElement();
            resolveTableReferences(tableElem);
		}
	}

    public void createAuditTables()
    {
        Element[] auditTables = new Element[tableNodes.size()];
        int auditTablesCount = 0;

        Element docElem = xmlDoc.getDocumentElement();
		Enumeration e = tableNodes.elements();
		while(e.hasMoreElements())
        {
            Element mainTable = (Element) e.nextElement();
            if(mainTable.getAttribute("audit").equals("yes"))
            {
                String mainTableName = mainTable.getAttribute("name");
                Element auditTable = xmlDoc.createElement("table");
                auditTable.setAttribute("name", mainTableName + "_AUD");
                auditTable.setAttribute("parent", mainTableName);
				auditTable.setAttribute("is-audit", "yes");

                NodeList copyColumns = mainTable.getChildNodes();
                for(int n = 0; n < copyColumns.getLength(); n++)
                {
                    Node node = copyColumns.item(n);
                    if(! node.getNodeName().equals("column"))
                        continue;

                    Element mainColumn = (Element) node;
                    String mainColumnName = mainColumn.getAttribute("name");

                    Element auditColumn = xmlDoc.createElement("column");
                    auditColumn.setAttribute("name", mainColumnName);
                    auditColumn.setAttribute("usetype", mainTableName + "." + mainColumnName);
                    auditColumn.setAttribute("descr", mainColumn.getAttribute("descr"));

                    auditTable.appendChild(auditColumn);
                }

                docElem.appendChild(auditTable);
                auditTables[auditTablesCount] = auditTable;
                auditTablesCount++;
            }
        }

        for(int i = 0; i < auditTablesCount; i++)
        {
            Element auditTable = auditTables[i];
            resolveTableReferences(auditTable);
            docElem.appendChild(auditTable);
            tableNodes.put(auditTable.getAttribute("name").toUpperCase(), auditTable);
        }
    }

	public void addTableStructure(Element structure, Element table, int level)
	{
		Element tableStruct = xmlDoc.createElement("table");
		String tableName = table.getAttribute("name");
		tableStruct.setAttribute("name", tableName);
		tableStruct.setAttribute("level", String.valueOf(level));
		structure.appendChild(tableStruct);

        Element connector = getParentConnectorColumn(table);
        if(connector != null)
        {
            tableStruct.setAttribute("parent-col", connector.getAttribute("refcol"));
            tableStruct.setAttribute("child-col", connector.getAttribute("name"));
        }

		Enumeration e = tableNodes.elements();
		while(e.hasMoreElements())
		{
			Element childTable = (Element) e.nextElement();
		    if(childTable.getAttribute("parent").equals(tableName))
			{
				addTableStructure(tableStruct, childTable, level+1);
			}
		}
	}

	public void createStructure()
	{
		Element structure = xmlDoc.createElement("table-structure");
		xmlDoc.getDocumentElement().appendChild(structure);

		Enumeration e = tableNodes.elements();
		while(e.hasMoreElements())
		{
			Element table = (Element) e.nextElement();
		    if(table.getAttribute("parent").length() == 0)
			{
				addTableStructure(structure, table, 0);
			}
		}
	}

	public void catalogNodes()
	{
		dataTypeNodes.clear();
		indexTypeNodes.clear();
		tableTypeNodes.clear();
		tableNodes.clear();
		columnTableNodes.clear();
		tableParams.clear();

        if(xmlDoc == null)
            return;

		NodeList children = xmlDoc.getDocumentElement().getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element element = (Element) node;
			String nodeName = node.getNodeName();

			if(nodeName.equals("datatype"))
            {
				dataTypeNodes.put(element.getAttribute("name"), node);

                // sqlWriteFmt was used in the perl version, but JDBC doesn't
                // require it so we'll remove the sqlWriteFmt to help save space
                NodeList sqlWriteFmt = ((Element) node).getElementsByTagName("sqlwritefmt");
                if(sqlWriteFmt.getLength() > 0)
                    node.removeChild(sqlWriteFmt.item(0));
            }
			else if(nodeName.equals("tabletype"))
				tableTypeNodes.put(element.getAttribute("name"), node);
			else if(nodeName.equals("indextype"))
				indexTypeNodes.put(element.getAttribute("name"), node);
			else if(nodeName.equals("table"))
				tableNodes.put(element.getAttribute("name").toUpperCase(), node);
		}

		Enumeration e = tableNodes.elements();
		while(e.hasMoreElements())
			fixupTableElement((Element) e.nextElement());

		/*
		 * at this time, all the inheritance and macro replacements should
		 * be complete, so we go ahead an "pull out" the tables that should
		 * be auto-generated (from columns) and resolve all references
		 */

		Node rootNode = xmlDoc.getDocumentElement();
		Iterator i = columnTableNodes.iterator();
		while(i.hasNext())
		{
			Element columnTableElem = (Element) i.next();
			tableNodes.put(columnTableElem.getAttribute("name").toUpperCase(), columnTableElem);
			rootNode.appendChild(columnTableElem);
		}

		resolveReferences();
        createAuditTables();
		createStructure();
		addMetaInformation();
	}
}

