package app.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.netspective.sparx.xaf.page.VirtualPath;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xif.db.DatabaseContext;

public class PageTag extends com.netspective.sparx.xaf.taglib.PageTag
{
    private static String DEFAULT_DS_NAME = "jdbc/library";
    private static boolean validConnection;

    public boolean validConnection()
    {
        if(validConnection)
            return true;

        DatabaseContext dbc = DatabaseContextFactory.getSystemContext();
        try
        {
            // try and get the default connection and run a simple statement
            Connection conn = dbc.getConnection(DEFAULT_DS_NAME);
            Statement stmt = conn.createStatement();
            stmt.execute("select count(*) from book_info");
        }
        catch(Exception e)
        {
            // if we get an exception here, we've got an invalid connection
            return false;
        }

        validConnection = true;
        return true;
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
            if (!hasPermission())
            {
                out.print(req.getAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME));
                return SKIP_BODY;
            }
            String rootPath = req.getContextPath();
            String resourcesUrl = rootPath + "/resources";

            /* Netspective sample apps have a special border around them. If this is not a sample app, then comment out
			   doSamplePageBegin() and uncomment the <!DOCTYPE>... through <body> */

            doSamplePageBegin(resourcesUrl + "/css/library.css");
            /*
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
			*/
            out.println("<table cellpadding=\"2\" cellspacing=\"5\" border=\"0\" >");
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
            out.println("       <td valign=\"top\" bgcolor=\"#333333\">");
            out.println("      <table width=\"100%\" border=\"0\" cellspacing=\"5\" cellpadding=\"2\">");
            out.println("         <tbody>");
            out.println("           <tr>");
            out.println("             <td valign=\"middle\" bgcolor=\"#EEEEEE\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/index.jsp\">Home</a></font><br>");
            out.println("             </td>");
            out.println("           </tr>");
            out.println("           <tr>");
            out.println("             <td valign=\"middle\" bgcolor=\"#EEEEEE\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/bookInfo.jsp?data_cmd=add\">Add Books</a></font><br>");
            out.println("             </td>");
            out.println("           </tr>");
            out.println("           <tr>");
            out.println("             <td valign=\"middle\" bgcolor=\"#EEEEEE\" align=\"center\"><font color=\"#ffffff\"><a href=\"" + rootPath + "/search.jsp\">Search Books</a></font><br>");
            out.println("             </td>");
            out.println("           </tr>");
            out.println("        </tbody>");
            out.println("      </table>");
            out.println("       <br>");
            out.println("       </td>");

            if(! validConnection())
            {
                String webXml = servletContext.getRealPath("/WEB-INF/web.xml");
                out.println("<h1>Invalid Data Source</h1>");
                out.println("<p align='left'>");
                out.println("The most common solution to this problem is to make sure that the 'url' parameter for the JDBC data source is correctly pointing to the HSQL database file.");
                out.println("If you are using Resin, please open <a href='"+ webXml +"'>" + webXml + "</a> and ensure that the <resource-ref> tag looks something like the following (feel free to copy/paste the red text):<br>");
                out.println("<br><pre>&lt;resource-ref>");
                out.println("  &lt;res-ref-name>"+ DEFAULT_DS_NAME +"&lt;/res-ref-name>");
                out.println("  &lt;res-type>javax.sql.DataSource&lt;/res-type>");
                out.println("  &lt;init-param driver-name=\"org.hsqldb.jdbcDriver\"/>");
                out.println("  &lt;init-param url=\"<font color=red>jdbc:hsqldb:"+ servletContext.getRealPath("/WEB-INF/Database/library.hsqldb") +"</font>\"/>");
                out.println("  &lt;init-param user=\"sa\"/>");
                out.println("  ...");
                out.println("&lt;/resource-ref></code><p>");
                out.println("NOTE: If you are NOT using Resin, please setup a valid data source named '"+ DEFAULT_DS_NAME +"' to point to the HSQL database.");
                out.println("</p>");
                return SKIP_BODY;
            }

            out.println("       <td align=\"center\" valign=\"top\" bgcolor=\"white\"><br>");
        }
        catch (Exception e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            throw new JspException(e.toString() + stack.toString());
        }

        if (handleDefaultBodyItem())
            return SKIP_BODY;
        else
            return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException
    {
        JspWriter out = pageContext.getOut();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String rootPath = req.getContextPath();
        try
        {
            out.println("   </td>");
            out.println("     </tr>");
            out.println("  </tbody>");
            out.println("</table>");
            out.println(" <p>&nbsp;");
            out.println(" <p>");
            //out.println("<table width=100%><tr><td align=right><a target='netspective' href='http://www.netspective.com'><img border='0' alt='Powered by Netspective Sparx' src='"+ rootPath +"/sparx/resources/images/powered-by-sparx.gif'></a></td><td><font size=1>"+ com.netspective.sparx.BuildConfiguration.getVersionAndBuildShort() +"</font></td></table></body>");
            //out.println("</body>");
            //out.println("</html>");
            doSamplePageEnd(); // remove if this is not a Netspective "Sample" application
        }
        catch (IOException e)
        {
            throw new JspException(e.toString());
        }

        doPageEnd();
        return EVAL_PAGE;
    }
}
