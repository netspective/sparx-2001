package com.xaf.page;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import com.xaf.value.*;

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

	public Map getAbsolutePathsMap() { return absPathMap; }

	public String getCaption(PageContext pc) { return caption != null ? caption : (page != null ? page.getCaption(pc) : null); }
	public void setCaption(String value) { caption = value; }

	public String getTitle(PageContext pc) { return title != null ? title : (page != null ? page.getTitle(pc) : null); }
	public void setTitle(String value) { title = value; }

	public String getHeading(PageContext pc) { return heading != null ? heading : (page != null ? page.getHeading(pc) : null); }
	public void setHeading(String value) { heading = value; }

	public FindResults findPath(String path)
	{
		return new FindResults(this, path);
	};

	public String getAbsolutePath()
	{
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