package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.field.SeparatorField;
import com.netspective.sparx.xaf.skin.SkinFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Aye Thu
 * Created on Feb 16, 2003 6:58:46 PM
 */
public class ThemeDialogSkin extends com.netspective.sparx.xaf.skin.StandardDialogSkin
{
    private String controlAreaClass;
    private String captionClass;
    private boolean frameVisible;

    public ThemeDialogSkin()
    {
        super();
        outerTableAttrs = "border=\"0\" cellspacing=\"0\" cellpadding=\"0\" nowrap";
        controlAttrs = " onfocus='controlOnFocus(this, event)' onchange='controlOnChange(this, event)' " +
                "onblur='controlOnBlur(this, event)' onkeypress='controlOnKeypress(this, event)' onclick='controlOnClick(this, event) '";
        controlAreaClass = " class=\"dialog-entry\" ";
        captionClass = " class=\"dialog-fields\"";
        hintFontAttrs = " class=\"dialog-fields-hint\" ";
        separatorHtml = "";
        frameVisible = true;
        captionSuffix = " ";
    }

    /**
     * Check to see if the dialog frame with the heading should be displayed or not
     * @return
     */
    public boolean isFrameVisible()
    {
        return frameVisible;
    }

    public void setFrameVisible(boolean frameVisible)
    {
        this.frameVisible = frameVisible;
    }

