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
 * $Id: GeneralColumn.java,v 1.2 2002-02-09 15:04:59 snshah Exp $
 */

package com.netspective.sparx.xaf.report.column;

import java.sql.Types;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.report.ColumnDataCalculator;
import com.netspective.sparx.xaf.report.Report;
import com.netspective.sparx.xaf.report.ReportColumn;
import com.netspective.sparx.xaf.report.ReportColumnConditionalApplyFlag;
import com.netspective.sparx.xaf.report.ReportColumnConditionalState;
import com.netspective.sparx.xaf.report.ReportColumnFactory;
import com.netspective.sparx.xaf.report.ReportContext;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class GeneralColumn implements ReportColumn
{
    static public final int ALIGN_LEFT = 0;
    static public final int ALIGN_CENTER = 1;
    static public final int ALIGN_RIGHT = 2;

    static public final String PLACEHOLDER_COLDATA = "{.}";
    static public final String PLACEHOLDER_OPEN = "{";
    static public final String PLACEHOLDER_CLOSE = "}";

    private int dataType;
    private int alignStyle;
    private int colIndexInArray;
    private int colIndexInResultSet;
    private SingleValueSource headingValueSource;
    private SingleValueSource urlValueSource;
    private SingleValueSource urlAnchorAttrsValueSource;
    private String calcCmd;
    private Format formatter;
    private String outputPattern;
    private int width;
    private long flags;
    private String breakHeader;
    private ReportColumnConditionalState[] conditionalStates;

    public GeneralColumn()
    {
        this(-1, null, null);
    }

    public GeneralColumn(int colIndex, String colHeading)
    {
        this(colIndex, colHeading, null);
    }

    public GeneralColumn(int colIndex, String colHeading, String colURL)
    {
        flags = 0;
        colIndexInArray = colIndex;
        colIndexInResultSet = colIndex + 1;
        setHeading(colHeading);
        setUrl(colURL);
        dataType = Types.VARCHAR;
        alignStyle = ALIGN_LEFT;
    }

    public final int getDataType()
    {
        return dataType;
    }

    public final void setDataType(int value)
    {
        dataType = value;
    }

    public final String getBreak()
    {
        return breakHeader;
    }

    public final void setBreak(String header)
    {
        breakHeader = header;
    }

    public final int getColIndexInResultSet()
    {
        return colIndexInResultSet;
    }

    public final int getColIndexInArray()
    {
        return colIndexInArray;
    }

    public final void setColIndexInArray(int value)
    {
        colIndexInArray = value;
        colIndexInResultSet = value + 1;
    }

    public final SingleValueSource getHeading()
    {
        return headingValueSource;
    }

    public final void setHeading(String value)
    {
        headingValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
    }

    public final SingleValueSource getUrl()
    {
        return urlValueSource;
    }

    public final void setUrl(String value)
    {
        urlValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
        if(urlValueSource != null)
            setFlag(COLFLAG_WRAPURL);
        else
            clearFlag(COLFLAG_WRAPURL);
    }

    public SingleValueSource getUrlAnchorAttrs()
    {
        return urlAnchorAttrsValueSource;
    }

    public void setUrlAnchorAttrs(String value)
    {
        urlAnchorAttrsValueSource = (value != null && value.length() > 0) ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null;
        if(urlAnchorAttrsValueSource != null)
            setFlag(COLFLAG_HAVEANCHORATTRS);
        else
            clearFlag(COLFLAG_HAVEANCHORATTRS);
    }

    public final int getWidth()
    {
        return width;
    }

    public final void setWidth(int value)
    {
        width = value;
    }

    public final int getAlignStyle()
    {
        return alignStyle;
    }

    public final void setAlignStyle(int value)
    {
        alignStyle = value;
    }

    public final long getFlags()
    {
        return flags;
    }

    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    public final void setFlag(long flag)
    {
        flags |= flag;
    }

    public final void clearFlag(long flag)
    {
        flags &= ~flag;
    }

    public final void updateFlag(long flag, boolean set)
    {
        if(set) flags |= flag; else flags &= ~flag;
    }

    public final String getCalcCmd()
    {
        return calcCmd;
    }

    public final void setCalcCmd(String value)
    {
        calcCmd = value;
    }

    public final Format getFormatter()
    {
        return formatter;
    }

    public void setFormatter(Format value)
    {
        formatter = value;
    }

    public void setFormat(String value)
    {
        formatter = ReportColumnFactory.getFormat(value);
    }

    public final String getOutput()
    {
        return outputPattern;
    }

    public final void setOutput(String value)
    {
        outputPattern = value;
        if(outputPattern != null)
        {
            outputPattern = value;
            setFlag(COLFLAG_HASOUTPUTPATTERN);
        }
        else
            clearFlag(COLFLAG_HASOUTPUTPATTERN);
    }

    public String resolvePattern(String srcStr)
    {
        // find all occurrences of ${.} and replace with ${x} where x is the col index (array)

        int findLoc = srcStr.indexOf(PLACEHOLDER_COLDATA);
        if(findLoc == -1)
            return srcStr;

        setFlag(COLFLAG_HASPLACEHOLDERS);

        String replacedIn = srcStr;
        String replaceWith = PLACEHOLDER_OPEN + colIndexInArray + PLACEHOLDER_CLOSE;
        while(findLoc >= 0)
        {
            StringBuffer sb = new StringBuffer(replacedIn);
            sb.replace(findLoc, findLoc + PLACEHOLDER_COLDATA.length(), replaceWith);
            replacedIn = sb.toString();
            findLoc = replacedIn.indexOf(PLACEHOLDER_COLDATA);
        }
        return replacedIn;
    }

    public String getFormattedData(ReportContext rc, long rowNum, Object[] rowData, boolean doCalc)
    {
        Object oData = rowData[getColIndexInArray()];
        String data = oData == null ? "" : oData.toString();
        if(doCalc)
        {
            ColumnDataCalculator calc = rc.getCalc(getColIndexInArray());
            if(calc != null)
                calc.addValue(rc, this, rowNum, rowData, data);
        }
        return data;
    }

    public String getFormattedData(ReportContext rc, ColumnDataCalculator calc)
    {
        if(calc != null)
        {
            if(formatter != null)
                return formatter.format(new Double(calc.getValue(rc)));
            else
                return rc.generalNumberFmt.format(calc.getValue(rc));
        }
        else
            return "";
    }

    public ReportColumnConditionalState[] getConditionalStates()
    {
        return conditionalStates;
    }

    public void importFromColumn(ReportColumn rc)
    {
        flags = rc.getFlags();

        this.headingValueSource = rc.getHeading();
        this.urlValueSource = rc.getUrl();
        this.urlAnchorAttrsValueSource = rc.getUrlAnchorAttrs();
        this.conditionalStates = rc.getConditionalStates();
        setAlignStyle(rc.getAlignStyle());
        setWidth(rc.getWidth());
        setCalcCmd(rc.getCalcCmd());
        Format fmt = rc.getFormatter();
        if(fmt != null)
            setFormatter(fmt);
        setOutput(rc.getOutput());
    }

    public void importChildrenFromXml(Element elem)
    {
        List conditionals = null;
        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.equals("conditional"))
            {
                if(conditionals == null) conditionals = new ArrayList();
                ReportColumnConditionalState conditional = new ReportColumnConditionalApplyFlag();
                conditional.importFromXml(this, (Element) node, conditionals.size());
                conditionals.add(conditional);
            }
        }

        if(conditionals != null)
        {
            conditionalStates = (ReportColumnConditionalState[]) conditionals.toArray(new ReportColumnConditionalState[conditionals.size()]);
            setFlag(COLFLAG_HAVECONDITIONALS);
        }
    }

    public void importFromXml(Element elem)
    {
        String value = elem.getAttribute("heading");
        if(value.length() > 0)
            setHeading(value);

        value = elem.getAttribute("url");
        if(value.length() > 0)
            setUrl(value);

        value = elem.getAttribute("url-anchor-attrs");
        if(value.length() > 0)
            setUrlAnchorAttrs(value);

        value = elem.getAttribute("align");
        if(value.length() > 0)
        {
            if(value.equals("right"))
                setAlignStyle(ReportColumn.ALIGN_RIGHT);
            else if(value.equals("center"))
                setAlignStyle(ReportColumn.ALIGN_CENTER);
            else
                setAlignStyle(ReportColumn.ALIGN_LEFT);
        }

        value = elem.getAttribute("width");
        if(value.length() > 0)
            setWidth(Integer.parseInt(value));

        value = elem.getAttribute("display");
        if(value.length() > 0 && value.equals("no"))
            setFlag(ReportColumn.COLFLAG_HIDDEN);

        value = elem.getAttribute("calc");
        if(value.length() > 0)
            setCalcCmd(value);

        value = elem.getAttribute("format");
        if(value.length() > 0)
            setFormat(value);

        value = elem.getAttribute("output");
        if(value.length() > 0)
            setOutput(value);

        value = elem.getAttribute("word-wrap");
        if(value.equals("no"))
            setFlag(ReportColumn.COLFLAG_NOWORDBREAKS);
        else
            clearFlag(ReportColumn.COLFLAG_NOWORDBREAKS);

        importChildrenFromXml(elem);
    }

    public void finalizeContents(Report report)
    {
    }
}