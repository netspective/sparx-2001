package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.sql.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.sql.*;

abstract public class ValueSource implements SingleValueSource
{
	static public final String BLANK_STRING = "";
	protected String valueKey;

	public String getId()
	{
		return getClass().getName() + ":" + valueKey;
	}

    public void initializeSource(String srcParams)
    {
		valueKey = srcParams;
    }

	abstract public String getValue(ValueContext vc);

    public Object getObjectValue(ValueContext vc)
    {
        return getValue(vc);
    }

	public int getIntValue(ValueContext vc)
    {
        return Integer.parseInt(getValue(vc));
    }

	public double getDoubleValue(ValueContext vc)
    {
        return Double.parseDouble(getValue(vc));
    }

	public String getValueOrBlank(ValueContext vc)
	{
		String value = getValue(vc);
		return value == null ? BLANK_STRING : value;
	}

	public boolean supportsSetValue()
	{
		return false;
	}

	public void setValue(ValueContext vc, Object value)
	{
		throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, Object)");
	}

	public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
	{
		switch(storeType)
		{
            case RESULTSET_STORETYPE_SINGLECOLUMN:
                setValue(vc, (Object) StatementManager.getResultSetSingleColumn(rs));
                break;

			case RESULTSET_STORETYPE_RESULTSET:
				setValue(vc, (Object) rs);
				break;

			case RESULTSET_STORETYPE_SINGLEROWMAP:
				setValue(vc, (Object) StatementManager.getResultSetSingleRowAsMap(rs));
				break;

			case RESULTSET_STORETYPE_SINGLEROWFORMFLD:
				throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSet, int) does not support storeType RESULTSET_STORETYPE_SINGLEROWFORMFLD");

			case RESULTSET_STORETYPE_MULTIROWMAP:
				setValue(vc, (Object) StatementManager.getResultSetRowsAsMapArray(rs));
				break;

			case RESULTSET_STORETYPE_SINGLEROWARRAY:
				setValue(vc, (Object) StatementManager.getResultSetSingleRowAsArray(rs));
				break;

			case RESULTSET_STORETYPE_MULTIROWMATRIX:
				setValue(vc, (Object) StatementManager.getResultSetRowsAsMatrix(rs));
				break;
		}
	}

	public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
	{
		switch(storeType)
		{
            case RESULTSET_STORETYPE_SINGLECOLUMN:
                setValue(vc, (Object) StatementManager.getResultSetSingleColumn(data));
                break;

			case RESULTSET_STORETYPE_RESULTSET:
				throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSetMetaData, Object[][], int) does not support RESULTSET_STORETYPE_RESULTSET (because ResultSet has already been exhausted/run).");

			case RESULTSET_STORETYPE_SINGLEROWMAP:
				setValue(vc, (Object) StatementManager.getResultSetSingleRowAsMap(rsmd, data));
				break;

			case RESULTSET_STORETYPE_SINGLEROWFORMFLD:
				throw new RuntimeException("ValueSource.setValue(ValueContext, ResultSet, int) does not support storeType RESULTSET_STORETYPE_SINGLEROWFORMFLD (use DialogFieldValue.setValue instead)");

			case RESULTSET_STORETYPE_MULTIROWMAP:
				setValue(vc, (Object) StatementManager.getResultSetRowsAsMapArray(rsmd, data));
				break;

			case RESULTSET_STORETYPE_SINGLEROWARRAY:
				setValue(vc, (Object) (data.length > 0 ? data[0] : null));
				break;

			case RESULTSET_STORETYPE_MULTIROWMATRIX:
				setValue(vc, (Object) data);
				break;
		}
	}

	public void setValue(ValueContext vc, String value)
	{
		throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, String)");
	}
}