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
 * $Id: DialogBeanGenerateClassDialog.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogManager;
import com.netspective.sparx.xaf.form.DialogManagerFactory;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class DialogBeanGenerateClassDialog extends Dialog
{
    protected TextField outputPathField;
    protected TextField generatedPkgPrefixField;
    protected SelectField dialogsSelectField;

    public DialogBeanGenerateClassDialog()
    {
        super("dialogbeangen", "Generate Dialog Beans");
        super.setLoopEntries(false);

        outputPathField = new TextField("output_path", "Output Path");
        outputPathField.setSize(60);
        outputPathField.setFlag(DialogField.FLDFLAG_REQUIRED);
        outputPathField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.web-inf-root-path}/classes/dialog/context"));

        generatedPkgPrefixField = new TextField("pkg_prefix", "Package Prefix");
        generatedPkgPrefixField.setFlag(DialogField.FLDFLAG_REQUIRED);
        generatedPkgPrefixField.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource("dialog.context."));

        ListValueSource allDialogs = ValueSourceFactory.getListValueSource("dialogs:.*");
        dialogsSelectField = new SelectField("dialogs", "Dialogs", SelectField.SELECTSTYLE_MULTIDUAL, allDialogs);
        dialogsSelectField.setFlag(DialogField.FLDFLAG_REQUIRED);
        dialogsSelectField.setDefaultListValue(allDialogs);
        dialogsSelectField.setSize(8);

        addField(outputPathField);
        addField(generatedPkgPrefixField);
        addField(dialogsSelectField);

        setDirector(new DialogDirector());
    }

    public String execute(DialogContext dc)
    {
        DialogManager manager = DialogManagerFactory.getManager(dc.getServletContext());
        Map dialogsInfo = manager.getDialogs();

        String outputPath = dc.getValue(outputPathField);
        String pkgPrefix = dc.getValue(generatedPkgPrefixField);
        String[] dialogNames = dc.getValues(dialogsSelectField);
        java.util.Arrays.sort(dialogNames, String.CASE_INSENSITIVE_ORDER);

        if(dialogNames == null)
            return "No dialogs were selected";

        StringBuffer html = new StringBuffer();
        html.append("<table border=0 cellspacing=0 cellpadding=4>");
        html.append("<tr><td cellspan=2><h2>Dialog Beans (classes) Generated</h2></td></tr>");
        html.append("<tr bgcolor='#EEEEEE'><th style='border-bottom:2 solid black'>Dialog Name</th><th style='border-bottom:2 solid black'>Class</th></tr>");

        for(int i = 0; i < dialogNames.length; i++)
        {
            String activeDialogName = dialogNames[i];
            html.append("<td style='border-bottom:1 solid #EEEEEE'>" + activeDialogName + "</td>");

            /* create a generic dialog and read in the fields */
            DialogManager.DialogInfo activeDialogInfo = (DialogManager.DialogInfo) dialogsInfo.get(activeDialogName);
            try
            {
                File javaFile = activeDialogInfo.generateDialogBean(outputPath, pkgPrefix);
                html.append("<td style='border-bottom:1 solid #EEEEEE'><a href='" + javaFile.getAbsolutePath() + "'>" + javaFile.getAbsolutePath() + "</a></td>");
            }
            catch(IOException e)
            {
                html.append("<td style='border-bottom:1 solid #EEEEEE'>" + e.toString() + "</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");

        return html.toString();
    }
}