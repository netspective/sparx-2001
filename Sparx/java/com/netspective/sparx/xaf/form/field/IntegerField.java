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
 * $Id: IntegerField.java,v 1.5 2002-12-24 15:39:57 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xaf.form.field;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogContextMemberInfo;

public class IntegerField extends TextField
{
    private int minValue = java.lang.Integer.MIN_VALUE;
    private int maxValue = java.lang.Integer.MAX_VALUE;

    public IntegerField()
    {
        super();
        setSize(10);
    }

    public IntegerField(String aName, String aCaption)
    {
        super(aName, aCaption);
        setSize(10);
    }

    public final int getMinValue()
    {
        return minValue;
    }

    public void setMinValue(int value)
    {
        minValue = value;
    }

    public final int getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(int value)
    {
        maxValue = value;
    }

    public void setMinMaxValue(int low, int high)
    {
        minValue = low;
        maxValue = high;
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String value = elem.getAttribute("min");
        if(value.length() != 0)
            minValue = Integer.parseInt(value);

        value = elem.getAttribute("max");
        if(value.length() != 0)
            maxValue = Integer.parseInt(value);
    }

    public boolean needsValidation(DialogContext dc)
    {
        return true;
    }

    public Object getValueAsObject(String value)
    {
        Integer result = null;

        try
        {
            result = new Integer(value);
        }
        catch(NumberFormatException e)
        {
            result = null;
        }
        return result;
    }

    public boolean isValid(DialogContext dc)
    {
        boolean textValid = super.isValid(dc);
        if(textValid)
        {
            String strValue = dc.getValue(this);
            if(!isRequired(dc) && (strValue == null || strValue.length() == 0))
                return true;

            Integer value = null;
            try
            {
                value = new Integer(strValue);
            }
            catch(Exception e)
            {
                invalidate(dc, "'" + strValue + "' is not a valid integer.");
                return false;
            }
            if(value.intValue() < minValue || value.intValue() > maxValue)
            {
                invalidate(dc, getCaption(dc) + " needs to be between " + minValue + " and " + maxValue + ".");
                return false;
            }
            return true;
        }
        else
            return false;
    }

    /**
     * Produces Java code when a custom DialogContext is created
     */
    public DialogContextMemberInfo getDialogContextMemberInfo()
    {
        DialogContextMemberInfo mi = createDialogContextMemberInfo("int");
        String fieldName = mi.getFieldName();
        String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "() { Integer o = (Integer) getValueAsObject(\"" + fieldName + "\"); return o == null ? 0 : o.intValue(); }\n");
        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "(" + dataType + " defaultValue) { Integer o = (Integer) getValueAsObject(\"" + fieldName + "\"); return o == null ? defaultValue : o.intValue(); }\n");

        mi.addJavaCode("\tpublic String get" + memberName + "String() { Integer o = (Integer) getValueAsObject(\"" + fieldName + "\"); return o == null ? \"0\" : o.toString(); }\n");
        mi.addJavaCode("\tpublic String get" + memberName + "String(String defaultValue) { Integer o = (Integer) getValueAsObject(\"" + fieldName + "\"); return o == null ? defaultValue : o.toString(); }\n");

        mi.addJavaCode("\tpublic Object get" + memberName + "Object() { return getValueAsObject(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic Object get" + memberName + "Object(Object defaultValue) { return getValueAsObject(\"" + fieldName + "\", defaultValue); }\n");

        mi.addJavaCode("\tpublic void set" + memberName + "(" + dataType + " value) { setValue(\"" + fieldName + "\", Integer.toString(value)); }\n");
        mi.addJavaCode("\tpublic void set" + memberName + "(String value) { setValue(\"" + fieldName + "\", value); }\n");
        mi.addJavaCode("\tpublic void set" + memberName + "Object(Object value) { setValue(\"" + fieldName + "\", value != null ? ((Integer) value).toString() : null); }\n");

        return mi;
    }
}
