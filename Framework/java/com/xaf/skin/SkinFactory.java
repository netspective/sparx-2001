package com.xaf.skin;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.report.*;

public class SkinFactory
{
	private static Map reportSkins = new Hashtable();
	private static Map dialogSkins = new Hashtable();
	private static boolean haveReportSkinsDefaults;
	private static boolean haveDialogSkinsDefaults;

	public static void addReportSkin(String id, ReportSkin skin)
	{
		reportSkins.put(id, skin);
	}

	public static void addReportSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		Class skinClass = Class.forName(className);
		addReportSkin(id, (ReportSkin) skinClass.newInstance());
	}

	public static void setupReportSkinsDefaults()
	{
        if(! reportSkins.containsKey("report"))
		    addReportSkin("report", new HtmlReportSkin());
		addReportSkin("component", new HtmlComponentSkin());
		addReportSkin("detail", new HtmlSingleRowReportSkin(1, true));
		addReportSkin("detail-2col", new HtmlSingleRowReportSkin(2, true));
		addReportSkin("data-only", new HtmlSingleRowReportNoCaptionSkin(1, true));
		addReportSkin("text-csv", new TextReportSkin(".csv", ",", "\"", true));
		addReportSkin("text-tab", new TextReportSkin(".txt", "  ", null, true));
		haveReportSkinsDefaults = true;
	}

	public static ReportSkin getReportSkin(String id)
	{
		if(! haveReportSkinsDefaults)
			setupReportSkinsDefaults();

		return (ReportSkin) reportSkins.get(id);
	}

	public static void addDialogSkin(String id, DialogSkin skin)
	{
		dialogSkins.put(id, skin);
	}

	public static void addDialogSkin(String id, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		Class skinClass = Class.forName(className);
		addDialogSkin(id, (DialogSkin) skinClass.newInstance());
	}

	public static void setupDialogSkinsDefaults()
	{
		/**
		 * Remember, skins can be created at runtime so we don't want to override
		 * any that a user might have already put into the factory.
		 */

		if(! dialogSkins.containsKey("default"))
			addDialogSkin("default", new StandardDialogSkin());

		if(! dialogSkins.containsKey("hand-held"))
			addDialogSkin("hand-held", new HandHeldDialogSkin());

		haveDialogSkinsDefaults = true;
	}

	public static DialogSkin getDialogSkin(String id)
	{
		if(! haveDialogSkinsDefaults)
			setupDialogSkinsDefaults();

		return (DialogSkin) dialogSkins.get(id);
	}

	public static DialogSkin getDialogSkin()
	{
		return getDialogSkin("default");
	}

	public static void createCatalog(Element parent)
	{
		if(! haveReportSkinsDefaults) setupReportSkinsDefaults();
		if(! haveDialogSkinsDefaults) setupDialogSkinsDefaults();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Dialog Skins");
		factoryElem.setAttribute("class", SkinFactory.class.getName());
		for(Iterator i = dialogSkins.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("dialog-skin");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((DialogSkin) entry.getValue()).getClass().getName());
			factoryElem.appendChild(childElem);
		}

		factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Report Skins");
		factoryElem.setAttribute("class", SkinFactory.class.getName());
		for(Iterator i = reportSkins.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("report-column-format");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((ReportSkin) entry.getValue()).getClass().getName());
			factoryElem.appendChild(childElem);
		}
	}

}