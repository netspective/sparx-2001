package com.xaf.html.component;

import java.io.*;
import java.util.*;

import com.xaf.html.*;
import com.xaf.page.*;
import com.xaf.value.*;

public class HierarchicalMenu extends AbstractComponent
{
	static public class DrawContext
	{
		public boolean firstMenu;
		public boolean lastMenu;

		public DrawContext()
		{
		}
	}

	private VirtualPath entries;
	private String sharedScriptsRootURL;
	private String entriesJS;
	private int menuNum = 0;
	private int menuWidth = 100;
	private int menuTopPos = 10;
	private int menuLeftPos = 10;
	private boolean isTopPermanent = false;
	private boolean isTopHorizontal = false;
	private boolean isTreeHorizontal = false;
	private boolean positionUnder = true;
	private boolean topMoreImagesVisible = true;
	private boolean treeMoreImagesVisible = true;
	private String fontColor = "black";
	private String mouseOverFontColor = "white";
	private String bgColor = "#FEC740";
	private String mouseOverBgColor = "navy";
	private String borderColor = "#FDF43F";
	private String separatorColor = "#FDF43F";

	public HierarchicalMenu(int menuNum, int left, int width, int top, VirtualPath entries, String sharedScriptsRootURL)
	{
		this.menuNum = menuNum;
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
		if(levelInfo.length() == 1) // top-level menu
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
		DrawContext dc = pc != null ? (DrawContext) pc.getRequest().getAttribute(DrawContext.class.getName()) : null;

		if(dc == null || dc.firstMenu)
		{
			script.append("<script>\n<!--\n");
			script.append("var SHARED_SCRIPTS_URL = '" + sharedScriptsRootURL + "';\n");
		}

		createMenuJS(pc, entries, script, Integer.toString(menuNum));

		if(dc == null || dc.lastMenu)
		{
			script.append("-->\n</script>\n");
			script.append("<script src='"+sharedScriptsRootURL+"/hiermenus/HM_Loader.js' language='JavaScript1.2' type='text/javascript'></script>\n");
		}

		entriesJS = script.toString();
	}

	public void printHtml(PageContext pc, Writer writer) throws IOException
	{
		if(entriesJS == null)
			createEntriesJS(pc);

		writer.write(entriesJS);
	}

    public VirtualPath getEntries() {
        return entries;
    }

    public void setEntries(VirtualPath entries) {
        this.entries = entries;
    }

    public String getSharedScriptsRootURL() {
        return sharedScriptsRootURL;
    }

    public void setSharedScriptsRootURL(String sharedScriptsRootURL) {
        this.sharedScriptsRootURL = sharedScriptsRootURL;
    }

    public String getEntriesJS() {
        return entriesJS;
    }

    public void setEntriesJS(String entriesJS) {
        this.entriesJS = entriesJS;
    }

    public int getMenuNum() {
        return menuNum;
    }

    public void setMenuNum(int menuNum) {
        this.menuNum = menuNum;
    }

    public int getMenuWidth() {
        return menuWidth;
    }

    public void setMenuWidth(int menuWidth) {
        this.menuWidth = menuWidth;
    }

    public int getMenuTopPos() {
        return menuTopPos;
    }

    public void setMenuTopPos(int menuTopPos) {
        this.menuTopPos = menuTopPos;
    }

    public int getMenuLeftPos() {
        return menuLeftPos;
    }

    public void setMenuLeftPos(int menuLeftPos) {
        this.menuLeftPos = menuLeftPos;
    }

    public boolean isTopPermanent() {
        return isTopPermanent;
    }

    public void setTopPermanent(boolean topPermanent) {
        isTopPermanent = topPermanent;
    }

    public boolean isTopHorizontal() {
        return isTopHorizontal;
    }

    public void setTopHorizontal(boolean topHorizontal) {
        isTopHorizontal = topHorizontal;
    }

    public boolean isTreeHorizontal() {
        return isTreeHorizontal;
    }

    public void setTreeHorizontal(boolean treeHorizontal) {
        isTreeHorizontal = treeHorizontal;
    }

    public boolean isPositionUnder() {
        return positionUnder;
    }

    public void setPositionUnder(boolean positionUnder) {
        this.positionUnder = positionUnder;
    }

    public boolean isTopMoreImagesVisible() {
        return topMoreImagesVisible;
    }

    public void setTopMoreImagesVisible(boolean topMoreImagesVisible) {
        this.topMoreImagesVisible = topMoreImagesVisible;
    }

    public boolean isTreeMoreImagesVisible() {
        return treeMoreImagesVisible;
    }

    public void setTreeMoreImagesVisible(boolean treeMoreImagesVisible) {
        this.treeMoreImagesVisible = treeMoreImagesVisible;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getMouseOverFontColor() {
        return mouseOverFontColor;
    }

    public void setMouseOverFontColor(String mouseOverFontColor) {
        this.mouseOverFontColor = mouseOverFontColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getMouseOverBgColor() {
        return mouseOverBgColor;
    }

    public void setMouseOverBgColor(String mouseOverBgColor) {
        this.mouseOverBgColor = mouseOverBgColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getSeparatorColor() {
        return separatorColor;
    }

    public void setSeparatorColor(String separatorColor) {
        this.separatorColor = separatorColor;
    }
}