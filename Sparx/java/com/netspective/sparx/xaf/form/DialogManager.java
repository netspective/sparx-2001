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
 * $Id: DialogManager.java,v 1.3 2002-08-25 17:34:09 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Map;
import java.net.URL;

import javax.servlet.ServletRequest;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.metric.Metric;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.skin.StandardDialogSkin;
import com.netspective.sparx.util.xml.XmlSource;

/**
 * The dialog manager is a dialog pool from which dialogs are retreived. When the <code>DialogManager</code> object is
 * created,  it catalogs all dialogs as XML elements and when a dialog is requested for the first time, a new
 * <code>Dialog</code> object is created and cached.
 */
public class DialogManager extends XmlSource
{
    public static class DialogInfo
    {
        public Element defnElement;
        public String pkgName;
        public String lookupName;
        public Dialog dialog;
        public Class dialogClass;
        public Class dialogContextClass;
        public Class directorClass;

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
        public Dialog getDialog()
        {
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

    static final String REQPARAMNAME_DIALOG = "dlg";
    private Map dialogs = new Hashtable();

    public DialogManager(File file)
    {
        loadDocument(file);
    }

    public Map getDialogs()
    {
        reload();
        return dialogs;
    }

    public Dialog getDialog(String name)
    {
        reload();

        DialogInfo info = (DialogInfo) dialogs.get(name);
        if(info == null)
            return null;

        return info.getDialog();
    }

    public Dialog getDialog(ServletRequest request)
    {
        String dialogName = request.getParameter(REQPARAMNAME_DIALOG);
        if(dialogName == null)
            dialogName = request.getParameter(Dialog.PARAMNAME_DIALOGQNAME);

        if(dialogName == null)
            return null;
        else
        {
            Dialog result = getDialog(dialogName);
            if(result == null)
            {
                result = new Dialog();
                result.setHeading("Dialog '" + dialogName + "' not found!");
            }
            return result;
        }
    }

    public void catalogNodes()
    {
        dialogs.clear();

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
                String stmtPkg = dialogsElem.getAttribute("package");

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
                        DialogInfo di = new DialogInfo(stmtPkg, dialogElem);
                        dialogs.put(di.getLookupName(), di);
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