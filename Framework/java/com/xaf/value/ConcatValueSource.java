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

public class ConcatValueSource implements SingleValueSource
{
	static public final String BLANK_STRING = "";
	private SingleValueSource valueSource;
	private String prependValue;
	private String appendValue;

    public ConcatValueSource(SingleValueSource vs, String prepend, String append)
    {
		valueSource = vs;
		prependValue = prepend;
		appendValue = append;
    }

	public String getId()
	{
		return getClass().getName() + ":" + prependValue + ":" + valueSource.getId() + ":" + appendValue;
	}

    public void initializeSource(String srcParams)
    {
		throw new RuntimeException("ConcatValueSource does not allow initializeSource: " + srcParams);
    }

	public String getValue(ValueContext vc)
	{
		if(prependValue == null && appendValue == null)
			return valueSource.getValue(vc);

		StringBuffer value = new StringBuffer(valueSource.getValue(vc));
		if(prependValue != null)
			value.insert(0, prependValue);
		if(appendValue != null)
			value.append(appendValue);

		return value.toString();
	}

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
		throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, ResultSet, int)");
	}

	public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
	{
		throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, ResultSetMetaData, Object[][], int)");
	}

	public void setValue(ValueContext vc, String value)
	{
		throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, String)");
	}
}