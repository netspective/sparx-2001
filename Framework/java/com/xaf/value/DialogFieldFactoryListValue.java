package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class DialogFieldFactoryListValue extends ListSource
{
    public DialogFieldFactoryListValue()
    {
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Returns a list of all the dialog field types (&lt;field.*&gt;) that can be used in dialogs. ",
            "*"
        );
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		Map fields = DialogFieldFactory.getFieldClasses();
		for(Iterator i = fields.keySet().iterator(); i.hasNext(); )
		{
			String mapKey = (String) i.next();
			Class fieldClass = (Class) fields.get(mapKey);
			choices.add(new SelectChoice(mapKey + " (" + fieldClass.getName() + ")"));
		}

		return choices;
	}
}