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
import org.apache.oro.text.perl.*;

public class DialogsListValue extends ListSource
{
    static public Perl5Util perlUtil = new Perl5Util();
    private String dialogsFilter;

    public DialogsListValue()
    {
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);
        dialogsFilter = "/" + srcParams + "/";
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();

        DialogManager dialogMgr = DialogManagerFactory.getManager(vc.getServletContext());

        Map dialogs = dialogMgr.getDialogs();
        for(Iterator i = dialogs.keySet().iterator(); i.hasNext(); )
        {
            String dialogName = (String) i.next();
            try
            {
				if(perlUtil.match(dialogsFilter, dialogName))
                    choices.add(new SelectChoice(dialogName));
            }
            catch (MalformedPerl5PatternException e)
            {
                choices.add(new SelectChoice(e.toString()));
            }
        }

		return choices;
	}
}