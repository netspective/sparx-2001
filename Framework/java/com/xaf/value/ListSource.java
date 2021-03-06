package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.form.DialogContext;
import com.xaf.form.field.SelectChoicesList;

public class ListSource implements ListValueSource
{
    private SelectChoicesList choices;
	private String[] values;
	protected String valueKey;

    public void initializeSource(String srcParams)
	{
		valueKey = srcParams;
	}

	public String getId()
	{
		return getClass().getName() + ":" + valueKey;
	}

    public SingleValueSource.Documentation getDocumentation()
    {
        return null;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
	{
		return choices;
	}

	public String[] getValues(ValueContext vc)
	{
		if(values != null)
			return values;

		SelectChoicesList scl = getSelectChoices(vc);
		if(scl != null)
			return scl.getCaptions();

		return null;
	}

	public void setChoices(SelectChoicesList choices) { this.choices = choices; }
	public void setValues(String[] values) { this.values = values; }
}