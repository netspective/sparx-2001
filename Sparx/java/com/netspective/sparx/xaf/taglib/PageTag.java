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
 * $Id: PageTag.java,v 1.2 2002-08-24 05:37:39 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogManager;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.security.AccessControlList;
import com.netspective.sparx.xaf.security.AccessControlListFactory;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xaf.security.LoginDialog;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.querydefn.QueryDefinition;
import com.netspective.sparx.xaf.querydefn.QuerySelectDialog;
import com.netspective.sparx.util.value.ServletValueContext;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;

public class PageTag extends javax.servlet.jsp.tagext.TagSupport
{
    static public final String PAGE_COMMAND_REQUEST_PARAM_NAME = "cmd";
    static public final String PAGE_SECURITY_MESSAGE_ATTRNAME = "security-message";
    static public final String PAGE_DEFAULT_LOGIN_DIALOG_CLASS = "com.netspective.sparx.xaf.security.LoginDialog";

    static public final String[] DIALOG_COMMAND_RETAIN_PARAMS =
            {
                PAGE_COMMAND_REQUEST_PARAM_NAME
            };

    static private com.netspective.sparx.xaf.security.LoginDialog loginDialog;

    private String title;
    private String heading;
    private String[] permissions;
    private long startTime;

    public void release()
    {
        super.release();
        title = null;
        heading = null;
        permissions = null;
    }

    public final String getTitle()
    {
        return title;
    }

    public final String getHeading()
    {
        return heading;
    }

    public void setTitle(String value)
    {
        title = value;
    }

    public void setHeading(String value)
    {
        heading = value;
    }

    public final String[] getPermissions()
    {
        return permissions;
    }

    public void setPermission(String value)
    {
        if(value == null || value.length() == 0)
            return;

        java.util.List perms = new java.util.ArrayList();
        java.util.StringTokenizer st = new java.util.StringTokenizer(value, ",");
        while(st.hasMoreTokens())
        {
            perms.add(st.nextToken());
        }
        permissions = (String[]) perms.toArray(new String[perms.size()]);
    }

    public String getLoginDialogClassName()
    {
        return PAGE_DEFAULT_LOGIN_DIALOG_CLASS;
    }

    public String getLoginDialogSkinName()
    {
        return null; // the "default" skin
    }

    protected boolean doLogin(javax.servlet.ServletContext servletContext, javax.servlet.Servlet page, javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws java.io.IOException, JspException
    {
        if(loginDialog == null)
        {
            String className = getLoginDialogClassName();
            try
            {
                Class loginDialogClass = Class.forName(className);
                loginDialog = (com.netspective.sparx.xaf.security.LoginDialog) loginDialogClass.newInstance();
            }
            catch(ClassNotFoundException e)
            {
                throw new javax.servlet.jsp.JspException("Login dialog class '" + className + "' not found in classpath.");
            }
            catch(IllegalAccessException e)
            {
                throw new javax.servlet.jsp.JspException("Unable to access login dialog class '" + className + "'.");
            }
            catch(InstantiationException e)
            {
                throw new javax.servlet.jsp.JspException("Unable to instantiate login dialog class '" + className + "'.");
            }
            loginDialog.initialize();
        }

        String logout = req.getParameter("_logout");
        if(logout != null)
        {
            com.netspective.sparx.util.value.ValueContext vc = new com.netspective.sparx.util.value.ServletValueContext(servletContext, page, req, resp);
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

        if(!loginDialog.accessAllowed(servletContext, req, resp))
        {
            String skinName = getLoginDialogSkinName();
            com.netspective.sparx.xaf.form.DialogContext dc = loginDialog.createContext(servletContext, page, req, resp, skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName));
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

    public boolean hasPermission()
    {
        if(permissions == null)
            return true;

        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());

        com.netspective.sparx.xaf.security.AuthenticatedUser user = (com.netspective.sparx.xaf.security.AuthenticatedUser) request.getSession(true).getAttribute("authenticated-user");
        if(user == null)
        {
            request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "No user identified.");
            return false;
        }

        com.netspective.sparx.xaf.security.AccessControlList acl = com.netspective.sparx.xaf.security.AccessControlListFactory.getACL(pageContext.getServletContext());
        if(acl == null)
        {
            request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "No ACL defined.");
            return false;
        }

        if(!user.hasAnyPermission(acl, permissions))
        {
            request.setAttribute(PAGE_SECURITY_MESSAGE_ATTRNAME, "Permission denied.");
            return false;
        }

        return true;
    }

