package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public interface ColumnDataCalculator
{
    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, String value);
    public void addValue(ReportContext rc, ReportColumn columnInfo, long rowNum, Object[] rowData, double value);

    public double getValue(ReportContext rc);
}