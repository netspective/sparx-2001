<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="table-name"/>
<xsl:variable name="default-text-size">32</xsl:variable>

<xsl:template match="table">
<xsl:variable name="_gen-table-method-name"><xsl:value-of select="@_gen-table-method-name"/></xsl:variable>
<xsl:variable name="_gen-table-row-class-name"><xsl:value-of select="@_gen-row-class-name"/></xsl:variable>
<xsl:variable name="_gen-table-row-name"><xsl:value-of select="@_gen-row-name"/></xsl:variable>
<xsl:variable name="table-abbrev"><xsl:value-of select="@abbrev"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.naming.NamingException;

import com.xaf.db.*;
import com.xaf.db.schema.*;
import com.xaf.value.*;

/**
 * Represents the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table.
<xsl:if test="@is-enum = 'yes'"> * &lt;p&gt;Because this table is an Enumeration tabletype, it automatically defines a
 * EnumeratedItem nested class that represents all of the values contained in the original SchemaDoc 
 * specification. Therefore, to get enumeration captions and abbreviations for tables that are
 * referencing this table as a foreign key, a join in the database is not required.&lt;/p&gt;</xsl:if>
 * &lt;p&gt;
 * Some relevant facts about this class:
 * &lt;ul&gt;
 *   &lt;li&gt; It represents the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; in the database.
 <xsl:if test="@parent"> * &lt;li&gt; It is a child of the &lt;code&gt;<xsl:value-of select="@parent"/>&lt;/code&gt; table in the database.
 </xsl:if> *   &lt;li&gt; It has <xsl:value-of select="count(column)"/> columns, all of which are represented as Column class instances as protected member variables and have public getter methods.
 *   &lt;li&gt; Wrappers are provided around the &lt;code&gt;createRow()&lt;/code&gt; and &lt;code&gt;createRows()&lt;/code&gt; methods to allow typed retrieval of the <xsl:value-of select="@_gen-row-name"/> and <xsl:value-of select="@_gen-rows-name"/> objects.
<xsl:if test="column[@primarykey='yes']"> * &lt;li&gt; The &lt;code&gt;get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="column[@primarykey='yes']/@_gen-method-name"/>()&lt;/code&gt; method is provided to retrieve a single row by the primary key
</xsl:if> 
<xsl:for-each select="column[@reftype='parent']"> * &lt;li&gt; The &lt;code&gt;get<xsl:value-of select="../@_gen-rows-name"/>By<xsl:value-of select="@_gen-method-name"/>()&lt;/code&gt; method is provided to retrieve all rows identified by the parent column
 * &lt;li&gt; The &lt;code&gt;delete<xsl:value-of select="../@_gen-rows-name"/>Using<xsl:value-of select="@_gen-method-name"/>()&lt;/code&gt; method is provided to delete all rows identified by the parent column
</xsl:for-each> * &lt;/ul&gt;
 * &lt;/p&gt;
 */
