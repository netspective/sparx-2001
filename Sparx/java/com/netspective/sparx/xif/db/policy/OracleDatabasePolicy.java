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
 * $Id: OracleDatabasePolicy.java,v 1.2 2002-11-14 02:57:15 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xif.db.policy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.netspective.sparx.util.value.ValueContext;

public class OracleDatabasePolicy extends BasicDatabasePolicy
{
    public Object executeAndGetSingleValue(Connection conn, String sql) throws SQLException
    {
        Object value = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            try
            {
                rs = stmt.executeQuery(sql);
                if(rs.next())
                    value = rs.getObject(1);
            }
            finally
            {
                if(rs != null) rs.close();
            }
        }
        catch(SQLException e)
        {
            throw new SQLException(e.toString() + " [" + sql + "]");
        }
        finally
        {
            if(stmt != null) stmt.close();
        }
        return value;
    }

    public Object handleAutoIncPreDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        Object autoIncValue = executeAndGetSingleValue(conn, "select " + seqOrTableName + ".nextval from dual");
        if(autoIncValue == null)
            throw new SQLException("Unable to obtain next ORACLE sequence value from sequence '" + seqOrTableName + "'");
        return autoIncValue;
    }

    public Object handleAutoIncPreDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, List columnNames, List columnValues) throws SQLException
    {
        Object autoIncValue = executeAndGetSingleValue(conn, "select " + seqOrTableName + ".nextval from dual");
        if(autoIncValue != null)
        {
            columnNames.add(autoIncColumnName);
            columnValues.add(autoIncValue);
            return autoIncValue;
        }
        else
            throw new SQLException("Unable to obtain next ORACLE sequence value from sequence '" + seqOrTableName + "'");
    }

    public Object getAutoIncCurrentValue(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName) throws SQLException
    {
        return executeAndGetSingleValue(conn, "select " + seqOrTableName + ".currval from dual");
    }
	/**
	 * @see com.netspective.sparx.xif.db.DatabasePolicy#getDBMSName()
	 */
	public String getDBMSName() {
		return "oracle";
	}

}
