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
 * $Id: FileTypeMetric.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.metric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FileTypeMetric extends Metric
{
    public static final int METRIC_TYPE_FILE = METRIC_TYPE_LAST;

    private boolean isCode;
    private long count;
    private long totalLines;
    private long totalBytes;
    private double avgLines;
    private double avgBytes;

    public FileTypeMetric(Metric root, String name, boolean isCode)
    {
        super(root, name, METRIC_TYPE_FILE);
        this.isCode = isCode;
    }

    public long getCount()
    {
        return count;
    }

    public long getTotalLines()
    {
        return totalLines;
    }

    public long getTotalBytes()
    {
        return totalBytes;
    }

    public long getLineCount(File entry) throws IOException
    {
        int result = 0;

        BufferedReader reader = new BufferedReader(new FileReader(entry));
        while(reader.readLine() != null)
            result++;
        reader.close();

        return result;
    }

    public void incrementCount(File entry)
    {
        count++;

        totalBytes += entry.length();
        avgBytes = (double) totalBytes / (double) count;

        if(isCode)
        {
            try
            {
                long lineCount = getLineCount(entry);
                totalLines += lineCount;
                avgLines = (double) totalLines / (double) count;
            }
            catch(Exception IOException)
            {
            }
        }
    }

    public Element createElement(Node parentNode)
    {
        Element metricElem = parentNode.getOwnerDocument().createElement("metric");
        parentNode.appendChild(metricElem);

        metricElem.setAttribute("name", getName());
        metricElem.setAttribute("type", "file-type");

        if(flagIsSet(METRICFLAG_SORT_CHILDREN))
            metricElem.setAttribute("sort-children", "yes");

        List children = getChildren();
        if(children != null && flagIsSet(METRICFLAG_SUM_CHILDREN))
        {
            metricElem.setAttribute("type", "file-types");

            count = 0;
            totalLines = 0;
            totalBytes = 0;

            for(Iterator i = children.iterator(); i.hasNext();)
            {
                FileTypeMetric ftMetric = ((FileTypeMetric) i.next());
                count += ftMetric.getCount();
                totalLines += ftMetric.getTotalLines();
                totalBytes += ftMetric.getTotalBytes();
            }

            if(count > 0)
            {
                avgLines = (double) totalLines / (double) count;
                avgBytes = (double) totalBytes / (double) count;
            }
        }

        NumberFormat fmt = NumberFormat.getNumberInstance();

        metricElem.setAttribute("count", fmt.format(count));
        metricElem.setAttribute("total-bytes", fmt.format(totalBytes));
        metricElem.setAttribute("avg-bytes", fmt.format((long) avgBytes));

        if(flagIsSet(METRICFLAG_SUM_CHILDREN) && totalLines > 0)
        {
            metricElem.setAttribute("total-lines", fmt.format(totalLines));
            metricElem.setAttribute("avg-lines", fmt.format((long) avgLines));
        }

        if(isCode)
        {
            metricElem.setAttribute("is-code", "yes");
            metricElem.setAttribute("total-lines", fmt.format(totalLines));
            metricElem.setAttribute("avg-lines", fmt.format((long) avgLines));
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