package com.xaf.db;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

public interface SchemaGeneratorSkin
{
	static public final int GENERATE_ITEMS_COUNT      = 6;

	/* The following constants are ordered by what makes sense for SQL. For example,
	   it doesn't make sense to generate TRIGGER_CODE before the TABLE_DEFN; nor
	   does it make sense to create TABLE_DATA before the FKEY_CONTRAINTS; etc.
	*/

	static public final int GENERATE_DROP_TABLE_DEFN  = 0;
	static public final int GENERATE_TABLE_DEFN       = 1;
	static public final int GENERATE_INDEXES          = 2;
	static public final int GENERATE_FKEY_CONSTRAINTS = 3;
	static public final int GENERATE_TRIGGER_CODE     = 4;
	static public final int GENERATE_TABLE_DATA       = 5;

	public void generate(Writer writer, Element tableElem, int item) throws IOException;
	public void generateDropTableDefn(Writer writer, Element tableElem) throws IOException;
	public void generateTableDefn(Writer writer, Element tableElem) throws IOException;
	public void generateTableContraints(Writer writer, Element tableElem) throws IOException;
	public void generateTableIndexes(Writer writer, Element tableElem) throws IOException;
	public void generateTableCode(Writer writer, Element tableElem) throws IOException;
	public void generateTableData(Writer writer, Element tableElem) throws IOException;
}