<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="page-heading"/>
<xsl:param name="test-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="sparx.shared.images-url"/>
<xsl:param name="sparx.ace.images-root-url"/>

<xsl:template match="xaf">
	<div class="page_source">
		Source: <xsl:value-of select="meta-info/source-files/source-file/@abs-path"/>
	</div>

	<div class="content">
	<ul>
		<xsl:apply-templates select="access-control/permission"/>
	</ul>
	
	<div class="content_head">Options</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Value</th>
		</tr>
		<xsl:for-each select="meta-info/options">
			<xsl:sort select="@name"/>
			<tr valign="top" class="data_table">
				<td class="data_table"><xsl:value-of select="@name"/></td>
				<td class="data_table"><font color="green"><xsl:value-of select="@value"/></font></td>
			</tr>
		</xsl:for-each>
	</table>
	
	<div class="content_head">Source Files</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
	<tr class="data_table_header">
		<th class="data_table">File</th>
		<th class="data_table">Included-from</th>
	</tr>
	<xsl:for-each select="meta-info/source-files/source-file">
		<tr class="data_table">
			<td class="data_table">
			<a class="data_table">
			<xsl:attribute name="href"><xsl:value-of select="@abs-path"/></xsl:attribute>
			<xsl:value-of select="@abs-path"/>
			</a>
			</td>
			<td class="data_table">
				<xsl:value-of select="@included-from"/>
				<xsl:if test="not(@included-from)">&#160;</xsl:if>
			</td>
		</tr>
	</xsl:for-each>
	</table>

	<p/>
	<xsl:if test="meta-info/errors">
		<div class="content_head">Errors</div>
		<ol>
		<xsl:for-each select="meta-info/errors/error">
			<li><xsl:value-of select="."/></li>
		</xsl:for-each>
		</ol>
	</xsl:if>
	</div>
</xsl:template>

<xsl:template match="permission">
	<li>
	<b><xsl:value-of select="@name"/></b>&#160;=&#160;
	<font color="red"><b><xsl:value-of select="@id"/></b></font>
	&#160;<font color="green"><code>(<xsl:value-of select="@full-name"/>)<br/><font color="skyblue"><xsl:value-of select="@bit-set"/></font></code></font>
	<xsl:if test="permission">
		<ul>
			<xsl:apply-templates select="permission"/>
		</ul>
	</xsl:if>
	</li>
</xsl:template>

</xsl:stylesheet>
