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
 * $Id: StatementParameter.java,v 1.1 2002-01-20 14:53:17 snshah Exp $
 */

package com.netspective.sparx.xaf.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Element;

import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class StatementParameter
{
    static class ApplyContext
    {
        private StatementInfo stmtInfo;
        private int activeParamNum;

        public ApplyContext(StatementInfo stmtInfo)
        {
            this.stmtInfo = stmtInfo;
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
    }

    private Object valueSource;
    private int paramType;

    public StatementParameter(StatementInfo statement, int paramNum, Element paramElem)
    {
        String valueSrcId = paramElem.getAttribute("value");
        if(valueSrcId.length() > 0)
        {
            valueSource = ValueSourceFactory.getSingleOrStaticValueSource(valueSrcId);
            String paramTypeName = paramElem.getAttribute("type");
            if(paramTypeName.length() > 0)
            {
                Integer typeNum = (Integer) StatementManager.SQL_TYPES_MAP.get(paramTypeName);
                if(typeNum == null)
                    throw new RuntimeException("param type '" + paramTypeName + "' is invalid for statement '" + statement.getId() + "'");
                paramType = typeNum.intValue();
            }
            else
            {
                paramType = Types.VARCHAR;
            }
        }
        else
        {
            valueSource = ValueSourceFactory.getListValueSource(paramElem.getAttribute("values"));
            paramType = Types.ARRAY;
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

    public void apply(ApplyContext ac, DatabaseContext dc, ValueContext vc, PreparedStatement stmt) throws SQLException
    {
        if(paramType != Types.ARRAY)
        {
            int paramNum = ac.getNextParamNum();
            SingleValueSource vs = (SingleValueSource) valueSource;
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
        else
        {
            String[] values = ((ListValueSource) valueSource).getValues(vc);
            for(int q = 0; q < values.length; q++)
            {
                int paramNum = ac.getNextParamNum();
                stmt.setObject(paramNum, values[q]);
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
}

