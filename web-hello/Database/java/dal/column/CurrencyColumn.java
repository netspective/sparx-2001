
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class CurrencyColumn extends AbstractColumn
{
	private java.lang.Float defaultValue;
	
	public CurrencyColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "currency");

		setSqlDefn("oracle", "number(12,2)");

		setSqlDefn("mssql", "money");
		setDataClassName("java.lang.Float");
	}
	
	public java.lang.Float getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.Float value) { defaultValue = value; }	

	public java.lang.Float parse(String text) { return new java.lang.Float(text); }
	public String format(java.lang.Float value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.Float value) { return value != null ? value.toString() : null; }

}
