/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:02:48 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ServletValueContext;
import com.xaf.value.ValueContext;
import com.xaf.form.DialogContext;

public class BasicRowData implements RowData
{
    public static int ROWDATAFLAG_VALUEISSINGLEVALUESRC = 1;
    public static int ROWDATAFLAG_VALUEISSQLEXPR = ROWDATAFLAG_VALUEISSINGLEVALUESRC * 2;

    private Row row;
    private ColumnData[] data;

    public BasicRowData(Row row)
	{
        this.row = row;
        data = new ColumnData[row.getColumnsCount()];
	}

    public Row getRow()
    {
        return row;
    }

    public ColumnData getData(Column column)
    {
        return data[column.getIndexInRow()];
    }

    public void setData(Column column, ColumnData value)
    {
        data[column.getIndexInRow()] = value;
    }
}
