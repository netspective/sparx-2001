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
 * $Id: SchemaDocFactory.java,v 1.2 2002-12-15 18:03:18 shahid.shah Exp $
 */

package com.netspective.sparx.xif;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.factory.FactoryListener;
import com.netspective.sparx.util.factory.FactoryEvent;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

public class SchemaDocFactory implements Factory
{
    static final String ATTRNAME_SCHEMADOC = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "schema-doc";
    static Map docs = new HashMap();
    static List listeners;

    public static void addListener(FactoryListener listener)
    {
        if(listeners == null)
            listeners = new ArrayList();

        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public static void contentsChanged(SchemaDocument instance)
    {
        if(listeners == null)
            return;

        FactoryEvent event = new FactoryEvent(SchemaDocFactory.class, instance);
        for(Iterator i = listeners.iterator(); i.hasNext();)
        {
            FactoryListener listener = (FactoryListener) i.next();
            listener.factoryContentsChanged(event);
        }
    }

    public static SchemaDocument getDoc(String file)
    {
        SchemaDocument schemaDoc = (SchemaDocument) docs.get(file);
        if(schemaDoc == null)
        {
            schemaDoc = new SchemaDocument(new File(file));
            docs.put(file, schemaDoc);
        }
        return schemaDoc;
    }

    public static SchemaDocument getDoc(ValueContext vc, String dataSourceId, String catalog, String schemaPattern) throws ParserConfigurationException, NamingException, SQLException
    {
        SchemaDocument schemaDoc = (SchemaDocument) docs.get(dataSourceId);
        if(schemaDoc == null)
        {
            DatabaseContext dbc = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
            schemaDoc = new SchemaDocument(dbc.getConnection(vc, dataSourceId), catalog, schemaPattern);
            docs.put(dataSourceId, schemaDoc);
        }
        return schemaDoc;
    }

    public static SchemaDocument getDoc(ServletContext context)
    {
        SchemaDocument doc = (SchemaDocument) context.getAttribute(ATTRNAME_SCHEMADOC);
        if(doc != null)
            return doc;

        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
        ValueContext vc = new ServletValueContext(context, null, null, null);
        doc = getDoc(appConfig.getTextValue(vc, "app.schema.source-file"));
        doc.initializeForServlet(context);
        context.setAttribute(ATTRNAME_SCHEMADOC, doc);
        return doc;
    }
}
