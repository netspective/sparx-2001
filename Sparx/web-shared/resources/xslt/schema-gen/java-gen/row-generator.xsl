<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="schema-class-name"/>
<xsl:param name="package-name"/>
<xsl:param name="row-name"/>
<xsl:variable name="default-text-size">32</xsl:variable>

<xsl:template match="table">
<xsl:variable name="_gen-table-class-name"><xsl:value-of select="@_gen-table-class-name"/></xsl:variable>
<xsl:variable name="_gen-table-method-name"><xsl:value-of select="@_gen-table-method-name"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.text.ParseException;
import javax.naming.NamingException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xif.dal.validation.result.*;
import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.sql.*;
import com.netspective.sparx.util.value.*;

/**
 * Represents a single row of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table.
<xsl:if test="@is-enum = 'yes'"> * &lt;p&gt;Because this interface defines the
 * row of a table that is an Enumeration tabletype, it is recommended that this
 * interface &lt;b&gt;not&lt;/b&gt; be used for simple reads from the database.
 * Instead, one should use the <xsl:value-of select="@_gen-table-class-name"/>.EnumeratedItem class
 * so that a join in the database will not be required for static data.</xsl:if>
 * &lt;p&gt;
 * Some relevant facts about this class:
 * &lt;ul&gt;
 *   &lt;li&gt; It represents the data that can be stored and retrieved from the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table in the database.
 <xsl:if test="@parent"> * &lt;li&gt; The row is a child of the &lt;code&gt;<xsl:value-of select="@parent"/>&lt;/code&gt; table in the database.
 </xsl:if> *   &lt;li&gt; It has <xsl:value-of select="count(column)"/> columns, all of which have getter and setter methods.
<xsl:if	test="column[@_gen-ref-table-is-enum = 'yes']"> *   &lt;li&gt; It has <xsl:value-of select="count(column[@_gen-ref-table-is-enum = 'yes'])"/> columns that reference Enumeration tables, all of which have special
 *              getter and setter methods to help prevent database joins for obtaining simple lookup values:
 *              &lt;ol&gt;
<xsl:for-each select="column[@_gen-ref-table-is-enum = 'yes']"> *                &lt;li&gt; &lt;code&gt;get<xsl:value-of select="@_gen-method-name"/>Enum()&lt;/code&gt; and &lt;code&gt;set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem)&lt;/code&gt;
</xsl:for-each> *              &lt;/ol&gt;
</xsl:if><xsl:if	test="child-table"> *   &lt;li&gt; It has children which can be retrieved using:
 *              &lt;ol&gt;
<xsl:for-each select="child-table"> *                &lt;li&gt; &lt;code&gt;get<xsl:value-of select="@_gen-rows-name"/>()&lt;/code&gt; (join condition is &lt;code&gt;parent.<xsl:value-of select="@parent-col"/> = this.<xsl:value-of select="@child-col"/>&lt;/code&gt;)
</xsl:for-each> *              &lt;/ol&gt;
</xsl:if> * &lt;/ul&gt;
 * &lt;/p&gt;
 */
