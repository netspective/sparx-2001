package com.xaf.form.field;

import com.xaf.form.*;

public class GridField extends DialogField
{
    public GridField()
    {
		super();
    }

	public String getControlHtml(DialogContext dc)
	{
		return dc.getSkin().getGridControlsHtml(dc, this);
	}
}