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
 * $Id: ImportConfigurationTask.java,v 1.2 2002-08-08 14:42:22 shahid.shah Exp $
 */

package com.netspective.sparx.util.ant;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManager;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.config.Property;

public class ImportConfigurationTask extends Task
{
    private String file;
    private String prefix = "config.";
    private boolean debug = false;

    public ImportConfigurationTask()
    {
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public void execute() throws BuildException
    {
        // because there's no "servlet context" available from Ant (command line) we need to simulate it so that if the
        // configuration items refer to the value source servlet-context-path the variables should still work
        File simulatedPath = new File(project.getProperty("app.root.dir"));
        System.setProperty("com.netspective.sparx.util.value.ServletContextPathValue.simulate", simulatedPath.getAbsolutePath());

        ConfigurationManager manager = ConfigurationManagerFactory.getManager(file);

        List errors = manager.getErrors();
        if(errors.size() > 0)
        {
            for(Iterator ei = errors.iterator(); ei.hasNext();)
                log("Configuration Warning: " + (String) ei.next());
        }

        int imported = 0;
        Configuration defaultConfig = manager.getDefaultConfiguration();
        for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry configEntry = (Map.Entry) i.next();

            // we only handle single-value items (not lists)
            if(configEntry.getValue() instanceof Property)
            {
                Property property = (Property) configEntry.getValue();
                String antPropertyName = prefix + property.getName();
                if(!property.flagIsSet(Property.PROPFLAG_IS_DYNAMIC))
                {
                    try
                    {
                        project.setProperty(antPropertyName, defaultConfig.getTextValue(null, property.getName()));
                        if(debug) log(antPropertyName + " = " + project.getProperty(antPropertyName));
                        imported++;
                    }
                    catch(Exception e)
                    {
                        // ignore any properties that we can't import
                    }
                }
            }
        }

        log("Imported " + imported + " configuration items from '" + manager.getSourceDocument().getFile().getAbsolutePath() + "' (prefix = '" + prefix + "')");
    }
}