package com.netspective.sparx.ace.page;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationPageException;
import com.netspective.sparx.xaf.navigate.NavigationTreeManager;
import com.netspective.sparx.xaf.navigate.NavigationTreeManagerFactory;

import javax.servlet.ServletContext;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: Apr 30, 2003
 * Time: 3:34:46 PM
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
		NavigationTreeManager manager = NavigationTreeManagerFactory.getManager(context);
		manager.addMetaInfoOptions();

		writer.write("<h3>App Requirements Page goes here.</h3>");
		//transform(nc, manager.getDocument(), com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "navigation-browser-xsl");
	}
}
