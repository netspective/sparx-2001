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
 * $Id: ReportBanner.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.report;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.skin.HtmlReportSkin;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class ReportBanner
{
    static public class Item
    {
        private SingleValueSource icon;
        private SingleValueSource caption;
        private SingleValueSource url;
        private Items childItems;

        public Item()
        {
        }

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

        public Items getChildItems()
        {
            return childItems;
        }

        public void importFromXml(Element elem)
        {
            setCaption(elem.getAttribute("caption"));
            setUrl(elem.getAttribute("url"));
            setIcon(elem.getAttribute("icon"));

            if(elem.getElementsByTagName("item").getLength() > 0)
            {
                childItems = new Items();
                childItems.importFromXml(elem);
            }
        }
    }

    static public class Items extends ArrayList
    {
        public static final short LAYOUTSTYLE_HORIZONTAL = 0;
        public static final short LAYOUTSTYLE_VERTICAL = 1;

        private short style = LAYOUTSTYLE_VERTICAL;
        private String separator = ", ";

        public void setStyle(short value)
        {
            style = value;
        }

        public boolean isHorizontalStyle()
        {
            return style == LAYOUTSTYLE_HORIZONTAL;
        }

        public boolean isVerticalStyle()
        {
            return style == LAYOUTSTYLE_VERTICAL;
        }

        public Item getItem(int n)
        {
            return (Item) get(n);
        }

        public void importFromXml(Element elem)
        {
            if(elem.getAttribute("style").equals("horizontal"))
                style = LAYOUTSTYLE_HORIZONTAL;

            String sep = elem.getAttribute("separator");
            if(sep.length() > 0)
                separator = sep;

            NodeList children = elem.getChildNodes();
            int columnIndex = 0;

            for(int n = 0; n < children.getLength(); n++)
            {
                Node node = children.item(n);
                if(node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                String childName = node.getNodeName();
                if(childName.equals("item"))
                {
                    Item item = new Item();
                    item.importFromXml((Element) node);
                    add(item);
                }
            }

            if(size() == 0)
                add(new Item("No banner items specified"));
        }

        public void produceHtml(Writer writer, ReportContext rc) throws IOException
        {
            int itemsCount = size();
            if(itemsCount == 0) return;
            String bannerItemFontAttrs = ((HtmlReportSkin) rc.getSkin()).getBannerItemFontAttrs();

            if(style == LAYOUTSTYLE_HORIZONTAL)
            {
                for(int i = 0; i < itemsCount; i++)
                {
                    Item item = (Item) get(i);
                    SingleValueSource itemUrl = item.getUrl();
                    SingleValueSource itemCaption = item.getCaption();
                    SingleValueSource itemIcon = item.getIcon();
                    String caption = itemCaption != null ? (itemUrl != null ? ("<a href='" + itemUrl.getValue(rc) + "'>" + itemCaption.getValue(rc) + "</a>") : itemCaption.getValue(rc)) : null;

                    writer.write("<font " + bannerItemFontAttrs + ">");
                    if(i > 0)
                        writer.write(separator);
                    if(itemIcon != null)
                        writer.write("<img src='" + itemIcon.getValue(rc) + "'>");
                    writer.write(caption);
                    writer.write("</font>");
                }
            }
            else
            {
                writer.write("<table border=0 cellspacing=0>");
                for(int i = 0; i < itemsCount; i++)
                {
                    Item item = (Item) get(i);
                    SingleValueSource itemUrl = item.getUrl();
                    SingleValueSource itemCaption = item.getCaption();
                    SingleValueSource itemIcon = item.getIcon();
                    Items childItems = item.getChildItems();
                    String caption = itemCaption != null ? (itemUrl != null ? ("<a href='" + itemUrl.getValue(rc) + "'>" + itemCaption.getValue(rc) + "</a>") : itemCaption.getValue(rc)) : null;

                    writer.write("<tr><td>");
                    writer.write(itemIcon != null ? "<img src='" + itemIcon.getValue(rc) + "'>" : "-");
                    writer.write("</td>");
                    writer.write("<td><font " + bannerItemFontAttrs + ">");
                    if(caption != null)
                        writer.write(caption);
                    if(childItems != null)
                        childItems.produceHtml(writer, rc);
                    writer.write("</font></td>");
                    writer.write("</tr>");
                }
                writer.write("</table>");
            }
        }
    }

    private Items items = new Items();

    public ReportBanner()
    {
    }

    public Items getItems()
    {
        return items;
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public void importFromXml(Element elem)
    {
        items.importFromXml(elem);
    }

    public void produceHtml(Writer writer, ReportContext rc) throws IOException
    {
        items.produceHtml(writer, rc);
    }
}