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