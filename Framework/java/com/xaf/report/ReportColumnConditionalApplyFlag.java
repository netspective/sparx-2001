package com.xaf.report;

import org.w3c.dom.Element;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueSourceFactory;
import com.xaf.form.Dialog;
import com.xaf.security.LoginDialog;
import com.xaf.security.AuthenticatedUser;
import com.xaf.security.AccessControlList;
import com.xaf.security.AccessControlListFactory;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class ReportColumnConditionalApplyFlag implements ReportColumnConditionalState
{
    private boolean clearFlag;
    private int reportColumnFlag;
    private String[] hasPermissions;
    private String[] lackPermissions;
    private SingleValueSource valueSource;

    public static Map flagValueMap = new HashMap();

    static
    {
        flagValueMap.put("hidden", new Long(ReportColumn.COLFLAG_HIDDEN));
    }

    public ReportColumnConditionalApplyFlag()
    {
		super();
        this.hasPermissions = null;
        this.lackPermissions = null;
    }

    public ReportColumnConditionalApplyFlag(int fieldFlag)
    {
		this();
        setReportColumnFlag(fieldFlag);
    }

    public int getReportColumnFlag()
    {
        return reportColumnFlag;
    }

    public void setReportColumnFlag(int reportColumnFlag)
    {
        this.reportColumnFlag = reportColumnFlag;
    }

    public boolean isClearFlag()
    {
        return clearFlag;
    }

    public void setClearFlag(boolean clearFlag)
    {
        this.clearFlag = clearFlag;
    }

    public boolean importFromXml(ReportColumn column, Element elem, int conditionalItem)
    {
        String flagName = elem.getAttribute("flag");
        Long flagValue = (Long) flagValueMap.get(flagName);
        if(flagValue == null)
            throw new RuntimeException("ReportColumn Conditional " + conditionalItem + " has has an invalid 'flag' attribute ("+ flagName +").");

        setReportColumnFlag(flagValue.intValue());

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

        String valueAvailStr = elem.getAttribute("has-value");
        if(valueAvailStr.length() == 0)
            valueAvailStr = elem.getAttribute("is-true");

        if(valueAvailStr.length() > 0)
            valueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueAvailStr);

        return true;
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

    public void makeStateChanges(ReportContext rc, ReportContext.ColumnState state)
    {
        // keep checking things until the status is set to false -- if it's false, we're going to just leave
        // and not do anything

        boolean status = true;

        if(status && (this.hasPermissions != null || this.lackPermissions != null))
        {
            String env = (String) rc.getSession().getAttribute(Dialog.ENV_PARAMNAME);
            if (env != null && env.equals("ace"))
            {
                // if the dialog is being run in ACE, don't allow conditionals to be executed since
                // conditionals can contain permission checking which is dependent upon the application
                state.setOutputFormat("Conditionals using permission checking are not allowed to run in ACE since " +
                    "they are dependent on the application's security settings.");
                return;
            }

            HttpServletRequest request = (HttpServletRequest) rc.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            AccessControlList acl = AccessControlListFactory.getACL(rc.getServletContext());
            if (this.hasPermissions != null)
                status = user.hasAnyPermission(acl, this.hasPermissions);
            if (status && this.lackPermissions != null)
                status = ! (user.hasAnyPermission(acl, this.lackPermissions));
        }

        if(status && valueSource != null)
        {
            Object value = valueSource.getObjectValue(rc);
            if(value instanceof Boolean)
                status = ((Boolean) value).booleanValue();
            else
                status = value != null;
        }

        if(status && clearFlag)
            state.clearFlag(reportColumnFlag);
        else if(status)
            state.setFlag(reportColumnFlag);
    }
}