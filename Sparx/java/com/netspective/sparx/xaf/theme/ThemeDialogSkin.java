package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.field.SeparatorField;
import com.netspective.sparx.util.config.Configuration;
import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Iterator;

/**
 * @author Aye Thu
 * Created on Feb 16, 2003 6:58:46 PM
 */
public class ThemeDialogSkin extends com.netspective.sparx.xaf.skin.StandardDialogSkin
{
    private static String themeResourcesBasePath;
    private String controlAreaClass;
    private String captionClass;

    public ThemeDialogSkin()
    {
        super();
        // default theme
        themeResourcesBasePath = "/sparx/resources/theme/sparx";

        outerTableAttrs = "border=\"0\" cellspacing=\"0\" cellpadding=\"0\" nowrap";
        controlAttrs = " onfocus='controlOnFocus(this, event)' onchange='controlOnChange(this, event)' " +
                "onblur='controlOnBlur(this, event)' onkeypress='controlOnKeypress(this, event)' onclick='controlOnClick(this, event) '";
        controlAreaClass = " class=\"dialog-entry\" ";
        captionClass = " class=\"dialog-fields\"";
    }

    public String getControlAreaClass()
    {
        return controlAreaStyleAttrs;
    }

    public void renderHtml(Writer writer, DialogContext dc) throws IOException
    {
        super.renderHtml(writer, dc);
    }

