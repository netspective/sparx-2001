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
 * $Id: NavigationTreeManager.java,v 1.5 2003-01-19 00:11:43 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.navigate;

import com.netspective.sparx.util.xml.XmlSource;
import com.netspective.sparx.util.ClassPath;
import com.netspective.sparx.xaf.skin.SkinFactory;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class NavigationTreeManager extends XmlSource
{
    public static final String NAME_DEFAULT = "default";
    private Map structures = new HashMap();

    public NavigationTreeManager(File file)
    {
        loadDocument(file);
    }

    public NavigationTree getTree(String name)
    {
        reload();
        return (NavigationTree) structures.get(name == null ? NAME_DEFAULT : name);
    }

    public void catalogNodes()
    {
        structures.clear();

        if(xmlDoc == null)
            return;

        Map controllers = new HashMap();
        NavigationController defaultController = new BasicNavigationController(NAME_DEFAULT, "/index.jsp", "");
        controllers.put(NAME_DEFAULT, defaultController);

        NodeList children = xmlDoc.getDocumentElement().getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node child = children.item(c);
            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;


            Element childElem = (Element) child;

            if (childElem.getNodeName().equals("controllers")){
                NodeList controllerNodes = childElem.getChildNodes();
                for (int i = 0; i < controllerNodes.getLength(); i++){
                    Node controllerNode = controllerNodes.item(i);
                    if (controllerNode.getNodeType() == Node.ELEMENT_NODE && controllerNode.getNodeName().equals("controller")) {
                        ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(childElem.getAttribute("class"), BasicNavigationController.class, true);
                        NavigationController controller = (NavigationController) instanceGen.getInstance();
                        controller.importFromXml((Element)controllerNode);
                        controllers.put(controller.getName(), controller);
                    }
                }
            }
            else if(childElem.getNodeName().equals("structure"))
            {
                setAttrValueDefault(childElem, "name", NAME_DEFAULT);
                ClassPath.InstanceGenerator instanceGen = new ClassPath.InstanceGenerator(childElem.getAttribute("class"), NavigationTree.class, true);
                NavigationTree tree = (NavigationTree) instanceGen.getInstance();
                tree.setControllers(controllers);
                tree.setControllerName(NAME_DEFAULT);
                tree.setController(defaultController);
                tree.setRoot(true);
                tree.importFromXml(childElem, tree);
                structures.put(childElem.getAttribute("name"), tree);
            }
            else if(childElem.getNodeName().equals("register-navigation-skin"))
            {
                String name = childElem.getAttribute("name");
                String className = childElem.getAttribute("class");
                try
                {
                    SkinFactory.addNavigationSkin(name, className);
                }
                catch (Exception e)
                {
                    addError("Error registering skin '"+ name +"': "+ e.toString());
                }
            }
        }

        addMetaInformation();
    }
}
