/**
 * This class was generated from a set of XML constraints
 *   by the Enhydra Zeus XML Data Binding Framework. All
 *   source code in this file is constructed specifically
 *   to work with other Zeus-generated classes. If you
 *   modify this file by hand, you run the risk of breaking
 *   this interoperation, as well as introducing errors in
 *   source code compilation.
 *
 * * * * * MODIFY THIS FILE AT YOUR OWN RISK * * * * *
 *
 * To find out more about the Enhydra Zeus framework, you
 *   can point your browser at <http://zeus.enhydra.org>
 *   where you can download releases, join and discuss Zeus
 *   on user and developer mailing lists, and access source
 *   code. Please report any bugs through that website.
 */
package com.netspective.junxion.edi.format;

// Global Implementation Import Statements
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class DataElemImpl extends DefaultHandler implements Unmarshallable, DataElem {

    private String name;
    private boolean zeus_nameSet;
    private String delimiter;
    private boolean zeus_delimiterSet;
    private String lengthMin;
    private boolean zeus_lengthMinSet;
    private String hideElement;
    private boolean zeus_hideElementSet;
    private String repeatMin;
    private boolean zeus_repeatMinSet;
    private String type;
    private boolean zeus_typeSet;
    private String lengthMax;
    private boolean zeus_lengthMaxSet;
    private String xmldefault;
    private boolean zeus_xmldefaultSet;
    private String repeatMax;
    private boolean zeus_repeatMaxSet;
    private String description;
    private boolean zeus_descriptionSet;

    /** The current node in unmarshalling */
    private Unmarshallable zeus_currentUNode;

    /** The parent node in unmarshalling */
    private Unmarshallable zeus_parentUNode;

    /** Whether this node has been handled */
    private boolean zeus_thisNodeHandled = false;

    /** The EntityResolver for SAX parsing to use */
    private static EntityResolver entityResolver;

    /** The ErrorHandler for SAX parsing to use */
    private static ErrorHandler errorHandler;

    public DataElemImpl() {
        zeus_nameSet = false;
        zeus_delimiterSet = false;
        lengthMin = "0";
        zeus_lengthMinSet = false;
        zeus_hideElementSet = false;
        repeatMin = "0";
        zeus_repeatMinSet = false;
        zeus_typeSet = false;
        lengthMax = "0";
        zeus_lengthMaxSet = false;
        zeus_xmldefaultSet = false;
        repeatMax = "1";
        zeus_repeatMaxSet = false;
        zeus_descriptionSet = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        zeus_nameSet = true;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        zeus_delimiterSet = true;
    }

    public String getLengthMin() {
        return lengthMin;
    }

    public void setLengthMin(String lengthMin) {
        this.lengthMin = lengthMin;
        zeus_lengthMinSet = true;
    }

    public String getHideElement() {
        return hideElement;
    }

    public void setHideElement(String hideElement) {
        this.hideElement = hideElement;
        zeus_hideElementSet = true;
    }

    public String getRepeatMin() {
        return repeatMin;
    }

    public void setRepeatMin(String repeatMin) {
        this.repeatMin = repeatMin;
        zeus_repeatMinSet = true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        zeus_typeSet = true;
    }

    public String getLengthMax() {
        return lengthMax;
    }

    public void setLengthMax(String lengthMax) {
        this.lengthMax = lengthMax;
        zeus_lengthMaxSet = true;
    }

    public String getXmldefault() {
        return xmldefault;
    }

    public void setXmldefault(String xmldefault) {
        this.xmldefault = xmldefault;
        zeus_xmldefaultSet = true;
    }

    public String getRepeatMax() {
        return repeatMax;
    }

    public void setRepeatMax(String repeatMax) {
        this.repeatMax = repeatMax;
        zeus_repeatMaxSet = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        zeus_descriptionSet = true;
    }

    public void marshal(File file) throws IOException {
        // Delegate to the marshal(Writer) method
        marshal(new FileWriter(file));
    }

    public void marshal(OutputStream outputStream) throws IOException {
        // Delegate to the marshal(Writer) method
        marshal(new OutputStreamWriter(outputStream));
    }

    public void marshal(Writer writer) throws IOException {
        // Write out the XML declaration
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");

        // Now start the recursive writing
        writeXMLRepresentation(writer, "");

        // Close up
        writer.flush();
        writer.close();
    }

    protected void writeXMLRepresentation(Writer writer,
                                          String indent)
        throws IOException {

        writer.write(indent);
        writer.write("<data-elem");

        // Handle attributes (if needed)
        if (zeus_nameSet) {
            writer.write(" name=\"");
            writer.write(escapeAttributeValue(name));
            writer.write("\"");
        }
        if (zeus_delimiterSet) {
            writer.write(" delimiter=\"");
            writer.write(escapeAttributeValue(delimiter));
            writer.write("\"");
        }
        if (zeus_lengthMinSet) {
            writer.write(" length-min=\"");
            writer.write(escapeAttributeValue(lengthMin));
            writer.write("\"");
        }
        if (zeus_hideElementSet) {
            writer.write(" hide-element=\"");
            writer.write(escapeAttributeValue(hideElement));
            writer.write("\"");
        }
        if (zeus_repeatMinSet) {
            writer.write(" repeat-min=\"");
            writer.write(escapeAttributeValue(repeatMin));
            writer.write("\"");
        }
        if (zeus_typeSet) {
            writer.write(" type=\"");
            writer.write(escapeAttributeValue(type));
            writer.write("\"");
        }
        if (zeus_lengthMaxSet) {
            writer.write(" length-max=\"");
            writer.write(escapeAttributeValue(lengthMax));
            writer.write("\"");
        }
        if (zeus_xmldefaultSet) {
            writer.write(" default=\"");
            writer.write(escapeAttributeValue(xmldefault));
            writer.write("\"");
        }
        if (zeus_repeatMaxSet) {
            writer.write(" repeat-max=\"");
            writer.write(escapeAttributeValue(repeatMax));
            writer.write("\"");
        }
        if (zeus_descriptionSet) {
            writer.write(" description=\"");
            writer.write(escapeAttributeValue(description));
            writer.write("\"");
        }
        writer.write(">");
        writer.write("</data-elem>\n");
    }

    private String escapeAttributeValue(String attributeValue) {
        String returnValue = attributeValue;
        for (int i = 0; i < returnValue.length(); i++) {
            char ch = returnValue.charAt(i);
            if (ch == '"') {
                returnValue = new StringBuffer()
                    .append(returnValue.substring(0, i))
                    .append("&quot;")
                    .append(returnValue.substring(i+1))
                    .toString();
            }
        }
        return returnValue;
    }

    /**
     * <p>
     *  This sets a SAX <code>EntityResolver</code> for this unmarshalling process.
     * </p>
     *
     * @param resolver the entity resolver to use.
     */
    public static void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }

    /**
     * <p>
     *  This sets a SAX <code>ErrorHandler</code> for this unmarshalling process.
     * </p>
     *
     * @param handler the entity resolver to use.
     */
    public static void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    public static DataElem unmarshal(File file) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new FileReader(file));
    }

    public static DataElem unmarshal(File file, boolean validate) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new FileReader(file), validate);
    }

    public static DataElem unmarshal(InputStream inputStream) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new InputStreamReader(inputStream));
    }

    public static DataElem unmarshal(InputStream inputStream, boolean validate) throws IOException {
        // Delegate to the unmarshal(Reader) method
        return unmarshal(new InputStreamReader(inputStream), validate);
    }

    public static DataElem unmarshal(Reader reader) throws IOException {
        // Delegate with default validation value
        return unmarshal(reader, false);
    }

    public static DataElem unmarshal(Reader reader, boolean validate) throws IOException {
        DataElemImpl dataElem = new DataElemImpl();
        dataElem.setCurrentUNode(dataElem);
        dataElem.setParentUNode(null);
        // Load the XML parser
        XMLReader parser = null;
        String parserClass = System.getProperty("org.xml.sax.driver",
            "org.apache.xerces.parsers.SAXParser");
        try {
            parser = XMLReaderFactory.createXMLReader(parserClass);

            // Set entity resolver, if needed
            if (entityResolver != null) {
                parser.setEntityResolver(entityResolver);
            }

            // Set error handler, if needed
            if (errorHandler != null) {
                parser.setErrorHandler(errorHandler);
            }

            // Register content handler
            parser.setContentHandler(dataElem);
        } catch (SAXException e) {
            throw new IOException("Could not load XML parser: " + 
                e.getMessage());
        }

        InputSource inputSource = new InputSource(reader);
        try {
            parser.setFeature("http://xml.org/sax/features/validation", new Boolean(validate).booleanValue());
            parser.parse(inputSource);
        } catch (SAXException e) {
            throw new IOException("Error parsing XML document: " + 
                e.getMessage());
        }

        // Return the resultant object
        return dataElem;
    }

    public Unmarshallable getParentUNode() {
        return zeus_parentUNode;
    }

    public void setParentUNode(Unmarshallable parentUNode) {
        this.zeus_parentUNode = parentUNode;
    }

    public Unmarshallable getCurrentUNode() {
        return zeus_currentUNode;
    }

    public void setCurrentUNode(Unmarshallable currentUNode) {
        this.zeus_currentUNode = currentUNode;
    }

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
        throws SAXException {

        // Feed this to the correct ContentHandler
        Unmarshallable current = getCurrentUNode();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
            return;
        }

        // See if we handle, or we delegate
        if ((localName.equals("data-elem")) && (!zeus_thisNodeHandled)) {
            // Handle ourselves
            for (int i=0, len=atts.getLength(); i<len; i++) {
                String attName= atts.getLocalName(i);
                String attValue = atts.getValue(i);
                if (attName.equals("name")) {
                    setName(attValue);
                }
                if (attName.equals("delimiter")) {
                    setDelimiter(attValue);
                }
                if (attName.equals("length-min")) {
                    setLengthMin(attValue);
                }
                if (attName.equals("hide-element")) {
                    setHideElement(attValue);
                }
                if (attName.equals("repeat-min")) {
                    setRepeatMin(attValue);
                }
                if (attName.equals("type")) {
                    setType(attValue);
                }
                if (attName.equals("length-max")) {
                    setLengthMax(attValue);
                }
                if (attName.equals("default")) {
                    setXmldefault(attValue);
                }
                if (attName.equals("repeat-max")) {
                    setRepeatMax(attValue);
                }
                if (attName.equals("description")) {
                    setDescription(attValue);
                }
            }
            zeus_thisNodeHandled = true;
            return;
        } else {
            // Delegate handling
        }
    }

    public void endElement(String namespaceURI, String localName,
                           String qName)
        throws SAXException {

        Unmarshallable current = getCurrentUNode();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
            return;
        }

        Unmarshallable parent = getCurrentUNode().getParentUNode();
        if (parent != null) {
            parent.setCurrentUNode(parent);
        }
    }

    public void characters(char[] ch, int start, int len)
        throws SAXException {

        // Feed this to the correct ContentHandler
        Unmarshallable current = getCurrentUNode();
        if (current != this) {
            current.characters(ch, start, len);
            return;
        }

        String text = new String(ch, start, len);
        text = text.trim();
    }

}
