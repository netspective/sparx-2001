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
 * $Id: AppDialogsPage.java,v 1.8 2002-12-28 20:07:36 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogManager;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.html.command.DialogComponentCommand;
import com.netspective.sparx.xaf.html.ComponentCommandFactory;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationPath;
import com.netspective.sparx.xaf.navigate.NavigationPageException;
import com.netspective.sparx.util.value.ValueContext;

public class AppDialogsPage extends AceServletPage
{
    DialogBeanGenerateClassDialog dialog;

    public final String getName()
    {
        return "dialogs";
    }

    public final String getEntityImageUrl()
    {
        return "dialogs.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return "Dialogs";
    }

    public final String getHeading(ValueContext vc)
    {
        return "Application Dialogs";
    }

    public void handleBeanGenerator(NavigationPathContext nc) throws IOException
    {
        if(dialog == null)
            dialog = new DialogBeanGenerateClassDialog();

        PrintWriter out = nc.getResponse().getWriter();
        DialogContext dc = dialog.createContext(nc.getServletContext(), nc.getServlet(), (HttpServletRequest) nc.getRequest(), (HttpServletResponse) nc.getResponse(), SkinFactory.getDialogSkin());
        dialog.prepareContext(dc);
        if(!dc.inExecuteMode())
        {
            out.write("&nbsp;<p><center>");
            dialog.renderHtml(out, dc, true);
            out.write("</center>");
        }
        else
            dialog.renderHtml(out, dc, true);
    }

    public void handlePageBody(Writer writer, NavigationPathContext nc) throws NavigationPageException, IOException
    {
        ServletContext context = nc.getServletContext();
        DialogManager manager = DialogManagerFactory.getManager(context);
        manager.addMetaInfoOptions();

        String testItem = getTestCommandItem(nc);
        if(testItem != null)
        {
            PrintWriter out = nc.getResponse().getWriter();
            DialogComponentCommand dcmd = ComponentCommandFactory.getDialogCommand(testItem);
            NavigationPath.FindResults path = nc.getActivePathFindResults();

            handleUnitTestPageBegin(writer, nc, "Form (Dialog) Unit Test");
            out.write("<h1>Form (Dialog) Unit Test: " + dcmd.getDialogName() + "</h1><p>");
            try
            {
                dcmd.handleCommand(nc, nc.getResponse().getWriter(), true);
            }
            catch (ComponentCommandException e)
            {
                throw new NavigationPageException(e);
            }
            out.write("<p>");
            out.write("Try out additional options by using the following format:<br>");
            out.write("<code>"+ path.getMatchedPath().getAbsolutePath() +"/test/dialogId,data-cmd,skin-name,debug-flags</code><p>");

            dcmd.setDataCmd("add");
            dcmd.setSkinName("standard");
            out.write("For example, to try the dialog in 'add' mode using the 'standard' skin:<br>");
            out.write("<a href='"+ dcmd.getCommand() +"'>"+ path.getMatchedPath().getAbsolutePath() +"/test/" + dcmd.getCommand() + "</a><p>");

            dcmd.setDataCmd("edit");
            dcmd.setSkinName(null);
            out.write("To try the dialog in 'edit' mode using the default skin:<br>");
            out.write("<a href='"+ dcmd.getCommand() +"'>"+ path.getMatchedPath().getAbsolutePath() +"/test/" + dcmd.getCommand() + "</a><p>");
            handleUnitTestPageEnd(writer, nc);
        }
        else
        {
            NavigationPath.FindResults results = nc.getActivePathFindResults();
            String[] unmatchedItems = results.unmatchedPathItems();
            if(unmatchedItems != null && unmatchedItems[0].equals("generate-dc"))
                handleBeanGenerator(nc);
            else
            {
                transform(nc, manager.getDocument(context, null), com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "ui-browser-xsl");
            }
        }
    }
}
