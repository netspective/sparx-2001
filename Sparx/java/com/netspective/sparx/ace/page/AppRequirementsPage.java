package com.netspective.sparx.ace.page;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationPageException;
import com.netspective.sparx.xaf.requirement.RequirementTreeManager;
import com.netspective.sparx.xaf.requirement.RequirementTreeManagerFactory;

import javax.servlet.ServletContext;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 12, 2003
 * Time: 4:20:09 PM
 * To change this template use Options | File Templates.
 */
public class AppRequirementsPage extends AceServletPage
{
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
		RequirementTreeManager manager = RequirementTreeManagerFactory.getManager(context);

		transform(nc, manager.getDocument(), "app.reports.requirements.xsl");
	}

}
