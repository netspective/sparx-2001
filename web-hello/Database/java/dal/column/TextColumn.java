
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class TextColumn extends AbstractColumn
{
	private java.lang.String defaultValue;
	
	public TextColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "varchar(%size%)");
		setDataClassName("java.lang.String");
		setSize(32);
	}
	
	public java.lang.String getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.String value) { defaultValue = value; }	

	public java.lang.String parse(String text) { return text; }
	public String format(java.lang.String value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.String value) { return value != null ? value.toString() : null; }

}
