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
 * $Id: NavigationTree.java,v 1.3 2002-12-26 19:35:40 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.page;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationTree extends VirtualPath
{
    private String defaultChildId = null;
    private SingleValueSource url = null;
    private Map ancestorMap = new HashMap();
    private List ancestorsList = new ArrayList();
    private boolean visible = true;

    /**
     * A String that represents the Id of the default child.  This property is relevant when trying to determine which
     * elements are part of the active path when the current element is not a leaf of the tree.
     * @return
     */
    public String getDefaultChildId()
    {
        return defaultChildId;
    }

    /**
     * A String that represents the Id of the default child.  This property is relevant when trying to determine which
     * elements are part of the active path when the current element is not a leaf of the tree.
     * @param  defaultChildId  The String representing the Id of the child that should be the default.
     */
    public void setDefaultChildId(String defaultChildId)
    {
        this.defaultChildId = defaultChildId;
    }

    /**
     * A ValueSource that represents what the URL of the element should be.  Unlike VIrtualPath, where its Id is the
     * URL that get rendered with the element, a NavigationTree's Id is simply to uniquely identify an element on the
     * tree.  If a url is not provided, the id is used for the url.
     * @return
     */
    public SingleValueSource getUrl()
    {
        return url;
    }

    /**
     * A ValueSource that represents what the URL of the element should be.  Unlike VIrtualPath, where its Id is the
     * URL that get rendered with the element, a NavigationTree's Id is simply to uniquely identify an element on the
     * tree.  If a url is not provided, the id is used for the url.
     * @param  pc  An object of type ValueContext to enable us to get the value from a ValueSource.
     * @return
     */
    public String getUrl(PageContext pc)
    {
        return url.getValue(pc);
    }

    /**
     * A ValueSource that represents what the URL of the element should be.  Unlike VIrtualPath, where its Id is the
     * URL that get rendered with the element, a NavigationTree's Id is simply to uniquely identify an element on the
     * tree.  If a url is not provided, the id is used for the url.
     * @param  url  The ValueSource to be used.
     */
    public void setUrl(SingleValueSource url)
    {
        this.url = url;
    }

    /**
     * A ValueSource that represents what the URL of the element should be.  Unlike VIrtualPath, where its Id is the
     * URL that get rendered with the element, a NavigationTree's Id is simply to uniquely identify an element on the
     * tree.  If a url is not provided, the id is used for the url.
     * @param  url  The String to obtain a reference to a ValueSource from the factory.
     */
    public void setUrl(String url)
    {
        this.url = ValueSourceFactory.getSingleOrStaticValueSource(url);
    }

    /**
     * A List that represents all of the ancestors of the current object.  This list is initialized by getting a
     * reference to the parents until the method getParent() returns null.  This List is relevant when determining what
     * elements to render on levels above the active path.
     * @return
     */
    public List getAncestorsList()
    {
        return ancestorsList;
    }

    /**
     * A List that represents all of the ancestors of the current object.  This list is initialized by getting a
     * reference to the parents until the method getParent() returns null.  This List is relevant when determining what
     * elements to render on levels above the active path.
     * @param  ancestorsList  The List that represents all of the parents of the current object.
     */
    public void setAncestorsList(List ancestorsList)
    {
        this.ancestorsList = ancestorsList;
    }

    /**
     * A Map that represents all of the ancestors of the current object.  This list is initialized by getting a
     * reference to the parents until the method getParent() returns null.  This Map is relevant when determining what
     * elements to render on levels above the active path.
     * @return
     */
    public Map getAncestorMap()
    {
        return ancestorMap;
    }

    /**
     * A Map that represents all of the ancestors of the current object.  This map is initialized by getting a
     * reference to the parents until the method getParent() returns null.  This Map is relevant when determining what
     * elements to render on levels above the active path.
     * @param  ancestorMap  The Map that represents all of the parents of the current object.
     */
    public void setAncestorMap(Map ancestorMap)
    {
        this.ancestorMap = ancestorMap;
    }

    /**
     * Returns a boolean that describes wether the current item of the NavigationTree should be displayed or not.
     * @param  nc  A NavigationContext object.  This object keeps a runtime state of the NavigationTree.
     * @return  <code>true</code> if the NavigationContext has true for this NavigationTree's Id.  If the NavigationContext
     *          does not have a value defined for it, it then looks at the variable defined in NavigationTree.  This variable
     *          is primarily driven by the importFromXml method.
     */
    public boolean isVisible(NavigationContext nc)
    {
        Boolean isVisible = nc.isNavVisible(this.getId());
        if (isVisible != null)
        {
            return isVisible.booleanValue();
        }
        else
        {
            return visible;
        }
    }

    /**
     * Sets the boolean variable that defines wether this NavigationTree should be displayed.  Calling this method does
     * not affect the runtime flag kept by the NavigationContext.
     * @param visible The boolean value.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Determines whether the NavigationTree is part of the active path.
     * @param  pc  A PageContext primarily to obtain the Active NavigationTree.
     * @return  <code>true</code> if the NavigationTree object is:
     *              1. The Active NavigationTree.
     *              2. In the ancestor list of the Active NavigationTree.
     *              3. One of the Default Children.
     */
    public boolean isInActivePath(PageContext pc)
    {
        //get the current NavigationTree
        NavigationTree currentNavTree = (NavigationTree) pc.getActivePath().getMatchedPath();

        if (this.getId().equals(currentNavTree.getId())) return true;

        //get the parents and for each set the property of current to true
        List ancestors = currentNavTree.getAncestorsList();
        for (int i = 0; i < ancestors.size(); i++)
        {
            NavigationTree navTree = (NavigationTree) ancestors.get(i);
            if (this.getId().equals(navTree.getId())) return true;
        }

        //get the default children if any and set the property of current to true
        Map childrenMap = currentNavTree.getChildrenMap();
        List childrenList = currentNavTree.getChildrenList();
        while (childrenMap != null && childrenList != null && !childrenMap.isEmpty() && !childrenList.isEmpty())
        {
            NavigationTree defaultChildNavTree = (NavigationTree) childrenMap.get(currentNavTree.getDefaultChildId());
            if (defaultChildNavTree == null)
            {
                defaultChildNavTree = (NavigationTree) childrenList.get(0);
            }
            if (this.getId().equals(defaultChildNavTree.getId())) return true;
            childrenMap = defaultChildNavTree.getChildrenMap();
            childrenList = defaultChildNavTree.getChildrenList();
        }
        //get that children's children and set and keep going down the path

        return false;
    }

    /**
     * Sets additional properties to the object from what VirtualPath is setting.  Specifically it sets the url, visible
     * and defaultChildId attributes.
     * @param  elem  The DOM element that contains the structure and values to be read.
     * @param  parent  The parent object.
     */
    public void importFromXml(Element elem, VirtualPath parent)
    {
        NavigationTree parentTree = (NavigationTree) parent;

        super.importFromXml(elem, parent);

        NodeList children = elem.getChildNodes();
        for (int c = 0; c < children.getLength(); c++)
        {
            Node child = children.item(c);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;
            if (childElem.getNodeName().equals("page"))
            {

                String id = childElem.getAttribute("id");
                NavigationTree childPath = (NavigationTree) parent.getChildrenMap().get(id);

                childPath.generateAncestorList();

                String url = childElem.getAttribute("url");
                if (url == null || url.length() == 0) url = id;
                childPath.setUrl(url);

                String visible = childElem.getAttribute("visible");
                if (visible != null && "no".equals(visible))
                    childPath.setVisible(false);

                String defaultChildId = childElem.getAttribute("default");
                childPath.setDefaultChildId(defaultChildId);

                //if this is the first child then if there is no default defined for the parent set this id as the
                //default.
                if (c == 0)
                {
                    String parentDefaultChildId = parentTree.getDefaultChildId();
                    if (parentDefaultChildId == null || parentDefaultChildId.length() == 0)
                    {
                        parentTree.setDefaultChildId(id);
                    }
                }
            }
        }
    }

    /**
     * Populates the ancestorMap and ancestorList.  It looks at its parent and adds a refence to it in the collections.
     * It then keeps doing that, but now looking at its parent's parent until it returns null.  It then reverses the list
     * to maintain a 0=top structure.
     */
    private void generateAncestorList()
    {
        NavigationTree currentPath = (NavigationTree) this.getParent();
        if (this.getParent() != null)
        {
            List ancestorListReversed = new ArrayList();
            Map ancestorMap = new HashMap();
            while (currentPath != null)
            {
                ancestorMap.put(currentPath.getId(), currentPath);
                ancestorListReversed.add(currentPath);
                currentPath = (NavigationTree) currentPath.getParent();
            }
            this.setAncestorMap(ancestorMap);

            List ancestorList = new ArrayList();
            for (int i = ancestorListReversed.size() - 1; i >= 0; i--)
            {
                NavigationTree navigationTree = (NavigationTree) ancestorListReversed.get(i);
                ancestorList.add(navigationTree);
            }
            this.setAncestorsList(ancestorList);
        }

    }

    /**
     * Overrides the method from VirtualPath in order to place NavigationTree objects in the structure.
     * @return
     */
    public VirtualPath getChildPathInstance()
    {
        return new NavigationTree();
    }

    /**
     * Renders a single level of the NavigationTree.  The actual rendering is done by an implementation of
     * TabbedNavigationSkin.
     * @param writer
     * @param nc
     * @param level
     */
    public void renderNavigation(Writer writer, NavigationContext nc, int level)
    {
        NavigationTree activeNavTree = (NavigationTree) nc.getActivePath().getMatchedPath();
        List ancestorList = activeNavTree.getAncestorsList();
        NavigationTree currentNavTree = null;

        if (level < ancestorList.size())
        {
            currentNavTree = (NavigationTree) ancestorList.get(level);
        }
        else if (level == ancestorList.size())
        {
            currentNavTree = activeNavTree;
        }
        else
        {
            return;
        }

        TabbedNavigationSkin skin = (TabbedNavigationSkin) nc.getSkin();
        //at this point it will set the style for the level
        try
        {
            skin.renderSingleNavigationLevel(writer, currentNavTree, nc);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    /**
     * Creates a navigation context for the given JSP page and skin. Gives the NavigationTree the ability
     * to control its own context.
     * @param jspPageContext
     * @param skin
     * @param popup set to true if page is being called for a popup-window
     * @return
     */
    public NavigationContext createContext(javax.servlet.jsp.PageContext jspPageContext, NavigationSkin skin, boolean popup)
    {
        NavigationContext result = new NavigationContext(this,
                jspPageContext.getServletContext(),
                (Servlet) jspPageContext.getPage(),
                (HttpServletRequest) jspPageContext.getRequest(),
                (HttpServletResponse) jspPageContext.getResponse(),
                this.getId(), skin);
        if(popup) result.setPopup(true);
        return result;
    }
}