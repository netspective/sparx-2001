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
import com.xaf.value.*;
import org.w3c.dom.Element;

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

        public String getCaption() { return caption; }
        public String getUrl() { return url; }
        public String getIcon() { return icon; }
    }

    private SingleValueSource heading;
    private SingleValueSource footing;
    private ArrayList items;

    public ReportFrame()
    {
        heading = null;
        footing = null;
    }

	public SingleValueSource getHeading() { return heading; }
	public void setHeading(String value) { heading = value != null && value.length() > 0 ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null; }
	public void setHeading(SingleValueSource vs) { heading = vs; }

    public SingleValueSource getFooting() { return footing; }
    public void setFooting(String value) { footing = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null; }

    public ArrayList getItems() { return items; }
    public Item getItem(int n) { return (Item) items.get(n); }
    public void addItem(Item item) { if(items == null) items = new ArrayList(); items.add(item); }

    public void importFromXml(Element elem)
    {
        setHeading(elem.getAttribute("heading"));
        setFooting(elem.getAttribute("footing"));
    }
}