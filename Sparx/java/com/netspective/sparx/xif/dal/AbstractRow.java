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
 * $Id: AbstractRow.java,v 1.5 2002-11-22 07:39:03 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xif.dal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.sql.DmlStatement;
import com.netspective.sparx.xif.db.DatabasePolicy;
import com.netspective.sparx.xif.db.policy.BasicDatabasePolicy;

public abstract class AbstractRow implements Row
{
    protected Table rowTable;
    protected Column[] rowColumns;
		protected Map haveSqlExprDataMap = new HashMap();
		protected Map sqlExprDataMap = new HashMap();
    protected boolean[] haveSqlExprData;

		public static String DEFAULT_DBMS = "ansi";
    public static String NO_SQL_EXPRESSION = "No SQL Expression";

    public AbstractRow(com.netspective.sparx.xif.dal.Column[] columns)
    {
        setColumns(columns);
    }

    public AbstractRow(Table table)
    {
        setTable(table);
    }

    public Object getActivePrimaryKeyValue()
    {
        return null;
    }

    public boolean haveSqlExprData(String dbms, int column)
    {
				boolean returnValue = false;

        if (null == this.haveSqlExprData)
        {
					// The Master haveSqlExprData doesnt exist - Create it...
            this.haveSqlExprData = new boolean[getColumns().length];
        }

        boolean[] haveSqlExprData = (boolean[]) haveSqlExprDataMap.get(dbms);

        if (null == haveSqlExprData)
        {
            haveSqlExprData = new boolean[getColumns().length];
            haveSqlExprDataMap.put(dbms, haveSqlExprData);
        }

        return haveSqlExprData[column];
    }

	public boolean haveSqlExprData (int column) {
		return haveSqlExprData (DEFAULT_DBMS, column);
	}

	public void setHaveSqlExprData (String dbms, int column, boolean value) {
		// First set the value of specified in the dbms specified for the column specified
		boolean[] haveSqlExprData = (boolean[]) haveSqlExprDataMap.get(dbms);

		if (null == haveSqlExprData)
			haveSqlExprData = new boolean [getColumns().length];

		haveSqlExprData[column] = value;
		haveSqlExprDataMap.put(dbms, haveSqlExprData);

		// Now check to see whether the value of the current column is the same
		// across all dbms values or not.  If so, change the global haveSqlExprData
		// value to the value passed into this method
		Set dbmsSet = sqlExprDataMap.keySet();
		Iterator dbmsIter = dbmsSet.iterator();
		boolean changeGlobal = true;

		while (changeGlobal && dbmsIter.hasNext()) {
			haveSqlExprData = (boolean[]) haveSqlExprDataMap.get((String) dbmsIter.next());

			if (null == haveSqlExprData) {
				haveSqlExprData = new boolean [getColumns().length];
				haveSqlExprDataMap.put(dbms, haveSqlExprData);
			}

			if (haveSqlExprData[column] != value) changeGlobal = false;
		}

		if (changeGlobal) this.haveSqlExprData[column] = value;
	}

	public String sqlExprDataToString (int column) {
		StringBuffer str = new StringBuffer();

		if (! haveSqlExprData[column]) return str.toString();

		Set dbmsSet = sqlExprDataMap.keySet();
		Iterator dbmsIter = dbmsSet.iterator();

		while (dbmsIter.hasNext()) {
			String dbms = (String) dbmsIter.next();

			haveSqlExprData = (boolean[]) haveSqlExprDataMap.get(dbms);

			if (null != haveSqlExprData && haveSqlExprData[column]) {
				String sqlExprData = getCustomSqlExpr (column, dbms);
				str.append("[DBMS: " + dbms + ", SQL Expr: " + sqlExprData + "]");
			}
		}

		return str.toString();
	}

	public void setSqlExprData (String dbms, int column, String sqlExpr) {
		String[] sqlExprData = (String[]) sqlExprDataMap.get(dbms);

        if(null == sqlExprData)
            sqlExprData = new String[getColumns().length];

        sqlExprData[column] = sqlExpr;

        sqlExprDataMap.put(dbms, sqlExprData);
	}

    public String getSqlExprData(String dbms, int column)
    {
        String[] sqlExprData = (String[]) sqlExprDataMap.get(dbms);
        String[] sqlExprDataDefault = (String[]) sqlExprDataMap.get(DEFAULT_DBMS);

        if (null == sqlExprData)
        {
            sqlExprData = new String[getColumns().length];
            sqlExprDataMap.put(dbms, sqlExprData);
        }

        String returnValue;

        if (haveSqlExprData(dbms, column))
            returnValue = sqlExprData[column];
        else
            returnValue = sqlExprDataDefault[column];

        return returnValue;
    }

    /**
     * Sets the SQL expression that should be passed into the database in place of a bind parameter. This
     * method is used when a column's value should be a database-dependent function or value as opposed to
     * a Java value that is passed in as a bind parameter.
     * @param column The zero-based column index of the column for which the SQL expression is being created
     * @param dbms A string representing the DBMS for which this SQL expression is relevant
     * @param sqlExpr the actual SQL expression
     */
    public void setCustomSqlExpr(int column, String dbms, String sqlExpr)
    {
		setSqlExprData (dbms, column, sqlExpr);
        setHaveSqlExprData (dbms, column, true);
    }

