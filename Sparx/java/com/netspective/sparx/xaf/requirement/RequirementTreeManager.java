package com.netspective.sparx.xaf.requirement;

import com.netspective.sparx.util.xml.XmlSource;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: nguyenth
 * Date: May 12, 2003
 * Time: 2:45:23 PM
 * To change this template use Options | File Templates.
 */
public class RequirementTreeManager extends XmlSource
{
	public RequirementTreeManager(File file)
	{
		loadDocument(file);
	}
}
