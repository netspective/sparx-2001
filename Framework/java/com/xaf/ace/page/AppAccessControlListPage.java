package com.xaf.ace.page;

import com.xaf.ace.AceServletPage;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.xaf.ace.*;
import com.xaf.page.*;
import com.xaf.security.*;

public class AppAccessControlListPage extends AceServletPage
{
	public final String getName() { return "acl"; }
	public final String getPageIcon() { return "access.gif"; }
	public final String getCaption(PageContext pc) { return "Access Control"; }
	public final String getHeading(PageContext pc) { return "Access Control List"; }

	public void handlePageBody(PageContext pc) throws ServletException, IOException
	{
		ServletContext context = pc.getServletContext();
		AccessControlList acl = AccessControlListFactory.getACL(context);
		acl.addMetaInfoOptions();
		transform(pc, acl.getDocument(), ACE_CONFIG_ITEMS_PREFIX + "acl-browser-xsl");
	}
}