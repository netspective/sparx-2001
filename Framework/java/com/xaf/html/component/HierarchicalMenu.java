package com.xaf.html.component;

import java.io.*;
import java.util.*;

import com.xaf.html.*;
import com.xaf.page.*;
import com.xaf.value.*;

public class HierarchicalMenu extends AbstractComponent
{
	private VirtualPath entries;
	private String sharedScriptsRootURL;
	private String entriesJS;
	private int menuWidth = 100;
	private int menuTopPos = 10;
	private int menuLeftPos = 10;
	private boolean isTopPermanent = true;
	private boolean isTopHorizontal = true;
	private boolean isTreeHorizontal = false;
	private boolean positionUnder = true;
	private boolean topMoreImagesVisible = false;
	private boolean treeMoreImagesVisible = true;
	private String fontColor = "black";
	private String mouseOverFontColor = "white";
	private String bgColor = "#E5E2C9";
	private String mouseOverBgColor = "navy";
	private String borderColor = "#A1A289";
	private String separatorColor = "#A1A289";

	public HierarchicalMenu(int left, int width, int top, VirtualPath entries, String sharedScriptsRootURL)
	{
		this.menuLeftPos = left;
		this.menuWidth = width;
		this.menuTopPos = top;
		this.entries = entries;
		this.sharedScriptsRootURL = sharedScriptsRootURL;
	}

	public void createMenuJS(PageContext pc, VirtualPath path, StringBuffer script, String levelInfo)
	{
		script.append("HM_Array");
		script.append(levelInfo);
		script.append(" = [\n");
		if(levelInfo.equals("1"))
		{
			script.append("[");
			script.append(menuWidth); script.append(", ");
			script.append(menuLeftPos); script.append(", ");
			script.append(menuTopPos); script.append(", '");
			script.append(fontColor); script.append("', '");
			script.append(mouseOverFontColor); script.append("', '");
			script.append(bgColor); script.append("', '");
			script.append(mouseOverBgColor); script.append("', '");
			script.append(borderColor); script.append("', '");
			script.append(separatorColor); script.append("', ");
			script.append(isTopPermanent ? 1 : 0); script.append(", ");
			script.append(isTopHorizontal ? 1 : 0); script.append(", ");
			script.append(isTreeHorizontal ? 1 : 0); script.append(", ");
			script.append(positionUnder ? 1 : 0); script.append(", ");
			script.append(topMoreImagesVisible ? 1 : 0); script.append(", ");
			script.append(treeMoreImagesVisible ? 1 : 0);
			script.append("],\n");
		}
		else
			script.append("[],\n");
		createItemsJS(pc, path, script);
		script.append("];\n\n");

		List children = path.getChildrenList();
		for(int c = 0; c < children.size(); c++)
		{
			VirtualPath child = (VirtualPath) children.get(c);
			if(child.getChildrenList().size() > 0)
			{
				createMenuJS(pc, child, script, levelInfo + "_" + (c+1));
			}
		}
	}

	public void createItemsJS(PageContext pc, VirtualPath path, StringBuffer script)
	{
		List children = path.getChildrenList();
		int lastChild = children.size()-1;
		for(int c = 0; c <= lastChild; c++)
		{
			VirtualPath child = (VirtualPath) children.get(c);

			script.append("['");
			script.append(child.getCaption(pc));
			script.append("', '");
			script.append(child.getAbsolutePath(pc));
			script.append("', 1, 0, ");
			script.append(child.getChildrenList().size() > 0 ? 1 : 0);
			script.append("]");
			if(c != lastChild)
				script.append(",\n");
			else
				script.append("\n");
		}
	}

	public void createEntriesJS(PageContext pc)
	{
		StringBuffer script = new StringBuffer();
		script.append("<script>\n<!--\n");
		script.append("var SHARED_SCRIPTS_URL = '" + sharedScriptsRootURL + "';\n");
		createMenuJS(pc, entries, script, "1");
		script.append("-->\n</script>\n");
		script.append("<script src='"+sharedScriptsRootURL+"/hiermenus/HM_Loader.js' language='JavaScript1.2' type='text/javascript'></script>\n");

		entriesJS = script.toString();
	}

	public void printHtml(PageContext pc, Writer writer) throws IOException
	{
		if(entriesJS == null)
			createEntriesJS(pc);

		writer.write(entriesJS);
	}
}