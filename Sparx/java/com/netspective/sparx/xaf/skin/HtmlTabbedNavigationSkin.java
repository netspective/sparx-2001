/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: HtmlTabbedNavigationSkin.java,v 1.6 2002-12-30 14:08:25 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.skin;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.servlet.Servlet;

import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.navigate.NavigationPathSkin;
import com.netspective.sparx.xaf.navigate.NavigationPath;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.BuildConfiguration;

import org.w3c.dom.Element;

public class HtmlTabbedNavigationSkin implements NavigationPathSkin
{
    protected NavigationStyle levelOneStyle;
    protected NavigationStyle levelTwoStyle;
    protected NavigationStyle levelThreeStyle;
    protected String cssFileName;
    protected String imagesBasePath;

    public HtmlTabbedNavigationSkin()
    {
        levelOneStyle = new HorizontalImagesStyle();
        levelTwoStyle = new HorizontalCaptionsStyle();
        levelThreeStyle = new VerticalCaptionsStyle();
        cssFileName = "/sparx/resources/css/tabbed-navigation-skin.css";
        imagesBasePath = "/resources/skin/tabbed-navigation";
    }

    public NavigationPathContext createContext(javax.servlet.jsp.PageContext jspPageContext, NavigationPath tree, String navTreeId, boolean popup)
    {
        NavigationPathContext result = new NavigationPathContext(tree,
                jspPageContext.getServletContext(),
                (Servlet) jspPageContext.getPage(),
                jspPageContext.getRequest(),
                jspPageContext.getResponse(),
                this, navTreeId);
        if(popup) result.setPopup(true);
        return result;
    }

    public void renderPageMetaData(Writer writer, NavigationPathContext nc) throws IOException
    {
        NavigationPath activePath = nc.getActivePath();

        writer.write("<!-- Application Header Begins -->\n");
        writer.write("<html>\n");
        writer.write("<head>\n");
        writer.write("<title>" + activePath.getTitle(nc) + "</title>\n");
        writer.write("	<link rel=\"stylesheet\" href=\"" + nc.getRootUrl() + cssFileName +"\" type=\"text/css\">\n");
        writer.write("</head>\n");
    }

