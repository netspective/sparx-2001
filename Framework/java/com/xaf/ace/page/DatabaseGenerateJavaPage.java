package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DatabaseGenerateJavaPage extends AceServletPage
{
	public final String getName() { return "generate-java"; }
	public final String getPageIcon() { return "ddl.gif"; }
	public final String getCaption(PageContext pc) { return "Generate Java"; }
	public final String getHeading(PageContext pc) { return "Generate Java Relational Objects"; }

	private DatabaseGenerateJavaDialog dialog;

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
        PrintWriter out = pc.getResponse().getWriter();
		if(dialog == null)
			dialog = new DatabaseGenerateJavaDialog();

        out.write("<p><center>");
        out.write(dialog.getHtml(pc.getServletContext(), pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin()));
        out.write("</center>");
    }
}
