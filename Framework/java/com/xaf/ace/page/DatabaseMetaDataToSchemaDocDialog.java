package com.xaf.ace.page;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

public class DatabaseMetaDataToSchemaDocDialog extends Dialog
{
	protected TextField dsDriverField;
	protected TextField dsUrlField;
	protected TextField dsUserNameField;
	protected TextField dsPasswordField;
	protected TextField dsCatalogField;
	protected TextField dsSchemaField;
	protected TextField outFileName;

    public DatabaseMetaDataToSchemaDocDialog()
    {
		super("schemadocgen", "Generate SchemaDoc from DataSource MetaData");

		dsDriverField = new TextField("ds_driver_name", "Driver Name");
		dsDriverField.setDefaultValue(new StaticValue("oracle.jdbc.driver.OracleDriver"));
		dsDriverField.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

		dsUrlField = new TextField("ds_url", "Database URL");
		dsUrlField.setDefaultValue(new StaticValue("jdbc:oracle:thin:@aruba:1521:127.0.0.1"));
		dsUrlField.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

		dsUserNameField = new TextField("ds_username", "User Name");
		dsUserNameField.setFlag(DialogField.FLDFLAG_PERSIST);

		dsPasswordField = new TextField("ds_password", "Password");
		dsPasswordField.setFlag(TextField.FLDFLAG_MASKENTRY);
		dsPasswordField.setFlag(DialogField.FLDFLAG_COLUMN_BREAK_AFTER);

		dsCatalogField = new TextField("ds_catalog", "Catalog");
		dsCatalogField.setFlag(DialogField.FLDFLAG_PERSIST);

		dsSchemaField = new TextField("ds_schema", "Schema");
		dsSchemaField.setFlag(DialogField.FLDFLAG_PERSIST);

		outFileName = new TextField("out_file_name", "Output File");
        outFileName.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.database-root-path}/schema-src/generated-schema.xml"));
		outFileName.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

		addField(dsDriverField);
		addField(dsUrlField);
		addField(dsUserNameField);
		addField(dsPasswordField);

		addField(dsCatalogField);
		addField(dsSchemaField);
		addField(outFileName);

		setDirector(new DialogDirector());
    }
}