package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;

public class ReportBanner
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

        public String getCaption() { return caption; }
        public String getUrl() { return url; }
        public String getIcon() { return icon; }
    }

    private ArrayList items = new ArrayList();

    public ReportBanner()
    {
    }

    public ArrayList getItems() { return items; }
    public Item getItem(int n) { return (Item) items.get(n); }
    public void addItem(Item item) { items.add(item); }
}