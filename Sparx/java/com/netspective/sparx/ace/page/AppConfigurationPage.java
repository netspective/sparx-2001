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
 * $Id: AppConfigurationPage.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.PropertiesCollection;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.xaf.page.PageContext;

public class AppConfigurationPage extends AceServletPage
{
    public final String getName()
    {
        return "config";
    }

    public final String getPageIcon()
    {
        return "configuration.gif";
    }

    public final String getCaption(PageContext pc)
    {
        return "Configuration";
    }

    public final String getHeading(PageContext pc)
    {
        return "Application Configuration";
    }

    public void createConfigElement(PageContext pc, Configuration defaultConfig, Element itemElem, Property property)
    {
        itemElem.setAttribute("name", property.getName());
        String expression = property.getExpression();
        String value = defaultConfig.getValue(pc, property, null);
        itemElem.setAttribute("value", value);
        if(!expression.equals(value))
        {
            itemElem.setAttribute("expression", expression);
            if(!property.flagIsSet(Property.PROPFLAG_IS_FINAL))
                itemElem.setAttribute("final", "no");
        }
        if(property.getDescription() != null)
            itemElem.setAttribute("description", property.getDescription());
    }

    public void handlePageBody(PageContext pc) throws ServletException, IOException
    {
        ServletContext context = pc.getServletContext();
        Document configDoc = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            configDoc = builder.newDocument();
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }

        Element configRootElem = configDoc.createElement("xaf");
        configDoc.appendChild(configRootElem);

        Element configItemsElem = configDoc.createElement("config-items");
        configRootElem.appendChild(configItemsElem);

        ConfigurationManager manager = ConfigurationManagerFactory.getManager(context);
        configItemsElem.setAttribute("source-file", manager.getSourceDocument().getFile().getAbsolutePath());
        configItemsElem.setAttribute("allow-reload", manager.getAllowReload() ? "Yes" : "No");

        List errors = manager.getErrors();
        if(errors.size() > 0)
        {
            Element errorsElem = configDoc.createElement("errors");
            configRootElem.appendChild(errorsElem);

            for(Iterator ei = errors.iterator(); ei.hasNext();)
            {
                Element errorElem = configDoc.createElement("error");
                Text errorText = configDoc.createTextNode((String) ei.next());
                errorElem.appendChild(errorText);
                errorsElem.appendChild(errorElem);
            }
        }

        Configuration defaultConfig = manager.getDefaultConfiguration();
        for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext();)
        {
            Element itemElem = configDoc.createElement("config-item");
            Map.Entry configEntry = (Map.Entry) i.next();

            if(configEntry.getValue() instanceof Property)
            {
                Property property = (Property) configEntry.getValue();
                createConfigElement(pc, defaultConfig, itemElem, property);
            }
            else if(configEntry.getValue() instanceof PropertiesCollection)
            {
                itemElem = configDoc.createElement("config-items");
                itemElem.setAttribute("name", (String) configEntry.getKey());

                PropertiesCollection propColl = (PropertiesCollection) configEntry.getValue();
                Collection coll = propColl.getCollection();
                for(Iterator c = coll.iterator(); c.hasNext();)
                {
                    Element childElem = configDoc.createElement("config-item");
                    Property property = (Property) c.next();
                    createConfigElement(pc, defaultConfig, childElem, property);
                    itemElem.appendChild(childElem);
                }
            }

            configItemsElem.appendChild(itemElem);
        }

        transform(pc, configDoc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "config-browser-xsl");
    }
}
