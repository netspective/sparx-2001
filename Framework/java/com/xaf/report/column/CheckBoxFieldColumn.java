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
		setHeading("<input type=checkbox onclick=\"setAllCheckboxes(this, '"+getFieldId()+"')\">");
	}

	public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc)
	{
		int colIndex = getColIndexInArray();
		ReportContext.ColumnState state = rc.getState(colIndex);
		String fieldName = state.getFieldId();
		String fieldValue = rc.getReport().replaceOutputPatterns(rc, rowNum, rowData, state.getFieldValueTemplate());

		boolean isChecked = false;
		String[] values = rc.getRequest().getParameterValues(fieldName);
		if(values != null)
		{
			for(int v = 0; v < values.length; v++)
			{
				if(values[v].equals(fieldValue))
				{
					isChecked = true;
					break;
				}
			}
		}
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

		return "<input type='checkbox' name='"+ fieldName +"' value='"+ fieldValue +"' "+ (isChecked ? "checked" : "") +">";

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