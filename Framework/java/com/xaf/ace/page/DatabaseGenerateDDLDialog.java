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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;

public class DatabaseGenerateDDLDialog extends Dialog
{
    protected TextField sourceFileField;
	protected TextField outputFileField;
    protected SelectField generatorFileField;
    protected SelectField tablesField;
    protected FilesystemEntriesListValue generatorsList;

    public DatabaseGenerateDDLDialog()
    {
		super("schemagen", "Generate DDL");

        sourceFileField = new TextField("source_file", "Source file");
        sourceFileField.setSize(60);
		sourceFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
        sourceFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config:app.schema.source-file"));

		outputFileField = new TextField("output_file", "Output file");
        outputFileField.setSize(60);
		outputFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
        outputFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.database-root-path}/schema/complete.sql"));

        generatorsList = new FilesystemEntriesListValue();
        generatorsList.setRootPath("config-expr:${framework.shared.xslt-path}/schema-gen");
        generatorsList.setFilter("\\.xsl$");

        generatorFileField = new SelectField("generator", "Generator", SelectField.SELECTSTYLE_RADIO, generatorsList);
        generatorFileField.setDefaultValue(new com.xaf.value.StaticValue("ansi.xsl"));

        ListValueSource allTables = ValueSourceFactory.getListValueSource("schema-tables:.*");
        tablesField = new SelectField("tables", "Tables", SelectField.SELECTSTYLE_MULTIDUAL, allTables);
        tablesField.setFlag(DialogField.FLDFLAG_REQUIRED);
        tablesField.setDefaultListValue(allTables);
        tablesField.setSize(8);

        addField(sourceFileField);
		addField(outputFileField);
        addField(generatorFileField);
        addField(tablesField);

		setDirector(new DialogDirector());
    }

    public String execute(DialogContext dc)
	{
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(dc.getValue("source_file"));
		String styleSheet = generatorsList.getRootPath().getValue(dc) + "/" + dc.getValue("generator");
        String outputFile = dc.getValue("output_file");

		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(styleSheet));

			transformer.transform
				(new javax.xml.transform.dom.DOMSource(schemaDoc.getDocument()),
				 new javax.xml.transform.stream.StreamResult(outputFile));

            return "<p>Saved generated schema in <a href='" + outputFile + "'>"+ outputFile +"</a>";
		}
		catch(TransformerConfigurationException e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
			return "<pre>" + e.toString() + stack.toString() + "</pre>";
		}
		catch(TransformerException e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
			return "<pre>" + e.toString() + stack.toString() + "</pre>";
		}
	}

}