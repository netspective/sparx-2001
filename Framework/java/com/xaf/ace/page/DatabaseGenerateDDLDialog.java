package com.xaf.ace.page;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class DatabaseGenerateDDLDialog extends Dialog
{
	protected TextField outputPathField;

    public DatabaseGenerateDDLDialog()
    {
		super("schemagen", "Generate DDL");

		outputPathField = new TextField("output_path", "Output Path");
		outputPathField.setSize(40);
		outputPathField.setFlag(DialogField.FLDFLAG_REQUIRED);

		addField(outputPathField);

		setDirector(new DialogDirector());
    }

	public void populateValues(DialogContext dc)
	{
		super.populateValues(dc);

		ConfigurationManager manager = ConfigurationManagerFactory.getManager(dc.getServletContext());
		dc.setValue(outputPathField, manager.getValue(dc, "app.database-root-path") + "/schema/complete.sql");
	}

	public boolean isValid(DialogContext dc)
	{
		boolean result = super.isValid(dc);
		if(! result)
			return false;

		/*
		int tablesOption = Integer.parseInt(dc.getValue("tables_option"));
		if(tablesOption == 1)
		{
			String[] tableNames = dc.getValues("tables");
			if(tableNames == null || tableNames.length == 0)
			{
				tablesField.invalidate(dc, "Please choose at least one table.");
				return false;
			}
		}
		*/

		return true;
	}
}