package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueSourceFactory;
import com.xaf.skin.HtmlReportSkin;

import java.util.*;
import java.io.Writer;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

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

        public SingleValueSource getCaption() { return caption; }
        public void setCaption(String value) { caption = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null); }

        public SingleValueSource getUrl() { return url; }
        public void setUrl(String value) { url = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null); }

        public SingleValueSource getIcon() { return icon; }
        public void setIcon(String value) { icon = (value != null && value.length() > 0 ? (ValueSourceFactory.getSingleOrStaticValueSource(value)) : null); }

        public Items getChildItems() { return childItems; }

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
        public static final short LAYOUTSTYLE_VERTICAL   = 1;

        private short style = LAYOUTSTYLE_VERTICAL;
        private String separator = ", ";

        public void setStyle(short value) { style = value; }
        public boolean isHorizontalStyle() { return style == LAYOUTSTYLE_HORIZONTAL; }
        public boolean isVerticalStyle() { return style == LAYOUTSTYLE_VERTICAL; }
        public Item getItem(int n) { return (Item) get(n); }

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
                    String caption = itemCaption != null ? (itemUrl != null ? ("<a href='"+ itemUrl.getValue(rc) +"'>"+ itemCaption.getValue(rc) +"</a>") : itemCaption.getValue(rc)) : null;

                    writer.write("<font "+bannerItemFontAttrs+">");
                    if(i > 0)
                        writer.write(separator);
                    if(itemIcon != null)
                        writer.write("<img src='"+ itemIcon.getValue(rc) +"'>");
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
                    String caption = itemCaption != null ? (itemUrl != null ? ("<a href='"+ itemUrl.getValue(rc) +"'>"+ itemCaption.getValue(rc) +"</a>") : itemCaption.getValue(rc)) : null;

                    writer.write("<tr><td>");
                    writer.write(itemIcon != null ? "<img src='"+ itemIcon.getValue(rc) +"'>" : "-");
                    writer.write("</td>");
                    writer.write("<td><font "+bannerItemFontAttrs+">");
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

    public Items getItems() { return items; }
    public void addItem(Item item) { items.add(item); }

    public void importFromXml(Element elem)
    {
        items.importFromXml(elem);
    }

    public void produceHtml(Writer writer, ReportContext rc) throws IOException
    {
        items.produceHtml(writer, rc);
    }
}