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
 * $Id: DocumentsPage.java,v 1.11 2002-12-28 20:07:36 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.netspective.sparx.ace.AceServletPage;
import com.netspective.sparx.ace.AppComponentsExplorerServlet;
import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.Property;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.xaf.html.SyntaxHighlight;
import com.netspective.sparx.xaf.navigate.*;

public class DocumentsPage extends AceServletPage
{
    private String name;
    private String caption;
    private String ref;
    private boolean isFileRef;
    private FileSystemContext fsContext;
    private boolean transformersMapInitialized;
    private Map transformersMap;

    public DocumentsPage()
    {
        super();
    }

    public DocumentsPage(String name, String dest)
    {
        int sepPos = name.indexOf(",");
        if(sepPos != -1)
        {
            this.name = name.substring(0, sepPos);
            this.caption = name.substring(sepPos + 1);
        }
        else
        {
            this.name = name;
            this.caption = name;
        }
        ref = dest;
        File file = new File(dest);
        isFileRef = file.exists();
    }

    public final String getName()
    {
        return name == null ? "documents" : name;
    }

    public final String getEntityImageUrl()
    {
        return "docs_project.gif";
    }

    public final String getCaption(ValueContext vc)
    {
        return caption == null ? "Documents" : caption;
    }

    public final String getHeading(ValueContext vc)
    {
        return getCaption(vc);
    }

    public final boolean isFileRef()
    {
        return isFileRef;
    }

    public void handlePage(Writer writer, NavigationPathContext nc) throws NavigationPageException, IOException
    {
        if(!transformersMapInitialized)
        {
            transformersMapInitialized = true;
            Configuration appConfig = ((AppComponentsExplorerServlet) nc.getServlet()).getAppConfig();
            Collection transformers = appConfig.getValues(nc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "transform");
            if(transformers != null)
            {
                transformersMap = new HashMap();
                for(Iterator i = transformers.iterator(); i.hasNext();)
                {
                    Object entry = i.next();
                    if(entry instanceof Property)
                    {
                        Property extensionInfo = (Property) entry;
                        String fileExtn = extensionInfo.getName();
                        String styleSheetName = appConfig.getValue(nc, extensionInfo, null);
                        transformersMap.put(fileExtn, styleSheetName);
                    }
                }
            }
        }

        String browseDoc = nc.getRequest().getParameter("browseDoc");
        if(browseDoc != null)
        {
            File browseFile = new File(browseDoc);
            String browseFilePath = browseFile.getAbsolutePath();
            String servletPath = nc.getServletContext().getRealPath("");
            if(browseFilePath.startsWith(servletPath))
            {
                if(handleDocument(writer, nc, browseFile, true))
                    return;
                else
                    throw new NavigationPageException("Don't know how to render " + browseFilePath);
            }
            else
                throw new NavigationPageException("File '"+ browseFilePath +"' is not within the context path '"+ servletPath +"'.");
        }

        if(!isFileRef)
        {
            ((HttpServletResponse) nc.getResponse()).sendRedirect(ref);
            return;
        }

        NavigationPath.FindResults activePath = nc.getActivePathFindResults();
        String relativePath = activePath.getUnmatchedPath();

        if(fsContext == null)
        {
            fsContext = new FileSystemContext(
                    activePath.getMatchedPath().getAbsolutePath(nc),
                    ref, getCaption(nc), relativePath);
        }
        else
            fsContext.setRelativePath(relativePath);

        FileSystemEntry activeEntry = fsContext.getActivePath();
        if(activeEntry.isDirectory())
        {
            Document navgDoc = null;
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                navgDoc = builder.newDocument();
            }
            catch(Exception e)
            {
                throw new NavigationPageException(e);
            }

            Element navgRootElem = navgDoc.createElement("xaf");
            navgDoc.appendChild(navgRootElem);

            fsContext.addXML(navgRootElem, fsContext);

            handlePageMetaData(writer, nc);
            handlePageHeader(writer, nc);
            transform(nc, navgDoc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "docs-browser-xsl");
            handlePageFooter(writer, nc);
        }
        else
        {
            if(! handleDocument(writer, nc, activeEntry, false))
            {
                if(transformersMap != null)
                {
                    String transformUsingStyleSheet = (String) transformersMap.get(activeEntry.getEntryType());
                    if(transformUsingStyleSheet != null)
                    {
                        handlePageMetaData(writer, nc);
                        handlePageHeader(writer, nc);
                        transform(nc, activeEntry, transformUsingStyleSheet);
                        handlePageFooter(writer, nc);
                    }
                    else
                        activeEntry.send((HttpServletResponse) nc.getResponse());
                }
                else
                    activeEntry.send((HttpServletResponse) nc.getResponse());
            }
        }
    }

    private boolean handleDocument(Writer writer, NavigationPathContext nc, File activeEntry, boolean browsing) throws NavigationPageException, IOException
    {
        if(activeEntry.getAbsolutePath().indexOf("javadoc") != -1)
            return false;

        StringWriter html = new StringWriter();
        if(! SyntaxHighlight.emitHtml(activeEntry, html))
            return false;

        handlePageMetaData(writer, nc);
        handlePageHeader(writer, nc);

        if(! browsing)
        {
            Document navgDoc = null;
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                navgDoc = builder.newDocument();
            }
            catch(Exception e)
            {
                throw new NavigationPageException(e);
            }

            Element navgRootElem = navgDoc.createElement("xaf");
            navgDoc.appendChild(navgRootElem);

            fsContext.addXML(navgRootElem, fsContext);
            transform(nc, navgDoc, com.netspective.sparx.Globals.ACE_CONFIG_ITEMS_PREFIX + "docs-browser-xsl");
        }

        PrintWriter out = nc.getResponse().getWriter();
        out.print("&nbsp;&nbsp;<b><code>"+ activeEntry.getAbsolutePath() +"</code></b>");
        out.print("<table border=0 cellspacing=5><tr><td>");
        out.print(html.toString());
        out.print("</td</tr></table>");

        handlePageFooter(writer, nc);

        return true;
    }
}
