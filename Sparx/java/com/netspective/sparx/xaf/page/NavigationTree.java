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
 * $Id: NavigationTree.java,v 1.1 2002-12-04 14:24:39 roque.hernandez Exp $
 */


package com.netspective.sparx.xaf.page;

import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationTree extends VirtualPath {

    private String defaultChildId = null;
    private SingleValueSource url = null;
    private int level;
    private Map sibilingMap = new HashMap();
    private List sibilingList = new ArrayList();
    private Map ancestorMap = new HashMap();
    private List ancestorsList = new ArrayList();
    private boolean visible = true;

    public String getDefaultChildId() {
        return defaultChildId;
    }

    public void setDefaultChildId(String defaultChildId) {
        this.defaultChildId = defaultChildId;
    }

    public SingleValueSource getUrl() {
        return url;
    }

    public String getUrl(PageContext pc) {
        return url.getValue(pc);
    }

    public void setUrl(SingleValueSource url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = ValueSourceFactory.getSingleOrStaticValueSource(url);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map getSibilingMap() {
        return sibilingMap;
    }

    public void setSibilingMap(Map sibilingMap) {
        this.sibilingMap = sibilingMap;
    }

    public List getSibilingList() {
        return sibilingList;
    }

    public void setSibilingList(List sibilingList) {
        this.sibilingList = sibilingList;
    }

    public List getAncestorsList() {
        return ancestorsList;
    }

    public void setAncestorsList(List ancestorsList) {
        this.ancestorsList = ancestorsList;
    }

    public Map getAncestorMap() {
        return ancestorMap;
    }

    public void setAncestorMap(Map ancestorMap) {
        this.ancestorMap = ancestorMap;
    }

    public boolean isVisible(NavigationContext nc) {
        Boolean isVisible = nc.isNavVisible(this.getId());
        if (isVisible != null) {            
            return isVisible.booleanValue();
        } else {
            return visible;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isCurrent(PageContext pc) {
        //get the current NavigationTree
        NavigationTree currentNavTree = (NavigationTree) pc.getActivePath().getMatchedPath();

        if (this.getId().equals(currentNavTree.getId())) return true;

        //get the parents and for each set the property of current to true
        List ancestors = currentNavTree.getAncestorsList();
        for (int i = 0; i < ancestors.size(); i++) {
            NavigationTree navTree = (NavigationTree) ancestors.get(i);
            if (this.getId().equals(navTree.getId())) return true;
        }

        //get the default children if any and set the property of current to true
        Map childrenMap = currentNavTree.getChildrenMap();
        List childrenList = currentNavTree.getChildrenList();
        while (childrenMap != null && childrenList != null && !childrenMap.isEmpty() && !childrenList.isEmpty()) {
            NavigationTree defaultChildNavTree = (NavigationTree) childrenMap.get(currentNavTree.getDefaultChildId());
            if (defaultChildNavTree == null) {
                defaultChildNavTree = (NavigationTree) childrenList.get(0);
            }
            if (this.getId().equals(defaultChildNavTree.getId())) return true;
            childrenMap = defaultChildNavTree.getChildrenMap();
            childrenList = defaultChildNavTree.getChildrenList();
        }
        //get that children's children and set and keep going down the path

        return false;
    }

    public void importFromXml(Element elem, VirtualPath parent) {

        NavigationTree parentTree = (NavigationTree) parent;
        int currentLevel = 0;
        if (parentTree.getParent() != null) {
            currentLevel = ((NavigationTree) parentTree.getParent()).getLevel() + 1;
            parentTree.setLevel(currentLevel);
        } else {
            parentTree.setLevel(0);
        }

        super.importFromXml(elem, parent);

        NodeList children = elem.getChildNodes();
        Map currentSibilingMap = new HashMap();
        List currentSibilingList = new ArrayList();
        for (int c = 0; c < children.getLength(); c++) {
            Node child = children.item(c);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;
            if (childElem.getNodeName().equals("page")) {

                String id = childElem.getAttribute("id");
                NavigationTree childPath = (NavigationTree) parent.getChildrenMap().get(id);

                currentSibilingMap.put(childPath.getId(),childPath);
                currentSibilingList.add(childPath);
                generateAncestorList();

                childPath.setLevel(currentLevel);
                childPath.setSibilingList(currentSibilingList);

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
                if (c == 0) {
                    String parentDefaultChildId = parentTree.getDefaultChildId();
                    if (parentDefaultChildId == null || parentDefaultChildId.length() == 0) {
                        parentTree.setDefaultChildId(id);
                    }
                }

                importFromXml(childElem, childPath);
            }
        }
    }

    private void generateAncestorList() {
        NavigationTree currentPath = (NavigationTree) this.getParent();
        if (this.getParent() != null) {
            List ancestorListReversed = new ArrayList();
            Map ancestorMap = new HashMap();
            while(currentPath != null){
                ancestorMap.put(currentPath.getId(), currentPath);
                ancestorListReversed.add(currentPath);
                currentPath = (NavigationTree)currentPath.getParent();
            }
            this.setAncestorMap(ancestorMap);

            List ancestorList = new ArrayList();
            for (int i = ancestorListReversed.size() - 1; i >= 0; i--) {
                NavigationTree navigationTree = (NavigationTree) ancestorListReversed.get(i);
                ancestorList.add(navigationTree);
            }
            this.setAncestorsList(ancestorList);
        }

    }

    public NavigationTree getTopAncestor(){
        return (NavigationTree) this.getAncestorsList().get(1);
    }

    public VirtualPath getChildPathInstance() {
        return new NavigationTree();
    }

    public void renderNavigation(Writer writer, NavigationContext nc, int level) {
        NavigationTree activeNavTree = (NavigationTree) nc.getActivePath().getMatchedPath();
        List ancestorList = activeNavTree.getAncestorsList();
        NavigationTree currentNavTree = null;

        if (level < ancestorList.size() ){
            currentNavTree = (NavigationTree) ancestorList.get(level);
        } else if (level == ancestorList.size()) {
            currentNavTree = activeNavTree;
        } else {
            return;
        }

        TabbedNavigationSkin skin = (TabbedNavigationSkin) nc.getSkin();
        //at this point it will set the style for the level
        try {
            skin.renderSingleNavigationLevel(writer, currentNavTree, nc);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}







