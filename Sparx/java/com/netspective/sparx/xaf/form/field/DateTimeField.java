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
 * $Id: DateTimeField.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogContextMemberInfo;
import com.netspective.sparx.xaf.form.DialogField;

public class DateTimeField extends TextField
{
    static public final long FLDFLAG_FUTUREONLY = TextField.FLDFLAG_STARTCUSTOM;
    static public final long FLDFLAG_PASTONLY = FLDFLAG_FUTUREONLY * 2;
    static public final long FLDFLAG_MAX_LIMIT = FLDFLAG_PASTONLY * 2;
    static public final long FLDFLAG_MIN_LIMIT = FLDFLAG_MAX_LIMIT * 2;
    static public final long FLDFLAG_STRICT_YEAR = FLDFLAG_MIN_LIMIT * 2;
    static public final long FLDFLAG_STRICT_TIME = FLDFLAG_STRICT_YEAR * 2;
    static public final long FLDFLAG_POPUP_CALENDAR = FLDFLAG_STRICT_TIME * 2;

    static public final int DTTYPE_DATEONLY = 0;
    static public final int DTTYPE_TIMEONLY = 1;
    static public final int DTTYPE_BOTH = 2;

    static public String[] formats = new String[]{"MM/dd/yyyy", "HH:mm", "MM/dd/yyyy HH:mm"};

    static public final String CALJS_CONFIGITEM_NAME = com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "dialog.field.calendar.js-src";
    static public final String CALIMG_CONFIGITEM_NAME = com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "dialog.field.calendar.image-url";

    private int dataType;
    private SimpleDateFormat format;
    private SimpleDateFormat sqlFormat;
    private Date preDate = null;
    private Date postDate = null;
    private String maxDateStr = null;   /* maximum date String */
    private String minDateStr = null;   /* minimum date string */

    public DateTimeField()
    {
        super();
        setDataType(DTTYPE_DATEONLY);
    }

    public DateTimeField(String aName, String aCaption, int aType)
    {
        super(aName, aCaption);
        setDataType(aType);
    }

    public final int getDataType()
    {
        return dataType;
    }

    public void setDataType(int value)
    {
        dataType = value;
        format = new SimpleDateFormat(formats[dataType]);
        format.setLenient(false);
        sqlFormat = new SimpleDateFormat(formats[0]);
        setSize(formats[dataType].length());
        setMaxLength(getSize());
    }

    public final SimpleDateFormat getFormat()
    {
        return format;
    }

    public final Date getPreDate()
    {
        return preDate;
    }

    public void setPreDate(Date value)
    {
        preDate = value;
    }

    public final Date getPostDate()
    {
        return postDate;
    }

    public void setPostDate(Date value)
    {
        postDate = value;
    }

    public void setPrePostDate(Date low, Date high)
    {
        preDate = low;
        postDate = high;
    }

    public String getMaxDateStr()
    {
        return maxDateStr;
    }

    public void setMaxDateStr(String maxDateStr)
    {
        this.maxDateStr = maxDateStr;
    }

    public String getMinDateStr()
    {
        return minDateStr;
    }

    public void setMinDateStr(String minDateStr)
    {
        this.minDateStr = minDateStr;
    }

