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
 * $Id: SchemaImportHandler.java,v 1.6 2002-12-23 05:07:02 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal.xml;

import com.netspective.sparx.xif.dal.Row;
import com.netspective.sparx.xif.dal.Schema;
import com.netspective.sparx.xif.dal.Table;
import com.netspective.sparx.xif.dal.TableImportStatistic;
import com.netspective.sparx.xif.dal.validation.ValidationException;
import com.netspective.sparx.xif.dal.validation.result.RowValidationResult;
import org.xml.sax.*;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Handler for all tables/rows.
 */
public class SchemaImportHandler implements ContentHandler
{
    static public final String ATTRNAME_SQL_EXPR = "_sql-expr";
    static public final String ATTRNAME_STORE_ID = "ID";
    static public final String ATTRNAME_RETRIEVE_ID = "IDREF";

    static public final Set SPECIAL_ATTRIBUTES = new HashSet();

    static
    {
        SPECIAL_ATTRIBUTES.add(ATTRNAME_SQL_EXPR);
        SPECIAL_ATTRIBUTES.add(ATTRNAME_STORE_ID);
        SPECIAL_ATTRIBUTES.add(ATTRNAME_RETRIEVE_ID);
    }

    private class NodeStackEntry
    {
        private String qName;
        private int depth;
        private Row row;
        private String rowColumnName;
        private boolean isSqlExpr;
        private boolean written;
        private String storeId;

        public NodeStackEntry(String qName, int depth)
        {
            this.qName = qName;
            this.depth = depth;
        }

        public NodeStackEntry(String qName, Row row, int depth)
        {
            this(qName, depth);
            this.row = row;
        }

        public NodeStackEntry(String qName, Row row, String activeRowColName, int depth)
        {
            this(qName, row, depth);
            this.rowColumnName = activeRowColName;
        }

        public NodeStackEntry(String qName, Row row, String activeRowColName, int depth, boolean isSqlExpr)
        {
            this(qName, row, depth);
            this.rowColumnName = activeRowColName;
            this.isSqlExpr = isSqlExpr;
        }

        public void handleAttributes(Attributes attributes, boolean allowColumnAssignments) throws ParseException
        {
            if(allowColumnAssignments && row != null)
            {
                for (int i = 0; i < attributes.getLength(); i++)
                {
                    String attrName = attributes.getQName(i);
                    if (! SPECIAL_ATTRIBUTES.contains(attrName))
                    {
                        if (!row.populateDataForXmlNodeName(attrName, attributes.getValue(i), false))
                            parseContext.addError("Column '" + attrName + "' not found for attribute in table '" + row.getTable().getName() + "'");
                    }
                }
            }

            if("yes".equals(attributes.getValue(ATTRNAME_SQL_EXPR)))
                isSqlExpr = true;

            String storeId = attributes.getValue(ATTRNAME_STORE_ID);
            if(storeId != null && storeId.length() > 0)
                setStoreId(storeId);

            String idRef = attributes.getValue(ATTRNAME_RETRIEVE_ID);
            if(idRef != null)
            {
                Object id = idReferences.get(idRef);
                if(idRef != null)
                    row.populateDataForXmlNodeName(rowColumnName, id.toString(), false);
                else
                    parseContext.addError("IDREF '"+ idRef +"' not found for column '"+ rowColumnName +"' in table '"+ row.getTable().getName() +"'.");
            }
        }

        public String getQName()
        {
            return qName;
        }

        public boolean isColumnEntry()
        {
            return rowColumnName != null ? true : false;
        }

        public boolean isSqlExpression()
        {
            return isSqlExpr;
        }

        public String getStoreId()
        {
            return storeId;
        }

        public void setStoreId(String storeId)
        {
            this.storeId = storeId;
        }

        public void write() throws NamingException, SQLException
        {
            if (!written && row != null)
            {
                RowValidationResult rvResult = row.getValidationResult();

                if (rvResult.isValid())
                {
                    written = true;
                    Table table = row.getTable();
                    try
                    {
                        table.insert(parseContext.getConnectionContext(), row);
                    }
                    catch (SQLException e)
                    {
                        throw new SQLException(e.getMessage() + "\n" + row);
                    }
                    TableImportStatistic tis = parseContext.getStatistics(table);
                    tis.incSuccessfulRows();

                    if(storeId != null)
                    {
                        idReferences.put(storeId, row.getActivePrimaryKeyValue());
                        tis.addIdReference(storeId, row.getActivePrimaryKeyValue());
                    }
                }
                else
                {
                    written = false;
                    ValidationException exception = new ValidationException(rvResult);
                    parseContext.addError("<b>Validation Error!</b><br>" + exception.getHtmlMessage() + row.toString());
                }
            }
        }
    }

