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
 * $Id: Format.java,v 1.2 2002-03-03 12:03:15 snshah Exp $
 */

package com.netspective.junxion.edi.format.igml;

import java.io.File;

import com.netspective.sparx.util.xml.DataModelSchema;
import com.netspective.sparx.util.xml.DataModelException;
import com.netspective.sparx.util.xml.DataModel;
import com.netspective.junxion.edi.format.igml.util.TextContainer;

public class Format implements DataModel
{
    /**
     * The root element of the IGML source is an element called <Standard>
     */
    private Standard standard;

    /**
     * The root element of the IGML source is an element called <Standard>
     */
    public TextContainer createStandard() throws DataModelException
    {
        if(standard != null)
            throw new DataModelException("Only a single <Standard> is allowed in any given Format");
        standard = new Standard();
        return standard;
    }

    public Standard getStandard()
    {
        return standard;
    }

    public void setStandard(Standard standard)
    {
        this.standard = standard;
    }

    public static void main(String[] args)
    {
        /* Introspect the Format class so that all the XML structures will be known */
        DataModelSchema.getSchema(Format.class);

        /* Parse the given file into the object model */
        Format format = new Format();
        DataModelSchema.ParseContext pc = DataModelSchema.parse(format, new File("c:/Projects/Sparx/web-shared/resources/edi/formats/IGML/IGML4010.xml"));
        System.out.println(pc.getSyntaxErrors().size());

        System.out.println(format.getStandard());
        System.out.println(format.getStandard().getId());
        System.out.println(format.getStandard().getTexts().size());
    }
}
