/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: ServletValueContext.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.Connection;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xaf.form.DialogManager;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;

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

    public final ServletRequest getRequest()
    {
        return request;
    }

    public final ServletResponse getResponse()
    {
        return response;
    }

    public final ServletContext getServletContext()
    {
        return servletContext;
    }

    public final Servlet getServlet()
    {
        return servlet;
    }

    public final HttpSession getSession()
    {
        return ((HttpServletRequest) request).getSession(true);
    }

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