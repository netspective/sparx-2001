package com.netspective.sparx.ace.page;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.requirement.RequirementTreeManager;
import com.netspective.sparx.xaf.requirement.RequirementTreeManagerFactory;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationTreeManager;
import com.netspective.sparx.xaf.navigate.NavigationTreeManagerFactory;

import com.netspective.sparx.xaf.page.PageControllerServlet;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.ace.AppComponentsExplorerServlet;

import java.io.Writer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 21, 2003
 * Time: 11:33:22 AM
 * To change this template use Options | File Templates.
 */
public class AppRequirementsGenMappingFileDialog extends Dialog
{
	private int fieldSize = 80;
	protected TextField outputFileField;
	protected TextField inputFileField;
	private AppRequirementsPage page;
	private NavigationPathContext npc;

	public void setPage(AppRequirementsPage p)
	{
		page = p;
	}

	public void setNavigationPathContext(NavigationPathContext c)
	{
		npc = c;
	}

	public AppRequirementsGenMappingFileDialog()
	{
		super("req_map_gen", "Generate Requirements Mapping File");
		super.setLoopEntries(false);

		inputFileField = new TextField("input_file", "Requirements XML File");
		inputFileField.setSize(fieldSize);
		inputFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
		inputFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.requirement.requirements-file}"));

		outputFileField = new TextField("output_file", "Output File");
		outputFileField.setSize(fieldSize);
		outputFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
		outputFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.requirement.requirements-mapping-file}"));

		addField(inputFileField);
		addField(outputFileField);

		setDirector(new DialogDirector());
	}

	public void execute(Writer writer, DialogContext dc) throws IOException
	{
		ServletContext context = dc.getServletContext();
		RequirementTreeManager reqManager = RequirementTreeManagerFactory.getManager(context);
		Document requirementsDoc = reqManager.getDocument();

/*
		NodeList requirements = requirementsDoc.getElementsByTagName("requirement");
		for (int i = 0; i < requirements.getLength(); i++)
		{
			Element requirement = (Element) requirements.item(i);
			writer.write(requirement.getAttribute("label") + "<br>");
		}
*/

		NavigationTreeManager manager = NavigationTreeManagerFactory.getManager(context);
		manager.addMetaInfoOptions();
		transform(npc, manager.getDocument(), "app.requirements.navigation.xsl", null);
	}

	public void transform(NavigationPathContext nc, Document doc, String styleSheetConfigName, String outputFileName) throws IOException
	{
		AppComponentsExplorerServlet servlet = ((AppComponentsExplorerServlet) nc.getServlet());
		Hashtable styleSheetParams = servlet.getStyleSheetParams();

		/**
		 * Add all of the entries from WEB-INF/conf/sparx.xml into the StyleSheet
		 * parameters. This will allow stylesheets to use the configuration
		 * properties as well.
		 */
		if (styleSheetParams.get("config-items-added") == null)
		{
			Configuration appConfig = ((PageControllerServlet) nc.getServlet()).getAppConfig();
			for (Iterator i = appConfig.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry configEntry = (Map.Entry) i.next();

				if (configEntry.getValue() instanceof Property)
				{
					Property property = (Property) configEntry.getValue();
					String propName = property.getName();
					styleSheetParams.put(propName, appConfig.getTextValue(nc, propName));
				}
			}
			styleSheetParams.put("config-items-added", new Boolean(true));
		}

		styleSheetParams.put("ace-url", nc.getRootUrl() + ((HttpServletRequest) nc.getRequest()).getServletPath());
		styleSheetParams.put("root-url", nc.getActivePathFindResults().getMatchedPath().getAbsolutePath(nc));
		styleSheetParams.put("page-heading", page.getHeading(nc));

		styleSheetParams.remove("detail-type");
		styleSheetParams.remove("detail-name");
		styleSheetParams.remove("sub-detail-name");

		styleSheetParams.put("detail-type", "navigation");
		styleSheetParams.put("detail-name", "GENERAL_REGISTRATION_RULES");

		String styleSheet = servlet.getAppConfig().getTextValue(nc, styleSheetConfigName);
		PrintWriter out = nc.getResponse().getWriter();

		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(styleSheet));

			for (Iterator i = styleSheetParams.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				transformer.setParameter((String) entry.getKey(), entry.getValue());
			}

			if (outputFileName == null)
			{
				transformer.transform
					(new javax.xml.transform.dom.DOMSource(doc),
						new javax.xml.transform.stream.StreamResult(out));
			}
			else
			{
				transformer.transform
					(new javax.xml.transform.dom.DOMSource(doc),
						new javax.xml.transform.stream.StreamResult(outputFileName));
			}
		}
		catch (TransformerConfigurationException e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
		}
		catch (TransformerException e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
		}
	}

}