    private ParseContext parseContext;
    private Stack nodeStack;
    private int depth;
    private Map idReferences = new HashMap();

    public SchemaImportHandler(ParseContext pc)
    {
        this.parseContext = pc;
        this.nodeStack = new Stack();
    }

    public Map getIdReferences()
    {
        return idReferences;
    }

    public void characters(char[] buf, int start, int end) throws SAXParseException
    {
        if (nodeStack.size() == 0)
            return;

        NodeStackEntry entry = (NodeStackEntry) nodeStack.peek();
        if (!entry.isColumnEntry())
            return;

        try
        {
            String text = new String(buf, start, end);
            if (text == null || text.trim().length() == 0)
                return;
            if (entry.isSqlExpression())
                entry.row.populateSqlExprForXmlNodeName(entry.rowColumnName, text, null);
            else
                entry.row.populateDataForXmlNodeName(entry.rowColumnName, text, true);
        }
        catch (ParseException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch (ImportException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
    }

    public final String getDepthStr()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++)
            sb.append(" ");
        return sb.toString();
    }

    public void startElement(String url, String localName, String qName, Attributes attributes) throws SAXException
    {
        try
        {
            Schema schema = parseContext.getSchema();
            if (depth == 0)
            {
                // root node, do nothing
                nodeStack.push(new NodeStackEntry(qName, depth));
            }
            else if (depth == 1)
            {
                // all the primary (top level) row inserts are done as single transactions and all children of
                // top-level rows are part of the same transaction
                parseContext.getConnectionContext().beginTransaction();
                Table childTable = schema.getTableForXmlNode(qName);
                if (childTable == null)
                {
                    nodeStack.push(new NodeStackEntry(qName, depth));
                    parseContext.addError("Table '" + qName + "' not found in the schema");
                }
                else
                {
                    Row childRow = childTable.createRow();
                    NodeStackEntry newEntry = new NodeStackEntry(qName, childRow, depth);
                    newEntry.handleAttributes(attributes, true);
                    nodeStack.push(newEntry);
                }
            }
            else
            {
                NodeStackEntry entry = (NodeStackEntry) nodeStack.peek();
                if (entry.row != null)
                {
                    // if we're starting a child row, be sure to write out the active entry so that if there
                    // are relational dependencies everything will work
                    if (entry.row.isValidXmlNodeNameForChildRow(qName))
                    {
                        entry.write();
                        Row childRow = entry.row.createChildRowForXmlNodeName(qName);
                        NodeStackEntry newEntry = new NodeStackEntry(qName, childRow, depth);
                        newEntry.handleAttributes(attributes, true);
                        nodeStack.push(newEntry);
                    }
                    else
                    {
                        if (entry.row.isValidXmlNodeNameForColumn(qName))
                        {
                            NodeStackEntry newEntry = new NodeStackEntry(qName, entry.row, qName, depth);
                            newEntry.handleAttributes(attributes, false);
                            nodeStack.push(newEntry);
                        }
                        else
                        {
                            nodeStack.push(new NodeStackEntry(qName, depth));
                            parseContext.addError("Column '" + qName + "' not found in table '" + entry.row.getTable().getName() + "'");
                        }
                    }
                }
                else
                    parseContext.addError("Don't know what to do with element '" + qName + "'");
            }
        }
        catch (NamingException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch (SQLException exc)
        {
            try
            {
                parseContext.getConnectionContext().rollbackActiveTransaction();
            }
            catch (SQLException se)
            {
                throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
            }
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch (ParseException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch (ImportException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        depth++;
    }

    public void endDocument() throws SAXException
    {
    }

    public void endElement(String url, String localName, String qName) throws SAXException
    {
        try
        {
            depth--;
            if (nodeStack.size() > 0)
            {
                NodeStackEntry entry = (NodeStackEntry) nodeStack.pop();
                if (entry != null && !entry.isColumnEntry() && entry.row != null)
                    entry.write();
                if (entry.depth == 1)
                    parseContext.getConnectionContext().commitActiveTransaction();
            }
        }
        catch (NamingException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch (SQLException se)
        {
            try
            {
                parseContext.getConnectionContext().rollbackActiveTransaction();
            }
            catch (SQLException se2)
            {
                throw new SAXParseException(se2.getMessage(), parseContext.getLocator(), se2);
            }
            throw new SAXException(se);
        }
    }

    public void endPrefixMapping(String s) throws SAXException
    {
    }

    public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException
    {
    }

    public void processingInstruction(String s, String s1) throws SAXException
    {
    }

    public void setDocumentLocator(Locator locator)
    {
        parseContext.setLocator(locator);
    }

    public void skippedEntity(String s) throws SAXException
    {
    }

    public void startDocument() throws SAXException
    {
    }

    public void startPrefixMapping(String s, String s1) throws SAXException
    {
    }
}
