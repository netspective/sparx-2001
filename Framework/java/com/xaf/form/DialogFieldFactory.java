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
import com.xaf.form.field.*;

public class DialogFieldFactory
{
	static Map fieldClasses = new Hashtable();
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
		defaultsAvailable = true;
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
}