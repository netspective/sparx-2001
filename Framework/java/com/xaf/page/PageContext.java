package com.xaf.page;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.*;

import com.xaf.value.*;

public class PageContext extends ServletValueContext
{
	static public final long PCFLAG_HASERROR = 0;

	private static int pageContextNum = 0;
	private VirtualPath.FindResults activePath;
	private String transactionId;
	private long resultCode;
	private StringBuffer errorMessage;
	private long flags;

	public PageContext(PageControllerServlet aServlet, HttpServletRequest aRequest, HttpServletResponse aResponse)
	{
        super(aServlet.getServletContext(), aServlet, aRequest, aResponse);

		pageContextNum++;
		activePath = aServlet.getPagesPath().findPath(aRequest.getPathInfo());

		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update((pageContextNum + new Date().toString()).getBytes());
			transactionId = md.digest().toString();
		}
		catch(NoSuchAlgorithmException e)
		{
			transactionId = "No MessageDigest Algorithm found!";
		}
	}

	public final VirtualPath.FindResults getActivePath() { return activePath; }
	public final String getTransactionId() { return transactionId; }

	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag) { flags |= flag; }
	public final void clearFlag(long flag) { flags &= ~flag; }

	public final boolean hasError() { return (flags & PCFLAG_HASERROR) != 0 ? true : false; }

	public String getErrorMessage() { return errorMessage.toString(); }
	public void addErrorMessage(String value, boolean haltProcessing)
	{
		errorMessage.append(value);
		setFlag(PCFLAG_HASERROR);
	}

	public long getResultCode() { return resultCode; }
	public void setResultCode(long value) { resultCode = value; }
}