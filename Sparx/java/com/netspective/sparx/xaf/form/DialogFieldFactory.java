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
 * $Id: DialogFieldFactory.java,v 1.4 2002-07-08 21:27:45 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.xaf.form.field.BooleanField;
import com.netspective.sparx.xaf.form.field.CurrencyField;
import com.netspective.sparx.xaf.form.field.DateTimeField;
import com.netspective.sparx.xaf.form.field.DebugField;
import com.netspective.sparx.xaf.form.field.DurationField;
import com.netspective.sparx.xaf.form.field.EmailField;
import com.netspective.sparx.xaf.form.field.FileField;
import com.netspective.sparx.xaf.form.field.FloatField;
import com.netspective.sparx.xaf.form.field.GridField;
import com.netspective.sparx.xaf.form.field.HtmlField;
import com.netspective.sparx.xaf.form.field.IntegerField;
import com.netspective.sparx.xaf.form.field.MemoField;
import com.netspective.sparx.xaf.form.field.PhoneField;
import com.netspective.sparx.xaf.form.field.ReportField;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.field.SeparatorField;
import com.netspective.sparx.xaf.form.field.SocialSecurityField;
import com.netspective.sparx.xaf.form.field.StaticField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.form.field.UriField;
import com.netspective.sparx.xaf.form.field.ZipField;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalApplyFlag;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalData;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalDisplay;
import com.netspective.sparx.xaf.querydefn.ResultSetNavigatorButtonsField;

/**
 * Dialog field factory class for registering and retrieving dialog field objects (including
 * conditional fields).
 */
public class DialogFieldFactory implements Factory
{
    static Map fieldClasses = new HashMap();
    static Map conditionalsClasses = new HashMap();

    static
    {
        fieldClasses.put("field.debug", DebugField.class);
        fieldClasses.put("field.composite", DialogField.class);
        fieldClasses.put("field.grid", GridField.class);
        fieldClasses.put("field.text", TextField.class);
        fieldClasses.put("field.static", StaticField.class);
        fieldClasses.put("field.memo", MemoField.class);
        fieldClasses.put("field.date", DateTimeField.class);
        fieldClasses.put("field.time", DateTimeField.class);
        fieldClasses.put("field.datetime", DateTimeField.class);
        fieldClasses.put("field.duration", DurationField.class);
        fieldClasses.put("field.boolean", BooleanField.class);
        fieldClasses.put("field.integer", IntegerField.class);
        fieldClasses.put("field.float", FloatField.class);
        fieldClasses.put("field.select", SelectField.class);
        fieldClasses.put("field.separator", SeparatorField.class);
        fieldClasses.put("field.ssn", SocialSecurityField.class);
        fieldClasses.put("field.phone", PhoneField.class);
        fieldClasses.put("field.report", ReportField.class);
        fieldClasses.put("field.zip", ZipField.class);
        fieldClasses.put("field.email", EmailField.class);
        fieldClasses.put("field.uri", UriField.class);
        fieldClasses.put("field.currency", CurrencyField.class);
        fieldClasses.put("field.file", FileField.class);
        fieldClasses.put("field.rs-navigator", ResultSetNavigatorButtonsField.class);
        fieldClasses.put("field.html", HtmlField.class);

        conditionalsClasses.put("display", DialogFieldConditionalDisplay.class); // legacy
        conditionalsClasses.put("display-on-js-expr", DialogFieldConditionalDisplay.class);
        conditionalsClasses.put("data", DialogFieldConditionalData.class); // legacy
        conditionalsClasses.put("display-when-partner-not-null", DialogFieldConditionalData.class);
        conditionalsClasses.put("apply-flag", DialogFieldConditionalApplyFlag.class);
        conditionalsClasses.put("set-value", DialogFieldConditionalApplyFlag.class);
    }

    /**
     * Gets the list of  dialog field classes as a map
     *
     * @return Map
     */
    public static Map getFieldClasses()
    {
        return fieldClasses;
    }

    /**
     * Add a new dialog field type
     *
     * @param tagName XML tag name
     * @param cls Class name for the new field
     */
    public static void addFieldType(String tagName, Class cls)
    {
        fieldClasses.put(tagName, cls);
    }

    public static void addFieldType(String tagName, String className) throws ClassNotFoundException
    {
        Class fieldClass = Class.forName(className);
        addFieldType(tagName, fieldClass);
    }

    public static void addConditionalType(String actionName, Class cls)
    {
        conditionalsClasses.put(actionName, cls);
    }

    public static void addConditionalType(String actionName, String className) throws ClassNotFoundException
    {
        Class fieldClass = Class.forName(className);
        addConditionalType(actionName, fieldClass);
    }

    public static void createCatalog(Element parent)
    {
        Document doc = parent.getOwnerDocument();
        Element factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Dialog Fields");
        factoryElem.setAttribute("class", DialogFieldFactory.class.getName());
        for(Iterator i = fieldClasses.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("dialog-field");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((Class) entry.getValue()).getName());
            factoryElem.appendChild(childElem);
        }

        factoryElem = doc.createElement("factory");
        parent.appendChild(factoryElem);
        factoryElem.setAttribute("name", "Dialog Field Conditionals");
        factoryElem.setAttribute("class", DialogFieldFactory.class.getName());
        for(Iterator i = conditionalsClasses.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            Element childElem = doc.createElement("dialog-field-conditional");
            childElem.setAttribute("name", (String) entry.getKey());
            childElem.setAttribute("class", ((Class) entry.getValue()).getName());
            factoryElem.appendChild(childElem);
        }
    }

    public static DialogField createField(String fieldType)
    {
        Class fieldClass = (Class) fieldClasses.get(fieldType);
        if(fieldClass == null)
            return null;

        try
        {
            return (DialogField) fieldClass.newInstance();
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static DialogFieldConditionalAction createConditional(String action)
    {
        Class condClass = (Class) conditionalsClasses.get(action);
        if(condClass == null)
            return null;

        try
        {
            return (DialogFieldConditionalAction) condClass.newInstance();
        }
        catch(Exception e)
        {
            return null;
        }
    }
}