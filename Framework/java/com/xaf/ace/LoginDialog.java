package com.xaf.ace;

import java.io.*;
import java.util.*;

import com.xaf.form.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;
import com.xaf.config.ConfigurationManager;
import com.xaf.config.ConfigurationManagerFactory;
import com.xaf.config.Configuration;

public class LoginDialog extends com.xaf.security.LoginDialog
{
    private String LOGIN_ID_PROPERTY = "framework.ace.login.user-name";
    private String PASSWORD_PROPERTY = "framework.ace.login.user-password";
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

	public void producePage(DialogContext dc, Writer writer) throws IOException
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
		writer.write("		<img src='"+ sharedImagesRootURL +"/ace/login_splash.gif' width='351' height='335' border='0'>");
		//writer.write("		<table cellpadding='0' cellspacing='0' width='473' bgcolor='#003366'>");
		writer.write("		<table cellpadding='0' cellspacing='0' width='351'>");
		writer.write("			<tr>");
		writer.write("				<td valign='middle' align='center'>");
		writer.write(               getHtml(dc, true));
		writer.write("				</td>");
		writer.write("			</tr>");
		writer.write("		</table>");
		writer.write("	</center>");
		writer.write("</body>");
	}

    public boolean isValid(DialogContext dc)
    {
		if(! super.isValid(dc))
			return false;


        DialogField userIdField = dc.getDialog().findField("user_id");
        String user = dc.getValue(userIdField);
        DialogField passwordField = dc.getDialog().findField("password");
        String password = dc.getValue(passwordField);

        if (user == null || user.length() == 0)
        {
            userIdField.invalidate(dc, "Please enter a user ID");
            return false;
        }
        if (password == null || password.length() == 0)
        {
            passwordField.invalidate(dc, "Please enter a password");
            return false;
        }

        ConfigurationManager cfgMgr = ConfigurationManagerFactory.getManager(dc.getServletContext());
        Configuration appConfig = cfgMgr.getDefaultConfiguration();

        String aceLoginID = appConfig.getValue(dc, this.LOGIN_ID_PROPERTY);
        String acePassword = appConfig.getValue(dc, this.PASSWORD_PROPERTY);

        if (user.equals(aceLoginID))
        {
            if (!password.equals(acePassword))
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
}