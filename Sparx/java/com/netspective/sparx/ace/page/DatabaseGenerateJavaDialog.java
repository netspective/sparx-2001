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
 * $Id: DatabaseGenerateJavaDialog.java,v 1.8 2002-12-23 04:24:33 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.xif.SchemaDocFactory;
import com.netspective.sparx.xif.SchemaDocument;

public class DatabaseGenerateJavaDialog extends Dialog
{
    protected TextField sourceFileField;
    protected TextField stylesheetsRootField;

    public DatabaseGenerateJavaDialog()
    {
        super("schemagen", "Generate Data Access Layer (DAL)");

        sourceFileField = new TextField("source_file", "Source file");
        sourceFileField.setSize(80);
        sourceFileField.setFlag(DialogField.FLDFLAG_REQUIRED);
        sourceFileField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config:app.schema.source-file"));

        stylesheetsRootField = new TextField("style_sheets_root", "Stylesheet root");
        stylesheetsRootField.setSize(80);
        stylesheetsRootField.setFlag(DialogField.FLDFLAG_REQUIRED);
        stylesheetsRootField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${"+ com.netspective.sparx.Globals.SHARED_CONFIG_ITEMS_PREFIX +"xslt-path}/schema-gen/java-gen"));

        addField(sourceFileField);
        addField(stylesheetsRootField);

        setDirector(new DialogDirector());
    }

    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        String srcFile = dc.getValue(sourceFileField);
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(srcFile);
        if(schemaDoc == null)
            //return "Schema file '" + dc.getValue("source_file") + "' not found.";
            writer.write("Schema file '" + srcFile + "' not found.");

        String stylesheetsRootPath = dc.getValue(stylesheetsRootField);
        StringBuffer output = new StringBuffer("<p align='center'>");
        SchemaDocument.DataAccessLayerGenerator dalGen = schemaDoc.getDataAccessLayerGenerator(stylesheetsRootPath);
        try
        {
            dalGen.generate();
            output.append("Generated " + dalGen.getDataTypesGeneratedCount() + " data types, ");
            output.append(dalGen.getTableTypesGeneratedCount() + " table types, and ");
            output.append(dalGen.getTablesGeneratedCount() + " tables.");
        }
        catch(TransformerConfigurationException e)
        {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            output.append("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
        catch(TransformerException e)
        {
            List messages = dalGen.getMessages();
            if(messages.size() > 0)
            {
                output.append("<ol>");
                for(int i = 0; i < messages.size(); i++)
                    output.append("<li>"+ messages.get(i));
                output.append("</ol>");
            }

            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            output.append("<pre>" + e.toString() + stack.toString() + "</pre>");
        }
        writer.write(output.toString());
    }

}