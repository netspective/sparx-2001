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
 * $Id: NavigationConditionalApplyFlag.java,v 1.1 2003-01-26 21:32:18 roque.hernandez Exp $
 */


package com.netspective.sparx.xaf.navigate;

import org.w3c.dom.Element;

import java.util.*;

import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.security.LoginDialog;
import com.netspective.sparx.xaf.security.AccessControlList;
import com.netspective.sparx.xaf.security.AccessControlListFactory;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.SingleValueSource;

import javax.servlet.http.HttpServletRequest;

public class NavigationConditionalApplyFlag extends NavigationConditionalAction {
    public static Map dialogFieldNameValueMap = new HashMap();

    static
    {
        dialogFieldNameValueMap.put("invisible", new Long(NavigationPath.NAVGPATHFLAG_INVISIBLE));
        dialogFieldNameValueMap.put("hidden", new Long(NavigationPath.NAVGPATHFLAG_HIDDEN));
        dialogFieldNameValueMap.put("read-only", new Long(NavigationPath.NAVGPATHFLAG_READONLY));
        dialogFieldNameValueMap.put("initial-focus", new Long(NavigationPath.NAVGPATHFLAG_INITIAL_FOCUS));
    }

    public NavigationConditionalApplyFlag() {
    }

    public NavigationConditionalApplyFlag(NavigationPath path) {
        super(path);
    }

    private boolean clearFlag;
    private long navigationPathFlag;
    private int dataCmd;
    private String[] hasPermissions;
    private String[] lackPermissions;
    private SingleValueSource valueSource;
    private SingleValueSource conditionalValueSource;

    public void importFromXml(Element elem) {

        super.importFromXml(elem);

        String flagName = elem.getAttribute("flag");
        Long fieldFlagValue = (Long) dialogFieldNameValueMap.get(flagName);

        if(fieldFlagValue != null)
            setNavigationPathFlag(fieldFlagValue.longValue());

        clearFlag = elem.getAttribute("clear").equals("yes");

        String permissionsStr = elem.getAttribute("has-permission");
        if(permissionsStr.length() > 0)
        {
            List permsList = new ArrayList();
            StringTokenizer st = new StringTokenizer(permissionsStr, ",");
            while(st.hasMoreTokens())
                permsList.add(st.nextToken());
            this.setHasPermissions((String[]) permsList.toArray(new String[permsList.size()]));
        }

        permissionsStr = elem.getAttribute("lack-permission");
        if(permissionsStr.length() > 0)
        {
            List permsList = new ArrayList();
            StringTokenizer st = new StringTokenizer(permissionsStr, ",");
            while(st.hasMoreTokens())
                permsList.add(st.nextToken());
            this.setLackPermissions((String[]) permsList.toArray(new String[permsList.size()]));
        }

        String dataCmdStr = elem.getAttribute("data-cmd");
        if(dataCmdStr.length() > 0)
        {
            setDataCmd(dataCmdStr);
            if(dataCmd == DialogContext.DATA_CMD_NONE)
            {
                //sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'data-cmd' attribute (" + dataCmdStr + ").");
            }
        }

        String valueAvailStr = elem.getAttribute("has-value");
        if(valueAvailStr.length() == 0)
            valueAvailStr = elem.getAttribute("is-true");

        if(valueAvailStr.length() > 0)
            valueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueAvailStr);
    }


        public String[] getHasPermissions()
    {
        return hasPermissions;
    }

    public void setHasPermissions(String[] permissions)
    {
        this.hasPermissions = permissions;
    }

    public String[] getLackPermissions()
    {
        return lackPermissions;
    }

    public void setLackPermissions(String[] lackPermissions)
    {
        this.lackPermissions = lackPermissions;
    }

    public long getNavigationPathFlag()
    {
        return navigationPathFlag;
    }

    public void setNavigationPathFlag(long navigationPathFlag)
    {
        this.navigationPathFlag = navigationPathFlag;
    }

    public boolean isClearFlag()
    {
        return clearFlag;
    }

    public void setClearFlag(boolean clearFlag)
    {
        this.clearFlag = clearFlag;
    }

     public int getDataCmd()
    {
        return dataCmd;
    }

    public void setDataCmd(int dataCmd)
    {
        this.dataCmd = dataCmd;
    }

    public void setDataCmd(String dataCmdStr)
    {
        //TODO: See if we should change this to refer to the NavigationContext instead, but this will work.
        dataCmd = DialogContext.getDataCmdIdForCmdText(dataCmdStr);
    }

    public void applyFlags(NavigationPathContext nc)
    {
        boolean status = true;

        // the keep checking things until the status is set to false -- if it's false, we're going to just leave
        // and not do anything
        /*  TODO: How do we get a hold of the dataCmd for the current request?  When we figure out, then need to update next lines.
        if(status && dataCmd != DialogContext.DATA_CMD_NONE)
            status = nc.matchesDataCmdCondition(dataCmd);
        */
        boolean hasPermissionFlg = true;
        boolean lackPermissionFlg = false;
        
        if(status && (this.hasPermissions != null || this.lackPermissions != null))
        {
            HttpServletRequest request = (HttpServletRequest) nc.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            AccessControlList acl = AccessControlListFactory.getACL(nc.getServletContext());
            
            for (int i = 0; i < user.getUserRoles().length; i++) {
                String s = user.getUserRoles()[i];
            }
            if(this.hasPermissions != null)
                hasPermissionFlg = user.hasAnyPermission(acl, this.hasPermissions);
            if(this.lackPermissions != null)
                lackPermissionFlg = user.hasAnyPermission(acl, this.lackPermissions);

            // set 'status' to true only if the user lacks certain permissions and
            // has certain permissions
            if(lackPermissionFlg == false && hasPermissionFlg == true)
                status = true;
            else
                status = false;
        }

        if(status && valueSource != null)
        {
            Object value = valueSource.getObjectValue(nc);
            if(value instanceof Boolean)
                status = ((Boolean) value).booleanValue();
            else
                status = value != null;
        }

        if(status && clearFlag)
        {
            nc.clearFlag(getPath().getId(), navigationPathFlag);
        }
        else if(status)
        {
            nc.setFlag(getPath().getId(), navigationPathFlag);
        }
    }
}
