package com.xaf.sql;

/**
 * Title:        SQL Generation Module
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import com.xaf.db.*;
import com.xaf.sql.query.*;

public class Generator
{
	static public class CustomSql
	{
		String customSql;
		public CustomSql(String sql)
		{
			customSql = sql;
		}
	}

	static public class DmlStatement
	{
		public String sql;
		public String tableName;
		public List columnNames;
        public List columnValues;
		public boolean[] bindValues;

		public String toString()
		{
			StringBuffer bind = new StringBuffer();
            if(bindValues != null)
            {
                for(int c = 0; c < bindValues.length; c++)
                {
                    if(bindValues[c])
                    {
                        bind.append(c + ": " + columnValues.get(c) + " ("+ columnValues.get(c).getClass() +")\n");
                    }
                }
            }
			return "SQL\n" + sql + "\n\nBIND\n" + bind;
		}
	}

	static public DmlStatement createInsertStmt(String tableName, List columnNames, List columnValues)
	{
		DmlStatement stmt = new DmlStatement();
		stmt.tableName = tableName;
		stmt.columnNames = columnNames;
        stmt.columnValues = columnValues;
		stmt.bindValues = new boolean[columnValues.size()];
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
				values.append(((CustomSql) value).customSql);
			}
			else
			{
				if(value == null)
					values.append("NULL");
				else
				{
					values.append("?");
                    stmt.bindValues[i] = true;
				}
			}
		}

		stmt.sql = "insert into "+tableName+" (" + names + ") values (" + values + ")";
		return stmt;
	}

	static public DmlStatement createUpdateStmt(String tableName, List columnNames, List columnValues, String whereCond)
	{
		DmlStatement stmt = new DmlStatement();
		stmt.tableName = tableName;
        stmt.columnNames = columnNames;
        stmt.columnValues = columnValues;
		stmt.bindValues = new boolean[columnValues.size()];
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
				sets.append(((CustomSql) value).customSql);
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
					stmt.bindValues[i] = true;
				}
			}
		}

		stmt.sql = "update "+tableName+" set " + sets;
        if(whereCond != null)
            stmt.sql += " where " + whereCond;

		return stmt;
	}

    /**
     * Creates SQL Delete statement
     *
     * @param tableName database table name
     * @param whereCond SQL WHERE string
     * @returns DmlStatement
     */
	static public DmlStatement createDeleteStmt(String tableName, String whereCond)
	{
		DmlStatement stmt = new DmlStatement();
		stmt.tableName = tableName;

		stmt.sql = "delete from "+tableName;
        if(whereCond != null)
            stmt.sql += " where " + whereCond;

		return stmt;
	}
}