package com.xaf.sql.query;

import com.xaf.value.*;

public class QuerySortFieldRef
{
	private QueryDefinition queryDefn;
	private SingleValueSource fieldNameSrc;
	private boolean isStatic;
	private QueryField field;
	private boolean descending;

    public QuerySortFieldRef(QueryDefinition queryDefn, String fieldName)
    {
		this.queryDefn = queryDefn;
		fieldNameSrc = ValueSourceFactory.getSingleOrStaticValueSource(fieldName);
		if(fieldNameSrc instanceof StaticValue)
		{
			isStatic = true;
			field = queryDefn.getField(fieldName);
		}
		else
		{
			isStatic = false;
			field = null;
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

	public QueryField getField(ValueContext vc)
	{
		if(isStatic) return field;

		String fieldName = fieldNameSrc.getValue(vc);
		if(fieldName != null)
			return queryDefn.getField(fieldName);
		else
			return null;
	}

	public boolean isAscending() { return ! descending;	}
	public boolean isDescending() { return descending; }

	public void setAscending() { descending = false; }
	public void setDescending() { descending = true; }
}