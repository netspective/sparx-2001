package app.security;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.http.*;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.security.*;
import com.netspective.sparx.xaf.skin.*;
import com.netspective.sparx.xaf.sql.*;
import com.netspective.sparx.util.value.*;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.Configuration;

public class AppLoginDialog extends LoginDialog
{
	protected DialogSkin skin;

	public void initialize()
	{
		super.initialize();

		skin = SkinFactory.getDialogSkin("login-skin");
		setHeading((SingleValueSource) null);
	}

	public DialogSkin getSkin()
	{
		return skin;
	}

    public void producePage(Writer writer, DialogContext dc) throws IOException
	{
		String resourcesUrl = ((HttpServletRequest) dc.getRequest()).getContextPath() + "/resources";

		writer.write("<head>");
		writer.write("<title>Welcome to CURA</title>");
		writer.write("</head>");
		writer.write("<body background='white'>");
		writer.write("	<center><br>");
        writer.write("  <table width='400' cellpadding='0' cellspacing='0'>");
        writer.write("  <tr><td align='center' ><img src='"+ resourcesUrl +"/images/design/logo-main.gif' border='0'></td</tr>");
		writer.write("	<tr><td align='center' >");
        renderHtml(writer, dc, true);
        writer.write("  </td></tr>");
        writer.write("	<tr><td align='center'>&nbsp;");
        writer.write("  </td></tr>");
        writer.write("  </table>");
		writer.write("	</center>");
		writer.write("</body>");
	}

    public boolean isValid(DialogContext dc)
    {
		if(! super.isValid(dc))
			return false;

		try
		{

			String userIdProvided = dc.getValue("user_id");

			DatabaseContext dbc = DatabaseContextFactory.getContext(dc);
			StatementManager stmtMgr = StatementManagerFactory.getManager(dc.getServletContext());

			StatementManager.ResultInfo ri = stmtMgr.execute(dbc, dc, null, "security.login-info", new Object[] { userIdProvided });
			Object[] loginInfo = stmtMgr.getResultSetSingleRowAsArray(ri.getResultSet());
			ri.close();

			if(loginInfo == null)
			{
				DialogField userIdField = dc.getDialog().findField("user_id");
				userIdField.invalidate(dc, "User id '"+ userIdProvided +"' is not valid.");
				return false;
			}

			String personId = loginInfo[0].toString();
			String password = loginInfo[1].toString();
			String passwordProvided = dc.getValue("password");

			if(! passwordProvided.equals(password))
			{
				DialogField passwordField = dc.getDialog().findField("password");
				passwordField.invalidate(dc, "Password is not valid.");
				return false;
			}

			dc.getRequest().setAttribute("user-person-id", personId);
		}
		catch(Exception e)
		{
			DialogField userIdField = dc.getDialog().findField("user_id");
			userIdField.invalidate(dc, e.toString());
			return false;
		}

		return true;
    }

	public AuthenticatedUser createUserData(DialogContext dc)
	{
		String personId = (String) dc.getRequest().getAttribute("user-person-id");
		Map personRegistration = null;
		Map memberOrgs = new HashMap();

		StatementManager.ResultInfo ri = null;
		try
		{
			DatabaseContext dbc = DatabaseContextFactory.getContext(dc);
			StatementManager stmtMgr = StatementManagerFactory.getManager(dc.getServletContext());

			ri = stmtMgr.execute(dbc, dc, null, "person.active-org-memberships", new Object[] { personId });
			ResultSet rs = ri.getResultSet();
            boolean orgSet = false;
			while(rs.next())
			{
				/* col 1 is the org_id, col 2 is org_name */
				memberOrgs.put(rs.getString(1), rs.getString(2));
                // by default, select the first organization to be the pre-selected one
                if (!orgSet)
                {
                    orgSet = true;
                    dc.getSession().setAttribute("organization", rs.getString(1));
                }
			}
			ri.close();

			ri = stmtMgr.execute(dbc, dc, null, "person.registration", new Object[] { personId });
			personRegistration = stmtMgr.getResultSetSingleRowAsMap(ri.getResultSet());
			ri.close();
		}
		catch(Exception e)
		{
			if(ri != null)
				throw new RuntimeException(e.toString() + "\n" + ri.getSQL(dc));
			else
				throw new RuntimeException(e.toString());
		}

		if(personRegistration == null)
			return null;

		AuthenticatedUser user = new BasicAuthenticatedUser(dc.getValue("user_id"), (String) personRegistration.get("complete_name"));
		user.setAttribute("person-id", personId);
        dc.getSession().setAttribute("person_id", personId);
		user.setAttribute("registration", personRegistration);
		user.setAttribute("member-orgs", memberOrgs);

		return user;
	}
}