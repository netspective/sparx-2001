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

    public static final String DASH_FORMAT = "dash";
	public static final String DASH_VALIDATE_PATTERN  = "^([\\d][\\d][\\d])[\\.-]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})([ ][x][\\d]{1,5})?$";
    public static final String DASH_DISPLAY_PATTERN = "s/" + DASH_VALIDATE_PATTERN + "/$1-$2-$3$4/g";
    public static final String DASH_SUBMIT_PATTERN = "s/" + DASH_VALIDATE_PATTERN + "/$1$2$3$4/g";
    public static final String DASH_VALIDATE_ERROR_MSG =  "Input must be in the 999-999-9999 x99999 format.";

    public static final String BRACKET_FORMAT = "bracket";
    public static final String BRACKET_VALIDATE_PATTERN = "^[\\(]?([\\d][\\d][\\d])[\\)]?[ ]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})([ ][x][\\d]{1,5})?$";
    public static final String BRACKET_DISPLAY_PATTERN =  "s/" + BRACKET_VALIDATE_PATTERN + "/($1) $2-$3$4/g";
    public static final String BRACKET_SUBMIT_PATTERN =  "s/" + BRACKET_VALIDATE_PATTERN + "/$1$2$3$4/g";
    public static final String BRACKET_VALIDATE_ERROR_MSG = "Input must be in the (999)999-9999 x99999 format.";

    private String formatType;

    public PhoneField()
    {
        super();
        this.setFlag(FLDFLAG_STRIPBRACKETS);
        // set the dafault regex pattern for the phone field
        this.setValidatePattern("/" + DASH_VALIDATE_PATTERN + "/");
        this.setValidatePatternErrorMessage(DASH_VALIDATE_ERROR_MSG);
        this.setDisplaySubstitutionPattern(DASH_DISPLAY_PATTERN);
        this.setSubmitSubstitutePattern(DASH_SUBMIT_PATTERN);
        this.formatType = DASH_FORMAT;
    }

    public void importFromXml(Element elem)
	{
        super.importFromXml(elem);
		String attr = elem.getAttribute("strip-brackets");
		if(attr.equals("no"))
            clearFlag(FLDFLAG_STRIPBRACKETS);

        attr = elem.getAttribute("format-type");
        if (attr == null || attr.equals(PhoneField.DASH_FORMAT))
        {
            this.formatType = PhoneField.DASH_FORMAT;
            this.setValidatePattern("/" + DASH_VALIDATE_PATTERN + "/");
            this.setValidatePatternErrorMessage(DASH_VALIDATE_ERROR_MSG);
            this.setDisplaySubstitutionPattern(DASH_DISPLAY_PATTERN);
            this.setSubmitSubstitutePattern(DASH_SUBMIT_PATTERN);
        }
        else if (attr.equals(PhoneField.BRACKET_FORMAT))
        {
            this.formatType = PhoneField.BRACKET_FORMAT;
            this.setValidatePattern("/" + BRACKET_VALIDATE_PATTERN + "/");
            this.setValidatePatternErrorMessage(BRACKET_VALIDATE_ERROR_MSG);
            this.setDisplaySubstitutionPattern(BRACKET_DISPLAY_PATTERN);
            this.setSubmitSubstitutePattern(BRACKET_SUBMIT_PATTERN);
        }
	}

    /**
     * Formats the phone value only if the strip brackets flag is set
     * else returns the passed in value
     */
    public String formatSubmitValue(String value)
    {
        if (this.flagIsSet(FLDFLAG_STRIPBRACKETS))
            return super.formatSubmitValue(value);
        else
            return value;
    }

    /**
     *  Passes on the phone format to the client side validations
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        StringBuffer buf = new StringBuffer(super.getCustomJavaScriptDefn(dc));
        buf.append("field.phone_format_type = '" + this.formatType + "';\n");
        return buf.toString();
    }
}