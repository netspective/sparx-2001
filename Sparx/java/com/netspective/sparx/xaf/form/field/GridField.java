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
 * $Id: GridField.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class GridField extends DialogField
{
    ListValueSource captions;

    public GridField()
    {
        super();
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String captionsStr = elem.getAttribute("captions");
        if(captionsStr.length() > 0)
            captions = ValueSourceFactory.getListValueSource(captionsStr);
    }

    public ListValueSource getCaptionsSource()
    {
        return captions;
    }

    public String[] getCaptions(DialogContext dc)
    {
        String[] result = null;

        if(captions == null)
        {
            List rows = getChildren();
            if(rows == null)
                return null;

            DialogField firstRow = (DialogField) rows.get(0);
            if(firstRow == null)
                return null;

            List firstRowChildren = firstRow.getChildren();
            result = new String[firstRowChildren.size()];

            Iterator i = firstRowChildren.iterator();
            int captionIndex = 0;
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                if(field.isVisible(dc))
                    result[captionIndex] = field.getCaption(dc);
                captionIndex++;
            }
        }
        else
        {
            result = captions.getValues(dc);
        }
        return result;
    }

    public void setCaptionsSource(ListValueSource value)
    {
        captions = value;
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        dc.getSkin().renderGridControlsHtml(writer, dc, this);
    }
}