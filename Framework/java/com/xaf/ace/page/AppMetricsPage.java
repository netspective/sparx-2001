package com.xaf.ace.page;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.*;
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
	public static final String METRICS_CFG_PREFIX = "framework.ace.metrics";
	public static final String FILESYS_CFG_PREFIX = METRICS_CFG_PREFIX + ".filesys";

	private Set countLinesInFileExtn = new HashSet();
	private boolean ignoreCaseInFileExtn;

	public final String getName() { return "metrics"; }
	public final String getPageIcon() { return "metrics.gif"; }
	public final String getCaption(PageContext pc) { return "Metrics"; }
	public final String getHeading(PageContext pc) { return "Application Metrics"; }

	public void calcFileSystemMetrics(File path, int depth, Metric dirMetrics, Metric allFileMetrics, FileTypeMetric codeFileMetrics, FileTypeMetric appFileMetrics)
	{
		Metric totalDirsMetric = dirMetrics.createChildMetricSimple("Total folders");
		Metric avgEntriesMetric = dirMetrics.createChildMetricAverage("Average entries per folder");
		Metric avgDepthMetric = dirMetrics.createChildMetricAverage("Average Depth");
		avgDepthMetric.incrementAverage(depth);

		File[] entries = path.listFiles();
		for(int i = 0; i < entries.length; i++)
		{
			File entry = entries[i];
			if(entry.isDirectory())
			{
				totalDirsMetric.incrementCount();
				File[] childEntries = entry.listFiles();
				avgEntriesMetric.incrementAverage(childEntries.length);
				calcFileSystemMetrics(entry, depth+1, dirMetrics, allFileMetrics, codeFileMetrics, appFileMetrics);
			}
			else
			{
				String entryCaption = entry.getName();
				String entryExtension = "(no extension)";
				int extnIndex = entryCaption.lastIndexOf('.');
				if(extnIndex > -1)
					entryExtension = entryCaption.substring(extnIndex);
				if(ignoreCaseInFileExtn)
					entryExtension = entryExtension.toLowerCase();

				Metric fileMetric = allFileMetrics.createChildMetricSimple(entryExtension);
				fileMetric.setFlag(Metric.METRICFLAG_SHOW_PCT_OF_PARENT);
				fileMetric.incrementCount();

				if(countLinesInFileExtn.contains(entryExtension))
				{
					FileTypeMetric ftMetric = (FileTypeMetric) codeFileMetrics.getChild(entryExtension);
	    			if(ftMetric == null)
		    			ftMetric = codeFileMetrics.createChildMetricFileType(entryExtension, true);
			    	ftMetric.incrementCount(entry);
				}
				else
				{
					FileTypeMetric ftMetric = (FileTypeMetric) appFileMetrics.getChild(entryExtension);
	    			if(ftMetric == null)
		    			ftMetric = appFileMetrics.createChildMetricFileType(entryExtension, false);
			    	ftMetric.incrementCount(entry);
				}
			}
		}
	}

	public void createFileSystemMetrics(Metric parentMetric, String pathStr)
	{
		Metric fsMetrics = parentMetric.createChildMetricGroup("Application Files");
		Metric dirMetrics = fsMetrics.createChildMetricGroup("Folders");
		Metric allFileMetrics = fsMetrics.createChildMetricSimple("Files");
		allFileMetrics.setFlag(Metric.METRICFLAG_SUM_CHILDREN);
		allFileMetrics.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		FileTypeMetric codeFileMetrics = parentMetric.createChildMetricFileType("Code Files", false);
		codeFileMetrics.setFlag(Metric.METRICFLAG_SUM_CHILDREN);
		codeFileMetrics.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		FileTypeMetric appFileMetrics = parentMetric.createChildMetricFileType("App Files", false);
		appFileMetrics.setFlag(Metric.METRICFLAG_SUM_CHILDREN);
		appFileMetrics.setFlag(Metric.METRICFLAG_SORT_CHILDREN);

		File path = new File(pathStr);
		calcFileSystemMetrics(path, 1, dirMetrics, allFileMetrics, codeFileMetrics, appFileMetrics);
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		ConfigurationManager config = ConfigurationManagerFactory.getManager(context);

		ignoreCaseInFileExtn = config.getBooleanValue(pc, FILESYS_CFG_PREFIX + ".ignore-case", true);
		String[] codeExtns = config.getDelimitedValues(pc, FILESYS_CFG_PREFIX + ".code-extensions", null, ",");
		if(codeExtns != null)
		{
			countLinesInFileExtn.clear();
			for(int i = 0; i < codeExtns.length; i++)
				countLinesInFileExtn.add("." + codeExtns[i]);
		}

		Metric metrics = new Metric(null, "Application Metrics", Metric.METRIC_TYPE_GROUP);

		createFileSystemMetrics(metrics, config.getValue(pc, "app.project-root"));

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