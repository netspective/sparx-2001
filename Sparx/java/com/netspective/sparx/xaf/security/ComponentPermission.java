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
 * $Id: ComponentPermission.java,v 1.1 2002-01-20 14:53:19 snshah Exp $
 */

package com.netspective.sparx.xaf.security;

import java.util.BitSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public BitSet getChildPermissions()
    {
        return childPermissions;
    }

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
                    acl.addError("Unable to find grant permission '" + permRefName + "' in permission '" + fullName + "'");
                    continue;
                }

                childPermissions.or(permRef.getChildPermissions());
                haveChildren = true;
            }
            else if(cName.equals("revoke"))
            {
                if(permRef == null)
                {
                    acl.addError("Unable to find revoke permission '" + permRefName + "' in permission '" + fullName + "'");
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