    public void setCustomSqlExpr(int column, String sqlExpr)
    {
    	setCustomSqlExpr(column, DEFAULT_DBMS, sqlExpr);
    }

	public String getCustomSqlExpr(int column, String dbms) {
		return getSqlExprData (dbms, column);
	}

	public String getCustomSqlExpr(int column) {
		return getCustomSqlExpr(column, DEFAULT_DBMS);
	}

    public Table getTable()
    {
        return rowTable;
    }

    public void setTable(Table value)
    {
        rowTable = value;
        haveSqlExprData = new boolean[rowTable.getColumnsCount()];
    }

    public com.netspective.sparx.xif.dal.Column[] getColumns()
    {
        return rowTable != null ? rowTable.getAllColumns() : rowColumns;
    }

    public void setColumns(com.netspective.sparx.xif.dal.Column[] value)
    {
        rowColumns = value;
        haveSqlExprData = new boolean[rowColumns.length];
    }

    abstract public Object[] getData();

    // abstract public List getDataForDmlStatement();

	abstract public List getDataForDmlStatement(DatabasePolicy dbPolicy);

    /**
     * Given a ResultSet, return a Map of all the column names in the ResultSet
     * in lowercase as the key and the index of the column as the value.
     */
    public static Map getColumnNamesIndexMap(ResultSet rs) throws SQLException
    {
        Map map = new HashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colsCount = rsmd.getColumnCount();
        for(int i = 1; i <= colsCount; i++)
        {
            map.put(rsmd.getColumnName(i).toLowerCase(), new Integer(i));
        }
        return map;
    }

    abstract public void populateDataByIndexes(ResultSet resultSet) throws SQLException;

    abstract public void populateDataByNames(ResultSet resultSet, Map colNameIndexMap) throws SQLException;

    abstract public void populateDataByNames(Element element) throws ParseException, DOMException;

    abstract public boolean isValidXmlNodeNameForColumn(String nodeName);

    abstract public boolean populateDataForXmlNodeName(String nodeName, String value, boolean append) throws ParseException;

    abstract public boolean populateSqlExprForXmlNodeName(String nodeName, String expr) throws ParseException;

    abstract public boolean isValidXmlNodeNameForChildRow(String nodeName);

    abstract public Row createChildRowForXmlNodeName(String nodeName);

    abstract public void populateDataByNames(DialogContext dc);

    abstract public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap);

    abstract public void setData(DialogContext dc);

    abstract public void setData(DialogContext dc, Map colNameFieldNameMap);

    public boolean valuesAreEqual(Object primary, Object compareTo)
    {
        if(primary == null && compareTo == null)
            return true;

        if((primary == null && compareTo != null) || (primary != null && compareTo == null))
            return false;

        return primary.equals(compareTo);
    }

    public DmlStatement createInsertDml(Table table, DatabasePolicy dbPolicy)
    {
        return new DmlStatement(table.getName(), dbPolicy, table.getColumnNames(), getDataForDmlStatement(dbPolicy));
    }

    public DmlStatement createUpdateDml(Table table, DatabasePolicy dbPolicy, String whereCond)
    {
        return new DmlStatement(table.getName(), dbPolicy, table.getColumnNames(), getDataForDmlStatement(dbPolicy), whereCond);
    }

    public DmlStatement createDeleteDml(Table table, DatabasePolicy dbPolicy, String whereCond)
    {
        return new DmlStatement(table.getName(), dbPolicy, whereCond);
    }

    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        return true;
    }

    public void afterInsert(ConnectionContext cc) throws NamingException, SQLException
    {
    }

    public void afterUpdate(ConnectionContext cc) throws NamingException, SQLException
    {
    }

    public void afterDelete(ConnectionContext cc) throws NamingException, SQLException
    {
    }

    public boolean isParentRow()
    {
        return false;
    }

    public void retrieveChildren(ConnectionContext cc) throws NamingException, SQLException
    {
    }
	/**
	 * @see com.netspective.sparx.xif.dal.Row#createDeleteDml(com.netspective.sparx.xif.dal.Table, java.lang.String)
	 */
	public DmlStatement createDeleteDml(Table table, String whereCond) {
		BasicDatabasePolicy dbPolicy = new BasicDatabasePolicy();

		return createDeleteDml(table, dbPolicy, whereCond);
	}

	/**
	 * @see com.netspective.sparx.xif.dal.Row#createInsertDml(com.netspective.sparx.xif.dal.Table)
	 */
	public DmlStatement createInsertDml(Table table) {
		BasicDatabasePolicy dbPolicy = new BasicDatabasePolicy();

		return createInsertDml(table, dbPolicy);
	}

	/**
	 * @see com.netspective.sparx.xif.dal.Row#createUpdateDml(com.netspective.sparx.xif.dal.Table, java.lang.String)
	 */
	public DmlStatement createUpdateDml(Table table, String whereCond) {
		BasicDatabasePolicy dbPolicy = new BasicDatabasePolicy();

		return createUpdateDml(table, dbPolicy, whereCond);
	}

}
