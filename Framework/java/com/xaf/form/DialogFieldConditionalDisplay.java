package com.xaf.form;

import org.w3c.dom.Element;

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

    public DialogFieldConditionalDisplay()
    {
		super();
    }

    public DialogFieldConditionalDisplay(DialogField sourceField, String partnerName, String jsExpr)
    {
		super(sourceField, partnerName);
		setExpression(jsExpr);
    }

    public boolean importFromXml(DialogField sourceField, Element elem, int conditionalItem)
    {
        if(! super.importFromXml(sourceField, elem, conditionalItem))
            return false;

        javaScriptExpression = elem.getAttribute("js-expr");
		if(javaScriptExpression == null || javaScriptExpression.length() == 0)
		{
			sourceField.addErrorMessage("Conditional " + conditionalItem + " has no associated 'js-expr' (JavaScript Expression).");
            return false;
		}

        return true;
    }

	public final String getExpression() { return javaScriptExpression; }
	public final void setExpression(String value) { javaScriptExpression = value; }
}