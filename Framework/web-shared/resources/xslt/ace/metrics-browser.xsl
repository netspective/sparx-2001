<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="page-heading"/>
<xsl:param name="sub-detail-name"/>

<xsl:param name="framework.shared.images-url"/>
<xsl:param name="framework.ace.images-root-url"/>

<xsl:param name="indent-str">&#160;&#160;&#160;&#160;</xsl:param>

<xsl:template match="xaf">
	<div class="content">
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Value</th>
			<th class="data_table">&#160;</th>
		</tr>
		<xsl:for-each select="metric">
			<xsl:apply-templates/>
		</xsl:for-each>
	</table>
	</div>
</xsl:template>

<xsl:template match="metric">
	<xsl:param name="indent"/>
	
	<tr valign="top" class="data_table">
		<td class="data_table">
			<xsl:value-of select="$indent"/>
			<xsl:if test="@group = 'yes'">
				<b><xsl:value-of select="@name"/></b>
			</xsl:if>
			<xsl:if test="not(@group) or @group = 'no'">
				<xsl:value-of select="@name"/>
			</xsl:if>
		</td>
		<td class="data_table" align="right">
			<font color="green">
			<xsl:if test="not(@value) or value = 0">
				&#160;
			</xsl:if>
			<xsl:if test="@value">
			<xsl:value-of select="@value"/>
			</xsl:if>
			</font>
		</td>
		<td class="data_table">
			<font color="#999999">
			<xsl:if test="not(@value-detail)">
				&#160;
			</xsl:if>
			<xsl:if test="@value-detail">
			<xsl:value-of select="@value-detail"/>
			</xsl:if>
			</font>
		</td>
	</tr>
	<xsl:choose>
		<xsl:when test="@sort-children = 'yes'">
			<xsl:apply-templates>
				<xsl:sort select="@name"/>
				<xsl:with-param name="indent" select="concat($indent, $indent-str)"/>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates>
				<xsl:with-param name="indent" select="concat($indent, $indent-str)"/>
			</xsl:apply-templates>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
