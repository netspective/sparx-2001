package com.xaf.ant;

import org.apache.tools.ant.*;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import com.xaf.*;
import com.xaf.config.*;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

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
        ConfigurationManager manager = ConfigurationManagerFactory.getManager(file);

        List errors = manager.getErrors();
        if(errors.size() > 0)
		{
			for(Iterator ei = errors.iterator(); ei.hasNext(); )
                log("Configuration Warning: " + (String) ei.next());
		}

        int imported = 0;
		Configuration defaultConfig = manager.getDefaultConfiguration();
		for(Iterator i = defaultConfig.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry configEntry = (Map.Entry) i.next();

            // we only handle single-value items (not lists)
			if(configEntry.getValue() instanceof Property)
			{
				Property property = (Property) configEntry.getValue();
                String antPropertyName = prefix + property.getName();
                if(! property.flagIsSet(Property.PROPFLAG_IS_DYNAMIC))
                {
                    try
                    {
                        project.setProperty(antPropertyName, defaultConfig.getValue(null, property.getName()));
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

		log("Imported "+ imported +" configuration items from '"+ manager.getSourceDocument().getFile().getAbsolutePath() + "' (prefix = '" + prefix +"')");
	}
}