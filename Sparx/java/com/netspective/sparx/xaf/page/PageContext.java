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
 * $Id: PageContext.java,v 1.4 2002-11-25 14:56:29 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.page;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.log.LogManager;

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
        activePath = aServlet.getPagesPath().findPath(aServlet.getActivePathToFind(aRequest));

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((pageContextNum + new Date().toString()).getBytes());
            transactionId = md.digest().toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            transactionId = "No MessageDigest Algorithm found!";
            LogManager.recordException(this.getClass(), "constructor", transactionId, e);
        }
           }

    public PageContext(VirtualPath pagesPath, ServletContext aServletContext, Servlet aServlet , HttpServletRequest aRequest, HttpServletResponse aResponse, String NavTreeId)
    {
        super(aServletContext, aServlet, aRequest, aResponse);

        pageContextNum++;
        System.out.println("path to find:" + NavTreeId );
        activePath = pagesPath.findPath(NavTreeId);

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((pageContextNum + new Date().toString()).getBytes());
            transactionId = md.digest().toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            transactionId = "No MessageDigest Algorithm found!";
            LogManager.recordException(this.getClass(), "constructor", transactionId, e);
        }
    }

    public final VirtualPath.FindResults getActivePath()
    {
        return activePath;
    }

    public final String getTransactionId()
    {
        return transactionId;
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
        return (flags & PCFLAG_HASERROR) != 0 ? true : false;
    }

    public String getErrorMessage()
    {
        return errorMessage.toString();
    }

    public void addErrorMessage(String value, boolean haltProcessing)
    {
        errorMessage.append(value);
        setFlag(PCFLAG_HASERROR);
    }

    public long getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(long value)
    {
        resultCode = value;
    }
}