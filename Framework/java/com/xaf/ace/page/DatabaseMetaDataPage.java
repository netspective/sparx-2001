package com.xaf.ace.page;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.ace.*;
import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.config.*;
import com.xaf.page.*;
import com.xaf.skin.*;

public class DatabaseMetaDataPage extends AceServletPage
{
	private DatabaseMetaDataToSchemaDocDialog dialog;

	public final String getName() { return "meta-data"; }
	public final String getPageIcon() { return "schema.gif"; }
	public final String getCaption(PageContext pc) { return "DB Meta Data"; }
	public final String getHeading(PageContext pc) { return "Database Meta Data"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
        PrintWriter out = pc.getResponse().getWriter();
		if(dialog == null)
			dialog = new DatabaseMetaDataToSchemaDocDialog();

		ServletContext context = pc.getServletContext();

		DialogContext dc = dialog.createContext(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
		dialog.prepareContext(dc);
		if(! dc.inExecuteMode())
		{
			out.write("&nbsp;<p><center>");
			out.write(dialog.getHtml(dc, true));
			out.write("</center>");
			return;
		}

		Connection conn = null;
		try
		{
			Class.forName(dc.getValue("ds_driver_name"));
			conn = DriverManager.getConnection(dc.getValue("ds_url"), dc.getValue("ds_username"), dc.getValue("ds_password"));

			SchemaDocument schema = new SchemaDocument(conn, dc.getValue("ds_catalog"), dc.getValue("ds_schema"));

			String fileName = dc.getValue("out_file_name");
			schema.saveXML(fileName);

			out.write("Wrote file '"+fileName+"'");
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
		finally
		{
			try
			{
				if(conn != null) conn.close();
			}
			catch(Exception e)
			{
				throw new ServletException(e);
			}
		}
	}
}