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
 * $Id: DialogFieldConditionalApplyFlag.java,v 1.2 2002-01-28 09:29:43 thua Exp $
 */

package com.netspective.sparx.xaf.form.conditional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.security.AccessControlList;
import com.netspective.sparx.xaf.security.AccessControlListFactory;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.security.LoginDialog;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.xaf.form.DialogFieldConditionalAction;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.field.SelectField;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DialogFieldConditionalApplyFlag extends DialogFieldConditionalAction
{
    private boolean clearFlag;
    private int dialogFieldFlag;
    private int dataCmd;
    private String[] hasPermissions;
    private String[] lackPermissions;
    private SingleValueSource valueSource;
    private SingleValueSource conditionalValueSource;

    public static Map dialogFieldNameValueMap = new HashMap();
    public static boolean flagNameMapIsAvail = false;

    public static void setupDialogFieldNameValueMap()
    {
        dialogFieldNameValueMap.put("invisible", new Integer(DialogField.FLDFLAG_INVISIBLE));
        dialogFieldNameValueMap.put("read-only", new Integer(DialogField.FLDFLAG_READONLY));
        dialogFieldNameValueMap.put("browser-read-only", new Integer(DialogField.FLDFLAG_BROWSER_READONLY));
        dialogFieldNameValueMap.put("hidden", new Integer(DialogField.FLDFLAG_INPUT_HIDDEN));
        dialogFieldNameValueMap.put("required", new Integer(DialogField.FLDFLAG_REQUIRED));
        flagNameMapIsAvail = true;
    }

    public DialogFieldConditionalApplyFlag()
    {
        super();
        this.hasPermissions = null;
        this.lackPermissions = null;
    }

    public DialogFieldConditionalApplyFlag(DialogField sourceField, int fieldFlag)
    {
        super(sourceField);
        setDialogFieldFlag(fieldFlag);
    }

    public int getDialogFieldFlag()
    {
        return dialogFieldFlag;
    }

    public void setDialogFieldFlag(int dialogFieldFlag)
    {
        this.dialogFieldFlag = dialogFieldFlag;
    }

    public boolean isClearFlag()
    {
        return clearFlag;
    }

    public void setClearFlag(boolean clearFlag)
    {
        this.clearFlag = clearFlag;
    }

    public boolean isPartnerRequired()
    {
        return false;
    }

    public boolean importFromXml(DialogField sourceField, Element elem, int conditionalItem)
    {
        if(!super.importFromXml(sourceField, elem, conditionalItem))
            return false;

        String flagName = elem.getAttribute("flag");
        if(!flagNameMapIsAvail) setupDialogFieldNameValueMap();
        Integer fieldFlagValue = (Integer) dialogFieldNameValueMap.get(flagName);
        //if(fieldFlagValue == null)
        //    sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'flag' attribute (" + flagName + ").");
        //else
        if(fieldFlagValue != null)
            setDialogFieldFlag(fieldFlagValue.intValue());

        clearFlag = elem.getAttribute("clear").equals("yes");

        String dataCmdStr = elem.getAttribute("data-cmd");
        if(dataCmdStr.length() > 0)
        {
            setDataCmd(dataCmdStr);
            if(dataCmd == DialogContext.DATA_CMD_NONE)
            {
                sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'data-cmd' attribute (" + dataCmdStr + ").");
                return false;
            }
        }

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

        String valueAvailStr = elem.getAttribute("has-value");
        if(valueAvailStr.length() == 0)
            valueAvailStr = elem.getAttribute("is-true");

        if(valueAvailStr.length() > 0)
            valueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueAvailStr);

        // if the action is "set-value" then look for the value to set the field to
        String valueStr = elem.getAttribute("value");
        if (valueStr.length() > 0)
            this.conditionalValueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueStr);
        return true;
    }

    public int getDataCmd()
    {
        return dataCmd;
    }

    public void setDataCmd(String dataCmdStr)
    {
        dataCmd = DialogContext.getDataCmdIdForCmdText(dataCmdStr);
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

    public void applyFlags(DialogContext dc)
    {
        boolean status = true;

        // the keep checking things until the status is set to false -- if it's false, we're going to just leave
        // and not do anything

        if(status && dataCmd != DialogContext.DATA_CMD_NONE)
            status = dc.matchesDataCmdCondition(dataCmd);

        boolean hasPermissionFlg = true;
        boolean lackPermissionFlg = false;
        if(status && (this.hasPermissions != null || this.lackPermissions != null))
        {
            String dialogEnv = (String) dc.getSession().getAttribute(Dialog.ENV_PARAMNAME);
            if(dialogEnv != null && dialogEnv.equals("ace"))
            {
                // if the dialog is being run in ACE, don't allow conditionals to be executed since
                // conditionals can contain permission checking which is dependent upon the application
                dc.addErrorMessage(getSourceField().getQualifiedName(),
                        "Conditionals using permission checking are not allowed to run in ACE since " +
                        "they are dependent on the application's security settings.");
                return;
            }

            HttpServletRequest request = (HttpServletRequest) dc.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            AccessControlList acl = AccessControlListFactory.getACL(dc.getServletContext());
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
            Object value = valueSource.getObjectValue(dc);
            if(value instanceof Boolean)
                status = ((Boolean) value).booleanValue();
            else
                status = value != null;
        }

        if(status && clearFlag)
        {
            dc.clearFlag(getSourceField().getQualifiedName(), dialogFieldFlag);
        }
        else if(status)
        {
            dc.setFlag(getSourceField().getQualifiedName(), dialogFieldFlag);
        }

        if (status && conditionalValueSource != null)
        {
            DialogField df = getSourceField();
            dc.setValue(df, valueSource.getObjectValue(dc).toString());
        }
    }
}