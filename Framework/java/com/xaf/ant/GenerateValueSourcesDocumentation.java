/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Dec 8, 2001
 * Time: 2:27:12 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.ant;

import org.apache.tools.ant.BuildException;

import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.io.*;

import org.apache.tools.ant.Task;

import com.xaf.value.ValueSourceFactory;
import com.xaf.value.SingleValueSource;
import com.xaf.value.ListValueSource;

/**
 * Generates a file of all of the Value Sources that is then included into the build cycle of the Netspective
 * website generator. This class will read the documentation for each value source and create a file suitable
 * for use on the website.
 */

public class GenerateValueSourcesDocumentation extends Task
{
    private String outputFile;

    public GenerateValueSourcesDocumentation()
    {
    }

    public void setOutput(String fileName)
    {
        outputFile = fileName;
    }

    public void execute() throws BuildException
	{
        File file = new File(outputFile);
        try
        {
            Writer writer = new FileWriter(file);

            writer.write("<table class=\"data\" cellspacing=\"0\" cellpadding=\"3\">\n");
            writer.write("\t<tr class=\"data_head\" valign=\"top\"><th class=\"data\">Name</th><th class=\"data\">SVS</th><th class=\"data\">LVS</th><th class=\"data\">RW</th><th class=\"data\">Usage</th><th class=\"data\">Description</th></tr>\n");
            Map valueSrcClasses = ValueSourceFactory.getValueSourceClasses();
            String[] vsNames = (String[]) valueSrcClasses.keySet().toArray(new String[valueSrcClasses.size()]);
            Arrays.sort(vsNames);

            for(int i = 0; i < vsNames.length; i++)
            {
                String vsId = vsNames[i];
                Class vsClass = (Class) valueSrcClasses.get(vsId);

                try
                {
                    Object inst = vsClass.newInstance();

                    boolean isSVS = inst instanceof SingleValueSource;
                    boolean isLVS = inst instanceof ListValueSource;

                    SingleValueSource.Documentation doc = isSVS ? ((SingleValueSource) inst).getDocumentation() : ((ListValueSource) inst).getDocumentation();
                    boolean allowWrite = isSVS ? ((SingleValueSource) inst).supportsSetValue() : false;

                    writer.write("\t<tr class=\"data\" valign=\"top\"><td class=\"data\"><nobr>"+ vsId +"</nobr></td><td class=\"data_check\">"+ (isSVS ? "S" : "<space/>") +"</td><td class=\"data_check\">"+ (isLVS ? "L" : "<space/>") +"</td><td class=\"data_check\">"+ (allowWrite ? "RW" : "<space/>") +"</td><td class=\"data_code\">"+ (doc != null ? doc.getParamsHtml(vsId) : "<space/>") +"</td><td class=\"data\">"+ (doc != null ? doc.getDescription() : "<space/>") +"</td></tr>\n");
                }
                catch(Exception e)
                {
                    writer.write("\t<tr class=\"data\" valign=\"top\"><td class=\"data\"><nobr>"+ vsId +"</nobr></td><td colspan=\"5\" class=\"data\">"+ e.toString() +"</td></tr>\n");
                }
            }

            writer.write("</table>\n");
            writer.close();
        }
        catch(IOException e)
        {
            throw new BuildException(e);
        }

        log("Generated value sources documention in " + file.getAbsolutePath());
    }
}
