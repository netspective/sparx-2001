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
 * @author Shahid N. Shah
 */

/**
 * $Id: PageComponent.java,v 1.1 2003-02-03 04:24:28 roque.hernandez Exp $
 */

package com.netspective.sparx.xaf.navigate;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.List;
import java.util.ArrayList;
import java.io.Writer;
import java.io.IOException;

import com.netspective.sparx.xaf.html.ComponentCommand;
import com.netspective.sparx.xaf.html.ComponentCommandFactory;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.util.value.ValueContext;

public class PageComponent {
    static public final int PAGECMPFLAG_COLUMN_BREAK = 1;
    static public final long PAGECMPFLAG_INVISIBLE = PAGECMPFLAG_COLUMN_BREAK * 2;
    static public final long PAGECMPFLAG_COLLAPSED = PAGECMPFLAG_INVISIBLE * 2;

    public class ComponentLayout {

        int columns = 1;
        int maxRows = 1;
        Component [][] componentsGrid;

        public void importFromXml(Element childElem) {

            String columnsStr = childElem.getAttribute("columns");
            if (columnsStr != null && columnsStr.length() > 0) {
                try {
                    columns = Integer.parseInt(columnsStr);
                } catch (NumberFormatException e) {
                    columns = 1;
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
            }
        }

        public void initializeComponents(List componentsList){
            int componentColumns = 1;
            int currentMaxRows = 1;

            for (int i = 0; i < componentsList.size(); i++) {
                Component component = (Component) componentsList.get(i);

                if (component.flagIsSet(PageComponent.PAGECMPFLAG_COLUMN_BREAK)){
                    componentColumns++;
                    currentMaxRows = 1;
                } else {
                    currentMaxRows++;
                    if (currentMaxRows > maxRows){
                        maxRows = currentMaxRows;
                    }
                }
            }
            if (componentColumns > columns) {
                columns = componentColumns;
            }

            componentsGrid = new Component[columns][maxRows];

            int currentColumn = 0;
            int currentRow = 0;
            for (int i = 0; i < componentsList.size(); i++) {
                Component component = (Component) componentsList.get(i);

                while (componentsGrid[currentColumn][currentRow] != null || (currentColumn == columns && currentRow == maxRows) ) {

                    if (currentRow + 1 < maxRows ) {
                        currentRow++;
                    } else {
                        currentRow = 0;
                        currentColumn++;
                    }
                }

                componentsGrid[currentColumn][currentRow] = component;

                if (component.colSpan > 1) {
                    for (int j = 1; j < component.colSpan; j++) {
                        componentsGrid[currentColumn + j][currentRow] = new Component();
                    }
                }

                if (component.rowSpan > 1) {
                     for (int j = 1; j < component.rowSpan; j++) {
                        componentsGrid[currentColumn][currentRow + 1] = new Component();
                    }
                }

                if (component.flagIsSet(PageComponent.PAGECMPFLAG_COLUMN_BREAK)){
                    currentColumn++;
                    currentRow = 0;
                } else {
                    currentRow++;
                }
            }

        }

        public void renderComponents(ValueContext vc, Writer writer) throws IOException, ComponentCommandException {
            writer.write("<table cellpadding='5' cellspacing='0'>");

            for (int currentRow = 0;  currentRow < maxRows; currentRow++) {

                writer.write("<tr valign=\"top\">");

                for (int currentColumn = 0; currentColumn < columns; currentColumn++) {
                    Component cmp = componentsGrid[currentColumn][currentRow];
                    if (cmp != null && cmp.getComponentCmd() != null) {
                        writer.write("<center>");
                        String colSpan = "";
                        String rowSpan = "";
                        if (cmp.colSpan > 1) {
                            colSpan = " colspan=\""+ Integer.toString(cmp.colSpan) + "\"";
                        }
                        if (cmp.rowSpan > 1) {
                            colSpan = " rowspan=\""+ Integer.toString(cmp.rowSpan) + "\"";
                        }
                        writer.write("<td" + colSpan + rowSpan + ">");
                        ComponentCommand cmd = cmp.getComponentCmd();
                        cmd.handleCommand(vc,writer,false);
                        writer.write("</center>");
                        writer.write("</td>");
                    }
                }

                writer.write("</tr>");
            }

            writer.write("</table>");
        }
    }


    public class Component {

        ComponentCommand componentCmd;
        long flags = 0;
        int colSpan = 1;
        int rowSpan = 1;

        public ComponentCommand getComponentCmd() {
            return componentCmd;
        }

        public void setComponentCmd(ComponentCommand componentCmd) {
            this.componentCmd = componentCmd;
        }

        public final long getFlags() {
            return flags;
        }

        public boolean flagIsSet(long flag) {
            return (flags & flag) == 0 ? false : true;
        }

        public void setFlag(long flag) {
            flags |= flag;
        }

        public void clearFlag(long flag) {
            flags &= ~flag;
        }

        public int getColSpan() {
            return colSpan;
        }

        public void setColSpan(int colSpan) {
            this.colSpan = colSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }


        public void importFromXml(Element childElem) {

            String cmd = childElem.getAttribute("cmd");
            if (cmd  != null && cmd.length() > 0) {
                int endOfCmdName = cmd.indexOf(',');
                String commandName = cmd.substring(0, endOfCmdName);
                ComponentCommand compCmd = ComponentCommandFactory.getCommand(commandName, cmd.substring(endOfCmdName + 1));
                this.setComponentCmd(compCmd);
            }

            String colBreak = childElem.getAttribute("col-break");
            if (colBreak != null && colBreak.length() > 0) {
                this.setFlag(PageComponent.PAGECMPFLAG_COLUMN_BREAK);
            }

            String colSpan = childElem.getAttribute("col-span");
            if (colSpan != null && colSpan.length() > 0) {
                int colSpanInt = 1;
                try {
                    colSpanInt = Integer.parseInt(colSpan);
                } catch (NumberFormatException e) {
                    colSpanInt = 1;
                }
                setColSpan(colSpanInt);
            }
            String rowSpan = childElem.getAttribute("row-span");
            if (rowSpan != null && rowSpan.length() > 0) {
                int rowSpanInt = 1;
                try {
                    rowSpanInt = Integer.parseInt(rowSpan);
                } catch (NumberFormatException e) {
                    rowSpanInt = 1;
                }
                setRowSpan(rowSpanInt);
            }
        }

    }

    List components;
    ComponentLayout layout;

    public List getComponents() {
        return components;
    }

    public void setComponents(List components) {
        this.components = components;
    }

    public ComponentLayout getLayout() {
        return layout;
    }

    public void setLayout(ComponentLayout layout) {
        this.layout = layout;
    }

    public void importFromXml(Element elem) {

        setComponents(new ArrayList());

        NodeList children = elem.getChildNodes();
        for (int c = 0; c < children.getLength(); c++) {
            Node child = children.item(c);

            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element childElem = (Element) child;

            String childName = childElem.getNodeName();
            if (childName.equals("component")) {
                Component childCmp = new Component();
                childCmp.importFromXml(childElem);
                components.add(childCmp);
            }

            if (childName.equals("layout")) {
                ComponentLayout layout = new ComponentLayout();
                layout.importFromXml(childElem);
                setLayout(layout);
            }
        }
        layout.initializeComponents(components);
    }

    public void renderComponents(ValueContext vc, Writer writer) throws IOException, ComponentCommandException {
        ComponentLayout layout = getLayout();
        layout.renderComponents(vc, writer);
    }

    public void handleDefaultBody(ValueContext vc) throws IOException, ComponentCommandException{
        renderComponents(vc, vc.getResponse().getWriter());
    }
}




