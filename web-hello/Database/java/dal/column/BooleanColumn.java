
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class BooleanColumn extends AbstractColumn
{
	private java.lang.Boolean defaultValue;
	
	public BooleanColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "boolean");

		setSqlDefn("oracle", "number(1)");

		setSqlDefn("hsqldb", "BIT");
		setDataClassName("java.lang.Boolean");
	}
	
	public java.lang.Boolean getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.Boolean value) { defaultValue = value; }	

	public java.lang.Boolean parse(String text) { return new java.lang.Boolean(text); }
	public String format(java.lang.Boolean value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.Boolean value) { return value != null ? value.toString() : null; }

}
