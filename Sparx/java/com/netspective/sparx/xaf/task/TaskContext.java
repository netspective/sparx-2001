/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: TaskContext.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.util.value.ServletValueContext;

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

    public final Object getCanvas()
    {
        return canvas;
    }

    public final void setCanvas(Object value)
    {
        canvas = value;
    }

    public final String getTransactionId()
    {
        return transactionId;
    }

    public final DialogContext getDialogContext()
    {
        return dialogContext;
    }

    public final Dialog getDialog()
    {
        return dialogContext != null ? dialogContext.getDialog() : null;
    }

    public final long getFlags()
    {
        return flags;
    }

    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    public final void setFlag(long flag)
    {
        flags |= flag;
    }

    public final void clearFlag(long flag)
    {
        flags &= ~flag;
    }

    public final boolean hasError()
    {
        return (flags & TCFLAG_HASERROR) != 0 ? true : false;
    }

    public final boolean hasResultMessage()
    {
        return (flags & TCFLAG_HASRESULTMSG) != 0 ? true : false;
    }

    public final boolean haltProcessing()
    {
        return (flags & TCFLAG_HALTPROCESSING) != 0 ? true : false;
    }

    public String getResultMessage()
    {
        return resultMessage.toString();
    }

    public void addResultMessage(String value)
    {
        resultMessage.append(value);
        setFlag(TCFLAG_HASRESULTMSG);
    }

    public String getErrorMessage()
    {
        return errorMessage.toString();
    }

    public void addErrorMessage(String value, boolean haltProcessing)
    {
        errorMessage.append(value);
        setFlag(TCFLAG_HASERROR);
        if(haltProcessing)
            setFlag(TCFLAG_HALTPROCESSING);
    }

    public long getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(long value)
    {
        resultCode = value;
    }

    public int getCountOfTasksExecuted()
    {
        return countOfTasksExecuted;
    }

    public void registerTaskExecutionBegin(Task task)
    {
        countOfTasksExecuted++;
    }

    public void registerTaskExecutionEnd(Task task)
    {
    }
}