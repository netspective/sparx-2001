package com.xaf.sql.query;

import java.util.*;
import com.xaf.value.*;

public class QuerySortFieldRef
{
	static public final String MULTIFIELD_SORT_DELIM = ",";

	private QueryDefinition queryDefn;
	private SingleValueSource fieldNameSrc;
	private boolean isStatic;
	private QueryField[] fields;
	private boolean descending;

    public QuerySortFieldRef(QueryDefinition queryDefn, String fieldName)
    {
		this.queryDefn = queryDefn;
		fieldNameSrc = ValueSourceFactory.getSingleOrStaticValueSource(fieldName);
		if(fieldNameSrc instanceof StaticValue)
		{
			isStatic = true;
			fields = queryDefn.getFieldsFromDelimitedNames(fieldName, MULTIFIELD_SORT_DELIM);
		}
		else
		{
			isStatic = false;
			fields = null;
		}
    }

    public QuerySortFieldRef(QueryDefinition queryDefn, QueryField field)
    {
		this.queryDefn = queryDefn;
		fieldNameSrc = null;
		isStatic = true;
		field = field;
    }

	public boolean isStatic() { return isStatic; }
	public SingleValueSource getFieldName() { return fieldNameSrc; }

	public QueryField[] getFields(ValueContext vc)
	{
		if(isStatic) return fields;

		String fieldName = fieldNameSrc.getValue(vc);
		if(fieldName != null)
		{
			return queryDefn.getFieldsFromDelimitedNames(fieldName, MULTIFIELD_SORT_DELIM);
		}
		else
			return null;
	}

	public boolean isAscending() { return ! descending;	}
	public boolean isDescending() { return descending; }

	public void setAscending() { descending = false; }
	public void setDescending() { descending = true; }
}