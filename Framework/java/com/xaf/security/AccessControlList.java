package com.xaf.security;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.servlet.*;

import org.w3c.dom.*;

import com.xaf.*;
import com.xaf.xml.*;
import com.xaf.form.*;
import com.xaf.sql.*;
import com.xaf.value.*;

public class AccessControlList extends XmlSource implements FactoryListener
{
	public static final String NAME_SEPARATOR = "/";
	public static final String INHERIT_ATTR_NAME = "inherit";
	public static final String PERMISSION_ELEM_NAME = "permission";

	public static class AccessControlCatalogContext
	{
		protected int index;
		protected Map permissions;
		protected List inheritors;

		public AccessControlCatalogContext()
		{
			index = 0;
			permissions = new HashMap();
			inheritors = new ArrayList();
		}

		public final int getNextIndex()
		{
			// index "0" is reserved for the "root" so we start at 1
			index++;
			return index;
		}
	}

	private ComponentPermission rootPerm;
	private Map permissionsByName = new HashMap();
	private List permissionsById = new ArrayList();

	public AccessControlList(File file)
	{
		loadDocument(file);
	}

	public void factoryContentsChanged(FactoryEvent event)
	{
		forceReload();
	}

	public int getHighestPermissionId()
	{
		return permissionsById.size();
	}

	public void addPermission(ComponentPermission perm)
	{
		permissionsByName.put(perm.getFullName(), perm);
		permissionsById.add(perm.getId(), perm);
	}

	public ComponentPermission getPermission(String name)
	{
		return (ComponentPermission) permissionsByName.get(name);
	}

	public ComponentPermission getPermission(int id)
	{
		try
		{
			return (ComponentPermission) permissionsById.get(id);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public Element addPermissionElem(Element parent, String name)
	{
		Document doc = parent.getOwnerDocument();
		Element result = doc.createElement(PERMISSION_ELEM_NAME);
		result.setAttribute("name", name);
		parent.appendChild(result);
		return result;
	}

	public void catalogPermissions(AccessControlCatalogContext context, Node parent, String parentFullName)
	{
		NodeList children = parent.getChildNodes();
		if(children.getLength() == 0)
			return;

		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element childElem = (Element) node;
			String cName = childElem.getNodeName();
			if(cName.equals(PERMISSION_ELEM_NAME))
			{
				String name = childElem.getAttribute("name");
				String fullName = parentFullName != null ? parentFullName + NAME_SEPARATOR + name : NAME_SEPARATOR + name;

				childElem.setAttribute("id", Integer.toString(context.getNextIndex()));
				childElem.setAttribute("full-name", fullName);
				context.permissions.put(fullName, childElem);

				String inherit = childElem.getAttribute(INHERIT_ATTR_NAME);
				if(inherit.length() > 0)
					context.inheritors.add(childElem);

				catalogPermissions(context, node, fullName);
			}
		}
	}

	public void generatePermissions(Element parentElem)
	{
		NodeList children = parentElem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element childElem = (Element) node;
			if(childElem.getNodeName().equals("generate-permissions"))
			{
				String factoryClassName = childElem.getAttribute("class");
				if(factoryClassName.length() == 0)
					addError("In generate-permissions tag, no class provided.");
				else
				{
					try
					{
						Element generatedRoot = addPermissionElem(parentElem, childElem.getAttribute("name"));
						generatedRoot.setAttribute("generated", "yes");

						Class factoryClass = Class.forName(factoryClassName);

						Method addListenerMethod = factoryClass.getMethod("addListener", new Class[] { FactoryListener.class });
						Method generateMethod = factoryClass.getMethod("generatePermissions", new Class[] { AccessControlList.class, Element.class });

						addListenerMethod.invoke(null, new Object[] { this });
						generateMethod.invoke(null, new Object[] { this, generatedRoot });
					}
					catch(ClassNotFoundException e)
					{
						addError(e.toString());
					}
					catch(NoSuchMethodException e)
					{
						addError("Method generatePermissions not found in '"+factoryClassName+"'");
					}
					catch(InvocationTargetException e)
					{
						addError(e.toString());
					}
					catch(IllegalAccessException e)
					{
						addError(e.toString());
					}
				}
			}
			else
			{
				generatePermissions(childElem);
			}
		}
	}

	public void catalogNodes()
	{
		permissionsByName.clear();
		permissionsById.clear();
		rootPerm = null;

        if(xmlDoc == null)
            return;

		NodeList children = xmlDoc.getDocumentElement().getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

            String nodeName = node.getNodeName();
			if(nodeName.equals("access-control"))
			{
				Element accessControlElem = (Element) node;
				generatePermissions(accessControlElem);

				AccessControlCatalogContext context = new AccessControlCatalogContext();
				catalogPermissions(context, node, null);

				for(Iterator i = context.inheritors.iterator(); i.hasNext(); )
				{
					Element inheritor = (Element) i.next();
					inheritNodes(inheritor, context.permissions, INHERIT_ATTR_NAME);

					// make sure the full-names have the right parent names
					String inhFullName = inheritor.getAttribute("full-name");
		    		NodeList inhChildren = inheritor.getChildNodes();
					for(int ic = 0; ic < inhChildren.getLength(); ic++)
					{
						Node inhNnode = inhChildren.item(ic);
						if(! inhNnode.getNodeName().equals(PERMISSION_ELEM_NAME))
							continue;

						Element inhChildElem = (Element) inhNnode;
						String inhChildFullName = inhFullName + NAME_SEPARATOR + inhChildElem.getAttribute("name");
						inhChildElem.setAttribute("full-name", inhChildFullName);
						context.permissions.put(inhChildFullName, inhChildElem);
					}
				}

				accessControlElem.setAttribute("id", "0");
				rootPerm = new ComponentPermission();
				rootPerm.importFromXml(this, null, accessControlElem);
				rootPerm.finalizeXml(this, accessControlElem);
			}
		}

		addMetaInformation();
	}
}