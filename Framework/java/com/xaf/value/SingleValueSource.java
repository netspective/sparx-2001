package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;
import java.sql.*;

import com.xaf.form.*;
import com.xaf.db.*;

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

	static public final int RESULTSET_STORETYPE_SINGLECOLUMN     = 0;
	static public final int RESULTSET_STORETYPE_SINGLEROWMAP     = 1;
	static public final int RESULTSET_STORETYPE_SINGLEROWFORMFLD = 2;
	static public final int RESULTSET_STORETYPE_MULTIROWMAP      = 3;
	static public final int RESULTSET_STORETYPE_SINGLEROWARRAY   = 4;
	static public final int RESULTSET_STORETYPE_MULTIROWMATRIX   = 5;
	static public final int RESULTSET_STORETYPE_RESULTSET        = 6;

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