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
 * $Id: ReportFrame.java,v 1.3 2002-10-13 18:39:45 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.report;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.xml.XmlSource;

public class ReportFrame
{
    static public class Item
    {
        private String icon;
        private String caption;
        private String url;

        public Item(String caption, String url)
        {
            this.caption = caption;
            this.url = url;
        }

        public Item(String caption, String url, String icon)
        {
            this.caption = caption;
            this.url = url;
            this.icon = icon;
        }

        public Item(String caption)
        {
            this.caption = caption;
        }

        public String getCaption()
        {
            return caption;
        }

        public String getUrl()
        {
            return url;
        }

        public String getIcon()
        {
            return icon;
        }
    }

    private SingleValueSource heading;
    private SingleValueSource headingExtra;
    private SingleValueSource footing;
    private SingleValueSource recordAddCaption;
    private SingleValueSource recordAddUrlFormat;
    private SingleValueSource recordEditUrlFormat;
    private SingleValueSource recordDeleteUrlFormat;
    private ArrayList items;

    public ReportFrame()
    {
        heading = null;
        footing = null;
    }

    public boolean hasHeadingOrFooting()
    {
        return heading != null || footing != null;
    }

    public SingleValueSource getHeading()
    {
        return heading;
    }

    public void setHeading(String value)
    {
        heading = value != null && value.length() > 0 ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public void setHeading(SingleValueSource vs)
    {
        heading = vs;
    }

    public SingleValueSource getHeadingExtra()
    {
        return headingExtra;
    }

    public void setHeadingExtra(String value)
    {
        headingExtra = value != null && value.length() > 0 ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public void setHeadingExtra(SingleValueSource headingExtra)
    {
        this.headingExtra = headingExtra;
    }

    public SingleValueSource getFooting()
    {
        return footing;
    }

    public void setFooting(String value)
    {
        footing = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public ArrayList getItems()
    {
        return items;
    }

    public Item getItem(int n)
    {
        return (Item) items.get(n);
    }

    public void addItem(Item item)
    {
        if(items == null) items = new ArrayList();
        items.add(item);
    }

    public SingleValueSource getRecordAddCaption()
    {
        return recordAddCaption;
    }

    public void setRecordAddCaption(SingleValueSource recordItemName)
    {
        this.recordAddCaption = recordItemName;
    }

    public void setRecordAddCaption(String value)
    {
        setRecordAddCaption((value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public SingleValueSource getRecordAddUrlFormat()
    {
        return recordAddUrlFormat;
    }

    public void setRecordAddUrlFormat(SingleValueSource RecordAddUrlFormat)
    {
        this.recordAddUrlFormat = RecordAddUrlFormat;
    }

    public void setRecordAddUrlFormat(String value)
    {
        setRecordAddUrlFormat((value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public SingleValueSource getRecordDeleteUrlFormat()
    {
        return recordDeleteUrlFormat;
    }

    public void setRecordDeleteUrlFormat(SingleValueSource RecordDeleteUrlFormat)
    {
        this.recordDeleteUrlFormat = RecordDeleteUrlFormat;
    }

    public void setRecordDeleteUrlFormat(String value)
    {
        setRecordDeleteUrlFormat((value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public SingleValueSource getRecordEditUrlFormat()
    {
        return recordEditUrlFormat;
    }

    public void setRecordEditUrlFormat(SingleValueSource RecordEditUrlFormat)
    {
        this.recordEditUrlFormat = RecordEditUrlFormat;
    }

    public void setRecordEditUrlFormat(String value)
    {
        setRecordEditUrlFormat((value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public void importFromXml(Element elem)
    {
        setHeading(XmlSource.getAttrValueOrTagText(elem, "heading", null));
        setHeadingExtra(XmlSource.getAttrValueOrTagText(elem, "heading-extra", null));
        setFooting(XmlSource.getAttrValueOrTagText(elem, "footing", null));

        setRecordAddCaption(XmlSource.getAttrValueOrTagText(elem, "record-add-caption", null));
        setRecordAddUrlFormat(XmlSource.getAttrValueOrTagText(elem, "record-add-url", null));
        setRecordEditUrlFormat(XmlSource.getAttrValueOrTagText(elem, "record-edit-url", null));
        setRecordDeleteUrlFormat(XmlSource.getAttrValueOrTagText(elem, "record-delete-url", null));
    }
}