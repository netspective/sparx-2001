/*
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author ThuA
 * @version 
 * Created on: Jul 26, 2001 3:59:10 PM
 */
package com.xaf.form.field;

public class ZipField extends TextField
{
    static public final String VALIDATE_PATTERN  = "^([\\d]{5})([-][\\d]{4})?$";
    public static final String VALIDATE_ERROR_MSG = "Zip codes must be in the 12345 or 12345-1234 format.";
    public static final String DISPLAY_PATTERN = "s/" + VALIDATE_PATTERN + "/$1$2/g";
    public static final String SUBMIT_PATTERN = "s/" + VALIDATE_PATTERN + "/$1$2/g";


    public ZipField()
    {
        super();
        // set the dafault regex pattern for the zip field
        setValidatePattern("/" + VALIDATE_PATTERN + "/");
        setValidatePatternErrorMessage(VALIDATE_ERROR_MSG);
        setSize(11);
    }

    public ZipField(String aName, String aCaption)
    {
        super(aName, aCaption);

        // set the dafault regex pattern for the zip field
        setValidatePattern("/" + VALIDATE_PATTERN + "/");
        setValidatePatternErrorMessage(VALIDATE_ERROR_MSG);
        setSize(11);
    }

}
