<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="listener-name"/>
<xsl:param name="listener-class-name"/>

<xsl:template match="table">
package <xsl:value-of select="$package-name"/>;

/**
 * Represents a listener for the &lt;code&gt;<xsl:value-of select="@name"/>&lt;/code&gt; table.
 */
public class <xsl:value-of select="$listener-name"/>
{
}
</xsl:template>
</xsl:stylesheet>