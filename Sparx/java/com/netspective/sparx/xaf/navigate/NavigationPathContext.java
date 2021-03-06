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
 * $Id: NavigationPathContext.java,v 1.9 2003-02-05 04:50:48 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.navigate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.xif.SchemaDocument;

public class NavigationPathContext extends ServletValueContext
{
    static public final long PCFLAG_HASERROR = 0;

    public class NavigationPathState {
        public NavigationPath path;
        public long flags;

        public NavigationPathState(NavigationPath path) {
            this.path = path;
            this.flags = path.getFlags();
            //TODO: See if it's necesary to have another constructor that takes a data-cmd like the FieldState
        }

        public final boolean flagIsSet(long flag)
        {
            return (flags & flag) != 0;
        }
    }


    private static int pageContextNum = 0;
    private NavigationTree ownerTree;
    private NavigationPath activeTree;
    private NavigationPathSkin skin;
    private NavigationPath.FindResults activePathFindResults;
    private String transactionId;
    private long resultCode;
    private StringBuffer errorMessage;
    private long flags;
    private boolean popup;
    private Map navigationStates = new HashMap();
    private int maxLevel = 0;
    private String rootUrl;

    public NavigationPathContext(NavigationTree ownerTree, ServletContext aContext, Servlet aServlet, ServletRequest aRequest, ServletResponse aResponse, NavigationPathSkin skin, String activePathId)
    {
        super(aContext, aServlet, aRequest, aResponse);

        pageContextNum++;
        this.ownerTree = ownerTree;
        this.skin = skin;
        activePathFindResults = ownerTree.findPath(activePathId);
        activeTree = activePathFindResults.getMatchedPath();
        maxLevel = ownerTree.getMaxLevel();
        rootUrl = ((HttpServletRequest) aRequest).getContextPath();

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

        createNavigationPathStates(ownerTree.getChildrenList());

        if(activeTree != null)
            activeTree.makeStateChanges(this);
    }

    public void createNavigationPathStates(List navPaths){

        if (navPaths == null) {
            return;
        }

        for (int i = 0; i < navPaths.size(); i++) {
            NavigationPath navPath = (NavigationPath) navPaths.get(i);
            navigationStates.put(navPath.getId(), new NavigationPathState(navPath));
            List children = navPath.getChildrenList();
            if(children != null)
                createNavigationPathStates(children);
        }
    }

    public String getApplicationName(NavigationPathContext nc)
    {
        String servletContextName = nc.getServletContext().getServletContextName();

        if (servletContextName != null && servletContextName.length() > 1) {
            return SchemaDocument.sqlIdentifierToText(nc.getServletContext().getServletContextName().substring(1), true);
        } else {
            return null;
        }
    }

    public final NavigationPath.FindResults getActivePathFindResults()
    {
        return activePathFindResults;
    }

    public final NavigationPath getActivePath()
    {
        return activeTree;
    }

    public final NavigationTree getOwnerTree()
    {
        return ownerTree;
    }

    public final NavigationPathSkin getSkin()
    {
        return skin;
    }

    public final String getRootUrl()
    {
        return rootUrl;
    }

    public final String getServletRootUrl()
    {
        return rootUrl + "/" + ((HttpServletRequest) getRequest()).getServletPath();
    }

    public int getMaxLevel()
    {
        return maxLevel;
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

    public Map getNavigationStates()
    {
        return navigationStates;
    }

    public void setNavigationStates(Map navigationStates)
    {
        this.navigationStates = navigationStates;
    }

    public void setFlag(String pathId, long flag)
    {
        NavigationPathState state = (NavigationPathState) navigationStates.get(pathId);
        if (state != null) {
            state.flags |= flag;
            //TODO: Need to see if we need to recurse into the children.  First thought: Not necesary.
        }
    }

    public void clearFlag(String pathId, long flag)
    {
        NavigationPathState state = (NavigationPathState) navigationStates.get(pathId);
        if(state != null)
        {
            state.flags &= ~flag;
            //TODO: Need to see if we need to recurse into the children.  First thought: Not necesary.
        }
    }

    public boolean flagIsSet(String pathId, long flag)
    {
        NavigationPathState state = (NavigationPathState) navigationStates.get(pathId);
        return state.flagIsSet(flag);
    }

    public boolean isPopup()
    {
        return popup;
    }

    public void setPopup(boolean popup)
    {
        this.popup = popup;
    }

}