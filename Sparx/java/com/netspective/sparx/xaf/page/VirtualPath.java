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
 * $Id: VirtualPath.java,v 1.4 2002-11-15 02:56:09 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.netspective.sparx.util.log.LogManager;

public class VirtualPath
{
    static public final String PATH_SEPARATOR = "/";

    public class FindResults
    {
        private boolean matchedHome;
        private String searchForPath;
        private VirtualPath searched;
        private VirtualPath matchedPath;
        private String[] unmatchedItems;

        public FindResults(VirtualPath search, String path)
        {
            if(path == null || path.length() == 0)
                path = "/";

            searchForPath = path;
            searched = search;

            matchedPath = (VirtualPath) search.getAbsolutePathsMap().get(path);

            if(matchedPath != null)
                return;

            List unmatchedItemsList = new ArrayList();
            Map absPathsMap = search.getAbsolutePathsMap();
            String partialPath = path;
            boolean finished = false;
            while(matchedPath == null && !finished)
            {
                int partialItemIndex = partialPath.lastIndexOf(PATH_SEPARATOR);
                if(partialItemIndex == -1)
                {
                    matchedPath = (VirtualPath) absPathsMap.get(partialPath);
                    if(matchedPath == null)
                        unmatchedItemsList.add(0, partialPath);
                    finished = true;
                }
                else
                {
                    unmatchedItemsList.add(0, partialPath.substring(partialItemIndex + 1));
                    partialPath = partialPath.substring(0, partialItemIndex);
                    matchedPath = (VirtualPath) absPathsMap.get(partialPath);
                }

                if(matchedPath != null && matchedPath.getPage() == null)
                    matchedPath = null;
            }

            unmatchedItems = (String[]) unmatchedItemsList.toArray(new String[unmatchedItemsList.size()]);
        }

        public boolean matchedHomePage()
        {
            return matchedHome;
        }

        public String getSearchedForPath()
        {
            return searchForPath;
        }

        public VirtualPath getSearchedInPath()
        {
            return searched;
        }

        public VirtualPath getMatchedPath()
        {
            return matchedPath;
        }

        public String[] unmatchedPathItems()
        {
            return unmatchedItems;
        }

        public String getUnmatchedPath()
        {
            if(unmatchedItems == null || unmatchedItems.length == 0)
                return null;

            StringBuffer result = new StringBuffer();
            for(int i = 0; i < unmatchedItems.length; i++)
            {
                result.append(PATH_SEPARATOR);
                result.append(unmatchedItems[i]);
            }
            return result.toString();
        }
    }

    private VirtualPath owner;
    private VirtualPath parent;
    private String name;
    private String caption;
    private String title;
    private String heading;
    private ServletPage page;
    private String id;
    private List childrenList = new ArrayList();
    private Map childrenMap = new HashMap();
    private Map absPathMap = new HashMap();

    public VirtualPath()
    {
    }

    public VirtualPath(String name)
    {
        this();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public VirtualPath getOwner()
    {
        return owner;
    }

    public void setOwner(VirtualPath value)
    {
        owner = value;
    }

    public VirtualPath getParent()
    {
        return parent;
    }

    public void setParent(VirtualPath value)
    {
        parent = value;
    }

    public ServletPage getPage()
    {
        return page;
    }

    public void setPage(ServletPage value)
    {
        page = value;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String value)
    {
        id = value;
    }

    public Map getAbsolutePathsMap()
    {
        return absPathMap;
    }

    public String getCaption(PageContext pc)
    {
        return caption != null ? caption : (page != null ? page.getCaption(pc) : null);
    }

    public void setCaption(String value)
    {
        caption = value != null && value.length() > 0 ? value : null;
    }

    public String getTitle(PageContext pc)
    {
        return title != null ? title : (page != null ? page.getTitle(pc) : null);
    }

    public void setTitle(String value)
    {
        title = value != null && value.length() > 0 ? value : null;
    }

    public String getHeading(PageContext pc)
    {
        return heading != null ? heading : (page != null ? page.getHeading(pc) : null);
    }

    public void setHeading(String value)
    {
        heading = value != null && value.length() > 0 ? value : null;
    }

    public void importFromXml(String xmlFile) throws ParserConfigurationException, SAXException, IOException
    {
        importFromXml(xmlFile, this);
    }

    public static VirtualPath importFromXml(String xmlFile, VirtualPath root) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        doc = parser.parse(xmlFile);
        doc.normalize();

        Element rootElem = doc.getDocumentElement();
        NodeList children = rootElem.getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node child = children.item(c);
            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;
            if(childElem.getNodeName().equals("structure"))
            {
                importFromXml(childElem, root);
            }
        }

