package com.netspective.sparx.xaf.theme;

import java.util.Map;
import java.util.HashMap;

 /**
 * The Theme class is used to save theme related information such as name, style, and location.
 *
 * @author Aye Thu
 * Created on Feb 16, 2003 10:32:51 PM
 */
public class Theme
{
    private Map styles;
    private String name;
    private String path;
    private String currentStyle;

    public Theme(String name)
    {
        this.name = name;
        styles = new HashMap();
        currentStyle = "";
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public ThemeStyle getStyle(String style)
    {
        return (ThemeStyle) styles.get(style);
    }

    public void setCurrentStyle(String style)
    {
        this.currentStyle = style;
    }
    /**
     * Set one of the theme styles as the current one
     * @param style
     * @return
     */
    public ThemeStyle getCurrentStyle(String style)
    {
        if (styles.containsKey(style))
        {
            currentStyle = style;
            return getCurrentStyle();
        }
        return null;
    }

    public ThemeStyle getCurrentStyle()
    {
        return (ThemeStyle) styles.get(currentStyle);
    }

    public Map getStyles()
    {
        return styles;
    }

    public void addStyle(String style)
    {
        styles.put(style, new ThemeStyle(style));
    }

 }
