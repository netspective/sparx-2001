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
 * 4. The names "Netspective", "Sparx", and "Junxion" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Netspective where possible. We suggest using
 *    the "powered by" button or creating a "powered by" link to
 *    http://www.netspective.com for each application using this code.
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
 * $Id: AppNavigationSkin.java,v 1.1 2003-01-09 03:37:51 shahid.shah Exp $
 */

package app.skin;

import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.skin.HtmlTabbedNavigationSkin;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;

public class AppNavigationSkin extends HtmlTabbedNavigationSkin
{
    public void renderPageMasthead(Writer writer, NavigationPathContext nc) throws IOException
    {
        writer.write("<body>");

        renderAuthenticatedUser(writer, nc);

        writer.write("<!-- Application Header Ends -->");

        writer.write("<!-- Master Header Begins -->");
        writer.write("   <TABLE class=\"mast_header\" cellSpacing=0 cellPadding=0 width=\"100%\" border=0>");
        writer.write("      <TR>");
        writer.write("	        <TD>&nbsp;<A class=\"app_name\" href=\"" + nc.getRootUrl() + "/index.jsp\"><b>Covenant</b> Requirements Management System</A></TD>");
        writer.write("       </TR>");

        writer.write("      <TR>");
        writer.write("	        <TD>");
        writer.write("<!-- Master Header Ends -->");
    }
}
