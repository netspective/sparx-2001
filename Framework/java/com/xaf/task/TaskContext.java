package com.xaf.task;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.value.*;

public class TaskContext implements ValueContext
{
	static public final long TCFLAG_HALTPROCESSING = 1;
	static public final long TCFLAG_HASERROR = TCFLAG_HALTPROCESSING * 2;
	static public final long TCFLAG_HASRESULTMSG = TCFLAG_HASERROR * 2;

	private static long taskContextNum = 0;
	private Task task;
	private String transactionId;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;
	private DialogContext dialogContext;
	private long resultCode;
	private StringBuffer resultMessage = new StringBuffer();
	private StringBuffer errorMessage = new StringBuffer();
	private long flags;

	public TaskContext(HttpServletRequest aRequest, HttpServletResponse aResponse, ServletContext aContext)
	{
		taskContextNum++;
		request = aRequest;
		response = aResponse;
		servletContext = aContext;

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
		this(dc.getRequest(), dc.getResponse(), dc.getServletContext());
		dialogContext = dc;
    }

	public final String getTransactionId() { return transactionId; }

	public final DialogContext getDialogContext() { return dialogContext; }
	public final Dialog getDialog() { return dialogContext != null ? dialogContext.getDialog() : null; }

	public final ServletContext getServletContext() { return servletContext; }
	public final HttpServletRequest getRequest() { return request; }
	public final HttpServletResponse getResponse() { return response; }

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
}