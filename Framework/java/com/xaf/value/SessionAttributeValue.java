package com.xaf.value;

import java.util.*;
import javax.servlet.http.*;

import com.xaf.db.*;
import com.xaf.form.*;
import com.xaf.form.field.*;

public class SessionAttributeValue extends ValueSource implements ListValueSource
{
    public SessionAttributeValue()
    {
		super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to HTTP session attributes. Intelligently handles object of types String, String[], and List",
            "attribute-name"
        );
    }

    public String getValue(ValueContext vc)
    {
		Object o = ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
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
        return ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
		Object o = ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
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
		Object o = ((HttpServletRequest) vc.getRequest()).getSession().getAttribute(valueKey);
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
		if(value == null)
			((HttpServletRequest) vc.getRequest()).getSession().removeAttribute(valueKey);
		else
			((HttpServletRequest) vc.getRequest()).getSession().setAttribute(valueKey, value);
	}
}