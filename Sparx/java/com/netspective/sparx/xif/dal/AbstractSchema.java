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
 * $Id: AbstractSchema.java,v 1.6 2002-12-23 05:07:01 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal;

import com.netspective.sparx.xif.dal.xml.ImportException;
import com.netspective.sparx.xif.dal.xml.ParseContext;
import com.netspective.sparx.xif.dal.xml.SchemaImportHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractSchema implements Schema
{
    private Map columnsByTypeNameMap = new HashMap();
    private Map tablesByNameMap = new HashMap();
    private Map tablesByXmlNodeNameMap = new HashMap();

    public AbstractSchema()
    {
    }

    public void registerColumnClass(String dataTypeName, Class colClass)
    {
        columnsByTypeNameMap.put(dataTypeName, colClass);
    }

    public Class getColumnClass(String dataTypeName)
    {
        return (Class) columnsByTypeNameMap.get(dataTypeName);
    }

    public Map getColumnClassesMap()
    {
        return columnsByTypeNameMap;
    }

    public AbstractSchema(List tables)
    {
        for (int i = 0; i < tables.size(); i++)
            addTable((Table) tables.get(i));
    }

    public AbstractSchema(Table[] tables)
    {
        for (int i = 0; i < tables.length; i++)
            addTable(tables[i]);
    }

    abstract public void initializeDefn();

    public void finalizeDefn()
    {
        for (Iterator i = tablesByNameMap.values().iterator(); i.hasNext();)
        {
            Table table = (Table) i.next();
            table.finalizeDefn();
        }
    }

    public int getTablesCount()
    {
        return tablesByNameMap.size();
    }

    public void addTable(Table table)
    {
        tablesByNameMap.put(table.getNameForMapKey(), table);
        tablesByXmlNodeNameMap.put(table.getNameForXmlNode(), table);
    }

    public Map getTablesMap()
    {
        return tablesByNameMap;
    }

    public Table getTable(String name)
    {
        return (Table) tablesByNameMap.get(AbstractTable.convertTableNameForMapKey(name));
    }

    public Table getTableForXmlNode(String nodeName)
    {
        Table table = getTable(nodeName);
        if (table != null)
            return table;
        else
            return (Table) tablesByXmlNodeNameMap.get(nodeName);
    }

    public Column getColumn(String tableName, String tableColumn)
    {
        Table table = getTable(tableName);
        if (table == null)
            return null;
        return table.getColumnByName(tableColumn);
    }

    public ForeignKey getForeignKey(Column srcColumn, short type, String ref)
    {
        String refTableName = null;
        String refColumnName = null;

        int delimPos = ref.indexOf(".");
        if (delimPos == -1)
        {
            refTableName = ref;
            refColumnName = "id";
        }
        else
        {
            refTableName = ref.substring(0, delimPos);
            refColumnName = ref.substring(delimPos + 1);
        }

        Table table = getTable(refTableName);
        if (table == null)
            return null;

        Column refColumn = table.getColumnByName(refColumnName);
        if (refColumn == null)
            return null;

        switch (type)
        {
            case ForeignKey.FKEYTYPE_LOOKUP:
                return new BasicForeignKey(srcColumn, refColumn);

            case ForeignKey.FKEYTYPE_PARENT:
                return new ParentForeignKey(srcColumn, refColumn);

            case ForeignKey.FKEYTYPE_SELF:
                return new SelfForeignKey(srcColumn, refColumn);
        }

        return null;
    }

    public ParseContext importFromXml(ConnectionContext cc, File srcFile) throws FileNotFoundException, IOException
    {
        String uri = "file:" + srcFile.getAbsolutePath().replace('\\', '/');
        for (int index = uri.indexOf('#'); index != -1; index = uri.indexOf('#'))
        {
            uri = uri.substring(0, index) + "%23" + uri.substring(index + 1);
        }

        FileInputStream inputStream = new FileInputStream(srcFile);
        InputSource inputSource = new InputSource();
        inputSource.setSystemId(uri);

        try
        {
            return importFromXml(cc, inputSource);
        }
        finally
        {
            if (inputStream != null)
                inputStream.close();
        }
    }

    public ParseContext importFromXml(ConnectionContext cc, InputSource inputSource) throws IOException
    {
        ParseContext pc = null;

        try
        {
            pc = new ParseContext(this, cc);

            XMLReader parser = pc.getParser();
            parser.setContentHandler(new SchemaImportHandler(pc));
            parser.parse(inputSource);
        }
        catch (ParserConfigurationException exc)
        {
            throw new ImportException("Parser has not been configured correctly", exc);
        }
        catch (SAXParseException exc)
        {
            throw new ImportException(exc.getMessage(), exc);
        }
        catch (SAXException exc)
        {
            Throwable t = exc.getException();
            if (t instanceof ImportException)
            {
                pc.addError(t.getMessage());
            }
            else
                pc.addError(exc.getMessage());
        }

        return pc;
    }
}
