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
 * $Id: LoginDialog.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace;

import java.io.IOException;
import java.io.Writer;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.skin.StandardDialogSkin;
import com.netspective.sparx.util.value.SingleValueSource;

public class LoginDialog extends com.netspective.sparx.xaf.security.LoginDialog
{
    private String LOGIN_ID_PROPERTY = com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "login.user-name";
    private String PASSWORD_PROPERTY = com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "login.user-password";
    protected StandardDialogSkin skin;

    public void initialize()
    {
        super.initialize();

        skin = new StandardDialogSkin();
        skin.setOuterTableAttrs("cellspacing='1' cellpadding='0'");
        skin.setInnerTableAttrs("cellspacing='0' cellpadding='4'");
        skin.setCaptionFontAttrs("size='2' face='tahoma,arial,helvetica' style='font-size:8pt' color='navy'");

        setHeading((SingleValueSource) null);
    }

    public DialogSkin getSkin()
    {
        return skin;
    }

    public void producePage(Writer writer, DialogContext dc) throws IOException
    {
        AppComponentsExplorerServlet servlet = (AppComponentsExplorerServlet) dc.getServlet();
        String sharedImagesRootURL = servlet.getSharedImagesRootURL();

        writer.write("<head>");
        writer.write("<title>Welcome to ACE</title>");
        writer.write("</head>");
        //writer.write("<body background='"+ sharedImagesRootURL +"/ace/2tone.gif'>");
        writer.write("<body background='white'>");
        writer.write("	<center><br>");
        //writer.write("		<img src='"+ sharedImagesRootURL +"/ace/login_splash.gif' width='473' height='132' border='0'>");
        writer.write("		<img src='" + sharedImagesRootURL + "/ace/login_splash.gif' width='351' height='335' border='0'>");
        //writer.write("		<table cellpadding='0' cellspacing='0' width='473' bgcolor='#003366'>");
        writer.write("		<table cellpadding='0' cellspacing='0' width='351'>");
        writer.write("			<tr>");
        writer.write("				<td valign='middle' align='center'>");
        renderHtml(writer, dc, true);
        writer.write("				</td>");
        writer.write("			</tr>");
        writer.write("		</table>");
        writer.write("	</center>");
        writer.write("</body>");
    }

    public boolean isValid(DialogContext dc)
    {
        if(!super.isValid(dc))
            return false;


        DialogField userIdField = dc.getDialog().findField("user_id");
        String user = dc.getValue(userIdField);
        DialogField passwordField = dc.getDialog().findField("password");
        String password = dc.getValue(passwordField);

        if(user == null || user.length() == 0)
        {
            userIdField.invalidate(dc, "Please enter a user ID");
            return false;
        }
        if(password == null || password.length() == 0)
        {
            passwordField.invalidate(dc, "Please enter a password");
            return false;
        }

        ConfigurationManager cfgMgr = ConfigurationManagerFactory.getManager(dc.getServletContext());
        Configuration appConfig = cfgMgr.getDefaultConfiguration();

        String aceLoginID = appConfig.getTextValue(dc, this.LOGIN_ID_PROPERTY);
        String acePassword = appConfig.getTextValue(dc, this.PASSWORD_PROPERTY);

        if(user.equals(aceLoginID))
        {
            if(!password.equals(acePassword))
            {
                passwordField.invalidate(dc, "Password is invalid");
                return false;
            }
        }
        else
        {
            userIdField.invalidate(dc, "User ID is invalid");
            return false;
        }
        // all checks were successful
        return true;
    }

    public String execute(DialogContext dc)
    {
        dc.getSession().setAttribute(Dialog.ENV_PARAMNAME, "ace");
        AuthenticatedUser user = createUserData(dc);
        applyAccessControls(dc, user);
        storeUserData(dc, user);
        return null;
    }
}