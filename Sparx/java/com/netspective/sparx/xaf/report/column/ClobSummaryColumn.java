package com.netspective.sparx.xaf.report.column;

import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.report.ColumnDataCalculator;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

import java.sql.Clob;
import java.sql.SQLException;
import java.io.Reader;
import java.io.IOException;

import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 23, 2003
 * Time: 9:15:42 AM
 * To change this template use Options | File Templates.
 */
public class ClobSummaryColumn extends GeneralColumn {
	protected int summaryLength = 300;
	protected SingleValueSource detailUrl = null;
	protected SingleValueSource detailUrlAnchorAttrs = null;

	boolean detailUrlFlag = false;
	boolean detailUrlAnchorAttrsFlag = false;

	public ClobSummaryColumn() {
		super();
		setSummaryLength(300);
	}

	public ClobSummaryColumn(int summaryLength) {
		super();
		setSummaryLength(summaryLength);
	}

	public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc) {
		Object oData = rowData[getColIndexInArray()];
		StringBuffer data = new StringBuffer();
		int numCharacters = 0;

		if (oData != null)
		{
			Clob clobData = (Clob) oData;

			try {
				char[] buffer = new char[summaryLength];
				Reader is = clobData.getCharacterStream();

				if (null == is)
					return "[Error occurred while getting Reader]";

				numCharacters = is.read(buffer);

				long clobLength = clobData.length();
				if (numCharacters != getSummaryLength() && numCharacters != clobLength && numCharacters != -1)
					data.append("[Clob Data Length Mismatch: Read: " + numCharacters + ", Length: " + clobLength + "] ");

				if (numCharacters != -1)
					data.append(buffer, 0, numCharacters);

				// If there is a Url specified _and_ the number of characters retreived is greated than summaryLength,
				// it means we have more to display.  In this case, place a "[More...] link.  Otherwise, dont.
				if (detailUrlFlag && clobLength > getSummaryLength())
				{
					String urlString = " <a href=\"" + getDetailUrl().getValue(rc) + "\"";
					if (detailUrlAnchorAttrsFlag)
						urlString += " " + getDetailUrlAnchorAttrs().getValue(rc);
					urlString += ">[More...]</a>";

					data.append(urlString);
				}
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

	// Accessors and Mutaters
	public int getSummaryLength() {
		return summaryLength;
	}

	public void setSummaryLength(int summaryLength) {
		this.summaryLength = summaryLength;
	}

	public SingleValueSource getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = (detailUrl != null && detailUrl.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(detailUrl) : null;

		if(this.detailUrl != null)
			detailUrlFlag = true;
	}

	public SingleValueSource getDetailUrlAnchorAttrs() {
		return detailUrlAnchorAttrs;
	}

	public void setDetailUrlAnchorAttrs(String detailUrlAnchorAttrs) {
		this.detailUrlAnchorAttrs = (detailUrlAnchorAttrs != null && detailUrlAnchorAttrs.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(detailUrlAnchorAttrs) : null;

		if(this.detailUrlAnchorAttrs != null)
			detailUrlAnchorAttrsFlag = true;
	}

	// Miscellaneous methods...

	public void importFromXml(Element elem) {
		super.importFromXml(elem);

		String detailUrl = elem.getAttribute("detail-url");
		if (0 < detailUrl.length()) {
			// There _is_ a value for this attribute - woo woo
			setDetailUrl(detailUrl);
		}

		String detailUrlAnchorAttrs = elem.getAttribute("detail-url-attrs");
		if (0 < detailUrlAnchorAttrs.length()) {
			// There _is_ a value for this attribute - woo woo
			setDetailUrlAnchorAttrs(detailUrlAnchorAttrs);
		}

		String summaryLength = elem.getAttribute("summary-length");
		if (0 < summaryLength.length()) {
			// There _is_ a value for this attribute - now we can use this as the # of characters to display in a
			// clob-summary field
			int summaryLengthInt = 300;

			try {
				summaryLengthInt = Integer.parseInt(summaryLength);
			} catch (NumberFormatException e) {
				// In case of error, let's go back to our holdover number... re-assign it to make sure
				summaryLengthInt = 300;
			}

			setSummaryLength(summaryLengthInt);
		}
	}
}