public class <xsl:value-of select="$row-name"/> extends AbstractRow implements <xsl:value-of select="@_gen-domain-class-name"/>
{
<xsl:for-each select="column">	/** The name of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column in the database **/
	public static final String COLNAME_<xsl:value-of select="@_gen-constant-name"/> = &quot;<xsl:value-of select="@name"/>&quot;;
</xsl:for-each>

<xsl:for-each select="column">	/** The name of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column suitable for use as a dialog field name **/
    public static final String DLGFIELDNAME_<xsl:value-of select="@_gen-constant-name"/> = &quot;<xsl:value-of select="@name"/>&quot;;
</xsl:for-each>

<xsl:for-each select="column">	/** The name of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column suitable for use as an XML node/element **/
	public static final String NODENAME_<xsl:value-of select="@_gen-constant-name"/> = &quot;<xsl:value-of select="@_gen-node-name"/>&quot;;
</xsl:for-each>

<xsl:for-each select="column">	/** The index of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column in a ResultSet when all columns are selected from a table (1-based) **/
	public static final int COLRSI_<xsl:value-of select="@_gen-constant-name"/> = <xsl:value-of select="position()"/>;
</xsl:for-each>

<xsl:for-each select="column">	/** The index of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column in an array when all columns are selected from a table (0-based) **/
	public static final int COLAI_<xsl:value-of select="@_gen-constant-name"/> = <xsl:value-of select="position()-1"/>;
</xsl:for-each>

<xsl:for-each select="column">	/** The &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column data **/
<xsl:choose>
    <xsl:when test="java-default">    protected <xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/><xsl:value-of select="' '"/><xsl:value-of select="@_gen-member-name"/> = <xsl:value-of select="java-default"/>;
    </xsl:when>
    <xsl:otherwise>    protected <xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/><xsl:value-of select="' '"/><xsl:value-of select="@_gen-member-name"/>;
    </xsl:otherwise>
</xsl:choose>
</xsl:for-each>

<xsl:for-each select="child-table">	/** The children rows from <xsl:value-of select="@name"/> connected to this row by parent.<xsl:value-of select="@parent-col"/> = this.<xsl:value-of select="@child-col"/> (null if children not retrieved) **/
	protected <xsl:value-of select="@_gen-rows-class-name"/><xsl:value-of select="' '"/><xsl:value-of select="@_gen-rows-member-name"/>;
</xsl:for-each>

	public <xsl:value-of select="$row-name"/>(<xsl:value-of select="@_gen-table-class-name"/> table)
	{
		super(table);
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="method-name"><xsl:value-of select="@_gen-method-name"/></xsl:variable>
<!-- DBMS-Specific SQL -->
<xsl:for-each select="default">
	<xsl:text>		</xsl:text>setCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, &quot;<xsl:value-of select="@dbms"/>&quot;, table.get<xsl:value-of select="$method-name"/>Column().getDefaultSqlExprValue(&quot;<xsl:value-of select="@dbms"/>&quot;));
</xsl:for-each>
</xsl:for-each>
	}

<xsl:if test="@_gen-table-orig-class-name">
	public <xsl:value-of select="$row-name"/>(<xsl:value-of select="@_gen-table-orig-class-name"/> table)
	{
		super(table);
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="method-name"><xsl:value-of select="@_gen-method-name"/></xsl:variable>
<!-- DBMS-Specific Defaults -->
<xsl:for-each select="default">
	<xsl:text>		</xsl:text>setCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, &quot;<xsl:value-of select="@dbms"/>&quot;, table.get<xsl:value-of select="$method-name"/>Column().getDefaultSqlExprValue(&quot;<xsl:value-of select="@dbms"/>&quot;));
</xsl:for-each>
</xsl:for-each>
	}
</xsl:if>
<xsl:if test="column[@primarykey='yes']">
	/**
	 * Return the value of the <xsl:value-of select="column[@primarykey='yes']/@name"/> column
	 **/
	public Object getActivePrimaryKeyValue()
	{
		return get<xsl:value-of select="column[@primarykey='yes']/@_gen-method-name"/>();
	}
