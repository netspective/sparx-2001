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
			out.print("<p>&nbsp;<img src='");
			out.print(sharedImagesRootURL);
			out.print("/ace/ace-text.gif'><p>");
			out.print("<table cellspacing=10><tr valign=top><td align=center>");
			out.print(BuildConfiguration.getProductBuild());
			out.print("<br>by Netspective Corp.");
			out.print("<p>Running on ");
			out.print(System.getProperty("os.name"));
			out.print("<br>Version ");
			out.print(System.getProperty("os.version"));
			out.print("</td><td align=center>Java Version ");
			out.print(System.getProperty("java.version"));
			out.print("<br>by ");
			out.print(System.getProperty("java.vendor"));
			out.print("<p>");
			out.print(System.getProperty("java.vm.name"));
			out.print("<br>Version ");
			out.print(System.getProperty("java.vm.version"));
			out.print("<br>by ");
			out.print(System.getProperty("java.vm.vendor"));
			out.print("</td></tr></table>");
			out.print("<center>");
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
	}
}