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
 * $Id: GenerateBuildLogClassTask.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.util.ant;

import java.io.File;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.netspective.sparx.BuildConfiguration;

public class GenerateBuildLogClassTask extends Task
{
    static private final String DEFAULT_PACKAGE_NAME = "com.netspective.sparx.xaf";
    static private final String DEFAULT_CLASS_NAME = "BuildLog";

    private String logPackageName = DEFAULT_PACKAGE_NAME;
    private String logClassName = DEFAULT_CLASS_NAME;

    public GenerateBuildLogClassTask()
    {
    }

    public void init() throws BuildException
    {
        logPackageName = DEFAULT_PACKAGE_NAME;
        logClassName = DEFAULT_CLASS_NAME;
    }

    public void setPackage(String pgkName)
    {
        logPackageName = pgkName;
    }

    public void setClass(String className)
    {
        logClassName = className;
    }

    public void execute() throws BuildException
    {
        String directory = logPackageName.replace('.', '/');
        File javaFile = new File(directory, logClassName + ".java");

        try
        {
            Date buildDate = new Date();
            InetAddress localhost = InetAddress.getLocalHost();

            Writer writer = new java.io.FileWriter(javaFile);
            writer.write("package " + logPackageName + ";\n\n");
            writer.write("public class " + logClassName + "\n");
            writer.write("{\n");
            writer.write("  public final static String BUILD_HOST_NAME = \"" + localhost.getHostName() + "\";\n");
            writer.write("  public final static String BUILD_HOST_IP = \"" + localhost.getHostAddress() + "\";\n");
            writer.write("  public final static String BUILD_DATE = \"" + buildDate.toString() + "\";\n\n");
            writer.write("  public final static String BUILD_OS_NAME = \"" + System.getProperty("os.name") + "\";\n");
            writer.write("  public final static String BUILD_OS_VERSION = \"" + System.getProperty("os.version") + "\";\n\n");
            writer.write("  public final static String BUILD_JAVA_VERSION = \"" + System.getProperty("java.version") + "\";\n");
            writer.write("  public final static String BUILD_JAVA_VENDOR = \"" + System.getProperty("java.vendor") + "\";\n\n");
            writer.write("  public final static String BUILD_VM_NAME = \"" + System.getProperty("java.vm.name") + "\";\n");
            writer.write("  public final static String BUILD_VM_VERSION = \"" + System.getProperty("java.vm.version") + "\";\n");
            writer.write("  public final static String BUILD_VM_VENDOR = \"" + System.getProperty("java.vm.vendor") + "\";\n\n");

            writer.write("  public final static String[] BUILD_CLASS_PATH = new String[] {\n");
            BuildConfiguration.ClassPathInfo[] cpi = BuildConfiguration.getClassPaths();
            for(int i = 0; i < cpi.length; i++)
            {
                BuildConfiguration.ClassPathInfo info = cpi[i];
                if(i > 0)
                    writer.write(", \n");
                String path = info.getClassPath().getAbsolutePath();
                writer.write("      \"" + path.replace('\\', '/') + (info.isValid() ? "" : " (INVALID)") + "\"");
            }
            writer.write("      };\n");

            writer.write("}\n");
            writer.close();

            project.setProperty("build.log.host.name", localhost.getHostName());
            project.setProperty("build.log.host.ip", localhost.getHostAddress());
            project.setProperty("build.log.date", buildDate.toString());

            log("Generated " + javaFile.getAbsolutePath());
        }
        catch(Exception e)
        {
            throw new BuildException(e);
        }
    }
}
