package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import com.xaf.form.DialogContext;
import com.xaf.form.field.SelectChoicesList;

public interface ListValueSource
{
    public void initializeSource(String srcParams);
    public SelectChoicesList getSelectChoices(DialogContext dc, String key);
}