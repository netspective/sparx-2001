/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: SchemaDocument.java,v 1.21 2002-12-23 05:02:23 shahid.shah Exp $
 */

package com.netspective.sparx.xif;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xif.dal.Schema;

/**
 * Provides the ability to fully describe an entire database
 * schema in a single or multiple XML files; complete support for data
 * dictionaries, column domains, and table inheritance is built-in.
 * These XML files can then be read in at run-time to provide complete
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
    public static final String ELEMNAME_DAL_GENERATOR = "dal-generator";
    public static final String ELEMNAME_GENERATE_ID = "generate-id";

    public static final String ATTRNAME_TYPE = "type";
    public static final String ATTRNAME_DAL_PACKAGE = "package";
    public static final String ATTRNAME_DAL_STYLESHEET = "style-sheet";
    public static final String ATTRPREFIX_INHERITFIELD = "field.";

    public static final String[] MACROSIN_COLUMNNODES = {"parentref", "lookupref", "selfref", "usetype", "cache", "sqldefn", "size", "decimals", "default"};
    public static final String[] MACROSIN_TABLENODES = {"name", "abbrev", "parent"};
    public static final String[] MACROSIN_INDEXNODES = {"name"};
    public static final String[] REFTYPE_NAMES = {"none", "parent", "lookup", "self", "usetype"};

    public static final String[] JAVA_RESERVED_WORDS = {"abstract", "assert", "boolean", "break", "byte",
                                                        "byvalue", "case", "cast", "catch",
                                                        "char", "class", "const", "continue",
                                                        "default", "do", "double", "else",
                                                        "extends", "false", "final", "finally",
                                                        "float", "for", "future", "generic",
                                                        "goto", "if", "implements", "import",
                                                        "inner", "instanceof", "int", "interface",
                                                        "long", "native", "new", "null", "operator",
                                                        "outer", "package", "private", "protected",
                                                        "public", "rest", "return", "short",
                                                        "static", "super", "switch", "synchronised",
                                                        "this", "throw", "throws", "transient",
                                                        "true", "try", "var", "void", "volatile",
                                                        "while"};

    public static final String[] JAVA_RESERVED_TERMS = {"append", "clone", "equals", "finalize", "getClass", "hashCode",
                                                        "insert", "interrupt", "length", "notify", "print", "println",
                                                        "read", "resume", "run", "sleep", "start", "stop", "suspend",
                                                        "toString", "wait", "write", "valueOf", "yield"};

    public static final int REFTYPE_NONE = 0;
    public static final int REFTYPE_PARENT = 1;
    public static final int REFTYPE_LOOKUP = 2;
    public static final int REFTYPE_SELF = 3;
    public static final int REFTYPE_USETYPE = 4;

    private static Set replaceMacrosInColumnNodes = null;
    private static Set replaceMacrosInTableNodes = null;
    private static Set replaceMacrosInIndexNodes = null;
    private static Set javaReservedWords = new HashSet();
    private static Set javaReservedTerms = new HashSet();
    private static Set refColumnExcludeElementsFromInherit = new HashSet();
    private static Set dialogFieldExcludeColElemsFromInherit = new HashSet();

    private Map dataTypeNodes = new HashMap();
    private Map tableTypeNodes = new HashMap();
    private Map indexTypeNodes = new HashMap();
    private Map tableNodes = new HashMap();
    private List columnTableNodes = new ArrayList();
    private Map tableParams = new HashMap(); // key is table name, value is hash-table of key/value pairs
    private Map enumTableDataChoices = new HashMap(); // key is an enum data table name, value is SelectChoicesList
    private Map tableDialogDefns = new HashMap(); // key is uppercase TABLE_NAME, value is DialogDefinition instance
    private Map dialogFieldDefns = new HashMap(); // key is uppercase TABLE_NAME.COLUMN_NAME, value is a DialogFieldDefinition instance
    private Map columnsWithFieldDefns = new HashMap(); // key is uppercase TABLE_NAME, value is a string list with names of columns with field defns
    private DataAccessLayerProperties dalProperties = new DataAccessLayerProperties();

    /**
     * Class which holds information about a JDBC type and its associated JavaClass and primitive type
     */
    static class JdbcDataType
    {
        private String typeName;
        private Integer jdbcType;
        private String javaType;
        private String javaTypeDefault;
        private String javaClassPkg;
        private String javaClass;

        public JdbcDataType(int jdtype)
        {
            jdbcType = new Integer(jdtype);
        }

        public JdbcDataType(String name, int jdtype, String jprimitive, String primDefault, String jclassPkg, String jclass)
        {
            typeName = name;
            jdbcType = new Integer(jdtype);
            javaType = jprimitive;
            javaTypeDefault = primDefault;
            javaClassPkg = jclassPkg;
            javaClass = jclass;
        }

        public JdbcDataType(String name, int jdtype, String jclassPkg, String jclass)
        {
            typeName = name;
            jdbcType = new Integer(jdtype);
            javaClassPkg = jclassPkg;
            javaClass = jclass;
        }

        public Element createTextElem(Element parent, String elemName, String elemText)
        {
            Element elem = parent.getOwnerDocument().createElement(elemName);
            Text textElem = parent.getOwnerDocument().createTextNode(elemText);
            elem.appendChild(textElem);
            parent.appendChild(elem);
            return elem;
        }

        public void setJavaItems(Element dataTypeElem)
        {
            if (javaClass != null)
            {
                Element javaClassElem = createTextElem(dataTypeElem, "java-class", javaClass);
                javaClassElem.setAttribute("package", javaClassPkg);
            }
            else
            {
                Element javaClassElem = createTextElem(dataTypeElem, "java-class", "Object");
                javaClassElem.setAttribute("package", "java.lang");
            }

            if (javaType != null)
            {
                Element javaTypeElem = createTextElem(dataTypeElem, "java-type", javaType);
                javaTypeElem.setAttribute("default", javaTypeDefault);
            }

            switch (jdbcType.intValue())
            {
                case java.sql.Types.DATE:
                case java.sql.Types.TIME:
                case java.sql.Types.TIMESTAMP:
                    switch (jdbcType.intValue())
                    {
                        case java.sql.Types.DATE:
                            createTextElem(dataTypeElem, "java-date-format-instance", "java.text.DateFormat.getDateInstance()");
                            break;

                        case java.sql.Types.TIME:
                            createTextElem(dataTypeElem, "java-date-format-instance", "java.text.DateFormat.getTimeInstance()");
                            break;

                        case java.sql.Types.TIMESTAMP:
                            createTextElem(dataTypeElem, "java-date-format-instance", "java.text.DateFormat.getDateTimeInstance()");
                            break;

                    }
                    break;
            }
        }

        /**
         * Define a <datatype> element under the given parent
         */
        public void define(Element parent, String dbmsId, Map jdbcTypeInfoMap, Map dbmdTypeInfoByName, Map dbmdTypeInfoByJdbcType)
        {
            Element dataTypeElem = parent.getOwnerDocument().createElement("datatype");
            dataTypeElem.setAttribute("name", typeName);

            createTextElem(dataTypeElem, "jdbc-type", jdbcType.toString());

            Element sqlDefnElem = null;
            Object[] dbmdTypeInfo = (Object[]) dbmdTypeInfoByJdbcType.get(jdbcType);
            if (dbmdTypeInfo != null)
            {
                // the original meta data was 1-based, but the Object[] is zero-based (be careful)
                String dmbdTypeName = dbmdTypeInfo[0] != null ? dbmdTypeInfo[0].toString() : "unknown";
                switch (jdbcType.intValue())
                {
                    case java.sql.Types.VARCHAR:
                        sqlDefnElem = sqlDefnElem = createTextElem(dataTypeElem, "sqldefn", dmbdTypeName + "(%size%)");
                        break;

                    case java.sql.Types.NUMERIC:
                        sqlDefnElem = createTextElem(dataTypeElem, "sqldefn", dmbdTypeName + "(%size%, %decimals%)");
                        break;

                    default:
                        sqlDefnElem = createTextElem(dataTypeElem, "sqldefn", dmbdTypeName);
                }
            }
            else
                sqlDefnElem = createTextElem(dataTypeElem, "sqldefn", "jdbc-type-" + jdbcType);
            sqlDefnElem.setAttribute("dbms", dbmsId);

            setJavaItems(dataTypeElem);
            parent.appendChild(dataTypeElem);
        }

        /**
         * Using the columnMetaData, assign the data type of the provided columnElem
         */
        public void assign(ResultSet columnMetaData, Map jdbcTypeInfoMap, Map dbmdTypeInfoByName, Map dbmdTypeInfoByJdbcType, Element columnElem) throws SQLException
        {
            if (typeName == null)
            {
                typeName = columnMetaData.getString(6);
                jdbcTypeInfoMap.put(typeName, this);
            }
            setAttribute(columnElem, "type", typeName);

            int size = columnMetaData.getInt(7);
            if (size > 0)
                setAttribute(columnElem, "size", Integer.toString(size));

            int decimals = columnMetaData.getInt(9);
            if (decimals > 0)
                setAttribute(columnElem, "decimals", Integer.toString(decimals));
        }
    }

    static
    {
        replaceMacrosInColumnNodes = new HashSet();
        replaceMacrosInTableNodes = new HashSet();
        replaceMacrosInIndexNodes = new HashSet();

        for (int i = 0; i < MACROSIN_COLUMNNODES.length; i++)
            replaceMacrosInColumnNodes.add(MACROSIN_COLUMNNODES[i]);
        for (int i = 0; i < MACROSIN_TABLENODES.length; i++)
            replaceMacrosInTableNodes.add(MACROSIN_TABLENODES[i]);
        for (int i = 0; i < MACROSIN_INDEXNODES.length; i++)
            replaceMacrosInIndexNodes.add(MACROSIN_INDEXNODES[i]);

        for (int i = 0; i < JAVA_RESERVED_WORDS.length; i++)
            javaReservedWords.add(JAVA_RESERVED_WORDS[i]);
        for (int i = 0; i < JAVA_RESERVED_TERMS.length; i++)
            javaReservedTerms.add(JAVA_RESERVED_TERMS[i]);

        refColumnExcludeElementsFromInherit.add(ELEMNAME_GENERATE_ID);
        dialogFieldExcludeColElemsFromInherit.add("size");
    }

    public SchemaDocument()
    {
    }

    public SchemaDocument(File file)
    {
        loadDocument(file);
    }

    public SchemaDocument(Connection conn, String catalog, String schemaPattern) throws ParserConfigurationException, SQLException
    {
        loadDocument(conn, catalog, schemaPattern);
    }

    public DataAccessLayerGenerator getDataAccessLayerGenerator(String sparxSharedJavaDalStylesheetsRootDir)
    {
        return new DataAccessLayerGenerator(sparxSharedJavaDalStylesheetsRootDir);
    }

    public boolean loadDocument(File file)
    {
        boolean status = super.loadDocument(file);
        SchemaDocFactory.contentsChanged(this);
        return status;
    }

    /**
     * Given a text string that defines a SQL table name or column name or other SQL identifier,
     * return a string that would be suitable for that string to be used as a caption or plain text.
     */
    public static String sqlIdentifierToText(String original, boolean uppercaseEachWord)
    {
        if (original == null || original.length() == 0)
            return original;

        StringBuffer text = new StringBuffer();
        text.append(Character.toUpperCase(original.charAt(0)));
        boolean wordBreak = false;
        for (int i = 1; i < original.length(); i++)
        {
            char ch = original.charAt(i);
            if (ch == '_')
            {
                text.append(' ');
                wordBreak = true;
            }
            else if (wordBreak)
            {
                text.append(uppercaseEachWord ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
                wordBreak = false;
            }
            else
                text.append(Character.toLowerCase(ch));
        }
        return text.toString();
    }

    public void overrideOrInheritAttribute(Element src, Element dest, String attribName, String defaultValue)
    {
        String overrideValue = src.getAttribute(attribName);
        if (overrideValue.length() > 0)
            dest.setAttribute(attribName, overrideValue);
        else
        {
            String originalValue = dest.getAttribute(attribName);
            if (originalValue.length() == 0)
                dest.setAttribute(attribName, defaultValue);
        }
    }

    public void overrideAttributes(Element srcElement, Element destElem)
    {
        NamedNodeMap inhAttrs = srcElement.getAttributes();
        for (int i = 0; i < inhAttrs.getLength(); i++)
        {
            Node attrNode = inhAttrs.item(i);
            destElem.setAttribute(attrNode.getNodeName(), attrNode.getNodeValue());
        }
    }

    public class TableDialogDefinition
    {
        protected Element table;
        protected Element dialogDefn;

        public TableDialogDefinition(Element table, Element dialogDefn)
        {
            this.table = table;

            String tableName = table.getAttribute("name");
            if (dialogDefn.getAttribute("name").length() == 0)
                dialogDefn.setAttribute("name", tableName);
            if (dialogDefn.getAttribute("heading").length() == 0)
                dialogDefn.setAttribute("heading", "create-data-cmd-heading:" + sqlIdentifierToText(tableName, true));

            this.dialogDefn = (Element) dialogDefn.cloneNode(true);
        }

        public Element getDialogFieldDefn()
        {
            return dialogDefn;
        }

        public Element getTable()
        {
            return table;
        }

        public String getMapKey()
        {
            return table.getAttribute("name").toUpperCase();
        }

        public Element createDialogElement(Element parent, Element tableDialogRefPlaceholder)
        {
            // if there are any attributes in the <field.table-column> tag, they all override whatever is in <field.xxx> tag inside the <column>
            overrideAttributes(tableDialogRefPlaceholder, dialogDefn);
            Element clonedDialogDefn = (Element) parent.getOwnerDocument().importNode(dialogDefn, true);
            clonedDialogDefn.setAttribute("_original-tag", tableDialogRefPlaceholder.getNodeName());
            clonedDialogDefn.setAttribute("class", "com.netspective.sparx.xif.dal.TableDialog");
            //Element clonedTableDefn = (Element) parent.getOwnerDocument().importNode(table, true);
            //clonedDialogDefn.appendChild(clonedTableDefn);
            parent.insertBefore(clonedDialogDefn, tableDialogRefPlaceholder);
            return clonedDialogDefn;
        }
    }

    public class DialogFieldDefinition
    {
        protected Element table;
        protected Element column;
        protected Element dialogFieldDefn;

        public DialogFieldDefinition(Element table, Element column, Element dialogFieldDefn)
        {
            this.table = table;
            this.column = column;

            // setup some useful defaults if they're not already defined
            final String columnName = column.getAttribute("name");
            setAttrValueDefault(dialogFieldDefn, "name", columnName);
            setAttrValueDefault(dialogFieldDefn, "caption", sqlIdentifierToText(columnName, false));

            this.dialogFieldDefn = (Element) dialogFieldDefn.cloneNode(true);

            // inherit all items common to columns and fields
            setAttrValueDefault(this.dialogFieldDefn, "required", column.getAttribute("required"));
            setAttrValueDefault(this.dialogFieldDefn, "primarykey", column.getAttribute("primarykey"));

            if(column.getAttribute("_gen-create-id").length() > 0)
                this.dialogFieldDefn.setAttribute("hidden", "yes");

            NamedNodeMap colAttrs = column.getAttributes();
            for(int a = 0; a < colAttrs.getLength(); a++)
            {
                // if there are any attributes in the <column> tag that beging with "field." then copy them without the field. prefix
                String attrName = colAttrs.item(a).getNodeName();
                if(attrName.startsWith(ATTRPREFIX_INHERITFIELD))
                    this.dialogFieldDefn.setAttribute(attrName.substring(ATTRPREFIX_INHERITFIELD.length()), colAttrs.item(a).getNodeValue());
            }

            // in a <column> tag the "size" really means "maximum length" so do the translation
            String size = findElementOrAttrValue(column, "size");
            if (size != null)
                this.dialogFieldDefn.setAttribute("max-length", size);

            if(this.dialogFieldDefn.getAttribute("hint").equals("descr"))
            {
                String description = findElementOrAttrValue(column, "descr");
                if (description != null && this.dialogFieldDefn.getAttribute("hint").equals("descr"))
                    this.dialogFieldDefn.setAttribute("hint", description);
            }

            // if we have a select field and no choices are specified, check to see if the <column> tag is a reference
            // to an enumeration table; if it is, then we probably want to make the enum's data choices for this field
            Reference refInfo = getColumnRefInfo(table, column);
            if (refInfo != null)
            {
                Element refTable = (Element) tableNodes.get(refInfo.tableName.toUpperCase());
                if (refTable.getAttribute("is-enum").equals("yes") && this.dialogFieldDefn.getAttribute("choices").length() == 0)
                    this.dialogFieldDefn.setAttribute("choices", "schema-enum:" + refInfo.tableName);
            }
        }

        public Element getColumn()
        {
            return column;
        }

        public Element getDialogFieldDefn()
        {
            return dialogFieldDefn;
        }

        public Element getTable()
        {
            return table;
        }

        public String getMapKey()
        {
            return (table.getAttribute("name") + "." + column.getAttribute("name")).toUpperCase();
        }

        public void setupDALProperties()
        {
            setAttrValueDefault(dialogFieldDefn, "dal-column-data-type-class", column.getAttribute("_gen-data-type-class"));
            setAttrValueDefault(dialogFieldDefn, "dal-column-method-name", column.getAttribute("_gen-method-name"));
            setAttrValueDefault(dialogFieldDefn, "dal-column-constant-name", column.getAttribute("_gen-constant-name"));
            setAttrValueDefault(dialogFieldDefn, "dal-table-method-name", table.getAttribute("_gen-table-method-name"));
            setAttrValueDefault(dialogFieldDefn, "dal-table-class-name", table.getAttribute("_gen-table-class-name"));
            setAttrValueDefault(dialogFieldDefn, "dal-table-row-class-name", table.getAttribute("_gen-row-class-name"));
            setAttrValueDefault(dialogFieldDefn, "dal-table-rows-class-name", table.getAttribute("_gen-rows-class-name"));
        }

        public Element resolveDialogField(Element dialogElem, Element tableColumnRefFieldPlaceholder)
        {
            // if there are any attributes in the <field.table-column> tag, they all override whatever is in <field.xxx> tag inside the <column>
            overrideAttributes(tableColumnRefFieldPlaceholder, dialogFieldDefn);
            Element clonedDialogFieldDefn = (Element) dialogElem.getOwnerDocument().importNode(dialogFieldDefn, true);
            clonedDialogFieldDefn.setAttribute("_original-tag", tableColumnRefFieldPlaceholder.getNodeName());
            //Element clonedColumnDefn = (Element) dialogElem.getOwnerDocument().importNode(column, true);
            //clonedDialogFieldDefn.appendChild(clonedColumnDefn);
            dialogElem.insertBefore(clonedDialogFieldDefn, tableColumnRefFieldPlaceholder);
            return clonedDialogFieldDefn;
        }
    }

    public TableDialogDefinition getTableDialogDefn(String tableName)
    {
        reload();
        return (TableDialogDefinition) tableDialogDefns.get(tableName.toUpperCase());
    }

    /**
     * Given a <field.table-column> element inside a <dialog> element in a DialogManager, return a suitable
     * field.xxx element that will take the place of the <field.table-column> tag. The suitable element is
     * extracted from a <column> element but could be inherited from a <datatype>.
     */
    public DialogFieldDefinition getDialogFieldDefn(String tableName, String columnName)
    {
        reload();
        return (DialogFieldDefinition) dialogFieldDefns.get((tableName + "." + columnName).toUpperCase());
    }

    public void setUrl(String url)
    {
        loadDocument(new File(url));
    }

    public Map getDataTypes()
    {
        return dataTypeNodes;
    }

    public Map getTableTypes()
    {
        return tableTypeNodes;
    }

    public Map getIndexTypes()
    {
        return indexTypeNodes;
    }

    public Map getTables()
    {
        return tableNodes;
    }

    public List getNamesOfTablesWithDialogsDefns()
    {
        reload();
        List result = new ArrayList();
        result.addAll(tableDialogDefns.keySet());
        return result;
    }

    public List getNamesOfColumnsInTableWithFieldDefns(String tableName)
    {
        reload();
        return (List) columnsWithFieldDefns.get(tableName.toUpperCase());
    }

    public String[] getTableNames(boolean includeAudit)
    {
        ArrayList tableNames = new ArrayList();

        if (includeAudit)
        {
            for (Iterator i = tableNodes.values().iterator(); i.hasNext();)
            {
                Element table = (Element) i.next();
                tableNames.add(table.getAttribute("name"));
            }
        }
        else
        {
            for (Iterator i = tableNodes.values().iterator(); i.hasNext();)
            {
                Element table = (Element) i.next();
                if (!table.getAttribute("is-audit").equals("yes"))
                    tableNames.add(table.getAttribute("name"));
            }
        }

        String[] result = new String[tableNames.size()];
        tableNames.toArray(result);
        Arrays.sort(result);
        return result;
    }

    public SelectChoicesList getEnumTableData(String tableName)
    {
        SelectChoicesList choices = (SelectChoicesList) enumTableDataChoices.get(tableName);
        if (choices != null)
            return choices;

        choices = new SelectChoicesList();

        Element tableElem = (Element) tableNodes.get(tableName.toUpperCase());
        if (tableElem == null)
        {
            choices.add(new SelectChoice("Enumeration table '" + tableName + "' not found in schema '" + this.getSourceDocument().getFile().getAbsolutePath() + "'"));
            return choices;
        }

        NodeList tableChildren = tableElem.getChildNodes();
        int tableChildrenCount = tableChildren.getLength();
        for (int c = 0; c < tableChildrenCount; c++)
        {
            Node node = tableChildren.item(c);
            if ("enum".equals(node.getNodeName()))
            {
                Element enumElem = (Element) node;
                String id = enumElem.getAttribute("id");
                String caption = enumElem.getFirstChild().getNodeValue();
                choices.add(id.length() > 0 ? new SelectChoice(caption, id) : new SelectChoice(caption));
            }
        }

        enumTableDataChoices.put(tableName, choices);
        return choices;
    }

    public DocumentFragment getCompositeColumns(Element table, Element column, Element composite)
    {
        NodeList compNodes = composite.getChildNodes();
        String compositeName = column.getAttribute("name");
        DocumentFragment compColumns = composite.getOwnerDocument().createDocumentFragment();
        for (int c = 0; c < compNodes.getLength(); c++)
        {
            Node compNode = compNodes.item(c);
            if (compNode.getNodeName().equals("column"))
            {
                Node nameAttr = ((Element) compNode).getAttributeNode("name");
                replaceNodeValue(nameAttr, "$name$", compositeName);
                fixupColumnElement(table, (Element) compNode, false);
                ((Element) compNode).setAttribute("descr", column.getAttribute("descr"));
                compColumns.appendChild(compNode);
            }
        }
        return compColumns;
    }

    public boolean isReferenceColumn(Element column)
    {
        return column.getAttribute("lookupref").length() > 0 || column.getAttribute("parentref").length() > 0 ||
                column.getAttribute("selfref").length() > 0 || column.getAttribute("usetype").length() > 0;
    }

    public void fixupColumnElement(Element table, Element column, boolean inRefColumn)
    {
        // if we're not fixing up a reference column and this column happens to be a reference, we're not going
        // to deal with it right now
        if (inRefColumn == false && isReferenceColumn(column))
            return;

        inheritNodes(column, dataTypeNodes, ATTRNAME_TYPE, inRefColumn ? refColumnExcludeElementsFromInherit : defaultExcludeElementsFromInherit);

        NodeList colInfo = column.getChildNodes();
        ArrayList sqlDefnElems = new ArrayList();
        Element dialogFieldDefn = null;
        String size = null;
        String decimals = null;
        String generateId = null;

        size = column.getAttribute("size");
        if (size != null && size.length() == 0)
            size = null;

        decimals = column.getAttribute("decimals");
        if (decimals != null && decimals.length() == 0)
            decimals = null;

        for (int i = 0; i < colInfo.getLength(); i++)
        {
            Node childNode = colInfo.item(i);
            String nodeName = childNode.getNodeName();

            if (nodeName.equals("table"))
            {
                columnTableNodes.add(childNode);
                fixupTableElement((Element) childNode);
            }
            else if (nodeName.equals("sqldefn"))
                sqlDefnElems.add(childNode);
            else if (nodeName.equals("size")) // the last size element overrides all
                size = childNode.getFirstChild().getNodeValue();
            else if (decimals == null && nodeName.equals("decimals"))
                decimals = childNode.getFirstChild().getNodeValue();
            else if (generateId == null && nodeName.equals(ELEMNAME_GENERATE_ID))
                generateId = childNode.getFirstChild().getNodeValue();
            else if (nodeName.startsWith(DialogField.FIELDTAGPREFIX))
                dialogFieldDefn = (Element) childNode;
        }

        if (size != null && sqlDefnElems.size() > 0)
        {
            for (int i = 0; i < sqlDefnElems.size(); i++)
                replaceNodeValue(((Element) sqlDefnElems.get(i)).getFirstChild(), "%size%", size);
        }
        if (decimals != null && sqlDefnElems.size() > 0)
        {
            for (int i = 0; i < sqlDefnElems.size(); i++)
                replaceNodeValue(((Element) sqlDefnElems.get(i)).getFirstChild(), "%decimals%", decimals);
        }

        String customSequence = column.getAttribute("sequence-name");
        String tableAbbrev = table.getAttribute("abbrev");

        if (0 == tableAbbrev.length()) tableAbbrev = table.getAttribute("name");

        // If we are given a custom sequence name, use it.  Otherwise generate it
        if (0 == customSequence.length()) customSequence = (tableAbbrev + "_" + column.getAttribute("name") + "_SEQ").toUpperCase();
        column.setAttribute("_gen-sequence-name", customSequence);

        if ("autoinc".equals(column.getAttribute("type")) || column.getAttribute("autoinc").equals("yes") || "autoinc".equals(generateId))
            column.setAttribute("_gen-create-id", "autoinc");

        if ("guid".equals(column.getAttribute("type")) || column.getAttribute("guid").equals("yes") || "guid".equals(generateId) ||
                "guid32".equals(column.getAttribute("type")) || column.getAttribute("guid32").equals("yes") || "guid32".equals(generateId))
            column.setAttribute("_gen-create-id", "guid32");

        if (dialogFieldDefn != null)
        {
            DialogFieldDefinition defn = new DialogFieldDefinition(table, column, dialogFieldDefn);
            dialogFieldDefns.put(defn.getMapKey(), defn);

            String tableName = table.getAttribute("name").toUpperCase();
            List tableColNames = (List) columnsWithFieldDefns.get(tableName);
            if (tableColNames == null)
            {
                tableColNames = new ArrayList();
                columnsWithFieldDefns.put(tableName, tableColNames);
            }
            tableColNames.add(column.getAttribute("name"));
        }
    }

    public String getPrimaryKey(Element table)
    {
        NodeList columns = table.getChildNodes();
        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (node.getNodeName().equals("column") && ((Element) node).getAttribute("primarykey").equals("yes"))
                return ((Element) node).getAttribute("name");
        }

        return null;
    }

    public Element getParentConnectorColumn(Element table)
    {
        NodeList columns = table.getChildNodes();
        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (node.getNodeName().equals("column") && ((Element) node).getAttribute("reftype").equals("parent"))
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
        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            if (nodeName.equals("column"))
            {
                Element column = (Element) node;
                String indexColName = column.getAttribute("name");
                if (column.getAttribute("unique").equals("yes"))
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
                else if (column.getAttribute("indexed").equals("yes"))
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
                if (column.getAttribute("indexgrp").length() > 0)
                    errors.add("The 'indexgrp' attribute is no longer supported in table '" + tableName + "' column '" + indexColName + "' (use 'index' tag instead)");
                if (column.getAttribute("uniquegrp").length() > 0)
                    errors.add("The 'uniquegrp' attribute is no longer supported in table '" + tableName + "' column '" + indexColName + "' (use 'index' tag instead)");

                String dalAccessor = column.getAttribute("java-dal-accessor");
                String columnName = column.getAttribute("name");
                if ("yes".equals(dalAccessor))
                {
                    Element accessorElem = tableDoc.createElement("java-dal-accessor");
                    accessorElem.setAttribute("name", "get" + tableName + "By" + XmlSource.xmlTextToJavaIdentifier(columnName, true));
                    accessorElem.setAttribute("columns", columnName);
                    accessorElem.setAttribute("type", "equality");
                    table.appendChild(accessorElem);
                }
                else if (dalAccessor.length() > 0)
                {
                    Element accessorElem = tableDoc.createElement("java-dal-accessor");
                    accessorElem.setAttribute("name", dalAccessor);
                    accessorElem.setAttribute("columns", columnName);
                    accessorElem.setAttribute("type", "equality");
                    table.appendChild(accessorElem);
                }
            }
            else if (nodeName.equals("index"))
            {
                Element index = (Element) node;
                inheritNodes(index, indexTypeNodes, ATTRNAME_TYPE, defaultExcludeElementsFromInherit);

                String indexName = index.getAttribute("name");
                NodeList columnElems = table.getElementsByTagName("column");
                int columnsCount = columnElems.getLength();

                String columnsList = index.getAttribute("columns");
                if (columnsList.length() > 0)
                {
                    StringTokenizer st = new StringTokenizer(index.getAttribute("columns"), ",");
                    while (st.hasMoreTokens())
                    {
                        String indexColName = st.nextToken().trim();
                        boolean found = false;
                        for (int ic = 0; ic < columnsCount; ic++)
                        {
                            Element columnElem = (Element) columnElems.item(ic);
                            if (columnElem.getAttribute("name").equals(indexColName))
                            {
                                found = true;
                                colNameElem = tableDoc.createElement("column");
                                colNameElem.setAttribute("name", indexColName);
                                index.appendChild(colNameElem);
                            }
                        }
                        if (!found)
                        {
                            errors.add("Column '" + indexColName + "' not found in index '" + indexName + "' of table '" + tableName + "'");
                        }
                    }
                }
            }
        }

        if (columnIndexes.size() > 0)
        {
            for (Iterator i = columnIndexes.iterator(); i.hasNext();)
            {
                table.appendChild((Element) i.next());
            }
        }
    }

    public DataAccessLayerProperties getDALProperties()
    {
        return dalProperties;
    }

    public Schema getSchema()
    {
        if (dalProperties.schemaInstance != null)
            return dalProperties.schemaInstance;

        try
        {
            Class cls = Class.forName(dalProperties.schemaQualifiedClassName);
            dalProperties.schemaInstance = (Schema) cls.newInstance();
            return dalProperties.schemaInstance;
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
        catch (InstantiationException e)
        {
            return null;
        }
        catch (IllegalAccessException e)
        {
            return null;
        }
    }

    public void fixupTableElement(Element table)
    {
        Hashtable params = new Hashtable();
        String tableName = table.getAttribute("name");
        tableParams.put(tableName, params);
        inheritNodes(table, tableTypeNodes, ATTRNAME_TYPE, defaultExcludeElementsFromInherit);

        NodeList columns = table.getChildNodes();
        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) node;
            String nodeName = node.getNodeName();

            if (nodeName.equals("column"))
                fixupColumnElement(table, childElem, false);
            else if (nodeName.equals("param"))
                params.put(childElem.getAttribute("name"), childElem.getFirstChild().getNodeValue());
        }

        Node[][] replaceCols = new Node[columns.getLength()][];
        int compIndex = 0;

        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeName().equals("column"))
            {
                NodeList composites = ((Element) node).getElementsByTagName("composite");
                for (int cc = 0; cc < composites.getLength(); cc++)
                {
                    DocumentFragment compCols = getCompositeColumns(table, (Element) node, (Element) composites.item(cc));
                    replaceCols[compIndex] = new Node[]{node, compCols};
                    compIndex++;
                }
            }
        }

        for (int j = 0; j < compIndex; j++)
        {
            table.insertBefore(replaceCols[j][1], replaceCols[j][0]);
            table.removeChild(replaceCols[j][0]);
        }

        params.put("tbl_name", table.getAttribute("name"));
        params.put("tbl_Name", ucfirst(table.getAttribute("name")));
        params.put("tbl_abbrev", table.getAttribute("abbrev"));

        Element tableParentElem = (Element) table.getParentNode();
        if (tableParentElem != null && tableParentElem.getNodeName().equals("column"))
        {
            Element parentColumn = tableParentElem;
            Element parentColTable = (Element) parentColumn.getParentNode();

            params.put("parenttbl_name", parentColTable.getAttribute("name"));
            params.put("parenttbl_Name", ucfirst(parentColTable.getAttribute("name")));
            params.put("parenttbl_abbrev", parentColTable.getAttribute("abbrev"));

            String primaryKey = getPrimaryKey(parentColTable);
            if (primaryKey != null)
                params.put("parenttbl_prikey", primaryKey);

            params.put("parentcol_name", parentColumn.getAttribute("name"));
            params.put("parentcol_Name", ucfirst(parentColumn.getAttribute("name")));
            params.put("parentcol_abbrev", parentColumn.getAttribute("abbrev"));
            params.put("parentcol_short", parentColumn.getAttribute("abbrev"));
            params.put("parentcol_Short", parentColumn.getAttribute("abbrev"));
        }

        replaceNodeMacros(table, replaceMacrosInTableNodes, params);

        boolean isEnum = false;
        boolean isLookup = false;

        Element lastColumnSeen = null;
        int lastEnumId = 0;
        columns = table.getChildNodes();
        for (int c = 0; c < columns.getLength(); c++)
        {
            Node node = columns.item(c);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            if (nodeName.equals("column"))
            {
                replaceNodeMacros(node, replaceMacrosInColumnNodes, params);
                lastColumnSeen = (Element) node;
            }
            else if (nodeName.equals("index"))
                replaceNodeMacros(node, replaceMacrosInTableNodes, params);
            else if (nodeName.equals("parent"))
            {
                if (table.getAttribute("parent").length() == 0)
                    table.setAttribute("parent", node.getFirstChild().getNodeValue());
            }
            else if (nodeName.equals("extends"))
            {
                String tableType = node.getFirstChild().getNodeValue();
                if (tableType.equals("Audit"))
                    table.setAttribute("audit", "yes");
                else if (tableType.equals("Enumeration"))
                    isEnum = true;
                else if (tableType.equals("Lookup"))
                    isLookup = true;
            }
            else if (nodeName.equals("enum"))
            {
                Element enumElem = (Element) node;
                String constant_name = enumElem.getAttribute("java-constant");
                if (constant_name != null && constant_name.length() > 0)
                    enumElem.setAttribute("java-constant-name", constant_name);
                else
                    enumElem.setAttribute("java-constant-name", xmlTextToJavaConstant(enumElem.getFirstChild().getNodeValue()));
                String enumId = enumElem.getAttribute("id");
                if (enumId.length() == 0)
                {
                    enumElem.setAttribute("id", Integer.toString(lastEnumId));
                    lastEnumId++;
                }
                else
                {
                    try
                    {
                        lastEnumId = Integer.parseInt(enumId) + 1;
                    }
                    catch (NumberFormatException e)
                    {
                        addError("Enum id '" + enumId + "' in table '" + tableName + "' is invalid.");
                    }
                }
            }
            else if (nodeName.equals("dialog"))
            {
                TableDialogDefinition tableDialogDefinition = new TableDialogDefinition(table, (Element) node);
                tableDialogDefns.put(tableDialogDefinition.getMapKey(), tableDialogDefinition);
            }
        }

        if (lastColumnSeen != null) lastColumnSeen.setAttribute("is-last", "yes");
        if (table.getAttribute("abbrev").length() == 0)
            table.setAttribute("abbrev", tableName);

        if (isEnum && !isLookup)
            table.setAttribute("is-enum", "yes");
        else if (isLookup)
            table.setAttribute("is-lookup", "yes");

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
            if (st.hasMoreTokens())
                columnName = st.nextToken();
            else
                columnName = "id";
        }
    }

    public Reference getRefInfo(Element elem, String attrName, int refType)
    {
        String attrValue = elem.getAttribute(attrName);
        if (attrValue.length() == 0)
            return null;
        else
            return new Reference(attrValue, refType);
    }

    public Reference getColumnRefInfo(Element tableElem, Element column)
    {
        Reference refInfo = getRefInfo(column, "lookupref", REFTYPE_LOOKUP);
        if (refInfo == null)
        {
            refInfo = getRefInfo(column, "parentref", REFTYPE_PARENT);
            if (refInfo != null)
                tableElem.setAttribute("parent", refInfo.tableName);
            else
            {
                refInfo = getRefInfo(column, "selfref", REFTYPE_SELF);
                if (refInfo != null)
                    refInfo.tableName = tableElem.getAttribute("name");
                else
                    refInfo = getRefInfo(column, "usetype", REFTYPE_USETYPE);
            }
        }

        return refInfo;
    }

    public void resolveColumnReferences(Element tableElem, Element column)
    {
        // if references are already resolved, do nothing
        if (column.getAttribute("reftype").length() > 0)
            return;

        Reference refInfo = getColumnRefInfo(tableElem, column);
        if (refInfo == null)
            return;

        Element refTable = (Element) tableNodes.get(refInfo.tableName.toUpperCase());
        if (refTable == null)
        {
            errors.add("Table '" + refInfo.tableName + "' not found for " + REFTYPE_NAMES[refInfo.type] + " reference '" + refInfo.reference + "' (in table '" + tableElem.getAttribute("name") + "' column '" + column.getAttribute("name") + "')");
            return;
        }

        column.setAttribute("reftype", REFTYPE_NAMES[refInfo.type]);
        column.setAttribute("reftbl", refTable.getAttribute("name"));

        // try and find the column that matches our reference's name
        NodeList refTableColumns = refTable.getChildNodes();
        for (int rc = 0; rc < refTableColumns.getLength(); rc++)
        {
            Node refTableColumnNode = refTableColumns.item(rc);
            if (refTableColumnNode.getNodeName().equals("column"))
            {
                Element refColumnElem = (Element) refTableColumnNode;
                String refColumnName = refColumnElem.getAttribute("name");
                if (refColumnName.equalsIgnoreCase(refInfo.columnName))
                {
                    // if the column we're pointing is a reference too, then resolve it first
                    resolveColumnReferences(refTable, refColumnElem);

                    column.setAttribute("refcol", refColumnName);

                    String copyType = findElementOrAttrValue(refColumnElem, "copytype");
                    if (copyType != null && copyType.length() > 0)
                        column.setAttribute("type", copyType);
                    else
                        column.setAttribute("type", refColumnElem.getAttribute("type"));

                    if (column.getAttribute("type").length() == 0)
                        errors.add("Column '" + refInfo.columnName + "' has no type in Table '" + refInfo.tableName + "' for " + REFTYPE_NAMES[refInfo.type] + " reference '" + refInfo.reference + "' (in table '" + tableElem.getAttribute("name") + "' column '" + column.getAttribute("name") + "')");

                    fixupColumnElement(tableElem, column, true);

                    Element refByElem = xmlDoc.createElement("referenced-by");
                    refColumnElem.appendChild(refByElem);
                    refByElem.setAttribute("type", REFTYPE_NAMES[refInfo.type]);
                    refByElem.setAttribute("table", tableElem.getAttribute("name"));
                    refByElem.setAttribute("column", column.getAttribute("name"));

                    // we found our colum in the ref table, no need to do anything else
                    return;
                }
            }
        }

        // if we get here, it means our column was not found
        errors.add("Column '" + refInfo.columnName + "' not found in Table '" + refInfo.tableName + "' for " + REFTYPE_NAMES[refInfo.type] + " reference '" + refInfo.reference + "' (in table '" + tableElem.getAttribute("name") + "' column '" + column.getAttribute("name") + "')");
    }

    public void resolveTableReferences(Element tableElem)
    {
        NodeList children = tableElem.getChildNodes();

        for (int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if (node.getNodeName().equals("column"))
            {
                Element column = (Element) node;
                resolveColumnReferences(tableElem, column);
            }
        }
    }

    public void resolveReferences()
    {
        Iterator i = tableNodes.values().iterator();
        while (i.hasNext())
        {
            Element tableElem = (Element) i.next();
            resolveTableReferences(tableElem);
        }
    }

    public void createAuditTables()
    {
        Element[] auditTables = new Element[tableNodes.size()];
        int auditTablesCount = 0;

        Element docElem = xmlDoc.getDocumentElement();
        Iterator i = tableNodes.values().iterator();
        while (i.hasNext())
        {
            Element mainTable = (Element) i.next();
            if (mainTable.getAttribute("audit").equals("yes"))
            {
                String mainTableName = mainTable.getAttribute("name");
                Element auditTable = xmlDoc.createElement("table");
                auditTable.setAttribute("name", mainTableName + "_AUD");
                auditTable.setAttribute("parent", mainTableName);
                auditTable.setAttribute("is-audit", "yes");

                NodeList copyColumns = mainTable.getChildNodes();
                for (int n = 0; n < copyColumns.getLength(); n++)
                {
                    Node node = copyColumns.item(n);
                    if (!node.getNodeName().equals("column"))
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

        for (int t = 0; t < auditTablesCount; t++)
        {
            Element auditTable = auditTables[t];
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
        if (connector != null)
        {
            tableStruct.setAttribute("parent-col", connector.getAttribute("refcol"));
            tableStruct.setAttribute("child-col", connector.getAttribute("name"));
        }

        Iterator i = tableNodes.values().iterator();
        while (i.hasNext())
        {
            Element childTable = (Element) i.next();
            if (childTable.getAttribute("parent").equals(tableName))
            {
                addTableStructure(tableStruct, childTable, level + 1);

                Element childTableElem = xmlDoc.createElement("child-table");
                childTableElem.setAttribute("name", childTable.getAttribute("name"));
                connector = getParentConnectorColumn(childTable);
                if (connector != null)
                {
                    childTableElem.setAttribute("parent-col", connector.getAttribute("refcol"));
                    childTableElem.setAttribute("child-col", connector.getAttribute("name"));
                }
                table.appendChild(childTableElem);
            }
        }
    }

    public void createStructure()
    {
        Element structure = xmlDoc.createElement("table-structure");
        xmlDoc.getDocumentElement().appendChild(structure);

        Iterator i = tableNodes.values().iterator();
        while (i.hasNext())
        {
            Element table = (Element) i.next();
            if (table.getAttribute("parent").length() == 0)
            {
                addTableStructure(structure, table, 0);
            }
        }
    }

    public void keepOnlyLastElement(Element parent, String childName)
    {
        NodeList elems = parent.getElementsByTagName(childName);
        if (elems.getLength() > 1)
        {
            for (int i = 0; i < elems.getLength() - 1; i++)
                parent.removeChild(elems.item(i));
        }
    }

    public void keepOnlyUniqueValidationRules(Element dataType)
    {
        NodeList validationRules = dataType.getElementsByTagName("validation");
        Map elemHash = new HashMap();

        if (0 < validationRules.getLength())
        {
            //Node validation = validationRules.item(0).cloneNode(false);
            for (int i = 0; i < validationRules.getLength(); i++)
            {
                Element parent = (Element) validationRules.item(i);
                NodeList rules = parent.getElementsByTagName("rule");

                for (int j = 0; j < rules.getLength(); j++)
                {
                    Element elem = (Element) rules.item(j);
                    String ruleContents = getRuleContents(elem);
                    elem.setAttribute("rule-contents", ruleContents);

                    if (elemHash.containsKey(ruleContents))
                    {
                        // This means that this node is a duplicate of one already checked, so replace the earlier
                        // node in the hash with this one and delete that older one from the DOM.
                        Node oldNode = (Node) elemHash.put(ruleContents, rules.item(j));
                        Node nodeParent = oldNode.getParentNode();
                        nodeParent.removeChild(oldNode);
                    }
                    else
                    {
                        // This means that this node is not present in the element Hash.  It needs to be there for
                        // dupe checking with other (later) nodes
                        elemHash.put(ruleContents, rules.item(j));
                    }
                }
            }
        }
    }

    private String getRuleContents(Node ruleNode)
    {
        Element elem = (Element) ruleNode;
        String ruleContents = elem.getNodeName() + "[";

        ruleContents += "name=" + elem.getAttribute("name");
        ruleContents += ":type=" + elem.getAttribute("type");

        ruleContents += "]";

        return ruleContents;
    }

    public void doDataTypeInheritance()
    {
        for (Iterator i = dataTypeNodes.values().iterator(); i.hasNext();)
        {
            Element elem = (Element) i.next();
            inheritNodes(elem, dataTypeNodes, ATTRNAME_TYPE, defaultExcludeElementsFromInherit);
        }

        for (Iterator i = dataTypeNodes.values().iterator(); i.hasNext();)
        {
            Element dataType = (Element) i.next();
            keepOnlyLastElement(dataType, "size");
            keepOnlyLastElement(dataType, "default");
            keepOnlyLastElement(dataType, "java-class");
            keepOnlyLastElement(dataType, "java-type");
            keepOnlyLastElement(dataType, "java-default");

            keepOnlyUniqueValidationRules(dataType);
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
        tableDialogDefns.clear();
        dialogFieldDefns.clear();
        columnsWithFieldDefns.clear();
        dalProperties = new DataAccessLayerProperties();

        if (xmlDoc == null)
            return;

        Element dalGeneratorElement = null;
        NodeList children = xmlDoc.getDocumentElement().getChildNodes();
        for (int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element element = (Element) node;
            String nodeName = node.getNodeName();

            if (nodeName.equals("datatype"))
            {
                dataTypeNodes.put(element.getAttribute("name"), node);

                // sqlWriteFmt was used in the perl version, but JDBC doesn't
                // require it so we'll remove the sqlWriteFmt to help save space
                NodeList sqlWriteFmt = ((Element) node).getElementsByTagName("sqlwritefmt");
                if (sqlWriteFmt.getLength() > 0)
                    node.removeChild(sqlWriteFmt.item(0));
            }
            else if (nodeName.equals("tabletype"))
                tableTypeNodes.put(element.getAttribute("name"), node);
            else if (nodeName.equals("indextype"))
                indexTypeNodes.put(element.getAttribute("name"), node);
            else if (nodeName.equals("table"))
                tableNodes.put(element.getAttribute("name").toUpperCase(), node);
            else if (nodeName.equals(ELEMNAME_DAL_GENERATOR))
                dalGeneratorElement = element;
        }

        Iterator i = tableNodes.values().iterator();
        while (i.hasNext())
            fixupTableElement((Element) i.next());

        /*
		 * at this time, all the inheritance and macro replacements should
		 * be complete, so we go ahead an "pull out" the tables that should
		 * be auto-generated (from columns) and resolve all references
		 */

        Node rootNode = xmlDoc.getDocumentElement();
        i = columnTableNodes.iterator();
        while (i.hasNext())
        {
            Element columnTableElem = (Element) i.next();
            tableNodes.put(columnTableElem.getAttribute("name").toUpperCase(), columnTableElem);
            rootNode.appendChild(columnTableElem);
        }

        resolveReferences();
        createAuditTables();
        createStructure();

        // we do this at the very last so that duplicate inheritance doesn't occur in columns -- also,
        // we want to be sure to do it so that data types that are generating Java can be "extended"
        doDataTypeInheritance();

        // setup all the DAL properties
        dalProperties.setGeneratorElement(dalGeneratorElement);

        addMetaInformation();
    }

    /*------------------------------------------------------------------------*/

    static public void setAttribute(Element elem, String name, String value)
    {
        if (value != null && value.length() > 0)
            elem.setAttribute(name, value);
    }

    public Map prepareJdbcTypeInfoMap()
    {
        Map jdbcTypeInfoMap = new HashMap();
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.VARCHAR), new JdbcDataType("text", java.sql.Types.VARCHAR, "java.lang", "String"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.BIGINT), new JdbcDataType("bigint", java.sql.Types.BIGINT, "long", "0", "java.lang", "Long"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.INTEGER), new JdbcDataType("integer", java.sql.Types.INTEGER, "int", "0", "java.lang", "Integer"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.DECIMAL), new JdbcDataType("decimal", java.sql.Types.DECIMAL, "float", "0.0", "java.lang", "Float"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.FLOAT), new JdbcDataType("float", java.sql.Types.FLOAT, "float", "0.0", "java.lang", "Float"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.DOUBLE), new JdbcDataType("double", java.sql.Types.DOUBLE, "double", "0.0", "java.lang", "Double"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.DATE), new JdbcDataType("date", java.sql.Types.DATE, "java.util", "Date"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.TIME), new JdbcDataType("time", java.sql.Types.TIME, "java.util", "Date"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.TIMESTAMP), new JdbcDataType("timestamp", java.sql.Types.TIMESTAMP, "java.util", "Date"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.BIT), new JdbcDataType("bit", java.sql.Types.BIT, "boolean", "false", "java.lang", "Boolean"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.NUMERIC), new JdbcDataType("numeric", java.sql.Types.NUMERIC, "0.0", "double", "java.lang", "Double"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.REAL), new JdbcDataType("real", java.sql.Types.REAL, "double", "0.0", "java.lang", "Double"));
        jdbcTypeInfoMap.put(new Integer(java.sql.Types.TINYINT), new JdbcDataType("tinyint", java.sql.Types.TINYINT, "0", "short", "java.lang", "Short"));

        return jdbcTypeInfoMap;
    }

    /* make the table name title cased (cap each letter after _) */
    public String fixupTableNameCase(String tableNameOrig)
    {
        StringBuffer tableNameBuf = new StringBuffer(tableNameOrig.toLowerCase());
        boolean capNext = false;
        for (int i = 0; i < tableNameBuf.length(); i++)
        {
            if (tableNameBuf.charAt(i) == '_')
                capNext = true;
            else
            {
                if (i == 0 || capNext)
                {
                    tableNameBuf.setCharAt(i, Character.toUpperCase(tableNameBuf.charAt(i)));
                    capNext = false;
                }
            }
        }
        return tableNameBuf.toString();
    }

    /**
     * Generate an XML document from an existing connection (reverse-engineer from an existing
     * database using the JDBC MetaData class)
     */
    public void loadDocument(Connection conn, String catalog, String schemaPattern) throws ParserConfigurationException, SQLException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        xmlDoc = parser.newDocument();

        Element root = xmlDoc.createElement("schema");
        root.setAttribute("name", "generated");
        root.setAttribute("catalog", catalog);
        root.setAttribute("schema", schemaPattern);
        xmlDoc.appendChild(root);

        Map dataTypesMap = prepareJdbcTypeInfoMap();
        DatabaseMetaData dbmd = conn.getMetaData();

        root.setAttribute("driver", dbmd.getDriverName());
        root.setAttribute("driver-version", dbmd.getDriverVersion());
        root.setAttribute("product", dbmd.getDatabaseProductName());
        root.setAttribute("product-version", dbmd.getDatabaseProductVersion());

        Map dbmdTypeInfoByName = new HashMap();
        Map dbmdTypeInfoByJdbcType = new HashMap();
        ResultSet typesRS = dbmd.getTypeInfo();
        while (typesRS.next())
        {
            int colCount = typesRS.getMetaData().getColumnCount();
            Object[] typeInfo = new Object[colCount];
            for (int i = 1; i <= colCount; i++)
                typeInfo[i - 1] = typesRS.getObject(i);
            dbmdTypeInfoByName.put(typesRS.getString(1), typeInfo);
            dbmdTypeInfoByJdbcType.put(new Integer(typesRS.getInt(2)), typeInfo);
        }
        typesRS.close();

        ResultSet tables = dbmd.getTables(catalog, schemaPattern, null, new String[]{"TABLE"});
        while (tables.next())
        {
            String tableNameOrig = tables.getString(3);
            String tableName = fixupTableNameCase(tableNameOrig);
            Element table = xmlDoc.createElement("table");
            table.setAttribute("name", tableName);
            root.appendChild(table);

            Map primaryKeys = new HashMap();
            try
            {
                ResultSet pkRS = dbmd.getPrimaryKeys(null, null, tableNameOrig);
                while (pkRS.next())
                {
                    primaryKeys.put(pkRS.getString(4), pkRS.getString(5));
                }
                pkRS.close();
            }
            catch (Exception e)
            {
                // driver may not support this function
            }

            Map fKeys = new HashMap();
            try
            {
                ResultSet fkRS = dbmd.getImportedKeys(null, null, tableNameOrig);
                while (fkRS.next())
                {
                    fKeys.put(fkRS.getString(8), fixupTableNameCase(fkRS.getString(3)) + "." + fkRS.getString(4).toLowerCase());
                }
                fkRS.close();
            }
            catch (Exception e)
            {
                // driver may not support this function
            }

            // we keep track of processed columns so we don't duplicate them in the XML
            Set processedColsMap = new HashSet();
            ResultSet columns = dbmd.getColumns(null, null, tableNameOrig, null);
            while (columns.next())
            {
                String columnNameOrig = columns.getString(4);
                if (processedColsMap.contains(columnNameOrig))
                    continue;
                processedColsMap.add(columnNameOrig);

                String columnName = columnNameOrig.toLowerCase();
                Element column = xmlDoc.createElement("column");
                try
                {
                    setAttribute(column, "name", columnName);
                    if (primaryKeys.containsKey(columnNameOrig))
                        setAttribute(column, "primarykey", "yes");

                    if (fKeys.containsKey(columnNameOrig))
                        setAttribute(column, "lookupref", (String) fKeys.get(columnNameOrig));
                    else
                    {
                        short jdbcType = columns.getShort(5);
                        JdbcDataType dataType = (JdbcDataType) dataTypesMap.get(new Integer(jdbcType));
                        if (dataType == null) dataType = new JdbcDataType(jdbcType);
                        dataType.assign(columns, dataTypesMap, dbmdTypeInfoByName, dbmdTypeInfoByJdbcType, column);
                    }

                    setAttribute(column, "default", columns.getString(13));
                    setAttribute(column, "descr", columns.getString(12));

                    if (columns.getString(18).equals("NO"))
                        setAttribute(column, "required", "yes");
                }
                catch (Exception e)
                {
                }

                table.appendChild(column);
            }
            columns.close();
        }
        tables.close();

        Iterator dataTypeValues = dataTypesMap.values().iterator();
        while (dataTypeValues.hasNext())
        {
            JdbcDataType jdt = (JdbcDataType) dataTypeValues.next();
            jdt.define(root, "generated", dataTypesMap, dbmdTypeInfoByName, dbmdTypeInfoByJdbcType);
        }
    }

    static public class SqlDdlGenerator
    {
        private File ddlGeneratorStyleSheet;
        private String destFile;

        public SqlDdlGenerator(File styleSheet, String destFile)
        {
            setDdlGeneratorStyleSheet(styleSheet);
            setDestFile(destFile);
        }

        public File getDdlGeneratorStyleSheet()
        {
            return ddlGeneratorStyleSheet;
        }

        public void setDdlGeneratorStyleSheet(File ddlGeneratorStyleSheet)
        {
            this.ddlGeneratorStyleSheet = ddlGeneratorStyleSheet;
        }

        public String getDestFile()
        {
            return destFile;
        }

        public void setDestFile(String destFile)
        {
            this.destFile = destFile;
        }

        public void generate(SchemaDocument schemaDoc) throws TransformerConfigurationException, TransformerException
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource(ddlGeneratorStyleSheet));

            transformer.transform
                    (new javax.xml.transform.dom.DOMSource(schemaDoc.getDocument()),
                            new javax.xml.transform.stream.StreamResult(destFile));
        }
    }

    public class DataAccessLayerProperties
    {
        private Element generatorElement;
        private String schemaQualifiedClassName;
        private Schema schemaInstance;

        private String dataTypesPkg;
        private String tableTypesPkg;
        private String tablesPkg;
        private String domainsPkg;
        private String listenersPkg;
        private String rowsPkg;
        private String rowsListPkg;
        private String schemaPkg;
        private String schemaClassName;

        private Map dataTypesClassMap = new HashMap();
        private Map tableTypesClassMap = new HashMap();
        private Map domainClassMap = new HashMap();
        private Map rowClassMap = new HashMap();
        private Map rowsClassMap = new HashMap();
        private Map tableClassMap = new HashMap();

        public DataAccessLayerProperties()
        {
        }

        public String getDataTypesPkg()
        {
            return dataTypesPkg;
        }

        public void setDataTypesPkg(String dataTypesPkg)
        {
            this.dataTypesPkg = dataTypesPkg;
        }

        public String getTableTypesPkg()
        {
            return tableTypesPkg;
        }

        public void setTableTypesPkg(String tableTypesPkg)
        {
            this.tableTypesPkg = tableTypesPkg;
        }

        public String getTablesPkg()
        {
            return tablesPkg;
        }

        public void setTablesPkg(String tablesPkg)
        {
            this.tablesPkg = tablesPkg;
        }

        public String getDomainsPkg()
        {
            return domainsPkg;
        }

        public void setDomainsPkg(String domainsPkg)
        {
            this.domainsPkg = domainsPkg;
        }

        public String getListenersPkg()
        {
            return listenersPkg;
        }

        public void setListenersPkg(String listenersPkg)
        {
            this.listenersPkg = listenersPkg;
        }

        public String getRowsPkg()
        {
            return rowsPkg;
        }

        public void setRowsPkg(String rowsPkg)
        {
            this.rowsPkg = rowsPkg;
        }

        public String getRowsListPkg()
        {
            return rowsListPkg;
        }

        public void setRowsListPkg(String rowsListPkg)
        {
            this.rowsListPkg = rowsListPkg;
        }

        public String getSchemaPkg()
        {
            return schemaPkg;
        }

        public void setSchemaPkg(String schemaPkg)
        {
            this.schemaPkg = schemaPkg;
        }

        public String getSchemaClassName()
        {
            return schemaClassName;
        }

        public void setSchemaClassName(String schemaClassName)
        {
            this.schemaClassName = schemaClassName;
        }

        public String getSchemaQualifiedClassName()
        {
            return schemaQualifiedClassName;
        }

        public void setGeneratorElement(Element value)
        {
            generatorElement = value;

            if (generatorElement == null)
            {
                generatorElement = xmlDoc.createElement(ELEMNAME_DAL_GENERATOR);
                xmlDoc.getDocumentElement().appendChild(generatorElement);
            }

            NodeList dpnl = generatorElement.getElementsByTagName("destination-path");
            if (dpnl.getLength() == 0)
            {
                Element destRoot = xmlDoc.createElement("destination-path");
                destRoot.appendChild(xmlDoc.createTextNode(new File(docSource.getFile().getParentFile(), "java").getAbsolutePath()));
                generatorElement.appendChild(destRoot);
            }

            Element activeElem = getOrCreateElement(generatorElement, "data-types");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.column");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/data-type-generator.xsl");
            dataTypesPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "table-types");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.table.type");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/table-type-generator.xsl");
            tableTypesPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "tables");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.table");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/table-generator.xsl");
            tablesPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "domains");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.domain");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/domain-generator.xsl");
            domainsPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "listeners");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.listener");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/listener-generator.xsl");
            listenersPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "rows");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.domain.row");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/row-generator.xsl");
            rowsPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "row-lists");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal.domain.rows");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/rows-generator.xsl");
            rowsListPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);

            activeElem = getOrCreateElement(generatorElement, "schema");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_PACKAGE, "app.dal");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/schema-generator.xsl");
            setAttrValueDefault(activeElem, "class", "DataAccessLayer");
            schemaPkg = activeElem.getAttribute(ATTRNAME_DAL_PACKAGE);
            schemaClassName = activeElem.getAttribute("class");

            schemaQualifiedClassName = schemaPkg + "." + schemaClassName;

            activeElem = getOrCreateElement(generatorElement, "xsd");
            setAttrValueDefault(activeElem, ATTRNAME_DAL_STYLESHEET, "${sparx.shared.dal.stylesheet.root.dir}/xsd-generator.xsl");

            Iterator i = tableNodes.values().iterator();
            while (i.hasNext())
                prepareTableElements((Element) i.next());

            setupDataTypesProperties();
            setupTableTypesProperties();
            setupTablesProperties();

            i = dialogFieldDefns.values().iterator();
            while(i.hasNext())
               ((DialogFieldDefinition) i.next()).setupDALProperties();
        }

        public void prepareTableElements(Element table)
        {
            String tableName = table.getAttribute("name");
            Document tableDoc = table.getOwnerDocument();

            NodeList columns = table.getChildNodes();
            Element colNameElem = null;
            for (int c = 0; c < columns.getLength(); c++)
            {
                Node node = columns.item(c);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                String nodeName = node.getNodeName();
                if (nodeName.equals("column"))
                {
                    Element columnElem = (Element) node;
                    NodeList javaClassElems = columnElem.getElementsByTagName("java-class");
                    if (javaClassElems.getLength() == 0)
                    {
                        String dataTypeName = columnElem.getAttribute("type");
                        errors.add("Column '" + columnElem.getAttribute("name") + "' (type '" + dataTypeName + "') in table '" + tableName + "' has no DAL java-class specification");
                        Element dataTypeElem = (Element) dataTypeNodes.get(dataTypeName);
                        if (dataTypeElem != null)
                            errors.add("Type '" + dataTypeName + "' has " + dataTypeElem.getElementsByTagName("java-class").getLength() + " DAL java-class elements");
                        else
                            errors.add("Type '" + dataTypeName + "' not found.");
                    }
                    else if (javaClassElems.getLength() > 1)
                        errors.add("Column '" + columnElem.getAttribute("name") + "' (type '" + columnElem.getAttribute("type") + "') in table '" + tableName + "' has multiple DAL java-class specifications");
                }
                else if (nodeName.equals("java-dal-accessor"))
                {
                    Element accessor = (Element) node;

                    String methodName = accessor.getAttribute("name");
                    NodeList columnElems = table.getElementsByTagName("column");
                    int columnsCount = columnElems.getLength();

                    // if the convenient method <java-dal-accessor name="abc" columns="a,b,c"/> is used, expand to <column name="a">, etc
                    String columnsList = accessor.getAttribute("columns");
                    if (columnsList.length() > 0)
                    {
                        StringTokenizer st = new StringTokenizer(accessor.getAttribute("columns"), ",");
                        while (st.hasMoreTokens())
                        {
                            String accessorColName = st.nextToken().trim();
                            colNameElem = tableDoc.createElement("column");
                            colNameElem.setAttribute("name", accessorColName);
                            accessor.appendChild(colNameElem);
                        }
                    }

                    // the @columns is now expanded, check that each of the accessor <column> tags actually exists in the table
                    NodeList accessorColumnElems = accessor.getElementsByTagName("column");
                    int accessorColumnsCount = accessorColumnElems.getLength();
                    for (int ac = 0; ac < accessorColumnsCount; ac++)
                    {
                        Element accessorColumnElem = (Element) accessorColumnElems.item(ac);
                        String accessorColName = accessorColumnElem.getAttribute("name");
                        boolean found = false;
                        for (int ic = 0; ic < columnsCount; ic++)
                        {
                            Element columnElem = (Element) columnElems.item(ic);
                            if (columnElem.getAttribute("name").equals(accessorColName))
                            {
                                found = true;
                                String dataType = columnElem.getAttribute("type");
                                Element dataTypeElem = (Element) dataTypeNodes.get(dataType);
                                if (dataTypeElem.getElementsByTagName("java-type").getLength() > 0)
                                {
                                    accessor.setAttribute("_gen-has-primitive-params", "yes");
                                }
                                break;
                            }
                        }
                        if (!found)
                        {
                            errors.add("Accessor '" + methodName + "' column '" + accessorColName + "' not found in table '" + tableName + "'");
                        }
                    }
                }
            }
        }

        public void setupDataTypesProperties()
        {
            Map dataTypes = getDataTypes();

            DATATYPES:
            for (Iterator i = dataTypes.values().iterator(); i.hasNext();)
            {
                Element dataTypeElem = (Element) i.next();
                String dataTypeName = XmlSource.xmlTextToJavaIdentifier(dataTypeElem.getAttribute("name"), true) + "Column";
                String javaTypeInitCap = null;
/*
                The code below stores the class name to be used when creating any elements of this data type in the
                Java code.  This class name is different from the name used when creating the Java class for the
                data type.  If one changes this value here but leaves the value for the "_gen-data-type-class-name"
                attribute intact, theoretically one can allow for the creation of custom Java classes for each datatype.
*/
                // Check to see if a dal-class child node is available...
                NodeList childNodes = dataTypeElem.getChildNodes();
                boolean hasCustomDalClass = false;
                boolean[] dalClassTaggedChildren = new boolean[childNodes.getLength()];
                int childNum = 0;
                int ruleNum = 0;

                for (childNum = 0; null != childNodes && childNum < childNodes.getLength(); childNum++)
                {
                    Node child = childNodes.item(childNum);

                    if (Node.ELEMENT_NODE != child.getNodeType())
                        continue;

                    if ("dal-class".equals(child.getNodeName()))
                    {
                        hasCustomDalClass = true;
                        dalClassTaggedChildren[childNum] = true;
                    }

                    if ("validation".equals(child.getNodeName()))
                    {
                        NodeList ruleNodes = child.getChildNodes();

                        for (int loop = 0; null != ruleNodes && loop < ruleNodes.getLength(); loop++)
                        {
                            Node rule = ruleNodes.item(loop);

                            if (Node.ELEMENT_NODE != rule.getNodeType())
                                continue;

                            String ruleName = ((Element) rule).getAttribute("name");
                            ruleName = "".equals(ruleName) ? "number-" : ruleName;
                            String ruleConstantName = ruleName;
                            String ruleIdentifierName = ruleName;
                            ruleConstantName = XmlSource.xmlTextToJavaConstant(ruleConstantName);
                            ruleIdentifierName = XmlSource.xmlTextToJavaIdentifier(ruleIdentifierName, true);

                            String ruleValue = (new Integer(ruleNum++)).toString();

                            ((Element) rule).setAttribute("_gen-java-constant-name", ruleConstantName);
                            ((Element) rule).setAttribute("_gen-java-constant-value", ruleValue);
                            ((Element) rule).setAttribute("_gen-rule-name", ruleName);
                            ((Element) rule).setAttribute("_gen-java-identifier-name", ruleIdentifierName);
                            ruleNum++;
                        }
                    }
                }

                // This is the default value of the dalClassName.  If no custom dal class nodes are found, this will
                // be used.  The default value is generated using information in the datatype itself.
                String dalClassName = dataTypesPkg + "." + dataTypeName;

                if (hasCustomDalClass)
                {
                    // If a custom class is specified, go ahead and plug in the package attribute and the classname
                    // into the dataTypesClassMap
                    for (childNum = 0; childNum < childNodes.getLength(); childNum++)
                    {
                        if (dalClassTaggedChildren[childNum])
                        {
                            Element child = (Element) childNodes.item(childNum);
                            // This means the last defined value of dal-class will be what takes effect.  Not an efficient
                            // solution, but let's see where it takes us
                            dalClassName = child.getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        }
                    }
                }

                dataTypesClassMap.put(dataTypeElem.getAttribute("name"), dalClassName);

                NodeList children = dataTypeElem.getChildNodes();
                for (int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if (child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if ("composite".equals(childName))
                    {
                        // composites will already have been "expanded" by the SchemaDocument so we don't create any classes
                        if (dataTypeElem.getElementsByTagName("composite").getLength() > 0)
                            continue DATATYPES;
                    }
                    else if ("java-type".equals(childName))
                        javaTypeInitCap = XmlSource.xmlTextToJavaIdentifier(child.getFirstChild().getNodeValue(), true);
                }

                dataTypeElem.setAttribute("_gen-data-type-name", dataTypeName);
                dataTypeElem.setAttribute("_gen-data-type-class-name", (String) dataTypesClassMap.get(dataTypeElem.getAttribute("name")));
                dataTypeElem.setAttribute("_gen-java-type-init-cap", javaTypeInitCap);
            }
        }

        public void setupTableTypesProperties()
        {
            Map tableTypes = getTableTypes();

            for (Iterator i = tableTypes.values().iterator(); i.hasNext();)
            {
                Element tableTypeElem = (Element) i.next();
                String tableTypeClassName = XmlSource.xmlTextToJavaIdentifier(tableTypeElem.getAttribute("name"), true);
                tableTypeElem.setAttribute("_gen_table_type_class_name", tableTypeClassName);

/*
                The code below stores the class name to be used when creating any elements of this data type in the
                Java code.  This class name is different from the name used when creating the Java class for the
                data type.  If one changes this value here but leaves the value for the "_gen-data-type-class-name"
                attribute intact, theoretically one can allow for the creation of custom Java classes for each datatype.
*/
                // Check to see if a dal-class child node is available...
                NodeList dalClassChildren = tableTypeElem.getChildNodes();
                boolean hasCustomDalClass = false;
                boolean[] dalClassTaggedChildren = new boolean[dalClassChildren.getLength()];
                int childNum = 0;


                for (childNum = 0; null != dalClassChildren && childNum < dalClassChildren.getLength(); childNum++)
                {
                    Node child = dalClassChildren.item(childNum);

                    if (Node.ELEMENT_NODE == child.getNodeType() && "dal-class".equals(child.getNodeName()))
                    {
                        hasCustomDalClass = true;
                        dalClassTaggedChildren[childNum] = true;
                    }
                }

                // This is the default value of the dalClassName.  If no custom dal class nodes are found, this will
                // be used.  The default value is generated using information in the datatype itself.
                String dalClassName = tableTypesPkg + "." + tableTypeClassName;

                if (hasCustomDalClass)
                {
                    // If a custom class is specified, go ahead and plug in the package attribute and the classname
                    // into the dataTypesClassMap
                    for (childNum = 0; childNum < dalClassChildren.getLength(); childNum++)
                    {
                        if (dalClassTaggedChildren[childNum])
                        {
                            Element child = (Element) dalClassChildren.item(childNum);
                            // This means the last defined value of dal-class will be what takes effect.  Not an efficient
                            // solution, but let's see where it takes us
                            dalClassName = child.getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        }
                    }
                }

                tableTypesClassMap.put(tableTypeElem.getAttribute("name"), dalClassName);

                NodeList children = tableTypeElem.getChildNodes();
                for (int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if (child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if ("column".equals(childName))
                    {
                        Element columnElem = (Element) child;
                        columnElem.setAttribute("_gen-member-name", XmlSource.xmlTextToJavaIdentifier(columnElem.getAttribute("name"), false));
                        columnElem.setAttribute("_gen-method-name", XmlSource.xmlTextToJavaIdentifier(columnElem.getAttribute("name"), true));
                        columnElem.setAttribute("_gen-data-type-class", (String) dataTypesClassMap.get(columnElem.getAttribute("type")));

                        NodeList jtnl = columnElem.getElementsByTagName("java-type");
                        if (jtnl.getLength() > 0)
                            columnElem.setAttribute("_gen-java-type-init-cap", XmlSource.xmlTextToJavaIdentifier(jtnl.item(0).getFirstChild().getNodeValue(), true));
                    }
                }
            }
        }

        public void setupTablesProperties()
        {
            Map tables = getTables();
            for (Iterator i = tables.values().iterator(); i.hasNext();)
            {
                Element tableElem = (Element) i.next();

                String tableName = tableElem.getAttribute("name");
                String tableNameAsJavaIdentfier = XmlSource.xmlTextToJavaIdentifier(tableName, true);
                String tableClassName = tableNameAsJavaIdentfier + "Table";
                String domainName = tableNameAsJavaIdentfier;
                String listenerName = tableNameAsJavaIdentfier + "Listener";
                String rowName = tableNameAsJavaIdentfier + "Row";
                String rowListName = tableNameAsJavaIdentfier + "Rows";
                StringBuffer tableTypesList = new StringBuffer();

                /* Add default entries to the domain, row, rows and table Class Maps */
                domainClassMap.put(tableName, domainsPkg + "." + domainName);
                rowClassMap.put(tableName, rowsPkg + "." + rowName);
                rowsClassMap.put(tableName, rowsListPkg + "." + rowListName);
                tableClassMap.put(tableName, tablesPkg + "." + tableClassName);

                NodeList children = tableElem.getChildNodes();
                for (int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if (child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if ("column".equals(childName))
                    {
                        Element columnElem = (Element) child;
                        String columnName = columnElem.getAttribute("name");
                        String dalColumnMemberName = columnElem.getAttribute("dal-member-name");
                        String dalColumnMethodName = columnElem.getAttribute("dal-method-name");
                        String dalColumnConstantName = columnElem.getAttribute("dal-constant-name");

                        // if we are given a member name, use it otherwise generate it and check to make sure it's not a reserved word
                        String columnMemberName = dalColumnMemberName.length() > 0 ? dalColumnMemberName : xmlTextToJavaIdentifier(columnName, false);
                        if (dalColumnMemberName.length() == 0 && (javaReservedWords.contains(columnMemberName) || javaReservedTerms.contains(columnMemberName)))
                            columnMemberName = "_" + columnMemberName;

                        // if we are given a method name, use it otherwise generate it and check to make sure it's not called "class" 'cause we shouldn't generate "getClass()"
                        String columnMethodName = dalColumnMethodName.length() > 0 ? dalColumnMethodName : xmlTextToJavaIdentifier(columnName, true);
                        if (dalColumnMethodName.length() == 0 && "class".equals(columnName))
                            columnMethodName = "_Class";

                        columnElem.setAttribute("_gen-member-name", columnMemberName);
                        columnElem.setAttribute("_gen-method-name", columnMethodName);
                        columnElem.setAttribute("_gen-constant-name", dalColumnConstantName.length() > 0 ? dalColumnConstantName : columnName.toUpperCase());
                        columnElem.setAttribute("_gen-node-name", xmlTextToNodeName(columnName));
                        columnElem.setAttribute("_gen-data-type-class", (String) dataTypesClassMap.get(columnElem.getAttribute("type")));

                        NodeList jtnl = columnElem.getElementsByTagName("java-type");
                        if (jtnl.getLength() > 0)
                            columnElem.setAttribute("_gen-java-type-init-cap", xmlTextToJavaIdentifier(jtnl.item(0).getFirstChild().getNodeValue(), true));
                    }
                    else if ("extends".equals(childName))
                    {
                        if (tableTypesList.length() > 0)
                            tableTypesList.append(", ");
                        tableTypesList.append((String) tableTypesClassMap.get(child.getFirstChild().getNodeValue()));
                    }
                    else if ("dal-domain-class".equals(childName))
                    {
                        String dalDomainClassName = ((Element) child).getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        // This overrides the default stored above
                        domainClassMap.put(tableName, dalDomainClassName);
                    }
                    else if ("dal-row-class".equals(childName))
                    {
                        String dalRowClassName = ((Element) child).getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        // This overrides the default stored above
                        rowClassMap.put(tableName, dalRowClassName);
                    }
                    else if ("dal-rows-class".equals(childName))
                    {
                        String dalRowsClassName = ((Element) child).getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        // This overrides the default stored above
                        rowsClassMap.put(tableName, dalRowsClassName);
                    }
                    else if ("dal-table-class".equals(childName))
                    {
                        String dalTableClassName = ((Element) child).getAttribute("package") + "." + child.getFirstChild().getNodeValue();
                        // This overrides the default stored above
                        tableClassMap.put(tableName, dalTableClassName);
                    }
                }

                if (tableTypesList.length() > 0)
                    tableElem.setAttribute("_implements-table-types", tableTypesList.toString());
                tableElem.setAttribute("_gen-domain-name", domainName);
                tableElem.setAttribute("_gen-domain-class-name", (String) domainClassMap.get(tableName));
                tableElem.setAttribute("_gen-domain-member-name", XmlSource.xmlTextToJavaIdentifier(tableName, false));
                tableElem.setAttribute("_gen-domain-method-name", tableNameAsJavaIdentfier);
                tableElem.setAttribute("_gen-listener-name", listenerName);
                tableElem.setAttribute("_gen-listener-class-name", listenersPkg + "." + listenerName);
                tableElem.setAttribute("_gen-listener-member-name", XmlSource.xmlTextToJavaIdentifier(tableName, false));
                tableElem.setAttribute("_gen-listener-method-name", tableNameAsJavaIdentfier);
                tableElem.setAttribute("_gen-table-name", tableClassName);
                tableElem.setAttribute("_gen-table-class-name", (String) tableClassMap.get(tableName));
                tableElem.setAttribute("_gen-table-member-name", XmlSource.xmlTextToJavaIdentifier(tableName, false));
                tableElem.setAttribute("_gen-table-method-name", tableNameAsJavaIdentfier);
                tableElem.setAttribute("_gen-row-name", rowName);
                tableElem.setAttribute("_gen-row-class-name", (String) rowClassMap.get(tableName));
                tableElem.setAttribute("_gen-rows-name", rowListName);
                tableElem.setAttribute("_gen-rows-member-name", xmlTextToJavaIdentifier(rowListName, false));
                tableElem.setAttribute("_gen-rows-class-name", (String) rowsClassMap.get(tableName));

                if (!(tablesPkg + "." + tableClassName).equals(tableClassMap.get(tableName)))
                    tableElem.setAttribute("_gen-table-orig-class-name", tablesPkg + "." + tableClassName);
            }

            for (Iterator i = tables.values().iterator(); i.hasNext();)
            {
                Element tableElem = (Element) i.next();

                NodeList children = tableElem.getChildNodes();
                for (int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if (child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if ("column".equals(childName))
                    {
                        Element columnElem = (Element) child;
                        String refTableName = columnElem.getAttribute("reftbl");
                        if (refTableName.length() > 0l)
                        {
                            Element refTableElem = (Element) getTables().get(refTableName.toUpperCase());
                            if (refTableElem != null)
                            {
                                if ("yes".equals(refTableElem.getAttribute("is-enum")))
                                    columnElem.setAttribute("_gen-ref-table-is-enum", "yes");
                                columnElem.setAttribute("_gen-ref-table-name", refTableElem.getAttribute("_gen-table-name"));
                                columnElem.setAttribute("_gen-ref-table-class-name", refTableElem.getAttribute("_gen-table-class-name"));
                                columnElem.setAttribute("_gen-ref-table-member-name", refTableElem.getAttribute("_gen-table-member-name"));
                                columnElem.setAttribute("_gen-ref-table-method-name", refTableElem.getAttribute("_gen-table-method-name"));
                            }
                        }
                    }
                    else if ("child-table".equals(childName))
                    {
                        Element childTableRefElem = (Element) child;
                        Element childTableElem = (Element) getTables().get(childTableRefElem.getAttribute("name").toUpperCase());
                        if (childTableElem != null)
                        {
                            NamedNodeMap attrs = childTableElem.getAttributes();
                            for (int a = 0; a < attrs.getLength(); a++)
                            {
                                // copy all the "generated" names/values for each child table so searches don't have
                                // to be performed later
                                Node attr = attrs.item(a);
                                String attrName = attr.getNodeName();
                                if (attrName.startsWith("_gen"))
                                    childTableRefElem.setAttribute(attrName, attr.getNodeValue());
                            }

                            Element connector = getParentConnectorColumn(childTableElem);
                            if (connector != null)
                            {
                                attrs = connector.getAttributes();
                                for (int a = 0; a < attrs.getLength(); a++)
                                {
                                    // copy all the "generated" names/values for each child column so searches don't have
                                    // to be performed later
                                    Node attr = attrs.item(a);
                                    String attrName = attr.getNodeName();
                                    if (attrName.startsWith("_gen"))
                                        childTableRefElem.setAttribute("child-col-" + attrName, attr.getNodeValue());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public class DataAccessLayerGenerator
    {
        private String sparxSharedJavaDalStylesheetsRootDir;
        private String destRoot;
        private String dataTypesGeneratorStyleSheet;
        private String tableTypesGeneratorStyleSheet;
        private String tablesGeneratorStyleSheet;
        private String domainsGeneratorStyleSheet;
        private String listenersGeneratorStyleSheet;
        private String rowsGeneratorStyleSheet;
        private String rowsListGeneratorStyleSheet;
        private String schemaGeneratorStyleSheet;
        private String xsdGeneratorStyleSheet;

        private int dataTypesGeneratedCount;
        private int tableTypesGeneratedCount;
        private int tablesGeneratedCount;

        private List messages = new ArrayList();

        public DataAccessLayerGenerator(String sparxSharedJavaDalStylesheetsRootDir)
        {
            this.sparxSharedJavaDalStylesheetsRootDir = sparxSharedJavaDalStylesheetsRootDir;

            destRoot = getTagText(dalProperties.generatorElement, "destination-path", null);
            if (destRoot != null)
            {
                File destFile = new File(destRoot);
                if (!destFile.isAbsolute())
                {
                    File srcFile = getSourceDocument().getFile();
                    destFile = new File(srcFile, destRoot);
                    destRoot = destFile.getAbsolutePath();
                }
            }

            NodeList children = dalProperties.generatorElement.getChildNodes();
            for (int n = 0; n < children.getLength(); n++)
            {
                Node node = children.item(n);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element element = (Element) node;
                String nodeName = element.getNodeName();

                if (nodeName.equals("data-types"))
                    dataTypesGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("table-types"))
                    tableTypesGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("tables"))
                    tablesGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("domains"))
                    domainsGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("listeners"))
                    listenersGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("rows"))
                    rowsGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("row-lists"))
                    rowsListGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("schema"))
                    schemaGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
                else if (nodeName.equals("xsd"))
                    xsdGeneratorStyleSheet = element.getAttribute(ATTRNAME_DAL_STYLESHEET);
            }
        }

        public List getMessages()
        {
            return messages;
        }

        public String getDestRoot()
        {
            return destRoot;
        }

        public void setDestRoot(String destRoot)
        {
            this.destRoot = destRoot;
        }

        public String getDataTypesGeneratorStyleSheet()
        {
            return dataTypesGeneratorStyleSheet;
        }

        public void setDataTypesGeneratorStyleSheet(String dataTypesGeneratorStyleSheet)
        {
            this.dataTypesGeneratorStyleSheet = dataTypesGeneratorStyleSheet;
        }

        public String getTableTypesGeneratorStyleSheet()
        {
            return tableTypesGeneratorStyleSheet;
        }

        public void setTableTypesGeneratorStyleSheet(String tableTypesGeneratorStyleSheet)
        {
            this.tableTypesGeneratorStyleSheet = tableTypesGeneratorStyleSheet;
        }

        public String getTablesGeneratorStyleSheet()
        {
            return tablesGeneratorStyleSheet;
        }

        public void setTablesGeneratorStyleSheet(String tablesGeneratorStyleSheet)
        {
            this.tablesGeneratorStyleSheet = tablesGeneratorStyleSheet;
        }

        public String getDomainsGeneratorStyleSheet()
        {
            return domainsGeneratorStyleSheet;
        }

        public void setDomainsGeneratorStyleSheet(String domainsGeneratorStyleSheet)
        {
            this.domainsGeneratorStyleSheet = domainsGeneratorStyleSheet;
        }

        public String getListenersGeneratorStyleSheet()
        {
            return listenersGeneratorStyleSheet;
        }

        public void setListenersGeneratorStyleSheet(String listenersGeneratorStyleSheet)
        {
            this.listenersGeneratorStyleSheet = listenersGeneratorStyleSheet;
        }

        public String getRowsGeneratorStyleSheet()
        {
            return rowsGeneratorStyleSheet;
        }

        public void setRowsGeneratorStyleSheet(String rowsGeneratorStyleSheet)
        {
            this.rowsGeneratorStyleSheet = rowsGeneratorStyleSheet;
        }

        public String getRowsListGeneratorStyleSheet()
        {
            return rowsListGeneratorStyleSheet;
        }

        public void setRowsListGeneratorStyleSheet(String rowsListGeneratorStyleSheet)
        {
            this.rowsListGeneratorStyleSheet = rowsListGeneratorStyleSheet;
        }

        public String getSchemaGeneratorStyleSheet()
        {
            return schemaGeneratorStyleSheet;
        }

        public String getXsdGeneratorStyleSheet()
        {
            return xsdGeneratorStyleSheet;
        }

        public void setSchemaGeneratorStyleSheet(String schemaGeneratorStyleSheet)
        {
            this.schemaGeneratorStyleSheet = schemaGeneratorStyleSheet;
        }

        public void setXsdGeneratorStyleSheet(String xsdGeneratorStyleSheet)
        {
            this.xsdGeneratorStyleSheet = xsdGeneratorStyleSheet;
        }

        public int getDataTypesGeneratedCount()
        {
            return dataTypesGeneratedCount;
        }

        public int getTableTypesGeneratedCount()
        {
            return tableTypesGeneratedCount;
        }

        public int getTablesGeneratedCount()
        {
            return tablesGeneratedCount;
        }

        public void createDataTypesClasses() throws TransformerConfigurationException, TransformerException
        {
            dataTypesGeneratedCount = 0;
            String dataTypesPkgDirName = dalProperties.dataTypesPkg.replace('.', '/');
            File dataTypesDir = new File(destRoot + "/" + dataTypesPkgDirName);
            dataTypesDir.mkdirs();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer dataTypesTransformer = tFactory.newTransformer(new StreamSource(dataTypesGeneratorStyleSheet));
            dataTypesTransformer.setParameter("package-name", dalProperties.dataTypesPkg);
            Map dataTypes = getDataTypes();

            messages.add(new String("Generating: Datatypes"));

            DATATYPES:
            for (Iterator i = dataTypes.values().iterator(); i.hasNext();)
            {
                Element dataTypeElem = (Element) i.next();
                String dataTypeName = dataTypeElem.getAttribute("_gen-data-type-name");

                // composites don't have classes, so we'll skip them
                if(dataTypeName.length() == 0)
                    continue DATATYPES;

                String dataTypeFile = dataTypesDir.getAbsolutePath() + "/" + dataTypeName + ".java";
                String javaTypeInitCap = dataTypeElem.getAttribute("_gen-java-type-init-cap");

                dataTypesTransformer.setParameter("data-type-name", dataTypeName);
                if (javaTypeInitCap != null)
                    dataTypesTransformer.setParameter("java-type-init-cap", javaTypeInitCap);

                messages.add(new String("Applying stylesheet '" + dataTypeFile + "' for dataType '" + dataTypeName + "'"));
                dataTypesTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(dataTypeElem),
                                new javax.xml.transform.stream.StreamResult(dataTypeFile));

                dataTypesGeneratedCount++;
            }
        }

        public void createTableTypesClasses() throws TransformerConfigurationException, TransformerException
        {
            String tableTypesPkgDirName = dalProperties.tableTypesPkg.replace('.', '/');
            File tableTypesDir = new File(destRoot + "/" + tableTypesPkgDirName);
            tableTypesDir.mkdirs();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer tableTypesTransformer = tFactory.newTransformer(new StreamSource(tableTypesGeneratorStyleSheet));

            Map tableTypes = getTableTypes();

            messages.add(new String("Generating: Tabletypes"));

            for (Iterator i = tableTypes.values().iterator(); i.hasNext();)
            {
                Element tableTypeElem = (Element) i.next();
                String tableTypeClassName = tableTypeElem.getAttribute("_gen_table_type_class_name");
                String tableTypeFile = tableTypesDir.getAbsolutePath() + "/" + tableTypeClassName + ".java";

                messages.add(new String("Applying stylesheet '" + tableTypeFile + "' for tableType '" + tableTypeClassName + "'"));

                tableTypesTransformer.setParameter("package-name", dalProperties.tableTypesPkg);
                tableTypesTransformer.setParameter("table-type-name", tableTypeClassName);
                tableTypesTransformer.setParameter("table-type-class-name", dalProperties.tableTypesPkg + "." + tableTypeClassName);
                tableTypesTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableTypeElem), new javax.xml.transform.stream.StreamResult(tableTypeFile));
                tableTypesGeneratedCount++;
            }
        }

        public void createTablesClasses() throws TransformerConfigurationException, TransformerException
        {
            String tablesPkgDirName = dalProperties.tablesPkg.replace('.', '/');
            File tablesDir = new File(destRoot + "/" + tablesPkgDirName);
            tablesDir.mkdirs();

            String domainsPkgDirName = dalProperties.domainsPkg.replace('.', '/');
            File domainsDir = new File(destRoot + "/" + domainsPkgDirName);
            domainsDir.mkdirs();

            String listenersPkgDirName = dalProperties.listenersPkg.replace('.', '/');
            File listenersDir = new File(destRoot + "/" + listenersPkgDirName);
            listenersDir.mkdirs();

            String rowsPkgDirName = dalProperties.rowsPkg.replace('.', '/');
            File rowsDir = new File(destRoot + "/" + rowsPkgDirName);
            rowsDir.mkdirs();

            String rowsListPkgDirName = dalProperties.rowsListPkg.replace('.', '/');
            File rowsListDir = new File(destRoot + "/" + rowsListPkgDirName);
            rowsListDir.mkdirs();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer tablesTransformer = tFactory.newTransformer(new StreamSource(tablesGeneratorStyleSheet));
            tablesTransformer.setParameter("schema-class-name", dalProperties.schemaQualifiedClassName);
            tablesTransformer.setParameter("package-name", dalProperties.tablesPkg);
            Transformer domainsTransformer = tFactory.newTransformer(new StreamSource(domainsGeneratorStyleSheet));
            domainsTransformer.setParameter("schema-class-name", dalProperties.schemaQualifiedClassName);
            domainsTransformer.setParameter("package-name", dalProperties.domainsPkg);
            Transformer listenersTransformer = tFactory.newTransformer(new StreamSource(listenersGeneratorStyleSheet));
            listenersTransformer.setParameter("schema-class-name", dalProperties.schemaQualifiedClassName);
            listenersTransformer.setParameter("package-name", dalProperties.listenersPkg);
            Transformer rowsTransformer = tFactory.newTransformer(new StreamSource(rowsGeneratorStyleSheet));
            rowsTransformer.setParameter("schema-class-name", dalProperties.schemaQualifiedClassName);
            rowsTransformer.setParameter("package-name", dalProperties.rowsPkg);
            Transformer rowsListTransformer = tFactory.newTransformer(new StreamSource(rowsListGeneratorStyleSheet));
            rowsListTransformer.setParameter("schema-class-name", dalProperties.schemaQualifiedClassName);
            rowsListTransformer.setParameter("package-name", dalProperties.rowsListPkg);

            messages.add(new String("Generating: Tables, Rows, RowLists and Domains"));

            Map tables = getTables();

            for (Iterator i = tables.values().iterator(); i.hasNext();)
            {
                Element tableElem = (Element) i.next();
                String tableName = tableElem.getAttribute("name");

                String tableClassName = tableElem.getAttribute("_gen-table-name");
                String domainName = tableElem.getAttribute("_gen-domain-name");
                String listenerName = tableElem.getAttribute("_gen-listener-name");
                String rowName = tableElem.getAttribute("_gen-row-name");
                String rowListName = tableElem.getAttribute("_gen-rows-name");

                String tableFile = tablesDir.getAbsolutePath() + "/" + tableClassName + ".java";
                String domainFile = domainsDir.getAbsolutePath() + "/" + domainName + ".java";
                String listenerFile = listenersDir.getAbsolutePath() + "/" + listenerName + ".java";
                String rowFile = rowsDir.getAbsolutePath() + "/" + rowName + ".java";
                String rowListFile = rowsListDir.getAbsolutePath() + "/" + rowListName + ".java";

                messages.add(new String("Applying stylesheet '" + tableFile + "' for table '" + tableClassName + "'"));
                tablesTransformer.setParameter("table-name", tableClassName);
                tablesTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(tableFile));
                tablesGeneratedCount++;

                messages.add(new String("Applying stylesheet '" + domainFile + "' for domain '" + domainName + "'"));
                domainsTransformer.setParameter("domain-name", domainName);
                domainsTransformer.setParameter("domain-class-name", dalProperties.domainClassMap.get(tableName));
                domainsTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(domainFile));

                messages.add(new String("Applying stylesheet '" + listenerFile + "' for listener '" + listenerName + "'"));
                listenersTransformer.setParameter("listener-name", listenerName);
                listenersTransformer.setParameter("listener-class-name", dalProperties.listenersPkg + "." + listenerName);
                listenersTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(listenerFile));

                messages.add(new String("Applying stylesheet '" + rowFile + "' for row '" + rowName + "'"));
                rowsTransformer.setParameter("row-name", rowName);
                rowsTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(rowFile));

                messages.add(new String("Applying stylesheet '" + rowListFile + "' for rowList '" + rowName + "'"));
                rowsListTransformer.setParameter("row-name", rowName);
                rowsListTransformer.setParameter("rows-name", rowListName);
                rowsListTransformer.transform
                        (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(rowListFile));
            }
        }

        public void createSchemaClass() throws TransformerConfigurationException, TransformerException
        {
            String schemaPkgDirName = dalProperties.schemaPkg.replace('.', '/');
            File schemaDir = new File(destRoot + "/" + schemaPkgDirName);
            schemaDir.mkdirs();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer schemaTransformer = tFactory.newTransformer(new StreamSource(schemaGeneratorStyleSheet));
            schemaTransformer.setParameter("package-name", dalProperties.schemaPkg);
            schemaTransformer.setParameter("class-name", dalProperties.schemaClassName);
            String schemaFile = schemaDir.getAbsolutePath() + "/" + dalProperties.schemaClassName + ".java";

            messages.add(new String("Generating '" + schemaFile + "'"));
            schemaTransformer.transform
                    (new javax.xml.transform.dom.DOMSource(getDocument()), new javax.xml.transform.stream.StreamResult(schemaFile));

            Transformer xsdTransformer = tFactory.newTransformer(new StreamSource(xsdGeneratorStyleSheet));
            String xsdFile = schemaDir.getAbsolutePath() + "/" + dalProperties.schemaClassName + ".xsd";
            messages.add(new String("Generating '" + xsdFile + "'"));
            xsdTransformer.transform
                    (new javax.xml.transform.dom.DOMSource(getDocument()), new javax.xml.transform.stream.StreamResult(xsdFile));
        }

        public String fixupFileName(String fileName)
        {
            final String replace = "${sparx.shared.dal.stylesheet.root.dir}";
            if (fileName.startsWith(replace))
                return sparxSharedJavaDalStylesheetsRootDir + fileName.substring(replace.length());
            else
            {
                File file = new File(fileName);
                if (!file.isAbsolute())
                    file = new File(getSourceDocument().getFile(), fileName);
                return file.getAbsolutePath();
            }
        }

        public void fixupFileNames()
        {
            dataTypesGeneratorStyleSheet = fixupFileName(dataTypesGeneratorStyleSheet);
            tableTypesGeneratorStyleSheet = fixupFileName(tableTypesGeneratorStyleSheet);
            tablesGeneratorStyleSheet = fixupFileName(tablesGeneratorStyleSheet);
            domainsGeneratorStyleSheet = fixupFileName(domainsGeneratorStyleSheet);
            listenersGeneratorStyleSheet = fixupFileName(listenersGeneratorStyleSheet);
            rowsGeneratorStyleSheet = fixupFileName(rowsGeneratorStyleSheet);
            rowsListGeneratorStyleSheet = fixupFileName(rowsListGeneratorStyleSheet);
            schemaGeneratorStyleSheet = fixupFileName(schemaGeneratorStyleSheet);
            xsdGeneratorStyleSheet = fixupFileName(xsdGeneratorStyleSheet);
        }

        public void generate() throws TransformerConfigurationException, TransformerException
        {
            fixupFileNames();
            new File(destRoot).mkdirs();
            messages.clear();

            createDataTypesClasses();
            createTableTypesClasses();
            createTablesClasses();
            createSchemaClass();
        }
    }

}

