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

public class NumericColumn extends GeneralColumn
{
    public NumericColumn()
    {
        setAlignStyle(ALIGN_RIGHT);
        setFormat("general");
    }

    public void setFormat(String value)
    {
        Format formatter = ReportColumnFactory.getFormat(value);
        if(formatter == null)
        {
            formatter = new DecimalFormat(value);
            ReportColumnFactory.addFormat(value, formatter);
        }
        setFormatter(formatter);
    }

	public String getFormattedData(ReportContext rc, Object[] rowData, boolean doCalc)
	{
        Object oData = rowData[getColIndexInArray()];
        String data = "";
        long value = 0;
        if(oData != null)
        {
            value = ((Number) rowData[getColIndexInArray()]).longValue();
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