package com.xaf.db;

/**
 * Lists common relational database management systems (DBMS) so that when
 * DBMS-specific code is required, developers can use these constants instead
 * of hard-coding specific behavior without named references.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DBMS
{
	static public final int GENERIC   = 0;
	static public final int ORACLE    = 1;
	static public final int MYSQL     = 2;
	static public final int MSSQL     = 3;
	static public final int MSJET     = 4;
	static public final int DB2       = 5;
	static public final int POSTGRES  = 6;

	static public final int LASTINDEX = 6;

	static public final String[] ID = new String[]
		{
			"all",
			"ora",
			"mysql",
			"mssql",
			"msjet",
			"db2",
			"pg",
		};

	static public final String[] NAME = new String[]
		{
			"Generic",
			"Oracle",
			"MySQL",
			"Microsoft SQL-Server",
			"Microsoft Jet",
			"IBM DB2",
			"PostgreSQL",
		};

	static public final int getCodeFromId(String id)
	{
		for(int i = 0; i < ID.length; i++)
			if(id.equals(ID[i]))
				return i;

		return GENERIC;
	}

	static public final int getCodeFromName(String name)
	{
		for(int i = 0; i < ID.length; i++)
			if(name.equals(NAME[i]))
				return i;

		return GENERIC;
	}
}