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
 * $Id: BasicDatabaseContext.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.xif.db.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

/**
 * The reference DatabaseContext implementation for a servlet or JSP using XAF
 * database and SQL packages.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class BasicDatabaseContext extends AbstractDatabaseContext
{
    static private Context env;

    public Connection getConnection(String dataSourceId) throws NamingException, SQLException
    {
        if(env == null)
            env = (Context) new InitialContext().lookup("java:comp/env");

        DataSource source = (DataSource) env.lookup(dataSourceId);
        if(source == null)
            throw new NamingException("Data source '" + dataSourceId + "' not found");

        return source.getConnection();
    }

    public final Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException
    {
        if(env == null)
            env = (Context) new InitialContext().lookup("java:comp/env");

        dataSourceId = translateDataSourceId(vc, dataSourceId);

        // check to see if there is already a connection bound with the request
        // meaning we're within a transaction. Reuse the connection if we are
        // within a connection
        Connection conn = null;
        if(vc != null)
        {
            ServletRequest request = vc.getRequest();
            conn = (Connection) request.getAttribute(dataSourceId);
        }
        if(conn == null)
        {
            DataSource source = (DataSource) env.lookup(dataSourceId);
            if(source == null)
                throw new NamingException("Data source '" + dataSourceId + "' not found");
            conn = source.getConnection();
        }
        return conn;
    }

    public void createCatalog(ValueContext vc, Element parent) throws NamingException
    {
        Document doc = parent.getOwnerDocument();

        Context env = (Context) new InitialContext().lookup("java:comp/env/jdbc");
        for(NamingEnumeration e = env.list(""); e.hasMore();)
        {
            Element propertyElem = doc.createElement("property");

            NameClassPair entry = (NameClassPair) e.nextElement();
            DatabaseContextFactory.addText(doc, propertyElem, "name", "jdbc/" + entry.getName());

            try
            {
                DataSource source = (DataSource) env.lookup(entry.getName());
                Connection conn = source.getConnection();
                DatabaseMetaData dbmd = conn.getMetaData();
                String databasePolicyClass = null;

                try
                {
                    databasePolicyClass = DatabaseContextFactory.getDatabasePolicy(conn).getClass().getName();
                }
                catch(Exception dpe)
                {
                    databasePolicyClass = dpe.toString();
                }

                DatabaseContextFactory.addText(doc, propertyElem, "value", dbmd.getDriverName());
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Product: " + dbmd.getDatabaseProductName());
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Product Version: " + dbmd.getDatabaseProductVersion());
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Driver Version: " + dbmd.getDriverVersion());
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Database Policy: " + databasePolicyClass);
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "URL: " + dbmd.getURL());
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "User: " + dbmd.getUserName());

                String resultSetType = "unknown";
                if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))
                    resultSetType = "scrollable (insensitive)";
                else if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))
                    resultSetType = "scrollable (sensitive)";
                else if(dbmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY))
                    resultSetType = "non-scrollabe (forward only)";
                DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "ResultSet Type: " + resultSetType);
            }
            catch(Exception ex)
            {
                DatabaseContextFactory.addText(doc, propertyElem, "value", ex.toString());
            }
            parent.appendChild(propertyElem);
        }
    }
}