public class <xsl:value-of select="$table-name"/> extends AbstractTable <xsl:if test="@_implements-table-types">implements <xsl:value-of select="@_implements-table-types"/></xsl:if>
{
<xsl:for-each select="column">	/** The &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column definition **/
	protected <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>;
</xsl:for-each>

<xsl:if test="@is-enum = 'yes'">
	/**
	 * This class represents a cache of the enumerated values from the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table.
	 * It is designed to allow access to the actual values stored in the database without having to retrieve
	 * the data from the database each time it's needed (for example, you would use this class to check the
	 * caption of an enumerated item without doing a join in the database).
	 */
	public static class EnumeratedItem implements com.xaf.db.schema.EnumeratedItem
	{
		protected static final Map captionsMap = new HashMap();
		protected static final Map abbrevsMap = new HashMap();
<xsl:for-each select="enum[@abbrev]">		public static final EnumeratedItem <xsl:value-of select="@java-constant-name"/> = new EnumeratedItem(<xsl:value-of select="@id"/>, &quot;<xsl:value-of select="."/>&quot;, &quot;<xsl:value-of select="@abbrev"/>&quot;);
</xsl:for-each>
<xsl:for-each select="enum[not(@abbrev)]">		public static final EnumeratedItem <xsl:value-of select="@java-constant-name"/> = new EnumeratedItem(<xsl:value-of select="@id"/>, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>
		static 
		{			
<xsl:for-each select="enum">			captionsMap.put(<xsl:value-of select="@java-constant-name"/>.getCaption().toUpperCase(), <xsl:value-of select="@java-constant-name"/>);
</xsl:for-each>
<xsl:for-each select="enum[@abbrev]">			abbrevsMap.put(<xsl:value-of select="@java-constant-name"/>.getAbbrev().toUpperCase(), <xsl:value-of select="@java-constant-name"/>);
</xsl:for-each>		};
		
		public static EnumeratedItem getEnum(int id)
		{
			switch(id)
			{
<xsl:for-each select="enum">				case <xsl:value-of select="@id"/>: return <xsl:value-of select="@java-constant-name"/>;
</xsl:for-each>			}
			return null;
		}
		
		public static EnumeratedItem getItemById(Integer id)
		{
			if(id == null) return null;
			return getEnum(id.intValue());
		}

		public static EnumeratedItem getItemById(Long id)
		{
			if(id == null) return null;
			return getEnum((int) id.longValue());
		}
		
		public static EnumeratedItem getItemByCaption(String caption)
		{
			return (EnumeratedItem) captionsMap.get(caption.toUpperCase());
		}

		public static EnumeratedItem getItemByAbbrev(String abbrev)
		{
			return (EnumeratedItem) abbrevsMap.get(abbrev.toUpperCase());
		}
	
		private int id;
		private Integer idObject;
		private String caption;
		private String abbrev;
	
		private EnumeratedItem(int id, String caption, String abbrev)
		{
			this.id = id;
			this.idObject = new Integer(id);
			this.caption = caption;
			this.abbrev = abbrev;
		}

		private EnumeratedItem(int id, String caption)
		{
			this(id, caption, null);
		}
		
		public int getId() { return id; }
		public Integer getIdAsInteger() { return idObject; }
		public String getCaption() { return caption; }
		public String getAbbrev() { return abbrev; }
		public String getAbbrevOrCaption() { return abbrev != null ? abbrev : caption; }
	}
</xsl:if>

	public <xsl:value-of select="$table-name"/>(Schema schema)
	{
		super(schema, &quot;<xsl:value-of select="@name"/>&quot;);
		initializeDefn();
	}

