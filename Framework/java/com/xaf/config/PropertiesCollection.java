package com.xaf.config;

import java.util.*;
import org.w3c.dom.*;

public interface PropertiesCollection
{
	public String getName();
	public Collection getCollection();
	public void importFromXml(Element elem, ConfigurationManager manager, Configuration config);
}