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

				NodeList statementChildren = node.getChildNodes();
				for(int c = 0; c < statementChildren.getLength(); c++)
				{
					Node stmtChild = statementChildren.item(c);
					if(stmtChild.getNodeType() != Node.ELEMENT_NODE)
						continue;

					String scName = stmtChild.getNodeName();
	    			if(scName.equals("dialog"))
					{
						DialogInfo di = new DialogInfo(stmtPkg, (Element) stmtChild);
		    			dialogs.put(di.getLookupName(), di);
						((Element) stmtChild).setAttribute("qualified-name", di.getLookupName());
						((Element) stmtChild).setAttribute("package", stmtPkg);
					}
					else if(scName.equals("register-field"))
					{
						Element typeElem = (Element) stmtChild;
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
					else if(scName.equals("register-skin"))
					{
						Element typeElem = (Element) stmtChild;
						String className = typeElem.getAttribute("class");
						try
						{
							SkinFactory.addDialogSkin(typeElem.getAttribute("name"), className);
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
				}
			}
		}
	}
}