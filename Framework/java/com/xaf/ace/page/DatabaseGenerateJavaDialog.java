package com.xaf.ace.page;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;
import com.xaf.db.SchemaDocument;
import com.xaf.db.SchemaDocFactory;
import com.xaf.skin.SkinFactory;
import com.xaf.page.PageContext;
import com.xaf.page.PageControllerServlet;
import com.xaf.page.VirtualPath;
import com.xaf.ace.AppComponentsExplorerServlet;
import com.xaf.xml.XmlSource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DatabaseGenerateJavaDialog extends Dialog
{
    protected FilesystemEntriesListValue generatorsList;

    protected TextField sourceFileField;
	protected TextField destRootField;

    protected TextField dataTypesPkgField;
    protected TextField tableTypesPkgField;
    protected TextField tablesPkgField;
    protected TextField domainsPkgField;
    protected TextField rowsPkgField;
    protected TextField rowsListPkgField;
    protected TextField schemaPkgField;
    protected TextField schemaClassNameField;

    protected SelectField dataTypesGeneratorField;
    protected SelectField tableTypesGeneratorField;
    protected SelectField tablesGeneratorField;
    protected SelectField domainsGeneratorField;
    protected SelectField rowsGeneratorField;
    protected SelectField rowsListGeneratorField;
    protected SelectField schemaGeneratorField;

    public DatabaseGenerateJavaDialog()
    {
		super("schemagen", "Generate Data Access Layer (DAL)");

        generatorsList = new FilesystemEntriesListValue();
        generatorsList.setRootPath("config-expr:${framework.shared.xslt-path}/schema-gen/java-gen");
        generatorsList.setFilter("\\.xsl$");

        sourceFileField = new TextField("source_file", "Source file");
        sourceFileField.setSize(60);
		sourceFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
        sourceFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config:app.schema.source-file"));

        destRootField = new TextField("dest_root", "Destination Root");
        destRootField.setSize(50);
		destRootField.setFlag(DialogField.FLDFLAG_REQUIRED);
        destRootField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.database-root-path}/java"));

		dataTypesPkgField = new TextField("data_types_pkg", "Datatypes Package");
        dataTypesPkgField.setSize(40);
		dataTypesPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        dataTypesPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.column"));

        tableTypesPkgField = new TextField("table_types_pkg", "Tabletypes Package");
        tableTypesPkgField.setSize(40);
		tableTypesPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        tableTypesPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.table.type"));

        tablesPkgField = new TextField("tables_pkg", "Tables Package");
        tablesPkgField.setSize(40);
		tablesPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        tablesPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.table"));

        domainsPkgField = new TextField("domains_pkg", "Domains Package");
        domainsPkgField.setSize(40);
		domainsPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        domainsPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.domain"));

        rowsPkgField = new TextField("rows_pkg", "Rows Package");
        rowsPkgField.setSize(40);
		rowsPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        rowsPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.domain.row"));

        rowsListPkgField = new TextField("rows_list_pkg", "Rows List Package");
        rowsListPkgField.setSize(40);
		rowsListPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        rowsListPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal.domain.rows"));

        schemaPkgField = new TextField("schema_pkg", "Schema Package");
        schemaPkgField.setSize(40);
		schemaPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        schemaPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dal"));

        schemaClassNameField = new TextField("schema_class_name", "Schema Class Name");
        schemaClassNameField.setSize(40);
		schemaClassNameField.setFlag(DialogField.FLDFLAG_REQUIRED);
        schemaClassNameField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("DataAccessLayer"));
        schemaClassNameField.setFlag(DialogField.FLDFLAG_COLUMN_BREAK_AFTER);

        dataTypesGeneratorField = new SelectField("data_types_gen", "Datatypes Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        dataTypesGeneratorField.setDefaultValue(new StaticValue("data-type-generator.xsl"));

        tableTypesGeneratorField = new SelectField("table_types_gen", "Tabletypes Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        tableTypesGeneratorField.setDefaultValue(new StaticValue("table-type-generator.xsl"));

        tablesGeneratorField = new SelectField("tables_gen", "Tables Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        tablesGeneratorField.setDefaultValue(new StaticValue("table-generator.xsl"));

        domainsGeneratorField = new SelectField("domains_gen", "Domains Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        domainsGeneratorField.setDefaultValue(new StaticValue("domain-generator.xsl"));

        rowsGeneratorField = new SelectField("rows_gen", "Rows Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        rowsGeneratorField.setDefaultValue(new StaticValue("row-generator.xsl"));

        rowsListGeneratorField = new SelectField("rows_list_gen", "Rows List Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        rowsListGeneratorField.setDefaultValue(new StaticValue("rows-generator.xsl"));

        schemaGeneratorField = new SelectField("schema_gen", "Schema Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        schemaGeneratorField.setDefaultValue(new StaticValue("schema-generator.xsl"));

        //ListValueSource allTables = ValueSourceFactory.getListValueSource("schema-tables:.*");
        //tablesField = new SelectField("tables", "Tables", SelectField.SELECTSTYLE_MULTIDUAL, allTables);
        //tablesField.setFlag(DialogField.FLDFLAG_REQUIRED);
        //tablesField.setDefaultListValue(allTables);
        //tablesField.setSize(8);

        addField(sourceFileField);
		addField(destRootField);

        addField(dataTypesPkgField);
        addField(tableTypesPkgField);
        addField(tablesPkgField);
        addField(domainsPkgField);
        addField(rowsPkgField);
        addField(rowsListPkgField);
        addField(schemaPkgField);
        addField(schemaClassNameField);

        addField(dataTypesGeneratorField);
        addField(tableTypesGeneratorField);
        addField(tablesGeneratorField);
        addField(domainsGeneratorField);
        addField(rowsGeneratorField);
        addField(rowsListGeneratorField);
        addField(schemaGeneratorField);

		setDirector(new DialogDirector());
    }

    public String execute(DialogContext dc)
	{
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(dc.getValue("source_file"));
        if(schemaDoc == null)
            return "Schema file '"+ dc.getValue("source_file") +"' not found.";

        String generatorsListRootPath = generatorsList.getRootPath().getValue(dc);
        SchemaDocument.ObjectRelationalGenerator orGenerator = new SchemaDocument.ObjectRelationalGenerator();
        orGenerator.setDestRoot(dc.getValue("dest_root"));

        orGenerator.setDataTypesPkg(dc.getValue("data_types_pkg"));
        orGenerator.setTableTypesPkg(dc.getValue("table_types_pkg"));
        orGenerator.setTablesPkg(dc.getValue("tables_pkg"));
        orGenerator.setDomainsPkg(dc.getValue("domains_pkg"));
        orGenerator.setRowsPkg(dc.getValue("rows_pkg"));
        orGenerator.setRowsListPkg(dc.getValue("rows_list_pkg"));
        orGenerator.setSchemaPkg(dc.getValue("schema_pkg"));
        orGenerator.setSchemaClassName(dc.getValue("schema_class_name"));

        orGenerator.setDataTypesGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("data_types_gen"));
        orGenerator.setTableTypesGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("table_types_gen"));
        orGenerator.setTablesGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("tables_gen"));
        orGenerator.setDomainsGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("domains_gen"));
        orGenerator.setRowsGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("rows_gen"));
        orGenerator.setRowsListGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("rows_list_gen"));
        orGenerator.setSchemaGeneratorStyleSheet(generatorsListRootPath + "/" + dc.getValue("schema_gen"));

        StringBuffer output = new StringBuffer("<p align='center'>");
        try
		{
            orGenerator.generate(schemaDoc);
            output.append("Generated " + orGenerator.getDataTypesGeneratedCount() + " data types, ");
            output.append(orGenerator.getTableTypesGeneratedCount() + " table types, and ");
            output.append(orGenerator.getTablesGeneratedCount() + " tables.");
		}
		catch(TransformerConfigurationException e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
			output.append("<pre>" + e.toString() + stack.toString() + "</pre>");
		}
		catch(TransformerException e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
			output.append("<pre>" + e.toString() + stack.toString() + "</pre>");
		}
        return output.toString();
	}

}