/*
 * Interface: com.netspective.sparx.xaf.sql.DmlStatementConditionalState
 * Created on Feb 4, 2002 2:11:25 AM
 * @author Aye Thu
 * @version
 */
package com.netspective.sparx.xaf.sql;

import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.security.LoginDialog;
import com.netspective.sparx.xaf.security.AccessControlList;
import com.netspective.sparx.xaf.security.AccessControlListFactory;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.SingleValueSource;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DmlStatementConditionalAction
{
    private String[] hasPermissions;
    private String[] lackPermissions;
    private SingleValueSource valueSource;

    public String[] getHasPermissions()
    {
        return hasPermissions;
    }

    public void setHasPermissions(String[] hasPermissions)
    {
        this.hasPermissions = hasPermissions;
    }

    public String[] getLackPermissions()
    {
        return lackPermissions;
    }

    public void setLackPermissions(String[] lackPermissions)
    {
        this.lackPermissions = lackPermissions;
    }

    public boolean importFromXml(Element elem)
    {
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

    public boolean checkCondtionals(TaskContext tc)
    {
        // keep checking things until the status is set to false -- if it's false, we're going to just leave
        // and not do anything
        boolean status = true;

        if(status && (this.hasPermissions != null || this.lackPermissions != null))
        {
            //String env = (String) tc.getSession().getAttribute(Dialog.ENV_PARAMNAME);
            //if(env != null && env.equals("ace"))
            //{
                // if the dialog is being run in ACE, don't allow conditionals to be executed since
                // conditionals can contain permission checking which is dependent upon the application
                //state.setOutputFormat("Conditionals using permission checking are not allowed to run in ACE since " +
                //        "they are dependent on the application's security settings.");
                //return;
           // }

            HttpServletRequest request = (HttpServletRequest) tc.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            AccessControlList acl = AccessControlListFactory.getACL(tc.getServletContext());
            if(this.hasPermissions != null)
                status = user.hasAnyPermission(acl, this.hasPermissions);
            if(status && this.lackPermissions != null)
                status = !(user.hasAnyPermission(acl, this.lackPermissions));
        }
        // checking for 'has-value' attribute
        if(status && valueSource != null)
        {
            String value = valueSource.getValue(tc);
            if (value != null && value.length() > 0)
                status = true;
            else
                status = false;
        }

        return status;
    }

}
