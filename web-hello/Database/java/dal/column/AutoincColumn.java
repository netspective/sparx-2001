
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class AutoincColumn extends AbstractColumn
{
	private java.lang.Long defaultValue;
	
	public AutoincColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "integer");

		setSqlDefn("oracle", "number(16)");
		setDataClassName("java.lang.Long");
	}
	
	public java.lang.Long getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.Long value) { defaultValue = value; }	

	public java.lang.Long parse(String text) { return new java.lang.Long(text); }
	public String format(java.lang.Long value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.Long value) { return value != null ? value.toString() : null; }

}
