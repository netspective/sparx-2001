package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.db.*;
import com.xaf.form.*;

public class DialogDataCmdExprValue extends StaticValue
{
    public DialogDataCmdExprValue()
    {
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Returns the current dialog data_cmd identifier plus the text provided that would be suitable for use " +
            "as the heading of a multi-purpose dialog (a dialog that can be used for adding, updating, and deleting). For "+
            "example, if <code><u>Person</u></code> is the text, and the current dialog's data_cmd is <code><u>add</u></code> then this SVS would return " +
            "<code><u>Add Person</u></code>.",
            "text"
        );
    }

	public String getValue(ValueContext vc)
    {
        DialogContext dc = null;
        if(vc instanceof DialogContext)
			dc = (DialogContext) vc;
		else
            dc = (DialogContext) vc.getRequest().getAttribute(DialogContext.DIALOG_CONTEXT_ATTR_NAME);

        if(dc == null)
            return "[no dataCmd] " + valueKey;
        else
        {
            return dc.getDataCommandText(true) + " " + valueKey;
        }
    }

}