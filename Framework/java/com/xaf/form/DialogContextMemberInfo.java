package com.xaf.form;

import java.util.List;
import java.util.ArrayList;

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
    private List imports;
	private StringBuffer code = new StringBuffer();

	public DialogContextMemberInfo(String fieldName, String dataType)
	{
		setFieldName(fieldName);
		setDataType(dataType);
	}

	public String getFieldName() { return fieldName; }
	public String getDataType() { return dataType; }
	public String getMemberName() { return memberName; }
	public String getCode() { return code.toString(); }
    public String[] getImportModules() { return imports == null ? null : ((String[]) imports.toArray(new String[imports.size()])); }

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
		this.memberName = com.xaf.xml.XmlSource.xmlTextToJavaIdentifier(fieldName, true);
	}

	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

    public void addImportModule(String module)
    {
        if(imports == null)
            imports = new ArrayList();
        imports.add(module);
    }

    public void addJavaCode(String codeFragment)
    {
        code.append(codeFragment);
    }

    public void addJavaCodeAndSeparator(String codeFragment)
    {
        addJavaCode(codeFragment);
        code.append("\n");
    }
}
