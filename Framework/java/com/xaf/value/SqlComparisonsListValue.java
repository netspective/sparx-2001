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
import com.xaf.sql.query.*;

public class SqlComparisonsListValue extends ListSource
{
	private String groupName;

    public SqlComparisonsListValue()
    {
    }

    public void initializeSource(String srcParams)
    {
		groupName = srcParams;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();

		List comparisons = SqlComparisonFactory.getComparisonsList();
		if(groupName == null || groupName.equals("all"))
		{
			for(Iterator i = comparisons.iterator(); i.hasNext(); )
			{
				SqlComparison comp = (SqlComparison) i.next();
				choices.add(new SelectChoice(comp.getCaption(), comp.getName()));
			}
		}
		else
		{
			for(Iterator i = comparisons.iterator(); i.hasNext(); )
			{
				SqlComparison comp = (SqlComparison) i.next();
				if(comp.getGroupName().equals(groupName))
					choices.add(new SelectChoice(comp.getCaption(), comp.getName()));
			}
		}

		return choices;
    }
}