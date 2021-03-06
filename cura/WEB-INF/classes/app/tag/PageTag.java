package app.tag;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import java.net.URLEncoder;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import com.netspective.sparx.util.config.*;
import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.html.*;
import com.netspective.sparx.xaf.html.component.*;
import com.netspective.sparx.xaf.navigate.*;
import com.netspective.sparx.xaf.page.*;
import com.netspective.sparx.xaf.security.*;
import com.netspective.sparx.xaf.skin.*;
import com.netspective.sparx.util.value.*;

import app.security.AppLoginDialog;

public class PageTag extends com.netspective.sparx.xaf.taglib.PageTag
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
				loginDialog.producePage(resp.getWriter(), dc);
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
            String rootPath = req.getContextPath();
			String resourcesUrl = rootPath + "/resources";
			doSamplePageBegin(resourcesUrl +"/css/main.css");

			if(doLogin(servletContext, (Servlet) pageContext.getPage(), req, resp))
			{
				req.setAttribute("skipped-body", "yes");
				return SKIP_BODY;
			}

			if(! hasPermission())
			{
				req.setAttribute("skipped-body", "yes");
				out.print(req.getAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME));
				return SKIP_BODY;
			}

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
                    (AuthenticatedUser) session.getAttribute(com.netspective.sparx.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            //String personId = (String) user.getUserId();
            Map personRegistration = (Map) user.getAttribute("registration");
            Object personId = personRegistration.get("person_id");
            //req.setAttribute("person_id", personId);

            // get the list of organizations the user belongs to
            Map memberOrgs = (Map) user.getAttribute("member-orgs");
            int orgCount = memberOrgs.size();
            // get the current organization selected
            String currOrgId = (String)session.getAttribute("organization");
            String currOrgName = (String)memberOrgs.get(currOrgId);

			/*
			out.println("<html>");
			out.println("<head>");
			out.println("	<title>"+ getTitle() +"</title>");
			out.println("</head>");
			out.println("<body  bgcolor='#FFFFFF'  text='#000000' marginheight='0' marginwidth='0' topmargin=0 leftmargin=0>");
            out.println("<link rel='stylesheet' href='"+ resourcesUrl +"/css/main.css'>");
            */
            out.println("<SCRIPT LANGUAGE='JavaScript'><!--");
            out.println("function goto_URL(object) {");
            out.println("   window.location.href = object.options[object.selectedIndex].value;");
            out.println("}//--></SCRIPT>");

			//mainMenu.printHtml(null, out);

            out.println("<!-- 0 --><table width='100%' border='0' cellpadding='0' cellspacing='0'>");
            out.println("<tr>");
            out.println("   <td align='left' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/masthead.jpg'  border='0' alt='Header Image'></td>");

            out.println("   <td align='right' valign='top' background='"+ resourcesUrl +"/images/design/logo-background.jpg'>");
            out.println("   <img src='"+ resourcesUrl +"/images/design/sublogo.jpg'  border='0' alt='Header Image'></td>");

            out.println("</tr>");
            out.println("<tr bgcolor='#4a74e7'>");
            out.println("   <td style='font-family: Trebuchet MS, Arial; font-size: 8pt;height:21px' align='left'>");
            out.println("       <b><font color='#FFFFFF'>");
            out.println("       " + personRegistration.get("complete_name") + " @ <a class='Menu' href='" + rootPath + "/account/home.jsp?org_id=" + currOrgId + "&org_name="+
                    URLEncoder.encode(currOrgName) +"'>" + currOrgName  + "</a></b></font>");
            out.println("   </td>");
            out.println("   <td align='right' style='font-family: Trebuchet MS, Arial; font-size: 8pt'><font color='white'>Account: &nbsp; ") ;
            out.println("   <select name='active_org'  style='font-size: 7pt' onChange='goto_URL(this)'>");
            out.println("       <option></option>");
            Iterator orgKeys = memberOrgs.keySet().iterator();
            while (orgKeys.hasNext())
            {
                String orgId = (String)orgKeys.next();
                //if (currOrgId.equals(orgId))
                //    out.println("       <option  selected value='" + rootPath + "/index.jsp?organization="+  orgId + "'>" + memberOrgs.get(orgId)+ " </option>");
                //else
                    out.println("       <option  value='" + rootPath + "/index.jsp?organization="+  orgId + "'>" + memberOrgs.get(orgId)+ " </option>");
            }
            out.println("   </select></font></td>");
            out.println("</tr>");
            out.println("</table><!-- 0 -->");

			out.println("<!-- 1 --><table width='100%' height='100%' cellpadding='0' cellspacing='0'>");
            out.println("   <tr height='100%'>");
            out.println("       <td height='100%' width='158' align='left' valign='top' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            // This is the static main menu
            out.println("       <!-- 2 --><table  width='100%' cellpadding='0' cellspacing='0'>");
            out.println("           <tr bgcolor='#0000A0'><td style='height:50px;width:158px;' align='left'>");
            out.println("           <label class='Font8pt'><b><font color='white'>Search for:</font></b></label><br><input name='SearchText' type='text' Class='Font8pt' maxlength='50' size='11' Align='AbsBottom' style='width:120px;' />");
            out.println("           <input type='image' title='Begin your search' alt='Begin your search' src='"+ resourcesUrl +"/images/design/go.gif' ID='GoButton' Border='0' Align='AbsBottom'/>");
            out.println("           </td></tr>");
            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>&nbsp;</td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "/index.jsp'>Home</a></font></b>");
            out.println("           </td></tr>");

            out.println("           <tr><td>");
            out.println("           <img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'>");
            out.println("           </td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "/account/index.jsp?_d_exec=1'>Accounts</a></font></b>");
            out.println("           </td></tr>");

            out.println("           <tr><td>");
            out.println("           <img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'>");
            out.println("           </td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "/contact/index.jsp?_d_exec=1'>Contacts</a></font></b>");
            out.println("           </td></tr>");

            out.println("           <tr><td>");
            out.println("           <img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'>");
            out.println("           </td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "/project/index.jsp?_d_exec=1'>Projects</a></font></b>");
            out.println("           </td></tr>");

            out.println("           <tr><td>");
            out.println("           <img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'>");
            out.println("           </td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "/task/index.jsp?_d_exec=1'>Tasks</a></font></b>");
            out.println("           </td></tr>");

            out.println("           <tr><td>");
            out.println("           <img src='"+ resourcesUrl +"/images/design/menu-divider.jpg'>");
            out.println("           </td></tr>");

            out.println("           <tr><td style='height:21px;width:158px;' align='left' background='"+ resourcesUrl +"/images/design/menu-background.jpg'>");
            out.println("           <b><font size='2' face='tahoma' color='#FFFFFF'>&nbsp;&nbsp;<a class='Menu' href='" + rootPath + "?_logout=yes'>Logout</a></font></b>");
            out.println("           </td></tr>");

            out.println("       <!-- 2 --></table>");
            out.println("       </td>");
            out.println("       <td align='left'  valign='top'>");
            out.println("       <!-- 2 --><table  width='100%' cellpadding='1' cellspacing='0'>");
			String heading = getHeading();
			if(heading != null)
			{
				out.print("<tr><td align='left' valign='middle' style='font-family: Trebuchet MS;font-size: 20pt'>"+ heading + "</td></tr>");
			}
            out.println("           <tr><td align='center' valign='top'><font face='tahoma' size=2>");
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
		String rootPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
		try
		{
			if(pageContext.getRequest().getAttribute("skipped-body") == null)
				out.println("</font></td></tr></table><!-- 2 --></td></tr></table><!-- 1 -->");
			/*
			out.println("<p>&nbsp;<p>&nbsp;<center><a target='netspective' href='http://www.netspective.com'><img border='0' alt='Powered by Netspective Sparx' src='"+ rootPath +"/sparx/resources/images/powered-by-sparx.gif'></a><br><font size=1>"+ com.netspective.sparx.BuildConfiguration.getVersionAndBuildShort() +"</font></center>");
			out.println("</td></tr></table>");
			out.print("</body>");
			out.print("</html>");
			*/
			doSamplePageEnd();
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}

		doPageEnd();
		return EVAL_PAGE;
	}
}
