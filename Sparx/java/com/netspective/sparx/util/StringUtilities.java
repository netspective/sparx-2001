package com.netspective.sparx.util;

import com.netspective.sparx.xaf.form.DialogContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
 * @author Shahbaz Javeed
 */

/**
 * $Id: StringUtilities.java,v 1.1 2003-01-30 16:07:44 shahbaz.javeed Exp $
 */
public class StringUtilities
{
    public static String MESSAGE_DIGEST_SHA = "SHA";
    public static String MESSAGE_DIGEST_MD2 = "MD2";
    public static String MESSAGE_DIGEST_MD5 = "MD5";

    public static String md5Hash(String algorithm, String input)
    {
        String output = "";

        MessageDigest md = null;
        byte[] digest = null;

        try
        {
            md = MessageDigest.getInstance(algorithm);
            md.update(input.getBytes());
            digest = md.digest();
        }
        catch (NoSuchAlgorithmException e)
        {
            output = null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i ++)
        {
            int b = digest[i] & 0xFF;
            if (0x10 > b) sb.append('0');
            sb.append(Integer.toHexString(b));
        }

        if (sb.equals(new StringBuffer()))
        {
            output = null;
        }
        else
        {
            output = sb.toString();
        }

        return output;
    }

    public static String convertStringsToTextSet(String[] values)
    {
        if (null == values) return null;

        String returnValue = "";

        for(int i = 0; i < values.length; i ++)
        {
            if (i == 0)
                returnValue += values[i];
            else
                returnValue += "," + values[i];
        }

        return returnValue;
    }

    public static String[] convertTextSetToStrings(String values)
    {
        if (null == values) return null;

        ArrayList textSetItem = new ArrayList();
        StringTokenizer st = new StringTokenizer(values, ",");

        while(st.hasMoreTokens())
        {
            textSetItem.add(st.nextToken().trim());
        }

        String[] valueList = new String[textSetItem.size()];

        for (int i = 0; i < textSetItem.size(); i ++)
        {
            valueList[i] = (String) textSetItem.get(i);
        }

        return valueList;
    }
}
