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
 * $Id: StatementManagerFactory.java,v 1.3 2002-08-25 16:06:16 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;

public class StatementManagerFactory implements Factory
{
    static final String ATTRNAME_STATEMENTMGR = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "statement-mgr";
    private static Map managers = new HashMap();

    /**
     * Method used for retrieving a <code>StatementManager</code> object representing a
     * SQL XML file. The factory first looks for the <code>StatementManager</code> object
     * from a <code>Map</code> and if it doesn't exist, it creates a new one and adds it
     * to the map of statement managers.
     *
     * @param file static SQL XML file name
     * @return StatementManager
     */
    public static StatementManager getManager(String file)
    {
        StatementManager activeManager = (StatementManager) managers.get(file);
        if(activeManager == null)
        {
            activeManager = new StatementManager(new File(file));
            managers.put(file, activeManager);
        }
        return activeManager;
    }

    /**
     * Method used for retrieving a <code>StatementManager</code> object within a web application context.
     * The factory retrieves the static SQL XML file name from <code>app.sql.source-file</code> configuraton entry
     * defined in <code>WEB-INF/conf/sparx.xml</code> of the web application.
     *
     * @param context the servlet context
     * @return StatementManager
     */
    public static StatementManager getManager(ServletContext context)
    {
        StatementManager manager = (StatementManager) context.getAttribute(ATTRNAME_STATEMENTMGR);
        if(manager != null)
            return manager;

        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
        ValueContext vc = new ServletValueContext(context, null, null, null);
        manager = getManager(appConfig.getTextValue(vc, "app.sql.source-file"));
        manager.initializeForServlet(context);
        context.setAttribute(ATTRNAME_STATEMENTMGR, manager);
        return manager;
    }
}