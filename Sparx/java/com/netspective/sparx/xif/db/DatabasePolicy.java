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
 * $Id: DatabasePolicy.java,v 1.2 2002-01-28 10:09:39 jruss Exp $
 */

package com.netspective.sparx.xif.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.netspective.sparx.util.value.ValueContext;

public interface DatabasePolicy
{
    /**
     * When an auto increment value is needed in a SQL insert, this method is called before the execution of the insert
     * statement. This gives the DatabasePolicy to go get a sequence value or do whatever is
     * necessary in order to fill in the auto inc value into the columnNames, columnValues (which are the DML items
     * being filled out by the DmlTask object. In case sequences are not used, this method will just do nothing and
     * wait for the handleAutoIncPostDmlExecute to be called.
     *
     * @param conn The database connection to use
     * @param seqOrTableName The sequence name or the table name
     * @param autoIncColumnName The name of the column that is the auto inc column
     * @returns The auto inc value generated by the database or null if not known yet
     */
    public Object handleAutoIncPreDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName) throws SQLException;

    /**
     * When an auto increment value is needed in a SQL DML, this method is called after the execution of the DML
     * (insert, update, or delete). This gives the DatabasePolicy to go get a sequence value or do whatever is
     * necessary in order to return the auto inc value.
     *
     * @param conn The database connection to use
     * @param vc The ValueContext being used to fill the DmlTask columns and values
     * @param seqOrTableName The sequence name or the table name
     * @param autoIncColumnName The name of the column that is the auto inc column
     * @param autoIncColumnValue The value of the auto inc column returned by handleAutoIncPreDmlExecute method
     * @returns The auto inc value generated by the database or null if not known yet
     */

    public Object handleAutoIncPostDmlExecute(Connection conn, String seqOrTableName, String autoIncColumnName, Object autoIncColumnValue) throws SQLException;

    /**
     * When an auto increment value is needed in a SQL insert, this method is called before the execution of the insert
     * statement. This gives the DatabasePolicy to go get a sequence value or do whatever is
     * necessary in order to fill in the auto inc value into the columnNames, columnValues (which are the DML items
     * being filled out by the DmlTask object. In case sequences are not used, this method will just do nothing and
     * wait for the handleAutoIncPostDmlExecute to be called.
     *
     * @param conn The database connection to use
     * @param vc The ValueContext being used to fill the DmlTask columns and values
     * @param seqOrTableName The sequence name or the table name
     * @param autoIncColumnName The name of the column that is the auto inc column
     * @param columnNames The list of column names prepared for use in the DmlTask prior to calling this method
     * @param columnValues The list of column values prepared for use in the DmlTask prior to calling this method
     * @returns The auto inc value generated by the database or null if not known yet
     */

    public Object handleAutoIncPreDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, List columnNames, List columnValues) throws SQLException;

    /**
     * When an auto increment value is needed in a SQL DML, this method is called after the execution of the DML
     * (insert, update, or delete). This gives the DatabasePolicy to go get a sequence value or do whatever is
     * necessary in order to return the auto inc value.
     *
     * @param conn The database connection to use
     * @param vc The ValueContext being used to fill the DmlTask columns and values
     * @param seqOrTableName The sequence name or the table name
     * @param autoIncColumnName The name of the column that is the auto inc column
     * @param autoIncColumnValue The value of the auto inc column returned by handleAutoIncPreDmlExecute method
     * @returns The auto inc value generated by the database or null if not known yet
     */
    public Object handleAutoIncPostDmlExecute(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName, Object autoIncColumnValue) throws SQLException;

    /**
     * Gets the current value of an auto increment column.
     *
     * @param conn The database connection to use
     * @param vc The ValueContext being used to fill the DmlTask columns and values
     * @param seqOrTableName The sequence name or the table name
     * @param autoIncColumnName The name of the column that is the auto inc column
     * @returns The auto inc value generated by the database or null if not known yet
     */
    public Object getAutoIncCurrentValue(Connection conn, ValueContext vc, String seqOrTableName, String autoIncColumnName) throws SQLException;

    /**
     * Returns a boolean value whether to retain autoinc columns in any
     * insert or update DMLs.  Required because SQL Server does not allow
     * including the column in either case, generating an exception if it is
     */
    public boolean retainAutoIncColInDml();
}
