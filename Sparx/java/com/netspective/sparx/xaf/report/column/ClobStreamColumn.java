package com.netspective.sparx.xaf.report.column;

import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ColumnDataCalculator;

import java.sql.Clob;
import java.sql.SQLException;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 23, 2003
 * Time: 9:45:49 AM
 * To change this template use Options | File Templates.
 */
public class ClobStreamColumn extends GeneralColumn {
	public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc) {
		Object oData = rowData[getColIndexInArray()];
		StringBuffer data = new StringBuffer();
		int numCharacters = 0;

		if (oData != null)
		{
			Clob clobData = (Clob) oData;

			try {
				char[] buffer = new char[(int) clobData.length()];
				Reader is = clobData.getCharacterStream();

				if (null == is)
					return "[Error occurred while getting Reader]";

				numCharacters = is.read(buffer);

				if (numCharacters != clobData.length() && numCharacters != -1)
					data.append("[Clob Data Length Mismatch: Read: " + numCharacters + ", Length: " + clobData.length() + "]");

				if (numCharacters != -1)
					data.append(buffer);
			} catch (SQLException e) {
				data.append("[Sql Exception occurred while fetching Clob substring]");
			} catch (IOException e) {
				data.append("[IO Exception occurred while reading from Clob Stream.  Aborting]");
			}
		}

		if(doCalc)
		{
			ColumnDataCalculator calc = rc.getCalc(getColIndexInArray());
			if(calc != null)
				calc.addValue(rc, this, rowNum, rowData, data.toString());
		}
		return data.toString();
	}
}
