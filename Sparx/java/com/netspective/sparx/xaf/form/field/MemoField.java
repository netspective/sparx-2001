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
 * $Id: MemoField.java,v 1.5 2003-05-23 14:19:02 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogContextMemberInfo;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.util.value.SingleValueSource;

public class MemoField extends DialogField
{
    static public final int WORDWRAP_SOFT = 0;
    static public final int WORDWRAP_HARD = 1;
    static public final String[] WORDWRAP_STYLES = new String[]{"soft", "hard"};

    protected int rows, cols;
    protected int wrap;
    private int maxLength;

    public MemoField()
    {
        super();
        rows = 3;
        cols = 40;
        maxLength = 2048;
        wrap = WORDWRAP_SOFT;
    }

    public MemoField(String aName, String aCaption, int aCols, int aRows, int length)
    {
        super(aName, aCaption);
        rows = aRows;
        cols = aCols;
        maxLength = length;
        wrap = WORDWRAP_SOFT;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public int getRows()
    {
        return rows;
    }

    public void setRows(int newRows)
    {
        rows = newRows;
    }

    public int getCols()
    {
        return cols;
    }

    public void setCols(int newCols)
    {
        cols = newCols;
    }

    public int getWordWrap()
    {
        return wrap;
    }

    public void setWordWrap(int value)
    {
        wrap = value;
    }


    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String value = elem.getAttribute("rows");
        if(value.length() != 0)
            rows = Integer.parseInt(value);

        value = elem.getAttribute("cols");
        if(value.length() != 0)
            cols = Integer.parseInt(value);

        value = elem.getAttribute("max-length");
        if(value.length() != 0)
            maxLength = Integer.parseInt(value);

        if(elem.getAttribute("wrap").equalsIgnoreCase("hard"))
            wrap = MemoField.WORDWRAP_HARD;
        else
            wrap = MemoField.WORDWRAP_SOFT;
    }

    public void populateValue(DialogContext dc, int formatType)
    {
        String value = dc.getValue(this);
        if(value == null)
            value = dc.getRequest().getParameter(getId());

        SingleValueSource defaultValue = getDefaultValue();
        if(dc.getRunSequence() == 1)
        {
            if((value != null && value.length() == 0 && defaultValue != null) ||
                    (value == null && defaultValue != null))
                value = defaultValue.getValueOrBlank(dc);
        }
        if(formatType == DialogField.DISPLAY_FORMAT)
            dc.setValue(this, this.formatDisplayValue(value));
        else if(formatType == DialogField.SUBMIT_FORMAT)
            dc.setValue(this, this.formatSubmitValue(value));

    }

    public boolean isValid(DialogContext dc)
    {
        String value = dc.getValue(this);
        if(isRequired(dc) && (value == null || value.length() == 0))
        {
            invalidate(dc, getCaption(dc) + " is required.");
            return false;
        }

        if(value != null && value.length() > maxLength)
        {
            invalidate(dc, getCaption(dc) + " is limited to " + maxLength + " characters.");
            return false;
        }
        return true;
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        if(isInputHidden(dc))
        {
            writer.write(getHiddenControlHtml(dc));
            return;
        }

        String value = dc.getValue(this);
        String id = getId();
        if(isReadOnly(dc))
        {
            String valueStr = value != null ? escapeHTML(value) : "";
            writer.write("<input type='hidden' name='" + id + "' value=\"" + valueStr + "\">" + valueStr);
        }
        else
        {
            writer.write(
                    "<textarea title=\"" + getTitle() + "\" maxlength=\"" + maxLength + "\" name=\"" + id + "\" rows=\"" + rows + "\" cols=\"" + cols + "\" wrap=\"" +
                    WORDWRAP_STYLES[wrap] + "\"" + (isRequired(dc) ? " class=\"" + dc.getSkin().getControlAreaRequiredStyleClass()+ "\" " : " ") +
                    dc.getSkin().getDefaultControlAttrs() +
                    ">" + (value != null ? escapeHTML(value) : "") + "</textarea>");
        }
    }

    /**
     *
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        return (super.getCustomJavaScriptDefn(dc) + "field.maxLength = " + this.getMaxLength() + ";\n");
    }

    /**
     * Produces Java code when a custom DialogContext is created
     */
    public DialogContextMemberInfo getDialogContextMemberInfo()
    {
        DialogContextMemberInfo mi = createDialogContextMemberInfo("String");
        String fieldName = mi.getFieldName();
        String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "() { return getValue(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "(" + dataType + " defaultValue) { return getValue(\"" + fieldName + "\", defaultValue); }\n");
        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "OrBlank() { return getValue(\"" + fieldName + "\", \"\"); }\n");
        mi.addJavaCode("\tpublic void set" + memberName + "(" + dataType + " value) { setValue(\"" + fieldName + "\", value); }\n");

        return mi;
    }
}
