package com.xaf.ace.page;

import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import com.xaf.value.*;

import java.io.PrintWriter;
import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.util.Map;

import org.w3c.dom.Element;

public class DialogBeanGenerateClassDialog extends Dialog
{
	protected TextField outputPathField;
    protected SelectField dialogsSelectField;

    public DialogBeanGenerateClassDialog()
    {
		super("dialogbeangen", "Generate Dialog Beans");
        super.setLoopEntries(false);

		outputPathField = new TextField("output_path", "Output Path");
		outputPathField.setSize(60);
		outputPathField.setFlag(DialogField.FLDFLAG_REQUIRED);
        outputPathField.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.web-inf-root-path}/classes/dialog/context"));

        ListValueSource allDialogs = ValueSourceFactory.getListValueSource("dialogs:.*");
        dialogsSelectField = new SelectField("dialogs", "Dialogs", SelectField.SELECTSTYLE_MULTIDUAL, allDialogs);
        dialogsSelectField.setFlag(DialogField.FLDFLAG_REQUIRED);
        dialogsSelectField.setDefaultListValue(allDialogs);
        dialogsSelectField.setSize(8);

		addField(outputPathField);
        addField(dialogsSelectField);

		setDirector(new DialogDirector());
    }

    public String execute(DialogContext dc)
    {
        DialogManager manager = DialogManagerFactory.getManager(dc.getServletContext());
        Map dialogsInfo = manager.getDialogs();

        String outputPath = dc.getValue(outputPathField);
        String[] dialogNames = dc.getValues(dialogsSelectField);
        java.util.Arrays.sort(dialogNames, String.CASE_INSENSITIVE_ORDER);

        if(dialogNames == null)
            return "No dialogs were selected";

        StringBuffer html = new StringBuffer();
        html.append("<table border=0 cellspacing=0 cellpadding=4>");
        html.append("<tr><td cellspan=2><h2>Dialog Beans (classes) Generated</h2></td></tr>");
        html.append("<tr bgcolor='#EEEEEE'><th style='border-bottom:2 solid black'>Dialog Name</th><th style='border-bottom:2 solid black'>Class</th></tr>");

        for(int i = 0; i < dialogNames.length;i++)
        {
            String activeDialogName = dialogNames[i];
            html.append("<td style='border-bottom:1 solid #EEEEEE'>" + activeDialogName + "</td>");

            /* create a generic dialog and read in the fields */
            DialogManager.DialogInfo activeDialogInfo = (DialogManager.DialogInfo) dialogsInfo.get(activeDialogName);
            try
            {
                File javaFile = activeDialogInfo.generateDialogBean(outputPath);
                html.append("<td style='border-bottom:1 solid #EEEEEE'><a href='"+javaFile.getAbsolutePath()+"'>" + javaFile.getAbsolutePath() + "</a></td>");
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