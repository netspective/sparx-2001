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
 * $Id: AppFactoryPage.java,v 1.3 2002-12-27 17:16:03 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.form.DialogFieldFactory;
import com.netspective.sparx.xaf.report.ColumnDataCalculatorFactory;
import com.netspective.sparx.xaf.report.ReportColumnFactory;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.querydefn.SqlComparisonFactory;
import com.netspective.sparx.xaf.task.TaskFactory;
import com.netspective.sparx.xaf.html.ComponentCommandFactory;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.ValueContext;

public class AppFactoryPage extends AceServletPage
{
    static public final int FACTORY_VALUESOURCE = 0;
    static public final int FACTORY_DIALOG_FIELD = 1;
    static public final int FACTORY_REPORT_COMPS = 2;
    static public final int FACTORY_TASK = 3;
    static public final int FACTORY_SKIN = 4;
    static public final int FACTORY_SQL_COMPARE = 5;
    static public final int FACTORY_COMPONENT_COMMANDS = 6;

    private String name;
    private String caption;
    private int factory;

    public AppFactoryPage()
    {
        super();
    }

    public AppFactoryPage(String name, String caption, int factory)
    {
        this();
        this.name = name;
        this.caption = caption;
        this.factory = factory;
    }

    public final String getName()
    {
        return name == null ? "factory" : name;
    }

    public final String getPageIcon()
    {
        return "factories.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return caption == null ? "Factories" : caption;
    }

    public final String getHeading(ValueContext vc)
    {
        return getCaption(vc);
    }

    public void handlePageBody(Writer writer, NavigationPathContext nc) throws ServletException, IOException
    {
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

        switch(factory)
        {
            case FACTORY_VALUESOURCE:
                ValueSourceFactory.createCatalog(rootElem);
                break;

            case FACTORY_DIALOG_FIELD:
                DialogFieldFactory.createCatalog(rootElem);
                break;

            case FACTORY_REPORT_COMPS:
                ReportColumnFactory.createCatalog(rootElem);
                ColumnDataCalculatorFactory.createCatalog(rootElem);
                break;

            case FACTORY_TASK:
                TaskFactory.createCatalog(rootElem);
                break;

            case FACTORY_SKIN:
                SkinFactory.createCatalog(rootElem);
                break;

            case FACTORY_SQL_COMPARE:
                SqlComparisonFactory.createCatalog(rootElem);
                break;

            case FACTORY_COMPONENT_COMMANDS:
                ComponentCommandFactory.createCatalog(rootElem);
        }

        transform(nc, doc, ACE_CONFIG_ITEM_PROPBROWSERXSL);
    }
}
