package com.xaf.ace.page;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.Metric;
import com.xaf.ace.*;
import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.page.*;
import com.xaf.sql.*;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class AppMetricsPage extends AceServletPage
{
	private Set countLinesInFileExtn;

	public final String getName() { return "metrics"; }
	public final String getPageIcon() { return "metrics.gif"; }
	public final String getCaption(PageContext pc) { return "Metrics"; }
	public final String getHeading(PageContext pc) { return "Application Metrics"; }

	public long getLineCount(File entry) throws IOException
	{
		int result = 0;

		BufferedReader reader = new BufferedReader(new FileReader(entry));
		while(reader.readLine() != null)
			result++;
		reader.close();

		return result;
	}

	public void calcFileSystemMetrics(File path, int depth, Metric dirMetrics, Metric fileMetrics)
	{
		Metric totalDirsMetric = dirMetrics.createChildMetricSimple("Total folders");
		Metric avgEntriesMetric = dirMetrics.createChildMetricAverage("Average entries per folder");
		Metric avgDepthMetric = dirMetrics.createChildMetricAverage("Average Depth");
		avgDepthMetric.incrementAverage(depth);

		Metric fileTypesMetric = fileMetrics.createChildMetricSimple("Total files");
		fileTypesMetric.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		Metric fileLinesMetric = fileMetrics.createChildMetricSimple("Total lines of code (LOC)");
		fileLinesMetric.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		Metric avgLinesMetric = fileMetrics.createChildMetricAverage("Average LOC per file");
		avgLinesMetric.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		File[] entries = path.listFiles();
		for(int i = 0; i < entries.length; i++)
		{
			File entry = entries[i];
			if(entry.isDirectory())
			{
				totalDirsMetric.incrementCount();
				File[] childEntries = entry.listFiles();
				avgEntriesMetric.incrementAverage(childEntries.length);
				calcFileSystemMetrics(entry, depth+1, dirMetrics, fileMetrics);
			}
			else
			{
				String entryCaption = entry.getName();
				String entryExtension = "(none)";
				int extnIndex = entryCaption.lastIndexOf('.');
				if(extnIndex > -1)
					entryExtension = entryCaption.substring(extnIndex);

				if(countLinesInFileExtn.contains(entryExtension))
				{
					Metric fileLineMetric = fileLinesMetric.getChild(entryExtension);
					if(fileLineMetric == null)
					{
						fileLineMetric = fileLinesMetric.createChildMetricSimple(entryExtension);
						fileLineMetric.setFlag(Metric.METRICFLAG_SHOW_PCT_OF_PARENT);
					}

					Metric avgLineMetric = avgLinesMetric.getChild(entryExtension);
					if(avgLineMetric == null)
						avgLineMetric = avgLinesMetric.createChildMetricAverage(entryExtension);

					long lines = 0;
					try
					{
						lines = getLineCount(entry);
					}
					catch(IOException e)
					{
					}

					fileLinesMetric.incrementAverage(lines);
					fileLineMetric.incrementAverage(lines);

					avgLinesMetric.incrementAverage(lines);
					avgLineMetric.incrementAverage(lines);
				}

				Metric fileTypeMetric = fileTypesMetric.getChild(entryExtension);
				if(fileTypeMetric == null)
				{
					fileTypeMetric = fileTypesMetric.createChildMetricSimple(entryExtension);
					fileTypeMetric.setFlag(Metric.METRICFLAG_SHOW_PCT_OF_PARENT);
				}

				fileTypesMetric.incrementCount();
				fileTypeMetric.incrementCount();
			}
		}
	}

	public void createFileSystemMetrics(Metric parentMetric, String pathStr)
	{
		Metric fsMetrics = parentMetric.createChildMetricGroup("Application Files");
		Metric dirMetrics = fsMetrics.createChildMetricGroup("Folders");
		Metric fileMetrics = fsMetrics.createChildMetricGroup("Files");

		File path = new File(pathStr);
		calcFileSystemMetrics(path, 1, dirMetrics, fileMetrics);
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		if(countLinesInFileExtn == null)
		{
			countLinesInFileExtn = new HashSet();
			countLinesInFileExtn.add(".java");
			countLinesInFileExtn.add(".jsp");
			countLinesInFileExtn.add(".sql");
			countLinesInFileExtn.add(".xml");
			countLinesInFileExtn.add(".xsl");
		}

		Metric metrics = new Metric(null, "Application Metrics", Metric.METRIC_TYPE_GROUP);

		ServletContext context = pc.getServletContext();

		ConfigurationManager cmanager = ConfigurationManagerFactory.getManager(context);
		createFileSystemMetrics(metrics, cmanager.getValue(pc, "app.project-root"));

		DialogManager dmanager = DialogManagerFactory.getManager(context);
		dmanager.getMetrics(metrics);

		StatementManager smanager = StatementManagerFactory.getManager(context);
		smanager.getMetrics(metrics);

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document xmlDoc = parser.newDocument();

			Element rootElem = xmlDoc.createElement("xaf");
			xmlDoc.appendChild(rootElem);

			metrics.createElement(rootElem);
			transform(pc, xmlDoc, ACE_CONFIG_ITEMS_PREFIX + "metrics-browser-xsl");
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
}