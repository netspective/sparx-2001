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
	static public class Sequence
	{
		String seqName;
		public Sequence(String name)
		{
			seqName = name;
		}
	}

	static public class DmlStatement
	{
		public String sql;
		public String tableName;
		public Object[] columns;
		public long bindColFlags;
		public long seqColFlags;

		public String toString()
		{
			StringBuffer bind = new StringBuffer();
			StringBuffer seq = new StringBuffer();

			for(int c = 0; c < columns.length; c++)
			{
				long valueIndexFlag = (long) java.lang.Math.pow(2.0, c);
				if((bindColFlags & valueIndexFlag) != 0)
				{
					bind.append((c / 2) + ": " + columns[c] + " ("+ columns[c].getClass() +")\n");
				}
				if((seqColFlags & valueIndexFlag) != 0)
				{
					seq.append((c / 2) + ": " + columns[c] + " ("+ columns[c].getClass() +")\n");
				}
			}

			return "SQL\n" + sql + "\n\nBIND\n" + bind + "\nSEQUENCES\n" + seq;
		}
	}

	static public DmlStatement createInsertStmt(String tableName, Object[] columns) throws UnknownDBMSException
	{
		DmlStatement stmt = new DmlStatement();
		stmt.tableName = tableName;
		stmt.columns = columns;
		stmt.bindColFlags = 0;
		int columnsCount = columns.length / 2;
		StringBuffer names = new StringBuffer();
		StringBuffer values = new StringBuffer();

		for(int i = 0; i < columnsCount; i++)
		{
			if(i != 0)
			{
				names.append(", ");
				values.append(", ");
			}
			names.append(columns[i * 2]);

			int valueIndex = (i * 2) + 1;
			long indexFlag = (long) java.lang.Math.pow(2.0, (double) valueIndex);
			Object value = columns[valueIndex];

			if(value instanceof Sequence)
			{
				values.append("?");
			    stmt.bindColFlags |= indexFlag;
			    stmt.seqColFlags |= indexFlag;
			}
			else
			{
				if(value == null)
					values.append("NULL");
				else
				{
					values.append("?");
				    stmt.bindColFlags |= indexFlag;
				}
			}
		}

		stmt.sql = "insert into "+tableName+" (" + names + ") values (" + values + ")";
		return stmt;
	}

	static public DmlStatement createUpdateStmt(String tableName, Object[] columns, String whereCond, Object[] whereCondParams) throws UnknownDBMSException
	{
		DmlStatement stmt = new DmlStatement();
		stmt.tableName = tableName;
		stmt.columns = columns;
		stmt.bindColFlags = 0;
		int columnsCount = columns.length / 2;
		StringBuffer sets = new StringBuffer();

		for(int i = 0; i < columnsCount; i++)
		{
			if(i != 0)
				sets.append(", ");

			int valueIndex = (i * 2) + 1;
			long indexFlag = (long) java.lang.Math.pow(2.0, (double) valueIndex);
			Object value = columns[valueIndex];

            sets.append(columns[i * 2]);
            sets.append(" = ");
            if(value == null)
            {
                sets.append("NULL");
            }
            else
            {
                sets.append("?");
                stmt.bindColFlags |= indexFlag;
            }
		}

		stmt.sql = "update "+tableName+" set " + sets;
        if(whereCond != null)
            stmt.sql += " where " + whereCond;

		return stmt;
	}
}