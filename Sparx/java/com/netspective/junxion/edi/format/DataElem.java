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

// Global Interface Import Statements
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

// Local Interface Import Statements
import java.io.Serializable;

public interface DataElem extends Serializable {

    public static final String ZEUS_XML_NAME = "data-elem";
    public static final String[] ZEUS_ATTRIBUTES = {"name", "delimiter", "length-min", "hide-element", "repeat-min", "type", "length-max", "default", "repeat-max", "description"};
    public static final String[] ZEUS_ELEMENTS = {};

    public String getName();

    public void setName(String name);

    public String getDelimiter();

    public void setDelimiter(String delimiter);

    public String getLengthMin();

    public void setLengthMin(String lengthMin);

    public String getHideElement();

    public void setHideElement(String hideElement);

    public String getRepeatMin();

    public void setRepeatMin(String repeatMin);

    public String getType();

    public void setType(String type);

    public String getLengthMax();

    public void setLengthMax(String lengthMax);

    public String getXmldefault();

    public void setXmldefault(String xmldefault);

    public String getRepeatMax();

    public void setRepeatMax(String repeatMax);

    public String getDescription();

    public void setDescription(String description);

    public void marshal(File file) throws IOException;

    public void marshal(OutputStream outputStream) throws IOException;

    public void marshal(Writer writer) throws IOException;

}
