
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class IntegerColumn extends AbstractColumn
{
	private java.lang.Integer defaultValue;
	
	public IntegerColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "integer");

		setSqlDefn("oracle", "number(8)");
		setDataClassName("java.lang.Integer");
	}
	
	public java.lang.Integer getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.Integer value) { defaultValue = value; }	

	public java.lang.Integer parse(String text) { return new java.lang.Integer(text); }
	public String format(java.lang.Integer value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.Integer value) { return value != null ? value.toString() : null; }

}
