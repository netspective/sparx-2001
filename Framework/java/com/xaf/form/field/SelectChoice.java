package com.xaf.form.field;

import java.util.*;

public class SelectChoice
{
	protected boolean selected;
	protected String caption;
	protected String value;

	public SelectChoice(String aCaption)
	{
		caption = aCaption;
		value = aCaption;
	}

	public SelectChoice(String aCaption, String aValue)
	{
		caption = aCaption;
		value = aValue;
	}

	public boolean isSelected() { return selected; }
	public void setSelected(boolean value) { selected = value; }

	public String getCaption() { return caption; }
	public String getValue() { return value; }
}
