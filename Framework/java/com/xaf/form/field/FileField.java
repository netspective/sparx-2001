package com.xaf.form.field;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.form.*;

public class FileField extends TextField
{
    public FileField()
    {
    }

	public FileField(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	public String getControlHtml(DialogContext dc)
	{
        String result = super.getControlHtml(dc);

		if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
			return getHiddenControlHtml(dc);

		String value = dc.getValue(this);
		if(value == null) value = "";

		if ( (!isReadOnly(dc)) && (! flagIsSet(FLDFLAG_MASKENTRY)))
		{
			return "<input type=\"file\" name=\""+ getId() +"\" size=\""+ getSize() + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
		}
		else
		{
			return result;
		}
	}
}