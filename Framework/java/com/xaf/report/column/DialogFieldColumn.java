package com.xaf.report.column;

import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.report.*;

public class DialogFieldColumn extends GeneralColumn
{
	private ReportField parent;
	private String fieldName;
	private String fieldIdRowSuffix;
	private int fieldFlags;

    public DialogFieldColumn()
    {
		super();
		fieldIdRowSuffix = "_${#}";
    }

	public ReportField getParentField() { return parent; }
	public void setParentField(ReportField value) { parent = value; }

	public String getFieldName() { return fieldName; }
	public void setFieldName(String value) { fieldName = value; }

	public String getFieldIdPrefix()
	{
		String fieldName = getFieldName();
		if(fieldName == null)
		{
			fieldName = "col_" + getColIndexInArray();
		}

		if(parent == null)
			return Dialog.PARAMNAME_CONTROLPREFIX + fieldName;
		else
			return parent.getId() + "." + fieldName;
	}

	public String getFieldIdRowSuffix() { return fieldIdRowSuffix; }
	public void setFieldIdRowSuffix(String value) { fieldIdRowSuffix = value; }

	public int getFieldFlags() { return fieldFlags; }
	public void setFieldFlags(int value) { fieldFlags = value; }

	public void importFromColumn(ReportColumn rc)
	{
		super.importFromColumn(rc);

		DialogFieldColumn ffc = (DialogFieldColumn) rc;
		setFieldName(ffc.getFieldName());
		setFieldFlags(ffc.getFieldFlags());
	}

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);
		String value = elem.getAttribute("field-name");
		if(value.length() > 0)
			setFieldName(value);

		value = elem.getAttribute("field-name-row-suffix");
		if(value.length() > 0)
			setFieldIdRowSuffix(value);
	}

}