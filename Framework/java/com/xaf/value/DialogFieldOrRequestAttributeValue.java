/*
 * Title:       DialogFieldOrRequestAttributeValue 
 * Description: DialogFieldOrRequestAttributeValue
 * Copyright:   Copyright (c) 2001
 * Company:     
 * @author      ThuA
 * @created     Nov 7, 2001 4:18:34 PM
 * @version     1.0
 */
package com.xaf.value;

import com.xaf.form.*;
import com.xaf.form.field.*;

import javax.servlet.ServletRequest;
import java.util.*;

public class DialogFieldOrRequestAttributeValue extends ValueSource implements ListValueSource
{
    public DialogFieldOrRequestAttributeValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to a specific field of a dialog. If the field-name refers to a dialog field whose value "+
            "is null, then this value source will return the value of a request attribute named field-name.",
            "field-name"
        );
    }

    public String getValue(ValueContext vc)
    {
        String value = null;
        ServletRequest request = vc.getRequest();

        DialogContext dc = (DialogContext) request.getAttribute(DialogContext.DIALOG_CONTEXT_ATTR_NAME);
        if (dc != null)
        {
            value = dc.getValue(valueKey);
        }
        else
        {
            value = request.getParameter(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        }
        if (value == null)
        {
		    Object o = vc.getRequest().getAttribute(valueKey);

		    if (o == null)
                return null;

		    if (o instanceof String)
		    {
			    return (String) o;
		    }
		    else if (o instanceof String[])
            {
                String[] array = (String[]) o;
                return array.length > 0 ? array[0] : null;
            }
            else if (o instanceof List)
            {
                List list = (List) o;
                return list.size() > 0 ? (String) list.get(0) : null;
            }
            else
            {
                return o.toString();
            }
        }
        return value;
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        SelectChoicesList choices = new SelectChoicesList();
		Object o = vc.getRequest().getAttribute(valueKey);
		if(o instanceof Map)
		{
			Map map = (Map) o;
			for(Iterator i = map.entrySet().iterator(); i.hasNext(); )
			{
				Map.Entry entry = (Map.Entry) i.next();
				choices.add(new SelectChoice(entry.getKey().toString(), entry.getValue().toString()));
			}
		}
		else
		{
			String[] values = getValues(vc);
			for(int i = 0; i < values.length; i++)
				choices.add(new SelectChoice(values[i]));
		}
        return choices;

	}

    public String[] getValues(ValueContext vc)
    {
		String[] values = vc.getRequest().getParameterValues(Dialog.PARAMNAME_CONTROLPREFIX + valueKey);
        if(values == null)
        {
            Object o = vc.getRequest().getAttribute(valueKey);
            if(o == null) return null;

            if(o instanceof String[])
            {
                return (String[]) o;
            }
            else if(o instanceof List)
             {
                List list = (List) o;
                String[] result = new String[list.size()];
                return (String[]) list.toArray(result);
            }
            else
            {
                return new String[] { o.toString() };
            }
        }
        return values;
	}
}
