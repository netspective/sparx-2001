package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class IntegerField extends TextField
{
	private int minValue = java.lang.Integer.MIN_VALUE;
	private int maxValue = java.lang.Integer.MAX_VALUE;

	public IntegerField()
	{
		super();
		setSize(10);
	}

	public IntegerField(String aName, String aCaption)
	{
		super(aName, aCaption);
		setSize(10);
	}

	public final int getMinValue() { return minValue; }
	public void setMinValue(int value) { minValue = value; }

	public final int getMaxValue() { return maxValue; }
	public void setMaxValue(int value) { maxValue = value; }

	public void setMinMaxValue(int low, int high)
	{
		minValue = low;
		maxValue = high;
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("min");
		if(value.length() != 0)
			minValue = Integer.parseInt(value);

		value = elem.getAttribute("max");
		if(value.length() != 0)
			maxValue = Integer.parseInt(value);
	}

	public boolean needsValidation(DialogContext dc)
	{
        return true;
	}

	public Object getValueForSqlBindParam(String value)
	{
        return new Integer(value);
	}

	public boolean isValid(DialogContext dc)
	{
		boolean textValid = super.isValid(dc);
		if(textValid)
		{
			String strValue = dc.getValue(this);
			if(! isRequired(dc) && (strValue == null || strValue.length() == 0))
				return true;

			Integer value = null;
			try
			{
				value = new Integer(strValue);
			}
			catch(Exception e)
			{
				invalidate(dc, "'" + strValue + "' is not a valid integer.");
				return false;
			}
			if(value.intValue() < minValue || value.intValue() > maxValue)
			{
				invalidate(dc, getCaption(dc) + " needs to be between " + minValue + " and " + maxValue + ".");
				return false;
			}
			return true;
		}
		else
			return false;
	}
}
