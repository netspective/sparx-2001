package app.tag;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.html.*;
import com.xaf.html.component.*;
import com.xaf.navigate.*;
import com.xaf.page.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;

import app.security.AppLoginDialog;

public class PageTag extends com.xaf.navigate.taglib.PageTag
{
	static private AppLoginDialog loginDialog;
	static private HierarchicalMenu mainMenu;
	static private VirtualPath menuStructure;

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

		if(! loginDialog.accessAllowed(servletContext, req, resp))
		{
			DialogContext dc = loginDialog.createContext(servletContext, page, req, resp, SkinFactory.getDialogSkin());
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

        HttpSession session = req.getSession();

		try
		{
			if(doLogin(servletContext, (Servlet) pageContext.getPage(), req, resp))
				return SKIP_BODY;

			if(! hasPermission())
			{
				out.print(req.getAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME));
				return SKIP_BODY;
			}
            String rootPath = req.getContextPath();
			String resourcesUrl = rootPath + "/resources";

			// The dynamic HTML menu is temporarily disabled
            /*
            if(menuStructure == null)
			{
				Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(servletContext);
				if(appConfig == null)
					throw new Exception("Unable to get default configuration manager");

				String structFile = appConfig.getValue(null, "app.ui.structure-file");
				if(structFile == null)
					throw new Exception("Unable to retrieve structure file from configuration variable 'app.navigate.structure-file'");

				menuStructure = VirtualPath.importFromXml(structFile);
				if(menuStructure == null)
					throw new Exception("Unable to create menu structure");

				mainMenu = new HierarchicalMenu(1, 5, 125, 60, menuStructure, appConfig.getValue(null, "framework.shared.scripts-url"));
				mainMenu.setTopPermanent(true);
				mainMenu.setTopHorizontal(true);
				mainMenu.setTopMoreImagesVisible(false);
				mainMenu.setBgColor("#AFD997");
				mainMenu.setBorderColor("navy");
			}
            */
            AuthenticatedUser user =
                    (AuthenticatedUser) session.getAttribute(com.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            //String personId = (String) user.getUserId();
            Map personRegistration = (Map) user.getAttribute("registration");
            BigDecimal personId = (BigDecimal) personRegistration.get("person_id");
            req.setAttribute("person_id", personId);


			out.println("<html>");
			out.println("<head>");
			out.println("	<title>"+ getTitle() +"</title>");
			out.println("</head>");
			out.println("<body  bgcolor='#FFFFFF' link='#cc0000' vlink='#336699' text='#000000' marginheight='0' marginwidth='0' topmargin=0 leftmargin=0>");

			//mainMenu.printHtml(null, out);

            out.println("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
            out.println("<tr >");
            out.println("   <td align='left' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/masthead.jpg'  border='0' alt='Header Image'>");
            out.println("   </td>");
            out.println("   <td align='right' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/sublogo.jpg'  border='0' alt='Header Image'>");
            out.println("   </td>");
            out.println("</tr>");
            out.println("<tr bgcolor='#8080FF'>");
            out.println("   <td colspan='2' align='left' height='25' background='"+ resourcesUrl +"/images/design/profile-background.jpg'><b><font face='verdana' color='#FFFFFF' size=2>" + personRegistration.get("complete_name") + "</font></b></td>");
            out.println("</tr>");
            out.println("</table>");

			out.println("<table width='100%' height='100%' cellpadding='0' cellspacing='0'>");
            out.println("   <tr height='100%'>");
            out.println("       <td height='100%' width='120' align='left' valign='top' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            // This is the static main menu
            out.println("       <table  width='100%' cellpadding='0' cellspacing='0'>");
            /*
            out.println("       <style type='text/css'>");
            out.println("       <!--                   ");
            out.println("           a:link  { color: white; text-decoration: none }   ");
            out.println("           a:active  { color: white; text-decoration: none }   ");
            out.println("           a:visited  { color: white; text-decoration: none }   ");
            out.println("           a:hover { color: yellow; text-decoration: none } ");
            out.println("        -->                   ");
            out.println("       </style>               ");
            */
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>&nbsp;</td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>&nbsp;</td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font face='tahoma' color='#ffffff'><img src='"+ resourcesUrl +"/images/design/menu-arrow.gif'><a style='a:hover { color: yellow; text-decoration: none } a:link  { color: white; text-decoration: none } a:visited  { color: white; text-decoration: none }' href='" + rootPath + "/index.jsp'>Home</a></font></b>");
            out.println("           </td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font face='tahoma' color='#ffffff'><img src='"+ resourcesUrl +"/images/design/menu-arrow.gif'><a style='color: white; text-decoration: none' href='" + rootPath + "/account/index.jsp'>Accounts</a></font></b>");
            out.println("           </td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font face='tahoma' color='#ffffff'><img src='"+ resourcesUrl +"/images/design/menu-arrow.gif'><a style='color: white; text-decoration: none' href='" + rootPath + "/contact/index.jsp'>Contacts</a></font></b>");
            out.println("           </td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font face='tahoma' color='#ffffff'><img src='"+ resourcesUrl +"/images/design/menu-arrow.gif'><a style='color: white; text-decoration: none' href='" + rootPath + "/project/index.jsp'>Projects</a></font></b>");
            out.println("           </td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font face='tahoma' color='#ffffff'><img src='"+ resourcesUrl +"/images/design/menu-arrow.gif'><a style='color: white; text-decoration: none' href='" + rootPath + "/task/index.jsp'>Tasks</a></font></b>");
            out.println("           </td></tr>");
            out.println("           <tr><td align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'><img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'></td></tr>");
            out.println("       </table>");
            out.println("       </td>");
            out.println("       <td align='left'  valign='top'>");
            out.println("       <table  width='100%' cellpadding='3' cellspacing='0'>");
            out.println("           <tr><td align='left'><font face='tahoma' size=2>");
			String heading = getHeading();
			if(heading != null)
			{
				out.print("<h1>"+ heading +"</h1>");
			}

		}
		catch(Exception e)
		{
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
			throw new JspException(e.toString() + stack.toString());
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
			out.println("</font></td></tr></table>");
            out.println("</td></tr></table>");
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
