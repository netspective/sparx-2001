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
    public static final String PATTERN_MATCH  = "^([\\d]{3})[-]?([\\d]{2})[-]?([\\d]{4})$";

    public SocialSecurityField()
    {
        super();
        setFlag(FLDFLAG_STRIPDASHES);
        setValidatePattern("/" + PATTERN_MATCH + "/");
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

}