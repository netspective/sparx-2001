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
 * $Id: AbstractDatabaseContext.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.xif.db.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.w3c.dom.Element;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabasePolicy;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       Shahid N. Shah
 * @version 1.0
 */

public abstract class AbstractDatabaseContext implements DatabaseContext
{
    private static boolean forceNonScrollableRS;

    public AbstractDatabaseContext()
    {
    }

    public DatabasePolicy getDatabasePolicy(Connection conn) throws SQLException
    {
        return DatabaseContextFactory.getDatabasePolicy(conn);
    }

    public abstract Connection getConnection(String dataSourceId) throws NamingException, SQLException;

    public String translateDataSourceId(ValueContext vc, String dataSourceId)
    {
        return dataSourceId != null ? dataSourceId : vc.getServletContext().getInitParameter("default-data-source");
    }

    public abstract Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException;

    static public void setNonScrollableResultSet(boolean force)
    {
        forceNonScrollableRS = force;
    }

    public int getScrollableResultSetType(ValueContext vc, String dataSourceId) throws NamingException, SQLException
    {
        return getScrollableResultSetType(getConnection(vc, dataSourceId));
    }

    public int getScrollableResultSetType(Connection connection) throws NamingException, SQLException
    {
        if(forceNonScrollableRS)
            return RESULTSET_NOT_SCROLLABLE;

        DatabaseMetaData dbmd = connection.getMetaData();

        if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))
            return ResultSet.TYPE_SCROLL_INSENSITIVE;

        if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))
            return ResultSet.TYPE_SCROLL_SENSITIVE;

        return RESULTSET_NOT_SCROLLABLE;
    }

    public void createCatalog(ValueContext vc, Element parent) throws NamingException
    {
    }
}