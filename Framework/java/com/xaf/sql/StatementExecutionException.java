package com.xaf.sql;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;

public class StatementExecutionException extends Exception
{
	private StatementManager stmtManager;
	private StatementManager.StatementInfo stmtInfo;
	private String errorMsg;

	StatementExecutionException(StatementManager manager, StatementManager.StatementInfo si, String parentExcpMsg)
	{
		stmtManager = manager;
		stmtInfo = si;
		errorMsg = parentExcpMsg;
	}

	public final String getStmtId() { return stmtInfo.pkgName + "." + stmtInfo.stmtName; }
	public final String getSQL() { return stmtInfo.sql; }
	public final StatementManager getManager() { return stmtManager; }

	public String getMessage()
	{
		return "Statement ID '"+ getStmtId() + "' executed improperly.\nError: " + errorMsg + "\nSQL: " + getSQL();
	}
}
