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
 * $Id: DialogFieldPopup.java,v 1.6 2003-04-18 00:05:24 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

/**
 * <code>DialogFieldPopup</code> class represents a dialog field with up a pop up window associated with it.
 */
public class DialogFieldPopup
{
    public final String DEFAULT_WINDOW_CLASS = "default";

    private SingleValueSource imgUrl;
    private String windowClass = DEFAULT_WINDOW_CLASS;
    private SingleValueSource actionUrl = null;
    private String[] fillFields = null;
    private boolean allowMulti = false;
    private boolean closeAfter = true;
    private String[] extractFields = null; // these are the fields whose values will be appended to the popup's URL

    protected DialogFieldPopup()
    {

    }

    /**
     * Construct <code>DialogFieldPopup</code>
     *
     * @param action    URL for the pop up window
     * @param fill      array of field names to fill the data with
     */
    public DialogFieldPopup(String action, String[] fill)
    {
        actionUrl = action != null ? ValueSourceFactory.getSingleOrStaticValueSource(action) : null;
        fillFields = fill;
    }
    /**
     * Constuct <code>DialogFieldPopup</code>
     *
     * @param action    action URL for the popup window
     * @param fill      field name to fill the data with
     */
    public DialogFieldPopup(String action, String fill)
    {
        actionUrl = action != null ? ValueSourceFactory.getSingleOrStaticValueSource(action) : null;
        fillFields = new String[]{fill};
    }

    public final String getPopupWindowClass()
    {
        return windowClass;
    }

    public void setPopupWindowClass(String value)
    {
        windowClass = value;
    }

    /**
     * Get the URL for the image
     * @param dc
     * @return image URL string
     */
    public final String getImageUrl(DialogContext dc)
    {
        return imgUrl != null ? imgUrl.getValue(dc) : null;
    }

    /**
     * Set the URL for the image. The URL string can be a single value source.
     * @param value  Single or static value source string
     */
    public void setImageUrl(String value)
    {
        imgUrl = value != null ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    /**
     * Get the action URL for the popup
     * @param dc
     * @return action URL string
     */
    public final String getActionUrl(DialogContext dc)
    {
        return actionUrl != null ? actionUrl.getValue(dc) : null;
    }

    public void setActionUrl(String action)
    {
        actionUrl = action != null ? ValueSourceFactory.getSingleOrStaticValueSource(action) : null;
    }

    public String[] getExtractFields()
    {
        return extractFields;
    }

    /**
     * Set the dialog fields for extracting values
     * @param value
     */
    public void setExtractFields(String[] value)
    {
        extractFields = value;
    }

    public final String[] getFillFields()
    {
        return fillFields;
    }

    public void setFillFields(String[] fields)
    {
        fillFields = fields;
    }

    public final boolean allowMultiSelect()
    {
        return allowMulti;
    }

    public void setAllowMultiSelect(boolean value)
    {
        allowMulti = value;
    }

    public final boolean closeAfterSelect()
    {
        return closeAfter;
    }

    public void setCloseAfterSelect(boolean value)
    {
        closeAfter = value;
    }
}