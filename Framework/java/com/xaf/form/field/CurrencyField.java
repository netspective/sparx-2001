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
        this.decimal = 2;
        this.currencySymbol = "$";
        this.negativePos = "before";
        this.setValidatePattern("/^([-])?([\\" + this.currencySymbol + "])?([0-9]+)([.]{1}[0-9]{," + this.decimal + "})?$/");
        this.setDisplaySubstitutionPattern("s/^([-])?([\\" + this.currencySymbol +
                "])?([0-9]+)([.]{1}[0-9]{," + this.decimal + "})?$/$1\\" + this.currencySymbol + "$3$4/g");
    }

    /**
     * Gets the currency symbol
     */
    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    /**
     * Sets the currency symbol
     */
    public void setCurrencySymbol(String currencySymbol)
    {
        this.currencySymbol = currencySymbol;
    }

    /**
     * Gets the number of decimal places allowed
     */
    public int getDecimal()
    {
        return decimal;
    }

    /**
     * Sets the number of decimal places allowed
     */
    public void setDecimal(int decimal)
    {
        this.decimal = decimal;
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

        String decimalExpr = "";
        if (this.decimal < 0)
        {
            addErrorMessage("Currency field's 'decimal' value must be greater than or equal to zero.");
            return;
        }
        else if (decimal > 0)
        {
            decimalExpr = "([.][\\d]{1," + this.decimal + "})?";
        }
        else
        {
            decimalExpr = "";
        }

        this.negativePos = elem.getAttribute("negative");
        if (negativePos == null || negativePos.length() == 0)
            this.negativePos = "before";
        else if (!negativePos.equals("before") && !negativePos.equals("after"))
            this.negativePos = "before";
        if (this.negativePos.equals("before"))
        {
            this.setValidatePattern("/^([-])?([\\" + this.currencySymbol + "])?([\\d]+)"+ decimalExpr + "$/");
            this.setDisplaySubstitutionPattern("s/^([-])?([\\" + this.currencySymbol +
                    "])?([\\d]+)" + decimalExpr + "$/$1\\" + this.currencySymbol + "$3$4/g");
            this.setSubmitSubstitutePattern("s/" +"^([-])?([\\" + this.currencySymbol + "])?([\\d]+)" + decimalExpr +
                    "$/$1$3$4/g");
            this.setValidatePatternErrorMessage("Currency values must have the format\\n" +
                this.currencySymbol + "xxx.xx for positive values and " +
                "-" + this.currencySymbol + "xxx.xx for negative values.");
        }
        else if (this.negativePos.equals("after"))
        {
            this.setValidatePattern("/^([\\" + this.currencySymbol + "])?([-]?[\\d]+)"+ decimalExpr + "$/");
            this.setDisplaySubstitutionPattern("s/" +"^([\\" + this.currencySymbol + "])?([-]?[\\d]+)"+ decimalExpr + "$/\\"+ this.currencySymbol + "$2$3/g");
            this.setSubmitSubstitutePattern("s/" + "^([\\" + this.currencySymbol + "])?([-]?[\\d]+)"+ decimalExpr + "$" + "/$2$3/g");
            this.setValidatePatternErrorMessage("Currency values must have the format\\n" +
                this.currencySymbol + "xxx.xx for positive values and " +
                this.currencySymbol + "-xxx.xx for negative values.");
        }

    }

    public boolean isValid(DialogContext dc)
	{
		String value = dc.getValue(this);

		if(value == null || value.length() == 0)
		{
            if (isRequired(dc))
            {
			    invalidate(dc, getCaption(dc) + " is required.");
			    return false;
            }
            else
            {
                return true;
            }
		}
        int symbolPos = value.indexOf(this.currencySymbol);
        if (symbolPos  != -1)
            value = value.substring(0, symbolPos) + value.substring(symbolPos+1);

        Double dbl = null;
        try
        {
            // validate that the value is a valid one
            dbl = new Double(value);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            invalidate(dc, getCaption(dc) + " must be in a decimal format.");
            return false;
        }

        if (dbl != null)
        {
            String dblStr = dbl.toString();
            if (dblStr.indexOf(".") != -1)
            {
                String decimalStr = dblStr.substring(dblStr.indexOf(".")+1);
                if (decimalStr.length() > this.decimal)
                {
                    invalidate(dc, this.getCaption(dc) + " can only have " + this.decimal + " decimal digits.");
                    return false;
                }
            }
        }

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
