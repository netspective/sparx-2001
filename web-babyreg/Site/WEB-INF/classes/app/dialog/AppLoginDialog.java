package app.dialog;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.http.*;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.security.*;
import com.netspective.sparx.xaf.skin.*;
import com.netspective.sparx.xaf.sql.*;
import com.netspective.sparx.util.value.*;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.Configuration;
import com.caucho.server.http.HttpResponse;

public class AppLoginDialog extends LoginDialog
{
	protected StandardDialogSkin skin;
    protected boolean showUserRegistration;

    public boolean isShowUserRegistration() {
        return showUserRegistration;
    }

    public TextField createUserIdField()
	{
		TextField result = new TextField("user_fname", "First Name");
		result.setFlag(DialogField.FLDFLAG_REQUIRED);
		result.setFlag(DialogField.FLDFLAG_INITIAL_FOCUS);
		return result;
	}

    // Usually this would not override neither of the these two methods.
    // In this case, because the application doesn't use the normal username/password
    // kind of login. ...  TODO
	public TextField createPasswordField()
	{
		TextField result = new TextField("user_lname", "Last Name");
		result.setFlag(DialogField.FLDFLAG_REQUIRED);
		return result;
	}

	public void initialize()
	{
		super.initialize();

		skin = new StandardDialogSkin();
		skin.setOuterTableAttrs("cellspacing='1' cellpadding='0'");
		skin.setInnerTableAttrs("cellspacing='0' cellpadding='4'");
		skin.setCaptionFontAttrs("size='2' face='tahoma,arial,helvetica' style='font-size:8pt' color='navy'");

		setHeading((SingleValueSource) null);
        setRetainAllRequestParams(true);
	}

	public DialogSkin getSkin()
	{
		return skin;
	}

	public void producePage(DialogContext dc, Writer writer) throws IOException
	{
		String resourcesUrl = ((HttpServletRequest) dc.getRequest()).getContextPath() + "/resources";
        String fname = dc.getRequest().getParameter("user_fname");
        String lname = dc.getRequest().getParameter("user_lname");
        System.out.println(fname + " " + lname);
        if (fname != null && fname.length() > 0) {
            dc.setValue(dc.getField("user_fname"),fname);
            dc.setValue(dc.getField("user_lname"),lname);
        }
        System.out.println(dc.getRequest().getParameter("user_fname"));
		writer.write("<head>");
		writer.write("<title>Welcome to the Hernandez new Baby Registry</title>");
		writer.write("</head>");
		writer.write("<body background='white'>");
		writer.write("	<center><br>");
		writer.write("		<img src='"+ resourcesUrl +"/images/login.gif' border='0'>");
		writer.write("		<p>");
		writer.write("		Type your fist and last name to get into the site.");
		writer.write("		<br>The names are case sensitive.");
		writer.write("		<p>");
        renderHtml(writer, dc, true);
        writer.write("		<br><a href='NewUserRegistration.jsp?data_cmd=add'>New User</a>");
		writer.write("	</center>");
		writer.write("</body>");
	}

    public boolean isValid(DialogContext dc)
    {
		if(! super.isValid(dc))
			return false;

		try
		{

			String userLNameProvided = dc.getValue("user_lname");
            String userFNameProvided = dc.getValue("user_fname");

			DatabaseContext dbc = DatabaseContextFactory.getContext(dc);
			StatementManager stmtMgr = StatementManagerFactory.getManager(dc.getServletContext());

			StatementManager.ResultInfo ri = stmtMgr.execute(dbc, dc, null, "User.login", new Object[] { userFNameProvided, userLNameProvided });
			Object[] loginInfo = stmtMgr.getResultSetSingleRowAsArray(ri.getResultSet());
			ri.close();

            // This means there was no match, therefor this person is not a user in the system yet
            // TODO: explain more on the login method used for this app ...
			if(loginInfo == null)
			{
				DialogField userFNameField = dc.getDialog().findField("user_fname");
                DialogField userLNameField = dc.getDialog().findField("user_lname");
                userFNameField.invalidate(dc, "First Name '"+ userFNameProvided + "' provided not valid.");
				userLNameField.invalidate(dc, "Last Name '"+ userLNameProvided + "' provided not valid.");
				return false;
			}

			String personId = loginInfo[0].toString();
			dc.getRequest().setAttribute("user-person-id", personId);
		}
		catch(Exception e)
		{
			DialogField userFnameField = dc.getDialog().findField("user_fname");
			userFnameField.invalidate(dc, e.toString());
			return false;
		}

		return true;
    }

	public AuthenticatedUser createUserData(DialogContext dc)
	{
		String personId = (String) dc.getRequest().getAttribute("user-person-id");
		String personRelationship = null;

		StatementManager.ResultInfo ri = null;
		try
		{
			DatabaseContext dbc = DatabaseContextFactory.getContext(dc);
			StatementManager stmtMgr = StatementManagerFactory.getManager(dc.getServletContext());

			ri = stmtMgr.execute(dbc, dc, null, "User.user_relationship", new Object[] { personId });
            personRelationship = stmtMgr.getResultSetSingleRowAsStrings(ri.getResultSet())[0];
            //personRelationship = stmtMgr.getResultSetSingleRowAsMap(ri.getResultSet());
			ri.close();
		}
		catch(Exception e)
		{
			if(ri != null)
				throw new RuntimeException(e.toString() + "\n" + ri.getSQL(dc));
			else
				throw new RuntimeException(e.toString());
		}

		if(personRelationship == null)
			return null;

		AuthenticatedUser user = new BasicAuthenticatedUser(dc.getValue("user_id"), personRelationship);
		user.setAttribute("person-id", personId);
		user.setAttribute("relationship", personRelationship);

        HttpServletRequest req = (HttpServletRequest) dc.getRequest();
		req.getSession(true).setAttribute("person-id", personId);

		return user;
	}
}