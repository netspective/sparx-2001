<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="table-type-name"/>
<xsl:param name="table-type-class-name"/>

<xsl:template match="tabletype">
package <xsl:value-of select="$package-name"/>;

public interface <xsl:value-of select="$table-type-name"/>
{
}
</xsl:template>

</xsl:stylesheet>