    /**
     * When cmd=dialog is called, the expected parameters are:
     *   0 dialog name (required)
     *   1 data command like add,edit,delete,confirm (optional, may be empty or set to "-" to mean "none")
     *   2 skin name (optional, may be empty or set to "-" to mean "none")
     */

    public void handleDialogInBody(String[] params) throws javax.servlet.jsp.JspException
    {
        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        String dialogName = params[0];
        String dataCmd = params.length > 1 ? ("-".equals(params[1]) ? null : params[1]) : null;
        String skinName = params.length > 2 ? ("-".equals(params[2]) ? null : params[2]) : null;

        if(dataCmd != null)
            pageContext.getRequest().setAttribute(com.netspective.sparx.xaf.form.Dialog.PARAMNAME_DATA_CMD_INITIAL, dataCmd);

        try
        {
            javax.servlet.jsp.JspWriter out = pageContext.getOut();
            javax.servlet.ServletContext context = pageContext.getServletContext();
            com.netspective.sparx.xaf.form.DialogManager manager = com.netspective.sparx.xaf.form.DialogManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("DialogManager not found in ServletContext");
                return;
            }

            com.netspective.sparx.xaf.form.Dialog dialog = manager.getDialog(dialogName);
            if(dialog == null)
            {
                out.write("Dialog '" + dialogName + "' not found in manager '" + manager + "'.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + skinName + "' not found in skin factory.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(context, (javax.servlet.Servlet) pageContext.getPage(), (javax.servlet.http.HttpServletRequest) pageContext.getRequest(), (javax.servlet.http.HttpServletResponse) pageContext.getResponse(), skin);
            dc.setRetainRequestParams(DIALOG_COMMAND_RETAIN_PARAMS);
            dialog.prepareContext(dc);

            if(dc.inExecuteMode())
            {
                dialog.execute(out, dc);
                if(! dc.executeStageHandled())
                {
                    out.write("Dialog '" + dialogName + "' did not handle the execute mode.<p>");
                    out.write(dc.getDebugHtml());
                }
            }
            else
                dialog.renderHtml(out, dc, true);
        }
        catch(java.io.IOException e)
        {
            throw new javax.servlet.jsp.JspException(e.toString());
        }
    }

    /**
     * When cmd=qd-dialog is called, the expected parameters are:
     *   0 query definition name (required)
     *   1 dialog name (required)
     *   2 skin name (optional, may be empty or set to "-" to mean "none")
     */

    public void handleQuerySelectDialogInBody(String[] params) throws javax.servlet.jsp.JspException
    {
        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        String source = params[0];
        String dialogName = params[1];
        String skinName = params.length > 2 ? ("-".equals(params[2]) ? null : params[2]) : null;

        try
        {
            javax.servlet.jsp.JspWriter out = pageContext.getOut();
            javax.servlet.ServletContext context = pageContext.getServletContext();

            com.netspective.sparx.xaf.sql.StatementManager manager = com.netspective.sparx.xaf.sql.StatementManagerFactory.getManager(context);
            if(manager == null)
            {
                out.write("StatementManager not found in ServletContext");
                return;
            }

            com.netspective.sparx.xaf.querydefn.QueryDefinition queryDefn = manager.getQueryDefn(source);
            if(queryDefn == null)
            {
                out.write("QueryDefinition '" + source + "' not found in StatementManager");
                return;
            }

            com.netspective.sparx.xaf.querydefn.QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogName);
            if(dialog == null)
            {
                out.write("QuerySelectDialog '" + dialogName + "' not found in QueryDefinition '" + source + "'");
                return;
            }

            com.netspective.sparx.xaf.form.DialogSkin skin = skinName == null ? com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin() : com.netspective.sparx.xaf.skin.SkinFactory.getDialogSkin(skinName);
            if(skin == null)
            {
                out.write("DialogSkin '" + skinName + "' not found in skin factory.");
                return;
            }

            com.netspective.sparx.xaf.form.DialogContext dc = dialog.createContext(pageContext.getServletContext(), (javax.servlet.Servlet) pageContext.getPage(), (javax.servlet.http.HttpServletRequest) pageContext.getRequest(), (javax.servlet.http.HttpServletResponse) pageContext.getResponse(), skin);
            dc.setRetainRequestParams(DIALOG_COMMAND_RETAIN_PARAMS);
            dialog.prepareContext(dc);

            dialog.renderHtml(out, dc, true);
            return;
        }
        catch(java.io.IOException e)
        {
            throw new javax.servlet.jsp.JspException(e.toString());
        }
    }

