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
 * $Id: SkinFactory.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.report.ReportSkin;

public class SkinFactory implements Factory
{
    private static Map reportSkins = new HashMap();
    private static Map dialogSkins = new HashMap();

    static
    {
        addReportSkin("report", new HtmlReportSkin());
        addReportSkin("component", new HtmlComponentSkin());
        addReportSkin("detail", new HtmlSingleRowReportSkin(1, true));
        addReportSkin("detail-2col", new HtmlSingleRowReportSkin(2, true));
        addReportSkin("data-only", new HtmlSingleRowReportNoCaptionSkin(1, true));
        addReportSkin("text-csv", new TextReportSkin(".csv", ",", "\"", true));
        addReportSkin("text-tab", new TextReportSkin(".txt", "  ", null, true));

        addDialogSkin("default", new StandardDialogSkin());
        addDialogSkin("hand-held", new HandHeldDialogSkin());
    }

    public static void addReportSkin(String id, ReportSkin skin)
    {
        reportSkins.put(id, skin);
    }

    public static void addReportSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addReportSkin(id, (ReportSkin) skinClass.newInstance());
    }

    public static ReportSkin getReportSkin(String id)
    {
        return (ReportSkin) reportSkins.get(id);
    }

    public static void addDialogSkin(String id, DialogSkin skin)
    {
        dialogSkins.put(id, skin);
    }

    public static void addDialogSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class skinClass = Class.forName(className);
        addDialogSkin(id, (DialogSkin) skinClass.newInstance());
    }

    public static DialogSkin getDialogSkin(String id)
    {
        return (DialogSkin) dialogSkins.get(id);
    }

    public static DialogSkin getDialogSkin()
    {
        return getDialogSkin("default");
    }

    public static void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Dialog Skins");
        factoryElem.setAttribute("class", SkinFactory.class.getName());
        for(Iterator i = dialogSkins.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("dialog-skin");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((DialogSkin) entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }

        factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Report Skins");
        factoryElem.setAttribute("class", SkinFactory.class.getName());
        for(Iterator i = reportSkins.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("report-column-format");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((ReportSkin) entry.getValue()).getClass().getName());
            factoryElem.appendChild(childElem);
        }
    }

}