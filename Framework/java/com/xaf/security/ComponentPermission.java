package com.xaf.security;

import java.util.*;
import java.security.*;
import org.w3c.dom.*;
import com.xaf.*;

public class ComponentPermission
{
	private int id;
	private String name;
	private String fullName;
	private BitSet childPermissions;

    public ComponentPermission()
    {
		childPermissions = new BitSet();
    }

	public int getId() { return id; }
	public String getName() { return name; }
	public String getFullName() { return fullName; }
	public BitSet getChildPermissions() { return childPermissions; }

	public void unionChildPermissions(ComponentPermission perm)
	{
		if(childPermissions == null)
			childPermissions = new BitSet();

		childPermissions.or(perm.getChildPermissions());
	}

	public void importFromXml(AccessControlList acl, ComponentPermission parentPerm, Element parentElem)
	{
		id = Integer.parseInt(parentElem.getAttribute("id"));
		name = parentElem.getAttribute("name");
		fullName = parentElem.getAttribute("full-name");
		childPermissions.set(id);

		acl.addPermission(this);

		NodeList children = parentElem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element childElem = (Element) node;
			String cName = childElem.getNodeName();
			if(cName.equals(AccessControlList.PERMISSION_ELEM_NAME))
			{
				ComponentPermission childPerm = new ComponentPermission();
				childPerm.importFromXml(acl, this, childElem);
				childPermissions.set(childPerm.getId());
				childPermissions.or(childPerm.getChildPermissions());
			}
		}
	}

	public void finalizeXml(AccessControlList acl, Element parentElem)
	{
		boolean haveChildren = false;

		NodeList children = parentElem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element childElem = (Element) node;
			ComponentPermission permRef = null;
			String permRefName = childElem.getAttribute("permission");
			if(permRefName.length() > 0)
				permRef = acl.getPermission(permRefName);

			String cName = childElem.getNodeName();
			if(cName.equals(AccessControlList.PERMISSION_ELEM_NAME))
			{
				ComponentPermission childPerm = acl.getPermission(childElem.getAttribute("full-name"));
				childPerm.finalizeXml(acl, childElem);
				haveChildren = true;
			}
			else if(cName.equals("grant"))
			{
				if(permRef == null)
				{
					acl.addError("Unable to find grant permission '"+ permRefName +"' in permission '"+ fullName +"'");
					continue;
				}

				childPermissions.or(permRef.getChildPermissions());
				haveChildren = true;
			}
			else if(cName.equals("revoke"))
			{
				if(permRef == null)
				{
					acl.addError("Unable to find revoke permission '"+ permRefName +"' in permission '"+ fullName +"'");
					continue;
				}

				childPermissions.andNot(permRef.getChildPermissions());
				haveChildren = true;
			}
		}

		if(haveChildren)
			parentElem.setAttribute("bit-set", childPermissions.toString());
	}
}