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
 * $Id: StatementParameter.java,v 1.4 2003-04-18 00:32:24 aye.thu Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogFieldFactory;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.form.field.IntegerField;
import com.netspective.sparx.xaf.form.field.FloatField;
import com.netspective.sparx.xaf.form.field.SelectField;

public class StatementParameter
{
    static class ApplyContext
    {
        private StatementInfo stmtInfo;
        private int activeParamNum;
        private List debugBindValues;

        public ApplyContext(StatementInfo stmtInfo, boolean debug)
        {
            this.stmtInfo = stmtInfo;
            if(debug) debugBindValues = new ArrayList();
            activeParamNum = 0;
        }

        public StatementInfo getStmtInfo()
        {
            return stmtInfo;
        }

        public int getNextParamNum()
        {
            return ++activeParamNum;
        }

        public void addDebugBindValue(Object object)
        {
            debugBindValues.add(object);
        }

        public Object[] getDebugBindValues()
        {
            return debugBindValues.toArray();
        }
    }

    private StatementInfo si;
    private String paramName;
    private Object valueSource;
    private int paramType;
    private int givenParamNum;
    private DialogField dialogField;
    private String fieldError;
    private boolean allowNull = false;

    public StatementParameter(StatementInfo si, int paramNum)
    {
        this.si = si;
        givenParamNum = paramNum;
    }

    public boolean allowNull()
    {
        return allowNull;
    }

    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(String paramName)
    {
        this.paramName = paramName;
    }

    public void setFieldDefaultValue()
    {
        if(paramType != Types.ARRAY)
        {
            dialogField.setDefaultValue((SingleValueSource) valueSource);
            dialogField.setHint(new StaticValue("Parameter is of type " + StatementManager.getTypeNameForId(paramType) + ", default value is '"+ ((SingleValueSource) valueSource).getId() +"'."));
        }
        else
        {
            ((SelectField) dialogField).setDefaultListValue((ListValueSource) valueSource);
            dialogField.setHint(new StaticValue("Parameter is of type " + StatementManager.getTypeNameForId(paramType) + ", default value is '"+ ((ListValueSource) valueSource).getId() +"'."));
        }
        if(fieldError != null)
            dialogField.setHint(fieldError + ". " + dialogField.getHint(null));
    }

    public void createDefaultDialogField()
    {
        String name = "param_" + givenParamNum;
        String caption = "Parameter " + givenParamNum;

        if(paramType != Types.ARRAY)
        {
            if(paramType == Types.VARCHAR)
                dialogField = new TextField(name, caption);
            else
            {
                switch(paramType)
                {
                    case Types.INTEGER:
                        dialogField = new IntegerField(name, caption);
                        break;

                    case Types.DOUBLE:
                        dialogField = new FloatField(name, caption);
                        break;
                }
            }
        }
        else
        {
            dialogField = new SelectField(name, caption, SelectField.SELECTSTYLE_MULTIDUAL, (ListValueSource) valueSource);
        }

        setFieldDefaultValue();
    }

