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
 * $Id: DatabaseMetaDataPage.java,v 1.2 2002-08-09 11:53:45 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.page.PageContext;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xif.SchemaDocument;

public class DatabaseMetaDataPage extends AceServletPage
{
    private DatabaseMetaDataToSchemaDocDialog dialog;

    public final String getName()
    {
        return "meta-data";
    }

    public final String getPageIcon()
    {
        return "schema.gif";
    }

    public final String getCaption(PageContext pc)
    {
        return "Reverse Engineer";
    }

    public final String getHeading(PageContext pc)
    {
        return "Reverse Engineer SchemaDoc";
    }

    public void handlePageBody(PageContext pc) throws ServletException, IOException
    {
        PrintWriter out = pc.getResponse().getWriter();
        if(dialog == null)
            dialog = new DatabaseMetaDataToSchemaDocDialog();

        ServletContext context = pc.getServletContext();

        DialogContext dc = dialog.createContext(context, pc.getServlet(), (HttpServletRequest) pc.getRequest(), (HttpServletResponse) pc.getResponse(), SkinFactory.getDialogSkin());
        dialog.prepareContext(dc);
        if(!dc.inExecuteMode())
        {
            out.write("&nbsp;<p><center>");
            dialog.renderHtml(out, dc, true);
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

            out.write("Wrote file '" + fileName + "'");
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