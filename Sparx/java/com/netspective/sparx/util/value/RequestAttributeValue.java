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
 * $Id: RequestAttributeValue.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.util.value;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;

/**
 * Provides access to ServletRequest attributes; intelligently handles object of type String, String[], List, and Map.
 */

public class RequestAttributeValue extends ValueSource implements ListValueSource
{
    public RequestAttributeValue()
    {
        super();
    }

    public Documentation getDocumentation()
    {
        return new Documentation(
                "Provides access to ServletRequest attributes; intelligently handles object of types String, String[], List, and Map.",
                "attribute-name"
        );
    }

    public String getValue(ValueContext vc)
    {
        Object o = vc.getRequest().getAttribute(valueKey);
        if(o == null) return null;

        if(o instanceof String)
        {
            return (String) o;
        }
        else if(o instanceof String[])
        {
            String[] array = (String[]) o;
            return array.length > 0 ? array[0] : null;
        }
        else if(o instanceof List)
        {
            List list = (List) o;
            return list.size() > 0 ? (String) list.get(0) : null;
        }
        else
        {
            return o.toString();
        }
    }

    public Object getObjectValue(ValueContext vc)
    {
        return vc.getRequest().getAttribute(valueKey);
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        SelectChoicesList choices = new SelectChoicesList();
        Object o = vc.getRequest().getAttribute(valueKey);
        if(o instanceof Map)
        {
            Map map = (Map) o;
            for(Iterator i = map.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();
                choices.add(new SelectChoice(entry.getKey().toString(), entry.getValue().toString()));
            }
        }
        else
        {
            String[] values = getValues(vc);
            for(int i = 0; i < values.length; i++)
                choices.add(new SelectChoice(values[i]));
        }
        return choices;
    }

    public String[] getValues(ValueContext vc)
    {
        Object o = vc.getRequest().getAttribute(valueKey);
        if(o == null) return null;

        if(o instanceof String[])
        {
            return (String[]) o;
        }
        else if(o instanceof List)
        {
            List list = (List) o;
            String[] result = new String[list.size()];
            return (String[]) list.toArray(result);
        }
        else
        {
            return new String[]{o.toString()};
        }
    }

    public boolean supportsSetValue()
    {
        return true;
    }

    public void setValue(ValueContext vc, Object value)
    {
        vc.getRequest().setAttribute(valueKey, value);
    }
}