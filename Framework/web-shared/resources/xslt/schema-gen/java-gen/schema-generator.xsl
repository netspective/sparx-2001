<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="class-name"/>

<xsl:template match="schema">
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;

import com.xaf.db.schema.*;

public class <xsl:value-of select="$class-name"/> extends AbstractSchema
{
	public static <xsl:value-of select="$class-name"/> instance = new <xsl:value-of select="$class-name"/>();

<xsl:for-each select="table">	protected <xsl:value-of select="@_gen-table-class-name"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-table-member-name"/>;
</xsl:for-each>
	
	public <xsl:value-of select="$class-name"/>()
	{
		super();
		initializeDefn();
	}

	public void initializeDefn()
	{
<xsl:for-each select="table">	
<xsl:text>		</xsl:text><xsl:value-of select="@_gen-table-member-name"/> = new <xsl:value-of select="@_gen-table-class-name"/>(this);
</xsl:for-each>	
		finalizeDefn();
	}

<xsl:for-each select="table">	public <xsl:value-of select="@_gen-table-class-name"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-table-method-name"/>Table() { return <xsl:value-of select="@_gen-table-member-name"/>; }
</xsl:for-each>
}
</xsl:template>

</xsl:stylesheet>






