package com.netspective.sparx.xaf.skin;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.netspective.sparx.xaf.page.*;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.BuildConfiguration;
import com.netspective.sparx.xif.SchemaDocument;
import org.w3c.dom.Element;

public class HtmlTabbedNavigationSkin implements NavigationSkin
{
    protected NavigationStyle horizontalCaptionStyle = new HorizontalCaptionsStyle();
    protected NavigationStyle horizontalImagesStyle = new HorizontalImagesStyle();
    protected NavigationStyle verticalCaptionStyle = new VerticalCaptionsStyle();
    protected String cssFileName = "/sparx/resources/css/tabbed-navigation-skin.css";

    public HtmlTabbedNavigationSkin()
    {
        horizontalCaptionStyle = new HorizontalCaptionsStyle();
        horizontalImagesStyle = new HorizontalImagesStyle();
        verticalCaptionStyle = new VerticalCaptionsStyle();
    }

    public String getApplicationName(NavigationContext nc)
    {
        return SchemaDocument.sqlIdentifierToText(nc.getServletContext().getServletContextName().substring(1), true);
    }

    public void renderPageMetaData(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        ServletPage page = nc.getPage();

        writer.write("<!-- Application Header Begins -->\n");
        writer.write("<html>\n");
        writer.write("<head>\n");
        writer.write("<title>" + (page != null ? page.getTitle(nc) : "No page found") + "</title>\n");
        writer.write("	<link rel=\"stylesheet\" href=\"" + rootUrl + cssFileName +"\" type=\"text/css\">\n");
        writer.write("</head>\n");
    }

