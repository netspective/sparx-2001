package com.xaf.form;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DialogFieldPopup
{
	public final String DEFAULT_WINDOW_CLASS = "default";

	private String imgUrl;
	private String windowClass = DEFAULT_WINDOW_CLASS;
	private String actionUrl = null;
	private String[] fillFields = null;
	private boolean allowMulti = false;
	private boolean closeAfter = true;

    public DialogFieldPopup(String action, String[] fill)
    {
		actionUrl = action;
		fillFields = fill;
    }

    public DialogFieldPopup(String action, String fill)
    {
		actionUrl = action;
		fillFields = new String[] { fill };
    }

	public final String getPopupWindowClass() { return windowClass; }
	public void setPopupWindowClass(String value) { windowClass = value; }

	public final String getImageUrl() { return imgUrl; }
	public void setImageUrl(String value) { imgUrl = value; }

	public final String getActionUrl() { return actionUrl; }
	public final String[] getFillFields() { return fillFields; }

	public final boolean allowMultiSelect() { return allowMulti; }
	public void setAllowMultiSelect(boolean value) { allowMulti = value; }

	public final boolean closeAfterSelect() { return closeAfter; }
	public void setCloseAfterSelect(boolean value) { closeAfter = value; }
}