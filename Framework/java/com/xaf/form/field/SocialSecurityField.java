package com.xaf.form.field;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class SocialSecurityField extends TextField
{
	static public final long FLDFLAG_STRIPDASHES = TextField.FLDFLAG_STARTCUSTOM;

    public SocialSecurityField()
    {
        super();
        setFlag(FLDFLAG_STRIPDASHES);
    }

	public void importFromXml(Element elem)
	{
        super.importFromXml(elem);
		String attr = elem.getAttribute("strip-dashes");
		if(attr.equals("no"))
            clearFlag(FLDFLAG_STRIPDASHES);
	}

	public boolean needsValidation(DialogContext dc)
	{
        return true;
	}

	public String formatValue(String value)
	{
        if(! flagIsSet(FLDFLAG_STRIPDASHES))
            return value;

        if(value == null)
            return value;

        if(value.length() != 11)
    		return value;

        StringBuffer ssnValueStr = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(value, "-");
        while(tokens.hasMoreTokens())
            ssnValueStr.append(tokens.nextToken());

        return ssnValueStr.toString();
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
            if(valLen == 11)
            {
                StringBuffer ssnValueStr = new StringBuffer();
                StringTokenizer tokens = new StringTokenizer(value, "-");
                while(tokens.hasMoreTokens())
                    ssnValueStr.append(tokens.nextToken());

                if(ssnValueStr.length() != 9)
                    return false;

                try
                {
                    double ssnValue = Double.parseDouble(ssnValueStr.toString());
                }
                catch(NumberFormatException e)
                {
    				invalidate(dc, "'" + value + "' is not a valid SSN.");
                    return false;
                }
            }
            else if(valLen == 9)
            {
                try
                {
                    double ssnValue = Double.parseDouble(value);
                }
                catch(NumberFormatException e)
                {
    				invalidate(dc, "'" + value + "' is not a valid SSN.");
                    return false;
                }
            }
            else
            {
   				invalidate(dc, "'" + value + "' is not a valid SSN.");
                return false;
            }
        }

        return true;
	}
}