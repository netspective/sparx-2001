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
 * $Id: Utils.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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