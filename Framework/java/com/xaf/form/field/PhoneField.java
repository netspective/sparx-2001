package com.xaf.form.field;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      American Red Cross
 * @author Sreedhar Goparaju
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class PhoneField extends TextField
{
    static public final long FLDFLAG_STRIPBRACKETS = TextField.FLDFLAG_STARTCUSTOM;

    public PhoneField()
    {
        super();
        setFlag(FLDFLAG_STRIPBRACKETS);
    }

    public void importFromXml(Element elem)
	{
        super.importFromXml(elem);
		String attr = elem.getAttribute("strip-brackets");
		if(attr.equals("no"))
            clearFlag(FLDFLAG_STRIPBRACKETS);
	}

    	public boolean needsValidation(DialogContext dc)
	{
        return true;
	}

	public String formatValue(String value)
	{
        if(! flagIsSet(FLDFLAG_STRIPBRACKETS))
            return value;

        if(value == null)
            return value;

        if(value.length() != 13)
    		return value;

        String phoneValueStr = null;
        phoneValueStr = (value.substring(2,3) + value.substring(6,3)+value.substring(10,4));
        return phoneValueStr;

	}
	public boolean isValid(DialogContext dc)
	{
        boolean result = super.isValid(dc);
        if(! result)
            return false;

        String value = dc.getValue(this);
        int valLen = value.length();
        if(value != null && valLen > 0)
        {
            if(valLen == 13)
            {
                String phoneValueStr = null;

                try
                {
                    phoneValueStr = value.substring(1,4);
                    phoneValueStr = phoneValueStr + value.substring(5,8);
                    phoneValueStr = phoneValueStr + value.substring(10,13) ;
                }
                catch (Exception e)
                {
                    invalidate(dc, "'" + phoneValueStr + "' is not a valid phone number.");
                    return false;
                }


                if(phoneValueStr.length() != 10)
                    return false;

                try
                {
                    double phoneValue = Double.parseDouble(phoneValueStr);
                }
                catch(NumberFormatException e)
                {
    				invalidate(dc, "'" + value + "' is not a valid phone number.");
                    return false;
                }
            }
            else if(valLen == 10)
            {
                try
                {
                    double phoneValue = Double.parseDouble(value);
                }
                catch(NumberFormatException e)
                {
    				invalidate(dc, "'" + value + "' is not a valid phone number.");
                    return false;
                }
            }
            else
            {
   				invalidate(dc, "'" + value + "' is not a valid phone number.");
                return false;
            }
        }

        return true;
	}

}