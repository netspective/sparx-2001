package app.tag;

import com.xaf.xml.XmlSource;
import com.xaf.config.*;
import com.xaf.value.*;

import java.util.*;
import java.io.File;

import org.w3c.dom.*;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;

public class MenuXml
{
    private Vector menuList;
    private Vector submenuList;

    class MenuInfo
    {
        String name;
        String url;
        String onImage;
        String offImage;
        Vector childMenuList;

        public MenuInfo(String name, String url, String onImage, String offImage)
        {
            this.name = name;
            this.url = url;
            this.onImage = onImage;
            this.offImage = offImage;
        }
    }

    public MenuXml()
    {
    }

    public void importFromXml(String fileLoc)
    {
        File menuXmlFile = new File(fileLoc);
        XmlSource menuXml = new XmlSource();
        Document xmlDoc = menuXml.loadXML(menuXmlFile);
        NodeList children = xmlDoc.getDocumentElement().getChildNodes();

        this.menuList = new Vector();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = node.getNodeName();
            String menuName, url, onImage, offImage;
            if(nodeName.equals("menu"))
            {
                Element menuElem = (Element) node;
                menuName = menuElem.getAttribute("name");
                url = menuElem.getAttribute("url");
                onImage = menuElem.getAttribute("imageOn");
                offImage = menuElem.getAttribute("imageOff");

                MenuInfo mi = new MenuInfo(menuName, url, onImage, offImage);
                this.addChildMenus(menuElem, mi);
                menuList.add(mi);
            }
        }

    }

    /**
     * Returns the JS menu string based upon the user type(hospital or region)
     */
    public String getMenuString(PageContext context)
    {
        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(context.getServletContext());
		ValueContext vc = new ServletValueContext(context.getServletContext(), null, null, null);
        String fileLoc = "";
        fileLoc = appConfig.getValue(vc, "app.xml.main-menu");
        //String imagePath = appConfig.getValue(vc, "app.resources.image-url");

        this.importFromXml(fileLoc);
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getResponse();

        String appPath = request.getContextPath();
        String imagePath = appPath + "/resources/images";

        StringBuffer sb = new StringBuffer();
        Enumeration enumList = this.menuList.elements();
        int mainIndex = 1;
        while (enumList.hasMoreElements())
        {
            MenuInfo mi = (MenuInfo) enumList.nextElement();
            sb.append("oCMenu.makeMenu('top" + mainIndex + "', '','" + mi.name +
                    "', '" + appPath + mi.url + "', '', 0, 0, '"+
                    imagePath + mi.offImage + "', '"+ imagePath + mi.onImage + "');\n");
            this.appendSubMenuString(sb, mi, mainIndex, appPath, imagePath);
            mainIndex++;
        }

        return sb.toString();
    }

    public void appendSubMenuString(StringBuffer sbuf, MenuInfo mi, int mainIndex, String appPath, String imagePath)
    {
        String parent = "";
        if (mainIndex > 9)
            parent = "sub";
        else
            parent = "top";

        if (mi.childMenuList != null && !mi.childMenuList.isEmpty())
        {
            Enumeration enumList = mi.childMenuList.elements();
            int subIndex = 1;
            while (enumList.hasMoreElements())
            {
                MenuInfo submi = (MenuInfo) enumList.nextElement();
                String index = Integer.toString(mainIndex) + Integer.toString(subIndex);
                sbuf.append("oCMenu.makeMenu('sub" + index + "', '" + parent + mainIndex + "','" + submi.name +
                    "', '" + appPath + submi.url + "', '', 0, 0, '"+
                    imagePath + submi.offImage + "', '"+ imagePath + submi.onImage +"');\n");
                this.appendSubMenuString(sbuf, submi, Integer.parseInt(index), appPath, imagePath);
                subIndex++;
            }
        }
    }

    private void addChildMenus(Element menuElem, MenuInfo mi)
    {
        String menuName, url, onImage, offImage;
        NodeList menuChildren = menuElem.getChildNodes();
        for(int c = 0; c < menuChildren.getLength(); c++)
        {
            if (mi.childMenuList == null)
                mi.childMenuList = new Vector();
            Node submenuNode = menuChildren.item(c);
            if(submenuNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element submenuElem = (Element) submenuNode;
            menuName = submenuElem.getAttribute("name");
            url = submenuElem.getAttribute("url");
            onImage = submenuElem.getAttribute("imageOn");
            offImage = submenuElem.getAttribute("imageOff");

            MenuInfo smi = new MenuInfo(menuName, url, onImage, offImage);
            this.addChildMenus(submenuElem, smi);
            mi.childMenuList.add(smi);

        }
        return ;
    }
}
