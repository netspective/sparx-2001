/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: DatabaseGenerateJavaDialog.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.util.value.FilesystemEntriesListValue;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.xif.SchemaDocFactory;
import com.netspective.sparx.xif.SchemaDocument;

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
        generatorsList.setRootPath("config-expr:${"+ com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX +"xslt-path}/schema-gen/java-gen");
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
            return "Schema file '" + dc.getValue("source_file") + "' not found.";

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