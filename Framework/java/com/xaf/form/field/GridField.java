package com.xaf.form.field;

import java.util.*;
import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.value.*;

public class GridField extends DialogField
{
	ListValueSource captions;

    public GridField()
    {
		super();
    }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String captionsStr = elem.getAttribute("captions");
		if(captionsStr.length() > 0)
			captions = ValueSourceFactory.getListValueSource(captionsStr);
	}

	public ListValueSource getCaptionsSource() { return captions; }

	public String[] getCaptions(DialogContext dc)
	{
		String[] result = null;

		if(captions == null)
		{
			List rows = getChildren();
			if(rows == null)
				return null;

			DialogField firstRow = (DialogField) rows.get(0);
			if(firstRow == null)
				return null;

			List firstRowChildren = firstRow.getChildren();
			result = new String[firstRowChildren.size()];

			Iterator i = firstRowChildren.iterator();
			int captionIndex = 0;
			while(i.hasNext())
			{
				DialogField field = (DialogField) i.next();
				if(field.isVisible(dc))
					result[captionIndex] = field.getCaption(dc);
				captionIndex++;
			}
		}
		else
		{
			result = captions.getValues(dc);
		}
		return result;
	}

	public void setCaptionsSource(ListValueSource value) { captions = value; }

	public String getControlHtml(DialogContext dc)
	{
		return dc.getSkin().getGridControlsHtml(dc, this);
	}
}