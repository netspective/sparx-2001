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

import com.xaf.form.*;
import com.xaf.db.*;

public interface SingleValueSource
{
	static public final int RESULTSET_STORETYPE_SINGLECOLUMN     = 0;
	static public final int RESULTSET_STORETYPE_SINGLEROWMAP     = 1;
	static public final int RESULTSET_STORETYPE_SINGLEROWFORMFLD = 2;
	static public final int RESULTSET_STORETYPE_MULTIROWMAP      = 3;
	static public final int RESULTSET_STORETYPE_SINGLEROWARRAY   = 4;
	static public final int RESULTSET_STORETYPE_MULTIROWMATRIX   = 5;
	static public final int RESULTSET_STORETYPE_RESULTSET        = 6;

	static public final String[] RESULTSET_STORETYPES =
	{
        "single-column",
		"row-map",
		"row-fields",
		"rows-map",
		"row-array",
		"rows-matrix",
		"result-set",
	};

	public String getId();
    public void initializeSource(String srcParams);
	public String getValue(ValueContext vc);
	public Object getObjectValue(ValueContext vc);
	public int getIntValue(ValueContext vc);
	public double getDoubleValue(ValueContext vc);
	public String getValueOrBlank(ValueContext vc);

	public boolean supportsSetValue();
	public void setValue(ValueContext vc, Object value);
	public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException;
	public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException;
	public void setValue(ValueContext vc, String value);
}