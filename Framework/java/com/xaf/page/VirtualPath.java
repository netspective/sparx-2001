package com.xaf.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import com.xaf.value.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
			while(matchedPath == null && ! finished)
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
					unmatchedItemsList.add(0, partialPath.substring(partialItemIndex+1));
					partialPath = partialPath.substring(0, partialItemIndex);
			    	matchedPath = (VirtualPath) absPathsMap.get(partialPath);
				}

				if(matchedPath != null && matchedPath.getPage() == null)
					matchedPath = null;
			}

		    unmatchedItems = (String[]) unmatchedItemsList.toArray(new String[unmatchedItemsList.size()]);
		}

		public boolean matchedHomePage() { return matchedHome; }
		public String getSearchedForPath() { return searchForPath; }
		public VirtualPath getSearchedInPath() { return searched; }
		public VirtualPath getMatchedPath() { return matchedPath; }
		public String[] unmatchedPathItems() { return unmatchedItems; }

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
    private String url;
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

	public String getName() { return name; }

	public VirtualPath getOwner() { return owner; }
	public void setOwner(VirtualPath value) { owner = value; }

	public VirtualPath getParent()	{ return parent; }
	public void setParent(VirtualPath value) { parent = value; }

	public ServletPage getPage() { return page; }
	public void setPage(ServletPage value) { page = value; }

    public String getUrl() { return url; }
	public void setUrl(String value) { url = value; }

	public Map getAbsolutePathsMap() { return absPathMap; }

	public String getCaption(PageContext pc) { return caption != null ? caption : (page != null ? page.getCaption(pc) : null); }
	public void setCaption(String value) { caption = value != null && value.length() > 0 ? value : null; }

	public String getTitle(PageContext pc) { return title != null ? title : (page != null ? page.getTitle(pc) : null); }
	public void setTitle(String value) { title = value != null && value.length() > 0 ? value : null; }

	public String getHeading(PageContext pc) { return heading != null ? heading : (page != null ? page.getHeading(pc) : null); }
	public void setHeading(String value) { heading = value != null && value.length() > 0 ? value : null; }

    public static VirtualPath importFromXml(String xmlFile) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        doc = parser.parse(xmlFile);
        doc.normalize();

        VirtualPath root = new VirtualPath();

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
                childPath.setUrl(childElem.getAttribute("url"));
                parent.getChildrenMap().put(childPath.getUrl(), childPath);
                parent.getChildrenList().add(childPath);
                parent.register(childPath);

                String caption = childElem.getAttribute("caption");
                childPath.setCaption(caption);

                String heading = childElem.getAttribute("heading");
                if(heading.length() == 0) heading = caption;
                childPath.setHeading(heading);

                String title = childElem.getAttribute("title");
                if(title.length() == 0) title = caption;
                childPath.setTitle(title);

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
        if(url != null)
            return url;

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

		if(startIndex < (pathItems.length-1))
			return child.addChild(pathItems, startIndex+1);
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

	static public String[] getPathItems(String path)
	{
		List items = new ArrayList();
		for(StringTokenizer st = new StringTokenizer(path, PATH_SEPARATOR); st.hasMoreTokens(); )
			items.add(st.nextToken());
		return (String[]) items.toArray(new String[items.size()]);
	}
}