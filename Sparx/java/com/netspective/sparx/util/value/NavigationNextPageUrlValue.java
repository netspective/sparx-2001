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
 * $Id: NavigationNextPageUrlValue.java,v 1.1 2003-02-03 00:34:24 shahid.shah Exp $
 */

package com.netspective.sparx.util.value;

import com.netspective.sparx.xaf.navigate.NavigationTreeManagerFactory;
import com.netspective.sparx.xaf.navigate.NavigationTreeManager;
import com.netspective.sparx.xaf.navigate.NavigationPath;

public class NavigationNextPageUrlValue extends NavigationPageUrlCmdValue
{

    public NavigationNextPageUrlValue()
    {
        super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Provides access to the URL defined in a NavigationTree (WEB-INF/ui/structure.xml) for the next page. If " +
                "no source-name is provided the navigation-id requested is read from the default NavigationTreeManager " +
                "of the default configuration file. If a source-name is provided, then the property-name is read from the " +
                "NavigationTreeManager named source-name in the default configuration file.",
                new String[]{"navigation-id", "source-name/navigation-id"}
        );
    }

    public String getValue(ValueContext vc)
    {
        NavigationPath navTree = null;
        if (source == null || source.length() == 0)
        {
            navTree = NavigationTreeManagerFactory.getNavigationTree(vc.getServletContext());
            if (navTree == null)
                return "No default NavigationTree found in " + getId();
        }
        else
        {
            NavigationTreeManager manager = NavigationTreeManagerFactory.getManager(vc.getServletContext());
            if (manager == null)
                return "No NavigationTreeManager found in " + getId();
            navTree = manager.getTree(source);
            if (navTree == null)
                return "No '" + source + "' Configuration found in " + getId();
        }

        NavigationPath activePath = (NavigationPath) navTree.getAbsolutePathsMap().get(navId);
        if (activePath == null)
            return "Path '" + navId + "' not found.";

        NavigationPath nextPath = activePath.getNextPath();
        if(nextPath == null)
            return "Path '" + navId + "' has no next path.";

        String navUrlAndParams = nextPath.getUrl(vc);

        int endOfNavUrlPos = navUrlAndParams.indexOf('?') < 0 ? navUrlAndParams.length() : navUrlAndParams.indexOf('?');

        String navUrl = navUrlAndParams.substring(0, endOfNavUrlPos);

        String navUrlParams = null;

        if (endOfNavUrlPos < 0)
        {
            navUrlParams = "";
        }
        else
        {
            navUrlParams = navUrlAndParams.substring(navUrlAndParams.indexOf('?') + 1);

        }

        String localParams = "";
        if (reqParams != null)
        {
            localParams = reqParams.getValue(vc);
        }

        String finalParams = resolveRequestPrameters(navUrlParams, localParams);

        return navUrl + (finalParams != null && finalParams.length() > 0 ? "?" + finalParams : "");
    }
}