    public void renderPageMasthead(Writer writer, NavigationPathContext nc) throws IOException
    {
        writer.write("<body>");
        AuthenticatedUser authUser = (AuthenticatedUser) nc.getSession().getAttribute("authenticated-user");
        if(authUser != null)
        {
            String personName = authUser != null ? authUser.getUserId() : "Not logged in";
            String personId = authUser != null ? authUser.getUserName() : "Not logged in";
            String orgName = authUser != null ? authUser.getUserOrgId() : "Not logged in";
            String orgId = authUser != null ? authUser.getUserOrgName() : "Not logged in";

            writer.write("	<table class=\"app_header\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
            writer.write("	    <tr>");
            writer.write("           <td class=\"app_header\" nowrap height=\"20\">");
            writer.write("	            <img src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-marker.gif\">&nbsp;USER:&nbsp;<a class=\"app_header\" href=\"" + nc.getRootUrl() + "/person/summary.jsp?person_id=" + personId + "\">" + personName.toUpperCase() + "</a>");
            writer.write("           </td>");
            writer.write("           <td class=\"app_header\" nowrap>");
            writer.write("               <img src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-marker.gif\">&nbsp;ORG:&nbsp;<a class=\"app_header\" href=\"" + nc.getRootUrl() + "/org/summary.jsp?org_id=" + orgId + "\">" + orgName.toUpperCase() + "</a>");
            writer.write("           </td>");
            writer.write("           <td width=\"100%\" class=\"app_header\" nowrap>");
            writer.write("	            <img src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\">");
            writer.write("           </td>");
            writer.write("           <td class=\"app_header\" nowrap>");
            writer.write("           <img src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-marker.gif\"><a class=\"app_header\" href=\"" + nc.getRootUrl() + "?_logout=yes\">&nbsp;LOGOUT</a>");
            writer.write("           </td>");
            writer.write("	    </tr>");
            writer.write("   </table>");
        }

        writer.write("<!-- Application Header Ends -->");

        writer.write("<!-- Master Header Begins -->");
        writer.write("   <TABLE class=\"mast_header\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0>");
        writer.write("      <TR>");
        writer.write("	        <TD>&nbsp;<A class=\"app_name\" href=\"" + nc.getRootUrl() + "/index.jsp\">"+ nc.getApplicationName(nc) +"</A></TD>");
        writer.write("	        <TD width=\"100%\"><IMG height=25 src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\" width=10 border=0></TD>");
        writer.write("			<TD rowspan=\"2\"><!-- space for image on right --></TD>");
        writer.write("       </TR>");

        writer.write("      <TR>");
        writer.write("	        <TD>");
        writer.write("<!-- Master Header Ends -->");
    }

    public void renderPageMenusLevelOne(Writer writer, NavigationPathContext nc) throws IOException
    {
        writer.write("<!-- App Tabs Begins -->");

        NavigationPath activePath = nc.getActivePath();
        levelOneStyle.renderHtml(writer,
                activePath.getLevel() == 1 ? activePath : (NavigationPath) nc.getActivePath().getAncestorsList().get(1), nc);

        writer.write("<!-- App Tabs Ends -->");

        writer.write("	        </TD>");
        writer.write("         <TD><IMG height=15 alt=\"\" src=\"" + nc.getRootUrl() + "/resources/images/design/app-tabs/apptab_div.gif\" width=\"100%\" border=0></TD>");
        writer.write("      </TR>");
        writer.write("   </TABLE>");
    }

    public void renderPageMenusLevelTwo(Writer writer, NavigationPathContext nc) throws IOException
    {
        writer.write("<!-- Function Tabs Begins -->");
        NavigationPath activePath = nc.getActivePath();
        switch(activePath.getLevel())
        {
            case 1:
                List activePathChildren = activePath.getChildrenList();
                if(activePath.getMaxLevel() > 1 && activePathChildren.size() > 0)
                    levelTwoStyle.renderHtml(writer, (NavigationPath) activePath.getChildrenList().get(0), nc);
                break;

            case 2:
                levelTwoStyle.renderHtml(writer, activePath, nc);
                break;

            case 3:
                levelTwoStyle.renderHtml(writer, (NavigationPath) activePath.getAncestorsList().get(2), nc);
                break;
        }
        writer.write("<!-- Function Tabs Ends -->");
    }

    public void renderPageMenusLevelThree(Writer writer, NavigationPathContext nc) throws IOException
    {
        NavigationPath activePath = nc.getActivePath();
        switch(activePath.getLevel())
        {
            case 2:
                List activePathChildren = activePath.getChildrenList();
                if(activePath.getMaxLevel() > 2 && activePathChildren.size() > 0)
                {
                    writer.write("    <TD vAlign=top height=\"100%\" rowspan=2>");
                    levelThreeStyle.renderHtml(writer, (NavigationPath) activePath.getChildrenList().get(0), nc);
                    writer.write("    </TD>");
                }
                break;

            case 3:
                writer.write("    <TD vAlign=top height=\"100%\" rowspan=2>");
                levelThreeStyle.renderHtml(writer, activePath, nc);
                writer.write("    </TD>");
                break;
        }

        writer.write("   <TD class=\"page_content\" vAlign=top align=\"center\" width=\"100%\">");
    }

    public void renderPageHeader(Writer writer, NavigationPathContext nc) throws IOException
    {
        renderPageMasthead(writer, nc);
        renderPageMenusLevelOne(writer, nc);
        renderPageMenusLevelTwo(writer, nc);

        NavigationPath activePath = nc.getActivePath();

        writer.write("<!-- Page Heading Begins -->");
        writer.write("<TABLE class=\"page_header\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0>");
        writer.write("<TR>");

        String actionIcon = nc.getRootUrl() + imagesBasePath + activePath.getId() + "/action-icon.gif";
        if (actionIcon != null && actionIcon.length() > 0)
            writer.write("<td>&nbsp;<img src=\"" + actionIcon + "\"></td>");

        //Page Heading
        writer.write("<TD nowrap class=page_header height=\"30\">" + activePath.getHeading(nc) + "</TD>");
        writer.write("<td><img src=\"" + nc.getRootUrl() + "/resources/images/design/page-header/page-heading-middle.gif\"></td>");
        writer.write("<TD width=\"100%\" background=\"" + nc.getRootUrl() + "/resources/images/design/page-header/page-heading-background.gif\"><IMG height=3 alt=\"\" src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\" width=10 border=0></TD>");

        // Select the entity icon that goes on the right of the Page Heading.
        String entityIcon = nc.getRootUrl() + imagesBasePath + activePath.getId() + "/entity-icon.gif";
        if (entityIcon != null && entityIcon.length() > 0)
            writer.write("<td><img src=\"" + entityIcon + "\"></td>");

        writer.write("</TR></TABLE>");
        writer.write("<!-- Page Heading Ends -->");

        writer.write("<TABLE class=\"main\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0 height=\"100%\">");
        writer.write("  <TR>");

        renderPageMenusLevelThree(writer, nc);
    }

    public void renderPageFooter(Writer writer, NavigationPathContext nc) throws IOException
    {
        String sparxResourcesUrl = nc.getRootUrl() + "/sparx/resources";

        writer.write("   </TD></TR>");
        writer.write("   <tr><td valign=bottom align=center class=\"page_content\"><table cellpadding=3>");
        writer.write("        <tr>");
        writer.write("                <td align=center class=\"power_by_sparx_footer\"> ");
        writer.write("                        <a target=\"netspective\" href=\"http://www.netspective.com/\"> ");
        writer.write("                        <img border=\"0\" alt=\"Powered by Netspective Sparx\" src=\"" + sparxResourcesUrl + "/images/powered-by-sparx.gif\">");
        writer.write("                        </a>");
        writer.write("                </td> ");
        writer.write("                <td align=center class=\"copyright_footer\"> ");
        writer.write("                        <a href='"+ nc.getRootUrl() +"/ace'>"+ BuildConfiguration.getVersionAndBuild() + "</a>. &copy; Netspective.");
        writer.write("                </td> ");
        writer.write("        </tr></table>");
        writer.write("   </td></tr></table>");
        writer.write("</body>");
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

        abstract public void renderHtml(Writer writer, NavigationPath currentNavTree, NavigationPathContext nc) throws IOException;
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

        public void renderHtml(Writer writer, NavigationPath currentNavTree, NavigationPathContext nc) throws IOException
        {
            //render each appTab
            List tabElements = currentNavTree.getSibilingList();

            writer.write("<TABLE " + tableClass + " " + tableAttrs + ">");
            //TODO: add flag to see wether we need an outer separator and how many of them.
            writer.write("<TR " + containerClass + " " + containerAttrs + ">");
            for (int i = 0; i < tabElements.size(); i++)
            {

                NavigationPath tabElement = (NavigationPath) tabElements.get(i);
                if (tabElement.isVisible(nc))
                {
                    writer.write("<TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgAttrs + " ");
                    writer.write("src=\"" + nc.getRootUrl() + imagesBasePath + tabElement.getId() + "/tab_separator.gif" + "\"></TD>");
                    writer.write("<TD " + navAttrs + ">");
                    writer.write("<A " + navLinkAttrs + " href=\"" + tabElement.getUrl(nc) + "\">");
                    writer.write("<IMG " + navImgAttrs + " ");
                    writer.write("src=\"" + nc.getRootUrl() + imagesBasePath + tabElement.getId() + "/tab_" + (tabElement.isInActivePath(nc) ? "on" : "off") + ".gif" + "\"></A></TD>");
                }
            }

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_RIGHT))
            {
                writer.write("<TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\" ></TD>");
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

            this.flags = NAVFLAG_EXPAND_MARGIN_RIGHT;
        }

        public void renderHtml(Writer writer, NavigationPath currentNavTree, NavigationPathContext nc) throws IOException
        {
            List tabElements = currentNavTree.getSibilingList();

            if (tabElements == null || tabElements.isEmpty())
            {
                return;
            }
            //TODO: 1) think about images

            writer.write("   <table " + tableClass + " " + tableAttrs + ">");
            writer.write("       <tr>");
            writer.write("           <TD " + outerSeparatorClass + " " + outerSeparatorAttrs + "><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
            writer.write("       </tr>");
            writer.write("       <tr" + containerClass + " " + containerAttrs + ">");

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_LEFT))
            {
                writer.write("           <TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
            }

            for (int i = 0; i < tabElements.size(); i++)
            {
                NavigationPath tabElement = (NavigationPath) tabElements.get(i);
                if (tabElement.isVisible(nc))
                {
                    writer.write("<TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
                    writer.write("<TD " + navAttrs + " " + navClass + (tabElement.isInActivePath(nc) ? "on" : "off") + "\">");
                    writer.write("<a " + navLinkAttrs + " " + navLinkClass + (tabElement.isInActivePath(nc) ? "on" : "off") + "\" href=\"" + tabElement.getUrl(nc) + "\">" + tabElement.getCaption(nc) + "</a></TD>");
                }
            }

            if (flagIsSet(NavigationStyle.NAVFLAG_EXPAND_MARGIN_RIGHT))
            {
                writer.write("           <TD width=\"100%\"><IMG " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
            }

            writer.write("       </tr>");
            writer.write("       <tr>");
            writer.write("           <TD " + outerSeparatorClass + " " + outerSeparatorAttrs + "><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
            writer.write("       </tr>");
            writer.write("   </table>");
        }
    }

    public class VerticalCaptionsStyle extends NavigationStyle
    {
        public VerticalCaptionsStyle()
        {
            flags = NavigationStyle.NAVFLAG_VERTICAL_DISPLAY;
            tableAttrs = "cellspacing=\"0\" cellpadding=\"0\" border=\"0\"";
            tableClass = "";
            outerSeparatorAttrs = "";
            outerSeparatorClass = "class=\"sidenav_off\"";
            outerSeparatorImgAttrs = "height=34 width=1 border=0 alt=\"\"";
            outerSeparatorImgClass = "";
            innerSeparatorAttrs = "colSpan=3";
            innerSeparatorClass = "class=\"sidenav_div\"";
            innerSeparatorImgAttrs = "height=1 width=1 border=0 alt=\"\"";
            innerSeparatorImgClass = "";
            navAttrs = "width=75";
            navClass = "class=\"sidenav_";
            navLinkClass = "class=\"sidenav_";
            navImgAttrs = "height=17 width=10 border=0 alt=\"\"";
        }

        public void renderHtml(Writer writer, NavigationPath currentNavTree, NavigationPathContext nc) throws IOException
        {
            List sideBarElements = currentNavTree.getSibilingList();
            if (sideBarElements == null || sideBarElements.isEmpty())
            {
                return;
            }

            writer.write("      <TABLE " + tableClass + " " + tableAttrs + ">");
            writer.write("        <TR " + outerSeparatorClass + " " + outerSeparatorAttrs + ">");
            writer.write("          <TD><IMG " + outerSeparatorImgClass + " " + outerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
            writer.write("        </TR>");
            writer.write("        <TR>");
            writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD></TR>");
            writer.write("        </TR>");

            for (int i = 0; i < sideBarElements.size(); i++)
            {
                NavigationPath sideBarElement = (NavigationPath) sideBarElements.get(i);
                writer.write("        <TR " + navAttrs + " " + navClass + (sideBarElement.isInActivePath(nc) ? "on" : "off") + "\">");
                writer.write("          <TD border=0><IMG " + navImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
                writer.write("              <TD><A " + navLinkClass + (sideBarElement.isInActivePath(nc) ? "on" : "off") + "\" ");
                writer.write("            href=\"" + sideBarElement.getUrl(nc) + "\"><nobr>" + sideBarElement.getCaption(nc) + "</nobr></A></TD>");
                writer.write("          <TD border=0><IMG " + navImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD>");
                writer.write("            </TR>");
                writer.write("        <TR>");
                writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD></TR>");
            }

            writer.write("        <TR>");
            writer.write("          <TD " + innerSeparatorClass + " " + innerSeparatorAttrs + "><IMG " + innerSeparatorImgClass + " " + innerSeparatorImgAttrs + " src=\"" + nc.getRootUrl() + "/resources/images/design/app-header/app-header-spacer.gif\"></TD></TR></TABLE>");
        }
    }
}
