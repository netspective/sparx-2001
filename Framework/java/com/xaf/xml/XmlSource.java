package com.xaf.xml;

/**
 * Title:        XML document container (automatically manages "include" files)
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

import org.w3c.dom.*;
import org.apache.xpath.objects.*;
import org.apache.xpath.*;

import com.xaf.Metric;

public class XmlSource
{
	public class SourceInfo
	{
		protected File source;
		protected long lastModified;
		protected SourceInfo parent;

		SourceInfo(SourceInfo includedFrom, File file)
		{
			source = file;
			lastModified = source.lastModified();
			parent = includedFrom;
		}

		public File getFile() { return source; }
		public SourceInfo getParent() { return parent; }

		public boolean sourceChanged()
		{
			return source.lastModified() > this.lastModified;
		}
	}

	protected boolean allowReload = true;
	protected ArrayList errors = new ArrayList();
	protected SourceInfo docSource;
	protected Hashtable sourceFiles = new Hashtable();
	protected Document xmlDoc;
	protected Element metaInfoElem;
	protected Element metaInfoOptionsElem;
	protected Set inheritanceHistorySet = new HashSet();
    protected Map templates = new HashMap();

	public boolean getAllowReload() { return allowReload; }
	public void setAllowReload(boolean value) { allowReload = value; }

	public void initializeForServlet(ServletContext servletContext)
	{
		if(com.xaf.config.ConfigurationManagerFactory.isProductionOrTestEnvironment(servletContext))
			setAllowReload(false);
	}

    /**
     * returns the boolean equivalent of a string, which is considered true
     * if either "on", "true", or "yes" is found, ignoring case.
     */
    public static boolean toBoolean(String s)
	{
        return (s.equalsIgnoreCase("on") ||
                s.equalsIgnoreCase("true") ||
                s.equalsIgnoreCase("yes"));
    }

    /**
     * Given a text string, return a string that would be suitable for that string to be used
     * as a Java identifier (as a variable or method name). Depending upon whether ucaseInitial
     * is set, the string starts out with a lowercase or uppercase letter. Then, the rule is
     * to convert all periods into underscores and title case any words separated by
     * underscores. This has the effect of removing all underscores and creating mixed case
     * words. For example, Person_Address becomes personAddress or PersonAddress depending upon
     * whether ucaseInitial is set to true or false. Person.Address would become Person_Address.
     */
	public static String xmlTextToJavaIdentifier(String xml, boolean ucaseInitial)
	{
		if(xml == null || xml.length() == 0)
			return xml;

		StringBuffer identifier = new StringBuffer();
        char ch = xml.charAt(0);
        identifier.append(ucaseInitial ? Character.toUpperCase(ch) : Character.toLowerCase(ch));

        boolean uCase = false;
		for(int i = 1; i < xml.length(); i++)
		{
			ch = xml.charAt(i);
            if(ch == '.')
            {
                identifier.append('_');
            }
			else if(ch != '_' && Character.isJavaIdentifierPart(ch))
			{
				identifier.append(Character.isUpperCase(ch) ? ch : (uCase ? Character.toUpperCase(ch) : Character.toLowerCase(ch)));
				uCase = false;
			}
			else
				uCase = true;
		}
		return identifier.toString();
	}

    /**
     * Given a text string, return a string that would be suitable for that string to be used
     * as a Java constant (public static final XXX). The rule is to basically take every letter
     * or digit and return it in uppercase and every non-letter or non-digit as an underscore.
     */
    public static String xmlTextToJavaConstant(String xml)
	{
		if(xml == null || xml.length() == 0)
			return xml;

		StringBuffer constant = new StringBuffer();
		for(int i = 0; i < xml.length(); i++)
		{
			char ch = xml.charAt(i);
            constant.append(Character.isJavaIdentifierPart(ch) ? Character.toUpperCase(ch) : '_');
		}
		return constant.toString();
	}

    /**
     * Given a text string, return a string that would be suitable for an XML element name. For example,
     * when given Person_Address it would return person-address. The rule is to basically take every letter
     * or digit and return it in lowercase and every non-letter or non-digit as a dash.
     */
    public static String xmlTextToNodeName(String xml)
	{
		if(xml == null || xml.length() == 0)
			return xml;

		StringBuffer constant = new StringBuffer();
		for(int i = 0; i < xml.length(); i++)
		{
			char ch = xml.charAt(i);
            constant.append(Character.isLetterOrDigit(ch) ? Character.toLowerCase(ch) : '-');
		}
		return constant.toString();
	}

	public Document getDocument()
	{
		reload();
		return xmlDoc;
	}

    /**
     * Given an element, see if the element is a <templates> element. If it is, then catalog all of
     * the elements as templates that can be re-used at a later point.
     */
    public void catalogElement(Element elem)
    {
        if(! "templates".equals(elem.getNodeName()))
            return;

        String pkgName = elem.getAttribute("package");

        NodeList children = elem.getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node child = children.item(c);
            if(child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;
            String templateName = childElem.getAttribute("id");
            if(templateName.length() == 0)
                templateName = childElem.getAttribute("name");

            templates.put(pkgName.length() > 0 ? (pkgName + "." + templateName) : templateName, childElem);
        }
    }

    /**
     * Given an element, apply templates to the node. If there is an attribute called "template" then inherit that
     * template first. Then, search through all of the nodes in the element and try to find all <include-template id="x">
     * elements to copy the template elements at those locations. Also, go through each child to see if a tag name
     * exists that matches a template name -- if it does, then "inherit" that template to replace the element at that
     * location.
     */
    public void processTemplates(Element elem)
    {
        inheritNodes(elem, templates, "template");

        NodeList includes = elem.getElementsByTagName("include-template");
		if(includes != null && includes.getLength() > 0)
        {
            for(int n = 0; n < includes.getLength(); n++)
            {
                Element include = (Element) includes.item(n);
                String templateName = include.getAttribute("id");
                Element template = (Element) templates.get(templateName);

                if(template != null)
                {
                    NodeList incChildren = template.getChildNodes();
                    for(int c = 0; c < incChildren.getLength(); c++)
                    {
                        Node incCopy = xmlDoc.importNode(incChildren.item(c), true);
                        if(incCopy.getNodeType() == Node.ELEMENT_NODE)
                            ((Element) incCopy).setAttribute("_included-from-template", templateName);
                        elem.insertBefore(incCopy, include);
                    }
                }
            }
        }

        NodeList children = elem.getChildNodes();
        for(int c = 0; c < children.getLength(); c++)
        {
            Node childNode = children.item(c);
            if(childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = childNode.getNodeName();
            if(templates.containsKey(nodeName))
            {
                Element template = (Element) templates.get(nodeName);
                Node incCopy = xmlDoc.importNode(template, true);
                if(incCopy.getNodeType() == Node.ELEMENT_NODE)
                    ((Element) incCopy).setAttribute("_included-from-template", nodeName);

                // make sure that the child's attributes overwrite the attributes in the templates with the same name
                NamedNodeMap attrsInChild = childNode.getAttributes();
                for(int a = 0; a < attrsInChild.getLength(); a++)
                {
                    Node childAttr = attrsInChild.item(a);
                    ((Element) incCopy).setAttribute(childAttr.getNodeName(), childAttr.getNodeValue());
                }

                // now do the actual replacement
                inheritNodes((Element) incCopy, templates, "template");
                elem.replaceChild(incCopy, childNode);
            }
            else
                inheritNodes((Element) childNode, templates, "template");
        }
    }

	public List getErrors() { return errors; }
	public void addError(String msg) { errors.add(msg); }

	public SourceInfo getSourceDocument() { return docSource; }
	public Map getSourceFiles() { return sourceFiles; }

	public boolean sourceChanged()
	{
		if(docSource == null)
			return false;

		if(sourceFiles.size() > 1)
		{
			for(Enumeration e = sourceFiles.elements(); e.hasMoreElements(); )
			{
				if(((SourceInfo) e.nextElement()).sourceChanged())
					return true;
			}
		}
		else
			return docSource.sourceChanged();

		// if we get to here, none of the files is newer than what's in memory
		return false;
	}

	public void forceReload()
	{
		loadDocument(docSource.getFile());
	}

	public void reload()
	{
		if(allowReload && docSource != null && sourceChanged())
			loadDocument(docSource.getFile());
	}

	public boolean loadDocument(File file)
	{
		docSource = null;
		xmlDoc = null;
		loadXML(file);
		catalogNodes();
		return errors.size() == 0 ? true : false;
	}

	public String findElementOrAttrValue(Element elem, String nodeName)
	{
		String attrValue = elem.getAttribute(nodeName);
		if(attrValue.length() > 0)
			return attrValue;

		NodeList children = elem.getChildNodes();
		for(int n = 0; n < children.getLength(); n++)
		{
			Node node = children.item(n);
			if(node.getNodeName().equals(nodeName))
				return node.getFirstChild().getNodeValue();
		}

		return null;
	}

	public String ucfirst(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public void inheritNodes(Element element, Map sourcePool, String attrName)
	{
		String inheritAttr = element.getAttribute(attrName);
		while(inheritAttr != null && inheritAttr.length() > 0)
		{
			Element inheritFromElem = null;
			StringTokenizer inheritST = new StringTokenizer(inheritAttr, ",");
			String[] inherits = new String[15];
			int inheritsCount = 0;
			while(inheritST.hasMoreTokens())
			{
				inherits[inheritsCount] = inheritST.nextToken();
			    inheritsCount++;
			}

            /** we're going to work backwards because we want to make sure the
             *  elements are added in the appropriate order (same order as the
             *  inheritance list)
             */

			for(int j = (inheritsCount-1); j >= 0; j--)
			{
				String inheritType = inherits[j];
				inheritFromElem = (Element) sourcePool.get(inheritType);
				if(inheritFromElem == null)
				{
					errors.add("can not extend '"+ element.getAttribute("name") +"' from '"+ inheritType +"': source not found");
					continue;
				}

				/* don't inherit the same objects more than once */
				String inheritanceId = Integer.toString(element.hashCode()) + '.' + Integer.toString(inheritFromElem.hashCode());
				if(inheritanceHistorySet.contains(inheritanceId))
					continue;
				inheritanceHistorySet.add(inheritanceId);

                Element extendsElem = xmlDoc.createElement("extends");
                extendsElem.appendChild(xmlDoc.createTextNode(inheritType));
                element.appendChild(extendsElem);

				NamedNodeMap inhAttrs = inheritFromElem.getAttributes();
				for(int i = 0; i < inhAttrs.getLength(); i++)
				{
					Node attrNode = inhAttrs.item(i);
					if(element.getAttribute(attrNode.getNodeName()).equals(""))
						element.setAttribute(attrNode.getNodeName(), attrNode.getNodeValue());
				}

				DocumentFragment inheritFragment = xmlDoc.createDocumentFragment();
				NodeList inhChildren = inheritFromElem.getChildNodes();
				for(int i = inhChildren.getLength()-1; i >= 0; i--)
				{
					Node childNode = inhChildren.item(i);

					// only add if there isn't an attribute overriding this element
					if(element.getAttribute(childNode.getNodeName()).length() == 0)
					{
						Node cloned = childNode.cloneNode(true);
						if(cloned.getNodeType() == Node.ELEMENT_NODE)
							((Element) cloned).setAttribute("_inherited-from", inheritType);
						inheritFragment.insertBefore(cloned, inheritFragment.getFirstChild());
					}
				}

				element.insertBefore(inheritFragment, element.getFirstChild());
			}

			// find the next one if we have more parents
			if(inheritFromElem != null)
				inheritAttr = inheritFromElem.getAttribute(attrName);
			else
				inheritAttr = null;
		}
	}

    public void replaceNodeValue(Node node, String findStr, String replStr)
    {
        String srcStr = node.getNodeValue();
		if(srcStr == null || findStr == null || replStr == null)
			return;

        int findLoc = srcStr.indexOf(findStr);
        if(findLoc >= 0)
        {
            StringBuffer sb = new StringBuffer(srcStr);
            sb.replace(findLoc, findLoc + findStr.length(), replStr);
            node.setNodeValue(sb.toString());
        }
    }

    public void replaceNodeMacros(Node inNode, HashSet nodeNames, Hashtable params)
    {
        if(params == null || params.size() == 0)
            return;

		String nodeType = inNode.getParentNode().getNodeName() + "." + inNode.getNodeName();
        Enumeration e = params.keys();
        while(e.hasMoreElements())
        {
			String paramName = (String) e.nextElement();
            String paramRepl = "$" + paramName + "$";
            String paramValue = (String) params.get(paramName);
			if(paramValue == null)
				continue;

            NamedNodeMap attrs = inNode.getAttributes();
            if(attrs != null && attrs.getLength() > 0)
            {
                for(int i = 0; i < attrs.getLength(); i++)
                {
                    Node attr = attrs.item(i);
                    if(nodeNames.contains(attr.getNodeName()))
                        replaceNodeValue(attr, paramRepl, paramValue);
                }
            }
            NodeList children = inNode.getChildNodes();
            for(int c = 0; c < children.getLength(); c++)
            {
                Node node = children.item(c);
                if(node.getNodeType() == Node.ELEMENT_NODE && nodeNames.contains(node.getNodeName()))
                    replaceNodeValue(node.getFirstChild(), paramRepl, paramValue);
            }
        }
    }

	public void addMetaInfoOptions()
	{
		if(xmlDoc == null || metaInfoElem == null)
			return;

		if(metaInfoOptionsElem != null)
		    metaInfoElem.removeChild(metaInfoOptionsElem);

	    metaInfoOptionsElem = xmlDoc.createElement("options");
		metaInfoOptionsElem.setAttribute("name", "Allow reload");
		metaInfoOptionsElem.setAttribute("value", (allowReload ? "Yes" : "No"));
		metaInfoElem.appendChild(metaInfoOptionsElem);
	}

	public void addMetaInformation()
	{
		metaInfoElem = xmlDoc.createElement("meta-info");
		xmlDoc.getDocumentElement().appendChild(metaInfoElem);

		addMetaInfoOptions();

		Element filesElem = xmlDoc.createElement("source-files");
		metaInfoElem.appendChild(filesElem);

		for(Iterator sfi = sourceFiles.values().iterator(); sfi.hasNext(); )
		{
			SourceInfo si = (SourceInfo) sfi.next();
			Element fileElem = xmlDoc.createElement("source-file");
			fileElem.setAttribute("abs-path", si.getFile().getAbsolutePath());
			if(si.getParent() != null)
				fileElem.setAttribute("included-from", si.getParent().getFile().getName());
			filesElem.appendChild(fileElem);
		}

		if(errors.size() > 0)
		{
			Element errorsElem = xmlDoc.createElement("errors");
	    	metaInfoElem.appendChild(errorsElem);

			for(Iterator ei = errors.iterator(); ei.hasNext(); )
			{
				Element errorElem = xmlDoc.createElement("error");
				Text errorText = xmlDoc.createTextNode((String) ei.next());
				errorElem.appendChild(errorText);
				errorsElem.appendChild(errorElem);
			}
		}
	}

	public void catalogNodes()
	{
	}

	public Document loadXML(File file)
	{
		if(docSource == null)
		{
			errors.clear();
			sourceFiles.clear();
			metaInfoElem = null;
			metaInfoOptionsElem = null;
		}

		SourceInfo sourceInfo = new SourceInfo(docSource, file);
		sourceFiles.put(file.getAbsolutePath(), sourceInfo);

		Document doc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.parse(file);
            doc.normalize();
		}
		catch(Exception e)
		{
			throw new RuntimeException("XML Parsing error in '" + file.getAbsolutePath() + "': " + e);
		}

		if(docSource == null)
		{
			xmlDoc = doc;
			docSource = sourceInfo;
		}

		/*
		 find all of the <include file="xyz"> elements and "include" all the
		 elements in that document as children of the main document
		*/

		Element rootElem = doc.getDocumentElement();
		NodeList includes = rootElem.getElementsByTagName("include");
		if(includes != null && includes.getLength() > 0)
		{
			for(int n = 0; n < includes.getLength(); n++)
			{
				Element include = (Element) includes.item(n);
                String incFileAttr = include.getAttribute("file");
				File incFile = new File(file.getParentFile(), incFileAttr);
				if(! sourceFiles.containsKey(incFile.getAbsolutePath()))
				{
					Document includeDoc = loadXML(incFile);
					if(includeDoc != null)
					{
						Element includeRoot = includeDoc.getDocumentElement();
						NodeList incChildren = includeRoot.getChildNodes();
						for(int c = 0; c < incChildren.getLength(); c++)
						{
                            Node incCopy = doc.importNode(incChildren.item(c), true);
                            if(incCopy.getNodeType() == Node.ELEMENT_NODE)
                                ((Element) incCopy).setAttribute("_included-from", incFileAttr);
							rootElem.insertBefore(incCopy, include);
						}
					}
				}
			}
		}

		return doc;
 	}

	public void saveXML(String fileName)
	{
		/* we use reflection so that org.apache.xml.serialize.* is not a package requirement */

		OutputStream os = null;
		try
		{
			Class serializerCls = Class.forName("org.apache.xml.serialize.XMLSerializer");
			Class outputFormatCls = Class.forName("org.apache.xml.serialize.OutputFormat");

			Constructor serialCons = serializerCls.getDeclaredConstructor(new Class[] { OutputStream.class, outputFormatCls });
			Constructor outputCons = outputFormatCls.getDeclaredConstructor(new Class[] { Document.class });

			os = new FileOutputStream(fileName);
			Object outputFormat = outputCons.newInstance(new Object[] { xmlDoc });
			Method indenting = outputFormatCls.getMethod("setIndenting", new Class[] { boolean.class });
			indenting.invoke(outputFormat, new Object[] { new Boolean(true) });

			Object serializer = serialCons.newInstance(new Object[] { os, outputFormat });
			Method serialize = serializerCls.getMethod("serialize", new Class[] { Document.class });
			serialize.invoke(serializer, new Object[] { xmlDoc });
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unable to save '" + fileName + "': " + e);
		}
		finally
		{
			try
			{
				if(os != null)
	    			os.close();
			}
			catch(Exception e)
			{
			}
		}
	}

	public NodeList selectNodeList(String expr) throws TransformerException
	{
		return XPathAPI.selectNodeList(xmlDoc, expr);
	}

	public long getSelectNodeListCount(String expr) throws TransformerException
	{
		NodeList nodes = selectNodeList(expr);
		return nodes.getLength();
	}

	public Metric getMetrics(Metric root)
	{
		return null;
	}
}