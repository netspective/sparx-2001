package com.xaf.security;

import java.util.*;
import com.xaf.value.*;

public class BasicAuthenticatedUser implements AuthenticatedUser
{
	private String userName;
	private String userId;
	private String userPassword;
	private String[] userRoles;
	private BitSet userPermissions;

    public BasicAuthenticatedUser(String name, String id)
    {
		userName = name;
		userId = id;
    }

	public String getUserName()
	{
		return userName;
	}

	public String getName() // implementation for java.security.Principal
	{
		return userId;
	}

	public String getUserId()
	{
		return userId;
	}

	public BitSet getUserPermissions()
	{
		return userPermissions;
	}

	public String[] getUserRoles()
	{
		return userRoles;
	}

	public void setRoles(AccessControlList acl, String[] roles)
	{
		userRoles = roles;
		if(userRoles == null)
			return;

		if(userPermissions == null)
			userPermissions = new BitSet(acl.getHighestPermissionId());

		for(int i = 0; i < userRoles.length; i++)
		{
			String roleName = roles[i];
			ComponentPermission role = acl.getPermission(roleName);
			if(role == null)
				throw new RuntimeException("Role '"+ roleName +"' does not exist in ACL.");
			userPermissions.or(role.getChildPermissions());
		}
	}

	public boolean hasPermission(AccessControlList acl, int permissionId)
	{
		ComponentPermission perm = acl.getPermission(permissionId);
		if(perm == null)
			throw new RuntimeException("Permission ID '"+ permissionId +"' does not exist in ACL.");
		return userPermissions.get(permissionId);
	}

	public boolean hasPermission(AccessControlList acl, String permissionName)
	{
		ComponentPermission perm = acl.getPermission(permissionName);
		if(perm == null)
			throw new RuntimeException("Permission '"+ permissionName +"' does not exist in ACL.");
		return userPermissions.get(perm.getId());
	}

	public boolean hasAnyPermission(AccessControlList acl, Integer[] permissionIds)
	{
		for(int i = 0; i < permissionIds.length; i++)
		{
			if(hasPermission(acl, permissionIds[i].intValue()))
				return true;
		}
		return false;
	}

	public boolean hasAnyPermission(AccessControlList acl, String[] permissionNames)
	{
		for(int i = 0; i < permissionNames.length; i++)
		{
			if(hasPermission(acl, permissionNames[i]))
				return true;
		}
		return false;
	}
}