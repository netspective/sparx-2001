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
 * $Id: ClassPathTask.java,v 1.4 2002-09-05 14:45:34 shahid.shah Exp $
 */

package com.netspective.sparx.util.ant;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import com.netspective.sparx.BuildConfiguration;
import com.netspective.sparx.util.ClassPath;

public class ClassPathTask extends Task
{
    private boolean listAll;
    private String showLocOfClass;
    private Path additionalClassPath;

    public ClassPathTask()
    {
    }

    public void init() throws BuildException
    {
        listAll = false;
        showLocOfClass = null;
    }

    public void setListall(boolean list)
    {
        listAll = list;
    }

    public void setClass(String className)
    {
        showLocOfClass = project.replaceProperties(className);
    }

    /**
     * Set the additional class path reference by optimizing the items and replacing the reference
     */
    public void setAdditionalRef(Reference r)
    {
        Path ref = (Path) r.getReferencedObject(project);
        Path newRef = new Path(project, optimizePath(ref.list()));
        newRef.concatSystemClasspath();
        project.getReferences().remove(r.getRefId());
        project.addReference(r.getRefId(), newRef);
        createAdditionalClasspath().setRefid(r);
    }

    /**
     * Make sure that the items in the path are not duplicated. Duplicates are checked by simple string comparison
     * for directory paths and checksums for actual files.
     */
    public String optimizePath(String[] items)
    {
        StringBuffer finalList = new StringBuffer();
        Set itemSet = new HashSet();
        Map filesMap = new HashMap();

        for(int i = 0; i < items.length; i++)
        {
            String item = items[i];
            if(itemSet.contains(item))
                continue;

            File file = new File(item);
            if(file.isFile() && file.exists())
            {
                // Compute Adler-32 checksum of the file
                try
                {
                    CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new Adler32());
                    byte[] tempBuf = new byte[512];
                    while (cis.read(tempBuf) >= 0) { }

                    String fileName = file.getName();
                    Checksum existingChecksum = (Checksum) filesMap.get(fileName);

                    if(existingChecksum != null && cis.getChecksum().getValue() == existingChecksum.getValue())
                        continue;
                    else
                        filesMap.put(fileName, cis.getChecksum());
                }
                catch(IOException ioe)
                {
                    // we're going to eat the error, but keep the item in the finalList
                }
            }

            finalList.append(item);
            finalList.append(File.pathSeparator);
            itemSet.add(item);
        }

        return finalList.toString();
    }

    public Path createAdditionalClasspath()
    {
        if (additionalClassPath == null)
            additionalClassPath = new Path(project);
        return additionalClassPath.createPath();
    }

    public void execute() throws BuildException
    {
        if(listAll)
        {
            ClassPath.ClassPathInfo[] cpi = additionalClassPath != null ?
                ClassPath.getClassPaths(additionalClassPath.toString()) :
                ClassPath.getClassPaths();

            for(int i = 0; i < cpi.length; i++)
            {
                ClassPath.ClassPathInfo info = cpi[i];
                log(info.getClassPath().getAbsolutePath() + (info.isValid() ? "" : " (INVALID)"));
            }
        }

        if(showLocOfClass != null)
        {
            log(showLocOfClass + " is " + ClassPath.getClassFileName(showLocOfClass));
        }
    }
}