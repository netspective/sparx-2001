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
 * $Id: DialogManager.java,v 1.8 2003-01-06 17:33:57 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.servlet.ServletContext;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import com.netspective.sparx.util.metric.Metric;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.skin.StandardDialogSkin;
import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.xif.SchemaDocument;
import com.netspective.sparx.xif.SchemaDocFactory;

/**
 * The dialog manager is a dialog pool from which dialogs are retreived. When the <code>DialogManager</code> object is
 * created,  it catalogs all dialogs as XML elements and when a dialog is requested for the first time, a new
 * <code>Dialog</code> object is created and cached.
 */
public class DialogManager extends XmlSource
{
    public class DialogInfo
    {
        private boolean finalized;
        private Element defnElement;
        private String pkgName;
        private String lookupName;
        private Dialog dialog;
        private Class dialogClass;
        private Class dialogContextClass;
        private Class directorClass;
        private String tableDialogTableName;

        public DialogInfo(String pkgName, Element elem, String tableDialogTableName)
        {
            this(pkgName, elem);
            if(defnElement.getAttribute("table").length() == 0)
                defnElement.setAttribute("table", tableDialogTableName);
            this.tableDialogTableName = tableDialogTableName;
        }

        public DialogInfo(String pkgName, Element elem)
        {
            this.pkgName = pkgName;
            this.defnElement = elem;
            this.dialog = null;
            this.lookupName = pkgName != null ? (pkgName + "." + elem.getAttribute("name")) : elem.getAttribute("name");

            boolean autoFind = false;
            String dialogClassName = defnElement.getAttribute("class");
            if(dialogClassName == null || dialogClassName.length() == 0)
            {
                dialogClassName = "form.";
                if(pkgName != null)
                    dialogClassName += pkgName + ".";
                dialogClassName += com.netspective.sparx.util.xml.XmlSource.xmlTextToJavaIdentifier(elem.getAttribute("name"), true);
                autoFind = true;
            }

            defnElement.setAttribute("qualified-name", lookupName);
            defnElement.setAttribute("package", pkgName);

            findDialogContextClass();
            try
            {
                dialogClass = Class.forName(dialogClassName);
                defineClassAttributes(defnElement, dialogClass, "_");
            }
            catch(ClassNotFoundException e)
            {
                if(!autoFind)
                    defnElement.setAttribute("_class-name", e.toString());
                dialogClass = Dialog.class;
            }

            String directorClassName = elem.getAttribute("director-class");
            if(directorClassName != null && directorClassName.length() > 0)
            {
                try
                {
                    directorClass = Class.forName(directorClassName);
                    defineClassAttributes(defnElement, directorClass, "_director-");
                }
                catch(Exception e)
                {
                    directorClass = DialogDirector.class;
                    elem.setAttribute("_director-class-name", e.toString());
                }
            }
            else
                directorClass = DialogDirector.class;

            if(hasTableColumnElements())
            {
                if(finalizeDialogsWithTableColFields == null)
                    finalizeDialogsWithTableColFields = new HashSet();
                finalizeDialogsWithTableColFields.add(this);
            }
        }

        public boolean isFinalized()
        {
            return finalized;
        }

        private void finalizeDefinition(ServletContext context, SchemaDocument defaultSchemaDoc)
        {
            // resolve all the table-column fields using the default schema
            NodeList tableColumnElements = defnElement.getElementsByTagName(Dialog.FIELDNAME_SCHEMA_TABLECOL);

            // since we're modifying the nodelist (adding/subtracting nodes) we can't do a simple "for loop"
            while(tableColumnElements.getLength() > 0)
            {
                Element tableColumnPlaceholderElem = (Element) tableColumnElements.item(0);
                resolveTableColumnField(context, defaultSchemaDoc, tableColumnPlaceholderElem);
                tableColumnElements = defnElement.getElementsByTagName(Dialog.FIELDNAME_SCHEMA_TABLECOL);
            }

            finalized = true;
        }

