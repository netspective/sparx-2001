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
 * $Id: SingleValueSource.java,v 1.1 2002-01-20 14:53:21 snshah Exp $
 */

package com.netspective.sparx.util.value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A SingleValueSource (SVS) is an object that returns a single value from a particular source
 * (like a request parameter, text field, or session attribute). This object is intended to be
 * initialized the initializeSource(String) method. The idea is that a single instance with a
 * particular URL-style parameter string will be provided and then whenever the value is needed,
 * a ValueContext will be provided to allow either static content or dynamic content to be served.
 */

public interface SingleValueSource
{
    public static class Documentation
    {
        private String description;
        private List srcParamsFmts = new ArrayList();

        public Documentation(String descr, String srcParamsFmt)
        {
            this.description = descr;
            addSrcParamsFmt(srcParamsFmt);
        }

        public Documentation(String descr, String[] srcParamsFmts)
        {
            this.description = descr;
            for(int i = 0; i < srcParamsFmts.length; i++)
                addSrcParamsFmt(srcParamsFmts[i]);
        }

        public String getParamsHtml(String valueSrcId)
        {
            StringBuffer fmts = new StringBuffer();
            for(int i = 0; i < srcParamsFmts.size(); i++)
            {
                if(i > 0) fmts.append("<br/>");
                fmts.append("<nobr><code>" + valueSrcId + ":" + (String) srcParamsFmts.get(i) + "</code></nobr>");
            }
            return fmts.toString();
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public List getSrcParamsFmt()
        {
            return srcParamsFmts;
        }

        public void addSrcParamsFmt(String srcParamsFmt)
        {
            if(srcParamsFmt.equals("*"))
                this.srcParamsFmts.add("<u>*</u>");
            else
                this.srcParamsFmts.add(srcParamsFmt);
        }
    }

    static public final int RESULTSET_STORETYPE_SINGLECOLUMN = 0;
    static public final int RESULTSET_STORETYPE_SINGLEROWMAP = 1;
    static public final int RESULTSET_STORETYPE_SINGLEROWFORMFLD = 2;
    static public final int RESULTSET_STORETYPE_MULTIROWMAP = 3;
    static public final int RESULTSET_STORETYPE_SINGLEROWARRAY = 4;
    static public final int RESULTSET_STORETYPE_MULTIROWMATRIX = 5;
    static public final int RESULTSET_STORETYPE_RESULTSET = 6;

    /**
     * String representations of the RESULTSET_STORETYPE_* contants.
     */
    static public final String[] RESULTSET_STORETYPES =
            {
                "single-column",
                "row-map",
                "row-fields",
                "rows-map",
                "row-array",
                "rows-matrix",
                "result-set",
            };

    /**
     * Returns the unique identifier for this single value source.
     */
    public String getId();

    /**
     * Returns the documentation for this single value source.
     */
    public Documentation getDocumentation();

    /**
     * Given a parameter string similar to a URL parameter, this method initializes a specific
     * instance of a single value source.
     */
    public void initializeSource(String srcParams);

    /**
     * Returns the value of this SVS as a String based on the environment contained in the ValueContext.
     */
    public String getValue(ValueContext vc);

    /**
     * Returns the value of this SVS as an object based on the environment contained in the ValueContext.
     */
    public Object getObjectValue(ValueContext vc);

    /**
     * Returns the value of this SVS as an integer based on the environment contained in the ValueContext.
     */
    public int getIntValue(ValueContext vc);

    /**
     * Returns the value of this SVS as a double based on the environment contained in the ValueContext.
     */
    public double getDoubleValue(ValueContext vc);

    /**
     * Returns the value of this SVS as a String based on the environment contained in the ValueContext;
     * if the value is null, then returns a blank string.
     */
    public String getValueOrBlank(ValueContext vc);

    /**
     * Returns true if this SVS can support the setting as well as the getting of values.
     */
    public boolean supportsSetValue();

    /**
     * Set the value of this SVS to the value provided.
     */
    public void setValue(ValueContext vc, Object value);

    /**
     * Set the value of this SVS based on the contents of the ResultSet and storeType, which
     * is one of RESULTSET_STORETYPE_* contants.
     */
    public void setValue(ValueContext vc, ResultSet rs, int storeType) throws SQLException;

    /**
     * Set the value of this SVS based on the contents of the ResultSet and storeType, which
     * is one of RESULTSET_STORETYPE_* contants.
     */
    public void setValue(ValueContext vc, ResultSetMetaData rsmd, Object[][] data, int storeType) throws SQLException;

    /**
     * Set the value of this SVS to the value provided.
     */
    public void setValue(ValueContext vc, String value);
}