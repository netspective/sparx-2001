package com.xaf.form;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class DialogFieldConditionalDisplay extends DialogFieldConditionalAction
{
	private String javaScriptExpression;

    public DialogFieldConditionalDisplay(DialogField sourceField, String partnerName, String jsExpr)
    {
		super(sourceField, partnerName);
		setExpression(jsExpr);
    }

	public final String getExpression() { return javaScriptExpression; }
	public final void setExpression(String value) { javaScriptExpression = value; }
}