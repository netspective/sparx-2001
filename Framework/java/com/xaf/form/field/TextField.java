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
    /* regular expression pattern for validating the value */
    private String validatePattern;
    /* substitution patttern to format the value so that it satisfies the validation regex */
    private String displaySubstitutionPattern;
    /* error message for when the validation fails */
    private String regexMessage;
    /* substitution pattern to format the value when the value is ready to submitted */
	private String submitSubstitutionPattern;

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

    public String getDisplaySubstitutionPattern()
    {
        return displaySubstitutionPattern;
    }

    public void setDisplaySubstitutionPattern(String validateSubstitutionPattern)
    {
        this.displaySubstitutionPattern = validateSubstitutionPattern;
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

        // extract the substitution/formatting pattern for displaying the value
        value = elem.getAttribute("display-pattern");
        if (value != null &&  value.length() != 0)
            this.setDisplaySubstitutionPattern(value);

        // extract the substituiton/formatting pattern for submitting the value
        value = elem.getAttribute("format-pattern");
        if (value != null && value.length() != 0)
            setSubmitSubstitutePattern(value);
	}

    /**
     * Format the dialog field value for every dialog stage(display/validation stages) but not
     * after successful validation(submit stage).
     *
     * @param   value field value
     * @returns String formatted text
     */
    public String formatDisplayValue(String value)
    {
		if(value == null) return null;

		long flags = getFlags();
		if((flags & FLDFLAG_UPPERCASE) != 0) value = value.toUpperCase();
		if((flags & FLDFLAG_LOWERCASE) != 0) value = value.toLowerCase();
		if((flags & FLDFLAG_TRIM) != 0) value = value.trim();

		if(this.displaySubstitutionPattern != null)
		{
			try
			{
				value = perlUtil.substitute(displaySubstitutionPattern, value);
			}
			catch(MalformedPerl5PatternException e)
			{
                e.printStackTrace();
				value = e.toString();
			}
		}
		return value;
    }

    /**
     * Format the dialog field value after successful validation.
     *
     * @param   value field value
     * @returns String formatted text
     */
	public String formatSubmitValue(String value)
	{
		if(value == null) return null;

		long flags = getFlags();
		if((flags & FLDFLAG_UPPERCASE) != 0) value = value.toUpperCase();
		if((flags & FLDFLAG_LOWERCASE) != 0) value = value.toLowerCase();
		if((flags & FLDFLAG_TRIM) != 0) value = value.trim();

		if(this.submitSubstitutionPattern != null)
		{
			try
			{
				value = perlUtil.substitute(submitSubstitutionPattern, value);
			}
			catch(MalformedPerl5PatternException e)
			{
                e.printStackTrace();
				value = e.toString();
			}
		}
		return value;
	}

	public void populateValue(DialogContext dc, int formatType)
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
        if (formatType == DialogField.DISPLAY_FORMAT)
		    dc.setValue(this, this.formatDisplayValue(value));
        else if (formatType == DialogField.SUBMIT_FORMAT)
            dc.setValue(this, this.formatSubmitValue(value));
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

        String readonlyStyle = dc.getSkin().getControlAreaStyleAttrs();
		if(isReadOnly(dc))
		{
			return "<input type='hidden' name='"+ getId() +"' value=\"" + value + "\"><span id='"+ getQualifiedName() +"'>" + value + "</span>";
		}
        else if (isBrowserReadOnly(dc))
        {
	        return "<input type=\"text\" name=\""+ getId() +"\" readonly " + readonlyStyle + " value=\"" + value + "\" maxlength=\""+ maxLength + "\" size=\""+ size + "\" "+ (isRequired(dc) ? "class='required'" : "") +dc.getSkin().getDefaultControlAttrs() + ">";
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
    public String getSubmitSubstitutePattern()
    {
        return submitSubstitutionPattern;
    }

    /**
     * Sets the regular expression used for formatting/substituting the field
     * @param str Pattern string
     */
    public void setSubmitSubstitutePattern(String str)
    {
        submitSubstitutionPattern = str;
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
        StringBuffer buf = new StringBuffer(super.getCustomJavaScriptDefn(dc));

        if (this.isBrowserReadOnly(dc))
            buf.append("field.readonly = 'yes';\n");
        else
            buf.append("field.readonly = 'no';\n");

        if (this.flagIsSet(TextField.FLDFLAG_UPPERCASE))
            buf.append("field.uppercase = 'yes';\n");
        else
            buf.append("field.uppercase = 'no';\n");

        if (this.validatePattern != null)
            buf.append("field.text_format_pattern = " + this.validatePattern + ";\n");
        if (this.regexMessage != null)
            buf.append("field.text_format_err_msg = '" + this.regexMessage + "';\n");

        return buf.toString();
    }

    /**
	 * Produces Java code when a custom DialogContext is created
	 */
	public DialogContextMemberInfo getDialogContextMemberInfo()
	{
		DialogContextMemberInfo mi = createDialogContextMemberInfo("String");
        String fieldName = mi.getFieldName();
		String memberName = mi.getMemberName();
        String dataType = mi.getDataType();

		mi.addJavaCode("\tpublic "+ dataType +" get" + memberName + "() { return getValue(\""+ fieldName +"\"); }\n");
        mi.addJavaCode("\tpublic "+ dataType +" get" + memberName + "("+ dataType +" defaultValue) { return getValue(\""+ fieldName +"\", defaultValue); }\n");
        mi.addJavaCode("\tpublic "+ dataType +" get" + memberName + "OrBlank() { return getValue(\""+ fieldName +"\", \"\"); }\n");
		mi.addJavaCode("\tpublic void set" + memberName + "("+ dataType +" value) { setValue(\""+ fieldName +"\", value); }\n");

		return mi;
	}
}
