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
 * $Id: SocialSecurityField.java,v 1.2 2002-03-26 17:57:52 eoliphan Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;

public class SocialSecurityField extends TextField
{
    static public final long FLDFLAG_STRIPDASHES = TextField.FLDFLAG_STARTCUSTOM;
    public static final String VALIDATE_PATTERN = "^([\\d]{3})[-]?([\\d]{2})[-]?([\\d]{4})$";
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
     * @return String
     */
    public String formatSubmitValue(String value)
    {
        if(!flagIsSet(FLDFLAG_STRIPDASHES))
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