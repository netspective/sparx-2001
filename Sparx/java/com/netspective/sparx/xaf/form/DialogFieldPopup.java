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
 * $Id: DialogFieldPopup.java,v 1.3 2002-07-10 20:56:24 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form;

/**
 * <code>DialogFieldPopup</code> class represents a dialog field with up a pop up window associated with it.
 */
public class DialogFieldPopup
{
    public final String DEFAULT_WINDOW_CLASS = "default";

    private String imgUrl;
    private String windowClass = DEFAULT_WINDOW_CLASS;
    private String actionUrl = null;
    private String[] fillFields = null;
    private boolean allowMulti = false;
    private boolean closeAfter = true;

    /**
     * Construct <code>DialogFieldPopup</code>
     *
     * @param action    URL for the pop up window
     * @param fill      array of field names to fill the data with
     */
    public DialogFieldPopup(String action, String[] fill)
    {
        actionUrl = action;
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
        actionUrl = action;
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

    public final String getImageUrl()
    {
        return imgUrl;
    }

    public void setImageUrl(String value)
    {
        imgUrl = value;
    }

    public final String getActionUrl()
    {
        return actionUrl;
    }

    public final String[] getFillFields()
    {
        return fillFields;
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