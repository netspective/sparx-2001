package com.netspective.product.cura.tag;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.xaf.form.*;
import com.xaf.navigate.*;
import com.xaf.security.*;
import com.xaf.skin.*;
import com.xaf.value.*;
import com.netspective.product.cura.xml.MenuXml;
import com.netspective.product.cura.security.AppLoginDialog;

public class PageTag extends com.xaf.navigate.taglib.PageTag
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
            MenuXml menu = new MenuXml();
            String jsString = menu.getMenuString(pageContext);

			out.println("<html>");
			out.println("<head>");
			out.println("	<title>"+ getTitle() +"</title>");
			out.println("</head>");
			out.println("<body  bgcolor='#FFFFFF' link='#cc0000' vlink='#336699' text='#000000' marginheight='0' marginwidth='0' topmargin=0 leftmargin=0>");

            out.println("<script language='JavaScript1.2' src='"+ rootPath +"/resources/scripts/coolmenus/coolmenus3.js'>");
            out.println("</script>");
            out.println("<script language='JavaScript1.2' src='"+ rootPath +"/resources/scripts/coolmenus/xmenu.js' type='text/javascript'>");
            out.println("</script>");

            out.println("<script language='JavaScript1.2' type='text/javascript'>");
            out.println(jsString);
            out.println("oCMenu.makeStyle(); oCMenu.construct();");
            out.println("</script>");
            out.println("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
            out.println("<tr bgcolor='#AFD997'>");
            out.println("   <td align='left' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/masthead.jpg'  border='0' alt='Header Image'>");
            out.println("   </td>");
            out.println("   <td align='right' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/sublogo.jpg'  border='0' alt='Header Image'>");
            out.println("   </td>");

            out.println("</tr>");
            out.println("</table><p>");


			out.print("<table><tr><td><font face='verdana' size=2>");

			String heading = getHeading();
			if(heading != null)
			{
				out.print("<h1>"+ heading +"</h1>");
			}

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
			out.print("</font></td></tr></table>");
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
