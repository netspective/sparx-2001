package com.xaf.config;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

import org.w3c.dom.*;
import com.xaf.value.*;

public interface Property
{
	public boolean isDynamic();
	public boolean hasReplacements();

	public String getName();
	public String getExpression();
	public String getValue(ValueContext vc);

	public void importFromXml(Element elem);
}