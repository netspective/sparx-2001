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

/**
 * A ListValueSource (LVS) is an object that returns a list of values from a particular source
 * (like a select field or SQL query).This object is intended to be
 * initialized the initializeSource(String) method. The idea is that a single instance with a
 * particular URL-style parameter string will be provided and then whenever the value is needed,
 * a ValueContext will be provided to allow either static content or dynamic content to be served.
 */

public interface ListValueSource
{
    /**
     * Returns the unique identifier for this list value source.
     */
	public String getId();

    /**
     * Returns the documentation for this list value source.
     */
    public SingleValueSource.Documentation getDocumentation();

    /**
     * Given a parameter string similar to a URL parameter, this method initializes a specific
     * instance of a list value source.
     */
    public void initializeSource(String srcParams);

    /**
     * Returns the contents of this list value source suitable for use in a SelectField for
     * a dialog.
     */
    public SelectChoicesList getSelectChoices(ValueContext vc);

    /**
     * Returns the contents of this list as a string list.
     */
	public String[] getValues(ValueContext vc);
}