</xsl:if>

	public Object[] getData()
	{
		return new Object[] {
<xsl:for-each select="column"><xsl:text>			</xsl:text><xsl:value-of select="@_gen-member-name"/><xsl:if test="position() != last()"><xsl:text>,
</xsl:text></xsl:if>
</xsl:for-each>
		};
	}

    public Object getDataByColumn(Column column)
    {
        int columnIndex = column.getIndexInRow();
        switch(columnIndex)
        {
<xsl:for-each select="column">
            case COLAI_<xsl:value-of select="@_gen-constant-name"/>:
                return get<xsl:value-of select="@_gen-method-name"/>();
</xsl:for-each>

            default:
                throw new IndexOutOfBoundsException("Column index "+ columnIndex +" does not exist.");
        }
    }

    public void setDataByColumn(Column column, Object value)
    {
        int columnIndex = column.getIndexInRow();
        switch(columnIndex)
        {
    <xsl:for-each select="column">
        <xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
            case COLAI_<xsl:value-of select="@_gen-constant-name"/>:
                set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="$java-class-spec"/>) value);
                return;
    </xsl:for-each>
        }
    }

    public void setSqlExprByColumn(Column column, String sqlExpr, String dbms)
    {
        int columnIndex = column.getIndexInRow();
        switch(columnIndex)
        {
    <xsl:for-each select="column">
            case COLAI_<xsl:value-of select="@_gen-constant-name"/>:
                set<xsl:value-of select="@_gen-method-name"/>SqlExpr(sqlExpr, dbms);
                return;
    </xsl:for-each>
        }
    }

    public void setTextByColumn(Column column, String text, boolean append) throws ParseException
    {
        int columnIndex = column.getIndexInRow();
        switch(columnIndex)
        {
    <xsl:for-each select="column">
        <xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
            case COLAI_<xsl:value-of select="@_gen-constant-name"/>:
    <xsl:choose>
    <xsl:when test="$java-class-spec = 'java.lang.String'">
                if(append) { String old = get<xsl:value-of select="@_gen-method-name"/>(); if (old != null) text = old + text; }
                set<xsl:value-of select="@_gen-method-name"/>(text);
                return;
    </xsl:when>
    <xsl:when test="@_gen-ref-table-is-enum = 'yes'">
                {
                    <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem enum = <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem.parseItem(text);
                    if(enum != null)
                    {
                        set<xsl:value-of select="@_gen-method-name"/>(enum.getId());
                        return;
                    }
                    else
                        throw new ParseException("<xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem was unable to parse '"+ text +"'", 0);
                }
    </xsl:when>
    <xsl:otherwise>
                set<xsl:value-of select="@_gen-method-name"/>(((<xsl:value-of select="@_gen-data-type-class"/>) column).parse(text));
                return;
    </xsl:otherwise>
    </xsl:choose>
    </xsl:for-each>
        }
    }

	/**
	 * Return true if the row is an instance of the <xsl:value-of select="$row-name"/> class
	 * and each of the columns in the row parameter matches the data contained in this
	 * row. The values of two columns are considered equal when either the object is the
	 * same, both are null, or the equals() method for the two objects when compared with
	 * other returns true.
	 **/
	public boolean equals(Object row)
	{
		if(this == row) return true;
		if(row == null) return false;
		if(! (row instanceof <xsl:value-of select="$row-name"/>)) return false;

		<xsl:value-of select="$row-name"/> compareRow = (<xsl:value-of select="$row-name"/>) row;
<xsl:for-each select="column">		if(! valuesAreEqual(get<xsl:value-of select="@_gen-method-name"/>(), compareRow.get<xsl:value-of select="@_gen-method-name"/>())) return false;
</xsl:for-each>
		return true;
	}

	public List getDataForDmlStatement(DatabasePolicy dbPolicy)
	{
		List data = new ArrayList();
		String dbms = dbPolicy.getDBMSName();

		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column">
<xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:choose>
	<xsl:when test="(@allow-sql-expr = 'no' or allow-sql-expr = 'no')">
		<xsl:text>		</xsl:text>data.add(table.get<xsl:value-of select="@_gen-method-name"/>Column().getValueForSqlBindParam(<xsl:value-of select="@_gen-member-name"/>));
	</xsl:when>
	<xsl:when test="default">
		<xsl:text>		</xsl:text>data.add(haveSqlExprData (dbms, COLAI_<xsl:value-of select="$constant-name"/>) ? ((Object) new DmlStatement.CustomSql(dbms, getCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, dbms))) : table.get<xsl:value-of select="@_gen-method-name"/>Column().getValueForSqlBindParam(<xsl:value-of select="@_gen-member-name"/>));
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>		</xsl:text>data.add(haveSqlExprData (dbms, COLAI_<xsl:value-of select="$constant-name"/>) ? ((Object) new DmlStatement.CustomSql(getCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, dbms))) : table.get<xsl:value-of select="@_gen-method-name"/>Column().getValueForSqlBindParam(<xsl:value-of select="@_gen-member-name"/>));
	</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
		return data;
	}

	public RowValidationResult getValidationResult()
	{
		boolean status = true;
		BasicRowValidationResult brvResult = new BasicRowValidationResult();

		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
		{
			BasicDataValidationResult bdvResult = (BasicDataValidationResult) table.get<xsl:value-of select="@_gen-method-name"/>Column().getValidationResult(this.<xsl:value-of select="@_gen-member-name"/>);
			if (null == bdvResult) throw new NullPointerException ("WTH?");

			brvResult.addDataValidationResult(bdvResult);
		}
</xsl:for-each>

		return brvResult;
	}

    public RowValidationResult getInsertValidationResult()
    {
        boolean status = true;
        BasicRowValidationResult brvResult = new BasicRowValidationResult();

        <xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) table.get<xsl:value-of select="@_gen-method-name"/>Column().getInsertValidationResult(this.<xsl:value-of select="@_gen-member-name"/>);
            if (null == bdvResult) throw new NullPointerException ("WTH?");

            brvResult.addDataValidationResult(bdvResult);
        }
