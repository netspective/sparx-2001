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
	protected <xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>;
</xsl:for-each>

<xsl:for-each select="child-table">	/** The children rows from <xsl:value-of select="@name"/> connected to this row by parent.<xsl:value-of select="@parent-col"/> = this.<xsl:value-of select="@child-col"/> (null if children not retrieved) **/
	protected <xsl:value-of select="@_gen-rows-class-name"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-rows-member-name"/>;
</xsl:for-each>

	public <xsl:value-of select="$row-name"/>(<xsl:value-of select="@_gen-table-class-name"/> table)
	{
		super(table);
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:if test="@default-java or default[@type = 'java']">
	<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/> = table.get<xsl:value-of select="@_gen-method-name"/>Column().getDefaultValue();
</xsl:if>
<xsl:if test="@default or default">
	<xsl:text>		</xsl:text>setCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, table.get<xsl:value-of select="@_gen-method-name"/>Column().getDefaultSqlExprValue());
</xsl:if>
</xsl:for-each>
	}

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

	public List getDataForDmlStatement()
	{
		List data = new ArrayList();
<xsl:for-each select="column">		data.add(haveSqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>] ? ((Object) new DmlStatement.CustomSql(sqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>])) : <xsl:value-of select="@_gen-member-name"/>);
</xsl:for-each>
		return data;
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
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="$result-set-index"/>));
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
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(colIndex.intValue()));
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

	public void populateDataByNames(Element element) throws ParseException, DOMException
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();

		NodeList rowChildren = element.getChildNodes();
		int rowChildrenCount = rowChildren.getLength();
		for(int i = 0; i &lt; rowChildrenCount; i++)
		{
			Node rowChildNode = rowChildren.item(i);
			if(rowChildNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element columnDataElem = (Element) rowChildNode;
			String columnName = columnDataElem.getNodeName();
			String columnValue = columnDataElem.getFirstChild().getNodeValue();
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:choose>
	<xsl:when test="$java-class-spec = 'java.lang.String'">
			if(NODENAME_<xsl:value-of select="@_gen-constant-name"/>.equals(columnName)) {
				set<xsl:value-of select="@_gen-method-name"/>(columnValue);
				break;
			}
	</xsl:when>
	<xsl:otherwise>			if(NODENAME_<xsl:value-of select="@_gen-constant-name"/>.equals(columnName)) {
				set<xsl:value-of select="@_gen-method-name"/>(table.get<xsl:value-of select="@_gen-method-name"/>Column().parse(columnValue));
				break;
			}
	</xsl:otherwise>
</xsl:choose>
</xsl:for-each>		}
	}

	public void populateDataByNames(DialogContext dc)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		Map fieldStates = dc.getFieldStates();
		DialogContext.DialogFieldState state = null;
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		state = (DialogContext.DialogFieldState) fieldStates.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
<xsl:text>      </xsl:text>if(state != null &amp;&amp; state.value != null &amp;&amp; state.value.length() > 0) set<xsl:value-of select="@_gen-method-name"/>(<xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">state.value</xsl:when><xsl:when test="$java-class-spec = 'java.util.Date'">(<xsl:value-of select="$java-class-spec"/>) state.field.getValueForSqlBindParam(state.value)</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().parse(state.value)</xsl:otherwise></xsl:choose>);
</xsl:for-each>
	}

	public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		Map fieldStates = dc.getFieldStates();
		String fieldName = null;
		DialogContext.DialogFieldState state = null;
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		fieldName = (String) colNameFieldNameMap.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		state = (DialogContext.DialogFieldState) fieldStates.get(fieldName != null ? fieldName : COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
<xsl:text>		</xsl:text>if(state != null &amp;&amp; state.value != null &amp;&amp; state.value.length() > 0) set<xsl:value-of select="@_gen-method-name"/>(<xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">state.value</xsl:when><xsl:when test="$java-class-spec = 'java.util.Date'">(<xsl:value-of select="$java-class-spec"/>) state.field.getValueForSqlBindParam(state.value)</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().parse(state.value)</xsl:otherwise></xsl:choose>);
</xsl:for-each>
	}

	public void setData(DialogContext dc)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		dc.setValue(COLNAME_<xsl:value-of select="@_gen-constant-name"/>, <xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">get<xsl:value-of select="@_gen-method-name"/>()</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().format(dc, get<xsl:value-of select="@_gen-method-name"/>())</xsl:otherwise></xsl:choose>);
</xsl:for-each>	}

	public void setData(DialogContext dc, Map colNameFieldNameMap)
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		String fieldName = null;
<xsl:for-each select="column"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>		fieldName = (String) colNameFieldNameMap.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		dc.setValue(fieldName != null ? fieldName : COLNAME_<xsl:value-of select="@_gen-constant-name"/>, <xsl:choose><xsl:when test="$java-class-spec = 'java.lang.String'">get<xsl:value-of select="@_gen-method-name"/>()</xsl:when><xsl:otherwise>table.get<xsl:value-of select="@_gen-method-name"/>Column().format(dc, get<xsl:value-of select="@_gen-method-name"/>())</xsl:otherwise></xsl:choose>);
</xsl:for-each>	}

