package com.xaf.security;

import java.util.*;
import java.security.*;

import com.xaf.value.*;

public interface AuthenticatedUser extends Principal
{
	public String getUserName();
	public String getUserId();

	public String getUserOrgName();
	public String getUserOrgId();

	public BitSet getUserPermissions();
	public String[] getUserRoles();

	public void setRoles(AccessControlList acl, String[] roles);
    public void removeRoles(AccessControlList acl, String[] roles);
    public void removeAllRoles(AccessControlList acl);

	public boolean hasPermission(AccessControlList acl, int permissionId);
	public boolean hasPermission(AccessControlList acl, String permissionName);
	public boolean hasAnyPermission(AccessControlList acl, Integer[] permissionIds);
	public boolean hasAnyPermission(AccessControlList acl, String[] permissionNames);

	public Object getAttribute(String attrName);
	public void setAttribute(String attrName, Object attrValue);
	public void removeAttribute(String attrName);
}