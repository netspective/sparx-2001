package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.*;
import com.xaf.ace.*;
import com.xaf.page.*;

public class HomePage extends AceServletPage
{
	public final String getName() { return "home"; }
	public final String getCaption(PageContext pc) { return "ACE Home"; }
	public final String getHeading(PageContext pc) { return "Welcome to ACE"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		String sharedImagesRootURL = ((PageControllerServlet) pc.getServlet()).getSharedImagesRootURL();
		try
		{
			PrintWriter out = pc.getResponse().getWriter();
			out.print("<br>&nbsp;<p>&nbsp;<p><center><img src='");
			out.print(sharedImagesRootURL);
			out.print("/design/sparx-logo-lg.gif'><br>");
			out.print(BuildConfiguration.getVersionAndBuild());
			out.print("<center>");
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
	}
}