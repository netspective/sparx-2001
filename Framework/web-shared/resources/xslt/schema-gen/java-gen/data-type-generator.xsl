<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="data-type-name"/>
<xsl:param name="java-type-init-cap"/>

<xsl:template match="datatype"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;

import com.xaf.db.schema.*;
import com.xaf.value.*;
<xsl:if test="java-class and java-class/@package != 'java.lang' and java-class/@package != 'java.util'">import <xsl:value-of select="java-class/@package"/>;</xsl:if>

public class <xsl:value-of select="$data-type-name"/> extends AbstractColumn
{
	private <xsl:value-of select="$java-class-spec"/> defaultValue;

	public <xsl:value-of select="$data-type-name"/>(Table table, String name)
	{
		super(table, name);
<xsl:for-each select="sqldefn">
		setSqlDefn(&quot;<xsl:value-of select="@dbms"/>&quot;, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>	
<xsl:if test="java-class">		setDataClassName(&quot;<xsl:value-of select="$java-class-spec"/>&quot;);
</xsl:if>
<xsl:if test="default">		setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(&quot;<xsl:value-of select="default"/>&quot;));
</xsl:if>
<xsl:if test="size">		setSize(<xsl:value-of select="size"/>);
</xsl:if>
	}
	
	public <xsl:value-of select="$java-class-spec"/> getDefaultValue() { return defaultValue; }	
	public void setDefaultValue(<xsl:value-of select="$java-class-spec"/> value) { defaultValue = value; }
}
</xsl:template>
</xsl:stylesheet>
