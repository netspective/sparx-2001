package com.xaf.form;

import org.w3c.dom.*;
import com.xaf.value.*;

public class DialogDirector extends DialogField
{
	private SingleValueSource submitCaption;
	private SingleValueSource cancelCaption;
	private SingleValueSource submitActionUrl;
	private SingleValueSource cancelActionUrl;

	public DialogDirector()
	{
		this("director");
	}

	public DialogDirector(String name)
	{
		super(name, null);
        this.submitCaption = ValueSourceFactory.getSingleOrStaticValueSource("   OK   ");
        this.cancelCaption = ValueSourceFactory.getSingleOrStaticValueSource(" Cancel ");
	}

	public SingleValueSource getSubmitCaption() { return submitCaption; }
	public void setSubmitCaption(String value)
    {
        submitCaption = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public SingleValueSource getCancelCaption() { return cancelCaption; }
	public void setCancelCaption(String value)
    {
        cancelCaption = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public SingleValueSource getSubmitActionUrl() { return submitActionUrl; }
	public void setSubmitActionUrl(String value)
    {
        submitActionUrl = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public SingleValueSource getCancelActionUrl() { return cancelActionUrl; }
	public void setCancelActionUrl(String value)
    {
        cancelActionUrl = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("style");
		if("data".equals(value))
			submitCaption = ValueSourceFactory.getSingleOrStaticValueSource("  Save  ");
		else if("confirm".equals(value))
		{
			submitCaption = ValueSourceFactory.getSingleOrStaticValueSource("  Yes  ");
			cancelCaption = ValueSourceFactory.getSingleOrStaticValueSource("  No   ");
		}

		value = elem.getAttribute("submit-caption");
        if (value != null && value.length() > 0)
		    submitCaption = ValueSourceFactory.getSingleOrStaticValueSource(value);

		value = elem.getAttribute("cancel-caption");
        if (value != null && value.length() > 0)
		    cancelCaption = ValueSourceFactory.getSingleOrStaticValueSource(value);

		value = elem.getAttribute("submit-url");
		if(value.length() != 0)
			this.setSubmitActionUrl(value);

		value = elem.getAttribute("cancel-url");
		if(value.length() != 0)
			this.setCancelActionUrl(value);
	}

	public String getControlHtml(DialogContext dc)
	{
		String attrs = dc.getSkin().getDefaultControlAttrs();

		String submitCaption = this.submitCaption.getValue(dc);
		String cancelCaption = this.cancelCaption.getValue(dc);

		switch(dc.getDataCommand())
		{
			case DialogContext.DATA_CMD_ADD:
			case DialogContext.DATA_CMD_EDIT:
				submitCaption = " Save ";
				break;

			case DialogContext.DATA_CMD_DELETE:
				submitCaption = " Delete ";
				break;

			case DialogContext.DATA_CMD_CONFIRM:
				submitCaption = "  Yes  ";
				cancelCaption = "  No   ";
				break;
		}

		StringBuffer html = new StringBuffer("<center>");
		html.append("<input type='submit' value='");
		html.append(submitCaption);
		html.append("' ");
		html.append(attrs);
		html.append(">&nbsp;&nbsp;");
		html.append("<input type='button' value='");
		html.append(cancelCaption);
		html.append("' ");
		if(cancelActionUrl == null)
		{
			html.append("onclick=\"document.location = '");
	    	html.append(dc.getOriginalReferer());
		    html.append("'\" ");
		}
		else
		{
            String cancelStr = cancelActionUrl != null ? cancelActionUrl.getValue(dc) : null;
			if("back".equals(cancelStr))
			{
				html.append("onclick=\"history.back()\" ");
			}
			else if(cancelStr != null && cancelStr.startsWith("javascript:"))
			{
				html.append("onclick=\"");
				html.append(cancelStr);
				html.append("\" ");
			}
			else
			{
				html.append("onclick=\"document.location = '");
				html.append(cancelStr);
				html.append("'\" ");
			}
		}
		html.append(attrs);
		html.append("></center>");

		return html.toString();
	}
}