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
 * $Id: GenerateValueSourcesDocumentation.java,v 1.1 2002-01-20 14:53:20 snshah Exp $
 */

package com.netspective.sparx.util.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

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

                    writer.write("\t<tr class=\"data\" valign=\"top\"><td class=\"data\"><nobr>" + vsId + "</nobr></td><td class=\"data_check\">" + (isSVS ? "S" : "<space/>") + "</td><td class=\"data_check\">" + (isLVS ? "L" : "<space/>") + "</td><td class=\"data_check\">" + (allowWrite ? "RW" : "<space/>") + "</td><td class=\"data_code\">" + (doc != null ? doc.getParamsHtml(vsId) : "<space/>") + "</td><td class=\"data\">" + (doc != null ? doc.getDescription() : "<space/>") + "</td></tr>\n");
                }
                catch(Exception e)
                {
                    writer.write("\t<tr class=\"data\" valign=\"top\"><td class=\"data\"><nobr>" + vsId + "</nobr></td><td colspan=\"5\" class=\"data\">" + e.toString() + "</td></tr>\n");
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
