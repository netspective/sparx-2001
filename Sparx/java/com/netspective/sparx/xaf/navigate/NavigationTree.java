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

package com.netspective.sparx.xaf.navigate;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FilenameFilter;

public class NavigationTree extends NavigationPage {

    protected Map resources;

    public void discoverResources(String appRoot, String rootUrl, Map inheritResources) {
        if (resources == null)
            resources = new HashMap();

        File dir = new File(appRoot + rootUrl);
        discoverResources(inheritResources, rootUrl, "/", dir);
    }

    protected void discoverResources(Map inheritResources, String rootUrl, String currentPathId, File dir) {

        File[] files = dir.listFiles();

        Map singlePathResources = (inheritResources == null ? new HashMap() : (inheritResources.get(currentPathId) == null ? new HashMap() : (Map)inheritResources.get(currentPathId) ) );

        for (int i = 0; files!= null && i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                discoverResources(inheritResources, rootUrl, currentPathId + (currentPathId.endsWith("/") ? "" : "/") + file.getName(), file);
            }
            else {
                String fileName = file.getName();
                int extnIndex = fileName.lastIndexOf(".");
                String justNameNoExtn = extnIndex == -1 ? fileName : fileName.substring(0, extnIndex);
                singlePathResources.put(justNameNoExtn, rootUrl + currentPathId + (currentPathId.endsWith("/") ? "" : "/") + fileName);
            }
        }
        resources.put(currentPathId, singlePathResources);
    }

    public Map getResources() {
        return resources;
    }

    public void resolveResources() {
        List children = this.getChildrenList();
        for (int i = 0; i < children.size(); i++) {
            NavigationPath navPath = (NavigationPath) children.get(i);
            navPath.resolveResources(getResources());
        }
    }

}
