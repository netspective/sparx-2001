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
 * $Id: TestDatabaseContext.java,v 1.2 2002-09-02 22:58:59 shahid.shah Exp $
 */

package com.netspective.sparx.xif.db.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;

/**
 * A trivial test implementation of a DatabaseContext that uses a map to store the data source information and
 * does not do any specific connection pooling on its own (the underlying driver may do the pooling).
 */

public class TestDatabaseContext extends AbstractDatabaseContext
{
    public static class DataSourceInfo
    {
        private String driverName;
        private String connUrl;
        private String connUser;
        private String connPassword;

        public DataSourceInfo(String driverName, String connUrl, String connUser, String connPassword)
        {
            this.driverName = driverName;
            this.connUrl = connUrl;
            this.connUser = connUser;
            this.connPassword = connPassword;
        }

        public String getDriverName()
        {
            return driverName;
        }

        public void setDriverName(String driverName)
        {
            this.driverName = driverName;
        }

        public String getConnUrl()
        {
            return connUrl;
        }

        public void setConnUrl(String connUrl)
        {
            this.connUrl = connUrl;
        }

        public String getConnUser()
        {
            return connUser;
        }

        public void setConnUser(String connUser)
        {
            this.connUser = connUser;
        }

        public String getConnPassword()
        {
            return connPassword;
        }

        public void setConnPassword(String connPassword)
        {
            this.connPassword = connPassword;
        }
    }

    private Map dataSources = new HashMap();

    public DataSourceInfo getDataSourceInfo(String dataSourceId)
    {
        return (DataSourceInfo ) dataSources.get(dataSourceId);
    }

    public void setDataSourceInfo(String dataSourceId, DataSourceInfo dataSourceInfo)
    {
        dataSources.put(dataSourceId, dataSourceInfo);
    }

    public Connection getConnection(String dataSourceId) throws NamingException, SQLException
    {
        DataSourceInfo dsInfo = getDataSourceInfo(dataSourceId);
        if(dsInfo != null)
        {
            try
            {
                Class.forName(dsInfo.getDriverName());
            }
            catch(ClassNotFoundException cnfe)
            {
                throw new NamingException("Driver '"+ dsInfo.getDriverName() +"' not found for dataSourceId '"+ dataSourceId +"'");
            }
            return DriverManager.getConnection(dsInfo.getConnUrl(), dsInfo.getConnUser(), dsInfo.getConnPassword());
        }
        else
            throw new NamingException("Information for DataSource '"+ dataSourceId +"' not found.");
    }

    public final Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException
    {
        dataSourceId = translateDataSourceId(vc, dataSourceId);

        // check to see if there is already a connection bound with the request
        // meaning we're within a transaction. Reuse the connection if we are
        // within a transaction
        Connection conn = vc != null ? getSharedConnection(vc, dataSourceId) : null;
        if(conn == null)
            conn = getConnection(dataSourceId);
        return conn;
    }
}
