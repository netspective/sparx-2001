package com.xaf.form.field;

import java.util.*;
import com.xaf.form.*;

public class SelectChoicesList
{
	ArrayList valueList = new ArrayList();
	HashMap valueMap = new HashMap();

	public void add(SelectChoice choice)
	{
		valueList.add(choice);
		valueMap.put(choice.value, choice);
	}

	public void clear()
	{
        valueList.clear();
		valueMap.clear();
	}

	public String[] getCaptions()
	{
		String[] result = new String[valueList.size()];
		int vIndex = 0;
		for(Iterator i = valueList.iterator(); i.hasNext(); )
		{
			SelectChoice choice = (SelectChoice) i.next();
			result[vIndex] = choice.getCaption();
			vIndex++;
		}
		return result;
	}

	public String[] getValues()
	{
		String[] result = new String[valueList.size()];
		int vIndex = 0;
		for(Iterator i = valueList.iterator(); i.hasNext(); )
		{
			SelectChoice choice = (SelectChoice) i.next();
			result[vIndex] = choice.getValue();
			vIndex++;
		}
		return result;
	}

	public Iterator getIterator()
	{
		return valueList.iterator();
	}

	public void calcSelections(DialogContext dc, SelectField field)
	{
		// make everthing "unselected" by default
		Iterator i = valueList.iterator();
		while(i.hasNext())
			((SelectChoice) i.next()).selected = false;

		if(field.isMulti())
		{
			String[] values = dc.getValues(field);
			if(values != null)
			{
				for(int v = 0; v < values.length; v++)
				{
					SelectChoice choice = (SelectChoice) valueMap.get(values[v]);
                    if(choice != null)
                        choice.selected = true;
				}
			}
		}
		else
		{
			String value = dc.getValue(field);
			if(value != null)
			{
				SelectChoice choice = (SelectChoice) valueMap.get(value);
                if(choice != null)
                    choice.selected = true;
			}
		}
	}
}