    public void renderHtml(Writer writer, DialogContext dc) throws IOException
    {
        long startTime = new Date().getTime();

        List fieldErrorMsgs = new ArrayList();
        List dlgErrorMsgs = dc.getErrorMessages();
        if(dlgErrorMsgs != null)
            fieldErrorMsgs.addAll(dlgErrorMsgs);

        Dialog dialog = dc.getDialog();

        int layoutColumnsCount = dialog.getLayoutColumnsCount();
        int dlgTableColSpan = 2;

        StringBuffer fieldsHtml = new StringBuffer();
        StringBuffer fieldsJSDefn = new StringBuffer();

        DialogDirector director = dialog.getDirector();
        if(layoutColumnsCount == 1)
        {
            Iterator i = dc.getDialog().getFields().iterator();
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                if(!field.isVisible(dc))
                    continue;

                appendFieldHtml(dc, field, fieldsHtml, fieldsJSDefn, fieldErrorMsgs);
            }

            if(director != null && director.isVisible(dc) && dc.getDataCommand() != DialogContext.DATA_CMD_PRINT)
            {
                fieldsHtml.append("<tr><td class=\"dialog-button-table\" colspan='2'>");
                StringWriter directorHtml = new StringWriter();
                director.renderControlHtml(directorHtml, dc);
                fieldsHtml.append(directorHtml);
                fieldsHtml.append("</td></tr>");
            }
        }
        else
        {
            StringBuffer[] layoutColsFieldsHtml = new StringBuffer[layoutColumnsCount];
            for(int i = 0; i < layoutColumnsCount; i++)
                layoutColsFieldsHtml[i] = new StringBuffer();

            int activeColumn = 0;

            Iterator i = dc.getDialog().getFields().iterator();
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                if(!field.isVisible(dc))
                    continue;

                if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
                    activeColumn++;
                appendFieldHtml(dc, field, layoutColsFieldsHtml[activeColumn], fieldsJSDefn, fieldErrorMsgs);
                if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_AFTER))
                    activeColumn++;
            }

            int lastColumn = layoutColumnsCount - 1;
            int cellWidth = 100 / layoutColumnsCount;
            dlgTableColSpan = 0;

            fieldsHtml.append("<tr valign='top'>");
            for(int c = 0; c < layoutColumnsCount; c++)
            {

                fieldsHtml.append("<td width='" + cellWidth + "%'><table width='100%'>");
                fieldsHtml.append(layoutColsFieldsHtml[c]);
                fieldsHtml.append("</table></td>");
                dlgTableColSpan++;

                if(c < lastColumn)
                {
                    fieldsHtml.append("<td>&nbsp;&nbsp;</td>");
                    dlgTableColSpan++;
                }
            }
            fieldsHtml.append("</tr>");

            if(director != null && director.isVisible(dc) && dc.getDataCommand() != DialogContext.DATA_CMD_PRINT)
            {
                fieldsHtml.append("<tr><td class=\"dialog-button-table\" colspan='" + dlgTableColSpan + "'>");
                StringWriter directorHtml = new StringWriter();
                director.renderControlHtml(directorHtml, dc);
                fieldsHtml.append(directorHtml);
                fieldsHtml.append("</td></tr>");
            }
        }

        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(dc.getServletContext());
        String sharedScriptsUrl = appConfig.getTextValue(dc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "scripts-url");

        StringBuffer errorMsgsHtml = new StringBuffer();
        if(fieldErrorMsgs.size() > 0)
        {
            errorMsgsHtml.append("<tr><td colspan='" + dlgTableColSpan + "'><ul type=square><font " + controlAreaFontAttrs + "><font " + errorMsgHdFontAttrs + "><b>" + errorMsgHdText + "</b></font>\n");
            for(int i = 0; i < fieldErrorMsgs.size(); i++)
            {
                String errorMsg = (String) fieldErrorMsgs.get(i);
                errorMsgsHtml.append("<li><a href='#dc_error_msg_" + i + "' style='text-decoration:none'><font " + errorMsgFontAttrs + ">" + errorMsg + "</font></a></li>\n");
            }
            errorMsgsHtml.append("</ul></td></tr>\n");
        }
        String dialogIncludeJS = (dialog.getIncludeJSFile() != null ? dialog.getIncludeJSFile().getValue(dc) : null);

        if(includePreStyleSheets != null)
            writer.write(includePreStyleSheets);
        writer.write("<link rel='stylesheet' href='" + appConfig.getTextValue(dc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "css-url") + "/dialog.css'>\n");
        if(includePostStyleSheets != null)
            writer.write(includePostStyleSheets);
        if(prependPreScript != null)
            writer.write(prependPreScript);
        writer.write(
            "<script language='JavaScript'>var _version = 1.0;</script>\n" +
            "<script language='JavaScript1.1'>_version = 1.1;</script>\n" +
            "<script language='JavaScript1.2'>_version = 1.2;</script>\n" +
            "<script language='JavaScript1.3'>_version = 1.3;</script>\n" +
            "<script language='JavaScript1.4'>_version = 1.4;</script>\n");
        if(includePreScripts != null)
            writer.write(includePreScripts);

        writer.write("<script src='" + sharedScriptsUrl + "/popup.js' language='JavaScript1.1'></script>\n");
        writer.write("<script src='" + sharedScriptsUrl + "/dialog.js' language='JavaScript1.2'></script>\n");

        writer.write(
                "<script language='JavaScript'>\n" +
                "	if(typeof dialogLibraryLoaded == 'undefined')\n" +
                "	{\n" +
                "		alert('ERROR: " + sharedScriptsUrl + "/dialog.js could not be loaded');\n" +
                "	}\n" +
                "</script>\n");

        if(dialogIncludeJS != null)
            writer.write("<script language='JavaScript' src='" + dialogIncludeJS + "'></script>\n");
        if(includePostScripts != null)
            writer.write(includePostScripts);
        if(prependPostScript != null)
            writer.write(prependPostScript);

        String dialogName = dialog.getName();

        String encType = dialog.flagIsSet(Dialog.DLGFLAG_ENCTYPE_MULTIPART_FORMDATA) ? "enctype=\"multipart/form-data\"" : "";
        String heading = null;
        SingleValueSource headingVS = dialog.getHeading();
        if(headingVS != null)
            heading = headingVS.getValue(dc);

        String actionURL = null;
        if(director != null)
            actionURL = director.getSubmitActionUrl() != null ? director.getSubmitActionUrl().getValue(dc) : null;

        if(actionURL == null)
            actionURL = ((HttpServletRequest) dc.getRequest()).getRequestURI();

        renderContentsHtml(writer, dc, appConfig, dialogName, actionURL, encType, heading, dlgTableColSpan, errorMsgsHtml, fieldsHtml);

        if(appendPreScript != null)
            writer.write(appendPreScript);

        writer.write(
                "<script language='JavaScript'>\n" +
                "       var " + dialogName + " = new Dialog(\"" + dialogName + "\");\n" +
                "       var dialog = " + dialogName + "; setActiveDialog(dialog);\n" +
                "       var field;\n" +
                fieldsJSDefn +
                "       dialog.finalizeContents();\n" +
                "</script>\n");

        if(appendPostScript != null)
            writer.write(appendPostScript);

        LogManager.recordAccess((HttpServletRequest) dc.getRequest(), null, this.getClass().getName(), dc.getLogId(), startTime);

    }

    /**
     * Generate the dialog field html
     * @param dc
     * @param field
     * @param fieldsHtml
     * @param fieldsJSDefn
     * @param fieldErrorMsgs
     * @throws IOException
     */
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
            messagesHtml.append(hint);
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
            if (field instanceof SeparatorField)

                fieldsHtml.append("<tr" + rowAttr + "><td class=\"dialog-fields-separator\" colspan='2'>" + controlHtml + "</td></tr>\n");
            else
                fieldsHtml.append("<tr" + rowAttr + "><td colspan='2'>" + controlHtml + "</td></tr>\n");
            if (messagesHtml != null && messagesHtml.length() > 0)
            {
                fieldsHtml.append("<tr><td class=\"dialog-fields-hint-table\" align=\"left\" valign=\"top\" nowrap colspan=\"2\" width=\"50%\">" +
                    "<span class=\"dialog-fields-hint\">&nbsp;&nbsp;&nbsp;"+  messagesHtml + "</span></td></tr>\n");
            }
        }
        else
        {
            fieldsHtml.append(
                    "<tr><td " + captionClass + ">" + caption + "</td>" +
                    "<td "+ controlAreaClass + " width='100%'>" + controlHtml + "</td></tr>\n");
            if (messagesHtml != null && messagesHtml.length() > 0)
            {
                fieldsHtml.append("<tr><td>&nbsp;</td><td class=\"dialog-fields-hint-table\" align=\"left\" valign=\"top\" nowrap width=\"50%\">" +
                    "<span class=\"dialog-fields-hint\">&nbsp;&nbsp;&nbsp;"+  messagesHtml + "</span></td></tr>\n");
            }
        }

        if(field.getSimpleName() != null)
            fieldsJSDefn.append(field.getJavaScriptDefn(dc));
    }

    public void renderContentsHtml(Writer writer, DialogContext dc, Configuration appConfig, String dialogName, String actionURL, String encType, String heading, int dlgTableColSpan, StringBuffer errorMsgsHtml, StringBuffer fieldsHtml) throws IOException
    {
        writer.write("<table " + outerTableAttrs + ">\n");
        if((heading != null && ! dc.getDialog().hideHeading(dc)) && isFrameVisible())
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
            String sep = "<a name=\"" + URLEncoder.encode(heading) + "\">" + heading + "</a>";
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
                writer.write(field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE) ? "<br>" : "");
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
        Theme theme = SkinFactory.getInstance().getCurrentTheme(dc);
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
