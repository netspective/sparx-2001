package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class BooleanField extends DialogField
{
	static public final int BOOLSTYLE_RADIO      = 0;
	static public final int BOOLSTYLE_CHECK      = 1;
	static public final int BOOLSTYLE_CHECKALONE = 2;
	static public final int BOOLSTYLE_COMBO      = 3;

	static public final int CHOICES_YESNO     = 0;
	static public final int CHOICES_TRUEFALSE = 1;
	static public final int CHOICES_ONOFF     = 2;

	static public final String[] CHOICES_TEXT = new String[] { "No", "Yes", "False", "True", "Off", "On" };

	private int style = BOOLSTYLE_CHECK;
	private int choices = CHOICES_YESNO;

	public BooleanField()
	{
		super();
	}

	public BooleanField(String aName, String aCaption, int aStyle, int aChoices)
	{
		super(aName, aCaption);
		style = aStyle;
		choices = aChoices;
	}

	public final int getStyle() { return style; }
	public void setStyle(int value) { style = value; }

	public final int getChoices() { return choices; }
	public void setChoices(int value) { choices = value; }

	public String getCaption(DialogContext dc)
	{
		if(style == BOOLSTYLE_CHECK)
			return DialogField.CUSTOM_CAPTION;
		else
			return super.getCaption(dc);
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String styleValue = elem.getAttribute("style");
		if(styleValue.length() > 0)
		{
			if(styleValue.equalsIgnoreCase("radio"))
				style = BooleanField.BOOLSTYLE_RADIO;
			else if (styleValue.equalsIgnoreCase("checkbox"))
				style = BooleanField.BOOLSTYLE_CHECK;
			else if (styleValue.equalsIgnoreCase("combo"))
				style = BooleanField.BOOLSTYLE_COMBO;
			else if (styleValue.equalsIgnoreCase("checkalone"))
				style = BooleanField.BOOLSTYLE_CHECKALONE;
			else
				style = BooleanField.BOOLSTYLE_CHECK;
	    }

		String choicesValue = elem.getAttribute("choices");
		if(choicesValue != null)
		{
			if(choicesValue.equalsIgnoreCase("yesno"))
				choices = BooleanField.CHOICES_YESNO;
			else if (choicesValue.equalsIgnoreCase("truefalse"))
				choices = BooleanField.CHOICES_TRUEFALSE;
			else if (choicesValue.equalsIgnoreCase("onoff"))
				choices = BooleanField.CHOICES_ONOFF;
			else
				choices = BooleanField.CHOICES_YESNO;
		}
	}

	public String getControlHtml(DialogContext dc)
	{
		if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
			return getHiddenControlHtml(dc);

		boolean value = false;
		String strValue = dc.getValue(this);
		if(strValue != null)
			value = new Integer(strValue).intValue() == 0 ? false : true;

		String falseText = CHOICES_TEXT[(choices * 2) + 0];
		String trueText = CHOICES_TEXT[(choices * 2) + 1];

		String id = getId();
		String defaultControlAttrs = dc.getSkin().getDefaultControlAttrs();
		switch(style)
		{
			case BOOLSTYLE_RADIO:
				return
					"<nobr><input type='radio' name='"+ id +"' id='"+ id +"0' value='0' "+ (value ? "" : "checked ") + defaultControlAttrs + "> <label for='"+ id + "0'>" + falseText + "</label></nobr> " +
					"<nobr><input type='radio' name='"+ id +"' id='"+ id +"1' value='1' "+ (value ? "checked " : "") + defaultControlAttrs + "> <label for='"+ id + "1'>" + trueText + "</label></nobr>";

			case BOOLSTYLE_CHECK:
				return "<nobr><input type='checkbox' name='"+ id +"' id='"+ id +"' value='1' "+ (value ? "checked " : "") + defaultControlAttrs + "> <label for='"+ id + "'>" + super.getCaption(dc) + "</label></nobr>";

			case BOOLSTYLE_CHECKALONE:
				return "<input type='checkbox' name='"+ id +"' value='1' "+ (value ? "checked " : "") + defaultControlAttrs + "> ";

			case BOOLSTYLE_COMBO:
				return
					"<select name='"+ id +"' "+ defaultControlAttrs + ">"+
					"<option "+ (value ? "" : "selected") + " value='0'>"+ falseText +"</option>" +
					"<option "+ (value ? "selected" : "") + " value='1'>"+ trueText +"</option>" +
					"</select>";
		}

		return "Unknown style " + style;
	}

}
