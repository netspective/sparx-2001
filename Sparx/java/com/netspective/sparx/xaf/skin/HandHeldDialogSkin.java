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
 * $Id: HandHeldDialogSkin.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.form.field.GridField;
import com.netspective.sparx.xaf.form.field.SeparatorField;
import com.netspective.sparx.util.value.SingleValueSource;

public class HandHeldDialogSkin implements DialogSkin
{
    protected String outerTableAttrs;
    protected String innerTableAttrs;
    protected String frameHdRowAlign;
    protected String frameHdRowAttrs;
    protected String frameHdFontAttrs;
    protected String fieldRowAttrs;
    protected String fieldRowErrorAttrs;
    protected String captionCellAttrs;
    protected String captionFontAttrs;
    protected String controlAreaFontAttrs;
    protected String controlAreaStyleAttrs;
    protected String controlAttrs;
    protected String separatorFontAttrs;
    protected String separatorHtml;
    protected String hintFontAttrs;
    protected String errorMsgFontAttrs;
    protected String captionSuffix;

    public HandHeldDialogSkin()
    {
        outerTableAttrs = "cellspacing='1' cellpadding='0' bgcolor='#6699CC' ";
        innerTableAttrs = "cellspacing='0' cellpadding='4' bgcolor='lightyellow' ";
        frameHdRowAlign = "LEFT";
        frameHdRowAttrs = "bgcolor='#6699CC' ";
        frameHdFontAttrs = "face='verdana,arial,helvetica' size=2 color='white' ";
        fieldRowAttrs = "";
        fieldRowErrorAttrs = "bgcolor='beige' ";
        captionCellAttrs = "align='right' ";
        captionFontAttrs = "";
        controlAreaFontAttrs = "";
        controlAreaStyleAttrs = "";
        controlAttrs = "";
        separatorFontAttrs = "";
        separatorHtml = "<hr size=1 color=#555555>";
        hintFontAttrs = "color='navy'";
        errorMsgFontAttrs = "color='red'";
        captionSuffix = ": ";
    }

    public void importFromXml(Element elem)
    {
        throw new RuntimeException("Not implemented yet.");
    }

    public final String getControlAreaFontAttrs()
    {
        return controlAreaFontAttrs;
    }

    public String getControlAreaStyleAttrs()
    {
        return controlAreaStyleAttrs;
    }

    public final String getDefaultControlAttrs()
    {
        return controlAttrs;
    }

