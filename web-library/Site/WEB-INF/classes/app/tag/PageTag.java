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
	static private VirtualPath menuStructure;

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
			if(! hasPermission())
			{
				out.print(req.getAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME));
				return SKIP_BODY;
			}
            String rootPath = req.getContextPath();
			String resourcesUrl = rootPath + "/resources";

			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<html>");
			out.println("<head>");
			out.println("  <title>" + getTitle() + "</title>");
			out.println("  <link rel='stylesheet' href='"+ resourcesUrl +"/css/library.css'>");
			out.println("");
			out.println("  <meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">");
			out.println("</head>");
			out.println("  <body>");
			out.println("");
			out.println("<table cellpadding=\"2\" cellspacing=\"5\" border=\"0\" width=\"100%\">");
			out.println("   <tbody>");
			out.println("     <tr>");
			out.println("       <td valign=\"top\" width=\"100\"><img src=\"" + resourcesUrl + "/images/scatteredBooks.jpg\" alt=\"Library logo\" width=\"150\" height=\"84\">");
			out.println("       <br>");
			out.println("       </td>");
			out.println("       <td valign=\"middle\" align=\"center\">");
			out.println("      <h1>" + getHeading() + "</h1>");
			out.println("       </td>");
			out.println("     </tr>");
			out.println("     <tr>");
			out.println("       <td valign=\"top\" bgcolor=\"#800000\">");
			out.println("      <table width=\"100%\" border=\"0\" cellspacing=\"5\" cellpadding=\"2\">");
			out.println("         <tbody>");
			out.println("           <tr>");
			out.println("             <td valign=\"middle\" bgcolor=\"#cc0000\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/home.jsp\">Home</a></font><br>");
			out.println("             </td>");
			out.println("           </tr>");
			out.println("           <tr>");
			out.println("             <td valign=\"middle\" bgcolor=\"#cc0000\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/add.jsp?data_cmd=add\">Add Books</a></font><br>");
			out.println("             </td>");
			out.println("           </tr>");
			out.println("           <tr>");
			out.println("             <td valign=\"middle\" bgcolor=\"#cc0000\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/search.jsp\">Search Books</a></font><br>");
			out.println("             </td>");
			out.println("           </tr>");
			out.println("");
			out.println("        </tbody>");
			out.println("      </table>");
			out.println("       <br>");
			out.println("       </td>");
//			out.println("       <td valign=\"top\" bgcolor=\"#800000\"><br>");
			out.println("       <td align=\"center\" valign=\"top\" bgcolor=\"white\"><br>");

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
			out.println("   </td>");
			out.println("     </tr>");
			out.println("");
			out.println("  </tbody>");
			out.println("</table>");
			out.println(" <br>");
			out.println(" <br>");
			out.println("</body>");
			out.println("</html>");
		}
		catch(IOException e)
		{
			throw new JspException(e.toString());
		}

		doPageEnd();
		return EVAL_PAGE;
	}
}
