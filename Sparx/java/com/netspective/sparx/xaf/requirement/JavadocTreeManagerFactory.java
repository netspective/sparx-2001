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
 * Date: May 30, 2003
 * Time: 11:26:03 AM
 * To change this template use Options | File Templates.
 */
public class JavadocTreeManagerFactory implements Factory
{
	static final String ATTR_NAME = com.netspective.sparx.Globals.DEFAULT_CONFIGITEM_PREFIX + "javadoc-tree-mgr";
	static private Map managers = new HashMap();

	/**
	 * Method used for retrieving a <code>JavadocTreeManager</code> object representing a
	 * XML structure file. The factory first looks for the <code>JavadocTreeManager</code> object
	 * from a <code>Map</code> and if it doesn't exist, it creates a new one and adds it
	 * to the map of managers.
	 *
	 * @param file static structure XML file name
	 * @return JavadocTreeManager
	 */
	public static JavadocTreeManager getManager(String file)
	{
		JavadocTreeManager activeManager = (JavadocTreeManager) managers.get(file);
		if (activeManager == null)
		{
			activeManager = new JavadocTreeManager(new File(file));
			managers.put(file, activeManager);
		}
		return activeManager;
	}

	/**
	 * Method used for retrieving a <code>JavadocTreeManager</code> object within a web application context.
	 * The factory retrieves the XML file name from <code>app.requirement.javadoc-requirement-labels-file</code> configuraton entry
	 * defined in <code>WEB-INF/conf/sparx.xml</code> of the web application.
	 *
	 * @param context the servlet context
	 * @return JavadocTreeManager
	 */
	public static JavadocTreeManager getManager(ServletContext context)
	{
		JavadocTreeManager manager = (JavadocTreeManager) context.getAttribute(ATTR_NAME);
		if (manager != null)
			return manager;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(context, null, null, null);
		manager = getManager(appConfig.getTextValue(vc, "app.requirement.javadoc-requirement-labels-file"));
		manager.initializeForServlet(context);
		context.setAttribute(ATTR_NAME, manager);
		return manager;
	}
}
