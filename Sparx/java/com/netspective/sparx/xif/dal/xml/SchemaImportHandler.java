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
 * $Id: SchemaImportHandler.java,v 1.1 2002-08-29 03:38:29 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal.xml;

import java.util.List;
import java.util.Stack;
import java.text.ParseException;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import com.netspective.sparx.xif.dal.Schema;
import com.netspective.sparx.xif.dal.Table;
import com.netspective.sparx.xif.dal.Row;
import com.netspective.sparx.xif.dal.TableImportStatistic;

/**
 * Handler for all tables/rows.
 */
public class SchemaImportHandler implements ContentHandler
{
    private class NodeStackEntry
    {
        private String qName;
        private int depth;
        private Row row;
        private String rowColumnName;
        private boolean written;

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

        public boolean isColumnEntry()
        {
            return rowColumnName != null ? true : false;
        }

        public void write() throws NamingException, SQLException
        {
            if(! written && row != null)
            {
                written = true;
                Table table = row.getTable();
                table.insert(parseContext.getConnectionContext(), row);
                TableImportStatistic tis = parseContext.getStatistics(table);
                tis.incSuccessfulRows();
                System.out.println("WROTE " + row.getClass().getName());
                System.out.println(row);
            }
        }
    }

    private ParseContext parseContext;
    private Stack nodeStack;
    private List errors;
    private int depth;

    public SchemaImportHandler(ParseContext pc)
    {
        this.parseContext = pc;
        this.errors = errors;
        this.nodeStack = new Stack();
    }

    public void characters(char[] buf, int start, int end) throws SAXParseException
    {
        if(nodeStack.size() == 0)
            return;

        NodeStackEntry entry = (NodeStackEntry) nodeStack.peek();
        if(! entry.isColumnEntry())
            return;

        try
        {
            String text = new String(buf, start, end);
            if (text == null || text.trim().length() == 0)
                return;
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
        for(int i = 0; i < depth; i++)
            sb.append(" ");
        return sb.toString();
    }

    public void startElement(String url, String localName, String qName, Attributes attributes) throws SAXException
    {
        try
        {
            Schema schema = parseContext.getSchema();
            if(depth == 0)
            {
                // root node, do nothing
                nodeStack.push(new NodeStackEntry(qName, depth));
            }
            else if(depth == 1)
            {
                // all the primary (top level) row inserts are done as single transactions and all children of
                // top-level rows are part of the same transaction
                parseContext.getConnectionContext().beginTransaction();
                Table childTable = schema.getTableForXmlNode(qName);
                if(childTable == null)
                {
                    nodeStack.push(new NodeStackEntry(qName, depth));
                    parseContext.addSyntaxError("Table '"+ qName +"' not found in the schema.");
                }
                else
                {
                    Row childRow = childTable.createRow();
                    nodeStack.push(new NodeStackEntry(qName, childRow, depth));
                    for (int i = 0; i < attributes.getLength(); i++)
                        childRow.populateDataForXmlNodeName(attributes.getQName(i), attributes.getValue(i), false);
                }
            }
            else
            {
                NodeStackEntry entry = (NodeStackEntry) nodeStack.peek();
                if(entry.row != null)
                {
                    // if we're starting a child row, be sure to write out the active entry so that if there
                    // are relational dependencies everything will work
                    if(entry.row.isValidXmlNodeNameForChildRow(qName))
                    {
                        entry.write();
                        Row childRow = entry.row.createChildRowForXmlNodeName(qName);
                        nodeStack.push(new NodeStackEntry(qName, childRow, depth));
                        for (int i = 0; i < attributes.getLength(); i++)
                            childRow.populateDataForXmlNodeName(attributes.getQName(i), attributes.getValue(i), false);
                    }
                    else
                    {
                        if(entry.row.isValidXmlNodeNameForColumn(qName))
                            nodeStack.push(new NodeStackEntry(qName, entry.row, qName, depth));
                        else
                        {
                            nodeStack.push(new NodeStackEntry(qName, depth));
                            parseContext.addSyntaxError("Column '"+ qName +"' not found in table '"+ entry.row.getTable().getName() +"'.");
                        }
                    }
                }
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
            catch(SQLException se)
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
            if(nodeStack.size() > 0)
            {
                NodeStackEntry entry = (NodeStackEntry) nodeStack.pop();
                if(entry != null && ! entry.isColumnEntry() && entry.row != null)
                    entry.write();
                if(entry.depth == 1)
                    parseContext.getConnectionContext().commitActiveTransaction();
            }
        }
        catch (NamingException exc)
        {
            throw new SAXParseException(exc.getMessage(), parseContext.getLocator(), exc);
        }
        catch(SQLException se)
        {
            try
            {
                parseContext.getConnectionContext().rollbackActiveTransaction();
            }
            catch(SQLException se2)
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
