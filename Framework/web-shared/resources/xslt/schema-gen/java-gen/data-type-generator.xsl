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
<xsl:if test="java-type">
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="$java-type-init-cap"/>Value(DataContext dc, <xsl:value-of select="java-type"/> defaultValue)
	{
		Object o = getObjectValue(dc);
		return o != null ? (((<xsl:value-of select="$java-class-spec"/>) o).<xsl:value-of select="java-type"/>Value()) : defaultValue;
	}		

	public <xsl:value-of select="java-type"/> get<xsl:value-of select="$java-type-init-cap"/>Value(DataContext dc)
	{
		return get<xsl:value-of select="$java-type-init-cap"/>Value(dc, (<xsl:value-of select="java-type"/>) <xsl:value-of select="java-type/@default"/>);
	}		

	public void set<xsl:value-of select="$java-type-init-cap"/>Value(DataContext dc, <xsl:value-of select="java-type"/> value)
	{
		setValue(dc, new <xsl:value-of select="$java-class-spec"/>(value));
	}		
</xsl:if>	
	public <xsl:value-of select="$java-class-spec"/> getValue(DataContext dc, <xsl:value-of select="$java-class-spec"/> defaultValue)
	{
		Object o = getObjectValue(dc);
		return o != null ? ((<xsl:value-of select="$java-class-spec"/>) o) : defaultValue;
	}

	public <xsl:value-of select="$java-class-spec"/> getValue(DataContext dc)
	{
		return (<xsl:value-of select="$java-class-spec"/>) getObjectValue(dc);
	}

	public void setValue(DataContext dc, <xsl:value-of select="$java-class-spec"/> value)
	{
		setValueObject(dc, value);
	}
}
</xsl:template>

</xsl:stylesheet>
