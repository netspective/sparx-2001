
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class StampColumn extends AbstractColumn
{
	private DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance();
	private java.util.Date defaultValue;
	
	public StampColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("ansi", "date");
		setDataClassName("java.util.Date");
	}
	
	public java.util.Date getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.util.Date value) { defaultValue = value; }	

	public DateFormat getDateFormat() { return dateFormat; }
	public void setDateFormat(DateFormat value) { value = dateFormat; }	
	public java.util.Date parse(String text) throws ParseException { return dateFormat.parse(text); }
	public String format(java.util.Date value) { return value != null ? dateFormat.format(value) : null; }
	public String format(DialogContext dc, java.util.Date value) { return value != null ? dateFormat.format(value) : null; }

}
