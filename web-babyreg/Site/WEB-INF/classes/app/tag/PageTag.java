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
import org.w3c.dom.Element;

public class PageTag extends com.netspective.sparx.xaf.taglib.PageTag
{
	static private AppLoginDialog loginDialog;

	protected boolean doLogin(ServletContext servletContext, Servlet page, HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		if(loginDialog == null)
		{
			loginDialog = new AppLoginDialog();
			loginDialog.initialize();
		}

		String logout = req.getParameter("_logout");
		if(logout != null)
		{
			ValueContext vc = new ServletValueContext(servletContext, page, req, resp);
			loginDialog.logout(vc);

			/** If the logout parameter included a non-zero length value, then
			 *  we'll redirect to the value provided.
			 */
			if(logout.length() == 0 || logout.equals("1") || logout.equals("yes"))
				resp.sendRedirect(req.getContextPath());
			else
				resp.sendRedirect(logout);
			return true;
		}
        DialogContext dc = null;
		if(! loginDialog.accessAllowed(servletContext, req, resp))
		{
			dc = loginDialog.createContext(servletContext, page, req, resp, SkinFactory.getDialogSkin());

            // this line calls the isValid() method of the AppLoginDialog
			loginDialog.prepareContext(dc);
			if(dc.inExecuteMode())
			{
				loginDialog.execute(dc);
			}
			else
			{
                loginDialog.producePage(dc, resp.getWriter());
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

            AuthenticatedUser user = (AuthenticatedUser) req.getSession(true).getAttribute(LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            //String userName = user.getUserName();
            String relationship = (String) user.getAttribute("relationship");
            String user_id = (String) user.getAttribute("person-id");

			String resourcesUrl = req.getContextPath() + "/resources";
            out.print("<html>");
			out.print("<head>");
			out.print("	<title>Baby Registry</title>");
			out.print("</head>");
			out.print("<body>");
                        out.print("<p>&nbsp;</p>");
                        out.print("<div align='center'>");
                        out.print("<center>");
                        out.print("<table border='0' width='95%' height='481'>");
                        out.print("<tr><td colspan='2' height='35'>");
                        out.print("<p align='center'>Welcome to our own baby registry &nbsp;<font face='Tahoma' size='2'><br>Choose what you want to do below&nbsp;</font></p>");
                        out.print("</td>");
                        out.print("</tr>");
                        out.print("<tr>");
                        out.print("<td width='21%' height='419' valign='top' align='left'><font face='Tahoma' size='2'></font>");
                        if ("0".equals(relationship) || "1".equals(relationship)){
                            out.print("<ul>");
                            out.print("<font face='Tahoma' size='2'><b><a href='AdminGiftDetail.jsp?data_cmd=add'>Add a new Item</a></b></font><br><br>");
                            out.print("<font face='Tahoma' size='2'><b><a href='AdminGiftList.jsp'>List All Items</a></b></font><br><br>");
                            out.print("</ul>");
                        }
						out.print("<ul>");
						out.print("<font face='Tahoma' size='2'><b><a href='GiftsPicked.jsp'>See what I got</a></b></font><br><br>");
						out.print("<font face='Tahoma' size='2'><b><a href='GiftsToBePicked.jsp'>Pick a Gift</a></b></font><br><br>");
                        out.print("<font face='Tahoma' size='2'><b><a href='YourGiftList.jsp?'>View your Gift List</a></b></font><br><br>");
                        out.print("<font face='Tahoma' size='2'><b><a href='NewGift.jsp'>Tell me what you got</a></b></font><br><br>");
                        out.print("<font face='Tahoma' size='2'><b><a href='Suggestion.jsp'>Make a suggestion</a></b></font><br><br>");
                        out.print("<font face='Tahoma' size='2'><b><a href='?_logout=1'>Logout</a></b><br><br>");
						out.print("</ul>");
						out.print("</td>");
						out.print("<td width='100%' valign=top><table width=100% border=0 cellpadding=5><tr><td>");
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