	public void initializeDefn()
	{
<xsl:for-each select="column">	
	<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/> = new <xsl:value-of select="@_gen-data-type-class"/>(this, <xsl:value-of select="$_gen-table-row-class-name"/>.COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
<xsl:if test="@type = 'autoinc' and @primarykey = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsSequencedPrimaryKey(true);<xsl:text>
		</xsl:text><xsl:value-of select="$member-name"/>.setSequenceName(&quot;<xsl:value-of select="$table-abbrev"/>_<xsl:value-of select="@name"/>_SEQ&quot;);
</xsl:if>
<xsl:if test="@type != 'autoinc' and @primarykey = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsNaturalPrimaryKey(true);
</xsl:if>
<xsl:if test="@required = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsRequired(true);
</xsl:if>
<xsl:if test="@unique = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsUnique(true);
</xsl:if>
<xsl:choose>
	<xsl:when test="@default-java">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultValue(<xsl:value-of select="@default-java"/>);
	</xsl:when>
	<xsl:when test="default[@type = 'java']">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultValue(<xsl:value-of select="default"/>);
	</xsl:when>
</xsl:choose>
<xsl:choose>	
	<xsl:when test="@default">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultSqlExprValue(&quot;<xsl:value-of select="@default"/>&quot;);
	</xsl:when>
	<xsl:when test="default[not(@type)]">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultSqlExprValue(&quot;<xsl:value-of select="default"/>&quot;);
	</xsl:when>
</xsl:choose>
<xsl:if test="size and size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setSize(<xsl:value-of select="size"/>);
</xsl:if>
<xsl:if test="@size and @size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setSize(<xsl:value-of select="@size"/>);
</xsl:if>
<xsl:if test="selfref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_SELF, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
<xsl:if test="parentref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_PARENT, &quot;<xsl:value-of select="parentref"/>&quot;);
</xsl:if>
<xsl:if test="lookupref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_LOOKUP, &quot;<xsl:value-of select="selfref"/>&quot;);
</xsl:if>
</xsl:for-each>	
		setAllColumns(new Column[] {
<xsl:for-each select="column"><xsl:text>			</xsl:text><xsl:value-of select="@_gen-member-name"/><xsl:if test="position() != last()"><xsl:text>, 
</xsl:text></xsl:if>
</xsl:for-each>
		});
	}

	/**
	* Create the <xsl:value-of select="$_gen-table-row-name"/> class that should
	* be used to store a single row of this table's data.
	*/
	public Row createRow()
	{
		return new <xsl:value-of select="$_gen-table-row-class-name"/>(this);
	}
	
	/**
	* Create the <xsl:value-of select="@_gen-rows-name"/> class that should
	* be used to store multiple rows of this table's data.
	*/
	public Rows createRows()
	{
		return new <xsl:value-of select="@_gen-rows-class-name"/>(this);
	}
	
	/**
	* Create the <xsl:value-of select="$_gen-table-row-name"/> class that should
	* be used to store a single row of this table's data.
	*/
	public <xsl:value-of select="$_gen-table-row-class-name"/> create<xsl:value-of select="$_gen-table-row-name"/>()
	{
		return new <xsl:value-of select="$_gen-table-row-class-name"/>(this);
	}
	
	/**
	* Create the <xsl:value-of select="@_gen-rows-name"/> class that should
	* be used to store multiple rows of this table's data.
	*/
	public <xsl:value-of select="@_gen-rows-class-name"/> create<xsl:value-of select="@_gen-rows-name"/>()
	{
		return new <xsl:value-of select="@_gen-rows-class-name"/>(this);
	}
	
<xsl:for-each select="column">	/** Returns the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; column definition **/
	public <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-method-name"/>Column() { return <xsl:value-of select="@_gen-member-name"/>; }
</xsl:for-each>

<xsl:for-each select="column">	
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type and @primarykey = 'yes'">
    /** 
     * Updates the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key as a primitive type
     */
    public boolean updateBy<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, Row row, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException 
    {
        return update(cc, row, &quot;<xsl:value-of select="@_gen-member-name"/> = ?&quot;, new <xsl:value-of select="$java-class-spec"/>(value));
    }
    
	/** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key as a primitive type **/
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException 
	{ 
		return get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value)); 
	}
</xsl:if><xsl:if test="@primarykey = 'yes'">
	/** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key **/
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value) throws NamingException, SQLException
	{ 
		return (<xsl:value-of select="$_gen-table-row-class-name"/>) getRecordByPrimaryKey(cc, value, null);
	}
</xsl:if>
<xsl:if test="java-type and (@primarykey = 'yes') and ../child-table">
	/** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key as a primitive type and loads all the child objects **/
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value, boolean retrieveChildren) throws NamingException, SQLException 
	{ 
		return get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value), retrieveChildren); 
	}
</xsl:if><xsl:if test="@primarykey = 'yes' and ../child-table">
	/** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key and loads all the child objects **/
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value, boolean retrieveChildren) throws NamingException, SQLException
	{
		<xsl:value-of select="$_gen-table-row-class-name"/> row = (<xsl:value-of select="$_gen-table-row-class-name"/>) getRecordByPrimaryKey(cc, value, null);
		if(retrieveChildren) row.retrieveChildren(cc);
		return row;	
	}
</xsl:if>
<xsl:if test="@reftype = 'parent'">
<xsl:if test="java-type">
	/** Returns the <xsl:value-of select="../@_gen-rows-class-name"/> identified by the parent key as a primitive type **/
	public <xsl:value-of select="../@_gen-rows-class-name"/> get<xsl:value-of select="../@_gen-rows-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException
	{
		return get<xsl:value-of select="../@_gen-rows-name"/>By<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value));
	}

	/** 
	 * Deletes all the rows identified by the parent key as a primitive type (does a fast SQL delete, which does not generate interim rows and therefore does not call beforeDelete and afterDelete methods in Row)
	 */
	public void delete<xsl:value-of select="../@_gen-rows-name"/>Using<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException
	{ 
		delete<xsl:value-of select="../@_gen-rows-name"/>Using<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value));
	}
