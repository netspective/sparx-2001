package com.xaf;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.text.*;

import org.w3c.dom.*;

public class Metric
{
	public static final int METRIC_TYPE_GROUP      = 0;
	public static final int METRIC_TYPE_SIMPLE_SUM = 1;
	public static final int METRIC_TYPE_AVERAGE    = 2;

	static public final int METRICFLAG_SORT_CHILDREN      = 1;
	static public final int METRICFLAG_SHOW_PCT_OF_PARENT = METRICFLAG_SORT_CHILDREN * 2;
	static public final int METRICFLAG_SUM_CHILDREN       = METRICFLAG_SHOW_PCT_OF_PARENT * 2;

	private Metric root;
	private Metric parent;
	private String name;
	private int type;
	private long count;
	private long sum;
	private long min = Long.MAX_VALUE;
	private long max = Long.MIN_VALUE;
	private List children;
	private Map childMap;
	private long flags;

    public Metric(Metric root, String name, int type)
    {
		this.root = root;
		this.name = name;
		this.type = type;
    }

	public String getName() { return name; }
	public Metric getRoot() { return root; }
	public Metric getParent() { return parent; }
	public void setParent(Metric metric) { parent = metric; }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { setFlag(flag, false); }
	public final void clearFlag(long flag) { clearFlag(flag, false); }

	public final void setFlag(long flag, boolean includeChildren)
	{
		flags |= flag;
		if(includeChildren && children != null)
		{
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				((Metric) i.next()).setFlag(flag);
			}
		}
	}

	public final void clearFlag(long flag, boolean includeChildren)
	{
		flags &= ~flag;
		if(includeChildren && children != null)
		{
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				((Metric) i.next()).clearFlag(flag);
			}
		}
	}

	public long getSum()
	{
		if(children != null && flagIsSet(METRICFLAG_SUM_CHILDREN))
		{
			count = 0;
			sum = 0;

			for(Iterator i = children.iterator(); i.hasNext(); )
			{
				count++;
				sum += ((Metric) i.next()).getSum();
			}
		}

		return sum;
	}

	public void setSum(long sum)
	{
		this.sum = sum;
	}

	public List getChildren()
	{
		return children;
	}

	protected void addChild(Metric metric)
	{
		metric.setParent(this);
		if(children == null)
		{
			children = new ArrayList();
			childMap = new HashMap();
		}
		children.add(metric);
		childMap.put(metric.getName(), metric);
	}

	public Metric createChildMetricGroup(String name)
	{
		Metric metric = getChild(name);
		if(metric != null)
			return metric;

		metric = new Metric(this, name, METRIC_TYPE_GROUP);
		addChild(metric);
		return metric;
	}

	public Metric createChildMetricSimple(String name)
	{
		Metric metric = getChild(name);
		if(metric != null)
			return metric;

		metric = new Metric(this, name, METRIC_TYPE_SIMPLE_SUM);
		addChild(metric);
		return metric;
	}

	public Metric createChildMetricAverage(String name)
	{
		Metric metric = getChild(name);
		if(metric != null)
			return metric;

		metric = new Metric(this, name, METRIC_TYPE_AVERAGE );
		addChild(metric);
		return metric;
	}

	public Metric getChild(String name)
	{
		if(childMap == null)
			return null;

		return (Metric) childMap.get(name);
	}

	public void incrementCount()
	{
		count++;
		sum++;
	}

	public void incrementAverage(long value)
	{
		this.count++;
		this.sum += value;
		if(value < min)
			min = value;
		if(value > max)
			max = value;
	}

	public Element createElement(Node parentNode)
	{
		Element metricElem = parentNode.getOwnerDocument().createElement("metric");
		parentNode.appendChild(metricElem);

		metricElem.setAttribute("name", name);
		switch(type)
		{
			case METRIC_TYPE_GROUP:
				metricElem.setAttribute("group", "yes");
				break;

			case METRIC_TYPE_SIMPLE_SUM:
				NumberFormat sumFmt = NumberFormat.getNumberInstance();
				metricElem.setAttribute("value", sumFmt.format(getSum()));
				break;

			case METRIC_TYPE_AVERAGE:
		   		metricElem.setAttribute("value", Double.toString(getSum() / count));
				metricElem.setAttribute("value-detail", "(min = " + min + ", max = " + max + ")");
				break;

			default:
		   		metricElem.setAttribute("value", "unknown metric type '"+ type +"'");
		}

		if(flagIsSet(this.METRICFLAG_SORT_CHILDREN))
			metricElem.setAttribute("sort-children", "yes");

		if(parent != null && flagIsSet(this.METRICFLAG_SHOW_PCT_OF_PARENT))
		{
			NumberFormat pctFmt = NumberFormat.getPercentInstance();
			metricElem.setAttribute("value-detail", pctFmt.format((double) sum / (double) parent.getSum()));
		}

		if(children != null)
		{
			for(Iterator i = children.iterator(); i.hasNext(); )
			{
				Metric childMetric = (Metric) i.next();
				childMetric.createElement(metricElem);
			}
		}

		return metricElem;
	}
}