</xsl:for-each>

        return brvResult;
    }

    public RowValidationResult getUpdateValidationResult()
    {
        boolean status = true;
        BasicRowValidationResult brvResult = new BasicRowValidationResult();

        <xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) table.get<xsl:value-of select="@_gen-method-name"/>Column().getUpdateValidationResult(this.<xsl:value-of select="@_gen-member-name"/>);
            if (null == bdvResult) throw new NullPointerException ("WTH?");

            brvResult.addDataValidationResult(bdvResult);
        }
</xsl:for-each>

        return brvResult;
    }

    public RowValidationResult getDeleteValidationResult()
    {
        boolean status = true;
        BasicRowValidationResult brvResult = new BasicRowValidationResult();

        <xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) table.get<xsl:value-of select="@_gen-method-name"/>Column().getDeleteValidationResult(this.<xsl:value-of select="@_gen-member-name"/>);
            if (null == bdvResult) throw new NullPointerException ("WTH?");

            brvResult.addDataValidationResult(bdvResult);
        }
</xsl:for-each>

        return brvResult;
    }

	public RowValidationResult getValidationResult(Writer writer) throws IOException
	{
		BasicRowValidationResult brvResult = new BasicRowValidationResult();
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		boolean status = true;

<xsl:for-each select="column"><xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
		{
			BasicDataValidationResult bdvResult = (BasicDataValidationResult) table.get<xsl:value-of select="@_gen-method-name"/>Column().getValidationResult(this.<xsl:value-of select="@_gen-member-name"/>);
			if (null == bdvResult) throw new NullPointerException ("WTH?");

			writer.write ("BDV Result: " + bdvResult.toString() + "&lt;br&gt;");
			writer.flush();

			brvResult.addDataValidationResult(bdvResult);

			writer.write ("BRV Result: " + brvResult.toString() + "&lt;br&gt;");
			writer.flush();

		}
</xsl:for-each>

		return brvResult;
	}

	public void populateDataByIndexes(ResultSet rs) throws SQLException
	{
	<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="result-set-index">COLRSI_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:choose>
	<xsl:when test="java-type">
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:value-of select="' '"/><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="$result-set-index"/>));
<xsl:text>		</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.wasNull() ? null : <xsl:value-of select="@_gen-member-name"/>Value);
	</xsl:when>
	<xsl:when test="$java-class-spec = 'java.lang.String' ">
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.getString(<xsl:value-of select="$result-set-index"/>));
	</xsl:when>
	<xsl:otherwise>
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="$java-class-spec"/>) rs.getObject(<xsl:value-of select="$result-set-index"/>));
	</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
	}

	public void populateDataByNames(ResultSet rs, Map colNameIndexMap) throws SQLException
	{
		if(colNameIndexMap == null) colNameIndexMap = getColumnNamesIndexMap(rs);
		Integer colIndex = null;
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name">COLNAME_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
		colIndex = (Integer) colNameIndexMap.get(<xsl:value-of select="$constant-name"/>);
<xsl:text>		</xsl:text>if(colIndex != null) {<xsl:text>
		</xsl:text>
<xsl:choose>
	<xsl:when test="java-type">
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:value-of select="' '"/><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(colIndex.intValue()));
<xsl:text>			</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.wasNull() ? null : <xsl:value-of select="@_gen-member-name"/>Value);
	</xsl:when>
	<xsl:when test="$java-class-spec = 'java.lang.String' ">
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.getString(colIndex.intValue()));
	</xsl:when>
	<xsl:otherwise>
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="$java-class-spec"/>) rs.getObject(colIndex.intValue()));
	</xsl:otherwise>
