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
 * $Id: NavigationSkinPageResources.java,v 1.1 2003-01-06 17:19:40 roque.hernandez Exp $
 */
package com.netspective.sparx.xaf.skin;

import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.xaf.navigate.NavigationPath;
import com.netspective.sparx.xaf.navigate.NavigationPathContext;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationSkinPageResources implements FilenameFilter {

    protected String entityIcon;
    protected String actionIcon;
    protected String tabSeparator;
    protected String tabOn;
    protected String tabOff;
    protected String headingBackground;
    protected String headingMiddle;

    protected String appRootPath;
    protected String resourcesBasePath;
    protected String altResourcesBasePath;

    protected Map imagesIndex;

    private static NavigationSkinPageResources instance;

    private NavigationSkinPageResources() {
        resourcesBasePath = "/sparx/resources/skin/default/navigation/pages";
        altResourcesBasePath = "/resources/skin/default/navigation/pages";
    }

    private NavigationSkinPageResources(NavigationSkinPageResources parent) {
        resourcesBasePath = "/sparx/resources/skin/default/navigation/pages";
        altResourcesBasePath = "/resources/skin/default/navigation/pages";
        this.entityIcon = parent.entityIcon;
        this.actionIcon = parent.actionIcon;
        this.tabOn = parent.tabOn;
        this.tabOff = parent.tabOff;
        this.tabSeparator = parent.tabSeparator;
        this.headingBackground = parent.headingBackground;
        this.headingMiddle = parent.headingMiddle;
    }

    public void discoverResources(NavigationPathContext context) {

        ConfigurationManager manager = ConfigurationManagerFactory.getManager(context.getServletContext());
        appRootPath = manager.getDefaultConfiguration().getTextValue(context, "app.site-root-path");

        imagesIndex = new HashMap();
        discoverResources(null, context.getOwnerTree());
    }

    public void discoverResources(NavigationSkinPageResources parentResources, NavigationPath currentNavPath) {

        NavigationSkinPageResources currentPageResources = (parentResources == null ? new NavigationSkinPageResources() : new NavigationSkinPageResources(parentResources));

        String currentImagesPath = resourcesBasePath + (currentNavPath.getId() == null ? "/" : currentNavPath.getId());
        String currentNavPathId = currentNavPath.getId() == null ? "/" : currentNavPath.getId();

        // do what is needed for the currentPageId
        discoverPageResources(currentImagesPath, currentPageResources);

        //discover pages in alternate location
        currentImagesPath = altResourcesBasePath + (currentNavPath.getId() == null ? "/" : currentNavPath.getId());
        discoverPageResources(currentImagesPath, currentPageResources);

        //put the currentPageImageIndex in the imageIndex map
        //TODO: compare if there has been any changes to it, if not, then use the parent's reference instead of the newly created one.
        imagesIndex.put(currentNavPathId, currentPageResources);

        List children = currentNavPath.getChildrenList();
        // then get the children and loop over the children calling itself for every children
        for (int i = 0; i < children.size(); i++) {
            NavigationPath navPath = (NavigationPath) children.get(i);
            //call the same method for this page's children
            discoverResources(currentPageResources, navPath);
        }
    }

    private void discoverPageResources(String currentImagesPath, NavigationSkinPageResources currentPageResources) {

        File dir = new File(appRootPath + currentImagesPath);
        File[] files = dir.listFiles(this);

        for (int i = 0; files != null && i < files.length; i++) {
            File file = files[i];
            String fileName = file.getName();
            int extnIndex = fileName.lastIndexOf(".");
            String justNameNoExtn = extnIndex == -1 ? fileName : fileName.substring(0, extnIndex);

            if (justNameNoExtn.equals("action-icon")) {
                currentPageResources.actionIcon = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("entity-icon")) {
                currentPageResources.entityIcon = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("tab-separator")) {
                currentPageResources.tabSeparator = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("tab-on")) {
                currentPageResources.tabOn = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("tab-off")) {
                currentPageResources.tabOff = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("page-heading-background")) {
                currentPageResources.headingBackground = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            } else if (justNameNoExtn.equals("page-heading-middle")) {
                currentPageResources.headingMiddle = currentImagesPath + (currentImagesPath.endsWith("/") ? "" : "/") + fileName;
            }
        }
    }

    public boolean accept(File dir, String name) {
        if (name.equals("CVS"))
            return false;

        return (name.indexOf(".") != -1); //We only want FILES in the current folder not folders
    }


    public String getImagePath(String pageId, String imageId, NavigationPathContext nc) {

        //TODO: here is where we do the lazy initialization.  If images have not been discovered then discover them.
        //      just for right now it's discovering them everytime.  Here is where we would put in place a way for
        //      the developer to override and refresh everytime.

        discoverResources(nc);

        NavigationSkinPageResources pageResources = (NavigationSkinPageResources) imagesIndex.get(pageId);

        if (imageId.equals("action-icon")) {
            return pageResources.actionIcon;
        } else if (imageId.equals("entity-icon")) {
            return pageResources.entityIcon;
        } else if (imageId.equals("tab-separator")) {
            return pageResources.tabSeparator;
        } else if (imageId.equals("tab-on")) {
            return pageResources.tabOn;
        } else if (imageId.equals("tab-off")) {
            return pageResources.tabOff;
        } else if (imageId.equals("page-heading-background")) {
            return pageResources.headingBackground;
        } else if (imageId.equals("page-heading-middle")) {
            return pageResources.headingMiddle;
        } else {
            return null;
        }
    }

    //TODO: This may not have to be singleton, think about it.
    public static NavigationSkinPageResources getInstance() {

        if (instance == null)
            instance = new NavigationSkinPageResources();

        return instance;
    }

}
