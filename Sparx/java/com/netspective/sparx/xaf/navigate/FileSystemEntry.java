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
 * $Id: FileSystemEntry.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.navigate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

public class FileSystemEntry extends File
{
    static public final String ROOT_URI = "/";
    static public final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private FileSystemEntry rootPath;
    private String entryCaption;
    private String entryExtension;
    private String entryURI = ROOT_URI;
    private String contentType = DEFAULT_CONTENT_TYPE;

    FileSystemEntry(FileSystemEntry aRootPath, String s)
    {
        super(s);
        rootPath = aRootPath;
        entryCaption = getName().replace('_', ' ');
        int extnIndex = entryCaption.lastIndexOf('.');
        if(extnIndex > -1)
        {
            entryExtension = entryCaption.substring(extnIndex + 1);
            entryCaption = entryCaption.substring(0, extnIndex);
        }
        else
            entryExtension = "";

        // find the "relative" portion of the path (everything but the root path)
        if(rootPath != null)
        {
            String absPath = getAbsolutePath();
            entryURI = absPath.substring(rootPath.getAbsolutePath().length()).replace('\\', '/');
        }
    }

    public final String getEntryCaption()
    {
        return entryCaption;
    }

    public void setEntryCaption(String value)
    {
        entryCaption = value;
    }

    public final String getEntryType()
    {
        return entryExtension;
    }

    public void setEntryType(String value)
    {
        entryExtension = value;
    }

    public final String getEntryURI()
    {
        return entryURI;
    }

    public void setEntryURI(String value)
    {
        entryURI = value;
    }

    public final boolean isRoot()
    {
        return rootPath == null;
    }

    public ArrayList getParents()
    {
        if(rootPath == null)
            return null;

        String rootPathStr = rootPath.getAbsolutePath();
        ArrayList parents = new ArrayList();
        File parent = getParentFile();
        while(parent != null)
        {
            boolean isRootPath = parent.getAbsolutePath().equals(rootPathStr);
            if(parent.getAbsolutePath().equals(rootPathStr))
            {
                parents.add(0, rootPath);
                break;
            }
            else
            {
                parents.add(0, new FileSystemEntry(isRootPath ? null : rootPath, parent.getAbsolutePath()));
                parent = parent.getParentFile();
            }
        }

        return parents;
    }

    public String findInPath(String fileName)
    {
        String testFile = getAbsolutePath() + separator + fileName;
        if(new File(testFile).exists())
            return testFile;

        if(rootPath == null)
            return null;

        String rootPathStr = rootPath.getAbsolutePath();
        File parent = getParentFile();
        while(parent != null)
        {
            testFile = parent.getAbsolutePath() + parent.separator + fileName;
            if(new File(testFile).exists())
                return testFile;

            boolean isRootPath = parent.getAbsolutePath().equals(rootPathStr);
            if(parent.getAbsolutePath().equals(rootPathStr))
                break;
            else
                parent = parent.getParentFile();
        }

        return null;
    }

    public void send(HttpServletResponse response) throws IOException
    {
        int sentsize = 0;

        InputStream in = new BufferedInputStream(new FileInputStream(getAbsolutePath()));

        response.setContentType(contentType);
        response.setContentLength((int) length()); // seems that here sits a possible problem for files >= 2 GB...

        OutputStream out = response.getOutputStream();

        int readlen;
        byte buffer[] = new byte[256];

        while((readlen = in.read(buffer)) != -1)
        {
            // throws IOException: broken pipe when download is canceled.
            out.write(buffer, 0, readlen);
            sentsize += readlen;
        }

        // Success ! Close streams.
        out.flush();
        out.close();

        in.close();
    }

}