    public void renderPageMasthead(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        AuthenticatedUser authUser = (AuthenticatedUser) nc.getSession().getAttribute("authenticated-user");
        String personName = authUser != null ? authUser.getUserId() : "Not logged in";
        String personId = authUser != null ? authUser.getUserName() : "Not logged in";
        String orgName = authUser != null ? authUser.getUserOrgId() : "Not logged in";
        String orgId = authUser != null ? authUser.getUserOrgName() : "Not logged in";
        String appName = getApplicationName(nc);

        writer.write("<body>");
        writer.write("	<table class=\"app_header\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        writer.write("	    <tr>");
        writer.write("           <td class=\"app_header\" nowrap height=\"20\">");
        writer.write("	            <img src=\"" + rootUrl + "/resources/images/design/app-header/app-header-marker.gif\">&nbsp;USER:&nbsp;<a class=\"app_header\" href=\"" + rootUrl + "/person/summary.jsp?person_id=" + personId + "\">" + personName.toUpperCase() + "</a>");
        writer.write("           </td>");
        writer.write("           <td class=\"app_header\" nowrap>");
        writer.write("               <img src=\"" + rootUrl + "/resources/images/design/app-header/app-header-marker.gif\">&nbsp;ORG:&nbsp;<a class=\"app_header\" href=\"" + rootUrl + "/org/summary.jsp?org_id=" + orgId + "\">" + orgName.toUpperCase() + "</a>");
        writer.write("           </td>");
        writer.write("           <td width=\"100%\" class=\"app_header\" nowrap>");
        writer.write("	            <img src=\"" + rootUrl + "/resources/images/design/app-header/app-header-spacer.gif\">");
        writer.write("           </td>");
        writer.write("           <td class=\"app_header\" nowrap>");
        writer.write("           <img src=\"" + rootUrl + "/resources/images/design/app-header/app-header-marker.gif\"><a class=\"app_header\" href=\"" + rootUrl + "?_logout=yes\">&nbsp;LOGOUT</a>");
        writer.write("           </td>");
        writer.write("	    </tr>");
        writer.write("   </table>");

        writer.write("<!-- Application Header Ends -->");

        writer.write("<!-- Master Header Begins -->");
        writer.write("   <TABLE class=\"mast_header\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0>");
        writer.write("      <TR>");
        writer.write("	        <TD>&nbsp;<A class=\"app_name\" href=\"" + rootUrl + "/index.jsp\">"+ appName +"</A></TD>");
        writer.write("	        <TD width=\"100%\"><IMG height=25 src=\"" + rootUrl + "/resources/images/design/app-header/app-header-spacer.gif\" width=10 border=0></TD>");
        writer.write("			<TD rowspan=\"2\"><!-- space for image on right --></TD>");
        writer.write("       </TR>");

        writer.write("      <TR>");
        writer.write("	        <TD>");
        writer.write("<!-- Master Header Ends -->");
    }

    public void renderPageMenusLevelOne(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        writer.write("<!-- App Tabs Begins -->");
        renderNavigation(writer, nc, 1, horizontalImagesStyle, rootUrl);
        writer.write("<!-- App Tabs Ends -->");

        writer.write("	        </TD>");
        writer.write("         <TD><IMG height=15 alt=\"\" src=\"" + rootUrl + "/resources/images/design/app-tabs/apptab_div.gif\" width=\"100%\" border=0></TD>");
        writer.write("      </TR>");
        writer.write("   </TABLE>");
    }

    public void renderPageMenusLevelTwo(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        writer.write("<!-- Function Tabs Begins -->");
        renderNavigation(writer, nc, 2, horizontalCaptionStyle, rootUrl);
        writer.write("<!-- Function Tabs Ends -->");
    }

    public void renderPageHeading(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        writer.write("<!-- Page Heading Begins -->");
        writer.write("<TABLE class=\"page_header\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0>");
        writer.write("<TR>");

        ServletPage page = nc.getPage();
        String actionIcon = page != null ? page.getPageIcon() : null;
        if (actionIcon != null && !"".equals(actionIcon))
        {
            writer.write("<td>&nbsp;<img src=\"" + actionIcon + "\"></td>");
        }
        //Page Heading
        writer.write("<TD nowrap class=page_header height=\"30\">" + (page != null ? page.getHeading(nc) : "No page found") + "</TD>");
        writer.write("<td><img src=\"" + rootUrl + "/resources/images/design/page-header/page-heading-middle.gif\"></td>");
        writer.write("<TD width=\"100%\" background=\"" + rootUrl + "/resources/images/design/page-header/page-heading-background.gif\"><IMG height=3 alt=\"\" src=\"" + rootUrl + "/resources/images/pixel.gif\" width=10 border=0></TD>");

        // Select the entity icon that goes on the right of the Page Heading.
        String entityIcon = null;
        if (entityIcon != null && !"".equals(entityIcon))
        {
            writer.write("<td><img src=\"" + entityIcon + "\"></td>");
        }

        writer.write("</TR></TABLE>");
        writer.write("<!-- Page Heading Ends -->");
    }

    public void renderPageMenusLevelThree(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        StringWriter sideBarNavHtml = new StringWriter();
        renderNavigation(sideBarNavHtml, nc, 3, verticalCaptionStyle, rootUrl);
        if(sideBarNavHtml.getBuffer().length() > 0)
        {
            writer.write("    <TD vAlign=top height=\"100%\">");
            writer.write(sideBarNavHtml.toString());
            writer.write("    </TD>");
        }
        writer.write("		  <TD class=\"page_content\" vAlign=top align=\"center\" width=\"100%\">");
    }

    public void renderPageFooter(Writer writer, NavigationContext nc, NavigationTree navTree, String rootUrl) throws IOException
    {
        String sparxResourcesUrl = rootUrl + "/sparx/resources";

        if (nc.getRequest().getAttribute("skipped-body") == null)
        {
            writer.write("   </TD></TR></TABLE>");
            writer.write("   <table width=100%> ");
            writer.write("        <tr> ");
            writer.write("                <td align=left class=\"power_by_sparx_footer\"> ");
            writer.write("                        <a target=\"netspective\" href=\"http://www.netspective.com/\"> ");
            writer.write("                        <img border=\"0\" alt=\"Powered by Netspective Sparx\" src=\"" + sparxResourcesUrl + "/images/powered-by-sparx.gif\">");
            writer.write("                        </a> ");
            writer.write("                        <br> ");
            writer.write("                                " + BuildConfiguration.getVersionAndBuildShort());
            writer.write("                </td> ");
            writer.write("                <td align=center class=\"copyright_footer\"> ");
            writer.write("                        Copyright &copy; 2002, Netspective Communications LLC. All Rights Reserved.");
            writer.write("                </td> ");
            writer.write("        </tr> ");
            writer.write("   </table> ");
            writer.write("</body>");
        }
    }

    public void renderNavigation(Writer writer, NavigationContext nc, int level, NavigationStyle style, String rootUrl) throws IOException
    {
        NavigationTree activeNavTree = (NavigationTree) nc.getActivePath().getMatchedPath();
        List ancestorList = activeNavTree.getAncestorsList();
        NavigationTree currentNavTree = null;

        if (level < ancestorList.size())
        {
            currentNavTree = (NavigationTree) ancestorList.get(level);
        }
        else if (level == ancestorList.size())
        {
            currentNavTree = activeNavTree;
        }
        else
        {
            return;
        }

        style.renderHtml(writer, currentNavTree, nc, rootUrl);
    }

    public void renderNavigation(Writer writer, NavigationTree owner, NavigationContext nc) throws IOException
    {
        NavigationTree navTree = (NavigationTree) nc.getActivePath().getMatchedPath();
        String rootUrl = ((HttpServletRequest)nc.getRequest()).getContextPath();

        renderPageMetaData(writer, nc, navTree, rootUrl);
        renderPageMasthead(writer, nc, navTree, rootUrl);
        renderPageMenusLevelOne(writer, nc, navTree, rootUrl);
        renderPageMenusLevelTwo(writer, nc, navTree, rootUrl);
        renderPageHeading(writer, nc, navTree, rootUrl);
        renderPageMenusLevelThree(writer, nc, navTree, rootUrl);
        renderPageFooter(writer, nc, navTree, rootUrl);
    }

    abstract public class NavigationStyle
    {
        static public final int NAVFLAG_VERTICAL_DISPLAY = 1;
        static public final int NAVFLAG_USE_IMAGES = NAVFLAG_VERTICAL_DISPLAY * 2;
        static public final int NAVFLAG_EXPAND_MARGIN_LEFT = NAVFLAG_USE_IMAGES * 2;
        static public final int NAVFLAG_EXPAND_MARGIN_RIGHT = NAVFLAG_EXPAND_MARGIN_LEFT * 2;

        protected String tableAttrs;
        protected String tableClass;

        protected String innerSeparatorAttrs;
        protected String innerSeparatorClass;
        protected String innerSeparatorImgClass;
        protected String innerSeparatorImgAttrs;

        protected String outerSeparatorAttrs;
        protected String outerSeparatorClass;
        protected String outerSeparatorImgClass;
        protected String outerSeparatorImgAttrs;

        protected String containerAttrs;
        protected String containerClass;

        protected String navAttrs;
        protected String navClass;
        protected String navImgAttrs;
        protected String navLinkAttrs;
        protected String navLinkClass;

        protected String prependHtml;
        protected String appendHtml;

        protected long flags;

        public NavigationStyle()
        {
            this.tableAttrs = "cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"";
            this.tableClass = "class=\"function_tabs\"";

            this.outerSeparatorAttrs = "colspan=\"50\" width=\"100%\"";
            this.outerSeparatorClass = "";
            this.outerSeparatorImgAttrs = "height=2 width=10 border=0 alt=\"\"";
            this.outerSeparatorImgClass = "";

            this.innerSeparatorAttrs = "";
            this.innerSeparatorClass = "";
            this.innerSeparatorImgAttrs = "height=8 width=10 border=0 alt=\"\"";
            this.innerSeparatorImgClass = "";

            this.containerAttrs = "";
            this.containerClass = "";

            this.navAttrs = "nowrap";
            this.navClass = "class=\"function_tab_";
            this.navImgAttrs = "";

            this.navLinkAttrs = "";
            this.navLinkClass = "class=\"tab_navigation_";

            this.flags = NAVFLAG_VERTICAL_DISPLAY | NAVFLAG_EXPAND_MARGIN_RIGHT;
        }

        public void importFromXml(Element element)
        {
        }

        public final boolean flagIsSet(long flag)
        {
            return (flags & flag) == 0 ? false : true;
        }


        public final void setFlag(long flag)
        {
            flags |= flag;
        }

        public final void clearFlag(long flag)
        {
            flags &= ~flag;
        }

        public final void updateFlag(long flag, boolean set)
        {
            if (set) flags |= flag; else flags &= ~flag;
        }

        abstract public void renderHtml(Writer writer, NavigationTree currentNavTree, NavigationContext nc, String rootUrl) throws IOException;
    }

    public class HorizontalImagesStyle extends NavigationStyle
    {
        public HorizontalImagesStyle()
        {
            flags = NAVFLAG_USE_IMAGES | NAVFLAG_EXPAND_MARGIN_LEFT;
            tableAttrs = "cellspacing=\"0\" cellpadding=\"0\" border=\"0\"";
            tableClass = "";
            innerSeparatorImgAttrs = "height=15 width=10 border=0 alt=\"\"";
            innerSeparatorImgClass = "";
            navAttrs = "width=75";
            navClass = "class=\"function_tab_";
            navImgAttrs = "height=15 width=75 border=0 alt=\"\"";
        }

        public void renderHtml(Writer writer, NavigationTree currentNavTree, NavigationContext nc, String rootUrl) throws IOException
        {
            //render each appTab
            List tabElements = currentNavTree.getSibilingList();

            writer.write("<TABLE " + tableClass + " " + tableAttrs + ">");
            //TODO: add flag to see wether we need an outer separator and how many of them.
            writer.write("<TR " + containerClass + " " + containerAttrs + ">");
            String appTabDivImage = "apptab_div.gif";
            for (int i = 0; i < tabElements.size(); i++)
            {

                appTabDivImage = "app-separator-" + i + ".gif";

                NavigationTree tabElement = (NavigationTree) tabElements.get(i);
                if (tabElement.isVisible(nc))
                {
                    writer.write("<TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgAttrs + " ");
                    writer.write("src=\"" + rootUrl + "/resources/images/design/app-tabs/" + appTabDivImage + "\"></TD>");
                    writer.write("<TD " + navAttrs + ">");
                    writer.write("<A " + navLinkAttrs + " href=\"" + tabElement.getUrl(nc) + "\">");
                    writer.write("<IMG " + navImgAttrs + " ");
                    //String imagePath = getValue(nc, tabElement.getId() + "." + (tabElement.isInActivePath(nc) ? "on" : "off"));
                    String imagePath = "need to fix"; //TODO
                    writer.write("src=\"" + imagePath + "\"></A></TD>");
                }
            }

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_RIGHT))
            {
                writer.write("<TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\" ></TD>");
            }

            writer.write("</TR>");
            writer.write("</TABLE>");
        }
    }

