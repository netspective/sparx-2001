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

public class StringsListValue extends ListSource
{
    private String selectChoiceDelim = ";";
    private String selectValueDelim = "=";

    public StringsListValue()
    {
    }

    public void initializeSource(String srcParams)
    {
		super.initializeSource(srcParams);

        SelectChoicesList choices = new SelectChoicesList();
		if(srcParams == null) return;

		StringTokenizer st = new StringTokenizer(srcParams, selectChoiceDelim);
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			if(token.equals("-"))
			{
				choices.add(new SelectChoice(""));
				continue;
			}

			String caption, value;
			int valueDelimPos = token.indexOf(selectValueDelim);
			if(valueDelimPos != -1)
			{
				caption = token.substring(0, valueDelimPos);
	    		value = token.substring(valueDelimPos + 1);
			}
			else
			{
				caption = token;
				value = token;
			}
			choices.add(new SelectChoice(caption, value));
		}

		setChoices(choices);
    }
}