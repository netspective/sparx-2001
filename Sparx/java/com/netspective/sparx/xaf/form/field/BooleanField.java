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
 * $Id: BooleanField.java,v 1.3 2002-05-28 14:54:36 jruss Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogContextMemberInfo;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class BooleanField extends DialogField
{
    static public final int BOOLSTYLE_RADIO = 0;
    static public final int BOOLSTYLE_CHECK = 1;
    static public final int BOOLSTYLE_CHECKALONE = 2;
    static public final int BOOLSTYLE_COMBO = 3;

    static public final int CHOICES_YESNO = 0;
    static public final int CHOICES_TRUEFALSE = 1;
    static public final int CHOICES_ONOFF = 2;

    static public final String[] CHOICES_TEXT = new String[]{"No", "Yes", "False", "True", "Off", "On"};

    private int style = BOOLSTYLE_CHECK;
    private int choices = CHOICES_YESNO;
    private SingleValueSource trueText;
    private SingleValueSource falseText;
    private SingleValueSource noneText;

    public BooleanField()
    {
        super();
    }

    public BooleanField(String aName, String aCaption, int aStyle, int aChoices)
    {
        super(aName, aCaption);
        style = aStyle;
        choices = aChoices;
        falseText = null;
        trueText = null;
        noneText = null;
    }

    public final int getStyle()
    {
        return style;
    }

    public void setStyle(int value)
    {
        style = value;
    }

    public final int getChoices()
    {
        return choices;
    }

    public void setChoices(int value)
    {
        choices = value;
    }

    public String getCaption(DialogContext dc)
    {
        if(style == BOOLSTYLE_CHECK)
            return DialogField.CUSTOM_CAPTION;
        else
            return super.getCaption(dc);
    }

    public Object getValueAsObject(String value)
    {
        return new Boolean("1".equals(value) ? true : false);
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String styleValue = elem.getAttribute("style");
        if(styleValue.length() > 0)
        {
            if(styleValue.equalsIgnoreCase("radio"))
                style = BooleanField.BOOLSTYLE_RADIO;
            else if(styleValue.equalsIgnoreCase("checkbox"))
                style = BooleanField.BOOLSTYLE_CHECK;
            else if(styleValue.equalsIgnoreCase("combo"))
                style = BooleanField.BOOLSTYLE_COMBO;
            else if(styleValue.equalsIgnoreCase("checkalone"))
                style = BooleanField.BOOLSTYLE_CHECKALONE;
            else
                style = BooleanField.BOOLSTYLE_CHECK;
        }

        String choicesValue = elem.getAttribute("choices");
        if(choicesValue != null)
        {
            if(choicesValue.equalsIgnoreCase("yesno"))
                choices = BooleanField.CHOICES_YESNO;
            else if(choicesValue.equalsIgnoreCase("truefalse"))
                choices = BooleanField.CHOICES_TRUEFALSE;
            else if(choicesValue.equalsIgnoreCase("onoff"))
                choices = BooleanField.CHOICES_ONOFF;
            else
                choices = BooleanField.CHOICES_YESNO;

            String falseText = CHOICES_TEXT[(choices * 2) + 0];
            String trueText = CHOICES_TEXT[(choices * 2) + 1];
            String noneText = "None";

            this.falseText = ValueSourceFactory.getSingleOrStaticValueSource(falseText);
            this.trueText = ValueSourceFactory.getSingleOrStaticValueSource(trueText);
            this.noneText = ValueSourceFactory.getSingleOrStaticValueSource(noneText);
        }

        String falseText = elem.getAttribute("false");
        if(falseText.length() > 0)
            this.falseText = ValueSourceFactory.getSingleOrStaticValueSource(falseText);

        String trueText = elem.getAttribute("true");
        if(trueText.length() > 0)
            this.trueText = ValueSourceFactory.getSingleOrStaticValueSource(trueText);

        String allowNeither = elem.getAttribute("allow-neither");
        if(allowNeither.length() > 0 ) {
                this.noneText = ValueSourceFactory.getSingleOrStaticValueSource(allowNeither);
        } else {
            this.noneText = null ;
        }
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        if(dc.flagIsSet(getQualifiedName(), FLDFLAG_INPUT_HIDDEN))
        {
            writer.write(getHiddenControlHtml(dc));
            return;
        }

        boolean value = false;
        int index = -1;
        String strValue = dc.getValue(this);
        if ((strValue != null) && (strValue.length() > 0))
        {
            value = new Integer(strValue).intValue() == 0 ? false : true;
            index = new Integer(strValue).intValue();
        }
        String falseText = "";
        String trueText = "";
        String noneText = "";
        if(this.falseText != null)
            falseText = this.falseText.getValue(dc);
        else
            falseText = CHOICES_TEXT[(this.choices * 2) + 0];

        if(this.trueText != null)
            trueText = this.trueText.getValue(dc);
        else
            trueText = CHOICES_TEXT[(this.choices * 2) + 1];

        if(this.noneText != null)
            noneText = this.noneText.getValue(dc);
        else
            noneText = "None";

        if(isReadOnly(dc))
        {
            if (this.noneText == null) {
                writer.write("<input type='hidden' name='" + getId() + "' value='" + (strValue != null ? strValue : "") + "'><span id='" + getQualifiedName() + "'>" + (value ? trueText : falseText) + "</span>");
            } else {
                writer.write("<input type='hidden' name='" + getId() + "' value='" +
                        (strValue != null ? strValue : "") + "'><span id='" + getQualifiedName() + "'>" +
                        (index == 0 ? falseText : (index == 1 ? trueText : noneText)) +
                        "</span>");
            }
            return;
        }

        String id = getId();
        String defaultControlAttrs = dc.getSkin().getDefaultControlAttrs();
        switch(style)
        {
            case BOOLSTYLE_RADIO:
                if (this.noneText != null)
                {
                    String[] val = { "" , "" , "" };
                    setChecked (strValue, val);
                    writer.write(
                        "<nobr><input type='radio' name='" + id + "' id='" + id + "0' value='0' " + val[0] + defaultControlAttrs + "> <label for='" + id + "0'>" + falseText + "</label></nobr> " +
                        "<nobr><input type='radio' name='" + id + "' id='" + id + "1' value='1' " + val[1] + defaultControlAttrs + "> <label for='" + id + "1'>" + trueText + "</label></nobr> " +
                        "<nobr><input type='radio' name='" + id + "' id='" + id + "2' value='2' " + val[2] + defaultControlAttrs + "> <label for='" + id + "2'>" + noneText + "</label></nobr>");
                }
                else
                {
                    writer.write(
                        "<nobr><input type='radio' name='" + id + "' id='" + id + "0' value='0' " + (value ? "" : "checked ") + defaultControlAttrs + "> <label for='" + id + "0'>" + falseText + "</label></nobr> " +
                        "<nobr><input type='radio' name='" + id + "' id='" + id + "1' value='1' " + (value ? "checked " : "") + defaultControlAttrs + "> <label for='" + id + "1'>" + trueText + "</label></nobr>");
                }
                break;

            case BOOLSTYLE_CHECK:
                writer.write("<nobr><input type='checkbox' name='" + id + "' id='" + id + "' value='1' " + (value ? "checked " : "") + defaultControlAttrs + "> <label for='" + id + "'>" + super.getCaption(dc) + "</label></nobr>");
                break;

            case BOOLSTYLE_CHECKALONE:
                writer.write("<input type='checkbox' name='" + id + "' value='1' " + (value ? "checked " : "") + defaultControlAttrs + "> ");
                break;

            case BOOLSTYLE_COMBO:
                writer.write(
                        "<select name='" + id + "' " + defaultControlAttrs + ">" +
                        "<option " + (value ? "" : "selected") + " value='0'>" + falseText + "</option>" +
                        "<option " + (value ? "selected" : "") + " value='1'>" + trueText + "</option>" +
                        "</select>");
                break;

            default:
                writer.write("Unknown style " + style);
        }
    }

   /**
     * Produces Java code when a custom DialogContext is created
     */
    public DialogContextMemberInfo getDialogContextMemberInfo()
    {
        DialogContextMemberInfo mi = createDialogContextMemberInfo("boolean");
        String fieldName = mi.getFieldName();
        String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "() { Boolean o = (Boolean) getValueAsObject(\"" + fieldName + "\"); return o == null ? false : o.booleanValue(); }\n");
        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "(" + dataType + " defaultValue) { Boolean o = (Boolean) getValueAsObject(\"" + fieldName + "\"); return o == null ? defaultValue : o.booleanValue(); }\n");
        mi.addJavaCode("\tpublic void set" + memberName + "(" + dataType + " value) { setValue(\"" + fieldName + "\", value == true ? \"1\" : \"0\"); }\n");

        return mi;
    }

    private void setChecked (String strValue, String[] val)
    {
        int index;
        if (strValue != null)
        {
            try
            {
                index = Integer.parseInt (strValue);
                val[index] = " checked ";
            }
            catch (NumberFormatException e) { }
        }
    }
}
