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

public class DatabaseGenerateDDLPage extends AceServletPage
{
	public final String getName() { return "generate-ddl"; }
	public final String getPageIcon() { return "ddl.gif"; }
	public final String getCaption(PageContext pc) { return "Generate DDL"; }
	public final String getHeading(PageContext pc) { return "Generate SQL Data Definition"; }

	private DatabaseGenerateDDLDialog dialog;

	public SchemaDocument getSchemaDocument(PageContext pc)
	{
		Configuration appConfig = ((PageControllerServlet) pc.getServlet()).getAppConfig();
		return SchemaDocFactory.getDoc(appConfig.getValue(pc, "app.schema.source-file"));
	}

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
        PrintWriter out = pc.getResponse().getWriter();
		if(dialog == null)
			dialog = new DatabaseGenerateDDLDialog();

		ServletContext context = pc.getServletContext();
		SchemaDocument schema = getSchemaDocument(pc);

		DialogContext dc = dialog.createContext(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
			return;
		}

		String outputPath = dc.getValue("output_path");
		this.transform(pc, getSchemaDocument(pc).getDocument(), "framework.ace.schema-generator-xsl", outputPath);

		if(outputPath != null)
			out.write("<p>Saved generated schema in <a href='" + outputPath + "'>"+ outputPath +"</a>");
    }
}
