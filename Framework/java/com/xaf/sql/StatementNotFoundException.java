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

public class StatementNotFoundException extends Exception
{
	private StatementManager stmtManager;
	private String stmtId;

	StatementNotFoundException(StatementManager manager, String id)
	{
		stmtManager = manager;
		stmtId = id;
	}

	public final String getStmtId() { return stmtId; }
	public final StatementManager getManager() { return stmtManager; }

	public String getMessage()
	{
		return "Statement ID '"+ stmtId + "' not found. Available: " + stmtManager.getStatements().keySet() + ". Errors: " + stmtManager.getErrors();
	}
}
