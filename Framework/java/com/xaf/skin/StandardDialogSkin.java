package com.xaf.skin;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class StandardDialogSkin implements DialogSkin
{
	public final String FIELDROW_PREFIX = "_dfr.";
	public final String GRIDHEADROW_PREFIX = "_dghr.";
	public final String GRIDFIELDROW_PREFIX = "_dgfr.";
	public final String EMPTY = "";

	protected String outerTableAttrs;
	protected String innerTableAttrs;
	protected String frameHdRowAlign;
	protected String frameHdRowAttrs;
	protected String frameHdFontAttrs;
    protected String errorMsgHdFontAttrs;
    protected String errorMsgHdText;
	protected String fieldRowAttrs;
	protected String fieldRowErrorAttrs;
	protected String gridCaptionFontAttrs;      // grid column font attributes
    protected String gridRowCaptionFontAttrs;   // grid row font attributes
	protected String captionCellAttrs;
	protected String captionFontAttrs;
	protected String controlAreaFontAttrs;
	protected String controlAttrs;
	protected String separatorFontAttrs;
	protected String separatorHtml;
    protected String hintFontAttrs;
    protected String errorMsgFontAttrs;
    protected String captionSuffix;
	protected String includePreScripts;
	protected String includePostScripts;
	protected String includePreStyleSheets;
	protected String includePostStyleSheets;
	protected String prependPreScript;
	protected String prependPostScript;
	protected String appendPreScript;
	protected String appendPostScript;

	public StandardDialogSkin()
	{
		outerTableAttrs = "cellspacing='1' cellpadding='0' bgcolor='#6699CC' ";
		innerTableAttrs = "cellspacing='0' cellpadding='4' bgcolor='lightyellow' ";
		frameHdRowAlign = "LEFT";
		frameHdRowAttrs = "bgcolor='#6699CC' ";
		frameHdFontAttrs = "face='verdana,arial,helvetica' size=2 color='yellow' ";
        errorMsgHdFontAttrs = "face='verdana,arial,helvetica' size=2 color='darkred'";
        errorMsgHdText = "Please review the following:";
		fieldRowAttrs = "";
		fieldRowErrorAttrs = "bgcolor='beige' ";
		captionCellAttrs = "align='right' ";
		captionFontAttrs = "size='2' face='tahoma,arial,helvetica' style='font-size:8pt' ";
		gridCaptionFontAttrs = "size='2' face='tahoma,arial,helvetica' color='navy' style='font-size:9pt' ";
        gridRowCaptionFontAttrs = "size='2' face='tahoma,arial,helvetica' color='navy' style='font-size:9pt' ";
		controlAreaFontAttrs = "size='2' face='tahoma,arial,helvetica' style='font-size:8pt' ";
		controlAttrs = " class='dialog_control' onfocus='controlOnFocus(this, event)' onchange='controlOnChange(this, event)' " +
            "onblur='controlOnBlur(this, event)' onkeypress='controlOnKeypress(this, event)' onclick='controlOnClick(this, event) '";
		separatorFontAttrs = "face='verdana,arial' size=2 color=#555555";
		separatorHtml = "<hr size=1 color=#555555>";
        hintFontAttrs = "color='navy'";
        errorMsgFontAttrs = "color='red'";
        captionSuffix = ": ";
		includePreScripts = null;
		includePostScripts = null;
		includePreStyleSheets = null;
		includePostStyleSheets = null;
		prependPreScript = null;
		prependPostScript = null;
		appendPreScript = null;
		appendPostScript = null;
	}

	public void importFromXml(Element elem)
	{
		NodeList children = elem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

            String nodeName = node.getNodeName();
			Element nodeElem = (Element) node;
			Node firstChild = node.getFirstChild();
			String nodeText = firstChild != null ? firstChild.getNodeValue() : null;

			if(nodeName.equals("outer-table-attrs") && nodeText != null)
				outerTableAttrs = nodeText;
			else if(nodeName.equals("inner-table-attrs") && nodeText != null)
				innerTableAttrs = nodeText;
			else if(nodeName.equals("frame-head-row-align") && nodeText != null)
				frameHdRowAlign = nodeText;
			else if(nodeName.equals("frame-head-row-attrs") && nodeText != null)
				frameHdRowAttrs = nodeText;
			else if(nodeName.equals("frame-head-font-attrs") && nodeText != null)
				frameHdFontAttrs = nodeText;
            else if(nodeName.equals("error-msg-hd-font-attrs") && nodeText != null)
                errorMsgHdFontAttrs = nodeText;
            else if(nodeName.equals("error-msg-hd-text") && nodeText != null)
                errorMsgHdText = nodeText;
			else if(nodeName.equals("field-row-attrs") && nodeText != null)
				fieldRowAttrs = nodeText;
			else if(nodeName.equals("field-row-error-attrs") && nodeText != null)
				fieldRowErrorAttrs = nodeText;
			else if(nodeName.equals("caption-cell-attrs") && nodeText != null)
				captionCellAttrs = nodeText;
			else if(nodeName.equals("caption-font-attrs") && nodeText != null)
				captionFontAttrs = nodeText;
			else if(nodeName.equals("grid-caption-font-attrs") && nodeText != null)
				gridCaptionFontAttrs = nodeText;
			else if(nodeName.equals("grid-row-caption-font-attrs") && nodeText != null)
				gridRowCaptionFontAttrs = nodeText;
			else if(nodeName.equals("control-area-font-attrs") && nodeText != null)
				controlAreaFontAttrs = nodeText;
			else if(nodeName.equals("control-attrs") && nodeText != null)
				controlAttrs = nodeText;
			else if(nodeName.equals("separator-font-attrs") && nodeText != null)
				separatorFontAttrs = nodeText;
			else if(nodeName.equals("separator-html") && nodeText != null)
				separatorHtml = nodeText;
			else if(nodeName.equals("hint-font-attrs") && nodeText != null)
				hintFontAttrs = nodeText;
			else if(nodeName.equals("error-msg-html") && nodeText != null)
				errorMsgFontAttrs = nodeText;
			else if(nodeName.equals("caption-suffix") && nodeText != null)
				captionSuffix = nodeText;
			else if(nodeName.equals("prepend-pre-script") && nodeText != null)
				prependPreScript = "<script>\n" + nodeText + "\n</script>";
			else if(nodeName.equals("prepend-post-script") && nodeText != null)
				prependPostScript = "<script>\n" + nodeText + "\n</script>";
			else if(nodeName.equals("append-pre-script") && nodeText != null)
				appendPreScript = "<script>\n" + nodeText + "\n</script>";
			else if(nodeName.equals("append-post-script") && nodeText != null)
				appendPostScript = "<script>\n" + nodeText + "\n</script>";
			else if(nodeName.equals("include-pre-script"))
			{
				String lang = nodeElem.getAttribute("language");
				if(lang.length() == 0) lang = "JavaScript";
				String inc = "<script src='"+nodeElem.getAttribute("src")+"' language='"+lang+"'></script>\n";;
				if(includePreScripts == null)
					includePreScripts = inc;
				else
					includePreScripts += inc;
			}
			else if(nodeName.equals("include-post-script"))
			{
				String lang = nodeElem.getAttribute("language");
				if(lang.length() == 0) lang = "JavaScript";
				String inc = "<script src='"+nodeElem.getAttribute("src")+"' language='"+lang+"'></script>\n";
				if(includePostScripts == null)
					includePostScripts = inc;
				else
					includePostScripts += inc;
			}
			else if(nodeName.equals("include-pre-stylesheet"))
			{
				String inc = "<link rel='stylesheet' href='"+nodeElem.getAttribute("href")+"'>\n";
				if(includePreStyleSheets == null)
					includePreStyleSheets = inc;
				else
					includePreStyleSheets += inc;
			}
			else if(nodeName.equals("include-post-stylesheet"))
			{
				String inc = "<link rel='stylesheet' href='"+nodeElem.getAttribute("href")+"'>\n";
				if(includePostStyleSheets == null)
					includePostStyleSheets = inc;
				else
					includePostStyleSheets += inc;
			}
		}
	}

	public final String getDefaultControlAttrs()
	{
		return controlAttrs;
	}

	public String getCompositeControlsHtml(DialogContext dc, DialogField parentField)
	{
		StringBuffer html = new StringBuffer();
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
					if(caption != DialogField.CUSTOM_CAPTION && caption != null)
                    {
						html.append("<nobr>" + (field.isRequired(dc) ? "<b>" + caption + "</b>" : caption));
                        if(captionSuffix != null)
                            html.append(captionSuffix);
                    }
				}
				html.append(field.getControlHtml(dc) + "&nbsp;");
				if(showCaption) html.append("</nobr>");
			}
		}
		return html.toString();
	}

	public void appendGridControlBasics(DialogContext dc, DialogField field, StringBuffer html)
	{
		String controlHtml = field.getControlHtml(dc);
		String popupHtml = getPopupHtml(dc, field);
		if(popupHtml != null)
			controlHtml += popupHtml;

		if(field.flagIsSet(DialogField.FLDFLAG_CREATEADJACENTAREA))
			controlHtml += "&nbsp;<span id='"+ field.getQualifiedName() +"_adjacent'></span>";

		StringBuffer messagesHtml = new StringBuffer();
        String hint = field.getHint();
        if(hint != null)
        {
            messagesHtml.append("<br><font "+hintFontAttrs+">");
            messagesHtml.append(hint);
            messagesHtml.append("</font>");
        }

		html.append("<font ");
		html.append(controlAreaFontAttrs);
		html.append(">");
		html.append(controlHtml);
		if(messagesHtml.length() > 0)
			html.append(messagesHtml);
		html.append("</font>");
	}

	public String getGridRowHtml(DialogContext dc, GridField gridField, DialogField compositeField, int row)
	{
		String rowAttr = " id='" + GRIDFIELDROW_PREFIX + compositeField.getQualifiedName() + "' ";
		StringBuffer rowHtml = new StringBuffer("\n<tr valign='top' "+rowAttr+">");
		Iterator i = compositeField.getChildren().iterator();

        // get the row's name
        String rowCaption = compositeField.getCaption(dc);
        if (rowCaption == null)
        {
            rowCaption = "";
        }
		if(row == 0)
		{
			String hRowAttr = " id='" + GRIDHEADROW_PREFIX + compositeField.getQualifiedName() + "' ";
			StringBuffer headerHtml = new StringBuffer("\n<tr "+hRowAttr+">");

			int fieldNum = 0;
			String[] fieldCaptions = gridField.getCaptions(dc);
            // save space in the header for the row captions
			headerHtml.append("<td></td> ");
            // append the row caption to the first row
            rowHtml.append("<td><font " + gridRowCaptionFontAttrs + ">");
            rowHtml.append(rowCaption);
            rowHtml.append("</font></td>");
			while(i.hasNext())
			{
				DialogField field = (DialogField) i.next();
				if(field.isVisible(dc))
				{
					String caption = fieldNum < fieldCaptions.length ? fieldCaptions[fieldNum] : field.getCaption(dc);

					headerHtml.append("<td align='center'><font ");
					headerHtml.append(gridCaptionFontAttrs);
					headerHtml.append(">");
					if(caption != null && caption != DialogField.CUSTOM_CAPTION)
					{
						headerHtml.append(field.isRequired(dc) ? "<b>" + caption + "</b>" : caption);
					}
					headerHtml.append("</font></td>");


					rowHtml.append("<td align='center'>");
					appendGridControlBasics(dc, field, rowHtml);
					rowHtml.append("</td>");
				}
				fieldNum++;
			}

			headerHtml.append("</tr>");
			headerHtml.append(rowHtml);
			headerHtml.append("</tr>");

			return headerHtml.toString();
		}
		else
		{
            // append the row caption to the first row
            rowHtml.append("<td><font " + gridRowCaptionFontAttrs + ">");
            rowHtml.append(rowCaption);
            rowHtml.append("</font></td>");

			while(i.hasNext())
			{
				DialogField field = (DialogField) i.next();
				if(field.isVisible(dc))
				{
					rowHtml.append("<td align='center'>");
					appendGridControlBasics(dc, field, rowHtml);
					rowHtml.append("</td>");
				}
			}
			rowHtml.append("</tr>");
			return rowHtml.toString();
		}
	}

	public String getGridControlsHtml(DialogContext dc, GridField gridField)
	{
		StringBuffer html = new StringBuffer("\n<table border=0>");

		Iterator i = gridField.getChildren().iterator();
		int row = 0;
		int colsCount = 0;
		while(i.hasNext())
		{
			DialogField rowField = (DialogField) i.next();
			if(colsCount == 0)
				colsCount = rowField.getChildren().size();

			if(rowField.isVisible(dc))
			{
				StringBuffer messagesHtml = new StringBuffer();
				boolean haveErrors = false;
				boolean firstMsg = true;
				List errorMessages = dc.getErrorMessages(rowField);
				if(errorMessages != null)
				{
					messagesHtml.append("<font "+errorMsgFontAttrs+">");
					Iterator emi = errorMessages.iterator();
					while(emi.hasNext())
					{
						if(! firstMsg)
	    					messagesHtml.append("<br>");
						else
							firstMsg = false;
						messagesHtml.append((String) emi.next());
					}
					messagesHtml.append("</font>");
					haveErrors = true;
				}

				html.append(getGridRowHtml(dc, gridField, rowField, row));
				if(haveErrors)
				{
					html.append("<tr><td colspan='"+colsCount+"'>");
					html.append("<font " + controlAreaFontAttrs);
					html.append(messagesHtml);
					html.append("</font></td></tr>");
				}
			}
			row++;
		}

		html.append("\n</table>");
		return html.toString();
	}

	public String getPopupHtml(DialogContext dc, DialogField field)
	{
		DialogFieldPopup popup = field.getPopup();
		if(popup == null)
			return null;

		String expression = "new DialogFieldPopup('"+ dc.getDialog().getName() +"', '"+ field.getQualifiedName() +"', '"+ popup.getActionUrl() +"', '"+ popup.getPopupWindowClass() +"', "+ popup.closeAfterSelect()+ ", "+ popup.allowMultiSelect();;
		String[] fillFields = popup.getFillFields();
		if(fillFields.length == 1)
		{
			expression += ", '"+ fillFields[0] +"')";
		}
		else
		{
			StringBuffer expr = new StringBuffer(expression);
			for(int i = 0; i < fillFields.length; i++)
				expr.append(", '"+ fillFields[i] +"'");
			expression = expr.toString() + ")";
		}

		String imageUrl = popup.getImageUrl();
		if(imageUrl == null)
			imageUrl = ConfigurationManagerFactory.getDefaultConfiguration(dc.getServletContext()).getValue(dc, "framework.shared.dialog.field.popup-image-src");

		return "&nbsp;<a href='' style='cursor:hand;' onclick=\"javascript:"+ expression +";return false;\"><img border='0' src='"+ imageUrl +"'></a>&nbsp;";

	}

	public void appendFieldHtml(DialogContext dc, DialogField field, StringBuffer fieldsHtml, StringBuffer fieldsJSDefn, List fieldErrorMsgs)
	{
		if(field.flagIsSet(DialogField.FLDFLAG_INPUT_HIDDEN))
		{
			fieldsHtml.append(field.getControlHtml(dc));
			return;
		}

		String name = field.getQualifiedName();
		String caption = field.getCaption(dc);
		ArrayList fieldChildren = field.getChildren();
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
					generated.append(childField.isRequired(dc) ? "<b>"+ childCaption + "</b>" : childCaption);
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

		String controlHtml = field.getControlHtml(dc);
		String popupHtml = getPopupHtml(dc, field);
		if(popupHtml != null)
			controlHtml += popupHtml;

		if(field.flagIsSet(DialogField.FLDFLAG_CREATEADJACENTAREA))
			controlHtml += "&nbsp;<span id='"+ field.getQualifiedName() +"_adjacent'></span>";

		StringBuffer messagesHtml = new StringBuffer();
        String hint = field.getHint();
        if(hint != null && !(field.isReadOnly(dc) && dc.getDialog().flagIsSet(Dialog.DLGFLAG_HIDE_READONLY_HINTS)))
        {
            messagesHtml.append("<br><font "+hintFontAttrs+">");
            messagesHtml.append(hint);
            messagesHtml.append("</font>");
        }
        boolean haveErrors = false;
		if(name != null)
		{
			List errorMessages = dc.getErrorMessages(field);
			if(errorMessages != null)
			{
				messagesHtml.append("<font "+errorMsgFontAttrs+">");
				Iterator emi = errorMessages.iterator();
				while(emi.hasNext())
				{
                    int msgNum = fieldErrorMsgs.size();
                    String msgStr = (String) emi.next();
                    fieldErrorMsgs.add(msgStr);
					messagesHtml.append("<br><a name='dc_error_msg_"+ msgNum +"'>" + msgStr + "</a>");
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
			fieldsHtml.append("<tr"+ rowAttr +"><td colspan='2'><font "+ controlAreaFontAttrs+">" + controlHtml + messagesHtml + "</font></td></tr>\n");
		}
		else
		{
			fieldsHtml.append(
				"<tr "+ rowAttr +"><td "+captionCellAttrs+"><font "+ captionFontAttrs+">" + caption + "</font></td>" +
				"<td><font "+ controlAreaFontAttrs +">" + controlHtml + messagesHtml + "</font></td></tr>\n");
		}

		if(field.getSimpleName() != null)
			fieldsJSDefn.append(field.getJavaScriptDefn(dc));
	}

	public String getHtml(DialogContext dc)
	{
		long startTime = new Date().getTime();

        List fieldErrorMsgs = dc.getErrorsCount() > 0 ? new ArrayList() : null;
        List dlgErrorMsgs = dc.getErrorMessages();
        if(dlgErrorMsgs != null)
            fieldErrorMsgs.addAll(dlgErrorMsgs);

		Dialog dialog = dc.getDialog();
		String dialogName = dialog.getName();

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
				if(! field.isVisible(dc))
					continue;

				appendFieldHtml(dc, field, fieldsHtml, fieldsJSDefn, fieldErrorMsgs);
			}

			if(director != null && director.isVisible(dc))
				appendFieldHtml(dc, director, fieldsHtml, fieldsJSDefn, fieldErrorMsgs);
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
				if(! field.isVisible(dc))
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

				fieldsHtml.append("<td width='"+cellWidth+"%'><table width='100%'>");
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

			if(director != null && director.isVisible(dc))
			{
				fieldsHtml.append("<tr><td colspan='"+dlgTableColSpan+"'><font "+ controlAreaFontAttrs +">");
				fieldsHtml.append(director.getControlHtml(dc));
				fieldsHtml.append("</font></td></tr>");
			}
		}

		String heading = null;
		SingleValueSource headingVS = dialog.getHeading();
		if(headingVS != null)
		    heading = headingVS.getValue(dc);

		String actionURL = null;
        if (director != null)
            actionURL = director.getSubmitActionUrl() != null ?director.getSubmitActionUrl().getValue(dc) : null;

		if(actionURL == null)
			actionURL = ((HttpServletRequest) dc.getRequest()).getRequestURI();

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(dc.getServletContext());
		String sharedScriptsUrl = appConfig.getValue(dc, "framework.shared.scripts-url");

        StringBuffer errorMsgsHtml = new StringBuffer();
        if(fieldErrorMsgs != null)
        {
            errorMsgsHtml.append("<tr><td colspan='"+dlgTableColSpan+"'><ul type=square><font "+ controlAreaFontAttrs +"><font "+ errorMsgHdFontAttrs +"><b>"+ errorMsgHdText +"</b></font>\n");
            for(int i = 0; i < fieldErrorMsgs.size(); i++)
            {
                String errorMsg = (String) fieldErrorMsgs.get(i);
                errorMsgsHtml.append("<li><a href='#dc_error_msg_"+ i +"' style='text-decoration:none'><font "+ errorMsgFontAttrs +">"+ errorMsg +"</font></a></li>\n");
            }
            errorMsgsHtml.append("</ul></td></tr>\n");
        }
        String dialogIncludeJS = (dialog.getIncludeJSFile() != null ? dialog.getIncludeJSFile().getValue(dc) : null);
		String html =
			(includePreStyleSheets != null ? includePreStyleSheets : EMPTY) +
			"<link rel='stylesheet' href='"+ appConfig.getValue(dc, "framework.shared.css-url") +"/dialog.css'>\n"+
			(includePostStyleSheets != null ? includePostStyleSheets : EMPTY) +
			(prependPreScript != null ? prependPreScript : EMPTY) +
			"<script language='JavaScript'>var _version = 1.0;</script>\n"+
			"<script language='JavaScript1.1'>_version = 1.1;</script>\n"+
			"<script language='JavaScript1.2'>_version = 1.2;</script>\n"+
			"<script language='JavaScript1.3'>_version = 1.3;</script>\n"+
			"<script language='JavaScript1.4'>_version = 1.4;</script>\n"+
			(includePreScripts != null ? includePreScripts : EMPTY) +
			"<script src='"+ sharedScriptsUrl +"/popup.js' language='JavaScript1.1'></script>\n"+
			"<script src='"+ sharedScriptsUrl +"/dialog.js' language='JavaScript1.2'></script>\n"+
			"<script language='JavaScript'>\n"+
			"	if(typeof dialogLibraryLoaded == 'undefined')\n"+
			"	{\n"+
			"		alert('ERROR: "+ sharedScriptsUrl +"/dialog.js could not be loaded');\n"+
			"	}\n"+
			"</script>\n"+
            (dialogIncludeJS != null ? "<script language='JavaScript' src='"+ dialogIncludeJS +"'></script>\n" : EMPTY) +
			(includePostScripts != null ? includePostScripts : EMPTY) +
			(prependPostScript != null ? prependPostScript : EMPTY) +
			"<table "+ outerTableAttrs +">\n" +
			"<tr><td><table "+innerTableAttrs+">" +
			(heading == null ? "" :
			"<tr "+frameHdRowAttrs+"><td colspan='"+dlgTableColSpan+"' align='"+ frameHdRowAlign +"'><font "+frameHdFontAttrs+"><b>"+ heading +"</b></font></td></tr>\n") +
            errorMsgsHtml +
			"<form id='" + dialogName + "' name='"+ dialogName +"' action='"+ actionURL +"' method='post' onsubmit='return(activeDialog.isValid())'>\n" +
			dc.getStateHiddens() + "\n" +
			fieldsHtml +
			"</form>\n" +
			"</table></td></tr></table>"+
			(appendPreScript != null ? appendPreScript : EMPTY) +
			"<script language='JavaScript'>\n"+
			"       var " + dialogName + " = new Dialog(\"" + dialogName + "\");\n" +
			"       var dialog = " + dialogName + "; setActiveDialog(dialog);\n" +
			"       var field;\n" +
				    fieldsJSDefn +
			"       dialog.finalizeContents();\n" +
			"</script>\n"+
			(appendPostScript != null ? appendPostScript : EMPTY);


		com.xaf.log.LogManager.recordAccess((HttpServletRequest) dc.getRequest(), null, this.getClass().getName(), dc.getLogId(), startTime);
		return html;
	}

	public String getSeparatorHtml(DialogContext dc, SeparatorField field)
	{
		String heading = field.getHeading();
		if(heading != null)
		{
			String sep = "<font "+separatorFontAttrs+"><b>"+ heading +"</b></font>";
			if(! field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE))
				sep += separatorHtml;

			if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
				return sep;
			else
				return "<br>" + sep;
		}
		else
		{
			if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE))
				return "";
			else
				return field.flagIsSet(SeparatorField.FLDFLAG_HIDERULE) ? "<br>" : "<hr size=1 color=silver>";
		}
	}

	public String getOuterTableAttrs() { return outerTableAttrs; }
	public void setOuterTableAttrs(String value) { outerTableAttrs = value; }

	public String getInnerTableAttrs() { return innerTableAttrs; }
	public void setInnerTableAttrs(String value) { innerTableAttrs = value; }

	public String getFrameHdRowAlign() { return frameHdRowAlign; }
	public void setFrameHdRowAlign(String value) { frameHdRowAlign = value; }

	public String getFrameHdRowAttrs() { return frameHdRowAttrs; }
	public void setFrameHdRowAttrs(String value) { frameHdRowAttrs = value; }

	public String getFrameHdFontAttrs() { return frameHdFontAttrs; }
	public void setFrameHdFontAttrs(String value) { frameHdFontAttrs = value; }

	public String getFieldRowAttrs() { return fieldRowAttrs; }
	public void setFieldRowAttrs(String value) { fieldRowAttrs = value; }

	public String getFieldRowErrorAttrs() { return fieldRowErrorAttrs; }
	public void setFieldRowErrorAttrs(String value) { fieldRowErrorAttrs = value; }

	public String getGridCaptionFontAttrs() { return gridCaptionFontAttrs; }
	public void setGridCaptionFontAttrs(String value) { gridCaptionFontAttrs = value; }

	public String getGridRowCaptionFontAttrs() { return gridRowCaptionFontAttrs; }
	public void setGridRowCaptionFontAttrs(String value) { gridRowCaptionFontAttrs = value; }

	public String getCaptionCellAttrs() { return captionCellAttrs; }
	public void setCaptionCellAttrs(String value) { captionCellAttrs = value; }

	public String getCaptionFontAttrs() { return captionFontAttrs; }
	public void setCaptionFontAttrs(String value) { captionFontAttrs = value; }

	public String getControlAreaFontAttrs() { return controlAreaFontAttrs; }
	public void setControlAreaFontAttrs(String value) { controlAreaFontAttrs = value; }

	public String getControlAttrs() { return controlAttrs; }
	public void setControlAttrs(String value) { controlAttrs = value; }

	public String getSeparatorFontAttrs() { return separatorFontAttrs; }
	public void setSeparatorFontAttrs(String value) { separatorFontAttrs = value; }

	public String getSeparatorHtml() { return separatorHtml; }
	public void setSeparatorHtml(String value) { separatorHtml = value; }

	public String getHintFontAttrs() { return hintFontAttrs; }
	public void setHintFontAttrs(String value) { hintFontAttrs = value; }

	public String getErrorMsgFontAttrs() { return errorMsgFontAttrs; }
	public void setErrorMsgFontAttrs(String value) { errorMsgFontAttrs = value; }

	public String getCaptionSuffix() { return captionSuffix; }
	public void setCaptionSuffix(String value) { captionSuffix = value; }

	public String getIncludePreScripts() { return includePreScripts; }
	public void setIncludePreScripts(String value) { includePreScripts = value; }

	public String getIncludePostScripts() { return includePostScripts; }
	public void setIncludePostScripts(String value) { includePostScripts = value; }

	public String getIncludePreStyleSheets() { return includePreStyleSheets; }
	public void setIncludePreStyleSheets(String value) { includePreStyleSheets = value; }

	public String getIncludePostStyleSheets() { return includePostStyleSheets; }
	public void setIncludePostStyleSheets(String value) { includePostStyleSheets = value; }

	public String getPrependPreScript() { return prependPreScript; }
	public void setPrependPreScript(String value) { prependPreScript = value; }

	public String getPrependPostScript() { return prependPostScript; }
	public void setPrependPostScript(String value) { prependPostScript = value; }

	public String getAppendPreScript() { return appendPreScript; }
	public void setAppendPreScript(String value) { appendPreScript = value; }

	public String getAppendPostScript() { return appendPostScript; }
	public void setAppendPostScript(String value) { appendPostScript = value; }

}