        public Element createErrorField(Element tableColumnPlaceholderElem, String message, String columnName)
        {
            Element result = tableColumnPlaceholderElem.getOwnerDocument().createElement("field.text");
            result.setAttribute("name", "error");
            result.setAttribute("caption", "Error");
            result.setAttribute("default", message + "\\: table '"+ tableColumnPlaceholderElem.getAttribute("table") +"' column '"+ columnName + "' in &lt;" + Dialog.FIELDNAME_SCHEMA_TABLECOL + "&gt;");
            result.setAttribute("read-only", "yes");
            return result;
        }

        private void resolveTableColumnField(ServletContext context, SchemaDocument defaultSchemaDoc, Element tableColumnPlaceholderElem)
        {
            final String schemaFileName = tableColumnPlaceholderElem.getAttribute("schema");
            SchemaDocument schemaDoc = schemaFileName.length() == 0 ? (defaultSchemaDoc != null ? defaultSchemaDoc : SchemaDocFactory.getDoc(context)) : SchemaDocFactory.getDoc(schemaFileName);
            if(schemaDoc == null)
                defnElement.replaceChild(createErrorField(tableColumnPlaceholderElem, "Schema '"+ schemaFileName +"' not found", tableColumnPlaceholderElem.getAttribute("column")), tableColumnPlaceholderElem);
            else
            {
                if(dependentSchemaDocs == null)
                    dependentSchemaDocs = new HashSet();
                dependentSchemaDocs.add(schemaDoc);

                if(tableDialogTableName != null && tableColumnPlaceholderElem.getAttribute("table").length() == 0)
                    tableColumnPlaceholderElem.setAttribute("table", tableDialogTableName);

                String tableName = tableColumnPlaceholderElem.getAttribute("table");
                String columnNamesPattern = tableColumnPlaceholderElem.getAttribute("column");
                if(columnNamesPattern.length() == 0) columnNamesPattern = tableColumnPlaceholderElem.getAttribute("columns");

                List columnNames = schemaDoc.getNamesOfColumnsInTableWithFieldDefns(tableName);
                if(columnNames != null)
                {
                    StringListMatcher matcher = new StringListMatcher(columnNames, columnNamesPattern);
                    List matchedColumnNames = matcher.getMatchedItems();
                    for(int i = 0; i < matchedColumnNames.size(); i++)
                    {
                        String matchedColumnName = (String) matchedColumnNames.get(i);
                        SchemaDocument.DialogFieldDefinition dfDefn = schemaDoc.getDialogFieldDefn(tableName, matchedColumnName);
                        if(dfDefn == null)
                            defnElement.insertBefore(createErrorField(tableColumnPlaceholderElem, "Field definition not found in schema", matchedColumnName), tableColumnPlaceholderElem);
                        else
                        {
                            Element actualField = dfDefn.resolveDialogField(defnElement, tableColumnPlaceholderElem);
                            actualField.setAttribute("column", matchedColumnName);
                        }

                    }
                }
                defnElement.removeChild(tableColumnPlaceholderElem);
            }
        }

        public boolean hasTableColumnElements()
        {
            return defnElement.getElementsByTagName(Dialog.FIELDNAME_SCHEMA_TABLECOL).getLength() > 0;
        }

        public Element getDefnElem()
        {
            return defnElement;
        }

        public String getPackageName()
        {
            return pkgName;
        }

        public String getLookupName()
        {
            return lookupName;
        }

        public Class getDialogClass()
        {
            return dialogClass;
        }

        /**
         * Gets the dialog context class
         *
         * @return Class
         */
        public Class getDialogContextClass()
        {
            return dialogContextClass;
        }

        public void findDialogContextClass()
        {
            try
            {
                dialogContextClass = Dialog.findDialogContextClass(pkgName, defnElement);
                if(dialogContextClass != DialogContext.class)
                    defineClassAttributes(defnElement, dialogContextClass, "_dc-");
            }
            catch(ClassNotFoundException e)
            {
                defnElement.setAttribute("_dc-class-name", e.toString());
            }
        }

