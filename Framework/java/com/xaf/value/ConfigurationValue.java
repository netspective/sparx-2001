package com.xaf.value;

import com.xaf.config.*;

public class ConfigurationValue extends ValueSource
{
	private String source;

    public ConfigurationValue()
    {
		super();
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Provides access to configuration variables in the default configuration file (configuration.xml). If "+
            "no source-name is provided the property-name requested is read from the default configuration element "+
            "of the default configuration file. If a source-name is provided, then the property-name is read from the "+
            "configuration named source-name in the default configuration file.",
            new String[] { "property-name", "source-name/property-name" }
        );
    }

    public void initializeSource(String srcParams)
    {
        int delimPos = srcParams.indexOf('/');
        if(delimPos >= 0)
        {
            source = srcParams.substring(0, delimPos);
            valueKey = srcParams.substring(delimPos+1);
        }
        else
            valueKey = srcParams;
    }

    public String getValue(ValueContext vc)
    {
		Configuration config = null;
		if(source == null)
		{
			config = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
			if(config == null)
				return "No default Configuration found in "+ getId();
		}
		else
		{
			ConfigurationManager manager = ConfigurationManagerFactory.getManager(vc.getServletContext());
			if(manager == null)
				return "No ConfigurationManager found in " + getId();
			config = manager.getConfiguration(source);
			if(config == null)
				return "No '"+source+"' Configuration found in "+ getId();
		}
		return config.getValue(vc, valueKey);
    }
}