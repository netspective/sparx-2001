/*
 * Title:       EmailField 
 * Description: EmailField
 * Copyright:   Copyright (c) 2001
 * Company:     
 * @author      ThuA
 * @created     Aug 20, 2001 11:18:35 AM
 * @version     1.0
 */
package com.xaf.form.field;

public class EmailField extends TextField
{
    public static String PATTERN_MATCH_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";

    public EmailField()
    {
        super();
        setValidatePattern("/" + PATTERN_MATCH_EMAIL + "/");
        setValidatePatternErrorMessage("Email must be in xxx@xxxx.xxx format.");
    }

	public EmailField(String aName, String aCaption)
	{
		super(aName, aCaption);
        setValidatePattern("/" + PATTERN_MATCH_EMAIL + "/");
        setValidatePatternErrorMessage("Email must be in xxx@xxxx.xxx format.");
	}



}
