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
	<table>
		<tr valign="top">
			<td>
				<h1>General Metrics</h1>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
					<tr class="data_table_header">
						<th class="data_table">Name</th>
						<th class="data_table">Value</th>
						<th class="data_table">&#160;</th>
					</tr>
					<xsl:for-each select="metric">
						<xsl:apply-templates select="metric[not(@type)]"/>
					</xsl:for-each>
				</table>
			</td>
			<td>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</td>
			<td>
				<xsl:for-each select="metric">
					<xsl:apply-templates select="metric[@type = 'file-types']"/>	
				</xsl:for-each>
			</td>
		</tr>
	</table>
	</div>
</xsl:template>

<xsl:template match="metric[not(@type)]">
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
			<xsl:apply-templates select="metric[not(@type)]">
				<xsl:sort select="@name"/>
				<xsl:with-param name="indent" select="concat($indent, $indent-str)"/>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="metric[not(@type)]">
				<xsl:with-param name="indent" select="concat($indent, $indent-str)"/>
			</xsl:apply-templates>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="metric[@type = 'file-types']">	
	<p/>
	<h1><xsl:value-of select="@name"/></h1>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table" style="border-bottom: 0">&#160;</th>
			<th class="data_table" style="border-bottom: 0">&#160;</th>
			<th class="data_table" colspan="2" style="border-bottom: 0">Bytes</th>			
			<th class="data_table" colspan="2" style="border-bottom: 0">Lines</th>			
		</tr>
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Count</th>
			<th class="data_table">Total</th>
			<th class="data_table">Avg</th>
			<th class="data_table">Total</th>
			<th class="data_table">Avg</th>
		</tr>

		<xsl:for-each select="metric">
		<xsl:sort select="not(@is-code)"/>
		<xsl:sort select="@name"/>	
		<tr valign="top" class="data_table">
			<td class="data_table">
				<xsl:value-of select="@name"/>
			</td>
			<td class="data_table" align="right">
				<xsl:value-of select="@count"/>
			</td>
			<td class="data_table" align="right">
				<xsl:value-of select="@total-bytes"/>
			</td>
			<td class="data_table" align="right">
				<xsl:choose>
					<xsl:when test="@avg-bytes != @total-bytes">
						<xsl:value-of select="@avg-bytes"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="data_table" align="right">
				<xsl:choose>
					<xsl:when test="@total-lines">
						<xsl:value-of select="@total-lines"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="data_table" align="right">
				<xsl:choose>
					<xsl:when test="@avg-lines != @total-lines">
						<xsl:value-of select="@avg-lines"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		</xsl:for-each>
		
		<tr valign="top" class="data_table">
			<td class="data_table">
				<b><xsl:value-of select="@name"/></b>
			</td>
			<td class="data_table" align="right">
				<b><xsl:value-of select="@count"/></b>
			</td>
			<td class="data_table" align="right">
				<b><xsl:value-of select="@total-bytes"/></b>
			</td>
			<td class="data_table" align="right">
				<b>
				<xsl:choose>
					<xsl:when test="@avg-bytes != @total-bytes">
						<xsl:value-of select="@avg-bytes"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
				</b>
			</td>
			<td class="data_table" align="right">
				<b>
				<xsl:choose>
					<xsl:when test="@total-lines">
						<xsl:value-of select="@total-lines"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
				</b>
			</td>
			<td class="data_table" align="right">
				<b>
				<xsl:choose>
					<xsl:when test="@avg-lines != @total-lines">
						<xsl:value-of select="@avg-lines"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
				</b>
			</td>
		</tr>
	</table>
</xsl:template>

</xsl:stylesheet>
