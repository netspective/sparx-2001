package com.xaf.value;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class RequestParameterValue extends ValueSource implements ListValueSource
{
    public RequestParameterValue()
    {
		super();
    }

    public String getValue(ValueContext vc)
    {
		return vc.getRequest().getParameter(valueKey);
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
		return vc.getRequest().getParameterValues(valueKey);
	}
}