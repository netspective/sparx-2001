package com.xaf.form;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DialogContextMemberInfo
{
	private String fieldName;
	private String dataType;
	private String memberName;
	private String getterMethodCode;
	private String setterMethodCode;

	public DialogContextMemberInfo(String fieldName, String dataType)
	{
		setFieldName(fieldName);
		setDataType(dataType);
	}

	public String getFieldName() { return fieldName; }
	public String getDataType() { return dataType; }
	public String getMemberName() { return memberName; }
	public String getGetterMethodCode() { return getterMethodCode; }
	public String getSetterMethodCode() { return setterMethodCode; }

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
		this.memberName = com.xaf.xml.XmlSource.xmlTextToJavaIdentifier(fieldName, true);
	}

	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	public void setGetterMethodCode(String value)
	{
		getterMethodCode = value;
	}

	public void setSetterMethodCode(String value)
	{
		setterMethodCode = value;
	}
}