        /**
         * Gets the dialog object and if it doesn't exist, a new one is created and returned
         *
         * @return Dialog
         */
        public Dialog getDialog(ServletContext context)
        {
            if(! finalized)
                finalizeDefinition(context, null);

            if(dialog == null)
            {
                try
                {
                    dialog = (Dialog) dialogClass.newInstance();
                }
                catch(Exception e)
                {
                    dialog = new Dialog();
                }
                dialog.setDialogDirectorClass(directorClass);
                dialog.setDialogContextClass(dialogContextClass);
                dialog.importFromXml(pkgName, defnElement);
            }
            return dialog;
        }

        public File generateDialogBean(String outputPath, String pkgPrefix) throws IOException
        {
            Dialog activeDialog = new Dialog();
            activeDialog.importFromXml(pkgName, defnElement);

            /* figure out the file name we need to create, and create the necessary paths */
            String classNamePrep = new String(lookupName);
            classNamePrep.replace('.', '/');

            File javaFilePath = new File(pkgName == null ? outputPath : (outputPath + "/" + pkgName));
            javaFilePath.mkdirs();

            File javaFile = new File(javaFilePath, com.netspective.sparx.util.xml.XmlSource.xmlTextToJavaIdentifier(defnElement.getAttribute("name"), true) + "Context.java");

            Writer writer = new java.io.FileWriter(javaFile);
            writer.write(activeDialog.getSubclassedDialogContextCode(pkgPrefix));
            writer.close();

            findDialogContextClass();
            return javaFile;
        }
    }

    public class TableDialogReference
    {
        private Element pkgElem;
        private Element defnElem;

        public TableDialogReference(Element pkgElem, Element defnElem)
        {
            this.pkgElem = pkgElem;
            this.defnElem = defnElem;
        }

        public void resolveTableDialog(ServletContext context, SchemaDocument defaultSchemaDoc)
        {
            final String schemaFileName = defnElem.getAttribute("schema");

            SchemaDocument schemaDoc = schemaFileName.length() == 0 ? (defaultSchemaDoc != null ? defaultSchemaDoc : SchemaDocFactory.getDoc(context)) : SchemaDocFactory.getDoc(schemaFileName);
            if(schemaDoc == null)
                addError("Schema '"+ schemaFileName +"' not found");
            else
            {
                if(dependentSchemaDocs == null)
                    dependentSchemaDocs = new HashSet();
                dependentSchemaDocs.add(schemaDoc);

                String tableNamePattern = defnElem.getAttribute("table");
                if(tableNamePattern.length() == 0) tableNamePattern = defnElem.getAttribute("tables");

                List namesOfTablesWithDialogs = schemaDoc.getNamesOfTablesWithDialogsDefns();
                if(namesOfTablesWithDialogs != null)
                {
                    StringListMatcher matcher = new StringListMatcher(namesOfTablesWithDialogs, tableNamePattern);
                    List matchedTableNames = matcher.getMatchedItems();
                    for(int i = 0; i < matchedTableNames.size(); i++)
                    {
                        String matchedTableName = (String) matchedTableNames.get(i);
                        SchemaDocument.TableDialogDefinition tableDialogDefinition = schemaDoc.getTableDialogDefn(matchedTableName);
                        if(tableDialogDefinition != null)
                        {
                            Element actualDialog = tableDialogDefinition.createDialogElement(pkgElem, defnElem);
                            processTemplates(actualDialog);
                            actualDialog.setAttribute("table", matchedTableName);
                            DialogInfo dialogInfo = new DialogInfo(pkgElem.getAttribute("package"), actualDialog, matchedTableName);
                            dialogs.put(dialogInfo.getLookupName(), dialogInfo);
                        }
                        else
                            addError("Table dialog for table '"+ matchedTableName +"' not found in schema '"+ schemaFileName +"'");
                    }
                }
            }
        }
    }

    static final String REQPARAMNAME_DIALOG = "dlg";
    private Map dialogs = new HashMap();  // all dialogs
    private Set finalizeTableDialogs;
    private Set finalizeDialogsWithTableColFields;
    private Set dependentSchemaDocs;

