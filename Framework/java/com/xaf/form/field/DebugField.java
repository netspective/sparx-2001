package com.xaf.form.field;

import java.util.*;
import java.io.*;

import com.xaf.form.*;
import com.xaf.report.*;
import com.xaf.report.column.*;
import com.xaf.skin.*;

public class DebugField extends DialogField
{
	static private Report report;
	static private ReportSkin reportSkin;

    public DebugField()
    {
		super("debug", "Debug");
    }

	//public boolean isVisible(DialogContext dc)
	//{
	//	String debug = dc.getRequest().getParameter("debug");
	//	return debug != null && debug.indexOf('d');
	//}

	public String getControlHtml(DialogContext dc)
	{
		if(report == null)
		{
			report = new StandardReport();
			ReportColumnsList columns = report.getColumns();
			columns.add(new GeneralColumn(0, "Class"));
			columns.add(new GeneralColumn(1, "Skin"));
			columns.add(new GeneralColumn(2, "Trans ID"));
			columns.add(new GeneralColumn(3, "Seq"));
			columns.add(new GeneralColumn(4, "Mode"));
			columns.add(new GeneralColumn(5, "Referer", "{.}"));

			reportSkin = SkinFactory.getReportSkin("detail");
		}

		Object[][] data = new Object[][]
		{
			{
				dc.getDialog().getClass().getName(),
				dc.getSkin().getClass().getName(),
				dc.getTransactionId(),
				new String("Run: " + dc.getRunSequence() + ", Exec: " + dc.getExecuteSequence()),
				new String("Active: " + dc.getActiveMode() + ", Next: " + dc.getNextMode()),
				dc.getOriginalReferer()
			}
		};

		try
		{
			StringWriter writer = new StringWriter();
			ReportContext rc = new ReportContext(dc, report, reportSkin);
			rc.produceReport(writer, data);
			return writer.toString();
		}
		catch(IOException e)
		{
			return e.toString();
		}
	}
}