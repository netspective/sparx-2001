package com.xaf.ace.page;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.navigate.*;
import com.xaf.page.*;

public class DocumentsPage extends AceServletPage
{
	private String name;
	private String caption;
	private String ref;
	private boolean isFileRef;
	private FileSystemContext fsContext;

	public DocumentsPage()
	{
		super();
	}

	public DocumentsPage(String name, String dest)
	{
		int sepPos = name.indexOf(",");
		if(sepPos != -1)
		{
			this.name = name.substring(0, sepPos);
			this.caption = name.substring(sepPos+1);
		}
		else
		{
			this.name = name;
			this.caption = name;
		}
		ref = dest;
		File file = new File(dest);
		isFileRef = file.exists();
	}

	public final String getName()
	{
		return name == null ? "documents" : name;
	}

	public final String getPageIcon()
	{
		return "docs_project.gif";
	}

	public final String getCaption(PageContext pc)
	{
		return caption == null ? "Documents" : caption;
	}

	public final String getHeading(PageContext pc)
	{
		return getCaption(pc);
	}

	public final boolean isFileRef()
	{
		return isFileRef;
	}

	public void handlePage(PageContext pc) throws ServletException
	{
		try
		{
			if(! isFileRef)
			{
				((HttpServletResponse) pc.getResponse()).sendRedirect(ref);
				return;
			}

			VirtualPath.FindResults activePath = pc.getActivePath();
			String relativePath = activePath.getUnmatchedPath();

			ServletContext context = pc.getServletContext();
			if(fsContext == null)
			{
				fsContext = new FileSystemContext(
					activePath.getMatchedPath().getAbsolutePath(pc),
					ref, getCaption(pc), relativePath);
			}
			else
				fsContext.setRelativePath(relativePath);

			FileSystemEntry activeEntry = fsContext.getActivePath();
			if(activeEntry.isDirectory())
			{
				Document navgDoc = null;
				try
				{
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					navgDoc = builder.newDocument();
				}
				catch(Exception e)
				{
					throw new ServletException(e);
				}

				Element navgRootElem = navgDoc.createElement("xaf");
				navgDoc.appendChild(navgRootElem);

				fsContext.addXML(navgRootElem, fsContext);

				handlePageMetaData(pc);
				handlePageHeader(pc);
				transform(pc, navgDoc, ACE_CONFIG_ITEMS_PREFIX + "docs-browser-xsl");
				handlePageFooter(pc);
			}
			else
			{
				activeEntry.send((HttpServletResponse) pc.getResponse());
			}
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
	}
}
