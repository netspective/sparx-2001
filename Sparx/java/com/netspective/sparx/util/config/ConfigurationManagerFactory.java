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
 * $Id: ConfigurationManagerFactory.java,v 1.2 2002-08-25 16:06:16 shahid.shah Exp $
 */

package com.netspective.sparx.util.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

import javax.servlet.ServletContext;

import com.netspective.sparx.util.factory.Factory;

public class ConfigurationManagerFactory implements Factory
{
    private static final String APPEXECENV_INIT_PARAM_NAME = "app-exec-environment";
    private static final String CONFIGMGR_ATTR_NAME = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "config-mgr";
    private static final String APPCONFIG_ATTR_NAME = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "app-config";
    private static Map managers = new HashMap();

    public static boolean isProductionEnvironment(ServletContext context)
    {
        String appEnv = context.getInitParameter(APPEXECENV_INIT_PARAM_NAME);
        return "Production".equalsIgnoreCase(appEnv);
    }

    public static boolean isProductionOrTestEnvironment(ServletContext context)
    {
        String appEnv = context.getInitParameter(APPEXECENV_INIT_PARAM_NAME);
        return "Production".equalsIgnoreCase(appEnv) || "Testing".equalsIgnoreCase(appEnv);
    }

    public static boolean isTestEnvironment(ServletContext context)
    {
        String appEnv = context.getInitParameter(APPEXECENV_INIT_PARAM_NAME);
        return "Testing".equalsIgnoreCase(appEnv);
    }

    public static boolean isDevelopmentEnvironment(ServletContext context)
    {
        String appEnv = context.getInitParameter(APPEXECENV_INIT_PARAM_NAME);
        return appEnv == null || "Development".equalsIgnoreCase(appEnv);
    }

    public static String getExecutionEvironmentName(ServletContext context)
    {
        String appEnv = context.getInitParameter(APPEXECENV_INIT_PARAM_NAME);
        if(appEnv == null)
            appEnv = "Development";
        return appEnv;
    }

    public static ConfigurationManager getManager(String file)
    {
        ConfigurationManager activeManager = (ConfigurationManager) managers.get(file);
        if(activeManager == null)
        {
            activeManager = new ConfigurationManager(new File(file));
            managers.put(file, activeManager);
        }
        return activeManager;
    }

    public static ConfigurationManager getManager(ServletContext context)
    {
        ConfigurationManager manager = (ConfigurationManager) context.getAttribute(CONFIGMGR_ATTR_NAME);
        if(manager != null)
            return manager;

        String configFile = context.getInitParameter(com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "config-file");
        if(configFile == null)
            configFile = "WEB-INF/conf/sparx.xml";
        manager = getManager(context.getRealPath(configFile));
        manager.initializeForServlet(context);

        context.setAttribute(CONFIGMGR_ATTR_NAME, manager);
        context.setAttribute(APPCONFIG_ATTR_NAME, manager.getDefaultConfiguration());
        return manager;
    }

    public static Configuration getDefaultConfiguration(ServletContext context)
    {
        Configuration config = (Configuration) context.getAttribute(APPCONFIG_ATTR_NAME);
        if(config != null)
            return config;

        // when we call getManager(context) it will automatically sets the APPCONFIG attribute
        getManager(context);
        return (Configuration) context.getAttribute(APPCONFIG_ATTR_NAME);
    }
}