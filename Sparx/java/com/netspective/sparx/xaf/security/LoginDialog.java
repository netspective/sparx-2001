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
 * $Id: LoginDialog.java,v 1.6 2003-02-24 03:46:04 aye.thu Exp $
 */

package com.netspective.sparx.xaf.security;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.Map;
import java.util.Iterator;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.netspective.sparx.util.log.AppServerLogger;
import com.netspective.sparx.util.log.LogManager;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.querydefn.QuerySelectScrollState;
import com.netspective.sparx.xaf.querydefn.QueryBuilderDialog;
import com.netspective.sparx.xaf.theme.ThemeFactory;
import com.netspective.sparx.xaf.theme.Theme;
import com.netspective.sparx.xaf.theme.ThemeStyle;
import com.netspective.sparx.util.value.ValueContext;

public class LoginDialog extends Dialog
{
    static public final String DEFAULT_COOKIENAME_USERID = "xaf_user_id_01";
    static public final String DEFAULT_ATTRNAME_USERINFO = "authenticated-user";

    private TextField userIdField;
    private TextField passwordField;
    private String loginImageSrc;
    private String userNameCookieName;
    private String userInfoSessionAttrName;
    private Theme theme;

    public LoginDialog()
    {
        super();

        userNameCookieName = DEFAULT_COOKIENAME_USERID;
        userInfoSessionAttrName = DEFAULT_ATTRNAME_USERINFO;

        setRetainAllRequestParams(true);
    }

    public DialogSkin getSkin()
    {
        return SkinFactory.getDialogSkin();
    }

    public TextField getUserIdField()
    {
        return userIdField;
    }

    public void setUserIdField(TextField userIdField)
    {
        this.userIdField = userIdField;
    }

    public TextField getPasswordField()
    {
        return passwordField;
    }

    public void setPasswordField(TextField passwordField)
    {
        this.passwordField = passwordField;
    }

    public String getLoginImageSrc()
    {
        return loginImageSrc;
    }

    public void setLoginImageSrc(String loginImageSrc)
    {
        this.loginImageSrc = loginImageSrc;
    }

