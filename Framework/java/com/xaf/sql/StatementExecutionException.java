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
import com.xaf.value.*;

public class StatementExecutionException extends Exception
{
	private StatementManager stmtManager;
	private StatementInfo stmtInfo;
	private String errorMsg;

	StatementExecutionException(StatementManager manager, StatementInfo si, String parentExcpMsg)
	{
		stmtManager = manager;
		stmtInfo = si;
		errorMsg = parentExcpMsg;
	}

	public final String getStmtId() { return stmtInfo.getId(); }
	public final StatementInfo getStmtInfo() { return stmtInfo; }
	public final StatementManager getManager() { return stmtManager; }

	public String getMessage(ValueContext vc)
	{
		return "Statement ID '"+ getStmtId() + "' executed improperly.\nError: " + errorMsg + "\nSQL: " + stmtInfo.getSql(vc);
	}
}
