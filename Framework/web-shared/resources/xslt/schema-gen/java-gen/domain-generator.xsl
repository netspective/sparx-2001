<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="domain-name"/>
<xsl:param name="domain-class-name"/>

<xsl:template match="table">
package <xsl:value-of select="$package-name"/>;

/**
 * Represents a single row of the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table.
<xsl:if test="@is-enum = 'yes'"> * &lt;p&gt;Because this interface defines the
 * row of a table that is an Enumeration tabletype, it is recommended that this
 * interface &lt;b&gt;not&lt;/b&gt; be used for simple reads from the database. 
 * Instead, one should use the <xsl:value-of select="@_gen-table-class-name"/>.EnumeratedItem class 
 * so that a join in the database will not be required for static data.</xsl:if>
 * &lt;p&gt;
 * Some relevant facts about this interface:
 * &lt;ul&gt;
 *   &lt;li&gt; It represents the data that can be stored and retrieved from the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table in the database.
 *   &lt;li&gt; It has <xsl:value-of select="count(column)"/> columns, all of which have getter and setter methods.
 * &lt;/ul&gt;
 * &lt;/p&gt;
 */
public interface <xsl:value-of select="$domain-name"/>
{
<xsl:for-each select="column">	
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type">
	/**
	 * <xsl:value-of select="@descr"/>
	 * @param defaultValue The value to return if <xsl:value-of select="@name"/> is null
	 **/
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="java-type"/> defaultValue);

	/**
	 * <xsl:value-of select="@descr"/>
	 **/
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>();

	/**
	 * <xsl:value-of select="@descr"/>
	 **/
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="java-type"/> value);
</xsl:if>	
	/**
	 * <xsl:value-of select="@descr"/>
	 * @param defaultValue The value to return if <xsl:value-of select="@name"/> is null
	 **/
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> defaultValue);

	/**
	 * <xsl:value-of select="@descr"/>
	 **/
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>();

	/**
	 * <xsl:value-of select="@descr"/>
	 **/
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> value);
</xsl:for-each>
}
</xsl:template>
</xsl:stylesheet>