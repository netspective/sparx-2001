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

public class DialogFieldOrRequestParameterValue extends ValueSource implements ListValueSource
{
    public DialogFieldOrRequestParameterValue()
    {
        super();
    }

    public String getValue(ValueContext vc)
    {
		String value = vc.getRequest().getParameter(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        if(value == null)
            value = vc.getRequest().getParameter(valueKey);
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