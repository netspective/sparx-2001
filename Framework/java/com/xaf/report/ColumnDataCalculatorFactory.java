package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import org.w3c.dom.*;
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

	public static void createCatalog(Element parent)
	{
		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Column Data Calculators");
		factoryElem.setAttribute("class", ColumnDataCalculatorFactory.class.getName());

		Element childElem = doc.createElement("calculator");
		childElem.setAttribute("name", "sum");
		childElem.setAttribute("class", ColumnSumCalculator.class.getName());
		factoryElem.appendChild(childElem);

		childElem = doc.createElement("calculator");
		childElem.setAttribute("name", "count");
		childElem.setAttribute("class", ColumnCountCalculator.class.getName());
		factoryElem.appendChild(childElem);
	}

}