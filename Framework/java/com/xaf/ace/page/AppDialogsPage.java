package com.xaf.ace.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.ace.*;
import com.xaf.form.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class AppDialogsPage extends AceServletPage
{
    DialogBeanGenerateClassDialog dialog;

	public final String getName() { return "dialogs"; }
	public final String getPageIcon() { return "dialogs.gif"; }
	public final String getCaption(PageContext pc) { return "Dialogs"; }
	public final String getHeading(PageContext pc) { return "Application Dialogs"; }

    public void handleBeanGenerator(PageContext pc) throws IOException
    {
        if(dialog == null)
            dialog = new DialogBeanGenerateClassDialog();

        PrintWriter out = pc.getResponse().getWriter();
		DialogContext dc = dialog.createContext(pc.getServletContext(), pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
		}
        else
            out.write(dialog.getHtml(dc, true));
    }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		manager.addMetaInfoOptions();

		String testItem = getTestCommandItem(pc);
		if(testItem != null)
		{
			String dialogId = testItem;
			Dialog dialog = manager.getDialog(dialogId);

			PrintWriter out = pc.getResponse().getWriter();
			out.write("<h1>Dialog: "+dialogId+"</h1>");
			out.write("<p>&nbsp;<center>");
			out.write(dialog.getHtml(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin()));
			out.write("</center>");
		}
        else
        {
            VirtualPath.FindResults results = pc.getActivePath();
            String[] unmatchedItems = results.unmatchedPathItems();
            if(unmatchedItems != null && unmatchedItems[0].equals("generate-dc"))
                handleBeanGenerator(pc);
            else
                transform(pc, manager.getDocument(), ACE_CONFIG_ITEMS_PREFIX + "ui-browser-xsl");
        }
	}
}
