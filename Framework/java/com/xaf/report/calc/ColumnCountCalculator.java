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

public class ColumnCountCalculator implements ColumnDataCalculator
{
    private long count;

    public ColumnCountCalculator()
    {
        count = 0;
    }

    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, double value) { count++; }
    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, String value) { count++; }
    public double getValue(ReportContext rc) { return count; }
}