/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Dec 26, 2001
 * Time: 1:25:54 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import com.xaf.BuildConfiguration;

import java.io.File;
import java.io.Writer;
import java.util.Date;
import java.net.InetAddress;

public class GenerateBuildLogClassTask extends Task
{
    static private final String DEFAULT_PACKAGE_NAME = "com.xaf";
    static private final String DEFAULT_CLASS_NAME = "BuildLog";

    private String logPackageName =  DEFAULT_PACKAGE_NAME;
    private String logClassName = DEFAULT_CLASS_NAME;

    public GenerateBuildLogClassTask()
    {
    }

    public void init() throws BuildException
    {
        logPackageName =  DEFAULT_PACKAGE_NAME;
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
            writer.write("  public final static String BUILD_HOST_NAME = \""+ localhost.getHostName() +"\";\n");
            writer.write("  public final static String BUILD_HOST_IP = \""+ localhost.getHostAddress() +"\";\n");
            writer.write("  public final static String BUILD_DATE = \""+ buildDate.toString() +"\";\n\n");
            writer.write("  public final static String BUILD_OS_NAME = \""+ System.getProperty("os.name") +"\";\n");
            writer.write("  public final static String BUILD_OS_VERSION = \""+ System.getProperty("os.version") +"\";\n\n");
            writer.write("  public final static String BUILD_JAVA_VERSION = \""+ System.getProperty("java.version") +"\";\n");
            writer.write("  public final static String BUILD_JAVA_VENDOR = \""+ System.getProperty("java.vendor") +"\";\n\n");
            writer.write("  public final static String BUILD_VM_NAME = \""+ System.getProperty("java.vm.name") +"\";\n");
            writer.write("  public final static String BUILD_VM_VERSION = \""+ System.getProperty("java.vm.version") +"\";\n");
            writer.write("  public final static String BUILD_VM_VENDOR = \""+ System.getProperty("java.vm.vendor") +"\";\n\n");

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
