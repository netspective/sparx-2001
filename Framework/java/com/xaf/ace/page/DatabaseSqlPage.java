package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.db.generate.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.sql.*;
import com.xaf.sql.query.*;
import com.xaf.skin.*;

public class DatabaseSqlPage extends AceServletPage
{
	public final String getName() { return "sql"; }
	public final String getCaption(PageContext pc) { return "SQL"; }
	public final String getHeading(PageContext pc) { return "SQL Components"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		String testWhat = getTestCommandItem(pc);
		if(testWhat != null)
		{
			VirtualPath.FindResults results = pc.getActivePath();
			String[] testParams = results.unmatchedPathItems();
			// note -- testParams[0] will be the word "test"
			//         testParams[1] will be "test what"
			if(testWhat.equals("statement"))
				handleTestStatement(pc, testParams[2]);
			else if(testWhat.equals("query-defn"))
				handleTestQueryDefn(pc, testParams[2]);
			else if(testWhat.equals("query-defn-dlg"))
				handleTestQueryDefnSelectDialog(pc, testParams[2], testParams[3]);
			return;
		}

		ServletContext context = pc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);
		manager.updateExecutionStatistics();
		transform(pc, manager.getDocument(), ACE_CONFIG_ITEMS_PREFIX + "sql-browser-xsl");
	}

	public void handleTestStatement(PageContext pc, String stmtId) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = pc.getResponse().getWriter();
		DatabaseContext dbc = DatabaseContextFactory.getContext(pc.getRequest(), context);

		out.write("<h1>SQL: "+stmtId+"</h1>");
		try
		{
			StatementInfo si = manager.getStatement(stmtId);
			out.write(si.getDebugHtml(pc));
			manager.produceReport(out, dbc, pc, SkinFactory.getReportSkin("report"), stmtId, null, null);
		}
		catch(Exception e)
		{
			StringWriter msg = new StringWriter();
			msg.write(e.toString());

			PrintWriter pw = new PrintWriter(msg);
			e.printStackTrace(pw);
			out.write("<pre>");
			out.write(msg.toString());
			out.write("</pre>");
		}
	}

	public void handleTestQueryDefn(PageContext pc, String queryDefnId) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = pc.getResponse().getWriter();

		out.write("<h1>Query Definition: "+queryDefnId+"</h1>");

		QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
		if(queryDefn == null)
		{
			out.write("QueryDefinition not found.");
			return;
		}

		QueryBuilderDialog dialog = queryDefn.getBuilderDialog();
		out.print(dialog.getHtml(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin()));
	}

	public void handleTestQueryDefnSelectDialog(PageContext pc, String queryDefnId, String dialogId) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);

        PrintWriter out = pc.getResponse().getWriter();

		out.write("<h1>Query Definition: "+queryDefnId+", Dialog: "+ dialogId +"</h1>");

		QueryDefinition queryDefn = manager.getQueryDefn(queryDefnId);
		QuerySelectDialog dialog = queryDefn.getSelectDialog(dialogId);
		out.print(dialog.getHtml(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin()));
	}

}
