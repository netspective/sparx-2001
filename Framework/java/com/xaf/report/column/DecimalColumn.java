package com.xaf.report.column;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.sql.*;
import java.text.*;

import com.xaf.report.*;

public class DecimalColumn extends NumericColumn
{
    public DecimalColumn()
    {
        setAlignStyle(ALIGN_RIGHT);
        setFormat("decimal");
    }

	public String getFormattedData(ReportContext rc, Object[] rowData, boolean doCalc)
	{
        Object oData = rowData[getColIndexInArray()];
        String data = "";
        double value = 0;
        if(oData != null)
        {
            value = ((Number) rowData[getColIndexInArray()]).doubleValue();
            data = ((NumberFormat) getFormatter()).format(value);
        }

        if(doCalc)
        {
            ColumnDataCalculator calc = rc.getCalc(getColIndexInArray());
            if(calc != null)
                calc.addValue(value);
        }
		return data;
	}
}