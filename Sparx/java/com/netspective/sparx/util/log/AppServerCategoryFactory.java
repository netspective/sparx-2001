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
 * $Id: AppServerCategoryFactory.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.util.log;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Category;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.CategoryFactory;

import com.netspective.sparx.util.factory.Factory;

/**
 *  Creates correctly populated instances of
 *  <a href="AppServerCategory.html"><code>AppServerCategory</code></a>.
 *  An attempt is made to
 *  determine the <code>hostname</code> using the
 *  <code>java.net</code> API.  The other three attributes,
 *  <p>
 *  <ol>
 *  <li>server name
 *  <li>component name
 *  <li>version string
 *  </ol>
 *  <p>
 *  can be set via the constructor.  All four attributes may
 *  be obtained and set through getters and setters.
 *
 *  @author Paul Glezen
 */
public class AppServerCategoryFactory implements CategoryFactory, Factory
{

    /** The hostname on which this factory resides.  This is
     determined dynamically using the java.net.InetAddress
     class. */
    protected String hostname;

    /** The application server name for this factory.  This
     is particularly meaningful in a CORBA or EBJ application
     server environment.  */
    protected String server;

    /** The name of the component using this factory.  */
    protected String component;

    /** An identifier for this particular version/release. */
    protected String version;

    /** The message bundle to be used by
     </code>AppServerCategory</code> instances.  */
    protected ResourceBundle messageBundle;

    /**
     *  Construct a new <code>AppServerCategoryFactory</code> with
     *  the provided attributes.  An attempt is made to obtain the
     *  hostname from the java.net API.  This constructor sets the
     *  newly created instance as the default factory for future
     *  invocations of {@link AppServerCategory#getInstance(String)}
     *  via {@link AppServerCategory#setFactory}.
     *
     *  @param categoryName  the name of the category.
     *  @param serverName    the name of the server using this category.  This
     *                       may be null.
     *  @param componentName the name of the component using this category.
     *                       This may be null.
     *  @param versionName   the version identifier of the component.  This may
     *                       may be null.
     */
    public AppServerCategoryFactory(String serverName, String componentName,
                                    String versionName)
    {
        try
        {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        }
        catch(java.net.UnknownHostException uhe)
        {
            LogLog.warn("AppServerCategoryFactory: could not determine local hostname.");
        }
        server = serverName;
        component = componentName;
        version = versionName;

        AppServerCategory.setFactory(this);
    }

    /**
     *  The default constructor merely calls the three-argument
     *  constructor with null values.
     */
    public AppServerCategoryFactory()
    {
        this(null, null, null);
    }

    /**
     *  Get the name of the component for which this category is logging.
     *
     *  @return the component name
     */
    public String getComponent()
    {
        return component;
    }

    /**
     *  Get the hostname of the machine on which this category is running.
     *
     *  @return the hostname
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     *  Get the name of the server process in which this category is running.
     *
     *  @return the server name
     */
    public String getServer()
    {
        return server;
    }

    /**
     *  Get the version name of the component in which this category is
     *  running.
     *
     *  @return the version name
     */
    public String getVersion()
    {
        return version;
    }

    /**
     *  Create a new instance of <code>AppServerCategory</code>
     *  using the information contained in this instance.
     */
    public Category makeNewCategoryInstance(String name)
    {
        Category result = new AppServerCategory(name, hostname, server,
                component, version);
        if(messageBundle != null)
            result.setResourceBundle(messageBundle);

        return result;
    }

    /**
     *  Set the name of the component for which the category will be logging.
     *
     *  @param component name of component
     *
     */
    public void setComponent(String component)
    {
        this.component = component;
    }

    /**
     *  Set the host name of the component on which this category is running.
     *  An attempt is made by the constructor to determine the hostname using
     *  the java.net API.  Use this method only to override this
     *  determination.
     *
     *  @param hostname the host name.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     *  Set the message bundle to be used for all <code>Category</code>
     *  objects created by this <code>CatgoryFactory</code>.
     *
     *  param bundle a bundle of messages
     */
    public void setMessageBundle(ResourceBundle bundle)
    {
        messageBundle = bundle;
    }

    /**
     *  Set the message bundle using the bundle filename.  This name
     *  should not include the "<code>.properties</code>" extension.
     *  Care should be taken to ensure the bundle file is somewhere
     *  in the system classpath or loadable by this class's class
     *  loader.
     *
     *  @param filename name of the bundle file
     */
    public void setMessageBundle(String filename)
    {
        try
        {
            messageBundle = ResourceBundle.getBundle(filename);
            LogLog.debug("Message bundle [" + filename + "] retrieved.");
        }
        catch(MissingResourceException mre)
        {
            LogLog.warn("Failed to find [" + filename + "] message bundle.");
        }
    }

    /**
     *  Set the name of the application server process in which this
     *  category is logging.
     *
     *  @param server name of application server process.
     */
    public void setServer(String server)
    {
        this.server = server;
    }

    /**
     *  Set the version string for the component.
     *
     *  @param version version name of component
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

}
