
package dal.column;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.DialogContext;


public class DatablockColumn extends AbstractColumn
{
	private java.lang.Object defaultValue;
	
	public DatablockColumn(Table table, String name)
	{
		super(table, name);

		setSqlDefn("oracle", "clob");
		setDataClassName("java.lang.Object");
	}
	
	public java.lang.Object getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(java.lang.Object value) { defaultValue = value; }	

	public java.lang.Object parse(String text) { return text; }
	public String format(java.lang.Object value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, java.lang.Object value) { return value != null ? value.toString() : null; }

}