    public void importFromXml(Element paramElem)
    {
        setParamName(paramElem.getAttribute("name"));
        String valueSrcId = paramElem.getAttribute("value");
        if(valueSrcId.length() > 0)
        {
            String paramTypeName = paramElem.getAttribute("type");
            setValue(paramTypeName, valueSrcId);
        }
        else
        {
            valueSrcId = paramElem.getAttribute("values");
            if(valueSrcId.length() > 0)
                setValues(valueSrcId);
            else
                throw new RuntimeException("Statement '"+ si.getId() +"' parameter "+ givenParamNum +" has no value specified");
        }
        // check to see whether or not the parameter allows NULLs
        String allowNullValue = paramElem.getAttribute("allow-null");
        if (allowNullValue != null && allowNullValue.equals("yes"))
            allowNull = true;

        NodeList children = paramElem.getChildNodes();
        for(int ch = 0; ch < children.getLength(); ch++)
        {
            Node node = children.item(ch);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.startsWith(DialogField.FIELDTAGPREFIX))
            {
                Element fieldElem = (Element) node;
                dialogField = DialogFieldFactory.createField(childName);
                if(dialogField != null)
                {
                    dialogField.importFromXml(fieldElem);
                    setFieldDefaultValue();
                }
                else
                {
                    fieldError = "Unable to create field of type '" + childName + "'";
                    createDefaultDialogField();
                }
            }
        }
    }

    private void setValues(String listValueSrcId)
    {
        valueSource = ValueSourceFactory.getListValueSource(listValueSrcId);
        paramType = Types.ARRAY;
    }

    private void setValue(String paramTypeName, String valueSrcId)
    {
        valueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueSrcId);
        if(paramTypeName != null && paramTypeName.length() > 0)
        {
            Integer typeNum = (Integer) StatementManager.SQL_TYPES_MAP.get(paramTypeName);
            if(typeNum == null)
                throw new RuntimeException("param type '" + paramTypeName + "' is invalid for statement '" + si.getId() + "'");
            paramType = typeNum.intValue();
        }
        else
        {
            paramType = Types.VARCHAR;
        }
    }

    public SingleValueSource getValueSource()
    {
        return (SingleValueSource) valueSource;
    }

    public ListValueSource getListSource()
    {
        return (ListValueSource) valueSource;
    }

    public boolean isListType()
    {
        return paramType == Types.ARRAY;
    }

    public int getParamType()
    {
        return paramType;
    }

    public DialogField getDialogField()
    {
        if(dialogField == null)
            createDefaultDialogField();
        return dialogField;
    }

    public void setUnitTestField(DialogField unitTestField)
    {
        this.dialogField = unitTestField;
    }

    /**
     * Apply bind parameter value to the given PreparedStatement that is running inside a StatementDialog unit
     * test type of DialogContext.
     */
    public void apply(ApplyContext ac, DialogContext dc, PreparedStatement stmt) throws SQLException
    {
        if(paramType != Types.ARRAY)
        {
            Object bindValue = dc.getValueForSqlBindParam(dialogField);
            stmt.setObject(ac.getNextParamNum(), bindValue);
            ac.addDebugBindValue(bindValue);
        }
        else
        {
            String[] values = dc.getValues(dialogField);
            for(int q = 0; q < values.length; q++)
            {
                int paramNum = ac.getNextParamNum();
                stmt.setObject(paramNum, values[q]);
                ac.addDebugBindValue(values[q]);
            }
        }
    }

    public void apply(ApplyContext ac, DatabaseContext dc, ValueContext vc, PreparedStatement stmt) throws SQLException
    {
        if(paramType != Types.ARRAY)
        {
            int paramNum = ac.getNextParamNum();
            SingleValueSource vs = (SingleValueSource) valueSource;
            String value = vs.getValue(vc);
            // if NULL value and the parameter is allowed to have NULLs
            if ((value == null || value.length() == 0) && allowNull)
            {
                stmt.setNull(paramNum, paramType);
            }
            else
            {
                if(paramType == Types.VARCHAR)
                    stmt.setObject(paramNum, vs.getValue(vc));
                else
                {
                    switch(paramType)
                    {
                        case Types.INTEGER:
                            stmt.setInt(paramNum, vs.getIntValue(vc));
                            break;

                        case Types.DOUBLE:
                            stmt.setDouble(paramNum, vs.getDoubleValue(vc));
                            break;
                    }
                }
            }
        }
        else
        {
            String[] values = ((ListValueSource) valueSource).getValues(vc);
            if (values == null && allowNull)
            {
                stmt.setNull(ac.getNextParamNum(), paramType);
            }
            else
            {
                for(int q = 0; q < values.length; q++)
                {
                    int paramNum = ac.getNextParamNum();
                    stmt.setObject(paramNum, values[q]);
                }
            }
        }
    }

    public void appendDebugHtml(StringBuffer html, ValueContext vc)
    {
        if(paramType != Types.ARRAY)
        {
            SingleValueSource vs = (SingleValueSource) valueSource;
            html.append("<li><code><b>");
            html.append(vs.getId());
            html.append("</b> = ");
            html.append(vs.getValue(vc));
            html.append("</code> (");
            html.append(StatementManager.getTypeNameForId(paramType));
            html.append(")</li>");
        }
        else
        {
            ListValueSource vs = (ListValueSource) valueSource;
            html.append("<li><code><b>");
            html.append(vs.getId());
            html.append("</b> = ");

            String[] values = vs.getValues(vc);
            if(values != null)
            {
                for(int v = 0; v < values.length; v++)
                {
                    if(v > 0)
                        html.append(", ");
                    html.append("'" + values[v] + "'");
                }
            }
            else
            {
                html.append("null");
            }

            html.append(" (list)</code></li>");
        }
    }

    public void appendExceptionText(StringBuffer text, ValueContext vc)
    {
        if(paramType != Types.ARRAY)
        {
            SingleValueSource vs = (SingleValueSource) valueSource;
            text.append(vs.getId());
            text.append(" = ");
            text.append(vs.getValue(vc));
            text.append(" (");
            text.append(StatementManager.getTypeNameForId(paramType));
            text.append(")\n");
        }
        else
        {
            ListValueSource vs = (ListValueSource) valueSource;
            text.append(vs.getId());
            text.append(" = ");

            String[] values = vs.getValues(vc);
            if(values != null)
            {
                for(int v = 0; v < values.length; v++)
                {
                    if(v > 0)
                        text.append(", ");
                    text.append("'" + values[v] + "'");
                }
            }
            else
            {
                text.append("null");
            }

            text.append(" (list)\n");
        }
    }
}

