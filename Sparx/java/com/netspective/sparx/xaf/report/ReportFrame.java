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
 * $Id: ReportFrame.java,v 1.5 2003-02-24 03:46:04 aye.thu Exp $
 */

package com.netspective.sparx.xaf.report;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.xml.XmlSource;

public class ReportFrame
{
    public final static long RPTFRAMEFLAG_HAS_HEADING = 1;
    public final static long RPTFRAMEFLAG_HAS_HEADINGEXTRA = RPTFRAMEFLAG_HAS_HEADING * 2;
    public final static long RPTFRAMEFLAG_HAS_FOOTING = RPTFRAMEFLAG_HAS_HEADINGEXTRA * 2;
    public final static long RPTFRAMEFLAG_HAS_ADD = RPTFRAMEFLAG_HAS_FOOTING * 2;
    public final static long RPTFRAMEFLAG_HAS_EDIT = RPTFRAMEFLAG_HAS_ADD * 2;
    public final static long RPTFRAMEFLAG_HAS_DELETE = RPTFRAMEFLAG_HAS_EDIT * 2;

    private SingleValueSource heading;
    private SingleValueSource headingExtra;
    private SingleValueSource footing;
    private SingleValueSource recordAddCaption;
    private SingleValueSource recordAddUrlFormat;
    private SingleValueSource recordEditUrlFormat;
    private SingleValueSource recordDeleteUrlFormat;
    private long flags;
    private ArrayList items;
    private boolean collapseable;
    private boolean collapsed;

    public ReportFrame()
    {
        heading = null;
        footing = null;
    }

    /**
     * Whther or not the report frame is collapseable
     * @return
     */
    public boolean allowCollapse()
    {
        return collapseable;
    }

    /**
     * Whether or not the report frame is minimized/maximized. This flag is only valid when the report is allowed
     * to be collapse (minimized).
     * @return
     */
    public boolean isCollapsed()
    {
        return collapsed;
    }

    public long getFlags()
    {
        return flags;
    }

    public void setFlags(long flags)
    {
        this.flags = flags;
    }

    public final void setFlag(long flag)
    {
        flags |= flag;
    }

    public final void clearFlag(long flag)
    {
        flags &= ~flag;
    }

    public final void applyFlag(long flag, boolean apply)
    {
        if(apply)
            flags |= flag;
        else
            flags &= ~flag;
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
        setHeading(value != null && value.length() > 0 ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public void setHeading(SingleValueSource vs)
    {
        heading = vs;
        applyFlag(RPTFRAMEFLAG_HAS_HEADING, heading != null);
    }

    public SingleValueSource getHeadingExtra()
    {
        return headingExtra;
    }

    public void setHeadingExtra(String value)
    {
        setHeadingExtra(value != null && value.length() > 0 ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    public void setHeadingExtra(SingleValueSource headingExtra)
    {
        this.headingExtra = headingExtra;
        applyFlag(RPTFRAMEFLAG_HAS_HEADINGEXTRA, headingExtra != null);
    }

    public SingleValueSource getFooting()
    {
        return footing;
    }

    public void setFooting(String value)
    {
        footing = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
        applyFlag(RPTFRAMEFLAG_HAS_FOOTING, footing != null);
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
        recordAddCaption = recordItemName;
        applyFlag(RPTFRAMEFLAG_HAS_ADD, recordAddCaption != null);
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
        applyFlag(RPTFRAMEFLAG_HAS_ADD, recordAddUrlFormat != null);
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
        applyFlag(RPTFRAMEFLAG_HAS_DELETE, recordDeleteUrlFormat != null);
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
        applyFlag(RPTFRAMEFLAG_HAS_EDIT, recordEditUrlFormat != null);
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

        String collapseableFlag = XmlSource.getAttrValueOrTagText(elem, "allow-minimize", null);
        if (collapseableFlag != null && collapseableFlag.equalsIgnoreCase("yes"))
        {
            collapseable = true;
            String minimized = XmlSource.getAttrValueOrTagText(elem, "minimized", null);
            if (minimized != null && minimized.equalsIgnoreCase("yes"))
                collapsed = true;
            else
                collapsed = false;
        }
        else
        {
            collapseable = false;
        }
    }

    /**
     * Import report heading actions from XML
     * @param elem
     */
    public void importHeadingActions(Element elem)
    {
        NodeList children = elem.getChildNodes();

        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.equals("item"))
            {
                String caption = ((Element)node).getAttribute("caption");
                if (caption == null || caption.length() == 0)
                    continue;

                ReportFrame.Item item = new ReportFrame.Item(caption);
                item.setUrl(((Element)node).getAttribute("url"));
                item.setIcon(((Element)node).getAttribute("icon"));
                addItem(item);
            }
        }
    }

    static public class Item
    {
       private SingleValueSource icon;
        private SingleValueSource caption;
        private SingleValueSource url;

        public Item(String caption, String url)
        {
            this.caption = ValueSourceFactory.getSingleOrStaticValueSource(caption);
            this.url = ValueSourceFactory.getSingleOrStaticValueSource(url);
        }

        public Item(String caption, String url, String icon)
        {
            this.caption = ValueSourceFactory.getSingleOrStaticValueSource(caption);
            this.url = ValueSourceFactory.getSingleOrStaticValueSource(url);
            this.icon = ValueSourceFactory.getSingleOrStaticValueSource(icon);
        }

        public Item(String caption)
        {
            this.caption = ValueSourceFactory.getSingleOrStaticValueSource(caption);
        }

        public Item(SingleValueSource caption, SingleValueSource url, SingleValueSource icon)
        {
            this.caption = caption;
            this.url = url;
            this.icon = icon;
        }

        public SingleValueSource getCaption()
        {
            return caption;
        }

        public void setCaption(String value)
        {
            caption = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null);
        }

        public SingleValueSource getUrl()
        {
            return url;
        }

        public void setUrl(String value)
        {
            url = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null);
        }

        public SingleValueSource getIcon()
        {
            return icon;
        }

        public void setIcon(String value)
        {
            icon = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null);
        }
    }
}