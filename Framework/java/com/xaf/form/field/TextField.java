package com.xaf.form.field;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;
import org.apache.oro.text.regex.*;
import org.apache.oro.text.perl.*;


public class TextField extends DialogField
{
	static public final long FLDFLAG_MASKENTRY   = DialogField.FLDFLAG_STARTCUSTOM;
	static public final long FLDFLAG_UPPERCASE   = FLDFLAG_MASKENTRY * 2;
	static public final long FLDFLAG_LOWERCASE   = FLDFLAG_UPPERCASE * 2;
	static public final long FLDFLAG_TRIM        = FLDFLAG_LOWERCASE * 2;
    static public final long FLDFLAG_STARTCUSTOM = FLDFLAG_TRIM * 2;



	private int size;
	private int maxLength;
    private String validatePattern = null;
    private String regexMessage = null;
    private String subgroupPattern = null;
    private Perl5Util pUtil  = null;

	public TextField()
	{
		super();
		size = 32;
		maxLength = 255;
	}

	public TextField(String aName, String aCaption)
	{
		super(aName, aCaption);
		size = 32;
		maxLength = 255;
	}

	public final int getSize() { return size; }
	public void setSize(int value) { size = value; }

	public final int getMaxLength() { return maxLength; }
	public void setMaxLength(int newLength) { maxLength = newLength; }

	public void importFromXml(Element elem)
	{
		super.importFromXml(elem);

		String value = elem.getAttribute("size");
		if(value.length() != 0)
			size = Integer.parseInt(value);

		value = elem.getAttribute("max-length");
		if(value.length() != 0)
			maxLength = Integer.parseInt(value);

		if(elem.getAttribute("uppercase").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_UPPERCASE);

		if(elem.getAttribute("lowercase").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_LOWERCASE);

		if(elem.getAttribute("trim").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_TRIM);

		if(elem.getAttribute("mask-entry").equalsIgnoreCase("yes"))
			setFlag(FLDFLAG_MASKENTRY);

        // extract the regex pattern
        value = elem.getAttribute("validate-pattern");
        if (value != null && value.length() != 0)
            setValidatePattern(value);

        value = elem.getAttribute("validate-msg");
        if (value != null && value.length() != 0)
            setValidatePatternErrorMessage(value);

	}

	public String formatValue(String value)
	{
		if(value == null) return null;

		long flags = getFlags();
		if((flags & FLDFLAG_UPPERCASE) != 0) value = value.toUpperCase();
		if((flags & FLDFLAG_LOWERCASE) != 0) value = value.toLowerCase();
		if((flags & FLDFLAG_TRIM) != 0) value = value.trim();
		return value;
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

        // call the super class's isValid method
        boolean result = super.isValid(dc);
        if(! result)
            return false;
        // if the field is a regular expression, validate!
        if ((value != null && value.length() > 0) && getValidatePattern() != null)
        {
            String[] subgroups = null;
            try
            {
                subgroups = this.patternMatches(value);
            }
            catch (MalformedPerl5PatternException e)
            {
                e.printStackTrace();
                invalidate(dc, "Invalid regular expression pattern.");
                return false;
            }
            if (subgroups == null || subgroups.length == 0)
            {
                invalidate(dc, this.getValidatePatternErrorMessage());
                result = false;
            }
        }
		return result;
	}

	public String getControlHtml(DialogContext dc)
	{
		if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
			return getHiddenControlHtml(dc);

		String value = dc.getValue(this);
		if(value == null) value = "";

		if(isReadOnly(dc))
		{
			return "<input type='hidden' name='"+ getId() +"' value='" + value + "'><span id='"+ getQualifiedName() +"'>" + value + "</span>";
		}
		else if(! flagIsSet(FLDFLAG_MASKENTRY))
		{
			return "<input type=\"text\" name=\""+ getId() +"\" value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
		}
		else
		{
			return "<input type=\"password\" name=\""+ getId() +"\" value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
		}
	}

    /**
     * Returns the regular expression used for validating the field
     *
     * @returns String regular expression pattern
     */
    public String getValidatePattern()
    {
        return validatePattern;
    }
    /**
     * Sets the regular expression used for validating the field
     * @param str Pattern string
     */
    public void setValidatePattern(String str)
    {
        validatePattern = str;
    }

    /**
     *
     */
    public String getValidatePatternErrorMessage()
    {
        return regexMessage;
    }

    /**
     *
     */
    public void setValidatePatternErrorMessage(String str)
    {
        regexMessage = str;
    }
    /**
     * Checks to see if the string matches the pattern
     *
     * @param checkStr String to be validated against the pattern
     * @returns String[] An array of the subgroups that matched
     */
    public String[] patternMatches(String value) throws MalformedPerl5PatternException
    {
        boolean result = false;
        if (pUtil == null)
            pUtil = new Perl5Util();
        result = pUtil.match(this.validatePattern, value);

        String[] subgroups = null;
        if (result)
        {
            // get subgroup count
            int count = pUtil.groups();
            if (count > 1)
            {
                subgroups = new String[count-1];
                for (int i=0; i < subgroups.length; i++)
                {
                    // ignore index '0' because that returns the entire string itself
                    // as a subgroup
                    if (pUtil.group(i+1) != null)
                        subgroups[i] = pUtil.group(i+1);
                }
            }
        }
        return subgroups;
    }




}
