package com.xaf.form.field;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;
import org.apache.oro.text.perl.*;

public class TextField extends DialogField
{
	static public final long FLDFLAG_MASKENTRY   = DialogField.FLDFLAG_STARTCUSTOM;
	static public final long FLDFLAG_UPPERCASE   = FLDFLAG_MASKENTRY * 2;
	static public final long FLDFLAG_LOWERCASE   = FLDFLAG_UPPERCASE * 2;
	static public final long FLDFLAG_TRIM        = FLDFLAG_LOWERCASE * 2;
    static public final long FLDFLAG_STARTCUSTOM = FLDFLAG_TRIM * 2;

    static public Perl5Util perlUtil = new Perl5Util();

	private int size;
	private int maxLength;
    private String validatePattern;
    private String regexMessage;
	private String substPattern;

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

        value = elem.getAttribute("format-pattern");
        if (value != null && value.length() != 0)
            setSubstitutePattern(value);
	}

	public String formatValue(String value)
	{
		if(value == null) return null;

		long flags = getFlags();
        System.out.println(value);
		if((flags & FLDFLAG_UPPERCASE) != 0) value = value.toUpperCase();
		if((flags & FLDFLAG_LOWERCASE) != 0) value = value.toLowerCase();
		if((flags & FLDFLAG_TRIM) != 0) value = value.trim();

		if(substPattern != null)
		{
			try
			{
				value = perlUtil.substitute(substPattern, value);
			}
			catch(MalformedPerl5PatternException e)
			{
                e.printStackTrace();
				value = e.toString();
			}
		}

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

    public boolean needsValidation(DialogContext dc)
	{
        return true;
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

		// if we're doing a regular expression pattern match, try it now
		if(validatePattern != null && value != null && value.length() > 0)
		{
            try
            {
				if(! perlUtil.match(this.validatePattern, value))
				{
					invalidate(dc, regexMessage);
					result = false;
				}
            }
            catch (MalformedPerl5PatternException e)
            {
                e.printStackTrace();
                invalidate(dc, e.toString());
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
        else if (isBrowserReadOnly(dc))
        {
	        return "<input type=\"text\" name=\""+ getId() +"\" readonly style=\"background-color: lightyellow\" value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
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
     * Returns the regular expression used for formatting/substituting the field
     *
     * @returns String regular expression pattern
     */
    public String getSubstitutePattern()
    {
        return substPattern;
    }

    /**
     * Sets the regular expression used for formatting/substituting the field
     * @param str Pattern string
     */
    public void setSubstitutePattern(String str)
    {
        substPattern = str;
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
     *
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        if (this.isBrowserReadOnly(dc))
            return (super.getCustomJavaScriptDefn(dc) + "field.readonly = 'yes';\n");
        else
            return (super.getCustomJavaScriptDefn(dc) + "field.readonly = 'no';\n");
    }
}
