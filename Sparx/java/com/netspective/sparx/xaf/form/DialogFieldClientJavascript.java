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
 * $Id: DialogFieldClientJavascript.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.xaf.form;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class DialogFieldClientJavascript
{
    private SingleValueSource event;
    private SingleValueSource type;
    private SingleValueSource script;
    public static final String[] eventList = {"is-valid", "value-changed", "click", "key-press", "get-focus", "lose-focus"};

    public DialogFieldClientJavascript()
    {
    }

    public void setEvent(String event)
    {
        this.event = (event != null ? ValueSourceFactory.getSingleOrStaticValueSource(event): null);
    }

    public void setType(String type)
    {
        this.type = (type != null ? ValueSourceFactory.getSingleOrStaticValueSource(type): null);
    }

    public void setScript(String script)
    {
        this.script = (script != null ? ValueSourceFactory.getSingleOrStaticValueSource(script): null);
    }

    /**
     * Check to see if the event is valid
     *
     * @param str event type read from XML
     * @returns true if it is a valid event, else false
     */
    public static boolean isValidEvent(String str)
    {
        if(str == null || str.length() == 0)
            return false;

        for(int i = 0; i < DialogFieldClientJavascript.eventList.length; i++)
        {
            if(str.equals(DialogFieldClientJavascript.eventList[i]))
                return true;
        }
        return false;
    }

    public SingleValueSource getType()
    {
        return type;
    }

    public SingleValueSource getEvent()
    {
        return event;
    }

    public SingleValueSource getScript()
    {
        return script;
    }

}