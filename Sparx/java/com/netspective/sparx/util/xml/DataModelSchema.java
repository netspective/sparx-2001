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
 * NOTE: This file borrows heavily from DataModelSchema.java in Jakarta Ant
 * @author Shahid N. Shah
 */

/**
 * $Id: DataModelSchema.java,v 1.1 2002-02-25 02:57:01 snshah Exp $
 */

package com.netspective.sparx.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Helper class that collects the methods a task or nested element
 * holds to set attributes, create nested elements or hold PCDATA
 * elements.
 *
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <a href="mailto:snshah@netspective.com">Shahid N. Shah</a>
 */
public class DataModelSchema
{
    public static final String ADD_TEXT_METHOD_NAME = "addText";
    private static final String lSep = System.getProperty("line.separator");

    private static SAXParserFactory parserFactory = null;
    private static XMLReader parser;
    private static Locator locator;

    /**
     * holds the types of the attributes that could be set.
     */
    private Map attributeTypes;

    /**
     * holds the attribute setter methods.
     */
    private Map attributeSetters;

    /**
     * Holds the types of nested elements that could be created.
     */
    private Map nestedTypes;

    /**
     * Holds methods to create nested elements.
     */
    private Map nestedCreators;

    /**
     * Holds methods to store configured nested elements.
     */
    private Map nestedStorers;

    /**
     * The method to add PCDATA stuff.
     */
    private Method addText = null;

    /**
     * The Class that's been introspected.
     */
    private Class bean;

    /**
     * instances we've already created
     */
    private static Map schemas = new HashMap();

    /**
     * Factory method for schema objects.
     */
    public synchronized static DataModelSchema getSchema(Class c)
    {
        DataModelSchema schema = (DataModelSchema) schemas.get(c);
        if (schema == null)
        {
            schema = new DataModelSchema(c);
            schemas.put(c, schema);
        }
        return schema;
    }

