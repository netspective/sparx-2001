package com.xaf.form;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public abstract class DialogFieldConditionalAction
{
	private DialogField sourceField = null;
	private DialogField partnerField = null;
	private String partnerFieldName = null;

	public DialogFieldConditionalAction()
	{
	}

    public DialogFieldConditionalAction(DialogField sourceField, String partnerFieldName)
    {
		this.sourceField = sourceField;
		this.partnerFieldName = partnerFieldName;
    }

	public final DialogField getSourceField() { return sourceField; }
	public final void setSourceField(DialogField value) { sourceField = value; }

	public final String getPartnerFieldName() { return partnerFieldName; }
	public final void setPartnerFieldName(String value) { partnerFieldName = value; }

	public final DialogField getPartnerField() { return partnerField; }
	public final void setPartnerField(DialogField value)
	{
		partnerField = value;
		if(partnerField != null)
			partnerField.addDependentCondition(this);
	}
}