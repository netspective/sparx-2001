package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.sql.Connection;
import javax.servlet.*;

import com.xaf.db.DatabaseContext;
import com.xaf.sql.StatementManager;
import com.xaf.form.DialogManager;

public interface ValueContext
{
	public Servlet getServlet();
	public ServletRequest getRequest();
	public ServletResponse getResponse();
	public ServletContext getServletContext();

    public DatabaseContext getDatabaseContext();
    public Connection getConnection();
    public Connection getConnection(String dataSourceId);

    public StatementManager getStatementManager();
    public DialogManager getDialogManager();
}