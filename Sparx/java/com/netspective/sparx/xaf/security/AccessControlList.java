/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following 
 * conditions are provided as a summary of the NSL but the NSL remains the 
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL. 
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only 
 *    (as Java .class files or a .jar file containing the .class files) and only 
 *    as part of an application that uses The Software as part of its primary 
 *    functionality. No distribution of the package is allowed as part of a software 
 *    development kit, other library, or development tool without written consent of 
 *    Netspective Corporation. Any modified form of The Software is bound by 
 *    these same restrictions.
 * 
 * 3. Redistributions of The Software in any form must include an unmodified copy of 
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective 
 *    Corporation and may not be used to endorse products derived from The 
 *    Software without without written consent of Netspective Corporation. "Sparx" 
 *    and "Netspective" may not appear in the names of products derived from The 
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the 
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING 
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Shahid N. Shah
 */
 
/**
 * $Id: AccessControlList.java,v 1.3 2002-11-30 16:39:44 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.security;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.util.factory.FactoryEvent;
import com.netspective.sparx.util.factory.FactoryListener;
import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.util.ClassPath;

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

                        Method addListenerMethod = factoryClass.getMethod("addListener", new Class[]{FactoryListener.class});
                        Method generateMethod = factoryClass.getMethod("generatePermissions", new Class[]{AccessControlList.class, Element.class});

                        addListenerMethod.invoke(null, new Object[]{this});
                        generateMethod.invoke(null, new Object[]{this, generatedRoot});
                    }
                    catch(ClassNotFoundException e)
                    {
                        addError(e.toString());
                    }
                    catch(NoSuchMethodException e)
                    {
                        addError("Method generatePermissions not found in '" + factoryClassName + "'");
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

    public String[] getCatalogedNodeIdentifiers()
    {
        return (String[]) permissionsByName.keySet().toArray(new String[permissionsByName.size()]);
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

                for(Iterator i = context.inheritors.iterator(); i.hasNext();)
                {
                    Element inheritor = (Element) i.next();
                    inheritNodes(inheritor, context.permissions, INHERIT_ATTR_NAME);

                    // make sure the full-names have the right parent names
                    String inhFullName = inheritor.getAttribute("full-name");
                    NodeList inhChildren = inheritor.getChildNodes();
                    for(int ic = 0; ic < inhChildren.getLength(); ic++)
                    {
                        Node inhNnode = inhChildren.item(ic);
                        if(!inhNnode.getNodeName().equals(PERMISSION_ELEM_NAME))
                            continue;

                        Element inhChildElem = (Element) inhNnode;
                        String inhChildFullName = inhFullName + NAME_SEPARATOR + inhChildElem.getAttribute("name");
                        inhChildElem.setAttribute("full-name", inhChildFullName);
                        context.permissions.put(inhChildFullName, inhChildElem);
                    }
                }

                accessControlElem.setAttribute("id", "0");
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(accessControlElem.getAttribute("class"), ComponentPermission.class, true);
                rootPerm = (ComponentPermission) instanceGen.getInstance();
                rootPerm.importFromXml(this, null, accessControlElem);
                rootPerm.finalizeXml(this, accessControlElem);
            }
        }

        addMetaInformation();
    }
}