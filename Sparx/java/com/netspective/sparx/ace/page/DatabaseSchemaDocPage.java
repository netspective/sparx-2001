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
 * $Id: DatabaseSchemaDocPage.java,v 1.8 2003-02-26 07:54:13 aye.thu Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.xaf.querydefn.QueryDefinition;
import com.netspective.sparx.xaf.querydefn.QueryBuilderDialog;
import com.netspective.sparx.xaf.skin.SkinFactory;
import com.netspective.sparx.xaf.navigate.NavigationPath;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;
import com.netspective.sparx.xaf.navigate.NavigationPageException;
import com.netspective.sparx.xif.SchemaDocFactory;
import com.netspective.sparx.xif.SchemaDocument;
import com.netspective.sparx.util.value.ValueContext;

public class DatabaseSchemaDocPage extends AceServletPage
{
    public final String getName()
    {
        return "schema-doc";
    }

    public final String getEntityImageUrl()
    {
        return "schema.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return "SchemaDoc (XML)";
    }

    public final String getHeading(ValueContext vc)
    {
        NavigationPath.FindResults results = ((NavigationPathContext) vc).getActivePathFindResults();
        String[] unmatchedItems = results.unmatchedPathItems();
        if(unmatchedItems != null && unmatchedItems[0].equals("query"))
        {
            if(unmatchedItems.length >= 2)
                return "Query " + unmatchedItems[1] + " Table";
            else
                return "Query -- Error";
        }
        else
            return "Database Schema (XML Source)";
    }

    public void handleTableQueryDefn(NavigationPathContext nc, String[] params) throws IOException
    {
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(nc.getServletContext());

        Writer out = nc.getResponse().getWriter();
        if(params.length < 2)
        {
            out.write("<p>Table name parameters is required.");
            return;
        }

        if(schemaDoc == null)
        {
            out.write("<p>SchemaDocument not found.");
            return;
        }

        String tableName = params[1];
        Map tableQueryDefDefns = schemaDoc.getTableQueryDefDefns(tableName);
        if(tableQueryDefDefns == null)
        {
            out.write("<p>No query definitions available for table '"+ tableName +"'.");
            return;
        }

        SchemaDocument.TableQueryDefDefinition queryDefDefinition = (SchemaDocument.TableQueryDefDefinition) tableQueryDefDefns.get(SchemaDocument.QUERYDEFID_DEFAULT.toUpperCase());
        if(queryDefDefinition == null)
        {
            out.write("<p>No query definition '"+ SchemaDocument.QUERYDEFID_DEFAULT +"' found for table '"+ tableName +"'.");
            return;
        }

        QueryDefinition queryDefn = queryDefDefinition.getQueryDefinition();
        if(queryDefn == null)
        {
            out.write("<p>Query definition '"+ SchemaDocument.QUERYDEFID_DEFAULT +"' in table '"+ tableName +"' could not be instantiated.");
            return;
        }

        out.write("<p><center>");
        QueryBuilderDialog dialog = queryDefn.getBuilderDialog();
        dialog.renderHtml(
            nc.getServletContext(), nc.getServlet(), (HttpServletRequest) nc.getRequest(),
            (HttpServletResponse) nc.getResponse(), SkinFactory.getInstance().getDialogSkin(nc));
        out.write("</center>");
    }

    public void handlePageBody(Writer writer, NavigationPathContext nc) throws NavigationPageException, IOException
    {
        SchemaDocument schema = SchemaDocFactory.getDoc(nc.getServletContext());
        schema.addMetaInfoOptions();

        NavigationPath.FindResults results = nc.getActivePathFindResults();
        String[] unmatchedItems = results.unmatchedPathItems();
        if(unmatchedItems != null && unmatchedItems[0].equals("query"))
            handleTableQueryDefn(nc, unmatchedItems);
        else
            transform(nc, schema.getDocument(), com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "schema-browser-xsl");
    }
}
