package com.xaf.navigate.taglib;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class PageTag extends TagSupport
{
	private String title;
	private String heading;

	public void release()
	{
		super.release();
		title = null;
		heading = null;
	}

	public final String getTitle() { return title; }
	public final String getHeading() { return heading; }

	public void setTitle(String value) { title = value; }
	public void setHeading(String value) { heading = value;	}

	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}
}
