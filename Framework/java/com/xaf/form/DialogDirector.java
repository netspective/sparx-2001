package com.xaf.form;

import org.w3c.dom.*;

public class DialogDirector extends DialogField
{
	private String submitCaption = "   OK   ";
	private String cancelCaption = " Cancel ";

	public DialogDirector()
	{
		this("director");
	}

	public DialogDirector(String name)
	{
		super(name, null);
	}

	public String getSubmitCaption() { return submitCaption; }
	public void setSubmitCaption(String value) { submitCaption = value; }

	public String getCancelCaption() { return submitCaption; }
	public void setCancelCaption(String value) { cancelCaption = value; }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("style");
		if("data".equals(value))
			submitCaption = "  Save  ";

		value = elem.getAttribute("submit-caption");
		if(value.length() != 0)
			submitCaption = value;

		value = elem.getAttribute("cancel-caption");
		if(value.length() != 0)
			cancelCaption = value;
	}

	public String getControlHtml(DialogContext dc)
	{
		String attrs = dc.getSkin().getDefaultControlAttrs();

		StringBuffer html = new StringBuffer("<center>");
		html.append("<input type='submit' value='");
		html.append(submitCaption);
		html.append("' ");
		html.append(attrs);
		html.append(">&nbsp;&nbsp;");
		html.append("<input type='button' value='");
		html.append(cancelCaption);
		html.append("' onclick=\"document.location = '");
		html.append(dc.getOriginalReferer());
		html.append("'\" ");
		html.append(attrs);
		html.append("></center>");

		return html.toString();
	}
}