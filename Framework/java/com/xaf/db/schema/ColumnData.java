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

public interface ColumnData
{
    public boolean hasValue();
    public boolean hasValue(ValueContext vc);

    public boolean isSingleValueSource();
    public boolean isSqlExpr();

    public Object getValue();
    public Object getValue(ValueContext vc);

    public void setValue(Object value);
    public void setSqlExprValue(String sqlExpr);
}
