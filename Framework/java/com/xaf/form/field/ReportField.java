package com.xaf.form.field;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.task.*;
import com.xaf.task.sql.*;
import com.xaf.value.*;

public class ReportField extends DialogField
{
	static public final int SELECTSTYLE_RADIO      = 0;
	static public final int SELECTSTYLE_MULTICHECK = 1;

	private int style;
	private ListValueSource defaultValue;
	private StatementTask task;
	private Throwable taskException;

	public ReportField()
	{
		super();
		style = SELECTSTYLE_MULTICHECK;
	}

	public ReportField(String aName, String aCaption, int aStyle)
	{
		super(aName, aCaption);
		style = aStyle;
	}

	public final boolean isMulti()
	{
		return style == SELECTSTYLE_MULTICHECK;
	}

	public final int getStyle() { return style; }
	public void setStyle(int value) { style = value; }

	public StatementTask getTask() { return task; }
	public void setReport(StatementTask task) { this.task = task; }

	public boolean defaultIsListValueSource()
	{
		return true;
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String styleValue = elem.getAttribute("style");
		if(styleValue.length() > 0)
		{
			if(styleValue.equalsIgnoreCase("radio"))
				style = SelectField.SELECTSTYLE_RADIO;
			else if (styleValue.equalsIgnoreCase("multicheck"))
				style = SelectField.SELECTSTYLE_MULTICHECK;
			else
				style = SelectField.SELECTSTYLE_RADIO;
		}

		String defaultv = elem.getAttribute("default");
		if(defaultv.length() > 0)
		{
            if(isMulti())
    			defaultValue = ValueSourceFactory.getListValueSource(defaultv);
            else
				super.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(defaultv));
		}
		else
			defaultValue = null;

		NodeList taskElems = elem.getElementsByTagName("statement");
		if(taskElems.getLength() > 0)
		{
			try
			{
				task = new StatementTask();
	    		task.initialize((Element) taskElems.item(0));
			}
			catch(TaskInitializeException e)
			{
				taskException = e;
			}
		}
	}

	public String getControlHtml(DialogContext dc)
	{
		boolean readOnly = isReadOnly(dc);
		String id = getId();
		String defaultControlAttrs = dc.getSkin().getDefaultControlAttrs();

		if(task == null)
			return taskException == null ?
				"No StatementTask is available." :
				taskException.getMessage();

		TaskContext tc = new TaskContext(dc);
		tc.setCanvas(this);
		try
		{
			task.execute(tc);
		}
		catch(TaskExecuteException e)
		{
			return e.toString();
		}
		return tc.hasError() ? tc.getErrorMessage() : tc.getResultMessage();
	}

}