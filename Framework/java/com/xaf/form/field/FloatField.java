package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class FloatField extends TextField
{
	protected float minValue = java.lang.Integer.MIN_VALUE;
	protected float maxValue = java.lang.Integer.MAX_VALUE;

	public FloatField()
	{
		super();
		setSize(12);
	}

	public FloatField(String aName, String aCaption)
	{
		super(aName, aCaption);
		setSize(12);
	}

	public float getMinValue() { return minValue; }
	public void setMinValue(float value) { minValue = value; }

	public float getMaxValue() { return maxValue; }
	public void setMaxValue(float value) { maxValue = value; }

	public void setMinMaxValue(float low, float high)
	{
		minValue = low;
		maxValue = high;
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("min");
		if(value.length() != 0)
			minValue = Float.parseFloat(value);

		value = elem.getAttribute("max");
		if(value.length() != 0)
			maxValue = Float.parseFloat(value);
	}

	public boolean needsValidation(DialogContext dc)
	{
        return true;
	}

    public Object getValueAsObject(String value)
    {
        Float result = null;

        try
        {
            result = new Float(value);
        }
        catch (NumberFormatException e)
        {
            result = null;
        }
        return result;
    }

	public boolean isValid(DialogContext dc)
	{
		boolean textValid = super.isValid(dc);
		if(textValid)
		{
			String strValue = dc.getValue(this);
			if(! isRequired(dc) && (strValue == null || strValue.length() == 0))
				return true;

			Float value = null;
			try
			{
				value = new Float(strValue);
			}
			catch(Exception e)
			{
				invalidate(dc, "'" + strValue + "' is not a valid number.");
				return false;
			}
			if(value.floatValue() < minValue || value.floatValue() > maxValue)
			{
				invalidate(dc, getCaption(dc) + " needs to be between " + minValue + " and " + maxValue + ".");
				return false;
			}
			return true;
		}
		else
			return false;
	}

    /**
	 * Produces Java code when a custom DialogContext is created
	 */
	public DialogContextMemberInfo getDialogContextMemberInfo()
	{
        DialogContextMemberInfo mi = createDialogContextMemberInfo("float");
        String fieldName = mi.getFieldName();
		String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

		mi.addJavaCode("\tpublic "+ dataType +" get" + memberName + "() { Float o = (Float) getValueAsObject(\""+ fieldName +"\"); return o == null ? (float) 0.0 : o.floatValue(); }\n");
		mi.addJavaCode("\tpublic void set" + memberName + "("+ dataType +" value) { setValue(\""+ fieldName +"\", Float.toString(value)); }\n");

		return mi;
	}
}
