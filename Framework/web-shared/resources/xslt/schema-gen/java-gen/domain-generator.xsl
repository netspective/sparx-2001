<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="domain-name"/>
<xsl:param name="domain-class-name"/>

<xsl:template match="table">
package <xsl:value-of select="$package-name"/>;

public interface <xsl:value-of select="$domain-name"/>
{
<xsl:for-each select="column">	
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type">
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="java-type"/> defaultValue);
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>();
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="java-type"/> value);
</xsl:if>	
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> defaultValue);
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>();
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> value);
	public void set<xsl:value-of select="@_gen-method-name"/>SqlExpr(String value);
</xsl:for-each>
}
</xsl:template>
</xsl:stylesheet>