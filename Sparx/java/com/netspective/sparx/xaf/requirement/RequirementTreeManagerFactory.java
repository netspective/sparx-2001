package com.netspective.sparx.xaf.requirement;

import com.netspective.sparx.util.factory.Factory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ServletValueContext;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 12, 2003
 * Time: 4:53:22 PM
 * To change this template use Options | File Templates.
 */
public class RequirementTreeManagerFactory implements Factory
{
	static final String ATTRNAME_REQTREEMGR = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "req-tree-mgr";
	static private Map managers = new HashMap();


	/**
	 * Method used for retrieving a <code>RequirementTreeManager</code> object representing a
	 * XML structure file. The factory first looks for the <code>RequirementTreeManager</code> object
	 * from a <code>Map</code> and if it doesn't exist, it creates a new one and adds it
	 * to the map of requirement managers.
	 *
	 * @param file static structure XML file name
	 * @return RequirementTreeManager
	 */
	public static RequirementTreeManager getManager(String file)
	{
		RequirementTreeManager activeManager = (RequirementTreeManager) managers.get(file);
		if (activeManager == null)
		{
			activeManager = new RequirementTreeManager(new File(file));
			managers.put(file, activeManager);
		}
		return activeManager;
	}

	/**
	 * Method used for retrieving a <code>RequirementTreeManager</code> object within a web application context.
	 * The factory retrieves the structure XML file name from <code>app.requirement.requirements-file</code> configuraton entry
	 * defined in <code>WEB-INF/conf/sparx.xml</code> of the web application.
	 *
	 * @param context the servlet context
	 * @return RequirementTreeManager
	 */
	public static RequirementTreeManager getManager(ServletContext context)
	{
		RequirementTreeManager manager = (RequirementTreeManager) context.getAttribute(ATTRNAME_REQTREEMGR);
		if (manager != null)
			return manager;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(context, null, null, null);
		manager = getManager(appConfig.getTextValue(vc, "app.requirement.requirements-file"));
		manager.initializeForServlet(context);
		context.setAttribute(ATTRNAME_REQTREEMGR, manager);
		return manager;
	}
}
