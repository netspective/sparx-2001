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

public class DialogFieldConditionalInvisible extends DialogFieldConditionalAction
{
    private String dataCmdStr;
    private int dataCmd;

    public DialogFieldConditionalInvisible()
    {
		super();
    }

    public DialogFieldConditionalInvisible(DialogField sourceField, String dataCmd)
    {
		super(sourceField);
		setDataCmd(dataCmd);
    }

    public boolean isPartnerRequired()
    {
        return false;
    }

    public boolean importFromXml(DialogField sourceField, Element elem, int conditionalItem)
    {
        if(! super.importFromXml(sourceField, elem, conditionalItem))
            return false;

        dataCmdStr = elem.getAttribute("data-cmd");
		if(dataCmdStr.length() == 0)
            dataCmdStr = null;

        if(dataCmdStr == null)
        {
            sourceField.addErrorMessage("Conditional " + conditionalItem + " has no associated 'data-cmd' (DialogContext Data Command).");
            return false;
        }
        else
        {
            dataCmd = DialogContext.getDataCmdIdForCmdText(dataCmdStr);
            if(dataCmd == DialogContext.DATA_CMD_NONE)
            {
                sourceField.addErrorMessage("Conditional " + conditionalItem + " has has an invalid 'data-cmd' ("+ dataCmdStr +").");
                return false;
            }
        }

        return true;
    }

    public int getDataCmd()
    {
        return dataCmd;
    }

    public void setDataCmd(String dataCmdStr)
    {
        this.dataCmdStr = dataCmdStr;
        dataCmd = DialogContext.getDataCmdIdForCmdText(dataCmdStr);
    }

    public boolean isVisible(DialogContext dc)
    {
        if(dc.getDataCommand() == dataCmd)
            return false;
        return true;
    }
}