</xsl:choose>	}
</xsl:for-each>
	}

	public void setData(DialogContext dc)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		<xsl:choose><xsl:when test="type = 'enum'">dc.setValueAsTextSet</xsl:when><xsl:otherwise>dc.setValue</xsl:otherwise></xsl:choose>(DLGFIELDNAME_<xsl:value-of select="@_gen-constant-name"/>, <xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">get<xsl:value-of select="@_gen-method-name"/>()</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().format(dc, get<xsl:value-of select="@_gen-method-name"/>())</xsl:otherwise></xsl:choose>);
</xsl:for-each>	}

	public void setData(DialogContext dc, Map colNameFieldNameMap)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		String fieldName = null;
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		fieldName = (String) colNameFieldNameMap.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		dc.setValue(fieldName != null ? fieldName : DLGFIELDNAME_<xsl:value-of select="@_gen-constant-name"/>, <xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">get<xsl:value-of select="@_gen-method-name"/>()</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().format(dc, get<xsl:value-of select="@_gen-method-name"/>())</xsl:otherwise></xsl:choose>);
</xsl:for-each>	}

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'before' and @event='insert']">
    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        if(! super.beforeInsert(cc, dml))
            return false;
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">before</xsl:with-param>
            <xsl:with-param name="event">insert</xsl:with-param>
        </xsl:call-template>
        return true;
    }
</xsl:if>

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'after' and @event='insert']">
    public void afterInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">after</xsl:with-param>
            <xsl:with-param name="event">insert</xsl:with-param>
        </xsl:call-template>
        super.afterInsert(cc, dml);
    }
</xsl:if>

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'before' and @event='update']">
    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        if(! super.beforeUpdate(cc, dml))
            return false;
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">before</xsl:with-param>
            <xsl:with-param name="event">update</xsl:with-param>
        </xsl:call-template>
        return true;
    }
</xsl:if>

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'after' and @event='update']">
    public void afterUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">after</xsl:with-param>
            <xsl:with-param name="event">update</xsl:with-param>
        </xsl:call-template>
        super.afterUpdate(cc, dml);
    }
</xsl:if>

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'before' and @event='delete']">
    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        if(! super.beforeDelete(cc, dml))
            return false;
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">before</xsl:with-param>
            <xsl:with-param name="event">delete</xsl:with-param>
        </xsl:call-template>
        return true;
    }
</xsl:if>

<xsl:if test="column/trigger[@type='dal-row-java' and @time = 'after' and @event='delete']">
    public void afterDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
    {
        <xsl:call-template name="row-trigger-code">
            <xsl:with-param name="time">after</xsl:with-param>
            <xsl:with-param name="event">delete</xsl:with-param>
        </xsl:call-template>
        super.afterDelete(cc, dml);
    }
</xsl:if>

<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="array-index">COLAI_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type">
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="java-type"/> defaultValue) { return <xsl:value-of select="$member-name"/> != null ? <xsl:value-of select="$member-name"/>.<xsl:value-of select="java-type"/>Value() : defaultValue; }
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>() { return <xsl:value-of select="$member-name"/>.<xsl:value-of select="java-type"/>Value(); }
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="java-type"/> value) { set<xsl:value-of select="@_gen-method-name"/>(new <xsl:value-of select="$java-class-spec"/>(value)); }
</xsl:if>
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> defaultValue) { return <xsl:value-of select="$member-name"/> != null ? <xsl:value-of select="$member-name"/> : defaultValue; }
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>() { return <xsl:value-of select="$member-name"/>; }
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> value) { <xsl:value-of select="$member-name"/> = value; haveSqlExprData[<xsl:value-of select="$array-index"/>] = false; }
<xsl:if test="@type = 'autoinc' or @_gen-create-id = 'autoinc' or @type = 'longint' or @type = 'guid32' or @_gen-create-id = 'guid32' or @type = 'guidtext'">
	// For Autoinc/Guid32 Transparency
	public Object get<xsl:value-of select="@_gen-method-name"/>Obj() { return (null != <xsl:value-of select="$member-name"/>) ? <xsl:value-of select="$member-name"/> : null; }
	public Object get<xsl:value-of select="@_gen-method-name"/>Obj(<xsl:value-of select="$java-class-spec"/> defaultValue) { return <xsl:value-of select="$member-name"/> != null ? <xsl:value-of select="$member-name"/> : defaultValue; }