    private DataModelSchema(final Class bean)
    {
        attributeTypes = new HashMap();
        attributeSetters = new HashMap();
        nestedTypes = new HashMap();
        nestedCreators = new HashMap();
        nestedStorers = new HashMap();

        this.bean = bean;

        Method[] methods = bean.getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            final Method m = methods[i];
            final String name = m.getName();
            Class returnType = m.getReturnType();
            Class[] args = m.getParameterTypes();

            if (ADD_TEXT_METHOD_NAME.equals(name)
                    && java.lang.Void.TYPE.equals(returnType)
                    && args.length == 1
                    && java.lang.String.class.equals(args[0]))
            {
                addText = methods[i];
            }
            else if (name.startsWith("set")
                    && java.lang.Void.TYPE.equals(returnType)
                    && args.length == 1
                    && !args[0].isArray())
            {
                String propName = getPropertyName(name, "set");
                AttributeSetter as = createAttributeSetter(m, args[0]);
                if (as != null)
                {
                    attributeTypes.put(propName, args[0]);
                    attributeSetters.put(propName, as);
                }
                System.out.println(this.toString() + ": found '" + propName + "' in " + bean.getName());
            }
            else if (name.startsWith("create")
                    && !returnType.isArray()
                    && !returnType.isPrimitive()
                    && args.length == 0)
            {
                String propName = getPropertyName(name, "create");
                System.out.println(this.toString() + ": found nested '" + propName + "' in " + bean.getName());
                nestedTypes.put(propName, returnType);
                nestedCreators.put(propName, new NestedCreator()
                {
                    public Object create(Object parent)
                            throws InvocationTargetException,
                            IllegalAccessException
                    {

                        return m.invoke(parent, new Object[]{});
                    }
                });
                getSchema(returnType);
            }
            else if (name.startsWith("addConfigured")
                    && java.lang.Void.TYPE.equals(returnType)
                    && args.length == 1
                    && !java.lang.String.class.equals(args[0])
                    && !args[0].isArray()
                    && !args[0].isPrimitive())
            {
                try
                {
                    final Constructor c =
                            args[0].getConstructor(new Class[]{});
                    String propName = getPropertyName(name, "addConfigured");
                    nestedTypes.put(propName, args[0]);
                    nestedCreators.put(propName, new NestedCreator()
                    {
                        public Object create(Object parent)
                                throws InvocationTargetException, IllegalAccessException, InstantiationException
                        {

                            Object o = c.newInstance(new Object[]{});
                            return o;
                        }
                    });
                    nestedStorers.put(propName, new NestedStorer()
                    {
                        public void store(Object parent, Object child)
                                throws InvocationTargetException, IllegalAccessException, InstantiationException
                        {

                            m.invoke(parent, new Object[]{child});
                        }
                    });
                }
                catch (NoSuchMethodException nse)
                {
                }
            }
            else if (name.startsWith("add")
                    && java.lang.Void.TYPE.equals(returnType)
                    && args.length == 1
                    && !java.lang.String.class.equals(args[0])
                    && !args[0].isArray()
                    && !args[0].isPrimitive())
            {
                try
                {
                    final Constructor c =
                            args[0].getConstructor(new Class[]{});
                    String propName = getPropertyName(name, "add");
                    nestedTypes.put(propName, args[0]);
                    nestedCreators.put(propName, new NestedCreator()
                    {
                        public Object create(Object parent)
                                throws InvocationTargetException, IllegalAccessException, InstantiationException
                        {

                            Object o = c.newInstance(new Object[]{});
                            m.invoke(parent, new Object[]{o});
                            return o;
                        }
                    });
                }
                catch (NoSuchMethodException nse)
                {
                }
            }
        }
    }

    private static SAXParserFactory getParserFactory()
    {
        if (parserFactory == null)
            parserFactory = SAXParserFactory.newInstance();

        return parserFactory;
    }

    /**
     * Parses the project file.
     */
    private static void parse(DataModel dm, File srcFile, List errorMessages) throws DataModelException
    {
        FileInputStream inputStream = null;
        InputSource inputSource = null;

        try
        {
            SAXParser saxParser = getParserFactory().newSAXParser();
            parser = saxParser.getXMLReader();

            String uri = "file:" + srcFile.getAbsolutePath().replace('\\', '/');
            for (int index = uri.indexOf('#'); index != -1; index = uri.indexOf('#'))
            {
                uri = uri.substring(0, index) + "%23" + uri.substring(index + 1);
            }

            inputStream = new FileInputStream(srcFile);
            inputSource = new InputSource(inputStream);
            inputSource.setSystemId(uri);

            parser.setContentHandler(new DataModelHandler(null, dm, dm, errorMessages));
            parser.parse(inputSource);
        }
        catch (ParserConfigurationException exc)
        {
            throw new DataModelException("Parser has not been configured correctly", exc);
        }
        catch (SAXParseException exc)
        {
            throw new DataModelException(exc.getMessage(), exc);
        }
        catch (SAXException exc)
        {
            Throwable t = exc.getException();
            if (t instanceof DataModelException)
            {
                throw (DataModelException) t;
            }
            throw new DataModelException(exc.getMessage(), t);
        }
        catch (FileNotFoundException exc)
        {
            throw new DataModelException(exc);
        }
        catch (IOException exc)
        {
            throw new DataModelException("Error reading project file", exc);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException ioe)
                {
                    // ignore this
                }
            }
        }
    }

    /**
     * Sets the named attribute.
     */
    public void setAttribute(DataModel dm, Object element, String attributeName, String value, List errors) throws DataModelException
    {
        AttributeSetter as = (AttributeSetter) attributeSetters.get(attributeName);
        if (as == null)
        {
            String msg = getElementName(dm, element) + " doesn't support the \"" + attributeName + "\" attribute.";
            errors.add(msg);
            return;
            //throw new DataModelException(msg);
        }
        try
        {
            as.set(dm, element, value);
        }
        catch (IllegalAccessException ie)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ie);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            if (t instanceof DataModelException)
            {
                throw (DataModelException) t;
            }
            throw new DataModelException(t);
        }
    }

    /**
     * Adds PCDATA areas.
     */
    public void addText(DataModel dm, Object element, String text, List errors)
    {
        if (addText == null)
        {
            String msg = getElementName(dm, element) + " doesn't support nested text data.";
            errors.add(msg);
            return;
            //throw new DataModelException(msg);
        }
        try
        {
            addText.invoke(element, new String[]{text});
        }
        catch (IllegalAccessException ie)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ie);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            if (t instanceof DataModelException)
            {
                throw (DataModelException) t;
            }
            throw new DataModelException(t);
        }
    }

    /**
     * Creates a named nested element.
     */
    public Object createElement(DataModel dm, Object element, String elementName, List errors)
            throws DataModelException
    {
        //System.out.println(this.toString() + ": create-elem = "+ elementName);

        NestedCreator nc = (NestedCreator) nestedCreators.get(elementName);
        if (nc == null)
        {
            String msg = getElementName(dm, element) + " doesn't support the nested \"" + elementName + "\" element.";
            errors.add(msg);
            return null;
            //throw new DataModelException(msg);
        }
        try
        {
            Object nestedElement = nc.create(element);
            return nestedElement;
        }
        catch (IllegalAccessException ie)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ie);
        }
        catch (InstantiationException ine)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ine);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            if (t instanceof DataModelException)
            {
                throw (DataModelException) t;
            }
            throw new DataModelException(t);
        }
    }

    /**
     * Creates a named nested element.
     */
    public void storeElement(DataModel dm, Object element, Object child, String elementName)
            throws DataModelException
    {
        if (elementName == null)
        {
            return;
        }
        NestedStorer ns = (NestedStorer) nestedStorers.get(elementName);
        if (ns == null)
        {
            return;
        }
        try
        {
            ns.store(element, child);
        }
        catch (IllegalAccessException ie)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ie);
        }
        catch (InstantiationException ine)
        {
            // impossible as getMethods should only return public methods
            throw new DataModelException(ine);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            if (t instanceof DataModelException)
            {
                throw (DataModelException) t;
            }
            throw new DataModelException(t);
        }
    }

    /**
     * returns the type of a named nested element.
     */
    public Class getElementType(String elementName)
            throws DataModelException
    {
        Class nt = (Class) nestedTypes.get(elementName);
        if (nt == null)
        {
            String msg = "Class " + bean.getName() +
                    " doesn't support the nested \"" + elementName + "\" element.";
            throw new DataModelException(msg);
        }
        return nt;
    }

    /**
     * returns the type of a named attribute.
     */
    public Class getAttributeType(String attributeName)
            throws DataModelException
    {
        Class at = (Class) attributeTypes.get(attributeName);
        if (at == null)
        {
            String msg = "Class " + bean.getName() +
                    " doesn't support the \"" + attributeName + "\" attribute.";
            throw new DataModelException(msg);
        }
        return at;
    }

    /**
     * Does the introspected class support PCDATA?
     */
    public boolean supportsCharacters()
    {
        return addText != null;
    }

    /**
     * Return all attribues supported by the introspected class.
     */
    public Set getAttributes()
    {
        return attributeSetters.keySet();
    }

    /**
     * Return all nested elements supported by the introspected class.
     */
    public Map getNestedElements()
    {
        return nestedTypes;
    }

    /**
     * Create a proper implementation of AttributeSetter for the given
     * attribute type.
     */
    private AttributeSetter createAttributeSetter(final Method m,
                                                  final Class arg)
    {

        // simplest case - setAttribute expects String
        if (java.lang.String.class.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new String[]{value});
                }
            };

            // now for the primitive types, use their wrappers
        }
        else if (java.lang.Character.class.equals(arg)
                || java.lang.Character.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Character[]{new Character(value.charAt(0))});
                }

            };
        }
        else if (java.lang.Byte.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Byte[]{new Byte(value)});
                }

            };
        }
        else if (java.lang.Short.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Short[]{new Short(value)});
                }

            };
        }
        else if (java.lang.Integer.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Integer[]{new Integer(value)});
                }

            };
        }
        else if (java.lang.Long.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Long[]{new Long(value)});
                }

            };
        }
        else if (java.lang.Float.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Float[]{new Float(value)});
                }

            };
        }
        else if (java.lang.Double.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent, new Double[]{new Double(value)});
                }

            };
        }
        // boolean gets an extra treatment, because we have a nice method
        // in XmlSource
        else if (java.lang.Boolean.class.equals(arg)
                || java.lang.Boolean.TYPE.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    m.invoke(parent,
                             new Boolean[]{new Boolean(XmlSource.toBoolean(value))});
                }

            };
        }
        // Class doesn't have a String constructor but a decent factory method
        else if (java.lang.Class.class.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException, DataModelException
                {
                    try
                    {
                        m.invoke(parent, new Class[]{Class.forName(value)});
                    }
                    catch (ClassNotFoundException ce)
                    {
                        throw new DataModelException(ce);
                    }
                }
            };
        }
        else if (java.io.File.class.equals(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException
                {
                    // resolve relative paths through DataModel
                    m.invoke(parent, new File[]{dm.resolveFile(value)});
                }

            };
        }
        else if (EnumeratedAttribute.class.isAssignableFrom(arg))
        {
            return new AttributeSetter()
            {
                public void set(DataModel dm, Object parent, String value)
                        throws InvocationTargetException, IllegalAccessException, DataModelException
                {
                    try
                    {
                        EnumeratedAttribute ea = (EnumeratedAttribute) arg.newInstance();
                        ea.setValue(value);
                        m.invoke(parent, new EnumeratedAttribute[]{ea});
                    }
                    catch (InstantiationException ie)
                    {
                        throw new DataModelException(ie);
                    }
                }
            };
        }
        else
        {
            // worst case. look for a public String constructor and use it

            try
            {
                final Constructor c =
                        arg.getConstructor(new Class[]{java.lang.String.class});

                return new AttributeSetter()
                {
                    public void set(DataModel dm, Object parent,
                                    String value)
                            throws InvocationTargetException, IllegalAccessException, DataModelException
                    {
                        try
                        {
                            Object attribute = c.newInstance(new String[]{value});
                            m.invoke(parent, new Object[]{attribute});
                        }
                        catch (InstantiationException ie)
                        {
                            throw new DataModelException(ie);
                        }
                    }
                };

            }
            catch (NoSuchMethodException nme)
            {
            }
        }

        return null;
    }

    protected String getElementName(DataModel dm, Object element)
    {
        return "Class " + (element != null ? element.getClass().getName() : "NULL");
    }

    /**
     * extract the name of a property from a method name - subtracting
     * a given prefix.
     */
    private String getPropertyName(String methodName, String prefix)
    {
        int start = prefix.length();
        return methodName.substring(start).toLowerCase();
    }

    private interface NestedCreator
    {
        public Object create(Object parent)
                throws InvocationTargetException, IllegalAccessException, InstantiationException;
    }

    private interface NestedStorer
    {
        public void store(Object parent, Object child)
                throws InvocationTargetException, IllegalAccessException, InstantiationException;
    }

    private interface AttributeSetter
    {
        public void set(DataModel dm, Object parent, String value)
                throws InvocationTargetException, IllegalAccessException,
                DataModelException;
    }

    /**
     * Handler for all nested properties.
     */
    private static class DataModelHandler implements ContentHandler
    {
        private DataModelHandler parentHandler;
        private DataModel dataModel;
        private Object parent;
        private Object child;
        private List errors;

        public DataModelHandler(DataModelHandler parentHandler, DataModel dm, Object parent, List errors)
        {
            System.out.println(this.toString() + ": construct parent = "+ parent + " parentHandler = " + parentHandler);
            this.parentHandler = parentHandler;
            this.dataModel = dm;
            this.parent = parent;
            this.errors = errors;
            parser.setContentHandler(this);
        }

        public void characters(char[] buf, int start, int end) throws SAXParseException
        {
            try
            {
                String text = new String(buf, start, end);
                if (text == null || text.trim().length() == 0)
                    return;

                DataModelSchema.getSchema(child.getClass()).addText(dataModel, child, text, errors);
            }
            catch (DataModelException exc)
            {
                throw new SAXParseException(exc.getMessage(), locator, exc);
            }
        }

        public void startElement(String url, String localName, String qName, Attributes attributes) throws SAXException
        {
            System.out.println(this.toString() + ": start " + qName + " parent = " + parent.toString());
            try
            {
                DataModelSchema parentSchema = DataModelSchema.getSchema(parent.getClass());
                child = parentSchema.createElement(dataModel, parent, qName.toLowerCase(), errors);
                if(child == null)
                    return;
                System.out.println(this.toString() + ": child created = " + child.toString());

                DataModelSchema childSchema = DataModelSchema.getSchema(child.getClass());

                for (int i = 0; i < attributes.getLength(); i++)
                {
                    childSchema.setAttribute(dataModel, child, attributes.getQName(i).toLowerCase(), attributes.getValue(i), errors);
                }
                childSchema.storeElement(dataModel, parent, child, qName.toLowerCase());
            }
            catch (DataModelException exc)
            {
                throw new SAXParseException(exc.getMessage(), locator, exc);
            }

            new DataModelHandler(this, dataModel, child, errors);
        }

        public void endDocument() throws SAXException
        {
        }

        public void endElement(String s, String s1, String s2) throws SAXException
        {
            if(parentHandler != null)
                parser.setContentHandler(parentHandler);
        }

        public void endPrefixMapping(String s) throws SAXException
        {
        }

        public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException
        {
        }

        public void processingInstruction(String s, String s1) throws SAXException
        {
        }

        public void setDocumentLocator(Locator locator)
        {
        }

        public void skippedEntity(String s) throws SAXException
        {
        }

        public void startDocument() throws SAXException
        {
        }

        public void startPrefixMapping(String s, String s1) throws SAXException
        {
        }
    }

    public static String toString(DataModel dm) throws DataModelException
    {
        Set visited = new HashSet();
        StringBuffer sb = new StringBuffer();
        toString("ROOT", dm.getClass(), sb, visited);
        return sb.toString();
    }

    public static void toString(String name, Class element, StringBuffer sb, Set visited) throws DataModelException
    {
        if (visited.contains(name))
            return;
        visited.add(name);

        DataModelSchema schema = DataModelSchema.getSchema(element);

        sb.append("<!ELEMENT ");
        sb.append(name).append(" ");

        if (schema.supportsCharacters())
            sb.append("#PCDATA");

        Map nested = schema.getNestedElements();
        Iterator i = nested.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry) i.next();
            toString(entry.getKey().toString(), entry.getClass(), sb, visited);
        }
        sb.append(">");

        i = schema.getAttributes().iterator();
        while (i.hasNext()) {
            String attrName = (String) i.next();
            sb.append(lSep).append("          ").append(attrName).append(" ");
            Class type = schema.getAttributeType(attrName);
        }
        sb.append(">").append(lSep);
    }

    static public class DataModelTest implements DataModel
    {
        private RootTest root;

        public DataModelTest()
        {
        }

        public RootTest createRoot()
        {
            root = new RootTest();
            return root;
        }

        public RootTest getRoot()
        {
            return root;
        }

        public void setRoot(RootTest root)
        {
            this.root = root;
        }

        public File resolveFile(String src)
        {
            return null;
        }
    }

    static public class RootTest
    {
        private String text;
        private int integer;
        private Nested1Test nested1;

        public RootTest()
        {
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public int getInteger()
        {
            return integer;
        }

        public void setInteger(int integer)
        {
            this.integer = integer;
        }

        public Nested1Test createNested1()
        {
            nested1 = new Nested1Test();
            return nested1;
        }

        public Nested1Test getNested1()
        {
            return nested1;
        }
    }

    static public class Nested1Test
    {
        private String text;
        private int integer;

        public Nested1Test()
        {
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public int getInteger()
        {
            return integer;
        }

        public void setInteger(int integer)
        {
            this.integer = integer;
        }

        public Nested11Test createNested11()
        {
            return new Nested11Test();
        }
    }

    static public class Nested11Test
    {
        private String text;
        private int integer;

        public Nested11Test()
        {
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public int getInteger()
        {
            return integer;
        }

        public void setInteger(int integer)
        {
            this.integer = integer;
        }
    }

    public static void main(String[] args)
    {
        List errorMessages = new ArrayList();

        DataModelTest dmt = new DataModelTest();
        DataModelSchema.getSchema(DataModelTest.class);
        DataModelSchema tSchema = DataModelSchema.getSchema(DataModelTest.class);
        DataModelSchema rtSchema = DataModelSchema.getSchema(RootTest.class);
        DataModelSchema.parse(dmt, new File("c:/test.xml"), errorMessages);
        System.out.println(DataModelSchema.toString(dmt));
        System.out.println(errorMessages.toString());
        System.out.println(dmt.getRoot().getText());
        System.out.println(dmt.getRoot().getInteger());
        System.out.println(dmt.getRoot().getNested1().getText());
    }
}
