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
 * $Id: ParseContext.java,v 1.5 2002-12-23 05:07:02 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal.xml;

import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.dal.Schema;
import com.netspective.sparx.xif.dal.Table;
import com.netspective.sparx.xif.dal.TableImportStatistic;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseContext
{
    private static SAXParserFactory parserFactory;

    private ConnectionContext cc;
    private Schema schema;
    private XMLReader parser;
    private Locator locator;
    private boolean throwSyntaxErrorException;
    private List errors;
    private List messages;
    private Map statistics; // key is table name, value is a TableImportStatistic object

    public ParseContext(Schema schema, ConnectionContext cc) throws ParserConfigurationException, SAXException
    {
        this.schema = schema;
        this.cc = cc;
        this.errors = new ArrayList();
        this.messages = new ArrayList();
        this.statistics = new HashMap();

        SAXParser saxParser = getParserFactory().newSAXParser();
        parser = saxParser.getXMLReader();
    }

    public ConnectionContext getConnectionContext()
    {
        return cc;
    }

    public Map getStatistics()
    {
        return statistics;
    }

    public TableImportStatistic getStatistics(Table table)
    {
        String tableName = table.getNameForMapKey();
        TableImportStatistic stat = (TableImportStatistic) statistics.get(tableName);
        if (stat == null)
        {
            stat = new TableImportStatistic(tableName);
            statistics.put(tableName, stat);
        }
        return stat;
    }

    private static SAXParserFactory getParserFactory()
    {
        if (parserFactory == null)
            parserFactory = SAXParserFactory.newInstance();

        return parserFactory;
    }

    public File resolveFile(String src)
    {
        return new File(src);
    }

    public void setThrowSyntaxErrorException(boolean throwSyntaxErrorException)
    {
        this.throwSyntaxErrorException = throwSyntaxErrorException;
    }

    public Schema getSchema()
    {
        return schema;
    }

    public XMLReader getParser()
    {
        return parser;
    }

    public Locator getLocator()
    {
        return locator;
    }

    public void setLocator(Locator locator)
    {
        this.locator = locator;
    }

    public boolean isThrowSyntaxErrorException()
    {
        return throwSyntaxErrorException;
    }

    public void addError(String message)
    {
        errors.add(message + " at " + locator.getSystemId() + " line " + locator.getLineNumber());
    }

    public List getErrors()
    {
        return errors;
    }

    public void addMessage(String message)
    {
        messages.add(message + " at " + locator.getSystemId() + " line " + locator.getLineNumber());
    }

    public List getMessages()
    {
        return messages;
    }
}