</xsl:if>
	/** Returns the <xsl:value-of select="../@_gen-rows-class-name"/> identified by the parent key **/
	public <xsl:value-of select="../@_gen-rows-class-name"/> get<xsl:value-of select="../@_gen-rows-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value) throws NamingException, SQLException
	{ 
		return (<xsl:value-of select="../@_gen-rows-class-name"/>) getRecordsByEquality(cc, <xsl:value-of select="../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="@_gen-constant-name"/>, value, null); 
	}

	/** 
	* Deletes all the rows identified by the parent key (does a fast SQL delete, which does not generate interim rows and therefore does not call beforeDelete and afterDelete methods in Row)
	**/
	public void delete<xsl:value-of select="../@_gen-rows-name"/>Using<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value) throws NamingException, SQLException
	{ 
		deleteRecordsByEquality(cc, <xsl:value-of select="../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="@_gen-constant-name"/>, value); 
	}
</xsl:if>
<xsl:if test="java-type and (@unique = 'yes' and @primarykey != 'yes')">
    /** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by the primary key as a primitive type **/
    public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException 
    { 
        return get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value)); 
    }
</xsl:if><xsl:if test="@unique = 'yes' and @primarykey != 'yes'">
    /** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by a unique key <xsl:value-of select="@name"/> **/
    public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value) throws NamingException, SQLException
    { 
        return (<xsl:value-of select="$_gen-table-row-class-name"/>) getRecordByPrimaryKey(cc, value, null);
    }
</xsl:if>
</xsl:for-each>
<xsl:for-each select="index[@type='unique' and @java-method-name]">
    /** Returns the <xsl:value-of select="$_gen-table-row-class-name"/> identified by unique keys <xsl:value-of select="@columns"/> **/
    public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@java-method-name"/>(ConnectionContext cc, <xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../column[@name=$index-column]/java-type"/><xsl:text> </xsl:text> <xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) throws NamingException, SQLException
    { 
        return (<xsl:value-of select="$_gen-table-row-class-name"/>) getRecordByEquality(cc, new String[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="../../column[@name=$index-column]/@_gen-constant-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, new Object[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable>new <xsl:value-of select="../../column[@name=$index-column]/java-class"/>(<xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/>)<xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, null);        
    }    
</xsl:for-each>

<xsl:for-each select="java-dal-accessor[@type='equality' and @name]">
    /** Returns the <xsl:value-of select="../@_gen-rows-class-name"/> identified by keys <xsl:value-of select="@columns"/> **/
    public <xsl:value-of select="../@_gen-rows-class-name"/><xsl:text> </xsl:text><xsl:value-of select="@name"/>(ConnectionContext cc, <xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../column[@name=$index-column]/java-type"/><xsl:text> </xsl:text> <xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) throws NamingException, SQLException
    { 
        return (<xsl:value-of select="../@_gen-rows-class-name"/>) getRecordsByEquality(cc, new String[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="../../column[@name=$index-column]/@_gen-constant-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, new Object[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable>new <xsl:value-of select="../../column[@name=$index-column]/java-class"/>(<xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/>)<xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, null);        
    }    
</xsl:for-each>

<xsl:for-each select="java-dal-accessor[@type='equality' and @name]">
    /** Returns the <xsl:value-of select="../@_gen-rows-class-name"/> identified by keys <xsl:value-of select="@columns"/> **/
    public <xsl:value-of select="../@_gen-rows-class-name"/><xsl:text> </xsl:text><xsl:value-of select="@name"/>(ConnectionContext cc, <xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../column[@name=$index-column]/java-class"/><xsl:text> </xsl:text> <xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) throws NamingException, SQLException
    {         
        <xsl:for-each select="column">
            <xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:if test="../../column[@name=$index-column]/@required and ../../column[@name=$index-column]/@required='yes'">
        if (<xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/> == null)
        {
            throw new SQLException("'" + <xsl:value-of select="../../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="../../column[@name=$index-column]/@_gen-constant-name"/> + "' column is a required field.");
        }
            </xsl:if>
        </xsl:for-each> 
        return (<xsl:value-of select="../@_gen-rows-class-name"/>) getRecordsByEquality(cc, new String[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../@_gen-row-class-name"/>.COLNAME_<xsl:value-of select="../../column[@name=$index-column]/@_gen-constant-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, new Object[]{<xsl:for-each select="column"><xsl:variable name="index-column"><xsl:value-of select="@name"/></xsl:variable><xsl:value-of select="../../column[@name=$index-column]/@_gen-member-name"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>}, null, true);
    }    
</xsl:for-each>

}
</xsl:template>

</xsl:stylesheet>
