package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.db.DatabaseContext;
import com.xaf.db.DatabaseContextFactory;
import com.xaf.sql.StatementManager;
import com.xaf.sql.StatementManagerFactory;
import com.xaf.form.DialogManager;
import com.xaf.form.DialogManagerFactory;
import com.xaf.task.sql.DmlTask;
import com.xaf.task.TaskExecuteException;
import com.xaf.task.TaskContext;

import java.lang.reflect.*;
import java.util.*;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.http.*;

public class ServletValueContext implements ValueContext
{
	protected ServletContext servletContext;
	protected Servlet servlet;
	protected ServletRequest request;
	protected ServletResponse response;

    public ServletValueContext()
    {
    }

	public ServletValueContext(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
	{
        initialize(context, servlet, request, response);
	}

    public void initialize(ServletContext context, Servlet servlet, ServletRequest request, ServletResponse response)
    {
        this.servletContext = context;
		this.request = request;
		this.response = response;
		this.servlet = servlet;
    }

	public final ServletRequest getRequest() { return request; }
	public final ServletResponse getResponse() { return response; }
	public final ServletContext getServletContext() { return servletContext; }
	public final Servlet getServlet() { return servlet; }
    public final HttpSession getSession() { return ((HttpServletRequest) request).getSession(true); }

    public DatabaseContext getDatabaseContext()
    {
        return DatabaseContextFactory.getContext(request, servletContext);
    }

    public Connection getConnection()
    {
        return getConnection(null);
    }

    public Connection getConnection(String dataSourceId)
    {
        DatabaseContext dbc = DatabaseContextFactory.getContext(request, servletContext);
        try
        {
            return dbc.getConnection(this, dataSourceId);
        }
        catch(javax.naming.NamingException e)
        {
            return null;
        }
        catch(java.sql.SQLException e)
        {
            return null;
        }
    }

    public StatementManager getStatementManager()
    {
        return StatementManagerFactory.getManager(servletContext);
    }

    public DialogManager getDialogManager()
    {
        return DialogManagerFactory.getManager(request, servletContext);
    }
}