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
 * $Id: RelationalCondition.java,v 1.1 2002-02-27 00:49:20 snshah Exp $
 */

package com.netspective.junxion.edi.format.igml.attributes;

import com.netspective.sparx.util.xml.EnumeratedAttribute;

public class RelationalCondition extends EnumeratedAttribute
{
    public final static String[] ENUM = new String[]
    {
        "R",
        "C",
        "E",
        "L",
        "P",
        "D1",
        "D2",
        "D3",
        "D4",
        "D5",
        "D6",
        "D7"
    };

    public final static String[] CAPTIONS = new String[]
    {
        "(R)equired - At least one of the Entity is required",
        "(C)onditional - If the first Entity is present, then all the other Entity are required",
        "(E)xclusion - Only one of the Entity can be present at a time.",
        "(L)ist Conditional - If the first Entity is present, then one of the remaining Entity must be present",
        "(P)aired - If any of the Entity are present, then all must be present",
        "D1 One and Only One - One and only one of the Entity can be used",
        "D2 All or None - If one of the Entity is used, then all of the Entity must be used",
        "D3 One or More - At least one of the Entity must be used",
        "D4 One or None - No more than one of the Entity can be used",
        "D5 If First, Then All - If the first Entity is used, then all the other Entity must be used",
        "D6 If First, Then At Least One More - If the first Entity is used, then at least one of the other Entity must be used",
        "D7 If First, Then If First, Then Others - If the first Entity is used, then none of the other Entity may be used"
    };

    public String[] getValues()
    {
        return ENUM;
    }
}
