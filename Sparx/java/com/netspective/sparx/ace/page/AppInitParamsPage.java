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
 * $Id: AppInitParamsPage.java,v 1.2 2002-08-25 17:33:31 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.BuildConfiguration;
import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.page.PageContext;

public class AppInitParamsPage extends AceServletPage
{
    public final String getName()
    {
        return "servlet-context";
    }

    public final String getPageIcon()
    {
        return "servlet_context.gif";
    }

    public final String getCaption(PageContext pc)
    {
        return "Servlet Context";
    }

    public final String getHeading(PageContext pc)
    {
        return "Application Servlet Context";
    }

    public void handlePageBody(PageContext pc) throws ServletException, IOException
    {
        ServletContext context = pc.getServletContext();
        Document doc = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }

        Element rootElem = doc.createElement("xaf");
        doc.appendChild(rootElem);

        Element propertiesElem = doc.createElement("properties");
        propertiesElem.setAttribute("name", "Execution Environment");
        rootElem.appendChild(propertiesElem);

        Element propertyElem = doc.createElement("property");
        addText(propertyElem, "name", "Is Production Environment");
        addText(propertyElem, "value", ConfigurationManagerFactory.isProductionEnvironment(context) ? "Yes" : "No");
        propertiesElem.appendChild(propertyElem);

        propertyElem = doc.createElement("property");
        addText(propertyElem, "name", "Is Production or Testing Environment");
        addText(propertyElem, "value", ConfigurationManagerFactory.isProductionOrTestEnvironment(context) ? "Yes" : "No");
        propertiesElem.appendChild(propertyElem);

        propertyElem = doc.createElement("property");
        addText(propertyElem, "name", "Is Testing Environment");
        addText(propertyElem, "value", ConfigurationManagerFactory.isTestEnvironment(context) ? "Yes" : "No");
        propertiesElem.appendChild(propertyElem);

        propertyElem = doc.createElement("property");
        addText(propertyElem, "name", "Is Development Environment");
        addText(propertyElem, "value", ConfigurationManagerFactory.isDevelopmentEnvironment(context) ? "Yes" : "No");
        propertiesElem.appendChild(propertyElem);

        propertiesElem = doc.createElement("properties");
        propertiesElem.setAttribute("name", "Classpath (" + Thread.currentThread().getContextClassLoader().getClass().getName() + ")");
        rootElem.appendChild(propertiesElem);

        BuildConfiguration.ClassPathInfo[] classPaths = BuildConfiguration.getClassPaths();
        if(classPaths != null)
        {
            for(int i = 0; i < classPaths.length; i++)
            {
                BuildConfiguration.ClassPathInfo cpi = classPaths[i];
                propertyElem = doc.createElement("property");
                addText(propertyElem, "name", cpi.getClassPath().getAbsolutePath());
                addText(propertyElem, "value", !cpi.isValid() ? "invalid" : (cpi.isDirectory() ? "directory" : (cpi.isJar() ? "JAR" : "ZIP")));
                propertiesElem.appendChild(propertyElem);
            }
        }

        propertiesElem = doc.createElement("properties");
        propertiesElem.setAttribute("name", "Init Parameters");
        rootElem.appendChild(propertiesElem);

        for(Enumeration e = context.getInitParameterNames(); e.hasMoreElements();)
        {
            propertyElem = doc.createElement("property");
            String paramName = (String) e.nextElement();
            addText(propertyElem, "name", paramName);
            addText(propertyElem, "value", context.getInitParameter(paramName));
            propertiesElem.appendChild(propertyElem);
        }

        propertiesElem = doc.createElement("properties");
        propertiesElem.setAttribute("name", "Libraries");
        rootElem.appendChild(propertiesElem);

        propertyElem = doc.createElement("property");
        String className = javax.xml.parsers.DocumentBuilderFactory.newInstance().getClass().getName();
        addText(propertyElem, "name", "XML Document Builder Factory");
        addText(propertyElem, "value", className);
        addText(propertyElem, "value-detail", BuildConfiguration.getClassFileName(className));
        propertiesElem.appendChild(propertyElem);

        propertyElem = doc.createElement("property");
        className = javax.xml.transform.TransformerFactory.newInstance().getClass().getName();
        addText(propertyElem, "name", "XSLT Transformer Factory");
        addText(propertyElem, "value", className);
        addText(propertyElem, "value-detail", BuildConfiguration.getClassFileName(className));
        propertiesElem.appendChild(propertyElem);

        transform(pc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
    }
}