    public DialogManager(File file)
    {
        loadDocument(file);
    }

    public boolean sourceChanged()
    {
        if(super.sourceChanged())
            return true;

        if(dependentSchemaDocs == null)
            return false;

        Iterator i = dependentSchemaDocs.iterator();
        while (i.hasNext())
        {
            SchemaDocument schemaDocument = (SchemaDocument) i.next();
            if(schemaDocument.sourceChanged())
                return true;
        }

        return false;
    }

    public Document getDocument(ServletContext context, SchemaDocument defaultSchemaDoc)
    {
        Document result = getDocument();
        finalizeDialogs(context, defaultSchemaDoc);
        return result;
    }

    private void finalizeDialogs(ServletContext context, SchemaDocument defaultSchemaDoc)
    {
        if(finalizeTableDialogs != null)
        {
            Iterator i = finalizeTableDialogs.iterator();
            while (i.hasNext())
            {
                TableDialogReference tableDialogReference = (TableDialogReference) i.next();
                tableDialogReference.resolveTableDialog(context, defaultSchemaDoc);
            }
            finalizeTableDialogs = null;
            addMetaInformation();
        }
        if(finalizeDialogsWithTableColFields != null)
        {
            Iterator i = finalizeDialogsWithTableColFields.iterator();
            while (i.hasNext())
            {
                DialogInfo dialogInfo = (DialogInfo) i.next();
                if(! dialogInfo.finalized) dialogInfo.finalizeDefinition(context, defaultSchemaDoc);
            }
            finalizeDialogsWithTableColFields = null;
            addMetaInformation();
        }
    }

    public Map getDialogs(ServletContext context, SchemaDocument defaultSchemaDoc)
    {
        reload();
        finalizeDialogs(context, defaultSchemaDoc);
        return dialogs;
    }

    public Dialog getDialog(ServletContext context, SchemaDocument defaultSchemaDoc, String name)
    {
        reload();
        finalizeDialogs(context, defaultSchemaDoc);

        DialogInfo info = (DialogInfo) dialogs.get(name);
        if(info == null)
            return null;

        return info.getDialog(context);
    }

    public String[] getCatalogedNodeIdentifiers()
    {
        return (String[]) dialogs.keySet().toArray(new String[dialogs.size()]);
    }

    public void catalogNodes()
    {
        dialogs.clear();
        finalizeTableDialogs = null;
        finalizeDialogsWithTableColFields = null;
        dependentSchemaDocs = null;

        if(xmlDoc == null)
            return;

        NodeList children = xmlDoc.getDocumentElement().getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            if(nodeName.equals("dialogs"))
            {
                Element dialogsElem = (Element) node;
                String dialogsPkg = dialogsElem.getAttribute("package");
                String idClassName = dialogsElem.getAttribute("id-class");
                if(idClassName.length() > 0)
                    catalogedNodeIdentifiersClassName = idClassName;

                NodeList dialogsChildren = node.getChildNodes();
                for(int c = 0; c < dialogsChildren.getLength(); c++)
                {
                    Node dialogsChild = dialogsChildren.item(c);
                    if(dialogsChild.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String scName = dialogsChild.getNodeName();
                    if(scName.equals("dialog"))
                    {
                        Element dialogElem = (Element) dialogsChild;
                        processTemplates(dialogElem);
                        DialogInfo di = new DialogInfo(dialogsPkg, dialogElem);
                        dialogs.put(di.getLookupName(), di);
                    }
                    else if(scName.equals("table-dialog") || scName.equals("table-dialogs"))
                    {
                        if(finalizeTableDialogs == null)
                            finalizeTableDialogs = new HashSet();
                        finalizeTableDialogs.add(new TableDialogReference(dialogsElem, (Element) dialogsChild));
                    }
                    else if(scName.equals("register-field"))
                    {
                        throw new RuntimeException("The register-field tag should be specified under the root tag now (not in dialogs tag).");
                    }
                }
            }
            else if(nodeName.equals("dialog-skin"))
            {
                Element skinElem = (Element) node;
                DialogSkin skin = null;
                String className = skinElem.getAttribute("class");
                if(className.length() > 0)
                {
                    try
                    {
                        Class skinClass = Class.forName(className);
                        skin = (DialogSkin) skinClass.newInstance();
                    }
                    catch(IllegalAccessException e)
                    {
                        errors.add("DialogSkin class '" + className + "' access exception: " + e.toString());
                    }
                    catch(ClassNotFoundException e)
                    {
                        errors.add("DialogSkin class '" + className + "' not found: " + e.toString());
                    }
                    catch(InstantiationException e)
                    {
                        errors.add("DialogSkin class '" + className + "' instantiation exception: " + e.toString());
                    }
                }
                else
                {
                    skin = new StandardDialogSkin();
                }

                skin.importFromXml(skinElem);
                SkinFactory.addDialogSkin(skinElem.getAttribute("name"), skin);
            }
            else if(nodeName.equals("register-field"))
            {
                Element typeElem = (Element) node;
                String className = typeElem.getAttribute("class");
                try
                {
                    DialogFieldFactory.addFieldType(typeElem.getAttribute("tag-name"), className);
                }
                catch(ClassNotFoundException e)
                {
                    errors.add("Field class '" + className + "' not found: " + e.toString());
                }
            }
            else
            {
                catalogElement((Element) node);
            }
        }

        addMetaInformation();
    }

