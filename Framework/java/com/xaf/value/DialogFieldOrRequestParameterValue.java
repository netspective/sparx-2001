package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.form.*;
import com.xaf.form.field.*;

import javax.servlet.ServletRequest;

public class DialogFieldOrRequestParameterValue extends ValueSource implements ListValueSource
{
    public DialogFieldOrRequestParameterValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to a specific field of a dialog. If the field-name refers to a dialog field whose value "+
            "is null, then this value source will return the value of a request parameter named field-name.",
            "field-name"
        );
    }

    public String getValue(ValueContext vc)
    {
        String value = null;
        ServletRequest request = vc.getRequest();
        // NOTE: The behavior of the "formOrRequest" value source is changed from returning
        // the raw value of the dialog field to returning the formatted value.
        DialogContext dc = (DialogContext) request.getAttribute(DialogContext.DIALOG_CONTEXT_ATTR_NAME);
        if (dc != null)
            value = dc.getValue(valueKey);
        else
		    value = request.getParameter(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        if(value == null)
            value = request.getParameter(valueKey);
        return value;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		String[] values = getValues(vc);
		for(int i = 0; i < values.length; i++)
			choices.add(new SelectChoice(values[i]));
        return choices;
	}

    public String[] getValues(ValueContext vc)
    {
		String[] values = vc.getRequest().getParameterValues(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        if(values == null)
            values = vc.getRequest().getParameterValues(valueKey);
        return values;
	}
}