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
 * $Id: AppServerCategory.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.util.log;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.spi.LoggingEvent;

/**
 *  Extends {@link org.apache.log4j.Category Category} by adding four
 *  text attributes relevant to applications applications run in
 *  application servers.  These attributes are
 *
 *  <p>
 *  <ul>
 *  <li><b>host</b> - the host on which the code is running
 *  <li><b>server</b> - the server process in which the code
 *      is running
 *  <li><b>component</b> - the component of which the code
 *      is a part
 *  <li><b>version</b> - the version of this code
 *  </ul>
 *
 *  <p>This <code>Category</code> subclass generates {@link
 *  AppServerLoggingEvent AppServerLoggingEvent} subclasses of {@link
 *  org.apache.log4j.spi.LoggingEvent LoggingEvent} which include the
 *  additional attributes.  {@link AppServerPatternLayout
 *  AppServerPatternLayout} provides the ability to format these
 *  attributes.
 *
 *  <p>Rather than set all these attributes for each
 *  <code>AppServerCategory</code> instance, it is usually more
 *  convenient to set them once on {@link AppServerCategoryFactory}.
 *  The factory can then be associated with the
 *  <code>AppServerCategory</code> class via {@link #setFactory}
 *  or with the entire hierarchy via
 *  {@link org.apache.log4j.Hierarchy#setCategoryFactory}.  In the
 *  former case, you should use {@link AppServerCategory#getInstance}
 *  to create new categories.  In the latter case, you would use
 *  {@link org.apache.log4j.Category#getInstance(String)}.  The former
 *  method allows finer granularity of control; the latter is more
 *  convenient.  Reliance on the
 *  {@link org.apache.log4j.PropertyConfigurator} will employ the
 *  latter.
 *
 *  <p>More convenient still is to rely on the
 *  {@link org.apache.log4j.Category} static initializer.  See the
 *  package level documention for details.
 *
 *  @author Paul Glezen */
public class AppServerCategory extends Category
{

    private static String FQCN = AppServerCategory.class.getName();

    /** The name of the component using this category.  */
    protected String component;

    /** The hostname on which this category resides.  */
    protected String hostname;

    /** The application server name for this category. This is
     particularly meaningful in a CORBA or EBJ application
     server environment.  */
    protected String server;

    /** An identifier for this particular version/release. */
    protected String version = com.netspective.sparx.BuildConfiguration.getVersionAndBuildShort();

    /** A reference to the factory to create <code>AppServerCategory</code>
     instances.  */
    private static CategoryFactory factory = new AppServerCategoryFactory(null, null, null);

    /**
     *  Construct a new AppServerCategory with the provided
     *  attributes.  The constructor is protected because the only
     *  classes invoking it should be a CategoryFactory subclass or
     *  a subclass of AppServerCategory.
     *
     *  @param categoryName the name of the category.
     *  @param instanceFCQN the fully qualified name of this category instance
     *  @param hostname     the name of the physical machine on which this
     *                      category resides.  This may be null.
     *  @param server       the name of the server using this category.  This
     *                      may be null.
     *  @param component    the name of the component using this category.
     *                      This may be null.
     *  @param version      the version identifier of the component.  This may
     *                      may be null.
     */
    protected AppServerCategory(String categoryName, String hostname,
                                String server, String component, String version)
    {
        super(categoryName);

        this.hostname = hostname;
        this.server = server;
        this.component = component;
        this.version = version;
    }

    /**
     *  Get the component name for this category.
     *
     *  @return the category name
     */
    public String getComponent()
    {
        String result = "";

        if(component != null)
            result = component;

        return result;
    }

    /**
     *  Get the hostname for this category.
     *
     *  @return a string representation of the hostname
     */
    public String getHostname()
    {
        String result = "";

        if(hostname != null)
            result = hostname;

        return result;
    }

    /**
     *  Return an <code>AppServerCategory</code> instance with the
     *  provided name.  If such an instance exists, return it.
     *  Otherwise, create a new one.
     *
     *  @param name the name of the Catgory
     *  @return an instance of <code>AppServerCategory</code>.  The
     *          signature indicates <code>Category</code> to maintain
     *          compatibility with the base class.
     */
    public static Category getInstance(String name)
    {
        return Category.getInstance(name, factory);
    }

    /**
     * Get the server name for this category.  This attribute is more
     * germane in application server environments such as CORBA and EJB.
     *
     * @return a string representing the server name
     */
    public String getServer()
    {
        String result = "";

        if(server != null)
            result = server;

        return result;
    }

    /**
     *  Get the version name for this category.
     *
     *  @return the version of the the component for this category.
     */
    public String getVersion()
    {
        String result = "";

        if(version != null)
            result = version;

        return result;
    }

    /**
     *  This method is overridden to ensure an instance of
     *  <code>AppServerLoggingEvent</code> is sent to the
     *  appenders.
     */
    protected void forcedLog(String fqn, Priority priority, Object message, Throwable t)
    {
        LoggingEvent event = new AppServerLoggingEvent(fqn, this, priority, message, t);
        callAppenders(event);
    }


    /**
     *  Set the component name for this category.
     *
     *  @param categoryName the component name to be used for this category.
     */
    public void setComponent(String componentName)
    {
        component = componentName;
    }

    /**
     *  Set the factory instance for creation of
     *  <code>AppServerCategory</code> instances.
     *  in the <code>getInstance</code> method.
     *
     *  @param factory an <code>AppServerCategory</code> factory
     */
    public static void setFactory(CategoryFactory factory)
    {
        AppServerCategory.factory = factory;
    }

    /**
     *  Explicity set the hostname for this category.
     *
     *  @param hostname the hostname to be used for this category.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     *  Set the server name for this category.
     *
     *  @param serverName the server name to be used for this category.
     *             This is useful in CORBA and EJB environments.
     */
    public void setServer(String serverName)
    {
        server = serverName;
    }

    /**
     *  Set the version of the component for this category.
     *
     *  @param versionName version name
     */
    public void setVersion(String versionName)
    {
        version = versionName;
    }
}