    public void renderCompositeControlsHtml(Writer writer, DialogContext dc, DialogField parentField) throws IOException
    {
        Iterator i = parentField.getChildren().iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc))
            {
                boolean showCaption = field.showCaptionAsChild();
                if(showCaption)
                {
                    String caption = field.getCaption(dc);
                    if(caption != DialogField.CUSTOM_CAPTION)
                    {
                        writer.write("<nobr>" + (field.isRequired(dc) ? "<b>" + caption + "</b>" : caption));
                        if(captionSuffix != null)
                            writer.write(captionSuffix);
                    }
                }
                field.renderControlHtml(writer, dc);
                writer.write("&nbsp;");
                if(showCaption) writer.write("</nobr>");
            }
        }
    }

    public void renderGridControlsHtml(Writer writer, DialogContext dc, GridField gridField) throws IOException
    {
        // grids are not supported
    }

    public void appendFieldHtml(DialogContext dc, DialogField field, StringBuffer fieldsHtml) throws IOException
    {
        if(field.flagIsSet(DialogField.FLDFLAG_INPUT_HIDDEN))
        {
            StringWriter fieldHtml = new StringWriter();
            field.renderControlHtml(fieldHtml, dc);
            fieldsHtml.append(fieldHtml);
            return;
        }

        String name = field.getQualifiedName();
        String caption = field.getCaption(dc);
        List fieldChildren = field.getChildren();
        if(caption != null && fieldChildren != null && caption.equals(DialogField.GENERATE_CAPTION))
        {
            StringBuffer generated = new StringBuffer();
            Iterator c = fieldChildren.iterator();
            while(c.hasNext())
            {
                DialogField childField = (DialogField) c.next();
                String childCaption = childField.getCaption(dc);
                if(childCaption != null && childCaption != DialogField.CUSTOM_CAPTION)
                {
                    if(generated.length() > 0)
                        generated.append(" / ");
                    generated.append(childField.isRequired(dc) ? "<b>" + childCaption + "</b>" : childCaption);
                }
            }
            caption = generated.toString();
        }
        else
        {
            if(caption != null && field.isRequired(dc))
                caption = "<b>" + caption + "</b>";
        }

        if(captionSuffix != null && caption != null && caption.length() > 0) caption += captionSuffix;

        StringWriter controlHtml = new StringWriter();
        field.renderControlHtml(controlHtml, dc);

        StringBuffer messagesHtml = new StringBuffer();
        String hint = field.getHint();
        if(hint != null)
        {
            messagesHtml.append("<br><font " + hintFontAttrs + ">");
            messagesHtml.append(hint);
            messagesHtml.append("</font>");
        }
        boolean haveErrors = false;
        if(name != null)
        {
            List errorMessages = dc.getErrorMessages(field);
            if(errorMessages != null)
            {
                messagesHtml.append("<font " + errorMsgFontAttrs + ">");
                Iterator emi = errorMessages.iterator();
                while(emi.hasNext())
                {
                    messagesHtml.append("<br>" + (String) emi.next());
                }
                messagesHtml.append("</font>");
                haveErrors = true;
            }
        }

        String rowAttr = fieldRowAttrs;
        if(haveErrors)
            rowAttr = rowAttr + fieldRowErrorAttrs;

        if(caption == null)
        {
            fieldsHtml.append("<tr" + rowAttr + "><td colspan='2'><font " + controlAreaFontAttrs + ">" + controlHtml + messagesHtml + "</font></td></tr>\n");
        }
        else
        {
            fieldsHtml.append(
                    "<tr " + rowAttr + "><td " + captionCellAttrs + "><font " + captionFontAttrs + ">" + caption + "</font></td>" +
                    "<td><font " + controlAreaFontAttrs + ">" + controlHtml + messagesHtml + "</font></td></tr>\n");
        }
    }

    public void renderHtml(Writer writer, DialogContext dc) throws IOException
    {
        Dialog dialog = dc.getDialog();
        String dialogName = dialog.getName();

        StringBuffer fieldsHtml = new StringBuffer();

        DialogDirector director = dialog.getDirector();

        Iterator i = dc.getDialog().getFields().iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(!field.isVisible(dc))
                continue;

            appendFieldHtml(dc, field, fieldsHtml);
        }

        if(director != null && director.isVisible(dc))
            appendFieldHtml(dc, director, fieldsHtml);

        String heading = null;
        SingleValueSource headingVS = dialog.getHeading();
        if(headingVS != null)
            heading = headingVS.getValue(dc);

        String actionURL = null;
        if(director != null)
            actionURL = director.getSubmitActionUrl() != null ?director.getSubmitActionUrl().getValue(dc) : null;

        if(actionURL == null)
            actionURL = ((HttpServletRequest) dc.getRequest()).getRequestURI();

        writer.write(
                "<table " + outerTableAttrs + ">\n" +
                "<tr><td><table " + innerTableAttrs + ">" +
                (heading == null ? "" :
                "<tr " + frameHdRowAttrs + "><td colspan='2' align='" + frameHdRowAlign + "'><font " + frameHdFontAttrs + "><b>" + heading + "</b></font></td></tr>\n") +
                "<form name='" + dialogName + "' action='" + actionURL + "' method='post' onsubmit='return(activeDialog.isValid())'>\n" +
                dc.getStateHiddens() + "\n" +
                fieldsHtml +
                "</form>\n" +
                "</table></td></tr></table>\n");
    }

    public void renderSeparatorHtml(Writer writer, DialogContext dc, SeparatorField field) throws IOException
    {
        String heading = field.getHeading();
        if(heading != null)
        {
            String sep = "<font " + separatorFontAttrs + "><b>" + heading + "</b></font>";
            if(!field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE))
                sep += separatorHtml;

            if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
                writer.write(sep);
            else
                writer.write("<br>" + sep);
        }
        else
        {
            if(!field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
                writer.write(field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE) ? "<br>" : "<hr size=1 color=silver>");
        }
    }
}