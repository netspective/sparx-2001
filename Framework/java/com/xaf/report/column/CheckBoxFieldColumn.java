package com.xaf.report.column;

import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.report.*;

public class CheckBoxFieldColumn extends DialogFieldColumn
{
    public CheckBoxFieldColumn()
    {
		super();
    }

	public void finalizeContents(Report report)
	{
		setHeading("<input type=checkbox onclick=\"setAllCheckboxes(this, '"+getFieldIdPrefix()+"')\">");
	}

	public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc)
	{
		int colIndex = getColIndexInArray();
		String fieldTemplate = rc.getState(colIndex).getFieldIdTemplate();
		String fieldName = rc.getReport().replaceOutputPatterns(rc, rowNum, rowData, fieldTemplate);

		boolean isChecked = rc.getRequest().getParameter(fieldName) != null;
		if(! isChecked)
		{
			Object oData = rowData[colIndex];
			if(oData instanceof Number)
			{
				int value = ((Number) oData).intValue();
				if(value == 0)
					isChecked = false;
				else if(value == 1)
					isChecked = true;
				else
					return "*"; // if the value is not zero or 1, we're not allowing checkbox
			}
			else if(oData instanceof Boolean)
			{
				isChecked = ((Boolean) oData).booleanValue();
			}
			else
			{
				return "CheckBoxFieldColumn (column " + getColIndexInArray() + ") must be a number or boolean";
			}
		}

		return "<input type='checkbox' name='"+ fieldName +"' "+ (isChecked ? "checked" : "") +">";

		/*
        if(doCalc)
        {
            ColumnDataCalculator calc = rc.getCalc(colIndex);
            if(calc != null)
                calc.addValue(oData);
        }
		*/
	}
}