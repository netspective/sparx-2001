package com.xaf.task;

import java.lang.reflect.*;
import java.util.*;
import javax.servlet.*;
import java.security.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.value.*;

public class TaskContext extends ServletValueContext
{
	static public final long TCFLAG_HALTPROCESSING = 1;
	static public final long TCFLAG_HASERROR = TCFLAG_HALTPROCESSING * 2;
	static public final long TCFLAG_HASRESULTMSG = TCFLAG_HASERROR * 2;

	private static long taskContextNum = 0;
	private Object canvas;
	private int countOfTasksExecuted;
	private String transactionId;
	private DialogContext dialogContext;
	private long resultCode;
	private StringBuffer resultMessage = new StringBuffer();
	private StringBuffer errorMessage = new StringBuffer();
	private long flags;

	public TaskContext(ServletContext aContext, Servlet aServlet, ServletRequest aRequest, ServletResponse aResponse)
	{
        super(aContext, aServlet, aRequest, aResponse);
		taskContextNum++;

		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update((taskContextNum + new Date().toString()).getBytes());
			transactionId = md.digest().toString();
		}
		catch(NoSuchAlgorithmException e)
		{
			transactionId = "No MessageDigest Algorithm found!";
		}
	}

    public TaskContext(DialogContext dc)
    {
		this(dc.getServletContext(), dc.getServlet(), dc.getRequest(), dc.getResponse());
		dialogContext = dc;
    }

	public final Object getCanvas() { return canvas; }
	public final void setCanvas(Object value) { canvas = value; }

	public final String getTransactionId() { return transactionId; }

	public final DialogContext getDialogContext() { return dialogContext; }
	public final Dialog getDialog() { return dialogContext != null ? dialogContext.getDialog() : null; }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }

	public final boolean hasError() { return (flags & TCFLAG_HASERROR) != 0 ? true : false; }
	public final boolean hasResultMessage() { return (flags & TCFLAG_HASRESULTMSG) != 0 ? true : false; }
	public final boolean haltProcessing() { return (flags & TCFLAG_HALTPROCESSING) != 0 ? true : false; }

	public String getResultMessage() { return resultMessage.toString(); }
	public void addResultMessage(String value)
	{
		resultMessage.append(value);
		setFlag(TCFLAG_HASRESULTMSG);
	}

	public String getErrorMessage() { return errorMessage.toString(); }
	public void addErrorMessage(String value, boolean haltProcessing)
	{
		errorMessage.append(value);
		setFlag(TCFLAG_HASERROR);
		if(haltProcessing)
			setFlag(TCFLAG_HALTPROCESSING);
	}

	public long getResultCode() { return resultCode; }
	public void setResultCode(long value) { resultCode = value; }

    public int getCountOfTasksExecuted() { return countOfTasksExecuted; }

    public void registerTaskExecutionBegin(Task task)
    {
       countOfTasksExecuted++;
    }

    public void registerTaskExecutionEnd(Task task)
    {
    }
}