    public boolean handleDefaultBodyItem() throws javax.servlet.jsp.JspException
    {
        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        String pageCmdParam = request.getParameter(PAGE_COMMAND_REQUEST_PARAM_NAME);
        if(pageCmdParam == null)
            return false;

        java.util.StringTokenizer st = new java.util.StringTokenizer(pageCmdParam, ",");
        String pageCmd = st.nextToken();
        java.util.List pageCmdParamsList = new java.util.ArrayList();
        while(st.hasMoreTokens())
            pageCmdParamsList.add(st.nextToken());

        String[] pageCmdParamsArray = null;
        if(pageCmdParamsList.size() > 0)
            pageCmdParamsArray = (String[]) pageCmdParamsList.toArray(new String[pageCmdParamsList.size()]);

        // a "standard" page command needs to be handled
        if(pageCmd.equals("dialog"))
            handleDialogInBody(pageCmdParamsArray);
        else if(pageCmd.equals("qd-dialog"))
            handleQuerySelectDialogInBody(pageCmdParamsArray);
        else
        {
            try
            {
                pageContext.getResponse().getWriter().write("Page command '" + pageCmd + "' not recognized.");
            }
            catch(java.io.IOException e)
            {
                throw new javax.servlet.jsp.JspException(e.toString());
            }
            return false;
        }

        return true;
    }