<xsl:if test="column[@type = 'autoinc']">
	public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
	{
		if(! super.beforeInsert(cc, dml))
			return false;

		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		DatabasePolicy databasePolicy = cc.getDatabasePolicy();
		Object value;

<xsl:for-each select="column[@type = 'autoinc']">
		if (databasePolicy.retainAutoIncColInDml()) {
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:text>		</xsl:text>Column <xsl:value-of select="@_gen-member-name"/>Col = table.get<xsl:value-of select="@_gen-method-name"/>Column();
<xsl:text>		</xsl:text>value = databasePolicy.handleAutoIncPreDmlExecute(cc.getConnection(), <xsl:value-of select="@_gen-member-name"/>Col.getSequenceName(), <xsl:value-of select="@_gen-member-name"/>Col.getName());
<xsl:text>		</xsl:text>dml.updateValue(COLAI_<xsl:value-of select="@_gen-constant-name"/>, value);
<xsl:text>		</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(value instanceof <xsl:value-of select="$java-class-spec"/> ? (<xsl:value-of select="$java-class-spec"/>) value : new <xsl:value-of select="$java-class-spec"/>(value.toString()));
		} else {
			dml.removeColumn("<xsl:value-of select="@name"/>");
			dml.createSql();
		}
</xsl:for-each>
		return true;
	}

	public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
	{
		if(! super.beforeUpdate(cc, dml))
			return false;

		DatabasePolicy databasePolicy = cc.getDatabasePolicy();

<xsl:for-each select="column[@type = 'autoinc']">
		if (!databasePolicy.retainAutoIncColInDml()) {
			dml.removeColumn("<xsl:value-of select="@name"/>");
			dml.createSql();
		}
</xsl:for-each>
		return true;
	}

	public void afterInsert(ConnectionContext cc) throws NamingException, SQLException
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		DatabasePolicy databasePolicy = cc.getDatabasePolicy();
		Object value;
    String seqOrTableName = "";
<xsl:for-each select="column[@type = 'autoinc']">
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:text>	</xsl:text>Column <xsl:value-of select="@_gen-member-name"/>Col = table.get<xsl:value-of select="@_gen-method-name"/>Column();
		if (databasePolicy.retainAutoIncColInDml()) {
				seqOrTableName = <xsl:value-of select="@_gen-member-name"/>Col.getSequenceName();
		} else {
				seqOrTableName = table.getName();
		}
<xsl:text>	</xsl:text>value = databasePolicy.handleAutoIncPostDmlExecute(cc.getConnection(), seqOrTableName, <xsl:value-of select="@_gen-member-name"/>Col.getName(), get<xsl:value-of select="@_gen-method-name"/>());
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(value instanceof <xsl:value-of select="$java-class-spec"/> ? (<xsl:value-of select="$java-class-spec"/>) value : new <xsl:value-of select="$java-class-spec"/>(value.toString()));
</xsl:for-each>

		super.afterInsert(cc);
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
	/** <xsl:value-of select="@descr"/> **/
	public void set<xsl:value-of select="@_gen-method-name"/>SqlExpr(String value) { setCustomSqlExpr(<xsl:value-of select="$array-index"/>, value); }
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
	public <xsl:value-of select="@_gen-row-class-name"/><xsl:text> </xsl:text>create<xsl:value-of select="@_gen-row-name"/>()
	{
		<xsl:value-of select="@_gen-table-class-name"/> table = ((<xsl:value-of select="$schema-class-name"/>) getTable().getParentSchema()).get<xsl:value-of select="@_gen-table-name"/>();
		<xsl:value-of select="@_gen-row-class-name"/> row = table.create<xsl:value-of select="@_gen-row-name"/>();
		row.set<xsl:value-of select="@child-col-_gen-method-name"/>(get<xsl:value-of select="../column[@name = $parent-col]/@_gen-method-name"/>());
		return row;
	}

	/** Returns the children rows from <xsl:value-of select="@name"/> connected to this row by <xsl:value-of select="@parent-col"/> = <xsl:value-of select="@child-col"/> that was retrieved by retrieveChildren() or get<xsl:value-of select="@_gen-rows-name"/>(ConnectionContext) (null otherwise) **/
	public <xsl:value-of select="@_gen-rows-class-name"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-rows-name"/>() throws NamingException, SQLException
	{
		return <xsl:value-of select="@_gen-rows-member-name"/>;
	}

	/** Executes the SQL necessary to retrieve the children rows from <xsl:value-of select="@name"/> connected to this row by <xsl:value-of select="@parent-col"/> = <xsl:value-of select="@child-col"/> and saves the data into a member variable that can be retrieved by calling get<xsl:value-of select="@_gen-rows-name"/>() **/
	public <xsl:value-of select="@_gen-rows-class-name"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-rows-name"/>(ConnectionContext cc) throws NamingException, SQLException
	{
		<xsl:value-of select="@_gen-table-class-name"/> table = ((<xsl:value-of select="$schema-class-name"/>) getTable().getParentSchema()).get<xsl:value-of select="@_gen-table-name"/>();
		<xsl:value-of select="@_gen-rows-member-name"/> = table.get<xsl:value-of select="@_gen-rows-name"/>By<xsl:value-of select="@child-col-_gen-method-name"/>(cc, get<xsl:value-of select="../column[@name = $parent-col]/@_gen-method-name"/>());
		return <xsl:value-of select="@_gen-rows-member-name"/>;
	}
</xsl:for-each>
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Primary Key = ");
		str.append(getActivePrimaryKeyValue());
		str.append("\n");
<xsl:for-each select="column">		str.append(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		str.append(" = ");
		str.append(<xsl:value-of select="@_gen-member-name"/> != null ? (<xsl:value-of select="@_gen-member-name"/>.toString() + " (" + <xsl:value-of select="@_gen-member-name"/>.getClass().getName() + ")") : "NULL");
		if(haveSqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>])
			str.append(" [SQL Expr: [" + sqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>] + "]]");
		else
			str.append(" [No SQL Expr]");
		str.append("\n");
</xsl:for-each>
		return str.toString();
	}
}
</xsl:template>

</xsl:stylesheet>
