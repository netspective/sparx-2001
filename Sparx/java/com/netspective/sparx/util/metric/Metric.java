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
 * $Id: Metric.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.metric;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Metric
{
    public static final int METRIC_TYPE_GROUP = 0;
    public static final int METRIC_TYPE_SIMPLE_SUM = 1;
    public static final int METRIC_TYPE_AVERAGE = 2;
    public static final int METRIC_TYPE_LAST = 3; /* for extension classes */

    static public final int METRICFLAG_SORT_CHILDREN = 1;
    static public final int METRICFLAG_SHOW_PCT_OF_PARENT = METRICFLAG_SORT_CHILDREN * 2;
    static public final int METRICFLAG_SUM_CHILDREN = METRICFLAG_SHOW_PCT_OF_PARENT * 2;

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

    public String getName()
    {
        return name;
    }

    public Metric getRoot()
    {
        return root;
    }

    public Metric getParent()
    {
        return parent;
    }

    public void setParent(Metric metric)
    {
        parent = metric;
    }

    public final long getFlags()
    {
        return flags;
    }

    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    public final void setFlag(long flag)
    {
        setFlag(flag, false);
    }

    public final void clearFlag(long flag)
    {
        clearFlag(flag, false);
    }

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

            for(Iterator i = children.iterator(); i.hasNext();)
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

        metric = new Metric(this, name, METRIC_TYPE_AVERAGE);
        addChild(metric);
        return metric;
    }

    public FileTypeMetric createChildMetricFileType(String name, boolean isCode)
    {
        FileTypeMetric metric = (FileTypeMetric) getChild(name);
        if(metric != null)
            return metric;

        metric = new FileTypeMetric(this, name, isCode);
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
                if(count > 0)
                {
                    metricElem.setAttribute("value", Double.toString(getSum() / count));
                    metricElem.setAttribute("value-detail", "(min = " + min + ", max = " + max + ")");
                }
                else
                    metricElem.setAttribute("value", "0.0");
                break;

            default:
                metricElem.setAttribute("value", "unknown metric type '" + type + "'");
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
            for(Iterator i = children.iterator(); i.hasNext();)
            {
                Metric childMetric = (Metric) i.next();
                childMetric.createElement(metricElem);
            }
        }

        return metricElem;
    }
}