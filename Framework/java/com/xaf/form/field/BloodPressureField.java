//package org.redcross.nbcs.common;
package com.xaf.form.field;
/**
 * Title:           NBCS Common Services
 * Description:
 * Copyright:       Copyright (c) 2001
 * Company:         American Red Cross
 * @author A. Thu
 * @version 1.0
 */

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;

/**
 * Represents a blood pressure field object for display in a dialog box
 *
 */
public class BloodPressureField extends TextField
{


    /**
     * Constructor
     */
    public BloodPressureField()
    {
        super();
    }


    /**
     * Checks to see if the blood pressure field needs validation
     *
     * @param dc Context in which the dialog object belongs to
     * @returns boolean
     */
    public boolean needsValidation(DialogContext dc)
    {
        return true;
    }

    /**
     * Checks to see if the blood pressure field is valid
     *
     * @param dc Context in which the dialog object belongs to
     * @returns boolean
     */
    public boolean isValid(DialogContext dc)
    {
        boolean result = super.isValid(dc);
        if(! result)
        {
            return false;
        }

        String value = dc.getValue(this);

        if (value.length() > this.getMaxLength())
        {
            invalidate(dc, "'" + value + "' is not a valid blood pressure value.");
            return false;
        }

        String systolicValueStr = null;
        String diastolicValueStr = null;

        StringTokenizer tokens = new StringTokenizer(value, "/");
        // parse the string for the systolic and Diastolic pressure values
        if (tokens.countTokens() != 2)
        {
            invalidate(dc, "'" + value + "' is not a valid blood pressure value.");
            return false;
        }
        systolicValueStr = tokens.nextToken();
        diastolicValueStr = tokens.nextToken();

        try
        {
            int systolicValue = Integer.parseInt(systolicValueStr);
            int diastolicValue = Integer.parseInt(diastolicValueStr);
            if (diastolicValue > systolicValue)
            {
                throw new Exception();
            }
            else if (systolicValue <= 0 || diastolicValue <= 0)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            invalidate(dc, "'" + value + "' is not a valid blood pressure value.");
            return false;
        }

        return true;
    }

    /**
     * Formats the string entered into the blood pressure field object
     *
     * @param value blood pressure string value
     * @returns String
     */
    public String postFormatValue(String value)
    {
        return value;
    }
}