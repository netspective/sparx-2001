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

public class StaticField extends TextField
{
    public StaticField()
    {
    }

	public StaticField(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	public String getControlHtml(DialogContext dc)
	{
		String value = dc.getValue(this);
		return "<input type='hidden' name='"+ getId() +"' value=\"" + (value != null ? value : "") + "\"><span id='"+ getQualifiedName() +"'>" + value + "</span>";
	}
}