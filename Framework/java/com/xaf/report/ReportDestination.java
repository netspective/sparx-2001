package com.xaf.report;

import java.io.*;
import java.util.*;

import com.xaf.config.*;
import com.xaf.value.*;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       Shahid N. Shah
 * @version 1.0
 */

public class ReportDestination
{
	static public final String DESTNAME_BROWSER_MULTI_PAGE  = "browser-paged";
	static public final String DESTNAME_BROWSER_SINGLE_PAGE = "browser";
	static public final String DESTNAME_FILE_DOWNLOAD       = "file";
	static public final String DESTNAME_FILE_EMAIL          = "email";

	static public final int DEST_BROWSER_MULTI_PAGE  = 0;
	static public final int DEST_BROWSER_SINGLE_PAGE = 1;
	static public final int DEST_FILE_DOWNLOAD       = 2;
	static public final int DEST_FILE_EMAIL          = 3;

	private int destination;
	private String storePathName;
	private String downloadUrl;
	private File storePath;
	private File file;
	private Writer writer;

	public ReportDestination(int dest, ValueContext vc, ReportSkin skin) throws IOException
	{
		destination = dest;
		initialize(vc, skin);
	}

	public ReportDestination(String dest, ValueContext vc, ReportSkin skin) throws IOException
	{
		destination = getDestIdFromName(dest);
		if(destination == -1)
			throw new RuntimeException("ReportDestination '"+ dest +"' not supported. Use 'file' or 'email'");

		initialize(vc, skin);
	}

	public void initialize(ValueContext vc, ReportSkin skin) throws IOException
	{
		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
		storePathName = appConfig.getValue(vc, "app.report-file-store-path");
		downloadUrl = appConfig.getValue(vc, "app.report-file-download-url");
		if(storePathName == null || downloadUrl == null)
			throw new RuntimeException("Configuration value 'app.report-file-store-path' and 'app.report-file-download-url' are required.");

		storePath = new File(storePathName);
		if(! storePath.exists())
			storePath.mkdirs();

		file = File.createTempFile("report_", skin != null ? skin.getFileExtension() : ".html", storePath);
		file.deleteOnExit();

		writer = new FileWriter(file);
	}

	public String getStorePathName() { return storePathName; }
	public String getDownloadUrl() { return downloadUrl; }
	public File getStorePath() { return storePath; }
	public File getFile() { return file; }
	public Writer getWriter() { return writer; }

	static public int getDestIdFromName(String name)
	{
		int result = -1;
		if(name.equals(DESTNAME_BROWSER_MULTI_PAGE))
			result = DEST_BROWSER_MULTI_PAGE;
		else if(name.equals(DESTNAME_BROWSER_SINGLE_PAGE))
			result = DEST_BROWSER_SINGLE_PAGE;
		else if(name.equals(DESTNAME_FILE_DOWNLOAD))
			result = DEST_FILE_DOWNLOAD;
		else if(name.equals(DESTNAME_FILE_EMAIL))
			result = DEST_FILE_EMAIL;
		return result;
	}

	public String getUserMessage()
	{
		if(destination == DEST_FILE_DOWNLOAD)
			return "Your file is ready for download. Please click <a href='"+ downloadUrl + "/" + file.getName() +"'>here</a> to retrieve it.";
		else
			return "E-mail not supported yet; however, your file is ready for download. Please click <a href='"+ downloadUrl + "/" + file.getName() +"'>here</a> to retrieve it.";
	}
}