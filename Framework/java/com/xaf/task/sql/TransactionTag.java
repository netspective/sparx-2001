/*
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author      A. Thu
 * @version     1.0
 * Created on:  Jul 26, 2001 2:37:36 PM
 */
package com.xaf.task.sql;

import com.xaf.task.TaskContext;
import com.xaf.task.TaskExecuteException;
import com.xaf.db.DatabaseContext;
import com.xaf.db.DatabaseContextFactory;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.sql.Connection;

public class TransactionTag extends TagSupport
{
	private TransactionTask task = new TransactionTask();

    public int doStartTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		TaskContext tc = new TaskContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
        try
        {
            task.setCommand("begin");
            task.execute(tc);
			if(tc.hasError())
				out.write(tc.getErrorMessage());
			else if(tc.hasResultMessage())
				out.write(tc.getResultMessage());
            return EVAL_BODY_INCLUDE;
        }
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
        catch (TaskExecuteException e)
		{
			try
            {
                out.write(e.getDetailedMessage());
            }
            catch(IOException ie)
            {
                throw new JspException(ie.getMessage());
            }
			return SKIP_PAGE;
		}
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		TaskContext tc = new TaskContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
		try
		{
            task.setCommand("end");
			task.execute(tc);
			if(tc.hasError())
				out.write(tc.getErrorMessage());
			else if(tc.hasResultMessage())
				out.write(tc.getResultMessage());
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}
		catch(TaskExecuteException e)
		{
			try
            {
                out.write(e.getDetailedMessage());
            }
            catch(IOException ie)
            {
                throw new JspException(ie.getMessage());
            }
			return SKIP_PAGE;
		}

		return EVAL_PAGE;
	}

}
