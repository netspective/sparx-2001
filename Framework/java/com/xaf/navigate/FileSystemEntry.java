package com.xaf.navigate;

import java.io.*;
import java.util.*;

public class FileSystemEntry extends File
{
	static public final String ROOT_URI = "/";
	private FileSystemEntry rootPath;
	private String entryCaption;
	private String entryExtension;
	private String entryURI = ROOT_URI;

	FileSystemEntry(FileSystemEntry aRootPath, String s)
	{
		super(s);
		rootPath = aRootPath;
		entryCaption = getName().replace('_', ' ');
		int extnIndex = entryCaption.lastIndexOf('.');
		if(extnIndex > -1)
		{
			entryExtension = entryCaption.substring(extnIndex+1);
			entryCaption = entryCaption.substring(0, extnIndex);
		}
		else
			entryExtension = "";

		// find the "relative" portion of the path (everything but the root path)
		if(rootPath != null)
		{
			String absPath = getAbsolutePath();
	    	entryURI = absPath.substring(rootPath.getAbsolutePath().length()).replace('\\', '/');
		}
	}

	public final String getEntryCaption() { return entryCaption; }
	public void setEntryCaption(String value) { entryCaption = value; }

	public final String getEntryType() { return entryExtension; }
	public void setEntryType(String value) { entryExtension = value; }

	public final String getEntryURI() { return entryURI; }
	public void setEntryURI(String value) { entryURI = value; }

	public final boolean isRoot() { return rootPath == null; }

	public ArrayList getParents()
	{
		if(rootPath == null)
			return null;

		String rootPathStr = rootPath.getAbsolutePath();
		ArrayList parents = new ArrayList();
		File parent = getParentFile();
		while(parent != null)
		{
			boolean isRootPath = parent.getAbsolutePath().equals(rootPathStr);
			if(parent.getAbsolutePath().equals(rootPathStr))
			{
				parents.add(0, rootPath);
				break;
			}
			else
			{
				parents.add(0, new FileSystemEntry(isRootPath ? null : rootPath, parent.getAbsolutePath()));
			    parent = parent.getParentFile();
			}
		}

		return parents;
	}

	public String findInPath(String fileName)
	{
		String testFile = getAbsolutePath() + separator + fileName;
		if(new File(testFile).exists())
			return testFile;

		if(rootPath == null)
			return null;

		String rootPathStr = rootPath.getAbsolutePath();
		File parent = getParentFile();
		while(parent != null)
		{
			testFile = parent.getAbsolutePath() + parent.separator + fileName;
			if(new File(testFile).exists())
				return testFile;

			boolean isRootPath = parent.getAbsolutePath().equals(rootPathStr);
			if(parent.getAbsolutePath().equals(rootPathStr))
				break;
			else
			    parent = parent.getParentFile();
		}

		return null;
	}
}
