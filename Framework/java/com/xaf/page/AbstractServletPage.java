package com.xaf.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public class AbstractServletPage implements ServletPage
{
	private static int pageNumber = 0;

    public AbstractServletPage()
    {
		pageNumber++;
    }

	public String getName()
	{
		return "servlet_page_" + pageNumber;
	}

	public String getPageIcon()
	{
		return null;
	}

	public String getCaption(PageContext pc)
	{
		return getName();
	}

	public String getHeading(PageContext pc)
	{
		String result = getCaption(pc);
		if(result == null)
			return getName();
		else
			return result;
	}

	public String getTitle(PageContext pc)
	{
		String result = getHeading(pc);
		if(result == null)
			return getCaption(pc);
		else
			return result;
	}

	public void registerPage(PageControllerServlet servlet, VirtualPath rootPath)
	{
	}

	public boolean requireLogin(PageContext pc)
	{
		return true;
	}

	public boolean canHandlePage(PageContext pc)
	{
		return true;
	}

	public void handlePageMetaData(PageContext pc) throws ServletException, IOException
	{
	}

	public void handlePageHeader(PageContext pc) throws ServletException, IOException
	{
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
	}

	public void handlePageFooter(PageContext pc) throws ServletException, IOException
	{
	}

	public void handlePage(PageContext pc) throws ServletException
	{
		try
		{
			handlePageMetaData(pc);
			handlePageHeader(pc);
			handlePageBody(pc);
			handlePageFooter(pc);
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
		/*
		try
		{
			PrintWriter out = pc.getResponse().getWriter();
	    	out.print("This is "+ this.getClass() + ". ");

			VirtualPath.FindResults results = pc.getActivePath();
			String[] unmatchedItems = results.unmatchedPathItems();
			out.print("Unmatched items: " + (unmatchedItems == null ? 0 : unmatchedItems.length));
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
		*/
	}
}