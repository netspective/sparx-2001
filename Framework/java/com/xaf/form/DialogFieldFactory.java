package com.xaf.form;

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
import com.xaf.form.field.*;
import com.xaf.sql.query.ResultSetNavigatorButtonsField;

public class DialogFieldFactory
{
	static Map fieldClasses = new Hashtable();
    static Map conditionalsClasses = new Hashtable();
	static boolean defaultsAvailable = false;

	public static Map getFieldClasses() { return fieldClasses; }

	public static void addFieldType(String tagName, Class cls)
	{
		fieldClasses.put(tagName, cls);
	}

	public static void addFieldType(String tagName, String className) throws ClassNotFoundException
	{
		Class fieldClass = Class.forName(className);
		addFieldType(tagName, fieldClass);
	}

    public static void addConditionalType(String actionName, Class cls)
	{
		conditionalsClasses.put(actionName, cls);
	}

    public static void addConditionalType(String actionName, String className) throws ClassNotFoundException
	{
		Class fieldClass = Class.forName(className);
		addConditionalType(actionName, fieldClass);
	}

	public static void setupDefaults()
	{
		fieldClasses.put("field.debug", DebugField.class);
		fieldClasses.put("field.composite", DialogField.class);
		fieldClasses.put("field.grid", GridField.class);
		fieldClasses.put("field.text", TextField.class);
		fieldClasses.put("field.static", StaticField.class);
		fieldClasses.put("field.memo", MemoField.class);
		fieldClasses.put("field.date", DateTimeField.class);
		fieldClasses.put("field.time", DateTimeField.class);
		fieldClasses.put("field.datetime", DateTimeField.class);
		fieldClasses.put("field.duration", DurationField.class);
		fieldClasses.put("field.boolean", BooleanField.class);
		fieldClasses.put("field.integer", IntegerField.class);
		fieldClasses.put("field.float", FloatField.class);
		fieldClasses.put("field.select", SelectField.class);
		fieldClasses.put("field.separator", SeparatorField.class);
        fieldClasses.put("field.ssn", SocialSecurityField.class);
        fieldClasses.put("field.phone", PhoneField.class);
        fieldClasses.put("field.bloodpressure", BloodPressureField.class);
        fieldClasses.put("field.report", ReportField.class);
        fieldClasses.put("field.zip", ZipField.class);
        fieldClasses.put("field.email", EmailField.class);
        fieldClasses.put("field.uri", UriField.class);
        fieldClasses.put("field.currency", CurrencyField.class);
        fieldClasses.put("field.file", FileField.class);
        fieldClasses.put("field.rs-navigator", ResultSetNavigatorButtonsField.class);

        conditionalsClasses.put("display", DialogFieldConditionalDisplay.class); // legacy
        conditionalsClasses.put("display-on-js-expr", DialogFieldConditionalDisplay.class);
        conditionalsClasses.put("data", DialogFieldConditionalData.class); // legacy
        conditionalsClasses.put("display-when-partner-not-null", DialogFieldConditionalData.class);
        conditionalsClasses.put("invisible-when-data-cmd", DialogFieldConditionalInvisible.class);

		defaultsAvailable = true;
	}

	public static void createCatalog(Element parent)
	{
		if(! defaultsAvailable) setupDefaults();

		Document doc = parent.getOwnerDocument();
		Element factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Dialog Fields");
		factoryElem.setAttribute("class", DialogFieldFactory.class.getName());
		for(Iterator i = fieldClasses.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("dialog-field");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((Class) entry.getValue()).getName());
			factoryElem.appendChild(childElem);
		}

        factoryElem = doc.createElement("factory");
		parent.appendChild(factoryElem);
		factoryElem.setAttribute("name", "Dialog Field Conditionals");
		factoryElem.setAttribute("class", DialogFieldFactory.class.getName());
		for(Iterator i = conditionalsClasses.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) i.next();

			Element childElem = doc.createElement("dialog-field-conditional");
			childElem.setAttribute("name", (String) entry.getKey());
			childElem.setAttribute("class", ((Class) entry.getValue()).getName());
			factoryElem.appendChild(childElem);
		}
	}

	public static DialogField createField(String fieldType)
	{
		if(! defaultsAvailable) setupDefaults();

		Class fieldClass = (Class) fieldClasses.get(fieldType);
		if(fieldClass == null)
			return null;

		try
		{
			return (DialogField) fieldClass.newInstance();
		}
		catch(Exception e)
		{
			return null;
		}
	}

    public static DialogFieldConditionalAction createConditional(String action)
	{
		if(! defaultsAvailable) setupDefaults();

		Class condClass = (Class) conditionalsClasses.get(action);
		if(condClass == null)
			return null;

		try
		{
			return (DialogFieldConditionalAction) condClass.newInstance();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}