    public class HorizontalCaptionsStyle extends NavigationStyle
    {
        public HorizontalCaptionsStyle()
        {
            this.tableAttrs = "cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"";
            this.tableClass = "class=\"function_tabs\"";

            this.outerSeparatorAttrs = "colspan=\"50\" width=\"100%\"";
            this.outerSeparatorClass = "";
            this.outerSeparatorImgAttrs = "height=2 width=10 border=0 alt=\"\"";
            this.outerSeparatorImgClass = "";

            this.innerSeparatorAttrs = "";
            this.innerSeparatorClass = "";
            this.innerSeparatorImgAttrs = "height=8 width=10 border=0 alt=\"\"";
            this.innerSeparatorImgClass = "";

            this.containerAttrs = "";
            this.containerClass = "";

            this.navAttrs = "nowrap";
            this.navClass = "class=\"function_tab_";
            this.navImgAttrs = "";

            this.navLinkAttrs = "";
            this.navLinkClass = "class=\"tab_navigation_";

            this.flags = NAVFLAG_VERTICAL_DISPLAY | NAVFLAG_EXPAND_MARGIN_RIGHT;
        }

        public void renderHtml(Writer writer, NavigationTree currentNavTree, NavigationContext nc, String rootUrl) throws IOException
        {
            List tabElements = currentNavTree.getSibilingList();

            if (tabElements == null || tabElements.isEmpty())
            {
                return;
            }
            //TODO: 1) think about images

            writer.write("   <table " + tableClass + " " + tableAttrs + ">");
            writer.write("       <tr>");
            writer.write("           <TD " + outerSeparatorClass + " " + outerSeparatorAttrs + "><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
            writer.write("       </tr>");
            writer.write("       <tr" + containerClass + " " + containerAttrs + ">");

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_LEFT))
            {
                writer.write("           <TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
            }

            for (int i = 0; i < tabElements.size(); i++)
            {
                NavigationTree tabElement = (NavigationTree) tabElements.get(i);
                if (tabElement.isVisible(nc))
                {
                    writer.write("<TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
                    writer.write("<TD " + navAttrs + " " + navClass + (tabElement.isInActivePath(nc) ? "on" : "off") + "\">");
                    writer.write("<a " + navLinkAttrs + " " + navLinkClass + (tabElement.isInActivePath(nc) ? "on" : "off") + "\" href=\"" + tabElement.getUrl(nc) + "\">" + tabElement.getCaption(nc) + "</a></TD>");
                }
            }

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_RIGHT))
            {
                writer.write("           <TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
            }

            writer.write("       </tr>");
            writer.write("       <tr>");
            writer.write("           <TD " + outerSeparatorClass + " " + outerSeparatorAttrs + "><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
            writer.write("       </tr>");
            writer.write("   </table>");
        }
    }

