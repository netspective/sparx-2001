package app.dialog;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.http.*;
import javax.servlet.ServletContext;
import javax.naming.NamingException;

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

public class SharedPasswordDialog extends Dialog
{
    DialogField passwordField;

    public SharedPasswordDialog() {
        super();
        //DialogField passwordField = new DialogField("password","Please enter the first name of the Dad or the Mom of the new baby");
        passwordField = new TextField("password","Shared Password");
        passwordField.setFlag(DialogField.FLDFLAG_REQUIRED);
        addField(passwordField);
        setHeading("You're not a user yet!");
        setName("Gift_PreUserRegistration");
        setDirector(new DialogDirector("NewUser"));
    }

    public void execute(Writer writer, DialogContext context) throws IOException {

        context.getSession().setAttribute("shared-password",context.getValue("password"));
        context.getRequest().setAttribute("data_cmd","add");
        //super.execute(writer, context);
        String query = new String();
        /*
        String person_id = "";
        String person_name = "";
        String url = "";
        HttpServletRequest request = (HttpServletRequest)context.getRequest();
        HttpServletResponse response = (HttpServletResponse)context.getResponse();
        url = request.getContextPath() + "/NewUser.jsp";
        //url = request.getContextPath() + "/userRegistration.jsp?first_name=&last_name=";
        //"&person_name=" + URLEncoder.encode(person_name);
        try
        {
            response.sendRedirect(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */

    }

    public boolean accessAllowed(ServletContext context, HttpServletRequest request, HttpServletResponse response)
	{
		return request.getSession(true).getAttribute("shared-password") != null;
	}

    public void producePage(DialogContext dc, Writer writer) throws IOException
	{
		String resourcesUrl = ((HttpServletRequest) dc.getRequest()).getContextPath() + "/resources";

		writer.write("<head>");
		writer.write("<title>Welcome to the Hernandez new Baby Registry</title>");
		writer.write("</head>");
		writer.write("<body background='white'>");
		writer.write("	<center><br>");
		writer.write("		<img src='"+ resourcesUrl +"/images/login.gif' border='0'>");
		writer.write("		<p>");
		writer.write("		Type the first name of the Dad or the Mom of the new baby.");
		writer.write("		<p>");
        renderHtml(writer, dc, true);
		writer.write("	</center>");
		writer.write("</body>");
	}

    public boolean isValid(DialogContext dc)
    {
		if(! super.isValid(dc))
			return false;

       String passwordProvided = dc.getValue("password");

       StatementManager sm = dc.getStatementManager();
       DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());
       String[] result = null;

       try {
         result = sm.executeStmtGetRowsAsStrings(dbContext, dc, null , "User.sharedPassword", null);
       } catch (StatementNotFoundException e) { return false;
       } catch (NamingException e) { return false;
       } catch (SQLException e) { return false;
       }

       if (result == null)
         return false;

       for (int i = 0; i < result.length; i++) {
          String s = result[i];
          if (s.equalsIgnoreCase(passwordProvided)){
            return true;
          }
       }

       DialogField password = dc.getDialog().findField("password");
       password.invalidate(dc, "The password provided not valid.");
       return false;

    }
}