</xsl:if><xsl:if test="@type = 'autoinc' or @_gen-create-id = 'autoinc' or @type = 'longint' or @type = 'guid32' or @_gen-create-id = 'guid32' or @type = 'guidtext'">
	public void set<xsl:value-of select="@_gen-method-name"/>(Object value)	{ <xsl:value-of select="$member-name"/> = (<xsl:value-of select="$java-class-spec"/>) value; haveSqlExprData[<xsl:value-of select="$array-index"/>] = false; }
	public Object get<xsl:value-of select="@_gen-method-name"/>(Object defaultValue) { return (null != <xsl:value-of select="$member-name"/>) ? <xsl:value-of select="$member-name"/> : defaultValue; }
</xsl:if>
	/** <xsl:value-of select="@descr"/> **/
	public void set<xsl:value-of select="@_gen-method-name"/>SqlExpr(String value) { setCustomSqlExpr(<xsl:value-of select="$array-index"/>, DEFAULT_DBMS, value); }
	public void set<xsl:value-of select="@_gen-method-name"/>SqlExpr(String dbms, String value) { setCustomSqlExpr(<xsl:value-of select="$array-index"/>, dbms, value); }
<xsl:if	test="@_gen-ref-table-is-enum = 'yes'">
	/** <xsl:value-of select="@descr"/> **/
	public <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem get<xsl:value-of select="@_gen-method-name"/>Enum() { return <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem.getItemById(get<xsl:value-of select="@_gen-method-name"/>()); }
	/** <xsl:value-of select="@descr"/> **/
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem value) { set<xsl:value-of select="@_gen-method-name"/>(value != null ? value.getIdAsInteger() : null); }
</xsl:if>
</xsl:for-each>
<xsl:if test="child-table[@child-col-_gen-method-name]">
	public boolean isParentRow()
	{
		return true;
	}

	public void retrieveChildren(ConnectionContext cc) throws NamingException, SQLException
	{
<xsl:for-each select="child-table">		get<xsl:value-of select="@_gen-rows-name"/>(cc);
</xsl:for-each>	}
</xsl:if>
<xsl:for-each select="child-table[@child-col-_gen-method-name]"><xsl:variable name="parent-col"><xsl:value-of select="@parent-col"/></xsl:variable>
	/** Create a <xsl:value-of select="@_gen-row-name"/> and automatically set the <xsl:value-of select="@name"/> <xsl:value-of select="@child-col"/> column to the current value of this table's <xsl:value-of select="@parent-col"/> column **/
	public <xsl:value-of select="@_gen-row-class-name"/><xsl:value-of select="' '"/>create<xsl:value-of select="@_gen-row-name"/>()
	{
		<xsl:value-of select="@_gen-table-class-name"/> table = ((<xsl:value-of select="$schema-class-name"/>) getTable().getParentSchema()).get<xsl:value-of select="@_gen-table-name"/>();
		<xsl:value-of select="@_gen-row-class-name"/> row = table.create<xsl:value-of select="@_gen-row-name"/>();
		row.set<xsl:value-of select="@child-col-_gen-method-name"/>(get<xsl:value-of select="../column[@name = $parent-col]/@_gen-method-name"/>());
		return row;
	}

	/** Returns the children rows from <xsl:value-of select="@name"/> connected to this row by <xsl:value-of select="@parent-col"/> = <xsl:value-of select="@child-col"/> that was retrieved by retrieveChildren() or get<xsl:value-of select="@_gen-rows-name"/>(ConnectionContext) (null otherwise) **/
	public <xsl:value-of select="@_gen-rows-class-name"/><xsl:value-of select="' '"/>get<xsl:value-of select="@_gen-rows-name"/>() throws NamingException, SQLException
	{
		return <xsl:value-of select="@_gen-rows-member-name"/>;
	}

	/** Executes the SQL necessary to retrieve the children rows from <xsl:value-of select="@name"/> connected to this row by <xsl:value-of select="@parent-col"/> = <xsl:value-of select="@child-col"/> and saves the data into a member variable that can be retrieved by calling get<xsl:value-of select="@_gen-rows-name"/>() **/
	public <xsl:value-of select="@_gen-rows-class-name"/><xsl:value-of select="' '"/>get<xsl:value-of select="@_gen-rows-name"/>(ConnectionContext cc) throws NamingException, SQLException
	{
		<xsl:value-of select="@_gen-table-class-name"/> table = ((<xsl:value-of select="$schema-class-name"/>) getTable().getParentSchema()).get<xsl:value-of select="@_gen-table-name"/>();
		<xsl:value-of select="@_gen-rows-member-name"/> = table.get<xsl:value-of select="@_gen-rows-name"/>By<xsl:value-of select="@child-col-_gen-method-name"/>(cc, get<xsl:value-of select="../column[@name = $parent-col]/@_gen-method-name"/>());
		return <xsl:value-of select="@_gen-rows-member-name"/>;
	}

    /** Executes the SQL necessary to remove the children rows from <xsl:value-of select="@name"/> connected to this row by <xsl:value-of select="@parent-col"/> = <xsl:value-of select="@child-col"/>.
        Note that this performs a fast SQL delete which means that DAL-based triggers will NOT be called. **/
    public <xsl:value-of select="@_gen-rows-class-name"/><xsl:value-of select="' '"/>delete<xsl:value-of select="@_gen-rows-name"/>(ConnectionContext cc) throws NamingException, SQLException
    {
        <xsl:value-of select="@_gen-table-class-name"/> table = ((<xsl:value-of select="$schema-class-name"/>) getTable().getParentSchema()).get<xsl:value-of select="@_gen-table-name"/>();
        table.delete<xsl:value-of select="@_gen-rows-name"/>Using<xsl:value-of select="@child-col-_gen-method-name"/>(cc, get<xsl:value-of select="../column[@name = $parent-col]/@_gen-method-name"/>());
        return <xsl:value-of select="@_gen-rows-member-name"/>;
    }
