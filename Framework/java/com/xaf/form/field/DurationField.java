package com.xaf.form.field;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;

public class DurationField extends DialogField
{
	static public final int DTTYPE_DATEONLY = 0;
	static public final int DTTYPE_TIMEONLY = 1;
	static public final int DTTYPE_BOTH     = 2;

	protected DateTimeField beginField;
	protected DateTimeField endField;

	public DurationField()
	{
		super();
	}

	public DurationField(String aName, String aCaption, int aType)
	{
		super(aName, aCaption);
		beginField = new DateTimeField("begin", "Begin", aType);
		endField = new DateTimeField("end", "End", aType);

		addChildField(beginField);
		addChildField(endField);
	}

	public DateTimeField getBeginField() { return beginField; }
	public DateTimeField getEndField() { return endField; }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String nodeName = elem.getNodeName();
		int type = DTTYPE_DATEONLY;

		if(nodeName.equals("datetime"))
			type = DTTYPE_BOTH;
		else if(nodeName.equals("time"))
			type = DTTYPE_TIMEONLY;

		String name = getSimpleName();
		beginField = new DateTimeField(name + "_begin", "Begin", type);
		endField = new DateTimeField(name + "_end", "End", type);

        // see if the default value attributes are set
        String value = elem.getAttribute("default-begin");
        if (value != null && value.length() != 0)
            beginField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(value));
        else
            beginField.setDefaultValue(this.getDefaultValue());

        value = elem.getAttribute("default-end");
        if (value != null && value.length() != 0)
            endField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(value));
        else
            endField.setDefaultValue(this.getDefaultValue());

		addChildField(beginField);
		addChildField(endField);
	}

	public boolean isValid(DialogContext dc)
	{
		boolean beginValid = beginField.isValid(dc);
		boolean endValid = endField.isValid(dc);

		if(!beginValid || !endValid)
			return false;

		String strBeginValue = dc.getValue(beginField);
		String strEndValue = dc.getValue(endField);

		boolean required = isRequired(dc);
		if(! required && (strBeginValue == null || strBeginValue.length() == 0))
			return true;
		if(! required && (strEndValue == null || strEndValue.length() == 0))
			return true;

		try
		{
			Date beginDate = beginField.getFormat().parse(dc.getValue(beginField));
			Date endDate = endField.getFormat().parse(dc.getValue(endField));

			if(beginDate.after(endDate))
			{
				invalidate(dc, "Begining value should be after ending value.");
				return false;
			}
		}
		catch(Exception e)
		{
			invalidate(dc, "One of the values is invalid. This error should never happen.");
			return false;
		}

		return super.isValid(dc);
	}
}
