/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: DatabaseImportData.java,v 1.7 2002-12-27 17:16:03 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.dal.Schema;
import com.netspective.sparx.xif.dal.TableImportStatistic;
import com.netspective.sparx.xif.dal.xml.ParseContext;
import com.netspective.sparx.xif.dal.xml.ImportException;
import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.util.ClassPath;
import com.netspective.sparx.util.value.ValueContext;

public class DatabaseImportData extends AceServletPage
{
    private DatabaseImportDataDialog dialog;

    public final String getName()
    {
        return "import-date";
    }

    public final String getPageIcon()
    {
        return "schema.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return "Import DAL Data";
    }

    public final String getHeading(ValueContext vc)
    {
        return "Import XML Data using DAL";
    }

    public void printStackTrace(Writer out, Exception e, String prepend) throws IOException
    {
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        if(prepend != null) out.write(prepend);
        out.write("<pre>" + e.toString() + stack.toString() + "</pre>");
    }

    public void echoDateFormat(Schema schema, Writer out, String dataType, String caption) throws IOException
    {
        Class colClass = schema.getColumnClass(dataType);
        if(colClass != null)
        {
            DateFormat format = null;
            try
            {
                Method method = colClass.getMethod("getDefaultDateFormat", null);
                format = (DateFormat) method.invoke(null, null);
            }
            catch (NoSuchMethodException e)
            {
            }
            catch (SecurityException e)
            {
            }
            catch (IllegalAccessException e)
            {
            }
            catch (IllegalArgumentException e)
            {
            }
            catch (InvocationTargetException e)
            {
            }
            out.write("<br>"+ caption +": " + colClass.getName() + " (");
            out.write("DateFormat class is " + (format != null ? format.getClass().getName() : "null"));
            out.write(", pattern is "+ (format != null ? (format instanceof SimpleDateFormat ? ("'<code>" + ((SimpleDateFormat) format).toLocalizedPattern() + "</code>'") : "unkown") : "unknown"));
            out.write(")");
        }
        else
            out.write("<br>"+ caption +": None");
    }

