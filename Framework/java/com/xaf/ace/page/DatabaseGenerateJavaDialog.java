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
    protected TextField sourceFileField;
	protected TextField destRootField;
    protected TextField dataTypesPkgField;
    protected SelectField dataTypesGeneratorField;
    protected TextField tablesPkgField;
    protected SelectField tablesGeneratorField;
    protected SelectField tablesField;
    protected FilesystemEntriesListValue generatorsList;

    public DatabaseGenerateJavaDialog()
    {
		super("schemagen", "Generate DDL");

        sourceFileField = new TextField("source_file", "Source file");
        sourceFileField.setSize(60);
		sourceFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
        sourceFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config:app.schema.source-file"));

        destRootField = new TextField("dest_root", "Destination Root");
        destRootField.setSize(60);
		destRootField.setFlag(DialogField.FLDFLAG_REQUIRED);
        destRootField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.database-root-path}/java"));

		dataTypesPkgField = new TextField("data_types_pkg", "Datatypes Package");
        dataTypesPkgField.setSize(60);
		dataTypesPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        dataTypesPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("schema.column"));

        generatorsList = new FilesystemEntriesListValue();
        generatorsList.setRootPath("config-expr:${framework.shared.xslt-path}/schema-gen/java-gen");
        generatorsList.setFilter("\\.xsl$");

        dataTypesGeneratorField = new SelectField("data_types_gen", "Datatypes Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        dataTypesGeneratorField.setDefaultValue(new StaticValue("data-type-generator.xsl"));

        tablesPkgField = new TextField("tables_pkg", "Tables Package");
        tablesPkgField.setSize(60);
		tablesPkgField.setFlag(DialogField.FLDFLAG_REQUIRED);
        tablesPkgField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("schema.table"));

        tablesGeneratorField = new SelectField("tables_gen", "Tables Generator", SelectField.SELECTSTYLE_COMBO, generatorsList);
        tablesGeneratorField.setDefaultValue(new StaticValue("table-generator.xsl"));

        ListValueSource allTables = ValueSourceFactory.getListValueSource("schema-tables:.*");
        tablesField = new SelectField("tables", "Tables", SelectField.SELECTSTYLE_MULTIDUAL, allTables);
        tablesField.setFlag(DialogField.FLDFLAG_REQUIRED);
        tablesField.setDefaultListValue(allTables);
        tablesField.setSize(8);

        addField(sourceFileField);
		addField(destRootField);
        addField(dataTypesPkgField);
        addField(dataTypesGeneratorField);
        addField(tablesPkgField);
        addField(tablesGeneratorField);
        addField(tablesField);

		setDirector(new DialogDirector());
    }

    public void createDataTypesClasses(DialogContext dc, SchemaDocument schemaDoc, Map dataTypesClassMap, StringBuffer output)
    {
        String destRoot = dc.getValue("dest_root");
        String dataTypesPkg = dc.getValue("data_types_pkg");
        String dataTypesPkgDirName = dataTypesPkg.replace('.', '/');
        File dataTypesDir = new File(destRoot + "/" + dataTypesPkgDirName);
        dataTypesDir.mkdirs();

        String dataTypesStyleSheet = generatorsList.getRootPath().getValue(dc) + "/" + dc.getValue("data_types_gen");
        try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer dataTypesTransformer = tFactory.newTransformer(new StreamSource(dataTypesStyleSheet));
            dataTypesTransformer.setParameter("package-name", dataTypesPkg);
            Map dataTypes = schemaDoc.getDataTypes();

            DATATYPES:
            for(Iterator i = dataTypes.values().iterator(); i.hasNext(); )
            {
                Element dataTypeElem = (Element) i.next();
                String dataTypeName = XmlSource.xmlTextToJavaIdentifier(dataTypeElem.getAttribute("name"), true) + "Column";
                String dataTypeFile = dataTypesDir.getAbsolutePath() + "/" + dataTypeName + ".java";
                String javaTypeInitCap = null;
                dataTypesClassMap.put(dataTypeElem.getAttribute("name"), dataTypesPkg + "." + dataTypeName);

                NodeList children = dataTypeElem.getChildNodes();
                for(int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if(child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if("composite".equals(childName))
                    {
                        // composites will already have been "expanded" by the SchemaDocument so we don't create any classes
                        if(dataTypeElem.getElementsByTagName("composite").getLength() > 0)
                            continue DATATYPES;
                    }
                    else if("java-type".equals(childName))
                        javaTypeInitCap = XmlSource.xmlTextToJavaIdentifier(child.getFirstChild().getNodeValue(), true);
                }

                dataTypesTransformer.setParameter("data-type-name", dataTypeName);
                if(javaTypeInitCap != null)
                    dataTypesTransformer.setParameter("java-type-init-cap", javaTypeInitCap);

                dataTypesTransformer.transform
                    (new javax.xml.transform.dom.DOMSource(dataTypeElem),
                     new javax.xml.transform.stream.StreamResult(dataTypeFile));

                output.append("<p>Saved generated java for data type '"+ dataTypeName +"' in <a href='" + dataTypeFile + "'>"+ dataTypeFile +"</a>");
            }
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
    }

    public void createTablesClasses(DialogContext dc, SchemaDocument schemaDoc, Map dataTypesClassMap, StringBuffer output)
    {
        String destRoot = dc.getValue("dest_root");
        String tablesPkg = dc.getValue("tables_pkg");
        String tablesPkgDirName = tablesPkg.replace('.', '/');
        File tablesDir = new File(destRoot + "/" + tablesPkgDirName);
        tablesDir.mkdirs();

        String rowsStyleSheet = generatorsList.getRootPath().getValue(dc) + "/" + dc.getValue("tables_gen");
        try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer rowsTransformer = tFactory.newTransformer(new StreamSource(rowsStyleSheet));
            rowsTransformer.setParameter("package-name", tablesPkg);
            Map tables = schemaDoc.getTables();

            TABLES:
            for(Iterator i = tables.values().iterator(); i.hasNext(); )
            {
                Element tableElem = (Element) i.next();
                String tableName = XmlSource.xmlTextToJavaIdentifier(tableElem.getAttribute("name"), true) + "Table";
                String rowsFile = tablesDir.getAbsolutePath() + "/" + tableName + ".java";

                NodeList children = tableElem.getChildNodes();
                for(int c = 0; c < children.getLength(); c++)
                {
                    Node child = children.item(c);
                    if(child.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String childName = child.getNodeName();
                    if("column".equals(childName))
                    {
                        Element columnElem = (Element) child;
                        columnElem.setAttribute("_gen-method-name", XmlSource.xmlTextToJavaIdentifier(columnElem.getAttribute("name"), true));
                        columnElem.setAttribute("_gen-data-type-class", (String) dataTypesClassMap.get(columnElem.getAttribute("type")));

                        NodeList jtnl = columnElem.getElementsByTagName("java-type");
                        if(jtnl.getLength() > 0)
                            columnElem.setAttribute("_gen-java-type-init-cap", XmlSource.xmlTextToJavaIdentifier(jtnl.item(0).getFirstChild().getNodeValue(), true));
                    }
                }

                rowsTransformer.setParameter("table-name", tableName);
                rowsTransformer.transform
                    (new javax.xml.transform.dom.DOMSource(tableElem), new javax.xml.transform.stream.StreamResult(rowsFile));

                output.append("<p>Saved generated java for table row '"+ tableName +"' in <a href='" + rowsFile + "'>"+ rowsFile +"</a>");
            }
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
    }

    public String execute(DialogContext dc)
	{
        Map dataTypesClassMap = new HashMap();
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(dc.getValue("source_file"));
        String destRoot = dc.getValue("dest_root");
        new File(destRoot).mkdirs();

        StringBuffer output = new StringBuffer("<p align='left'>");
        createDataTypesClasses(dc, schemaDoc, dataTypesClassMap, output);
        createTablesClasses(dc, schemaDoc, dataTypesClassMap, output);
        output.append("</p>");
        return output.toString();
	}

}