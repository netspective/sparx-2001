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
        // set the dafault regex pattern for the phone field
        setValidatePattern("/^([\\d][\\d][\\d])[\\.-]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})[ ]?([x][\\d]{1,5})?$/");
        setValidatePatternErrorMessage("Input must be in the 999-999-9999 x99999 format.");
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

        String retValue = value;

        try
        {
            String[] subgroups = this.patternMatches(value);
            if (subgroups != null && subgroups.length > 0)
            {
                retValue = "";
                for (int i=0; i < subgroups.length; i++)
                {
                    if (subgroups[i] != null)
                        retValue += subgroups[i];
                }
            }

        }
        catch (Exception mpe)
        {
            mpe.printStackTrace();

        }
        return retValue;

	}
	public boolean isValid(DialogContext dc)
	{
        boolean result = super.isValid(dc);
        if(! result)
            return false;


        return true;
	}

}