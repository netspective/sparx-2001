package com.xaf.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.value.*;

public class RedirectPage extends AbstractServletPage
{
	private SingleValueSource redirect;
	private String name;
	private String caption;

    public RedirectPage(String name, String caption, String redirectStr)
    {
		super();
	    this.name = name;
		this.caption = caption;
		if(redirectStr != null)
			redirect = ValueSourceFactory.getSingleOrStaticValueSource(redirectStr);
    }

	public String getName()
	{
		return name != null ? name : super.getName();
	}

	public String getCaption(PageContext pc)
	{
		return caption != null ? caption : super.getCaption(pc);
	}

	public void handlePage(PageContext pc) throws ServletException
	{
		try
		{
			if(redirect == null)
			{
				HttpServletRequest request = (HttpServletRequest) pc.getRequest();
				((HttpServletResponse) pc.getResponse()).sendRedirect(request.getContextPath());
			}
			else
			{
				String redirectStr = redirect.getValue(pc);
				((HttpServletResponse) pc.getResponse()).sendRedirect(redirectStr);
			}
		}
		catch(IOException e)
		{
			throw new ServletException(e);
		}
	}
}