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
 * $Id: DialogFieldConditionalAction.java,v 1.3 2002-07-10 20:56:24 aye.thu Exp $
 */

package com.netspective.sparx.xaf.form;

import org.w3c.dom.Element;

/**
 * Abstract class for defining conditional actions for dialog fields.
 */
public abstract class DialogFieldConditionalAction
{
    private DialogField sourceField = null;
    private DialogField partnerField = null;
    private String partnerFieldName = null;

    /**
     * Empty constructor
     */
    public DialogFieldConditionalAction()
    {
    }

    /**
     * Construct a <code>DialogFieldConditionalAction</code> object
     */
    public DialogFieldConditionalAction(DialogField sourceField)
    {
        setSourceField(sourceField);
    }

    /**
     * Construct a <code>DialogFieldConditionalAction</code> object whose source dialog
     * field is related to another dialog field
     *
     * @param sourceField   parent dialog field
     * @param partnerName   name of the parter dialog field
     */
    public DialogFieldConditionalAction(DialogField sourceField, String partnerName)
    {
        setSourceField(sourceField);
        setPartnerFieldName(partnerName);
    }

    /**
     * Indicates whether or not a parter field is required for the dialog field condition
     */
    public boolean isPartnerRequired()
    {
        return true;
    }

    /**
     * Import the condition from XML
     *
     * @param sourceField       parent dialog field
     * @param elem              conditional XML element
     * @param conditionalItem   conditional item name
     */
    public boolean importFromXml(DialogField sourceField, Element elem, int conditionalItem)
    {
        this.sourceField = sourceField;

        if(isPartnerRequired())
        {
            partnerFieldName = elem.getAttribute("partner");
            if(partnerFieldName == null || partnerFieldName.length() == 0)
            {
                sourceField.addErrorMessage("Conditional " + conditionalItem + " has no associated 'partner' (field).");
                partnerFieldName = null;
                return false;
            }
        }

        return true;
    }

    public final DialogField getSourceField()
    {
        return sourceField;
    }

    public final void setSourceField(DialogField value)
    {
        sourceField = value;
    }

    public final String getPartnerFieldName()
    {
        return partnerFieldName;
    }

    public final void setPartnerFieldName(String value)
    {
        partnerFieldName = value;
    }

    public final DialogField getPartnerField()
    {
        return partnerField;
    }

    public final void setPartnerField(DialogField value)
    {
        partnerField = value;
        if(partnerField != null)
            partnerField.addDependentCondition(this);
    }
}