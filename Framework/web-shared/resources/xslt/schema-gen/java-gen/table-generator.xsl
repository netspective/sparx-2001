<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="table-name"/>
<xsl:variable name="default-text-size">32</xsl:variable>

<xsl:template match="table">
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;

import com.xaf.db.schema.*;
import com.xaf.value.*;

public class <xsl:value-of select="$table-name"/> extends AbstractTable
{
<xsl:for-each select="column">	protected <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text><xsl:value-of select="@name"/>;
</xsl:for-each>
	
	public <xsl:value-of select="$table-name"/>(Schema schema)
	{
		super(schema, &quot;<xsl:value-of select="@name"/>&quot;);
		initializeDefn(schema);
	}

	public void initializeDefn(Schema schema)
	{
<xsl:for-each select="column">	
	<xsl:variable name="column-name"><xsl:value-of select="@name"/></xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="$column-name"/> = new <xsl:value-of select="@_gen-data-type-class"/>(this, &quot;<xsl:value-of select="@name"/>&quot;);
<xsl:if test="default"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(&quot;<xsl:value-of select="default"/>&quot;));
</xsl:if>
<xsl:if test="size and size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setSize(<xsl:value-of select="size"/>);
</xsl:if>
<xsl:if test="@size and @size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setSize(<xsl:value-of select="@size"/>);
</xsl:if>
<xsl:if test="selfref"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setForeignKeyRef(Column.FKEYREF_SELF, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
<xsl:if test="parentref"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setForeignKeyRef(Column.FKEYREF_PARENT, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
<xsl:if test="lookupref"><xsl:text>		</xsl:text><xsl:value-of select="$column-name"/>.setForeignKeyRef(Column.FKEYREF_LOOKUP, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
</xsl:for-each>	
	}

<xsl:for-each select="column">	public <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-method-name"/>Column() { return <xsl:value-of select="@name"/>; }
</xsl:for-each>

<xsl:for-each select="column">	
<xsl:variable name="column-name"><xsl:value-of select="@name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type">
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(DataContext dc, <xsl:value-of select="java-type"/> defaultValue)
	{
		return <xsl:value-of select="$column-name"/>.get<xsl:value-of select="$java-type-init-cap"/>Value(dc, defaultValue);
	}		

	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(DataContext dc)
	{
		return <xsl:value-of select="$column-name"/>.get<xsl:value-of select="$java-type-init-cap"/>Value(dc);
	}		

	public void set<xsl:value-of select="@_gen-method-name"/>(DataContext dc, <xsl:value-of select="java-type"/> value)
	{
		<xsl:value-of select="$column-name"/>.setValue(dc, new <xsl:value-of select="$java-class-spec"/>(value));
	}		
</xsl:if>	
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(DataContext dc, <xsl:value-of select="$java-class-spec"/> defaultValue)
	{
		return <xsl:value-of select="$column-name"/>.getValue(dc, defaultValue);
	}

	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(DataContext dc)
	{
		return <xsl:value-of select="$column-name"/>.getValue(dc);
	}

	public void set<xsl:value-of select="@_gen-method-name"/>(DataContext dc, <xsl:value-of select="$java-class-spec"/> value)
	{
		<xsl:value-of select="$column-name"/>.setValue(dc, value);
	}
</xsl:for-each>
}
</xsl:template>

</xsl:stylesheet>






