package com.xaf.db;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.sql.*;
import javax.naming.*;
import javax.servlet.ServletRequest;

import org.w3c.dom.*;

import com.xaf.value.*;

/**
 * The reference DatabaseContext implementation for a servlet or JSP using XAF
 * database and SQL packages.
 *
 * @author Shahid N. Shah
 * @version 1.0
 */

public class BasicDatabaseContext extends AbstractDatabaseContext
{
	static private Context env;

	public final Connection getConnection(ValueContext vc, String dataSourceId) throws NamingException, SQLException
	{
		if(env == null)
			env = (Context) new InitialContext().lookup("java:comp/env");

		dataSourceId = translateDataSourceId(vc, dataSourceId);

        // check to see if there is already a connection bound with the request
        // meaning we're within a transaction. Reuse the connection if we are
        // within a connection
        ServletRequest request = vc.getRequest();
        Connection conn = (Connection) request.getAttribute(dataSourceId);
        if (conn == null)
        {
            DataSource source = (DataSource) env.lookup(dataSourceId);
            if(source == null)
                throw new NamingException("Data source '" + dataSourceId + "' not found");
            conn = source.getConnection();
        }
		return conn;
	}

    public void createCatalog(ValueContext vc, Element parent) throws NamingException
    {
        Document doc = parent.getOwnerDocument();

		Context env = (Context) new InitialContext().lookup("java:comp/env/jdbc");
		for(NamingEnumeration e = env.list(""); e.hasMore(); )
		{
			Element propertyElem = doc.createElement("property");

			NameClassPair entry = (NameClassPair) e.nextElement();
			DatabaseContextFactory.addText(doc, propertyElem, "name", "jdbc/" + entry.getName());

			try
			{
				DataSource source = (DataSource) env.lookup(entry.getName());
				DatabaseMetaData dbmd = source.getConnection().getMetaData();
				DatabaseContextFactory.addText(doc, propertyElem, "value", dbmd.getDriverName());
				DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "Version " + dbmd.getDriverVersion());
				DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "URL: " + dbmd.getURL());
				DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "User: " + dbmd.getUserName());

				String resultSetType = "unknown";
				if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))
				    resultSetType = "scrollable (insensitive)";
				else if(dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))
				    resultSetType = "scrollable (sensitive)";
				else if(dbmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY))
				    resultSetType = "non-scrollabe (forward only)";
				DatabaseContextFactory.addText(doc, propertyElem, "value-detail", "ResultSet Type: " + resultSetType);
			}
			catch(Exception ex)
			{
				DatabaseContextFactory.addText(doc, propertyElem, "value", ex.toString());
			}
			parent.appendChild(propertyElem);
		}
    }
}