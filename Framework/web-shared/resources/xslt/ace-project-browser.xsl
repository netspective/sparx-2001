<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="test-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="framework.shared.images-url"/>
<xsl:param name="ace-navigate-images-root-url"><xsl:value-of select="$framework.shared.images-url"/>/navigate</xsl:param>

<xsl:template match="xaf/path">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading"><xsl:value-of select="@caption"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="1" ></td>
	</tr>
	</table>
	<table bgcolor="#EEEEEE" border="0" cellspacing="0" cellpadding="3" width="100%">		
		<tr>
		<td>
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
		</td>
		</tr>
		<tr class="heading_rule">
			<td height="1" bgcolor="#999999"></td>
		</tr>
	</table>
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