    public void appendFieldHtml(DialogContext dc, DialogField field, StringBuffer fieldsHtml, StringBuffer fieldsJSDefn, List fieldErrorMsgs) throws IOException
    {
        if(field.isInputHidden(dc))
        {
            StringWriter writer = new StringWriter();
            field.renderControlHtml(writer, dc);
            fieldsHtml.append(writer);
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
        String popupHtml = getPopupHtml(dc, field);
        if(popupHtml != null)
            controlHtml.write(popupHtml);

        if(field.flagIsSet(DialogField.FLDFLAG_CREATEADJACENTAREA))
        {
            String adjValue = dc.getAdjacentAreaValue(field);
            controlHtml.write("&nbsp;<span id='" + field.getQualifiedName() + "_adjacent'>"+ (adjValue != null ? adjValue : "") +"</span>");
        }

        StringBuffer messagesHtml = new StringBuffer();
        String hint = field.getHint(dc);
        if(hint != null && !(field.isReadOnly(dc) && dc.getDialog().flagIsSet(Dialog.DLGFLAG_HIDE_READONLY_HINTS)))
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
                    int msgNum = fieldErrorMsgs.size();
                    String msgStr = (String) emi.next();
                    fieldErrorMsgs.add(msgStr);
                    messagesHtml.append("<br><a name='dc_error_msg_" + msgNum + "'>" + msgStr + "</a>");
                }
                messagesHtml.append("</font>");
                haveErrors = true;
            }
        }

        /*
		 * each field row gets its own ID so DHTML can hide/show the row
		 */

        String rowAttr = fieldRowAttrs + " id='" + FIELDROW_PREFIX + field.getQualifiedName() + "' ";
        if(haveErrors)
            rowAttr = rowAttr + fieldRowErrorAttrs;

        if(caption == null)
        {
            fieldsHtml.append("<tr" + rowAttr + "><td colspan='2'><font " + controlAreaFontAttrs + ">" + controlHtml + messagesHtml + "</font></td></tr>\n");
        }
        else
        {
            fieldsHtml.append(
                    "<tr><td " + captionClass + ">" + caption + "</td>" +
                    "<td "+ controlAreaClass + " width='100%'>" + controlHtml + messagesHtml + "</td></tr>\n");
        }

        if(field.getSimpleName() != null)
            fieldsJSDefn.append(field.getJavaScriptDefn(dc));
    }

    public void renderContentsHtml(Writer writer, DialogContext dc, Configuration appConfig, String dialogName, String actionURL, String encType, String heading, int dlgTableColSpan, StringBuffer errorMsgsHtml, StringBuffer fieldsHtml) throws IOException
    {
        writer.write("<table " + outerTableAttrs + ">\n");
        if(heading != null && ! dc.getDialog().hideHeading(dc))
        {
            writer.write("  <tr>\n");
            writer.write("      <td class=\"panel-input\">\n");
            renderTab(writer, dc, heading);
            writer.write("      </td>\n");
            writer.write("  </tr>\n");
        }

        //writer.write(
        //        "<table " + innerTableAttrs + ">\n<tr><td>\n");
        writer.write("  <tr>\n");
        writer.write("      <td class=\"panel-content-input\">\n");
        writer.write("          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
        if(summarizeErrors)
            writer.write(errorMsgsHtml.toString());

        writer.write(
                "<form id='" + dialogName + "' name='" + dialogName + "' action='" + actionURL + "' method='post' " +
                encType + " onsubmit='return(activeDialog.isValid())'>\n" +
                dc.getStateHiddens() + "\n" +
                fieldsHtml +
                "</form>\n");

        writer.write("          </table>\n");
        writer.write("      </td>\n" +
                "   </tr>\n</table>");
    }

    /**
     * Render the separator field
     * @param writer
     * @param dc
     * @param field
     * @throws IOException
     */
    public void renderSeparatorHtml(Writer writer, DialogContext dc, SeparatorField field) throws IOException
    {
        String heading = field.getHeading();

        if(heading != null)
        {
            String sep = "<font class=\"dialog-fields-header\" " + separatorFontAttrs + "><a name=\"" +
                    URLEncoder.encode(heading) + "\"><b>" + heading + "</b></a></font>";
            if(field.getBannerText() != null)
            {
                sep += "<br><font " + separatorBannerTextFontAttrs + ">";
                sep += field.getBannerText();
                sep += "</font>";
            }
            if(!field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE))
                sep += separatorHtml;

            if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
                writer.write(sep);
            else
                writer.write("<br>" + sep);
        }
        else
        {
            if(! field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
                writer.write(field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE) ? "<br>" : "<hr size=1 color=silver>");
        }
    }



    /**
     * Render the dialog heading
     * @param writer
     * @param dc
     * @param heading
     * @throws IOException
     */
    public void renderTab(Writer writer, DialogContext dc, String heading) throws IOException
    {
        ThemeFactory tf = ThemeFactory.getInstance(dc);
        Theme theme = tf.getCurrentTheme();
        if (theme == null)
            throw new RuntimeException("There is no default theme defined. ");
        String imgPath = ((HttpServletRequest)dc.getRequest()).getContextPath() + theme.getCurrentStyle().getImagePath();

        writer.write("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" nowrap>\n");
        writer.write("    <tr>\n");
        writer.write("        <td class=\"panel-frame-heading-action-left-blank-input\" align=\"left\" valign=\"middle\" nowrap width=\"17\">" +
                "<img src=\""+ imgPath + "/panel/input/spacer.gif\" alt=\"\" height=\"5\" width=\"17\" border=\"0\"></td>\n");
        writer.write("        <td class=\"panel-frame-heading-input\" align=\"left\" valign=\"middle\" nowrap>" +
                heading + "</td>\n");
        writer.write("        <td class=\"panel-frame-heading-action-right-blank-input\" align=\"center\" valign=\"middle\" nowrap width=\"17\">" +
                "<img src=\""+ imgPath + "/panel/input/spacer.gif\" alt=\"\" height=\"5\" width=\"17\" border=\"0\"></td>\n");
        writer.write("        <td class=\"panel-frame-mid-input\" align=\"right\" valign=\"top\" nowrap width=\"100%\">" +
                "<img src=\""+ imgPath + "/panel/input/spacer.gif\" alt=\"\" height=\"5\" width=\"100%\" border=\"0\"></td>\n");
        writer.write("        <td class=\"panel-frame-end-cap-input\" align=\"right\" valign=\"top\" nowrap width=\"2\"></td>\n");
        writer.write("    </tr>\n");
        writer.write("</table>\n");
    }
}
