/*
 * Title:       UriField
 * Description: UriField
 * Copyright:   Copyright (c) 2001
 * Company:     
 * @author      Shahid N. Shah
 * @created     Aug 20, 2001 11:18:35 AM
 * @version     1.0
 */
package com.xaf.form.field;

public class UriField extends TextField
{
    public static String PATTERN_MATCH_EMAIL = "^.+://.+$";

    public UriField()
    {
        super();
        setValidatePattern("/" + PATTERN_MATCH_EMAIL + "/");
        setValidatePatternErrorMessage("URL must be in http://xyz.com/abc format.");
    }

	public UriField(String aName, String aCaption)
	{
		super(aName, aCaption);
        setValidatePattern("/" + PATTERN_MATCH_EMAIL + "/");
        setValidatePatternErrorMessage("URL must be in http://xyz.com/abc format.");
	}

}
