package com.xaf.db;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class SchemaGeneratorDialog extends Dialog
{
	TextField dsDriverField;
	TextField dsUrlField;
	TextField dsUserNameField;
	TextField dsPasswordField;
	SelectField tablesOptionField;
	SelectField tablesField;
	SelectField generateOptionsField;

    public SchemaGeneratorDialog()
    {
		super("schemagen", "Generate DDL");

		dsDriverField = new TextField("ds_driver_name", "Driver Name");
		dsDriverField.setDefaultValue(new StaticValue("sun.jdbc.odbc.JdbcOdbcDriver"));
		dsDriverField.setFlag(DialogField.FLDFLAG_REQUIRED);

		dsUrlField = new TextField("ds_url", "Database URL");
		dsUrlField.setDefaultValue(new StaticValue("jdbc:odbc:OTRACK"));
		dsUrlField.setFlag(DialogField.FLDFLAG_REQUIRED);

		dsUserNameField = new TextField("ds_username", "User Name");
		dsPasswordField = new TextField("ds_password", "Password");
		dsPasswordField.setFlag(TextField.FLDFLAG_MASKENTRY);
		dsPasswordField.setFlag(DialogField.FLDFLAG_COLUMN_BREAK_AFTER);

		tablesOptionField = new SelectField("tables_option", "Tables", SelectField.SELECTSTYLE_COMBO, "All=0;Specific...=1");
		tablesField = new SelectField("tables", null, SelectField.SELECTSTYLE_MULTIDUAL, ValueSourceFactory.getListValueSource("schema-tables:"));
		tablesField.addConditionalAction(new DialogFieldConditionalDisplay(tablesField, "tables_option", "control.selectedIndex == 1"));
		tablesField.setSize(8);

		/* the value of the items (such as Table Definitions=0) comes from
		   the SchemaGeneratorSkin.GENERATE_* constants
		*/
		generateOptionsField =
			new SelectField(
				"generate_options", "Generate", SelectField.SELECTSTYLE_MULTICHECK,
			    "Drop Table Definitions=0;Table Definitions=1;Indexes=2;Foreign-key Constraints=3;Trigger Code=4;Table Data=5");
		generateOptionsField.setFlag(DialogField.FLDFLAG_REQUIRED);

		addField(new SeparatorField("JDBC 2.0 Data Source"));
		addField(dsDriverField);
		addField(dsUrlField);
		addField(dsUserNameField);
		addField(dsPasswordField);

		addField(new SeparatorField("DDL Options"));
		addField(generateOptionsField);
		addField(tablesOptionField);
		addField(tablesField);

		setDirector(new DialogDirector());
    }

	public boolean isValid(DialogContext dc)
	{
		boolean result = super.isValid(dc);
		if(! result)
			return false;

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

		return true;
	}
}