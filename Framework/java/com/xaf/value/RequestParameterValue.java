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

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to HTTP servlet request parameters. All parameter values are returned as String objects.",
            "parameter-name"
        );
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