    public TextField createUserIdField()
    {
        TextField result = new TextField("user_id", "User ID");
        result.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);
        result.setFlag(DialogField.FLDFLAG_INITIAL_FOCUS);
        return result;
    }

    public TextField createPasswordField()
    {
        TextField result = new TextField("password", "Password");
        result.setFlag(DialogField.FLDFLAG_REQUIRED | TextField.FLDFLAG_MASKENTRY);
        return result;
    }

    /**
     * Allow additional fields to be added after the user id and password fields.
     */
    public void customInitialize()
    {
    }

    /**
     * Add the user id and password fields and provide custom initialization.
     */
    public void initialize()
    {
        this.setHeading("Please Login");
        this.setName("login");

        setUserIdField(createUserIdField());
        setPasswordField(createPasswordField());

        addField(userIdField);
        addField(passwordField);

        customInitialize();

        addField(new DialogDirector());
    }

    public String getUserNameCookieName()
    {
        return userNameCookieName;
    }

    public void setUserNameCookieName(String value)
    {
        userNameCookieName = value;
    }

    public String getUserInfoSessionAttrName()
    {
        return userInfoSessionAttrName;
    }

    public void setUserInfoSessionAttrName(String value)
    {
        userInfoSessionAttrName = value;
    }

    public String getImageSrc()
    {
        return loginImageSrc;
    }

    public void setImageSrc(String value)
    {
        loginImageSrc = value;
    }

    public boolean accessAllowed(ServletContext context, HttpServletRequest request, HttpServletResponse response)
    {
        return request.getSession(true).getAttribute(userInfoSessionAttrName) != null;
    }

    public void producePage(Writer writer, DialogContext dc) throws IOException
    {
        // associate a theme with this context
        ThemeFactory tf = ThemeFactory.getInstance(dc);
        theme = tf.getCurrentTheme();
        if (theme != null)
        {
            // get all the CSS files associated with this theme/style combination
            ThemeStyle style = theme.getCurrentStyle();
            String imgPath = style.getImagePath();
            Map cssResources = style.getCssResources();
            Iterator it = cssResources.values().iterator();
            writer.write("<html>\n");
            writer.write("<head>\n");
            while (it.hasNext())
            {
                String css = (String) it.next();
                writer.write("	<link rel=\"stylesheet\" href=\"" + ((HttpServletRequest)dc.getRequest()).getContextPath() +
                        css + "\" type=\"text/css\">\n");
            }
            writer.write("</head>\n");
            writer.write("<body bgcolor=\"#ffffff\" marginheight=\"100\" topmargin=\"100\">");
            writer.write("    <div align=\"center\">");
            renderHtml(writer, dc, true);
                    /*
            writer.write("                                            <table class=\"dialog-pattern\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
            writer.write("                                                <tr height=\"30\">");
            writer.write("                                                    <td class=\"dialog-fields\" valign=\"middle\" nowrap width=\"50%\" height=\"30\">");
            writer.write("                                                        <div align=\"right\">");
            writer.write("                                                            <span class=\"report-fields\"><b>User Name</b></span></div>");
            writer.write("                                                    </td>");
            writer.write("                                                    <td class=\"dialog-entry\" align=\"left\" nowrap width=\"50%\" height=\"30\"><input class=\"dialog-input-required\" type=\"text\" name=\"textfieldName\" size=\"24\" border=\"0\"></td>");
            writer.write("                                                </tr>");
            writer.write("                                                <tr height=\"30\">");
            writer.write("                                                    <td class=\"dialog-fields\" valign=\"middle\" nowrap width=\"50%\" height=\"30\">");
            writer.write("                                                        <div align=\"right\">");
            writer.write("                                                            <span class=\"report-fields\"><b>Password</b></span></div>");
            writer.write("                                                    </td>");
            writer.write("                                                    <td class=\"dialog-entry\" align=\"left\" nowrap width=\"50%\" height=\"30\"><input class=\"dialog-input-required\" type=\"text\" name=\"textfieldName\" size=\"24\" border=\"0\"></td>");
            writer.write("                                                </tr>");
            writer.write("                                                <tr>");
            writer.write("                                                    <td nowrap></td>");
            writer.write("                                                    <td class=\"dialog-button-table\" width=\"100%\" nowrap><a class=\"dialog-button\" href=\"#\">Enter</a>&nbsp;&nbsp;<a class=\"dialog-button\" href=\"#\">Reset</a></td>");
            writer.write("                                                </tr>");
            writer.write("                                            </table>");
            */
            writer.write("    </div>");
            writer.write("</body>");
        }
    }

    public AuthenticatedUser getActiveUser(ValueContext vc)
    {
        return (AuthenticatedUser) ((HttpServletRequest) vc.getRequest()).getSession(true).getAttribute(userInfoSessionAttrName);
    }

    public AuthenticatedUser createUserData(DialogContext dc)
    {
        return new BasicAuthenticatedUser(dc.getValue(userIdField), dc.getValue(userIdField));
    }

    public void applyAccessControls(DialogContext dc, AuthenticatedUser user)
    {
        AccessControlList acl = AccessControlListFactory.getACL(dc.getServletContext());
        user.setRoles(acl, new String[]{"/role/super-user"});
    }

    public void storeUserData(DialogContext dc, AuthenticatedUser user)
    {
        HttpServletRequest req = (HttpServletRequest) dc.getRequest();
        req.getSession(true).setAttribute(userInfoSessionAttrName, user);

        Cookie cookie = new Cookie(userNameCookieName, dc.getValue(userIdField));
        cookie.setPath("/");
        ((HttpServletResponse) dc.getResponse()).addCookie(cookie);

        AppServerLogger cat = (AppServerLogger) AppServerLogger.getLogger(LogManager.MONITOR_SECURITY);
        if(cat.isInfoEnabled())
        {
            String userId = user.getUserId();
            StringBuffer info = new StringBuffer();
            info.append("login");
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(userId);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(user.getUserOrgId());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteUser());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteHost());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteAddr());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            BitSet perms = user.getUserPermissions();
            info.append(perms != null ? user.getUserPermissions().toString() : "{}");
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            String[] roles = user.getUserRoles();
            if(roles != null)
            {
                for(int r = 0; r < roles.length; r++)
                {
                    if(r > 0)
                        info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
                    info.append(roles[r]);
                }
            }
            cat.info(info);
        }

        cat = (AppServerLogger) AppServerLogger.getLogger(LogManager.DEBUG_SECURITY);
        if(cat.isDebugEnabled())
        {
            String userId = user.getUserId();
            cat.debug("User '" + userId + "' (" + user.getUserName() + ") is now authenticated for Session ID '" + req.getSession(true).getId() + "'");

            BitSet perms = user.getUserPermissions();
            if(perms != null)
                cat.debug("User '" + userId + "' has permissions " + user.getUserPermissions().toString());
            else
                cat.debug("User '" + userId + " has no permissions.");

            String[] roles = user.getUserRoles();
            if(roles != null)
            {
                for(int r = 0; r < roles.length; r++)
                    cat.debug("User '" + userId + "' has role " + roles[r]);
            }
            else
                cat.debug("User '" + userId + " has no roles.");
        }
    }

    public void clearUserData(ValueContext vc)
    {
        HttpServletRequest req = (HttpServletRequest) vc.getRequest();
        HttpSession session = req.getSession();
        AuthenticatedUser user = (AuthenticatedUser) req.getSession(true).getAttribute(userInfoSessionAttrName);

        AppServerLogger cat = (AppServerLogger) AppServerLogger.getLogger(LogManager.MONITOR_SECURITY);
        if(user != null && cat.isInfoEnabled())
        {
            String userId = user.getUserId();
            StringBuffer info = new StringBuffer();
            info.append("logout");
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(userId);
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(user.getUserOrgId());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteUser());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteHost());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            info.append(req.getRemoteAddr());
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            BitSet perms = user.getUserPermissions();
            info.append(perms != null ? user.getUserPermissions().toString() : "{}");
            info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
            String[] roles = user.getUserRoles();
            if(roles != null)
            {
                for(int r = 0; r < roles.length; r++)
                {
                    if(r > 0)
                        info.append(LogManager.MONITOR_ENTRY_FIELD_SEPARATOR);
                    info.append(roles[r]);
                }
            }
            cat.info(info);
        }
        try
        {
            // clean up any connections still held open by query select dialogs
            QuerySelectScrollState activeState = (QuerySelectScrollState) session.getAttribute(QueryBuilderDialog.QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);
            if(activeState != null)
            {
                activeState.close();
                session.removeAttribute(QueryBuilderDialog.QBDIALOG_ACTIVE_QSSS_SESSION_ATTR_NAME);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        req.getSession(true).removeAttribute(userInfoSessionAttrName);
        Cookie cookie = new Cookie(userNameCookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        ((HttpServletResponse) vc.getResponse()).addCookie(cookie);
    }

    public String execute(DialogContext dc)
    {
        dc.getSession().setAttribute(Dialog.ENV_PARAMNAME, "app");
        AuthenticatedUser user = createUserData(dc);
        applyAccessControls(dc, user);
        storeUserData(dc, user);
        return null;
    }

    public void logout(ValueContext vc)
    {
        clearUserData(vc);
    }
}