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
	static public final String PATTERN_MATCHPHONE  = "^([\\d][\\d][\\d])[\\.-]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})[ ]?([x][\\d]{1,5})?$";

    public PhoneField()
    {
        super();
        setFlag(FLDFLAG_STRIPBRACKETS);
        // set the dafault regex pattern for the phone field
        setValidatePattern("/" + PATTERN_MATCHPHONE + "/");
        setValidatePatternErrorMessage("Input must be in the 999-999-9999 x99999 format.");
    }

    public void importFromXml(Element elem)
	{
        super.importFromXml(elem);
		String attr = elem.getAttribute("strip-brackets");
		if(attr.equals("no"))
            clearFlag(FLDFLAG_STRIPBRACKETS);

        if(flagIsSet(FLDFLAG_STRIPBRACKETS))
		{
			setSubstitutePattern("s/" + PATTERN_MATCHPHONE + "/$1$2$3$4/g");
		}
	}
}