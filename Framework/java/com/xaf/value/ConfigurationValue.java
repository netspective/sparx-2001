package com.xaf.value;

import com.xaf.config.*;

public class ConfigurationValue extends ValueSource
{
	private String source;

    public ConfigurationValue()
    {
		super();
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