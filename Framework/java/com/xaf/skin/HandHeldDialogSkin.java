package com.xaf.skin;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

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
					if(caption != DialogField.CUSTOM_CAPTION)
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

	public String getGridControlsHtml(DialogContext dc, GridField gridField)
	{
        return getClass().getName() + " does not support grid rows yet.";
    }

	public void appendFieldHtml(DialogContext dc, DialogField field, StringBuffer fieldsHtml)
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

		String rowAttr = fieldRowAttrs;
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
	}

	public String getHtml(DialogContext dc)
	{
		Dialog dialog = dc.getDialog();
		String dialogName = dialog.getName();

		StringBuffer fieldsHtml = new StringBuffer();

		DialogDirector director = dialog.getDirector();

        Iterator i = dc.getDialog().getFields().iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(! field.isVisible(dc))
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
        if (director != null)
            actionURL = director.getSubmitActionUrl() != null ?director.getSubmitActionUrl().getValue(dc) : null;

		if(actionURL == null)
			actionURL = ((HttpServletRequest) dc.getRequest()).getRequestURI();

		return
			"<table "+ outerTableAttrs +">\n" +
			"<tr><td><table "+innerTableAttrs+">" +
			(heading == null ? "" :
			"<tr "+frameHdRowAttrs+"><td colspan='2' align='"+ frameHdRowAlign +"'><font "+frameHdFontAttrs+"><b>"+ heading +"</b></font></td></tr>\n") +
			"<form name='"+ dialogName +"' action='"+ actionURL +"' method='post' onsubmit='return(activeDialog.isValid())'>\n" +
			dc.getStateHiddens() + "\n" +
			fieldsHtml +
			"</form>\n" +
			"</table></td></tr></table>\n";
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
}