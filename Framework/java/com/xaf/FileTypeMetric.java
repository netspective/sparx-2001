package com.xaf;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.text.*;

import org.w3c.dom.*;

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

	public long getCount() { return count; }
	public long getTotalLines() { return totalLines; }
	public long getTotalBytes() { return totalBytes; }

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

			for(Iterator i = children.iterator(); i.hasNext(); )
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
			for(Iterator i = children.iterator(); i.hasNext(); )
			{
				Metric childMetric = (Metric) i.next();
				childMetric.createElement(metricElem);
			}
		}

		return metricElem;
	}
}