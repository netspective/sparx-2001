package com.xaf.skin;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class StandardDialogSkin implements DialogSkin
{
	public final String FIELDROW_PREFIX = "_dfr.";
	public final String GRIDHEADROW_PREFIX = "_dghr.";
	public final String GRIDFIELDROW_PREFIX = "_dgfr.";

	protected String outerTableAttrs;
	protected String innerTableAttrs;
	protected String frameHdRowAlign;
	protected String frameHdRowAttrs;
	protected String frameHdFontAttrs;
	protected String fieldRowAttrs;
	protected String fieldRowErrorAttrs;
	protected String gridCaptionFontAttrs;
	protected String captionCellAttrs;
	protected String captionFontAttrs;
	protected String controlAreaFontAttrs;
	protected String controlAttrs;
	protected String separatorFontAttrs;
	protected String separatorHtml;
    protected String hintFontAttrs;
    protected String errorMsgFontAttrs;
    protected String captionSuffix;

	public StandardDialogSkin()
	{
		outerTableAttrs = "cellspacing='1' cellpadding='0' bgcolor='#6699CC' ";
		innerTableAttrs = "cellspacing='0' cellpadding='4' bgcolor='lightyellow' ";
		frameHdRowAlign = "LEFT";
		frameHdRowAttrs = "bgcolor='#6699CC' ";
		frameHdFontAttrs = "face='verdana,arial,helvetica' size=2 color='white' ";
		fieldRowAttrs = "";
		fieldRowErrorAttrs = "bgcolor='beige' ";
		captionCellAttrs = "align='right' ";
		captionFontAttrs = "size='2' face='tahoma,arial,helvetica' style='font-size:8pt' ";
		gridCaptionFontAttrs = "size='2' face='tahoma,arial,helvetica' color='navy' style='font-size:9pt' ";
		controlAreaFontAttrs = "size='2' face='tahoma,arial,helvetica' style='font-size:8pt' ";
		controlAttrs = "class='dialog_control' onfocus='controlOnFocus(this)' onchange='controlOnChange(this)' onblur='controlOnBlur(this)' ";
		separatorFontAttrs = "face='verdana,arial' size=2 color=#555555";
		separatorHtml = "<hr size=1 color=#555555>";
        hintFontAttrs = "color='navy'";
        errorMsgFontAttrs = "color='red'";
        captionSuffix = ": ";
	}

	public final String getControlAreaFontAttrs()
	{
		return controlAreaFontAttrs;
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
					String caption = field.getCaption();
					if(caption != DialogField.CUSTOM_CAPTION)
                    {
						html.append("<nobr>" + (field.isRequired(dc) ? "<b>" + field.getCaption() + "</b>" : field.getCaption()));
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

		if(row == 0)
		{
			String hRowAttr = " id='" + GRIDHEADROW_PREFIX + compositeField.getQualifiedName() + "' ";
			StringBuffer headerHtml = new StringBuffer("\n<tr "+hRowAttr+">");
			while(i.hasNext())
			{
				DialogField field = (DialogField) i.next();
				if(field.isVisible(dc))
				{
					String caption = field.getCaption();

					headerHtml.append("<td><font ");
					headerHtml.append(gridCaptionFontAttrs);
					headerHtml.append(">");
					if(caption != null && caption != DialogField.CUSTOM_CAPTION)
					{
						headerHtml.append(field.isRequired(dc) ? "<b>" + field.getCaption() + "</b>" : field.getCaption());
					}
					headerHtml.append("</font></td>");

					rowHtml.append("<td>");
					appendGridControlBasics(dc, field, rowHtml);
					rowHtml.append("</td>");
				}
			}

			headerHtml.append("</tr>");
			headerHtml.append(rowHtml);
			headerHtml.append("</tr>");

			return headerHtml.toString();
		}
		else
		{
			while(i.hasNext())
			{
				DialogField field = (DialogField) i.next();
				if(field.isVisible(dc))
				{
					rowHtml.append("<td>");
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
				ArrayList errorMessages = dc.getErrorMessages(rowField);
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

		return "&nbsp;<a style='cursor:hand;' onclick=\"javascript:"+ expression +"\"><img border='0' src='"+ popup.getImageUrl() +"'></a>&nbsp;";
	}

	public String getJavaScriptDefn(DialogContext dc, DialogField field)
	{
		String fieldClassName = field.getClass().getName();
		String js =
			"field = new DialogField(\"" + fieldClassName + "\", \""+ field.getId() + "\", \"" + field.getSimpleName() + "\", \"" + field.getQualifiedName() + "\", \"" + field.getCaption() + "\", " + field.getFlags() +");\n" +
			"dialog.registerField(field);\n";

		if(field instanceof SelectField)
			js += "field.style = " + ((SelectField) field).getStyle() + ";\n";

		ArrayList dependentConditions = field.getDependentConditions();
		if(dependentConditions != null)
		{
			StringBuffer dcJs = new StringBuffer();
			Iterator i = dependentConditions.iterator();
			while(i.hasNext())
			{
				DialogFieldConditionalAction o = (DialogFieldConditionalAction) i.next();

				if(o instanceof DialogFieldConditionalDisplay)
				{
					DialogFieldConditionalDisplay action = (DialogFieldConditionalDisplay) o;
					dcJs.append("field.dependentConditions[field.dependentConditions.length] = new DialogFieldConditionalDisplay(\""+ action.getSourceField().getQualifiedName() +"\", \""+ action.getPartnerField().getQualifiedName() + "\", \""+ action.getExpression() + "\");\n");
				}
			}
			js = js + dcJs.toString();
		}

		ArrayList children = field.getChildren();
		if(children != null)
		{
			StringBuffer childJs = new StringBuffer();
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				DialogField child = (DialogField) i.next();
				childJs.append(getJavaScriptDefn(dc, child));
			}
			js = js + childJs.toString();
		}

		return js;
	}

	public void appendFieldHtml(DialogContext dc, DialogField field, StringBuffer fieldsHtml, StringBuffer fieldsJSDefn)
	{
		if(field.flagIsSet(DialogField.FLDFLAG_INPUT_HIDDEN))
		{
			fieldsHtml.append(field.getControlHtml(dc));
			return;
		}

		String name = field.getQualifiedName();
		String caption = field.getCaption();
		ArrayList fieldChildren = field.getChildren();
		if(caption != null && fieldChildren != null && caption.equals(DialogField.GENERATE_CAPTION))
		{
			StringBuffer generated = new StringBuffer();
			Iterator c = fieldChildren.iterator();
			while(c.hasNext())
			{
				DialogField childField = (DialogField) c.next();
				String childCaption = childField.getCaption();
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
        if(hint != null)
        {
            messagesHtml.append("<br><font "+hintFontAttrs+">");
            messagesHtml.append(hint);
            messagesHtml.append("</font>");
        }
        boolean haveErrors = false;
		if(name != null)
		{
			ArrayList errorMessages = dc.getErrorMessages(field);
			if(errorMessages != null)
			{
				messagesHtml.append("<font "+errorMsgFontAttrs+">");
				Iterator emi = errorMessages.iterator();
				while(emi.hasNext())
				{
					messagesHtml.append("<br>" + (String) emi.next());
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
			fieldsJSDefn.append(getJavaScriptDefn(dc, field));
	}

	public String getHtml(DialogContext dc)
	{
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

				appendFieldHtml(dc, field, fieldsHtml, fieldsJSDefn);
			}

			if(director != null && director.isVisible(dc))
				appendFieldHtml(dc, director, fieldsHtml, fieldsJSDefn);
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
				appendFieldHtml(dc, field, layoutColsFieldsHtml[activeColumn], fieldsJSDefn);
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

		String actionURL = dialog.getActionURL();
		if(actionURL == null)
			actionURL = ((HttpServletRequest) dc.getRequest()).getRequestURI();

		return
			"<link rel='stylesheet' href='/shared/resources/css/dialog.css'>\n"+
			"<script language='JavaScript'>var _version = 1.0;</script>\n"+
			"<script language='JavaScript1.1'>_version = 1.1;</script>\n"+
			"<script language='JavaScript1.2'>_version = 1.2;</script>\n"+
			"<script language='JavaScript1.3'>_version = 1.3;</script>\n"+
			"<script language='JavaScript1.4'>_version = 1.4;</script>\n"+
			"<script src='/shared/resources/scripts/popup.js' language='JavaScript1.1'></script>\n"+
			"<script src='/shared/resources/scripts/dialog.js' language='JavaScript1.2'></script>\n"+
			"<script>\n"+
			"	if(typeof dialogLibraryLoaded == 'undefined')\n"+
			"	{\n"+
			"		alert('ERROR: /shared/resources/scripts/dialog.js could not be loaded');\n"+
			"	}\n"+
			"</script>\n"+
			"<table "+ outerTableAttrs +">\n" +
			"<tr><td><table "+innerTableAttrs+">" +
			(heading == null ? "" :
			"<tr "+frameHdRowAttrs+"><td colspan='"+dlgTableColSpan+"' align='"+ frameHdRowAlign +"'><font "+frameHdFontAttrs+"><b>"+ heading +"</b></font></td></tr>\n") +
			"<form name='"+ dialogName +"' action='"+ actionURL +"' method='post' onsubmit='return(activeDialog.isValid())'>\n" +
			dc.getStateHiddens() + "\n" +
			fieldsHtml +
			"</form>\n" +
			"</table></td></tr></table>"+
			"<script>\n"+
			"       var " + dialogName + " = new Dialog(\"" + dialogName + "\");\n" +
			"       var dialog = " + dialogName + "; setActiveDialog(dialog);\n" +
			"       var field;\n" +
				    fieldsJSDefn +
			"       dialog.finalizeContents();\n" +
			"</script>\n";
	}

	public String getSeparatorHtml(DialogContext dc, SeparatorField field)
	{
		String heading = field.getHeading();
		if(heading != null)
		{
			String sep =  "<font "+separatorFontAttrs+"><b>"+ heading +"</b></font>"+separatorHtml;
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
				return "<hr size=1 color=silver>";
		}
	}
}