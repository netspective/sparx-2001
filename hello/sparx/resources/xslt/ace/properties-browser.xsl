<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="page-heading"/>
<xsl:param name="sub-detail-name"/>

<xsl:param name="sparx.shared.images-url"/>
<xsl:param name="sparx.ace.images-root-url"/>

<xsl:template match="xaf/properties">
	<xsl:choose>
		<xsl:when test="@name and (@name != $page-heading)">
			<div class="content">
			<div class="content_head">
				<xsl:if test="@name">
					<xsl:value-of select="@name"/>
				</xsl:if>
				<xsl:if test="not(@name)">
					<xsl:value-of select="$page-heading"/>
				</xsl:if>
			</div>
			<xsl:if test="@class">
				<div class="content_subhead">
					Source: <xsl:value-of select="@class"/>
				</div>
			</xsl:if>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<xsl:if test="@class">
				<div class="page_source">
					Source: <xsl:value-of select="@class"/>
				</div>
			</xsl:if>
		</xsl:otherwise>
	</xsl:choose>

	<div class="content">
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Value</th>
		</tr>
		<xsl:for-each select="*">
			<xsl:sort select="name"/>
			<tr valign="top" class="data_table">
				<td class="data_table"><xsl:value-of select="name"/></td>
				<td class="data_table">
					<font color="green">
					<xsl:value-of select="value"/>
					<xsl:for-each select="value-detail">
						<br/><xsl:value-of select="."/>
					</xsl:for-each>
					</font>
				</td>
			</tr>
		</xsl:for-each>
	</table>
	</div>
</xsl:template>

<xsl:template match="xaf/factory[not(value-source)]">
	<div class="page_source">
		Source: <xsl:value-of select="@class"/>
	</div>
	
	<div class="content">
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Class</th>
		</tr>
		<xsl:for-each select="*">
			<xsl:sort select="@name"/>
			<tr valign="top" class="data_table">
				<td class="data_table"><xsl:value-of select="@name"/></td>
				<td class="data_table"><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
		</xsl:for-each>
	</table>
	</div>
</xsl:template>

<xsl:template match="xaf/factory[value-source]">
	<div class="page_source">
		Source: <xsl:value-of select="@class"/>
	</div>
	
	<div class="content">
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">SVS</th>
			<th class="data_table">LVS</th>
			<th class="data_table" title="Read/Write">RW</th>
			<th class="data_table">Usage</th>
			<th class="data_table">Class</th>
		</tr>
		<xsl:for-each select="*">
			<xsl:sort select="@name"/>
			<tr valign="top" class="data_table">
				<td class="data_table"><xsl:value-of select="@name"/></td>
				<td class="data_table">
					<xsl:if test="@svs = 'yes'">S</xsl:if>
				</td>
				<td class="data_table">
					<xsl:if test="@lvs = 'yes'">L</xsl:if>
				</td>
				<td class="data_table">
					<xsl:if test="@allow-write = 'yes'">RW</xsl:if>
				</td>				
				<td class="data_table"><xsl:value-of select="@params" disable-output-escaping="yes"/></td>
				<td class="data_table">
					<font color="green"><xsl:value-of select="@class"/></font>
					<xsl:if test="descr">
						<br/><xsl:value-of select="descr" disable-output-escaping="yes"/>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
	</table>
	</div>
</xsl:template>

</xsl:stylesheet>
