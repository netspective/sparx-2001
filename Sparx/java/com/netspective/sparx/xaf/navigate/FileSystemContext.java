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
 * $Id: FileSystemContext.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.navigate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileSystemContext implements FilenameFilter
{
    private String rootURI;
    private FileSystemEntry rootPath;
    private FileSystemEntry activePath;
    private String relativePathStr;

    public FileSystemContext(String aRootURI, String aRootPathStr, String aRootCaption, String aRelativePathStr)
    {
        rootURI = aRootURI;
        rootPath = new FileSystemEntry(null, aRootPathStr);
        rootPath.setEntryCaption(aRootCaption);
        setRelativePath(aRelativePathStr);
    }

    public final String getRootURI()
    {
        return rootURI;
    }

    public void setRootURI(String value)
    {
        rootURI = value;
    }

    public final FileSystemEntry getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(FileSystemEntry value)
    {
        rootPath = value;
    }

    public final FileSystemEntry getActivePath()
    {
        return activePath;
    }

    public void setActivePath(FileSystemEntry value)
    {
        activePath = value;
    }

    public final String getRelativePath()
    {
        return relativePathStr;
    }

    public void setRelativePath(String value)
    {
        relativePathStr = value;
        if(relativePathStr != null && (relativePathStr.equals("") || relativePathStr.equals("/")))
            relativePathStr = null;

        if(relativePathStr != null)
            activePath = new FileSystemEntry(rootPath, rootPath.getAbsolutePath() + rootPath.separator + relativePathStr);
        else
            activePath = rootPath;
    }

    /* called in FileSystemContext when Filenames need to be filtered */
    public boolean accept(File dir, String name)
    {
        return true;
    }

    public void addXML(Element fsElem, FilenameFilter filter)
    {
        Document doc = fsElem.getOwnerDocument();

        Element pathElem = (Element) fsElem.appendChild(doc.createElement("path"));
        pathElem.setAttribute("caption", activePath.getEntryCaption());
        pathElem.setAttribute("url", activePath.getEntryURI());
        pathElem.setAttribute("path", activePath.getAbsolutePath());

        Element parents = (Element) pathElem.appendChild(doc.createElement("parents"));
        ArrayList parentList = activePath.getParents();
        if(parentList != null)
        {
            Iterator i = parentList.iterator();
            int level = 1;
            while(i.hasNext())
            {
                FileSystemEntry entry = (FileSystemEntry) i.next();
                Element parent = (Element) parents.appendChild(doc.createElement("parent"));
                parent.setAttribute("level", new Integer(level).toString());
                parent.setAttribute("caption", entry.getEntryCaption());
                parent.setAttribute("url", rootURI + entry.getEntryURI());
                parent.setAttribute("path", entry.getAbsolutePath());
                parent.setAttribute("isroot", new Boolean(entry.isRoot()).toString());
                parent.setAttribute("islast", new Boolean(!i.hasNext()).toString());

                level++;
            }
        }

        Element folders = (Element) pathElem.appendChild(doc.createElement("folders"));
        Element files = (Element) pathElem.appendChild(doc.createElement("files"));

        File[] entries = activePath.listFiles(filter);
        if(entries != null)
        {
            for(int i = 0; i < entries.length; i++)
            {
                FileSystemEntry entry = new FileSystemEntry(rootPath, entries[i].getAbsolutePath());
                Element entryElem = null;
                if(entry.isDirectory())
                    entryElem = (Element) folders.appendChild(doc.createElement("folder"));
                else
                {
                    entryElem = (Element) files.appendChild(doc.createElement("file"));
                    entryElem.setAttribute("type", entry.getEntryType());
                }
                entryElem.setAttribute("caption", entry.getEntryCaption());
                entryElem.setAttribute("type", entry.getEntryType());
                entryElem.setAttribute("url", rootURI + entry.getEntryURI());
                entryElem.setAttribute("path", entry.getAbsolutePath());
            }
        }
    }
}