    public Object getValueAsObject(String value)
    {
        if(value == null || value.length() == 0)
            return null;
        try
        {
            if(dataType == DTTYPE_TIMEONLY)
                return new String(this.formatTimeValue(value));
            else
                return new Date(format.parse(value).getTime());
        }
        catch(ParseException e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    public Object getValueForSqlBindParam(String value)
    {
        try
        {
            if(dataType == DTTYPE_TIMEONLY)
                return new String(this.formatTimeValue(value));
            else
                return new java.sql.Date(format.parse(value).getTime());
        }
        catch(ParseException e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Strips the ":" from the Time field. Must only be used when the
     * DateTime field contains only time.
     *
     * @param value Time field string
     * @returns String formatted Time string
     */
    private String formatTimeValue(String value)
    {
        if(value == null)
            return value;

        StringBuffer timeValueStr = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(value, ":");
        while(tokens.hasMoreTokens())
            timeValueStr.append(tokens.nextToken());

        return timeValueStr.toString();
    }

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String nodeName = elem.getNodeName();
        if(nodeName.equals("field.datetime"))
            setDataType(DTTYPE_BOTH);
        else if(nodeName.equals("field.time"))
            setDataType(DTTYPE_TIMEONLY);

        // make sure the format setting is after the data type setting
        String value = elem.getAttribute("format");
        if(value != null && value.length() > 0)
        {
            this.format = new SimpleDateFormat(value);
            this.format.setLenient(false);
        }

        String maxDateTime = elem.getAttribute("max");
        if(maxDateTime == null || maxDateTime.length() == 0)
            maxDateTime = elem.getAttribute("max-value");
        if(maxDateTime != null && maxDateTime.length() > 0)
        {
            this.maxDateStr = maxDateTime;
            setFlag(FLDFLAG_MAX_LIMIT);
        }
        String minDateTime = elem.getAttribute("min");
        if(minDateTime == null || minDateTime.length() == 0)
            minDateTime = elem.getAttribute("min-value");
        if(minDateTime != null && minDateTime.length() > 0)
        {
            this.minDateStr = minDateTime;
            setFlag(FLDFLAG_MIN_LIMIT);
        }

        if(elem.getAttribute("future-only").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_REQUIRED);

        if(elem.getAttribute("past-only").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_READONLY);

        String strictFlag = elem.getAttribute("strict-year");
        if(strictFlag != null && strictFlag.equals("no"))
            clearFlag(FLDFLAG_STRICT_YEAR);
        else
            setFlag(FLDFLAG_STRICT_YEAR);
        String strictTimeFlag = elem.getAttribute("strict-time");
        if(strictTimeFlag != null && strictTimeFlag.equals("no"))
            clearFlag(FLDFLAG_STRICT_TIME);
        else
            setFlag(FLDFLAG_STRICT_TIME);

        if(elem.getAttribute("popup-calendar").equalsIgnoreCase("yes"))
            setFlag(FLDFLAG_POPUP_CALENDAR);
    }

    public boolean isValid(DialogContext dc)
    {
        boolean textValid = super.isValid(dc);
        if(!textValid) return false;

        String strValue = dc.getValue(this);
        if(!isRequired(dc) && (strValue == null || strValue.length() == 0))
            return true;

        Date value = null;
        try
        {
            value = format.parse(strValue);
        }
        catch(Exception e)
        {
            invalidate(dc, "'" + strValue + "' is not valid (format is " + format.toPattern() + ").");
            return false;
        }

        try
        {
            if(flagIsSet(FLDFLAG_MAX_LIMIT))
            {
                Date maxDate = format.parse(this.maxDateStr);
                if(value.after(maxDate))
                {
                    invalidate(dc, getCaption(dc) + " must not be greater than " + maxDateStr + ".");
                    return false;
                }
            }
            if(flagIsSet(FLDFLAG_MIN_LIMIT))
            {
                Date minDate = format.parse(this.minDateStr);
                if(value.before(minDate))
                {
                    invalidate(dc, getCaption(dc) + " must not be less than " + minDateStr + ".");
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            invalidate(dc, "Maximum or minimum date '" + this.maxDateStr + "' is not valid (format is " + formats[dataType] + ").");
            e.printStackTrace();
            return false;
        }

        Date now = new Date();
        long flags = getFlags();
        if((flags & FLDFLAG_FUTUREONLY) != 0 && value.before(now))
        {
            invalidate(dc, getCaption(dc) + " must be in the future.");
            return false;
        }
        if((flags & FLDFLAG_PASTONLY) != 0 && value.after(now))
        {
            invalidate(dc, getCaption(dc) + " must be in the past.");
            return false;
        }
        if(preDate != null && value.after(preDate))
        {
            invalidate(dc, getCaption(dc) + " must be after " + preDate + ".");
            return false;
        }
        if(postDate != null && value.before(postDate))
        {
            invalidate(dc, getCaption(dc) + " must be before " + postDate + ".");
            return false;
        }

        return true;
    }

    /**
     * Overwrites DialogField's getCustomJavaScriptDefn()
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        StringBuffer buf = new StringBuffer(super.getCustomJavaScriptDefn(dc));
        buf.append("field.dateDataType = " + this.getDataType() + ";\n");
        buf.append("field.dateFormat = '" + this.getFormat().toPattern() + "';\n");

        if(this.flagIsSet(DateTimeField.FLDFLAG_STRICT_YEAR))
            buf.append("field.dateStrictYear = true;\n");
        else
            buf.append("field.dateStrictYear = false;\n");

        if(this.getDataType() == DTTYPE_TIMEONLY)
        {
            if(this.flagIsSet(DateTimeField.FLDFLAG_STRICT_TIME))
                buf.append("field.timeStrict = true;\n");
            else
                buf.append("field.timeStrict = false;\n");
        }

        return buf.toString();
    }

    /**
     * Populates a default value into the dialog field. Overwites TextField.populateValue().
     *
     * @param dc Dialog context
     */
    public void populateValue(DialogContext dc, int formatType)
    {
        super.populateValue(dc, formatType);
        String value = dc.getValue(this);

        String xlatedDate = null;
        switch(this.dataType)
        {
            case DateTimeField.DTTYPE_DATEONLY:
            case DateTimeField.DTTYPE_BOTH:
                xlatedDate = this.translateDateString(value);
                break;
            case DateTimeField.DTTYPE_TIMEONLY:
                xlatedDate = this.translateTimeString(value);
                break;
            default:
                break;
        }

        if(xlatedDate != null)
            dc.setValue(this, xlatedDate);

    }

    /**
     * Translates a reserved date word such as "today" or "now" into the actual time
     *
     * @param str reserved string
     * @returns String actual time string
     */
    public String translateTimeString(String str)
    {
        String xlatedDate = str;

        if(str != null && (str.startsWith("today") || str.startsWith("now")))
        {
            Date dt = new Date();
            xlatedDate = this.format.format(dt);
        }
        return xlatedDate;
    }

    /**
     * Translates a reserved date word such as "today" or "now" into the actual date
     *
     * @param str reserved string
     * @returns String actual date string
     */
    public String translateDateString(String str)
    {
        String xlatedDate = str;

        if(str != null && (str.startsWith("today") || str.startsWith("now")))
        {
            int strLength = 0;
            if(str.startsWith("today"))
                strLength = "today".length();
            else
                strLength = "now".length();
            Date dt = null;
            if(str.length() > strLength)
            {
                try
                {
                    String opValueStr = null;
                    if(str.charAt(strLength) == '+')
                        opValueStr = str.substring(strLength + 1);
                    else
                        opValueStr = str.substring(strLength);
                    int opValue = Integer.parseInt(opValueStr);
                    Calendar calendar = new GregorianCalendar();
                    calendar.add(Calendar.DAY_OF_MONTH, opValue);
                    dt = calendar.getTime();
                    xlatedDate = this.format.format(dt);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                dt = new Date();
                xlatedDate = this.format.format(dt);
            }
        }
        return xlatedDate;
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        super.renderControlHtml(writer, dc);
        if(flagIsSet(FLDFLAG_INPUT_HIDDEN) || !flagIsSet(FLDFLAG_POPUP_CALENDAR))
            return;

        Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(dc.getServletContext());
        String calScriptUrl = config.getTextValue(dc, CALJS_CONFIGITEM_NAME, "/shared/resources/scripts/calendar.js");
        String calImageUrl = config.getTextValue(dc, CALIMG_CONFIGITEM_NAME, "/shared/resources/images/navigate/calendar.gif");

        writer.write("<script src='" + calScriptUrl + "'></script> <a href='javascript:activeDialog.fieldsByQualName[\"" + this.getQualifiedName() + "\"].popupCalendar()'><img src='" + calImageUrl + "' title='Select from Calendar' border=0></a>");
    }

    /**
     * Produces Java code when a custom DialogContext is created
     */
    public DialogContextMemberInfo getDialogContextMemberInfo()
    {
        DialogContextMemberInfo mi = createDialogContextMemberInfo("Date");
        mi.addImportModule("java.util.Date");
        String fieldName = mi.getFieldName();
        String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "() { return (Date) getValueAsObject(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic " + dataType + " get" + memberName + "(" + dataType + " defaultValue) { return (Date) getValueAsObject(\"" + fieldName + "\", defaultValue); }\n");

        return mi;
    }
}
