package com.xaf.form;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.xml.parsers.*;
import javax.servlet.*;

import org.w3c.dom.*;

import com.xaf.form.field.*;
import com.xaf.xml.*;
import com.xaf.skin.*;
import com.xaf.Metric;

public class DialogManager extends XmlSource
{
	public static class DialogInfo
	{
		public Element defnElement;
		public String pkgName;
		public String lookupName;
		public Dialog dialog;

		public DialogInfo(String pkgName, Element elem)
		{
			this.pkgName = pkgName;
			this.defnElement = elem;
			this.dialog = null;
			this.lookupName = pkgName != null ? (pkgName + "." + elem.getAttribute("name")) : elem.getAttribute("name");
		}

		public String getLookupName() { return lookupName; }
	}

	static final String REQPARAMNAME_DIALOG = "dlg";
	Map dialogs = new Hashtable();

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

		if(info.dialog == null)
		{
			String dialogClassName = info.defnElement.getAttribute("class");
			if(dialogClassName == null || dialogClassName.length() == 0)
				dialogClassName = "dialog." + name;

			try
			{
				Class dialogClass = Class.forName(dialogClassName);
				info.dialog = (Dialog) dialogClass.newInstance();
			}
		    catch(Exception e)
			{
				info.dialog = new Dialog();
			}

			info.dialog.importFromXml(info.pkgName, info.defnElement);
		}

		return info.dialog;
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
				result.setHeading("Dialog '"+dialogName+"' not found!");
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
						DialogInfo di = new DialogInfo(stmtPkg, dialogElem);
		    			dialogs.put(di.getLookupName(), di);
						dialogElem.setAttribute("qualified-name", di.getLookupName());
						dialogElem.setAttribute("package", stmtPkg);
					}
					else if(scName.equals("register-field"))
					{
						Element typeElem = (Element) dialogsChild;
						String className = typeElem.getAttribute("class");
						try
						{
							DialogFieldFactory.addFieldType(typeElem.getAttribute("tag-name"), className);
						}
						catch(ClassNotFoundException e)
						{
							errors.add("Field class '"+className+"' not found: " + e.toString());
						}
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
						errors.add("DialogSkin class '"+className+"' access exception: " + e.toString());
					}
					catch(ClassNotFoundException e)
					{
						errors.add("DialogSkin class '"+className+"' not found: " + e.toString());
					}
					catch(InstantiationException e)
					{
						errors.add("DialogSkin class '"+className+"' instantiation exception: " + e.toString());
					}
				}
				else
				{
					skin = new StandardDialogSkin();
				}

				skin.importFromXml(skinElem);
				SkinFactory.addDialogSkin(skinElem.getAttribute("name"), skin);
			}
		}

		addMetaInformation();
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
			packagesMetric.setSum(getSelectNodeListCount("//dialogs"));
			dialogSkinsMetric.setSum(getSelectNodeListCount("//dialog-skin"));
			customFieldTypesMetric.setSum(getSelectNodeListCount("//register-field"));

			NodeList dialogsList = selectNodeList("//dialog");
			dialogsMetric.setSum(dialogsList.getLength());
			for(int n = 0; n < dialogsList.getLength(); n++)
			{
				Element dialogElem = (Element) dialogsList.item(n);
				NodeList dialogChildren = dialogElem.getChildNodes();

				int fieldCount = 0;
				for(int c = 0; c < dialogChildren.getLength(); c++)
				{
					Node dialogChild = dialogChildren.item(c);
					if(dialogChild.getNodeName().startsWith(DialogField.FIELDTAGPREFIX))
						fieldCount++;
				}
				fieldsPerDlgMetric.incrementAverage(fieldCount);
			}

			NodeList fieldsList = selectNodeList("//dialog/*");
			for(int n = 0; n < fieldsList.getLength(); n++)
			{
				Node fieldNode = fieldsList.item(n);
				String nodeName = fieldNode.getNodeName();
				if(! nodeName.startsWith(DialogField.FIELDTAGPREFIX))
					continue;

				Metric fieldTypeMetric = fieldsMetric.getChild(nodeName);
				if(fieldTypeMetric == null)
				{
					fieldTypeMetric = fieldsMetric.createChildMetricSimple(nodeName);
					fieldTypeMetric.setFlag(Metric.METRICFLAG_SHOW_PCT_OF_PARENT);
				}

				fieldsMetric.incrementCount();
				fieldTypeMetric.incrementCount();
			}
		}
		catch(Exception e)
		{
			metrics.createChildMetricSimple(e.toString());
		}

		return metrics;
	}
}