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

        value = elem.getAttribute("begin-min-value");
        if (value != null && value.length() != 0)
        {
            this.beginField.setMinDateStr(this.beginField.translateDateString(value));
        }

        value = elem.getAttribute("end-max-value");
        if (value != null && value.length() != 0)
        {
            this.endField.setMaxDateStr(this.endField.translateDateString(value));
        }

        if(elem.getAttribute("popup-calendar").equalsIgnoreCase("yes"))
        {
            beginField.setFlag(DateTimeField.FLDFLAG_POPUP_CALENDAR);
            endField.setFlag(DateTimeField.FLDFLAG_POPUP_CALENDAR);
        }

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

        Date beginDate, endDate;
		try
		{
			beginDate = beginField.getFormat().parse(dc.getValue(beginField));
			endDate = endField.getFormat().parse(dc.getValue(endField));

			if(beginDate.after(endDate))
			{
				invalidate(dc, "Beginning value should be before ending value.");
				return false;
			}

		}
		catch(Exception e)
		{
			invalidate(dc, "One of the values is invalid. This error should never happen.");
			return false;
		}

        try
        {
            String maxDateStr = this.endField.getMaxDateStr();
            if (maxDateStr != null)
            {
                Date maxDate = endField.getFormat().parse(maxDateStr);
                if (endDate.after(maxDate))
                {
                    invalidate(dc, endField.getCaption(dc) + " must not be greater than " + maxDateStr + ".");
                    return false;
                }
            }
            String minDateStr = this.beginField.getMinDateStr();
            if (minDateStr != null)
            {
                Date minDate = beginField.getFormat().parse(minDateStr);
                if (beginDate.before(minDate))
                {
                    invalidate(dc, beginField.getCaption(dc) + " must not be less than " + minDateStr + ".");
                    return false;
                }
            }

        }
        catch (Exception e)
        {
			invalidate(dc, "One of the min and max values is invalid. This error should never happen.");
            e.printStackTrace();
			return false;
        }


		return super.isValid(dc);
	}

    /**
     * Populates a default value into the dialog field. Overwites TextField.populateValue().
     *
     * @param dc Dialog context
     */
	public void populateValue(DialogContext dc, int formatType)
	{
        super.populateValue(dc, formatType);
        String xlatedDate = null;

        String beginValue = dc.getValue(this.beginField);
        if (beginValue != null)
        {
            xlatedDate = this.beginField.translateDateString(beginValue);
            dc.setValue(this.beginField, xlatedDate);
        }

        String endValue = dc.getValue(this.endField);
        if (endValue != null)
        {
            xlatedDate = this.endField.translateDateString(endValue);
            dc.setValue(this.endField, xlatedDate);
        }



	}

}
