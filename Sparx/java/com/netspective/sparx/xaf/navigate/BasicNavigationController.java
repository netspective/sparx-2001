/**
 * Created by IntelliJ IDEA.
 * User: Roque Hernandez
 * Date: Jan 17, 2003
 * Time: 10:58:37 PM
 * To change this template use Options | File Templates.
 */
package com.netspective.sparx.xaf.navigate;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import com.netspective.sparx.util.ClassPath;
import com.netspective.sparx.util.value.ConfigurationExprValue;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSource;

public class BasicNavigationController implements NavigationController
{

    String url;
    ConfigurationExprValue retainParams = new ConfigurationExprValue();
    String retainParamsSource;
    String name;

    public BasicNavigationController()
    {

    }

    public BasicNavigationController(String name, String url, String retainParams)
    {
        this.name = name;
        this.url = url;
        this.retainParamsSource = retainParams;
        this.retainParams.initializeSource(retainParams);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getRetainParamsValue(ValueContext vc)
    {
        return retainParamsSource != null ? retainParams.getValue(vc) : null;
    }

    public void setRetainParamsSource(String retainParams)
    {
        this.retainParamsSource = retainParams;
        this.retainParams.initializeSource(retainParams);
    }

    public ValueSource getRetainParams()
    {
        return retainParams;
    }

    public void setRetainParams(ValueSource retainParams)
    {
        this.retainParams = (ConfigurationExprValue) retainParams;
    }

    public String getRetainParamsSource()
    {
        return this.retainParamsSource;
    }

    public void importFromXml(Element node)
    {

        String name = node.getAttribute("name");
        setName(name);

        String url = node.getAttribute("url");
        setUrl(url);

        String retainParams = node.getAttribute("retain-params");
        if (retainParams != null && retainParams.length() > 0)
        {
            setRetainParamsSource(retainParams);

        }
    }
}
