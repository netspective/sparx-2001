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
    public static final String VALIDATE_PATTERN  = "^([\\d]{3})[-]?([\\d]{2})[-]?([\\d]{4})$";
    public static final String DISPLAY_SUBSTITUTION_PATTERN = "s/" + VALIDATE_PATTERN + "/$1-$2-$3/g";
    public static final String SUBMIT_SUBSTITUTION_PATTERN = "/s" + VALIDATE_PATTERN + "/$1$2$3/g";

    public SocialSecurityField()
    {
        super();
        setFlag(FLDFLAG_STRIPDASHES);
        setValidatePattern("/" + VALIDATE_PATTERN + "/");
        setDisplaySubstitutionPattern(DISPLAY_SUBSTITUTION_PATTERN);
        setSubmitSubstitutePattern(SUBMIT_SUBSTITUTION_PATTERN);
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

    /**
     * Format the SSN value by stripping the dashes if <b>strip-dashes</b> attribute is set
     * to "yes" (after it has been validated and is ready for submission). Currently, this
     * method is not using the regular expression for formatting the submittal value.
     *
     * @param value dialog field value
     * @returns String
     */
	public String formatSubmitValue(String value)
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

}