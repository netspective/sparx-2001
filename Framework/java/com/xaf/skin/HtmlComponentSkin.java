package com.xaf.skin;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

public class HtmlComponentSkin extends HtmlReportSkin
{
    public HtmlComponentSkin()
    {
		super();
        clearFlag(HTMLFLAG_SHOW_HEAD_ROW | HTMLFLAG_ADD_ROW_SEPARATORS);
		dataFontAttrs = "face='verdana,arial' size='2' style='font-size: 7pt;'";
    }
}