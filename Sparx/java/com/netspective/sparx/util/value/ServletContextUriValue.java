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
 * $Id: ServletContextUriValue.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.value;

import javax.servlet.http.HttpServletRequest;

public class ServletContextUriValue extends ValueSource
{
    public final int URITYPE_ROOT = 0;
    public final int URITYPE_ACTIVE_SERVLET = 1;
    public final int URITYPE_CUSTOM_FROM_ROOT = 2;
    public final int URITYPE_CUSTOM_FROM_SERVLET = 3;

    private int type;

    public ServletContextUriValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Generates a URL based on the current application by automatically prepending the default servlet URL " +
                "to the expression provided. There are four styles that can be used:\n" +
                "<ol>\n" +
                "<li><code><b>/</b></code> is used when you want to refer to the root path of your application</li>\n" +
                "<li>The identifier <code><b>active-servlet</b></code> is used if you want to refer to the actively running servlet</li>\n" +
                "<li>Any URL starting with a slash is considered a URL that is relative to the root path of your application</li>\n" +
                "<li>Any URL not starting with a slash is considered a URL that is relative to the current servlet</li>\n" +
                "</ol>\n",
                new String[]{"/", "<u>active-servlet</u>", "/absolute/url", "relative/url"}
        );
    }

    public void initializeSource(String srcParams)
    {
        super.initializeSource(srcParams);
        type = URITYPE_ROOT;
        if(srcParams.equals("/"))
            type = URITYPE_ROOT;
        else if(srcParams.equals("active-servlet"))
            type = URITYPE_ACTIVE_SERVLET;
        else
        {
            if(srcParams.startsWith("/"))
                type = URITYPE_CUSTOM_FROM_ROOT;
            else
                type = URITYPE_CUSTOM_FROM_SERVLET;
        }
    }

    public String getValue(ValueContext vc)
    {
        HttpServletRequest request = (HttpServletRequest) vc.getRequest();
        if(request == null)
            return "ValueContext.getRequest() is NULL in " + getId();

        String contextPath = request.getContextPath();
        switch(type)
        {
            case URITYPE_ROOT:
                return contextPath;

            case URITYPE_ACTIVE_SERVLET:
                return contextPath + request.getServletPath();

            case URITYPE_CUSTOM_FROM_ROOT:
                return contextPath + valueKey;

            case URITYPE_CUSTOM_FROM_SERVLET:
                return contextPath + request.getServletPath() + valueKey;
        }

        return contextPath;
    }
}