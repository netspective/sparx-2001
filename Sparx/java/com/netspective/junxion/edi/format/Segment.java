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
import java.util.List;
import java.io.Serializable;

public interface Segment extends Serializable {

    public static final String ZEUS_XML_NAME = "segment";
    public static final String[] ZEUS_ATTRIBUTES = {"repeat-min", "delimiter", "name", "hide-element", "description", "repeat-max", "id"};
    public static final String[] ZEUS_ELEMENTS = {"data-elem-group", "data-elem"};

    public List getDataElemGroupList();

    public void setDataElemGroupList(List dataElemGroupList);

    public void addDataElemGroup(DataElemGroup dataElemGroup);

    public void removeDataElemGroup(DataElemGroup dataElemGroup);

    public List getDataElemList();

    public void setDataElemList(List dataElemList);

    public void addDataElem(DataElem dataElem);

    public void removeDataElem(DataElem dataElem);

    public String getRepeatMin();

    public void setRepeatMin(String repeatMin);

    public String getDelimiter();

    public void setDelimiter(String delimiter);

    public String getName();

    public void setName(String name);

    public String getHideElement();

    public void setHideElement(String hideElement);

    public String getDescription();

    public void setDescription(String description);

    public String getRepeatMax();

    public void setRepeatMax(String repeatMax);

    public String getId();

    public void setId(String id);

    public void marshal(File file) throws IOException;

    public void marshal(OutputStream outputStream) throws IOException;

    public void marshal(Writer writer) throws IOException;

}
