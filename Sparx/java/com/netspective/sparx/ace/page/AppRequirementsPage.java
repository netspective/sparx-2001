package com.netspective.sparx.ace.page;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.navigate.*;
import com.netspective.sparx.xaf.requirement.RequirementTreeManager;
import com.netspective.sparx.xaf.requirement.RequirementTreeManagerFactory;
import com.netspective.sparx.xaf.requirement.JavadocTreeManager;
import com.netspective.sparx.xaf.requirement.JavadocTreeManagerFactory;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementManagerFactory;
import com.netspective.sparx.xaf.form.DialogManager;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.skin.SkinFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.io.IOException;
import java.io.PrintWriter;

import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 12, 2003
 * Time: 4:20:09 PM
 * To change this template use Options | File Templates.
 */
public class AppRequirementsPage extends AceServletPage
{
	AppRequirementsGenMappingFileDialog dialog;

	public final String getName()
	{
		return "requirements";
	}

	public final String getEntityImageUrl()
	{
		return "dialogs.gif";
	}

	public final String getCaption(ValueContext vc)
	{
		return "Requirements";
	}

	public final String getHeading(ValueContext vc)
	{
		return "Application Requirements";
	}

	public void handlePageBody(Writer writer, NavigationPathContext nc) throws NavigationPageException, IOException
	{
		ServletContext context = nc.getServletContext();

		NavigationPath.FindResults results = nc.getActivePathFindResults();
		String[] unmatchedItems = results.unmatchedPathItems();
		if (results != null && unmatchedItems != null && unmatchedItems.length > 0)
		{
			String type = unmatchedItems[0];
			if (type.equals("all"))
			{
      	handleNavigation(writer, nc);
				handleStatement(writer, nc);
				handleQueryDef(writer, nc);
				handleDialog(writer, nc);
				handleJava(writer, nc);
			}
			else if (type.equals("generate-mapping"))
			{
				handleGenerateMappingFile(writer, nc);
			}
			else if (type.equals("navigation"))
			{
				handleNavigation(writer, nc);
			}
			else if (type.equals("statement"))
			{
				handleStatement(writer, nc);
			}
			else if (type.equals("querydef"))
			{
				handleQueryDef(writer, nc);
			}
			else if (type.equals("dialog"))
			{
				handleDialog(writer, nc);
			}
			else if (type.equals("java"))
			{
				handleJava(writer, nc);
			}
			else if (type.equals("plsql"))
			{
				writer.write("<h4 style='color:red; font-family:Tahoma'>The PL/SQL Codes option is left as an exercise for studious pupils.</h4>");
			}

			return;
		}

		RequirementTreeManager reqManager = RequirementTreeManagerFactory.getManager(context);
		transform(nc, reqManager.getDocument(), "app.requirements.xsl");
	}

	public void handleJava(Writer writer, NavigationPathContext nc) throws IOException
	{
		ServletContext context = nc.getServletContext();
		JavadocTreeManager manager = JavadocTreeManagerFactory.getManager(context);
		manager.addMetaInfoOptions();
		transform(nc, manager.getDocument(), "app.requirements.java.xsl");
	}

	public void handleGenerateMappingFile(Writer writer, NavigationPathContext nc) throws IOException
	{
		if (dialog == null)
			dialog = new AppRequirementsGenMappingFileDialog();

		PrintWriter out = nc.getResponse().getWriter();
		DialogContext dc = dialog.createContext(nc.getServletContext(), nc.getServlet(), (HttpServletRequest) nc.getRequest(),
			(HttpServletResponse) nc.getResponse(), SkinFactory.getInstance().getDialogSkin(nc));
		dialog.prepareContext(dc);
		dialog.setNavigationPathContext(nc);

		if (!dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			dialog.renderHtml(out, dc, true);
			out.write("</center>");
		}
		else
			dialog.renderHtml(out, dc, true);
	}

	public void handleNavigation(Writer writer, NavigationPathContext nc) throws IOException
	{
		ServletContext context = nc.getServletContext();
		NavigationTreeManager manager = NavigationTreeManagerFactory.getManager(context);
		manager.addMetaInfoOptions();
		transform(nc, manager.getDocument(), "app.requirements.navigation.xsl");
	}

	public void handleStatement(Writer writer, NavigationPathContext nc) throws IOException
	{
		ServletContext context = nc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);
		manager.updateExecutionStatistics();
		manager.addMetaInfoOptions();
		transform(nc, manager.getDocument(nc.getServletContext(), null), "app.requirements.statement.xsl");
	}

	public void handleQueryDef(Writer writer, NavigationPathContext nc) throws IOException
	{
		ServletContext context = nc.getServletContext();
		StatementManager manager = StatementManagerFactory.getManager(context);
		manager.updateExecutionStatistics();
		manager.addMetaInfoOptions();
		transform(nc, manager.getDocument(nc.getServletContext(), null), "app.requirements.querydef.xsl");
	}

	public void handleDialog(Writer writer, NavigationPathContext nc) throws IOException
	{
		ServletContext context = nc.getServletContext();
		DialogManager manager = DialogManagerFactory.getManager(context);
		manager.addMetaInfoOptions();
		transform(nc, manager.getDocument(context, null), "app.requirements.dialog.xsl");
	}
}
