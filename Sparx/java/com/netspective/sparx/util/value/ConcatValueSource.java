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
 * $Id: ConcatValueSource.java,v 1.2 2002-02-09 13:02:12 snshah Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ConcatValueSource implements SingleValueSource
{
    static public final String BLANK_STRING = "";
    private SingleValueSource valueSource;
    private String prependValue;
    private String appendValue;

    public ConcatValueSource(SingleValueSource vs, String prepend, String append)
    {
        valueSource = vs;
        prependValue = prepend;
        appendValue = append;
    }

    public String getId()
    {
        return getClass().getName() + ":" + prependValue + ":" + valueSource.getId() + ":" + appendValue;
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return null;
    }

    public void initializeSource(String srcParams)
    {
        throw new RuntimeException("ConcatValueSource does not allow initializeSource: " + srcParams);
    }

    public String getValue(ValueContext vc)
    {
        if(prependValue == null && appendValue == null)
            return valueSource.getValue(vc);

        StringBuffer value = new StringBuffer(valueSource.getValue(vc));
        if(prependValue != null)
            value.insert(0, prependValue);
        if(appendValue != null)
            value.append(appendValue);

        return value.toString();
    }

    public Object getObjectValue(ValueContext vc)
    {
        return getValue(vc);
    }

    public int getIntValue(ValueContext vc)
    {
        return Integer.parseInt(getValue(vc));
    }

    public double getDoubleValue(ValueContext vc)
    {
        return Double.parseDouble(getValue(vc));
    }

    public String getValueOrBlank(ValueContext vc)
    {
        String value = getValue(vc);
        return value == null ? BLANK_STRING : value;
    }

    public boolean supportsSetValue()
    {
        return false;
    }

    public void setValue(ValueContext vc, Object value)
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, Object)");
    }

    public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, ResultSet, int)");
    }

    public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, ResultSetMetaData, Object[][], int)");
    }

    public void setValue(ValueContext vc, String value)
    {
        throw new RuntimeException("Class " + this.getClass().getName() + " does not support setValue(ValueContext, String)");
    }
}