package com.xaf.report.calc;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.report.*;

public class ColumnSumCalculator implements ColumnDataCalculator
{
    private double sum;

    public ColumnSumCalculator()
    {
    }

    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, double value) { sum += value; }
    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, String value) { sum += Double.parseDouble(value); }
    public double getValue(ReportContext rc) { return sum; }
}