package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class SeparatorField extends DialogField
{
	static public final long FLDFLAG_HIDERULE = DialogField.FLDFLAG_STARTCUSTOM;
	protected String heading;

	public SeparatorField()
	{
		super();
	}

	public SeparatorField(String aHeading)
	{
		super(null, null);
		heading = aHeading;
	}

	public SeparatorField(String name, String aHeading)
	{
		super(name, null);
		heading = aHeading;
	}

	public String getHeading() { return heading; }
	public void setHeading(String value) { heading = value; }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		heading = elem.getAttribute("heading");
		if(heading.length() == 0) heading = null;

		if(elem.getAttribute("rule").equals("no"))
			setFlag(FLDFLAG_HIDERULE);
	}

	public String getControlHtml(DialogContext dc)
	{
		return dc.getSkin().getSeparatorHtml(dc, this);
	}
}
