package com.netspective.sparx.xaf.theme;

import java.util.Map;

/**
 * Style for the theme
 */
public class ThemeStyle
{
    private String requiredFieldClass;
    private String fieldClass;

    private String styleName;
    private String imagePath;
    private String cssPath;
    private Map cssResources;
    private Map imageResources;

    public ThemeStyle(String name)
    {
        styleName = name;
        requiredFieldClass = "dialog-input-required";
        fieldClass = "dialog-input";
    }

    /**
     * Get the CSS class name for a required dialog field
     * @return
     */
    public String getRequiredFieldClass()
    {
        return requiredFieldClass;
    }

    /**
     * Get the CSS class name for a dialog field
     * @return
     */
    public String getFieldClass()
    {
        return fieldClass;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public void setCssPath(String cssPath)
    {
        this.cssPath = cssPath;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public String getCssPath()
    {
        return cssPath;
    }

    public String getName()
    {
        return styleName;
    }
    /**
     * Get the Cascading Style Sheets for this style
     * @return
     */
    public Map getCssResources()
    {
        return cssResources;
    }

    public void setCssResources(Map cssResources)
    {
        this.cssResources = cssResources;
    }

    /**
     * Get the image resources for this style
     * @return
     */
    public Map getImageResources()
    {
        return imageResources;
    }

    public void setImageResources(Map imageResource)
    {
        this.imageResources = imageResource;
    }
}
