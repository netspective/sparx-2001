package com.xaf.navigate;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

public class FileSystemContext
{
	private String rootURI;
	private FileSystemEntry rootPath;
	private FileSystemEntry activePath;
	private String relativePathStr;

	public FileSystemContext(String aRootURI, String aRootPathStr, String aRootCaption, String aRelativePathStr)
	{
		rootURI = aRootURI;
		rootPath = new FileSystemEntry(null, aRootPathStr);
		rootPath.setEntryCaption(aRootCaption);

		relativePathStr = aRelativePathStr;
		if(relativePathStr != null && (relativePathStr.equals("") || relativePathStr.equals("/")))
			relativePathStr = null;

		if(relativePathStr != null)
			activePath = new FileSystemEntry(rootPath, rootPath.getAbsolutePath() + rootPath.separator + relativePathStr);
		else
			activePath = rootPath;
	}

	public final String getRootURI() { return rootURI; }
	public void setRootURI(String value) { rootURI = value; }

	public final FileSystemEntry getRootPath() { return rootPath; }
	public void setRootPath(FileSystemEntry value) { rootPath = value; }

	public final FileSystemEntry getActivePath() { return activePath; }
	public void setActivePath(FileSystemEntry value) { activePath = value; }

	public final String getRelativePath() { return relativePathStr; }
	public void setRelativePath(String value) { relativePathStr = value; }

	public void addXML(Element fsElem, FilenameFilter filter)
	{
		Document doc = fsElem.getOwnerDocument();

		Element pathElem = (Element) fsElem.appendChild(doc.createElement("path"));
		pathElem.setAttribute("caption", activePath.getEntryCaption());
		pathElem.setAttribute("url", activePath.getEntryURI());
		pathElem.setAttribute("path", activePath.getAbsolutePath());

		Element parents = (Element) pathElem.appendChild(doc.createElement("parents"));
		ArrayList parentList = activePath.getParents();
		if(parentList != null)
		{
			Iterator i = parentList.iterator();
			int level = 1;
			while(i.hasNext())
			{
				FileSystemEntry entry = (FileSystemEntry) i.next();
				Element parent = (Element) parents.appendChild(doc.createElement("parent"));
				parent.setAttribute("level", new Integer(level).toString());
				parent.setAttribute("caption", entry.getEntryCaption());
				parent.setAttribute("url", entry.getEntryURI());
				parent.setAttribute("path", entry.getAbsolutePath());
				parent.setAttribute("isroot", new Boolean(entry.isRoot()).toString());

				level++;
			}
		}

		Element folders = (Element) pathElem.appendChild(doc.createElement("folders"));
		Element files = (Element) pathElem.appendChild(doc.createElement("files"));

		File[] entries = activePath.listFiles(filter);
		if(entries != null)
		{
			for(int i = 0; i < entries.length; i++)
			{
				FileSystemEntry entry = new FileSystemEntry(rootPath, entries[i].getAbsolutePath());
				Element entryElem = null;
				if(entry.isDirectory())
					entryElem = (Element) folders.appendChild(doc.createElement("folder"));
				else
				{
					entryElem = (Element) files.appendChild(doc.createElement("file"));
					entryElem.setAttribute("type", entry.getEntryType());
				}
				entryElem.setAttribute("caption", entry.getEntryCaption());
				entryElem.setAttribute("url", entry.getEntryURI());
				entryElem.setAttribute("path", entry.getAbsolutePath());
			}
		}
	}
}