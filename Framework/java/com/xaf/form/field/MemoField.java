package com.xaf.form.field;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.value.*;
import com.xaf.form.*;

public class MemoField extends DialogField
{
	static public final int WORDWRAP_SOFT = 0;
	static public final int WORDWRAP_HARD = 1;
	static public final String[] WORDWRAP_STYLES = new String[] { "soft", "hard" };

	protected int rows, cols;
	protected int wrap;
    private int maxLength;

	public MemoField()
	{
		super();
		rows = 3;
		cols = 40;
        maxLength = 2048;
		wrap = WORDWRAP_SOFT;
	}

	public MemoField(String aName, String aCaption, int aCols, int aRows, int length)
	{
		super(aName, aCaption);
		rows = aRows;
		cols = aCols;
        maxLength = length;
		wrap = WORDWRAP_SOFT;
	}

    public int getMaxLength() { return maxLength; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }

	public int getRows() { return rows; }
	public void setRows(int newRows) { rows = newRows; }

	public int getCols() { return cols; }
	public void setCols(int newCols) { cols = newCols; }

	public int getWordWrap() { return wrap; }
	public void setWordWrap(int value) { wrap = value; }


	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("rows");
		if(value.length() != 0)
			rows = Integer.parseInt(value);

		value = elem.getAttribute("cols");
		if(value.length() != 0)
			cols = Integer.parseInt(value);

		value = elem.getAttribute("max-length");
		if(value.length() != 0)
			maxLength = Integer.parseInt(value);

		if(elem.getAttribute("wrap").equalsIgnoreCase("hard"))
			wrap = MemoField.WORDWRAP_HARD;
		else
			wrap = MemoField.WORDWRAP_SOFT;
	}

	public void populateValue(DialogContext dc)
	{
        String value = dc.getValue(this);
        if(value == null)
		    value = dc.getRequest().getParameter(getId());

		SingleValueSource defaultValue = getDefaultValue();
		if(dc.getRunSequence() == 1)
		{
			if((value != null && value.length() == 0 && defaultValue != null) ||
				(value == null && defaultValue != null))
				value = defaultValue.getValueOrBlank(dc);
		}

		dc.setValue(this, formatValue(value));
	}

	public boolean isValid(DialogContext dc)
	{
		String value = dc.getValue(this);
		if(isRequired(dc) && (value == null || value.length() == 0))
		{
			invalidate(dc, getCaption(dc) + " is required.");
			return false;
		}

        if (value != null && value.length() > maxLength)
        {
            invalidate(dc, getCaption(dc) + " is limited to " + maxLength + " characters.");
            return false;
        }
		return true;
	}

	public String getControlHtml(DialogContext dc)
	{
		if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
			return getHiddenControlHtml(dc);

		String value = dc.getValue(this);
		String id = getId();
		if(isReadOnly(dc))
		{
			return "<input type='hidden' name='"+ id +"' value='" + (value != null ? value : "") + "'>" + value;
		}
		else
		{
			return
            "<textarea maxlength=\"" + maxLength + "\" name=\""+ id +"\" rows=\"" + rows + "\" cols=\"" + cols + "\" wrap=\"" +
        			WORDWRAP_STYLES[wrap] + "\"" + (isRequired(dc) ? "class='required'" : "") + dc.getSkin().getDefaultControlAttrs() +
                    ">" + (value != null ? value : "") + "</textarea>";
		}
	}

    /**
     *
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        return (super.getCustomJavaScriptDefn(dc) + "field.maxLength = " + this.getMaxLength() + ";\n");
    }
}
