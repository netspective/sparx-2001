package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;

public class TextField extends DialogField
{
	static public final long FLDFLAG_MASKENTRY   = DialogField.FLDFLAG_STARTCUSTOM;
	static public final long FLDFLAG_UPPERCASE   = FLDFLAG_MASKENTRY * 2;
	static public final long FLDFLAG_LOWERCASE   = FLDFLAG_UPPERCASE * 2;
	static public final long FLDFLAG_TRIM        = FLDFLAG_LOWERCASE * 2;
    static public final long FLDFLAG_STARTCUSTOM = FLDFLAG_TRIM * 2;

	private int size;
	private int maxLength;

	public TextField()
	{
		super();
		size = 32;
		maxLength = 255;
	}

	public TextField(String aName, String aCaption)
	{
		super(aName, aCaption);
		size = 32;
		maxLength = 255;
	}

	public final int getSize() { return size; }
	public void setSize(int value) { size = value; }

	public final int getMaxLength() { return maxLength; }
	public void setMaxLength(int newLength) { maxLength = newLength; }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("size");
		if(value.length() != 0)
			size = Integer.parseInt(value);

		value = elem.getAttribute("max-length");
		if(value.length() != 0)
			maxLength = Integer.parseInt(value);

		if(elem.getAttribute("uppercase").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_UPPERCASE);

		if(elem.getAttribute("lowercase").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_LOWERCASE);

		if(elem.getAttribute("trim").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_TRIM);

		if(elem.getAttribute("mask-entry").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_MASKENTRY);
	}

	public String formatValue(String value)
	{
		if(value == null) return null;

		long flags = getFlags();
		if((flags & FLDFLAG_UPPERCASE) != 0) value = value.toUpperCase();
		if((flags & FLDFLAG_LOWERCASE) != 0) value = value.toLowerCase();
		if((flags & FLDFLAG_TRIM) != 0) value = value.trim();
		return value;
	}

	public void populateValue(DialogContext dc)
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

		dc.setValue(this, formatValue(value));
	}

	public boolean isValid(DialogContext dc)
	{
		String value = dc.getValue(this);
		if(isRequired(dc) && (value == null || value.length() == 0))
		{
			invalidate(dc, getCaption(dc) + " is required.");
			return false;
		}
		return super.isValid(dc);
	}

	public String getControlHtml(DialogContext dc)
	{
		if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
			return getHiddenControlHtml(dc);

		String value = dc.getValue(this);
		if(value == null) value = "";

		if(isReadOnly(dc))
		{
			return "<input type='hidden' name='"+ getId() +"' value='" + value + "'><span id='"+ getQualifiedName() +"'>" + value + "</span>";
		}
		else if(! flagIsSet(FLDFLAG_MASKENTRY))
		{
			return "<input type=\"text\" name=\""+ getId() +"\" value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
		}
		else
		{
			return "<input type=\"password\" name=\""+ getId() +"\" value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
		}
	}
}
