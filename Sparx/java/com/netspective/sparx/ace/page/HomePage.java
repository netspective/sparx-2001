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
 * $Id: HomePage.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import com.netspective.sparx.BuildConfiguration;
import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.page.PageContext;
import com.netspective.sparx.xaf.page.PageControllerServlet;

public class HomePage extends AceServletPage
{
    public final String getName()
    {
        return "home";
    }

    public final String getPageIcon()
    {
        return "home.gif";
    }

    public final String getCaption(PageContext pc)
    {
        return "ACE Home";
    }

    public final String getHeading(PageContext pc)
    {
        return "Welcome to ACE";
    }

    public void handlePageBody(PageContext pc) throws ServletException, IOException
    {
        String sharedImagesRootURL = ((PageControllerServlet) pc.getServlet()).getSharedImagesRootURL();
        try
        {
            PrintWriter out = pc.getResponse().getWriter();

            out.print("<table cool border='0' cellpadding='0' cellspacing='0'>");
            out.print("<tr height='304'>");
            out.print("	<td width='3590' height='304' colspan='2' valign='top' align='left' xpos='0'><img src='" + sharedImagesRootURL + "/ace/home-main.gif' width='800' height='304' border='0'></td>");
            out.print("	<td width='1' height='304'><spacer type='block' width='1' height='304'></td>");
            out.print("</tr>");
            out.print("<tr height='214'>");
            out.print("	<td width='269' height='214' valign='top' align='left' xpos='0'>");
            out.print("		<table border='0' cellpadding='0' cellspacing='0' width='269' height='214' background='" + sharedImagesRootURL + "/ace/home-bottom-left.gif' align='left'>");
            out.print("			<tr>");
            out.print("				<td valign='top' align='left'>");
            out.print("					<div align='right'>");
            out.print("						<table border='0' cellpadding='0' cellspacing='0' height='45' width='269'>");
            out.print("							<tr>");
            out.print("								<td valign='middle' align='right' class='list'>");
            out.print("									<div align='right'>");
            out.print("										<span class='dialog_control'><b>" + com.netspective.sparx.util.config.ConfigurationManagerFactory.getExecutionEvironmentName(pc.getServletContext()) + " </b>Environment<b><br>" +
                    BuildConfiguration.getProductBuild() + "</b><br>");
            out.print("											");
            out.print("											 by Netspective Corp.</span></div>");
            out.print("								</td>");
            out.print("								<td valign='middle' align='right' width='102'><img src='" + sharedImagesRootURL + "/ace/spacer.gif' width='1' height='1' border='0'></td>");
            out.print("							</tr>");
            out.print("						</table>");
            out.print("						<table border='0' cellpadding='0' cellspacing='0' height='45' width='269'>");
            out.print("							<tr>");
            out.print("								<td valign='middle' align='right' class='list'>");
            out.print("									<div align='right'>");
            out.print("										");
            out.print("										Running on " + System.getProperty("os.name") + " v. " + System.getProperty("os.version") + "<br>");
            out.print("										");
            out.print("										 Java Version " + System.getProperty("java.version") + "<br>");
            out.print("										");
            out.print("										 by " + System.getProperty("java.vendor") + "</div>");
            out.print("								</td>");
            out.print("								<td valign='middle' align='right' width='82'><img src='" + sharedImagesRootURL + "/ace/spacer.gif' width='1' height='1' border='0'></td>");
            out.print("							</tr>");
            out.print("						</table>");
            out.print("						<table border='0' cellpadding='0' cellspacing='0' height='45' width='269'>");
            out.print("							<tr>");
            out.print("								<td valign='middle' align='right' class='list'>");
            out.print("									<div align='right'>");
            out.print("										");
            out.print("										" + System.getProperty("java.vm.name") + "<br>");
            out.print("										");
            out.print("										Version " + System.getProperty("java.vm.version") + "<br>");
            out.print("										");
            out.print("										by " + System.getProperty("java.vm.vendor") + "</div>");
            out.print("								</td>");
            out.print("								<td valign='middle' align='right' width='50'><img src='" + sharedImagesRootURL + "/ace/spacer.gif' width='1' height='1' border='0'></td>");
            out.print("							</tr>");
            out.print("						</table>");
            out.print("					</div>");
            out.print("				</td>");
            out.print("			</tr>");
            out.print("		</table>");
            out.print("	</td>");
            out.print("	<td width='3321' height='214' valign='top' align='left' xpos='269'><img src='" + sharedImagesRootURL + "/ace/home-bottom-right.gif' width='531' height='214' border='0'></td>");
            out.print("	<td width='1' height='214'><spacer type='block' width='1' height='214'></td>");
            out.print("</tr>");
            out.print("<tr height='8567'>");
            out.print("	<td width='3591' height='8568' colspan='3' rowspan='2' valign='top' align='left' xpos='0'>");
            out.print("		<table border='0' cellpadding='0' cellspacing='0' width='100%' background='" + sharedImagesRootURL + "/ace/2tone.gif' height='100%'>");
            out.print("			<tr>");
            out.print("				<td><img src='" + sharedImagesRootURL + "/ace/spacer.gif' width='1' height='1' border='0'></td>");
            out.print("			</tr>");
            out.print("		</table>");
            out.print("	</td>");
            out.print("</tr>");
            out.print("<tr height='1' cntrlrow></tr>");
            out.print("</table>");
        }
        catch(IOException e)
        {
            throw new ServletException(e);
        }
    }
}