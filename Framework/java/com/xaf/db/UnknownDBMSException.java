package com.xaf.db;

/**
 * Thrown when a specific DBMS is needed but that DBMS is uknown; for example,
 * if ORACLE is the DBMS required but the ORACLE DBMS is not defined, this
 * exception will be thrown.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class UnknownDBMSException extends Exception
{
	private int dbmsType;

	UnknownDBMSException(int type)
	{
		super("Unknown DBMS type " + type);
		dbmsType = type;
	}

	public final int getType() { return dbmsType; }
}
