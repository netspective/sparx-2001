/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: PhoneField.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;

public class PhoneField extends TextField
{
    static public final long FLDFLAG_STRIPBRACKETS = TextField.FLDFLAG_STARTCUSTOM;

    public static final String DASH_FORMAT = "dash";
    public static final String DASH_VALIDATE_PATTERN = "^([\\d][\\d][\\d])[\\.-]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})([ ][x][\\d]{1,5})?$";
    public static final String DASH_DISPLAY_PATTERN = "s/" + DASH_VALIDATE_PATTERN + "/$1-$2-$3$4/g";
    public static final String DASH_SUBMIT_PATTERN = "s/" + DASH_VALIDATE_PATTERN + "/$1$2$3$4/g";
    public static final String DASH_VALIDATE_ERROR_MSG = "Input must be in the 999-999-9999 x99999 format.";

    public static final String BRACKET_FORMAT = "bracket";
    public static final String BRACKET_VALIDATE_PATTERN = "^[\\(]?([\\d][\\d][\\d])[\\)]?[ ]?([\\d][\\d][\\d])[\\.-]?([\\d]{4})([ ][x][\\d]{1,5})?$";
    public static final String BRACKET_DISPLAY_PATTERN = "s/" + BRACKET_VALIDATE_PATTERN + "/($1) $2-$3$4/g";
    public static final String BRACKET_SUBMIT_PATTERN = "s/" + BRACKET_VALIDATE_PATTERN + "/$1$2$3$4/g";
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
        if(attr == null || attr.equals(PhoneField.DASH_FORMAT))
        {
            this.formatType = PhoneField.DASH_FORMAT;
            this.setValidatePattern("/" + DASH_VALIDATE_PATTERN + "/");
            this.setValidatePatternErrorMessage(DASH_VALIDATE_ERROR_MSG);
            this.setDisplaySubstitutionPattern(DASH_DISPLAY_PATTERN);
            this.setSubmitSubstitutePattern(DASH_SUBMIT_PATTERN);
        }
        else if(attr.equals(PhoneField.BRACKET_FORMAT))
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
        if(this.flagIsSet(FLDFLAG_STRIPBRACKETS))
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