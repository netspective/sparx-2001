package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.ace.*;
import com.xaf.page.*;

public class DatabasePage extends AceServletPage
{
	public final String getName() { return "database"; }
	public final String getCaption(PageContext pc) { return "Database"; }
	public final String getHeading(PageContext pc) { return "ACE - Database"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		PrintWriter out = pc.getResponse().getWriter();
		out.print("I'm in DatabasePage!");
	}
}