    public class VerticalCaptionsStyle extends NavigationStyle
    {
        public VerticalCaptionsStyle()
        {
            flags = NavigationStyle.NAVFLAG_VERTICAL_DISPLAY;
            tableAttrs = "cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=150";
            tableClass = "";
            outerSeparatorAttrs = "";
            outerSeparatorClass = "class=\"sidenav_off\"";
            outerSeparatorImgAttrs = "height=34 width=1 border=0 alt=\"\"";
            outerSeparatorImgClass = "";
            innerSeparatorAttrs = "colSpan=2";
            innerSeparatorClass = "class=\"sidenav_div\"";
            innerSeparatorImgAttrs = "height=1 width=1 border=0 alt=\"\"";
            innerSeparatorImgClass = "";
            navAttrs = "width=75";
            navClass = "class=\"sidenav_";
            navLinkClass = "class=\"sidenav_";
            navImgAttrs = "height=17 width=10 border=0 alt=\"\"";
        }

        public void renderHtml(Writer writer, NavigationTree currentNavTree, NavigationContext nc, String rootUrl) throws IOException
        {
            //render each appTab
            List sideBarElements = currentNavTree.getSibilingList();
            if (sideBarElements == null || sideBarElements.isEmpty())
            {
                return;
            }

            writer.write("      <TABLE " + tableClass + " " + tableAttrs + ">");
            writer.write("        <TR " + outerSeparatorClass + " " + outerSeparatorAttrs + ">");
            writer.write("          <TD><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
            writer.write("        </TR>");
            //writer.write("        <TR " + navStyle.outerSeparatorClass + " " + navStyle.outerSeparatorAttrs + ">");
            writer.write("        <TR>");
            writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD></TR>");
            writer.write("        </TR>");

            for (int i = 0; i < sideBarElements.size(); i++)
            {
                NavigationTree sideBarElement = (NavigationTree) sideBarElements.get(i);
                writer.write("        <TR " + navAttrs + " " + navClass + (sideBarElement.isInActivePath(nc) ? "on" : "off") + "\">");
                writer.write("          <TD><IMG " + navImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD>");
                writer.write("              <TD><A " + navLinkClass + (sideBarElement.isInActivePath(nc) ? "on" : "off") + "\" ");
                writer.write("            href=\"" + sideBarElement.getUrl(nc) + "\">" + sideBarElement.getCaption(nc) + "</A></TD>");
                writer.write("            </TR>");
                writer.write("        <TR>");
                writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD></TR>");
            }

            writer.write("        <TR>");
            writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + rootUrl + "/resources/images/pixel.gif\"></TD></TR></TABLE>");
        }
    }
}
