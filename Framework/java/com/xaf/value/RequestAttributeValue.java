package com.xaf.value;

import java.util.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

/**
 * Provides access to ServletRequest attributes; intelligently handles object of type String, String[], List, and Map.
 */

public class RequestAttributeValue extends ValueSource implements ListValueSource
{
    public RequestAttributeValue()
    {
		super();
    }

    public Documentation getDocumentation()
    {
        return new Documentation(
            "Provides access to ServletRequest attributes; intelligently handles object of types String, String[], List, and Map.",
            "attribute-name"
        );
    }

    public String getValue(ValueContext vc)
    {
		Object o = vc.getRequest().getAttribute(valueKey);
		if(o == null) return null;

		if(o instanceof String)
		{
			return (String) o;
		}
		else if(o instanceof String[])
		{
			String[] array = (String[]) o;
			return array.length > 0 ? array[0] : null;
		}
		else if(o instanceof List)
		{
			List list = (List) o;
			return list.size() > 0 ? (String) list.get(0) : null;
		}
		else
		{
			return o.toString();
		}
    }

	public Object getObjectValue(ValueContext vc)
    {
        return vc.getRequest().getAttribute(valueKey);
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

	public boolean supportsSetValue()
	{
		return true;
	}

	public void setValue(ValueContext vc, Object value)
	{
		vc.getRequest().setAttribute(valueKey, value);
	}
}