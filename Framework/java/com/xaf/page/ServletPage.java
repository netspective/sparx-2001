package com.xaf.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public interface ServletPage
{
	public String getName();
	public String getPageIcon();
	public String getCaption(PageContext pc);
	public String getHeading(PageContext pc);
	public String getTitle(PageContext pc);

	public void registerPage(PageControllerServlet servlet, VirtualPath rootPath);
	public boolean requireLogin(PageContext pc);
	public boolean canHandlePage(PageContext pc);
	public void handlePage(PageContext pc) throws ServletException;
}