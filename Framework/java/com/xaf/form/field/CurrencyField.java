/*
 * Title:       CurrencyField 
 * Description: CurrencyField
 * Copyright:   Copyright (c) 2001
 * Company:     
 * @author      ThuA
 * @created     Oct 23, 2001 5:12:42 PM
 * @version     1.0
 */
package com.xaf.form.field;

import com.xaf.form.*;
import org.w3c.dom.Element;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

import java.util.*;
import java.text.*;

public class CurrencyField extends TextField
{
    private DecimalFormat currencyFormat;
    private Locale currencyLocale;
    private String currencySymbol;
    private int decimal;
    private String negativePos;

    public CurrencyField()
    {
        super();
        this.currencyLocale = Locale.US;
    }

    /**
     * Read the field configurations from the XML file
     * @param elem <field.currency> node
     */
    public void importFromXml(Element elem)
	{
        super.importFromXml(elem);

        String attr = elem.getAttribute("type");
        if (attr == null || attr.length() == 0)
        {
            this.currencyLocale = Locale.US;
            NumberFormat nFormat = NumberFormat.getCurrencyInstance(this.currencyLocale);
            DecimalFormatSymbols cSymbol = new DecimalFormatSymbols(this.currencyLocale);
            if (nFormat instanceof DecimalFormat)
            {
                this.currencyFormat = (DecimalFormat) nFormat;
                this.currencySymbol = cSymbol.getCurrencySymbol();
            }
        }
        else
        {
            if (attr.equals("UK"))
                this.currencyLocale = Locale.UK;
            else if (attr.equals("US"))
                this.currencyLocale = Locale.US;

            NumberFormat nFormat = NumberFormat.getCurrencyInstance(this.currencyLocale);
            DecimalFormatSymbols cSymbol = new DecimalFormatSymbols(this.currencyLocale);
            if (nFormat instanceof DecimalFormat)
            {
                this.currencyFormat = (DecimalFormat) nFormat;
                this.currencySymbol = cSymbol.getCurrencySymbol();
            }
        }
        String digits = elem.getAttribute("decimal");
        try
        {
            if (digits == null || digits.length() == 0)
                this.decimal = 2;
            else
                this.decimal = Integer.parseInt(digits);
        }
        catch (Exception e)
        {
            this.decimal = 2;
        }

        String negativePos = elem.getAttribute("negative");
        if (negativePos == null || negativePos.length() == 0)
            this.negativePos = "before";
        else if (!negativePos.equals("before") && !negativePos.equals("after"))
            this.negativePos = "before";
        if (this.negativePos.equals("before"))
        {
            this.setValidatePattern("/^([-])?([\\$])?([0-9]+)([.]{1}[0-9]{" + this.decimal + "})?$/");
            this.setSubstitutePattern("s/" +"^([-])?([\\$])?([0-9]+)([.]{1}[0-9]{" + this.decimal + "})?$" + "/$1$3$4/g");
            this.setValidatePatternErrorMessage("Currency values must have the format " +
                this.currencySymbol + "xxx.xx or xxx.xx for positive values and " +
                "-" + this.currencySymbol + "xxx.xx or -xxx.xx for negative values.");
        }
        else if (this.negativePos.equals("after"))
        {
            this.setValidatePattern("/^([\\$])?([-]?[0-9]+)([.]{1}[0-9]{" + this.decimal + "})?$/");
            this.setSubstitutePattern("s/" + "^([\\$])?([-]?[0-9]+)([.]{1}[0-9]{" + this.decimal + "})?$" + "/$2$3/g");
            this.setValidatePatternErrorMessage("Currency values must have the format " +
                this.currencySymbol + "xxx.xx or xxx.xx for positive values and " +
                this.currencySymbol + "-xxx.xx or -xxx.xx for negative values.");
        }

    }

    public boolean isValid(DialogContext dc)
	{
		String value = dc.getValue(this);
		if(isRequired(dc) && (value == null || value.length() == 0))
		{
			invalidate(dc, getCaption(dc) + " is required.");
			return false;
		}
        System.out.println(value);
        try
        {
            // validate that the value is a valid one
            double currencyValue = Double.parseDouble(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            invalidate(dc, getCaption(dc) + " must be in a decimal format.");
            return false;
        }


        /*
        try
        {
            currencyFormat.parse(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            invalidate(dc, getCaption(dc) + " must be in " + currencyFormat.toPattern() + " format.");
            return false;
        }
        */
		return true;
	}



    /**
     *  Passes on the phone format to the client side validations
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        StringBuffer buf = new StringBuffer(super.getCustomJavaScriptDefn(dc));
        buf.append("field.currency_symbol = '" + this.currencySymbol + "';\n");
        buf.append("field.negative_pos = '" + this.negativePos + "';\n");
        buf.append("field.decimal = '" + this.decimal + "';\n");
        return buf.toString();
    }
}