        return root;
    }

    public static void importFromXml(Element elem, VirtualPath parent)
    {
        NodeList children = elem.getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node child = children.item(c);
            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;
            if(childElem.getNodeName().equals("page"))
            {
                VirtualPath childPath = new VirtualPath();
                childPath.setOwner(parent.getOwner());
                childPath.setParent(parent);
                childPath.setId(childElem.getAttribute("id"));
                parent.getChildrenMap().put(childPath.getId(), childPath);
                parent.getChildrenList().add(childPath);
                parent.register(childPath);

                String caption = childElem.getAttribute("caption");
                childPath.setCaption(caption);

                String heading = childElem.getAttribute("heading");
                if(heading.length() == 0) heading = caption;
                childPath.setHeading(heading);

                String title = childElem.getAttribute("title");
                if(title.length() == 0) title = heading;
                childPath.setTitle(title);

                String pageClass = childElem.getAttribute("class");
                if(pageClass.length() != 0)
                {
                    try
                    {
                        Class cls = Class.forName(pageClass);
                        ServletPage page = (ServletPage) cls.newInstance();
                        childPath.setPage(page);
                    }
                    catch (Exception e)
                    {
                        LogManager.recordException(VirtualPath.class, "importFromXml", "Unable to instantiate page class '" + pageClass + "'", e);
                        childPath.setCaption(caption + " (" + e.toString() + ")");
                    }
                }

                importFromXml(childElem, childPath);
            }
        }
    }

    public FindResults findPath(String path)
    {
        return new FindResults(this, path);
    };

    public String getAbsolutePath()
    {
        if(id != null)
            return id;

        StringBuffer path = name != null ? new StringBuffer(name) : new StringBuffer();
        VirtualPath active = getParent();
        while(active != null)
        {
            path.insert(0, PATH_SEPARATOR);
            String activeName = active.getName();
            if(activeName != null)
                path.insert(0, activeName);
            active = active.getParent();
        }
        return path.toString();
    }

    public String getAbsolutePath(PageContext pc)
    {
        String absPath = getAbsolutePath();
        if(pc == null)
            return absPath;

        HttpServletRequest request = (HttpServletRequest) pc.getRequest();
        return request.getContextPath() + request.getServletPath() + absPath;
    }

    public void register(VirtualPath path)
    {
        String absolutePath = path.getAbsolutePath();
        absPathMap.put(absolutePath, path);
        if(parent != null)
            parent.register(path);
        if(owner != null)
            owner.register(path);
    }

    protected VirtualPath addChild(String path)
    {
        if(path == null || path.length() == 0 || path.equals("/"))
        {
            absPathMap.put("/", this);
            if(parent != null) parent.getAbsolutePathsMap().put("/", this);
            if(owner != null) owner.getAbsolutePathsMap().put("/", this);
            return this;
        }

        String[] items = getPathItems(path);
        return addChild(items, 0);
    }

    protected VirtualPath addChild(String[] pathItems, int startIndex)
    {
        String childName = pathItems[startIndex];

        VirtualPath child = (VirtualPath) childrenMap.get(childName);
        if(child == null)
        {
            child = new VirtualPath(childName);
            child.setOwner(owner);
            child.setParent(this);

            childrenMap.put(childName, child);
            childrenList.add(child);
            register(child);
        }

        if(startIndex < (pathItems.length - 1))
            return child.addChild(pathItems, startIndex + 1);
        else
            return child;
    }

    public VirtualPath registerPage(String path, ServletPage page)
    {
        VirtualPath child = addChild(path);
        child.setPage(page);
        return child;
    }

    public List getChildrenList()
    {
        return childrenList;
    }

    public Map getChildrenMap()
    {
        return childrenMap;
    }

    public String getDebugHtml(PageContext pc)
    {
        return getDebugHtml(pc, this);
    }

    static public String getDebugHtml(PageContext pc, VirtualPath parent)
    {
        if(parent.childrenList == null || parent.childrenList.size() == 0)
            return null;

        StringBuffer html = new StringBuffer("<ol>");
        Iterator i = parent.childrenList.iterator();
        while(i.hasNext())
        {
            VirtualPath path = (VirtualPath) i.next();
            html.append("<li>");
            html.append(path.getAbsolutePath() + ": " + path.getCaption(pc) + ", " + path.getHeading(pc) + ", " + path.getTitle(pc));
            String children = getDebugHtml(pc, path);
            if(children != null)
                html.append(children);
            html.append("</li>");
        }
        html.append("</ol>");

        return html.toString();
    }

    static public String[] getPathItems(String path)
    {
        List items = new ArrayList();
        for(StringTokenizer st = new StringTokenizer(path, PATH_SEPARATOR); st.hasMoreTokens();)
            items.add(st.nextToken());
        return (String[]) items.toArray(new String[items.size()]);
    }
}