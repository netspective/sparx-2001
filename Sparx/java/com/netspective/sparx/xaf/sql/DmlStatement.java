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
 * $Id: DmlStatement.java,v 1.4 2002-11-22 07:39:03 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.netspective.sparx.xif.db.DatabasePolicy;

public class DmlStatement
{
    static public class CustomSql
    {
		public static String DEFAULT_DBMS = "ansi";

        Map customSql = new HashMap();
	    Map haveCustomSqlMap = new HashMap();

        public CustomSql(String sql)
        {
            customSql.put(DEFAULT_DBMS, sql);
	        haveCustomSqlMap.put(DEFAULT_DBMS, new Boolean(true));
        }
        
        public CustomSql(String dbms, String sql)
        {
            customSql.put(dbms, sql);
	        haveCustomSqlMap.put(dbms, new Boolean(true));

			Boolean haveCustomSql = (Boolean) haveCustomSqlMap.get(DEFAULT_DBMS);
	        if (!haveCustomSql.booleanValue()) haveCustomSqlMap.put(DEFAULT_DBMS, new Boolean(true));
        }
        
		public String getCustomSql()
		{
			return getCustomSql(DEFAULT_DBMS);
		}
		
        public String getCustomSql(String dbms)
        {
			String returnValue;
	        Boolean haveCustomSql = (Boolean) haveCustomSqlMap.get(dbms);
	        Boolean haveCustomSqlDefault = (Boolean) haveCustomSqlMap.get(DEFAULT_DBMS);

			if (null == haveCustomSql || haveCustomSql.booleanValue())
				returnValue = (String) customSql.get(DEFAULT_DBMS);
	        else
				returnValue = (String) customSql.get(dbms);

	        return returnValue;
        }
    }

    static public final short STMTTYPE_INSERT = 0;
    static public final short STMTTYPE_UPDATE = 1;
    static public final short STMTTYPE_DELETE = 2;

	public static String DEFAULT_DBMS = "ansi";

    private short stmtType;
	private Map sql = new HashMap();
//    private String sql;
    private String tableName;
		// We want a new List object that we can remove columns
		// from without affecting the DAL objects
    private Vector columnNames = new Vector();
    private Vector columnValues = new Vector();
    private boolean[] bindValues;
    private String whereCond;
		private int autoIncIdx;

    public DmlStatement(String tableName, DatabasePolicy dbPolicy, List columnNames, List columnValues)
    {
		String dbms = dbPolicy.getDBMSName();

        stmtType = STMTTYPE_INSERT;
        this.tableName = tableName;
		this.columnNames.addAll(columnNames);
        this.columnValues.addAll(columnValues);
        this.bindValues = new boolean[columnValues.size()];
        createInsertSql(dbms);
    }

	public DmlStatement(String tableName, List columnNames, List columnValues)
    {
        stmtType = STMTTYPE_INSERT;
        this.tableName = tableName;
				this.columnNames.addAll(columnNames);
        this.columnValues.addAll(columnValues);
        this.bindValues = new boolean[columnValues.size()];
        createInsertSql(DEFAULT_DBMS);
    }



    public DmlStatement(String tableName, DatabasePolicy dbPolicy, List columnNames, List columnValues, String whereCond)
    {
		String dbms = dbPolicy.getDBMSName();
		
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.columnNames.addAll(columnNames);
		this.columnValues.addAll(columnValues);
        this.whereCond = whereCond;
        this.bindValues = new boolean[columnValues.size()];
        createUpdateSql(dbms);
    }

	public DmlStatement(String tableName, List columnNames, List columnValues, String whereCond)
    {
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.columnNames.addAll(columnNames);
				this.columnValues.addAll(columnValues);
        this.whereCond = whereCond;
        this.bindValues = new boolean[columnValues.size()];
        createUpdateSql(DEFAULT_DBMS);
    }



    public DmlStatement(String tableName, DatabasePolicy dbPolicy, String whereCond)
    {
		String dbms = dbPolicy.getDBMSName();
		
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.whereCond = whereCond;
        createDeleteSql(dbms);
    }

	public DmlStatement(String tableName, String whereCond)
    {
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.whereCond = whereCond;
        createDeleteSql(DEFAULT_DBMS);
    }



    public short getStmtType()
    {
        return stmtType;
    }

    public String getSql()
    {
        return (String) sql.get(DEFAULT_DBMS);
    }

