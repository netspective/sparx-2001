package com.netspective.sparx.xaf.form.field;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.xaf.form.DialogContext;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: thua
 * Date: Feb 14, 2003
 * Time: 4:41:29 PM
 * To change this template use Options | File Templates.
 */
public class ReportSelectedItemsField extends SelectField
{
    public ReportSelectedItemsField()
    {
    }

    public ReportSelectedItemsField(String aName, String aCaption)
    {
        super(aName, aCaption, SelectField.SELECTSTYLE_MULTILIST);
    }

    public void populateValue(DialogContext dc, int formatType)
    {
        if (isMulti())
        {
            // multi select list
            String[] values = dc.getValues(this);
            if (values == null || values.length == 0)
                values = dc.getRequest().getParameterValues(getId());

            // initial display of the dialog
            if (dc.getRunSequence() == 1)
            {
                ListValueSource defaultValue = getDefaultListValue();
                // if no request parameter is passed in and the XML defined default value exists
                if ((values != null && values.length == 0 && defaultValue != null) ||
                        (values == null && defaultValue != null))
                {
                    SelectChoicesList list = defaultValue.getSelectChoices(dc);
                    String[] defaultvalues = list.getValues();
                    dc.setValues(this, list.getValues());
                }
            }
            else
                dc.setValues(this, values);
        }
        else
        {
            super.populateValue(dc, formatType);
        }
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
         // the 'choices' for this special SELECT field comes from the request param only. They are not defined
        // in XML.
        String[] values = dc.getValues(this);

        SelectChoicesList choices = new SelectChoicesList();
        if (values != null)
        {
            for (int i=0; i < values.length; i++)
            {
                choices.add(new SelectChoice(values[i]));
            }
        }
        choices.calcSelections(dc, this);

        /*
        if (listSource != null)
        {
            choices = listSource.getSelectChoices(dc);
            if (choices == null)
                throw new RuntimeException("Choices is NULL in " + listSource.getClass().getName());
            choices.calcSelections(dc, this);
        }
        else
            choices = EMPTY_CHOICES;
        */

        boolean readOnly = isReadOnly(dc);
        String id = getId();
        String defaultControlAttrs = dc.getSkin().getDefaultControlAttrs();

        StringBuffer options = new StringBuffer();
        int itemIndex = 0;
        Iterator i = choices.getIterator();

        if (readOnly)
        {
            while (i.hasNext())
            {
                SelectChoice choice = (SelectChoice) i.next();
                if (choice.selected)
                {
                    if (options.length() > 0)
                        options.append(", ");
                    options.append("<input type='hidden' name='" + id + "' value=\"" + choice.value + "\">");
                    options.append(choice.caption);
                }
            }
            writer.write(options.toString());
            return;
        }
        else
        {
            while (i.hasNext())
            {
                SelectChoice choice = (SelectChoice) i.next();
                options.append("    <option value=\"" + choice.value + "\" " + (choice.selected ? "selected" : "") + ">" + choice.caption + "</option>\n");
            }
            writer.write("<select name='" + id + "' size='" + getSize() + "' multiple='yes' " + defaultControlAttrs +
                    (isInputHidden(dc) ? " style=\"display:none;\"" : "") +
                    ">\n" + options + "</select>\n");

        }

    }

}
