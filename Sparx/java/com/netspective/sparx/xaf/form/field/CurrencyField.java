/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: CurrencyField.java,v 1.1 2002-01-20 14:53:18 snshah Exp $
 */

package com.netspective.sparx.xaf.form.field;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.DialogContext;

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
        if(attr == null || attr.length() == 0)
        {
            this.currencyLocale = Locale.US;
            NumberFormat nFormat = NumberFormat.getCurrencyInstance(this.currencyLocale);
            DecimalFormatSymbols cSymbol = new DecimalFormatSymbols(this.currencyLocale);
            if(nFormat instanceof DecimalFormat)
            {
                this.currencyFormat = (DecimalFormat) nFormat;
                this.currencySymbol = cSymbol.getCurrencySymbol();
            }
        }
        else
        {
            if(attr.equals("UK"))
                this.currencyLocale = Locale.UK;
            else if(attr.equals("US"))
                this.currencyLocale = Locale.US;

            NumberFormat nFormat = NumberFormat.getCurrencyInstance(this.currencyLocale);
            DecimalFormatSymbols cSymbol = new DecimalFormatSymbols(this.currencyLocale);
            if(nFormat instanceof DecimalFormat)
            {
                this.currencyFormat = (DecimalFormat) nFormat;
                this.currencySymbol = cSymbol.getCurrencySymbol();
            }
        }
        String digits = elem.getAttribute("decimal");
        try
        {
            if(digits == null || digits.length() == 0)
                this.decimal = 2;
            else
                this.decimal = Integer.parseInt(digits);
        }
        catch(Exception e)
        {
            this.decimal = 2;
        }

        String decimalExpr = "";
        if(this.decimal < 0)
        {
            addErrorMessage("Currency field's 'decimal' value must be greater than or equal to zero.");
            return;
        }
        else if(decimal > 0)
        {
            decimalExpr = "([.][\\d]{1," + this.decimal + "})?";
        }
        else
        {
            decimalExpr = "";
        }

        this.negativePos = elem.getAttribute("negative");
        if(negativePos == null || negativePos.length() == 0)
            this.negativePos = "before";
        else if(!negativePos.equals("before") && !negativePos.equals("after"))
            this.negativePos = "before";
        if(this.negativePos.equals("before"))
        {
            this.setValidatePattern("/^([-])?([\\" + this.currencySymbol + "])?([\\d]+)" + decimalExpr + "$/");
            this.setDisplaySubstitutionPattern("s/^([-])?([\\" + this.currencySymbol +
                    "])?([\\d]+)" + decimalExpr + "$/$1\\" + this.currencySymbol + "$3$4/g");
            this.setSubmitSubstitutePattern("s/" + "^([-])?([\\" + this.currencySymbol + "])?([\\d]+)" + decimalExpr +
                    "$/$1$3$4/g");
            this.setValidatePatternErrorMessage("Currency values must have the format\\n" +
                    this.currencySymbol + "xxx.xx for positive values and " +
                    "-" + this.currencySymbol + "xxx.xx for negative values.");
        }
        else if(this.negativePos.equals("after"))
        {
            this.setValidatePattern("/^([\\" + this.currencySymbol + "])?([-]?[\\d]+)" + decimalExpr + "$/");
            this.setDisplaySubstitutionPattern("s/" + "^([\\" + this.currencySymbol + "])?([-]?[\\d]+)" + decimalExpr + "$/\\" + this.currencySymbol + "$2$3/g");
            this.setSubmitSubstitutePattern("s/" + "^([\\" + this.currencySymbol + "])?([-]?[\\d]+)" + decimalExpr + "$" + "/$2$3/g");
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
            if(isRequired(dc))
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
        if(symbolPos != -1)
            value = value.substring(0, symbolPos) + value.substring(symbolPos + 1);

        Double dbl = null;
        try
        {
            // validate that the value is a valid one
            dbl = new Double(value);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            invalidate(dc, getCaption(dc) + " must be in a decimal format.");
            return false;
        }

        if(dbl != null)
        {
            String dblStr = dbl.toString();
            if(dblStr.indexOf(".") != -1)
            {
                String decimalStr = dblStr.substring(dblStr.indexOf(".") + 1);
                if(decimalStr.length() > this.decimal)
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