</xsl:for-each>
}
</xsl:template>

<!--
    The following code checks to see if there are any special declarations in table or column triggers and performs the
    declarations. Then, it emits the table trigger code with the 'before-columns' position and then each column's
    triggers, and finally the table trigger code in the 'after-columns' position.
-->
<xsl:template name="row-trigger-code">
    <xsl:param name="time"/>
    <xsl:param name="event"/>

<xsl:if test=".//trigger[@type='dal-row-java' and @time=$time and @event=$event and @use-table-instance='yes']">        <xsl:value-of select="@_gen-table-class-name"/> table = (<xsl:value-of select="@_gen-table-class-name"/>) getTable();
</xsl:if>
<xsl:if test=".//trigger[@type='dal-row-java' and @time=$time and @event=$event and @use-db-policy='yes']">        DatabasePolicy databasePolicy = cc.getDatabasePolicy();
</xsl:if>
<xsl:if test=".//trigger[@type='dal-row-java' and @time=$time and @event=$event and @use-dbms-id='yes']">        String dbmsId = databasePolicy.getDBMSName();
</xsl:if>

<xsl:for-each select="trigger[@type='dal-row-java' and @position='before-columns' and @time=$time and @event=$event]">        <xsl:value-of select="final-code"/>
</xsl:for-each>
<xsl:for-each select="column/trigger[@type='dal-row-java' and @time=$time and @event=$event]">        <xsl:value-of select="final-code"/>
</xsl:for-each>
<xsl:for-each select="trigger[@type='dal-row-java' and (not(@position) or @position='after-columns') and @time=$time and @event=$event]">        <xsl:value-of select="final-code"/>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
