package com.xaf.ant;

import org.apache.tools.ant.*;
import com.xaf.db.*;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Iterator;

public class GenerateJavaDALTask extends Task
{
    private String schemaDocFile;
    private String destRoot;
    private String styleSheetRoot;

    private String dataTypesPkg = "dal.column";
    private String tableTypesPkg = "dal.table.type";
    private String tablesPkg = "dal.table";
    private String domainsPkg = "dal.domain";
    private String rowsPkg = "dal.domain.row";
    private String rowsListPkg = "dal.domain.rows";
    private String schemaPkg = "dal";
    private String schemaClassName = "DataAccessLayer";

    private String dataTypesGeneratorStyleSheet = "data-type-generator.xsl";
    private String tableTypesGeneratorStyleSheet = "table-type-generator.xsl";
    private String tablesGeneratorStyleSheet = "table-generator.xsl";
    private String domainsGeneratorStyleSheet = "domain-generator.xsl";
    private String rowsGeneratorStyleSheet = "row-generator.xsl";
    private String rowsListGeneratorStyleSheet = "rows-generator.xsl";
    private String schemaGeneratorStyleSheet = "schema-generator.xsl";

    public GenerateJavaDALTask()
    {
    }

    public void setSchema(String schemaDocFile)
    {
        this.schemaDocFile = schemaDocFile;
    }

    public void setDest(String destRoot)
    {
        this.destRoot = destRoot;
    }

    public void setStylesheetroot(String styleSheetRoot)
    {
        this.styleSheetRoot = styleSheetRoot;
    }

    public void setDatatypespkg(String dataTypesPkg)
    {
        this.dataTypesPkg = dataTypesPkg;
    }

    public void setTabletypespkg(String tableTypesPkg)
    {
        this.tableTypesPkg = tableTypesPkg;
    }

    public void setTablespkg(String tablesPkg)
    {
        this.tablesPkg = tablesPkg;
    }

    public void setDomainspkg(String domainsPkg)
    {
        this.domainsPkg = domainsPkg;
    }

    public void setRowspkg(String rowsPkg)
    {
        this.rowsPkg = rowsPkg;
    }

    public void setRowslistpkg(String rowsListPkg)
    {
        this.rowsListPkg = rowsListPkg;
    }

    public void setSchemapkg(String schemaPkg)
    {
        this.schemaPkg = schemaPkg;
    }

    public void setSchemaclassname(String schemaClassName)
    {
        this.schemaClassName = schemaClassName;
    }

    public void setDatatypesgeneratorstylesheet(String dataTypesGeneratorStyleSheet)
    {
        this.dataTypesGeneratorStyleSheet = dataTypesGeneratorStyleSheet;
    }

    public void setTabletypesgeneratorstylesheet(String tableTypesGeneratorStyleSheet)
    {
        this.tableTypesGeneratorStyleSheet = tableTypesGeneratorStyleSheet;
    }

    public void setTablesgeneratorstylesheet(String tablesGeneratorStyleSheet)
    {
        this.tablesGeneratorStyleSheet = tablesGeneratorStyleSheet;
    }

    public void setDomainsgeneratorstylesheet(String domainsGeneratorStyleSheet)
    {
        this.domainsGeneratorStyleSheet = domainsGeneratorStyleSheet;
    }

    public void setRowsgeneratorstylesheet(String rowsGeneratorStyleSheet)
    {
        this.rowsGeneratorStyleSheet = rowsGeneratorStyleSheet;
    }

    public void setRowslistgeneratorstylesheet(String rowsListGeneratorStyleSheet)
    {
        this.rowsListGeneratorStyleSheet = rowsListGeneratorStyleSheet;
    }

    public void setSchemageneratorstylesheet(String schemaGeneratorStyleSheet)
    {
        this.schemaGeneratorStyleSheet = schemaGeneratorStyleSheet;
    }

    public void execute() throws BuildException
	{
        log("Opening SchemaDoc (XML) file " + schemaDocFile + "...");
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(schemaDocFile);
        if(schemaDoc == null)
            throw new BuildException("Unable to open SchemaDoc file '"+ schemaDocFile +"'");

        List errors = schemaDoc.getErrors();
        if(errors.size() > 0)
		{
			for(Iterator ei = errors.iterator(); ei.hasNext(); )
                log("SchemaDoc Warning: " + (String) ei.next());
		}

        SchemaDocument.ObjectRelationalGenerator orGenerator = new SchemaDocument.ObjectRelationalGenerator();
        orGenerator.setDestRoot(destRoot);

        orGenerator.setDataTypesPkg(dataTypesPkg);
        orGenerator.setTableTypesPkg(tableTypesPkg);
        orGenerator.setTablesPkg(tablesPkg);
        orGenerator.setDomainsPkg(domainsPkg);
        orGenerator.setRowsPkg(rowsPkg);
        orGenerator.setRowsListPkg(rowsListPkg);
        orGenerator.setSchemaPkg(schemaPkg);
        orGenerator.setSchemaClassName(schemaClassName);

        orGenerator.setDataTypesGeneratorStyleSheet(styleSheetRoot + "/" + dataTypesGeneratorStyleSheet);
        orGenerator.setTableTypesGeneratorStyleSheet(styleSheetRoot + "/" + tableTypesGeneratorStyleSheet);
        orGenerator.setTablesGeneratorStyleSheet(styleSheetRoot + "/" + tablesGeneratorStyleSheet);
        orGenerator.setDomainsGeneratorStyleSheet(styleSheetRoot + "/" + domainsGeneratorStyleSheet);
        orGenerator.setRowsGeneratorStyleSheet(styleSheetRoot + "/" + rowsGeneratorStyleSheet);
        orGenerator.setRowsListGeneratorStyleSheet(styleSheetRoot + "/" + rowsListGeneratorStyleSheet);
        orGenerator.setSchemaGeneratorStyleSheet(styleSheetRoot + "/" + schemaGeneratorStyleSheet);

        try
		{
            log("Generating Data Access Layer...");
            orGenerator.generate(schemaDoc);
            log("Generated Java Data Access Layer in " + destRoot);
            log("Generated " + orGenerator.getDataTypesGeneratedCount() + " data types");
            log("Generated " + orGenerator.getTableTypesGeneratedCount() + " table types");
            log("Generated " + orGenerator.getTablesGeneratedCount() + " tables");
		}
		catch(Exception e)
		{
            throw new BuildException(e);
		}
	}
}