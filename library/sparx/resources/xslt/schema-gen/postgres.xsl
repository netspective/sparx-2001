<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="ansi.xsl"/>

<xsl:variable name="dbms-id">postgres</xsl:variable>
<xsl:variable name="generate-constraints">no</xsl:variable>
<xsl:variable name="generate-seq">yes</xsl:variable>
<xsl:variable name="system-date-function">now()</xsl:variable>
<xsl:variable name="system-boolean-false">'0'</xsl:variable>
<xsl:variable name="system-boolean-true">'1'</xsl:variable>

<xsl:template name="sequence-definition">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:if test="$generate-drop-seq">
		<xsl:text>drop sequence </xsl:text>
		<xsl:value-of select="$column/@_gen-sequence-name"/>
		<xsl:value-of select="$statement-terminator"/>
	</xsl:if>

	<xsl:text>create sequence </xsl:text>
	<xsl:value-of select="$column/@_gen-sequence-name"/>
	<xsl:text> increment 1 start 1</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
</xsl:template>

</xsl:stylesheet>