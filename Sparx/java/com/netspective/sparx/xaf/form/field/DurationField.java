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
 * $Id: DurationField.java,v 1.2 2002-10-13 21:19:12 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.util.Date;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class DurationField extends DialogField
{
    static public final int DTTYPE_DATEONLY = 0;
    static public final int DTTYPE_TIMEONLY = 1;
    static public final int DTTYPE_BOTH = 2;

    protected DateTimeField beginField;
    protected DateTimeField endField;

    public DurationField()
    {
        super();
    }

    public DurationField(String aName, String aCaption, int aType)
    {
        super(aName, aCaption);
        beginField = new DateTimeField("begin", "Begin", aType);
        endField = new DateTimeField("end", "End", aType);

        addChildField(beginField);
        addChildField(endField);
    }

    public DateTimeField getBeginField()
    {
        return beginField;
    }

    public DateTimeField getEndField()
    {
        return endField;
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String nodeName = elem.getNodeName();
        int type = DTTYPE_DATEONLY;

        if(nodeName.equals("datetime"))
            type = DTTYPE_BOTH;
        else if(nodeName.equals("time"))
            type = DTTYPE_TIMEONLY;

        String name = getSimpleName();
        beginField = new DateTimeField(name + "_begin", "Begin", type);
        endField = new DateTimeField(name + "_end", "End", type);

        // see if the default value attributes are set
        String value = elem.getAttribute("default-begin");
        if(value != null && value.length() != 0)
            beginField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(value));
        else
            beginField.setDefaultValue(this.getDefaultValue());

        value = elem.getAttribute("default-end");
        if(value != null && value.length() != 0)
            endField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(value));
        else
            endField.setDefaultValue(this.getDefaultValue());

        value = elem.getAttribute("begin-min-value");
        if(value != null && value.length() != 0)
        {
            this.beginField.setMinDateStr(this.beginField.translateDateString(value));
        }

        value = elem.getAttribute("end-max-value");
        if(value != null && value.length() != 0)
        {
            this.endField.setMaxDateStr(this.endField.translateDateString(value));
        }

        if(elem.getAttribute("popup-calendar").equalsIgnoreCase("yes"))
        {
            beginField.setFlag(DateTimeField.FLDFLAG_POPUP_CALENDAR);
            endField.setFlag(DateTimeField.FLDFLAG_POPUP_CALENDAR);
        }

        if(flagIsSet(FLDFLAG_COLUMN_BREAK_AFTER))
            beginField.setFlag(FLDFLAG_COLUMN_BREAK_AFTER);

        if(flagIsSet(FLDFLAG_COLUMN_BREAK_BEFORE))
            beginField.setFlag(FLDFLAG_COLUMN_BREAK_BEFORE);

        addChildField(beginField);
        addChildField(endField);
    }

    public boolean isValid(DialogContext dc)
    {
        boolean beginValid = beginField.isValid(dc);
        boolean endValid = endField.isValid(dc);

        if(!beginValid || !endValid)
            return false;

        String strBeginValue = dc.getValue(beginField);
        String strEndValue = dc.getValue(endField);

        boolean required = isRequired(dc);
        if(!required && (strBeginValue == null || strBeginValue.length() == 0))
            return true;
        if(!required && (strEndValue == null || strEndValue.length() == 0))
            return true;

        Date beginDate, endDate;
        try
        {
            beginDate = beginField.getFormat().parse(dc.getValue(beginField));
            endDate = endField.getFormat().parse(dc.getValue(endField));

            if(beginDate.after(endDate))
            {
                invalidate(dc, "Beginning value should be before ending value.");
                return false;
            }

        }
        catch(Exception e)
        {
            invalidate(dc, "One of the values is invalid. This error should never happen.");
            return false;
        }

        try
        {
            String maxDateStr = this.endField.getMaxDateStr();
            if(maxDateStr != null)
            {
                Date maxDate = endField.getFormat().parse(maxDateStr);
                if(endDate.after(maxDate))
                {
                    invalidate(dc, endField.getCaption(dc) + " must not be greater than " + maxDateStr + ".");
                    return false;
                }
            }
            String minDateStr = this.beginField.getMinDateStr();
            if(minDateStr != null)
            {
                Date minDate = beginField.getFormat().parse(minDateStr);
                if(beginDate.before(minDate))
                {
                    invalidate(dc, beginField.getCaption(dc) + " must not be less than " + minDateStr + ".");
                    return false;
                }
            }

        }
        catch(Exception e)
        {
            invalidate(dc, "One of the min and max values is invalid. This error should never happen.");
            e.printStackTrace();
            return false;
        }


        return super.isValid(dc);
    }

    /**
     * Populates a default value into the dialog field. Overwites TextField.populateValue().
     *
     * @param dc Dialog context
     */
    public void populateValue(DialogContext dc, int formatType)
    {
        super.populateValue(dc, formatType);
        String xlatedDate = null;

        String beginValue = dc.getValue(this.beginField);
        if(beginValue != null)
        {
            xlatedDate = this.beginField.translateDateString(beginValue);
            dc.setValue(this.beginField, xlatedDate);
        }

        String endValue = dc.getValue(this.endField);
        if(endValue != null)
        {
            xlatedDate = this.endField.translateDateString(endValue);
            dc.setValue(this.endField, xlatedDate);
        }


    }

}
