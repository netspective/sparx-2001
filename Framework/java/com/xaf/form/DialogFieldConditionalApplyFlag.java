package com.xaf.form;

import org.w3c.dom.Element;

import java.util.*;

import com.xaf.security.AuthenticatedUser;
import com.xaf.security.LoginDialog;
import com.xaf.security.AccessControlListFactory;
import com.xaf.security.AccessControlList;
import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueSourceFactory;

import javax.servlet.http.HttpServletRequest;

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
        if(! super.importFromXml(sourceField, elem, conditionalItem))
            return false;

        String flagName = elem.getAttribute("flag");
        if(! flagNameMapIsAvail) setupDialogFieldNameValueMap();
        Integer fieldFlagValue = (Integer) dialogFieldNameValueMap.get(flagName);
        if(fieldFlagValue == null)
            sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'flag' attribute ("+ flagName +").");
        else
            setDialogFieldFlag(fieldFlagValue.intValue());

        clearFlag = elem.getAttribute("clear").equals("yes");

        String dataCmdStr = elem.getAttribute("data-cmd");
		if(dataCmdStr.length() > 0)
        {
            setDataCmd(dataCmdStr);
            if(dataCmd == DialogContext.DATA_CMD_NONE)
            {
                sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'data-cmd' attribute ("+ dataCmdStr +").");
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
            HttpServletRequest request = (HttpServletRequest) dc.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            AccessControlList acl = AccessControlListFactory.getACL(dc.getServletContext());
            if (this.hasPermissions != null)
                hasPermissionFlg = user.hasAnyPermission(acl, this.hasPermissions);
            if (this.lackPermissions != null)
                lackPermissionFlg = user.hasAnyPermission(acl, this.lackPermissions);

            // set 'status' to true only if the user lacks certain permissions and
            // has certain permissions
            if (lackPermissionFlg == false && hasPermissionFlg == true)
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
            dc.clearFlag(getSourceField().getQualifiedName(), dialogFieldFlag);
        else if(status)
            dc.setFlag(getSourceField().getQualifiedName(), dialogFieldFlag);
    }
}