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

public class DmlStatement
{
    static public class CustomSql
    {
        String customSql;
        public CustomSql(String sql)
        {
            customSql = sql;
        }
    }

    static public final short STMTTYPE_INSERT = 0;
    static public final short STMTTYPE_UPDATE = 1;
    static public final short STMTTYPE_DELETE = 2;

    private short stmtType;
    private String sql;
    private String tableName;
    private List columnNames;
    private List columnValues;
    private boolean[] bindValues;
    private String whereCond;

    public DmlStatement(String tableName, List columnNames, List columnValues)
    {
        stmtType = STMTTYPE_INSERT;
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.columnValues = columnValues;
        this.bindValues = new boolean[columnValues.size()];
        createInsertSql();
    }

    public DmlStatement(String tableName, List columnNames, List columnValues, String whereCond)
    {
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.columnValues = columnValues;
        this.whereCond = whereCond;
        this.bindValues = new boolean[columnValues.size()];
        createUpdateSql();
    }

    public DmlStatement(String tableName, String whereCond)
    {
        stmtType = STMTTYPE_UPDATE;
        this.tableName = tableName;
        this.whereCond = whereCond;
        createDeleteSql();
    }

    public short getStmtType()
    {
        return stmtType;
    }

    public String getSql()
    {
        return sql;
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

    public void updateValue(int index, Object value)
    {
        columnValues.set(index, value);
        createSql();
    }

    public void createSql()
    {
        switch(stmtType)
        {
            case STMTTYPE_INSERT:
                createInsertSql();
                break;

            case STMTTYPE_UPDATE:
                createUpdateSql();
                break;

            case STMTTYPE_DELETE:
                createDeleteSql();
                break;
        }
    }

    public void createInsertSql()
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
                values.append(((CustomSql) value).customSql);
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

        sql = "insert into "+tableName+" (" + names + ") values (" + values + ")";
    }

    public void createUpdateSql()
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
                    bindValues[i] = true;
                }
            }
        }

        sql = "update "+tableName+" set " + sets;
        if(whereCond != null)
            sql += " where " + whereCond;

    }

    public void createDeleteSql()
	{
		sql = "delete from "+ tableName;
        if(whereCond != null)
            sql += " where " + whereCond;
	}

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
