package com.xaf.security;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.xaf.config.*;
import com.xaf.value.*;

public class AccessControlListFactory
{
	static final String ATTRNAME_ACL = "framework.acl";
	static Map managers = new Hashtable();

	public static AccessControlList getACL(String file)
	{
		AccessControlList acl = (AccessControlList) managers.get(file);
		if(acl == null)
		{
			acl = new AccessControlList(new File(file));
			managers.put(file, acl);
		}
		return acl;
	}

	public static AccessControlList getACL(ServletContext context)
	{
		AccessControlList acl = (AccessControlList) context.getAttribute(ATTRNAME_ACL);
		if(acl != null)
			return acl;

		Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context);
		ValueContext vc = new ServletValueContext(context, null, null, null);
		acl = getACL(appConfig.getValue(vc, "app.security.acl-file"));
		acl.initializeForServlet(context);
		context.setAttribute(ATTRNAME_ACL, acl);
		return acl;
	}
}