    public String getSql(String dbms)
    {
        return (String) sql.get(dbms);
    }

    public String getTableName()
    {
        return tableName;
    }

    public List getColumnNames()
    {
        return columnNames;
    }

    public List getColumnValues()
    {
        return columnValues;
    }

    public boolean[] getBindValues()
    {
        return bindValues;
    }

    public String getWhereCond()
    {
        return whereCond;
    }

    public void updateValue(int index, String dbms, Object value)
    {
        columnValues.set(index, value);
        createSql(dbms);
    }

    public void createSql()
    {
		createSql(DEFAULT_DBMS);
    }

    public void createSql(String dbms)
    {
        switch(stmtType)
        {
            case STMTTYPE_INSERT:
                createInsertSql(dbms);
                break;

            case STMTTYPE_UPDATE:
                createUpdateSql(dbms);
                break;

            case STMTTYPE_DELETE:
                createDeleteSql(dbms);
                break;
        }
    }

    public void createInsertSql()
    {
    	createInsertSql(DEFAULT_DBMS);
    }

    public void createInsertSql(String dbms)
    {
        int columnsCount = columnNames.size();
        StringBuffer names = new StringBuffer();
        StringBuffer values = new StringBuffer();

        for(int i = 0; i < columnsCount; i++)
        {
            if(i != 0)
            {
                names.append(", ");
                values.append(", ");
            }
            names.append((String) columnNames.get(i));

            Object value = columnValues.get(i);
            if(value instanceof CustomSql)
            {
                values.append(((CustomSql) value).getCustomSql(dbms));
            }
            else
            {
                if(value == null)
                    values.append("NULL");
                else
                {
                    values.append("?");
                    bindValues[i] = true;
                }
            }
        }

		String sqlString = "insert into " + tableName + " (" + names + ") values (" + values + ")";
        sql.put(dbms, "insert into " + tableName + " (" + names + ") values (" + values + ")");
    }

    public void createUpdateSql()
    {
    	createUpdateSql(DEFAULT_DBMS);
    }
    
    public void createUpdateSql(String dbms)
    {
        int columnsCount = columnNames.size();
        StringBuffer sets = new StringBuffer();

        for(int i = 0; i < columnsCount; i++)
        {
            if(i != 0)
                sets.append(", ");

            Object value = columnValues.get(i);

            sets.append((String) columnNames.get(i));
            sets.append(" = ");

            if(value instanceof CustomSql)
            {
                sets.append(((CustomSql) value).getCustomSql(dbms));
            }
            else
            {
                if(value == null)
                {
                    sets.append("NULL");
                }
                else
                {
                    sets.append("?");
                    bindValues[i] = true;
                }
            }
        }

        String sql = "update " + tableName + " set " + sets;
        if(whereCond != null)
            sql += " where " + whereCond;
            
        this.sql.put(dbms, sql);

    }

    public void createDeleteSql()
    {
    	createDeleteSql(DEFAULT_DBMS);
    }
    
    public void createDeleteSql(String dbms)
    {
        String sql = "delete from " + tableName;
        if(whereCond != null)
            sql += " where " + whereCond;
            
        this.sql.put(dbms, sql);
    }

    public String toString()
    {
        StringBuffer bind = new StringBuffer();
        if(bindValues != null)
        {
            //for(int c = 0; c < bindValues.length; c++)
            for(int c = 0; c < columnValues.size(); c++)
            {
                if(bindValues[c])
                {
                    bind.append(c + ": " + columnValues.get(c) + " (" + columnValues.get(c).getClass() + ")\n");
                }
            }
        }
        return "SQL\n" + sql + "\n\nBIND\n" + bind;
    }

    public void removeColumn(String columnName)
    {
        int columnsCount = columnNames.size();

        for(int i = 0; i < columnsCount; i++)
        {
						String compColName = (String) columnNames.get(i);
						if (compColName.equals(columnName)) {
							columnNames.remove(i);
							columnValues.remove(i);
							autoIncIdx = i;
							shuffleBindValues();
							break;
						}
				}
		}

		private void shuffleBindValues()
		{
			int columnsCount = columnNames.size();

			for(int i = autoIncIdx; i < columnsCount; i++) {
				if (i < columnsCount - 1) {
					bindValues[i] = bindValues[i + 1];
				} else {
					bindValues[i] = false;
				}
			}
		}
}
