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
 * $Id: DatabaseContextFactory.java,v 1.5 2002-09-02 23:00:26 shahid.shah Exp $
 */

package com.netspective.sparx.xif.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xif.db.policy.OracleDatabasePolicy;
import com.netspective.sparx.xif.db.policy.SqlServerDatabasePolicy;
import com.netspective.sparx.xif.db.policy.HSqlDbDatabasePolicy;
import com.netspective.sparx.xif.db.context.BasicDatabaseContext;

/**
 * Provides a factory pattern for constructing DatabaseContext instances. There are a number of rules that are followed
 * to help make the selection of a DatabaseContext as extensible as possible. Here are the rules:
 * <ol>
 *      <li>
 *          If there is a ServletContext attribute named "DatabaseContext", then the value of that attribute
 *          specifies an actual instance of a class that will be used as the DatabaseContext. This is a dynamic check
 *          which means that each time getContext is called, the ServletContext attribute will be checked.
 *      </li>
 *      <li>
 *          If a system property named com.netspective.sparx.xif.db.DatabaseContext.class is found, then the value of
 *          this system property is the name of a class that will be dynamically loaded to provide the default
 *          DatabaseContext instance. Once this property is found, the value of the class is cached and not checked
 *          again until the servlet is restarted.
 *      </li>
 *      <li>
 *          If a class called app.db.context.AppDatabaseContext is found in the classpath, then that class is used to instantiate
 *          a new object that will provide the default DatabaseContext instance. Once this property is found, the class
 *          is cached and not checked again until the servlet is restarted.
 *      </li>
 *      <li>
 *          If no ServletContext attribute is found, no 'com.netspective.sparx.xif.db.DatabaseContext.class' system
 *          property is found, and no class called app.db.context.AppDatabaseContext is found in the classpath, then the default
 *          DatabaseContext class that is used is BasicDatabaseContext. Errors for ClassNotFound are sent to STDERR.
 *      </li>
 * </ol>
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DatabaseContextFactory implements Factory
{
    /**
     * If the DatabaseContext is stored in a ServletContext atttibute, then this is the
     * preferred name of the attribute.
     */
    public static final String DBCONTEXT_SERVLETCONTEXT_ATTR_NAME = "DatabaseContext";

    /**
     * If a class by the name of CUSTOM_CONTEXT_CLASS is found in the classpath, it will be used as the default
     * DatabaseContext class instead of BasicDatabaseContext.
     */
    public static final String CUSTOM_CONTEXT_CLASS = "app.db.context.AppDatabaseContext";

    /**
     * If a system property named CONTEXTNAME_PROPNAME is found, it will be used to specify the DatabaseContext class
     */
    public static final String CONTEXTNAME_PROPNAME = "com.netspective.sparx.xif.db.DatabaseContext.class";

    /**
     * Once a DatabaseContext class has been instantiated, it is cached in the useContext variable.
     */
    private static DatabaseContext useContext = null;

    /**
     * A map with a list of all the DatabasePolicy objects
     */
    private static Map databasePolicies = new HashMap();

    static
    {
        addDatabasePolicy("Oracle", new OracleDatabasePolicy());
        addDatabasePolicy("Microsoft SQL Server", new SqlServerDatabasePolicy());
        addDatabasePolicy("HSQL Database Engine", new HSqlDbDatabasePolicy());
    }

    static public void addDatabasePolicy(String databaseProductName, DatabasePolicy policy)
    {
        databasePolicies.put(databaseProductName, policy);
    }

    static public void addDatabasePolicy(String databaseProductName, String policyClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        DatabasePolicy policy = (DatabasePolicy) Class.forName(policyClassName).newInstance();
        databasePolicies.put(databaseProductName, policy);
    }

    static public DatabasePolicy getDatabasePolicy(String databaseProductName)
    {
        return (DatabasePolicy) databasePolicies.get(databaseProductName);
    }

    static public DatabasePolicy getDatabasePolicy(Connection conn) throws SQLException
    {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        DatabasePolicy policy = getDatabasePolicy(databaseProductName);
        if(policy == null)
            throw new SQLException("Database policy not found for database '" + databaseProductName + "'");
        else
            return policy;
    }

    public static void addText(Document doc, Element parent, String elemName, String text)
    {
        Element elemNode = doc.createElement(elemName);
        Text textNode = doc.createTextNode(text);
        elemNode.appendChild(textNode);
        parent.appendChild(elemNode);
    }

    public static void addErrorProperty(Document doc, Element parent, String errorMsg)
    {
        Element propertyElem = doc.createElement("property");
        addText(doc, propertyElem, "name", "error");
        addText(doc, propertyElem, "value", errorMsg);
        parent.appendChild(propertyElem);
    }

    /**
     * Creates a catalog of all the data sources available
     */
    public static void createCatalog(ValueContext vc, Element parent) throws NamingException
    {
        ServletContext servletContext = vc.getServletContext();
        ServletRequest servletRequest = vc.getRequest();
        DatabaseContext dc = getContext(servletRequest, servletContext);

        Document doc = parent.getOwnerDocument();
        Element dataSourcesElem = doc.createElement("properties");
        parent.appendChild(dataSourcesElem);
        dataSourcesElem.setAttribute("class", dc != null ? dc.getClass().getName() : "No DatabaseContext found.");

        if(dc != null)
            dc.createCatalog(vc, dataSourcesElem);
        else
            addErrorProperty(doc, dataSourcesElem, "DatabaseContext could not be located. Check system property '" + CONTEXTNAME_PROPNAME + "'");
    }

    public static DatabaseContext getSystemContext()
    {
        if(useContext != null)
            return useContext;

        /**
         * if we haven't figured out our "default" connection manager, try and figure it out now -- use the rules
         * specified in the class documentation.
         */

        DatabaseContext dc = null;
        String contextClassName = System.getProperty(CONTEXTNAME_PROPNAME, CUSTOM_CONTEXT_CLASS);
        try
        {
            dc = (DatabaseContext) Class.forName(contextClassName).newInstance();
        }
        catch(ClassNotFoundException e)
        {
            dc = new BasicDatabaseContext();

            // if the CUSTOM_CONTEXT_CLASS is not found, it's NOT really an error so don't print a message
            if(! contextClassName.equals(CUSTOM_CONTEXT_CLASS))
                e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            dc = new BasicDatabaseContext();
            e.printStackTrace();
        }
        catch(InstantiationException e)
        {
            dc = new BasicDatabaseContext();
            e.printStackTrace();
        }

        useContext = dc;
        return useContext;
    }

    public static DatabaseContext getContext(ServletRequest servletRequest, ServletContext servletContext)
    {
        DatabaseContext dc = (DatabaseContext) servletContext.getAttribute(DBCONTEXT_SERVLETCONTEXT_ATTR_NAME);
        return dc != null ? dc : getSystemContext();
    }

    public static DatabaseContext getContext(DialogContext dialogContext)
    {
        DatabaseContext dc = dialogContext.getDatabaseContext();
        return dc != null ? dc : getContext(dialogContext.getRequest(), dialogContext.getServletContext());
    }
}