    static public long getFieldsCount(Metric fieldsMetric, Node parent)
    {
        long totalsCount = 0;

        NodeList children = parent.getChildNodes();
        if(children != null && children.getLength() > 0)
        {
            for(int c = 0; c < children.getLength(); c++)
            {
                Node child = children.item(c);
                String nodeName = child.getNodeName();

                if(nodeName.startsWith(DialogField.FIELDTAGPREFIX))
                {
                    totalsCount++;

                    Metric fieldTypeMetric = fieldsMetric.getChild(nodeName);
                    if(fieldTypeMetric == null)
                    {
                        fieldTypeMetric = fieldsMetric.createChildMetricSimple(nodeName);
                        fieldTypeMetric.setFlag(Metric.METRICFLAG_SHOW_PCT_OF_PARENT);
                    }

                    fieldsMetric.incrementCount();
                    fieldTypeMetric.incrementCount();
                }

                totalsCount += getFieldsCount(fieldsMetric, child);
            }
        }

        return totalsCount;
    }

    public Metric getMetrics(Metric root)
    {
        reload();

        Metric metrics = root.createChildMetricGroup("User Interface");
        Metric packagesMetric = metrics.createChildMetricSimple("Total Packages");
        Metric dialogsMetric = metrics.createChildMetricSimple("Total Dialogs");
        Metric fieldsPerDlgMetric = metrics.createChildMetricAverage("Avg Fields per Dialog");
        Metric fieldsMetric = metrics.createChildMetricSimple("Total Fields");
        fieldsMetric.setFlag(Metric.METRICFLAG_SORT_CHILDREN);
        Metric dialogSkinsMetric = metrics.createChildMetricSimple("Custom Dialog Skins");
        Metric customFieldTypesMetric = metrics.createChildMetricSimple("Custom Field Types");

        try
        {
            dialogSkinsMetric.setSum(getSelectNodeListCount("//dialog-skin"));
            customFieldTypesMetric.setSum(getSelectNodeListCount("//register-field"));

            NodeList dialogsList = selectNodeList("//dialog");
            dialogsMetric.setSum(dialogsList.getLength());
            for(int n = 0; n < dialogsList.getLength(); n++)
            {
                long fieldCount = getFieldsCount(fieldsMetric, dialogsList.item(n));
                fieldsPerDlgMetric.incrementAverage(fieldCount);
            }
        }
        catch(Exception e)
        {
            metrics.createChildMetricSimple(e.toString());
        }

        return metrics;
    }
}