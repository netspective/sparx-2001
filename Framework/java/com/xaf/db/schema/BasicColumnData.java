/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:02:48 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.value.ValueContext;
import com.xaf.value.SingleValueSource;

public class BasicColumnData implements ColumnData
{
    protected static short COLDATAFLAG_VALUEISSINGLEVALUESRC = 1;
    protected static short COLDATAFLAG_VALUEISSQLEXPR = 2;

    protected short flags;
    protected Object data;

    public BasicColumnData(Object data)
    {
        setValue(data);
    }

    public BasicColumnData(String data)
    {
        setSqlExprValue((String) data);
    }

    public boolean hasValue() { return data != null; }
    public boolean hasValue(ValueContext vc)
    {
        return data == null ?
                false :
                (((flags & COLDATAFLAG_VALUEISSINGLEVALUESRC) != 0 ? ((SingleValueSource) data).getValue(vc) : data) != null);
    }

    public boolean isSingleValueSource() { return (flags & COLDATAFLAG_VALUEISSINGLEVALUESRC) != 0; }
    public boolean isSqlExpr() { return (flags & COLDATAFLAG_VALUEISSQLEXPR) != 0; }

    public Object getValue() { return data; }
    public Object getValue(ValueContext vc)
    {
        return data == null ?
                null :
                ((flags & COLDATAFLAG_VALUEISSINGLEVALUESRC) != 0 ? ((SingleValueSource) data).getValue(vc) : data);
    }

    public void setValue(Object value)
    {
        data = value;
        if(data instanceof SingleValueSource)
            flags |= COLDATAFLAG_VALUEISSINGLEVALUESRC;
        else
            flags &= ~COLDATAFLAG_VALUEISSINGLEVALUESRC;
    }

    public void setSqlExprValue(String sqlExpr)
    {
        setValue(sqlExpr);
        if(data != null)
            flags |= COLDATAFLAG_VALUEISSQLEXPR;
        else
            flags &= ~COLDATAFLAG_VALUEISSQLEXPR;
    }
}

