package app.tag;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.navigate.*;
import com.netspective.sparx.xaf.security.*;
import com.netspective.sparx.xaf.skin.*;
import com.netspective.sparx.util.value.*;
import app.dialog.AppLoginDialog;
import app.dialog.SharedPasswordDialog;
import org.w3c.dom.Element;


public class RegistrationPageTag extends com.netspective.sparx.xaf.taglib.PageTag
{
	static private SharedPasswordDialog sharedPasswordDialog;

	protected boolean doLogin(ServletContext servletContext, Servlet page, HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		if(sharedPasswordDialog == null)
		{
			sharedPasswordDialog = new SharedPasswordDialog();
			//sharedPasswordDialog.initialize();
		}

		DialogContext dc = null;

        if (! sharedPasswordDialog.accessAllowed(servletContext, req, resp)) {
            dc = sharedPasswordDialog.createContext(servletContext, page, req, resp, SkinFactory.getDialogSkin());

            sharedPasswordDialog.prepareContext(dc);

            if(dc.inExecuteMode())
            {
                sharedPasswordDialog.execute(resp.getWriter(),dc);
            }
            else
            {
                sharedPasswordDialog.producePage(dc, resp.getWriter());
                return true;
            }
        }
        return false;
	}

	public int doStartTag() throws JspException
	{
		doPageBegin();

		JspWriter out = pageContext.getOut();

		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse) pageContext.getResponse();
		ServletContext servletContext = pageContext.getServletContext();

		try
		{
			if(doLogin(servletContext, (Servlet) pageContext.getPage(), req, resp))
				return SKIP_BODY;

			if(! hasPermission())
			{
				out.print(req.getAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME));
                System.out.println("NOT PERMISSION!!!!!!!!!!!!!!!!!");
				return SKIP_BODY;
			}


			String resourcesUrl = req.getContextPath() + "/resources";
            out.write("<head>");
            out.write("<title>Welcome to the Hernandez new Baby Registry</title>");
            out.write("</head>");
            out.write("<body background='white'>");
            out.write("	<center><br>");
            out.write("		<img src='"+ resourcesUrl +"/images/login.gif' border='0'>");
            out.write("		<p>");
            out.write("		Please, enter the following information.<br>");
            out.write("		<br>Keep in mind that the first and last name");
            out.write("		<br>that you enter on this screen are the ones");
            out.write("		<br>you will use to login later.");
            out.write("		<br>The names are case sensitive.");
            out.write("		<br>Enter a phone number only if you do not have an e-mail.");
            out.write("		<p>");
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}

		if(handleDefaultBodyItem())
			return SKIP_BODY;
		else
			return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		JspWriter out = pageContext.getOut();
		try
		{
            out.write("</center>");
            out.write("</body>");
			out.print("<font></td></tr></table>");
			out.print("</body>");
			out.print("</html>");
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}

		doPageEnd();
		return EVAL_PAGE;
	}
}
