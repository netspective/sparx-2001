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
 * $Id: ClassPath.java,v 1.3 2002-11-30 16:36:47 shahid.shah Exp $
 */

package com.netspective.sparx.util;

import com.netspective.sparx.util.log.LogManager;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClassPath
{
    /**
     * Prints the absolute pathname of the class file containing the specified class name, as prescribed by
     * the class path.
     *
     * @param className Name of the class.
     */
    public static String getClassFileName(String className)
    {

        String resource = new String(className);

        if(!resource.startsWith("/"))
            resource = "/" + resource;

        resource = resource.replace('.', '/');
        resource = resource + ".class";

        java.net.URL classUrl = ClassPath.class.getResource(resource);

        if(classUrl == null)
            return null;
        else
            return classUrl.getFile();
    }

    static public class InstanceGenerator
    {
        private String className;
        private Class defaultClass;
        private Object instance;
        private Exception error;

        public InstanceGenerator(String className, Class defaultClass, boolean validate)
        {
            this.className = className;
            this.defaultClass = defaultClass;

            Class activeClass = null;
            if(className == null || className.length() == 0)
                activeClass = defaultClass;
            else
            {
                try
                {
                    activeClass = Class.forName(className);
                }
                catch (ClassNotFoundException e)
                {
                    error = e;
                    return;
                }
            }

            try
            {
                instance = activeClass.newInstance();
            }
            catch (InstantiationException e)
            {
                error = e;
                return;
            }
            catch (IllegalAccessException e)
            {
                error = e;
                return;
            }

            if(! defaultClass.isInstance(instance))
                error = new ClassCastException("class "+ className +" must extend class "+ defaultClass.getName() +".");

            if(validate && error != null)
            {
                LogManager.recordException(this.getClass(), "constructor", "class = '"+ className +"', default = '"+ defaultClass +"'", error);
                throw getException();
            }
        }

        public String getClassName()
        {
            return className;
        }

        public Class getDefaultClass()
        {
            return defaultClass;
        }

        public boolean isValid()
        {
            return error == null;
        }

        public RuntimeException getException()
        {
            return new RuntimeException(error.toString());
        }

        public Object getInstance()
        {
            return instance;
        }
    }

    static public class ClassPathInfo
    {
        private File classPath;
        private boolean isValid;
        private boolean isDirectory;
        private boolean isJar;
        private boolean isZip;

        public ClassPathInfo(String path)
        {
            classPath = new File(path);

            if(classPath.exists())
            {
                if(classPath.isDirectory())
                {
                    isValid = true;
                    isDirectory = true;
                }
                else
                {
                    isValid = true;
                    String pathLower = path.toLowerCase();
                    if(pathLower.endsWith(".jar"))
                        isJar = true;
                    else if(pathLower.endsWith(".zip"))
                        isZip = true;
                    else
                        isValid = false;
                }
            }
            else
                isValid = false;
        }

        public File getClassPath()
        {
            return classPath;
        }

        public boolean isValid()
        {
            return isValid;
        }

        public boolean isDirectory()
        {
            return isDirectory;
        }

        public boolean isJar()
        {
            return isJar;
        }

        public boolean isZip()
        {
            return isZip;
        }
    }

    public static void addClassPaths(List classPathList, String path)
    {
        StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
        while(tokenizer.hasMoreTokens())
        {
            String pathName = tokenizer.nextToken();
            classPathList.add(new ClassPathInfo(pathName));
        }
    }

    public static ClassPathInfo[] getClassPaths(String[] paths)
    {
        List classPathList = new ArrayList();
        for(int i = 0; i < paths.length; i++)
            addClassPaths(classPathList, paths[i]);
        if(classPathList.size() == 0)
            return null;

        return (ClassPathInfo[]) classPathList.toArray(new ClassPathInfo[classPathList.size()]);
    }

    public static ClassPathInfo[] getClassPaths(String path)
    {
        List classPathList = new ArrayList();
        addClassPaths(classPathList, path);
        if(classPathList.size() == 0)
            return null;

        return (ClassPathInfo[]) classPathList.toArray(new ClassPathInfo[classPathList.size()]);
    }

    public static ClassPathInfo[] getClassPaths()
    {
        return getClassPaths(System.getProperty("java.class.path"));
    }
}