    public int doStartTag() throws javax.servlet.jsp.JspException
    {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws javax.servlet.jsp.JspException
    {
        return EVAL_PAGE;
    }

    /**
     * Records the start time when the page is loaded
     */
    public void doPageBegin()
    {
        startTime = new java.util.Date().getTime();
        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        org.apache.log4j.NDC.push(request.getSession(true).getId());
    }

    /**
     * Records the total time when the page is finished loading
     */
    public void doPageEnd()
    {
        javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest) pageContext.getRequest());
        com.netspective.sparx.util.log.LogManager.recordAccess(request, null, pageContext.getPage().getClass().getName(), request.getRequestURI(), startTime);
        org.apache.log4j.NDC.pop();
    }

    /**
     * Used by Sparx sample applications to show the shell
     */
    public void doSamplePageBegin(String styleSheet) throws IOException
    {
        Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(pageContext.getServletContext());
        ServletValueContext svc = new ServletValueContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());

        String sparxACEUrl = config.getTextValue(svc, "sparx.ace.root-url");
        String sparxSampleImagesUrl = config.getTextValue(svc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "images-url") + "/samples";

        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        out.println("<html>");
        out.println("	<head>");
        out.println("		<meta http-equiv='content-type' content='text/html;charset=ISO-8859-1'>");
        out.println("		<title>"+ getTitle() +"</title>");
        if(styleSheet != null)
            out.println("		<link rel='stylesheet' href='"+ styleSheet +"'>");
        out.println("	</head>");
        out.println("");
        out.println("	<body bgcolor='#cccccc' leftmargin='5' marginheight='5' marginwidth='5' topmargin='5'>");
        out.println("		<basefont face='Trebuchet MS' size=2>");
        out.println("		<center>");
        out.println("		<table width='100%' border='0' cellspacing='0' cellpadding='0' height='100%'>");
        out.println("			<tr height='56'>");
        out.println("				<td align='left' valign='top' height='56'>");
        out.println("					<table width='100%' border='0' cellspacing='0' cellpadding='0'>");
        out.println("						<tr>");
        out.println("							<td align='left' valign='top' width='412'><a href='http://www.netspective.com'><img src='"+ sparxSampleImagesUrl +"/sample-apps-01.gif' alt='' width='412' height='56' border='0'></a></td>");
        out.println("							<td align='left' valign='top' width='100%'><img src='"+ sparxSampleImagesUrl +"/sample-apps-02.gif' alt='' width='100%' height='56' border='0'></td>");
        out.println("							<td align='left' valign='top' width='181'>");
        out.println("								<table width='64' border='0' cellspacing='0' cellpadding='0'>");
        out.println("									<tr>");
        out.println("										<td align='left' valign='top'><img src='"+ sparxSampleImagesUrl +"/sample-apps-03.gif' alt='' width='181' height='9' border='0'></td>");
        out.println("									</tr>");
        out.println("									<tr>");
        out.println("										<td align='left' valign='top'>");
        out.println("											<table width='72' border='0' cellspacing='0' cellpadding='0'>");
        out.println("												<tr>");
        out.println("													<td align='left' valign='top'><img src='"+ sparxSampleImagesUrl +"/sample-apps-04.gif' alt='' width='6' height='47' border='0'></td>");
        out.println("													<td align='left' valign='top'>");
        out.println("														<table width='64' border='0' cellspacing='0' cellpadding='0'>");
        out.println("															<tr height='17'>");
        out.println("																<td align='center' valign='middle' bgcolor='white' height='17'><font size=1 face='Arial,Helvetica,Geneva,Swiss,SunSans-Regular'><a href='"+ sparxACEUrl +"'>Admin Console (ACE)</a></font></td>");
        out.println("															</tr>");
        out.println("															<tr>");
        out.println("																<td align='left' valign='top'><img src='"+ sparxSampleImagesUrl +"/sample-apps-07.gif' alt='' width='156' height='30' border='0'></td>");
        out.println("															</tr>");
        out.println("														</table>");
        out.println("													</td>");
        out.println("													<td align='left' valign='top'><img src='"+ sparxSampleImagesUrl +"/sample-apps-06.gif' alt='' width='19' height='47' border='0'></td>");
        out.println("												</tr>");
        out.println("											</table>");
        out.println("										</td>");
        out.println("									</tr>");
        out.println("								</table>");
        out.println("							</td>");
        out.println("						</tr>");
        out.println("					</table>");
        out.println("				</td>");
        out.println("			</tr>");
        out.println("			<tr height='100%'>");
        out.println("				<td align='left' valign='top' height='100%'>");
        out.println("					<table width='100%' border='0' cellspacing='0' cellpadding='0' height='100%'>");
        out.println("						<tr height='100%'>");
        out.println("							<td align='left' valign='top' width='15' height='100%' background='"+ sparxSampleImagesUrl +"/sample-apps-08.gif'><img src='"+ sparxSampleImagesUrl +"/sample-apps-spacer.gif' alt='' width='15' height='100%' border='0'></td>");
        out.println("							<td align='left' valign='top' width='100%' height='100%' bgcolor='white'>");
    }

    /**
     * Used by Sparx sample applications to show the shell
     */
    public void doSamplePageEnd() throws IOException
    {
        Configuration config = ConfigurationManagerFactory.getDefaultConfiguration(pageContext.getServletContext());
        ServletValueContext svc = new ServletValueContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());
        String sparxSampleImagesUrl = config.getTextValue(svc, com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX + "images-url") + "/samples";

        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        out.println("                         <p><center><font size=2 color=silver>"+ com.netspective.sparx.BuildConfiguration.getProductBuild() + "</font></center>");
        out.println("							</td>");
        out.println("							<td align='left' valign='top' width='19' height='100%' background='"+ sparxSampleImagesUrl +"/sample-apps-10.gif'><img src='"+ sparxSampleImagesUrl +"/sample-apps-spacer.gif' alt='' width='19' height='100%' border='0'></td>");
        out.println("						</tr>");
        out.println("					</table>");
        out.println("				</td>");
        out.println("			</tr>");
        out.println("			<tr height='26'>");
        out.println("				<td align='left' valign='top' height='26'>");
        out.println("					<table width='100%' border='0' cellspacing='0' cellpadding='0'>");
        out.println("						<tr valign='bottom'>");
        out.println("							<td align='left' valign='top' width='412'><a href='http://www.netspective.com'><img src='"+ sparxSampleImagesUrl +"/sample-apps-11.gif' alt='' width='412' height='26' border='0'></a></td>");
        out.println("							<td align='left' valign='top' width='100%'><img src='"+ sparxSampleImagesUrl +"/sample-apps-12.gif' alt='' width='100%' height='26' border='0'></td>");
        out.println("							<td align='left' valign='top' width='181'><a href='http://developer.netspective.com'><img src='"+ sparxSampleImagesUrl +"/sample-apps-13.gif' alt='' width='181' height='26' border='0'></a></td>");
        out.println("						</tr>");
        out.println("					</table>");
        out.println("				</td>");
        out.println("			</tr>");
        out.println("		</table>");
        out.println("		</center>");
        out.println("	</body>");
        out.println("</html>");
    }
}
