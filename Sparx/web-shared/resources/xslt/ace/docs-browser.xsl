<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="sparx.shared.images-url"/>
<xsl:param name="ace-navigate-images-root-url"><xsl:value-of select="$sparx.shared.images-url"/>/navigate</xsl:param>

<xsl:template match="xaf/path">
	<xsl:if test="parents/parent">
		<div class="page_source">
			<xsl:for-each select="parents/parent">
				<xsl:if test="@isroot = 'true'">
					<img border='0'>
						<xsl:attribute name="src"><xsl:value-of select="$ace-navigate-images-root-url"/>/home-sm.gif</xsl:attribute>
					</img>
				</xsl:if>
				&#160;<a><xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="@caption"/></a>
				<xsl:if test="@islast != 'true'">
					&#160;<img border='0'>
						<xsl:attribute name="src"><xsl:value-of select="$ace-navigate-images-root-url"/>/parent-separator.gif</xsl:attribute>
					</img>				
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:if>

	<table cellspacing="5">
	<tr><td>
	
	<table>
		<xsl:for-each select="folders/folder">
			<xsl:sort select="@caption"/>
			<tr>
				<td>
				<img border='0'>
					<xsl:attribute name="src"><xsl:value-of select="$ace-navigate-images-root-url"/>/folder-orange-closed.gif</xsl:attribute>
				</img>
				</td>
				<td><a><xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="@caption"/></a></td>
			</tr>
		</xsl:for-each>
		<xsl:for-each select="files/file">
			<xsl:sort select="@caption"/>
			<tr>
				<td>
				<img border='0'>
					<xsl:attribute name="src"><xsl:value-of select="$ace-navigate-images-root-url"/>/page-yellow.gif</xsl:attribute>
				</img>
				</td>
				<td><a><xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="@caption"/></a></td>
			</tr>
		</xsl:for-each>
	</table>
	
	</td></tr>
	</table>
</xsl:template>

</xsl:stylesheet>
