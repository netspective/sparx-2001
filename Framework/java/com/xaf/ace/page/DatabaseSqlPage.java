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
import com.xaf.sql.*;
import com.xaf.sql.query.*;
import com.xaf.skin.*;

public class DatabaseSqlPage extends AceServletPage
{
	public final String getName() { return "sql"; }
	public final String getPageIcon() { return "sql.gif"; }
	public final String getCaption(PageContext pc) { return "SQL Statements"; }
	public final String getHeading(PageContext pc) { return "SQL Statements"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		String testItem = getTestCommandItem(pc);
		if(testItem != null)
		    handleTestStatement(pc, testItem);
		else
		{
			ServletContext context = pc.getServletContext();
			StatementManager manager = StatementManagerFactory.getManager(context);
			manager.updateExecutionStatistics();
			manager.addMetaInfoOptions();
			transform(pc, manager.getDocument(), ACE_CONFIG_ITEMS_PREFIX + "sql-browser-xsl");
		}
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
			manager.produceReport(out, dbc, pc, null, SkinFactory.getReportSkin("report"), stmtId, null, null);
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
}