    public void handlePageBody(Writer writer, NavigationPathContext nc) throws ServletException, IOException
    {
        PrintWriter out = nc.getResponse().getWriter();
        if(dialog == null)
            dialog = new DatabaseImportDataDialog();

        ServletContext context = nc.getServletContext();

        DialogContext dc = dialog.createContext(context, nc.getServlet(), (HttpServletRequest) nc.getRequest(), (HttpServletResponse) nc.getResponse(), SkinFactory.getDialogSkin());
        dialog.prepareContext(dc);
        if(!dc.inExecuteMode())
        {
            out.write("&nbsp;<p><center>");
            dialog.renderHtml(out, dc, true);
            out.write("</center>");
            return;
        }

        String sourceFile = dialog.getSourceFile(dc);
        Class schemaClass = dialog.getSchemaClass(dc);

        out.write("<table class='data_table'>");
        out.write("<tr class='data_table'><td class='data_table' align=right>Source File:</td><td class='data_table'><font color='green'>"+ sourceFile +"</font></td></tr>");
        out.write("<tr class='data_table'><td class='data_table' align=right valign='top'>Schema Class:</td><td class='data_table'><font color='green'><b>"+ schemaClass.getName() +"</b> ("+ ClassPath.getClassFileName(schemaClass.getName()) +")");

        Schema schema = null;
        try
        {
            schema = (Schema) schemaClass.newInstance();
        }
        catch(Exception e)
        {
            printStackTrace(out, e, "<br>");
            schema = null;
        }

        if(schema != null)
        {
            echoDateFormat(schema, out, "date", "Date Column");
            echoDateFormat(schema, out, "time", "Time Column");
            echoDateFormat(schema, out, "stamp", "Stamp Column");
        }

        out.write("</font></td></tr>");

        if(schema != null)
        {
            ConnectionContext cc = null;
            out.write("<tr class='data_table' valign='top'><td class='data_table' align=right>Connection:</td><td class='data_table'><font color='green'>");
            try
            {
                cc = dialog.getConnectionContext(dc);

                Connection conn = cc.getConnection();
                DatabaseMetaData dbmd = conn.getMetaData();
                String databasePolicyClass = null;

                try
                {
                    databasePolicyClass = DatabaseContextFactory.getDatabasePolicy(conn).getClass().getName();
                }
                catch(Exception dpe)
                {
                    databasePolicyClass = dpe.toString();
                }

                out.write(dbmd.getDriverName() + "<br>");
                out.write("Product: " + dbmd.getDatabaseProductName() + "<br>");
                out.write("Product Version: " + dbmd.getDatabaseProductVersion() + "<br>");
                out.write("Driver Version: " + dbmd.getDriverVersion() + "<br>");
                out.write("Database Policy: " + databasePolicyClass + "<br>");
                out.write("URL: " + dbmd.getURL() + "<br>");
                out.write("User: " + dbmd.getUserName() + "<br>");
            }
            catch(Exception e)
            {
                try
                {
                    if(cc != null)
                        cc.rollbackTransaction();
                    printStackTrace(out, e, "<br>");
                    cc = null;
                }
                catch(SQLException se)
                {
                    throw new ServletException(se);
                }
            }
            out.write("</font></td></tr>");

            if(cc != null)
            {
                out.write("<tr class='data_table' valign='top'><td class='data_table' align=right>Results:</td><td class='data_table'><font color='green'>");
                try
                {
                    // do the actual parsing
                    ParseContext parseContext = schema.importFromXml(cc, new File(sourceFile));

                    List messages = parseContext.getMessages();
                    if(messages != null && messages.size() > 0)
                    {
                        out.write("<font color=blue><b>Messages</b><ul>");
                        for(int i = 0; i < messages.size(); i++)
                            out.write("<li>"+ (String) messages.get(i) +"</li>");
                        out.write("</ul></font>");
                    }

                    List errors = parseContext.getErrors();
                    if(errors != null && errors.size() > 0)
                    {
                        out.write("<font color=red><b>Errors</b><ul>");
                        for(int i = 0; i < errors.size(); i++)
                            out.write("<li>"+ (String) errors.get(i) +"</li>");
                        out.write("</ul></font>");
                    }
                    Map stats = parseContext.getStatistics();
                    if(stats != null && stats.size() > 0)
                    {
                        out.write("<b>Statistics</b><ol>");
                        Iterator i = stats.entrySet().iterator();
                        while(i.hasNext())
                        {
                            Map.Entry entry = (Map.Entry) i.next();
                            TableImportStatistic stat = (TableImportStatistic) entry.getValue();
                            out.write("<li>");
                            out.write("Table "+ stat.getTableName() +": "+ stat.getSuccessfulRows() +" successful, "+ stat.getUnsuccessfulRows() +" unsuccessful");
                            List idReferences = stat.getIdReferences();
                            if(idReferences != null && idReferences.size() > 0)
                            {
                                out.write("<br><b>ID References</b><ul>");
                                for(int j = 0; j < idReferences.size(); j++)
                                {
                                    out.write("<li>" + (String) idReferences.get(j) + "</li>");
                                }
                                out.write("</ul>");
                            }
                            List importErrors = stat.getImportErrors();
                            if(importErrors != null && importErrors.size() > 0)
                            {
                                out.write("<br><b>Errors</b><ul>");
                                for(int j = 0; j < importErrors.size(); j++)
                                {
                                    out.write("<li>" + (String) importErrors.get(j) + "</li>");
                                }
                                out.write("</ul>");
                            }
                            out.write("</li>");
                        }
                        out.write("<ol>");
                    }
                    cc.commitTransaction();
                }
                catch(SQLException se)
                {
                    printStackTrace(out, se, "<p>");
                }
                catch(ImportException e)
                {
                    printStackTrace(out, e, null);
                }
                out.write("</font></td></tr>");
            }
        }
        out.write("</table>");
    }
}
