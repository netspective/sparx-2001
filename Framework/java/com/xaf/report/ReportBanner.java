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

import java.util.*;

public class ReportBanner
{
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

        public SingleValueSource getCaption() { return caption; }
        public SingleValueSource getUrl() { return url; }
        public SingleValueSource getIcon() { return icon; }
    }

    private ArrayList items = new ArrayList();

    public ReportBanner()
    {
    }

    public ArrayList getItems() { return items; }
    public Item getItem(int n) { return (Item) items.get(n); }
    public void addItem(Item item) { items.add(item); }
}