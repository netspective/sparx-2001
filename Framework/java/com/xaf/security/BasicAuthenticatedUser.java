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
	private String userOrgName;
	private String userOrgId;
	private Map attributes = new HashMap();

    public BasicAuthenticatedUser(String name, String id)
    {
		userName = name;
		userId = id;
    }

    public BasicAuthenticatedUser(String name, String id, String orgName, String orgId)
    {
		this(name, id);
		userOrgName = orgName;
		userOrgId = orgId;
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

	public String getUserOrgName()
	{
		return userOrgName;
	}

	public String getUserOrgId()
	{
		return userOrgId;
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

    public void removeRoles(AccessControlList acl, String[] roles)
    {
        if(userRoles == null || userPermissions == null)
            return;

        // clear all the permissions that the roles may have granted earlier
        for(int i = 0; i < roles.length; i++)
        {
            String roleName = roles[i];
			ComponentPermission role = acl.getPermission(roleName);
			if(role == null)
				throw new RuntimeException("Role '"+ roleName +"' does not exist in ACL.");
			userPermissions.andNot(role.getChildPermissions());
        }

        if(roles == userRoles)
        {
            // if we're removing all the current user roles, it's a special case
            // because we're probably coming from the removeAllRoles method
            userRoles = null;
        }
        else
        {
            // loop through the current user roles and track the ones we're keeping
            // so that we can hang on to them in userRoles
            List keepRoles = new ArrayList();
            for(int i = 0; i < userRoles.length; i++)
            {
                String checkRole = userRoles[i];
                boolean removingRole = false;
                for(int j = 0; j < roles.length; j++)
                {
                    if(checkRole.equals(roles[j]))
                    {
                        removingRole = true;
                        break;
                    }
                }
                if(! removingRole)
                    keepRoles.add(checkRole);
            }
            if(keepRoles.size() > 0)
                userRoles = (String[]) keepRoles.toArray(new String[keepRoles.size()]);
            else
                userRoles = null;
        }
    }

    public void removeAllRoles(AccessControlList acl)
    {
        if(userRoles != null)
            removeRoles(acl, userRoles);
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

	public Object getAttribute(String attrName)
	{
		return attributes.get(attrName);
	}

	public void setAttribute(String attrName, Object attrValue)
	{
		attributes.put(attrName, attrValue);
	}

	public void removeAttribute(String attrName)
	{
		attributes.remove(attrName);
	}
}