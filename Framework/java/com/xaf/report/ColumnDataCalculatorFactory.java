package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.report.calc.*;

public class ColumnDataCalculatorFactory
{
    static public ColumnDataCalculator createDataCalc(String cmd)
    {
        if(cmd.equals("sum"))
            return new ColumnSumCalculator();
        else if(cmd.equals("count"))
            return new ColumnCountCalculator();

        return null;
    }
}