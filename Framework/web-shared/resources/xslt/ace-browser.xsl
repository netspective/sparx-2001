<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="test-url"/>
<xsl:param name="app-area"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<xsl:param name="framework.shared.images-url"/>
<xsl:param name="app.ace.images-root-url"/>

<xsl:template match="xaf">
	<style>
		body { font-family: verdana; font-size: 9pt; margin: 0; }
		h1 { font-family: arial; font-size: 12pt; font-weight: bold; color: darkred; }
		th { font-family: verdana; font-size: 9pt; font-weight: bold; color: darkred; }
		td { font-family: tahoma; font-size: 8pt; }

		table.heading { width: 100%; }
		tr.heading { background-color: #389cce; }
		tr.heading_rule { background-color: black; }
		td.heading { color: lightyellow; font-size: 14pt; font-weight: bold; font-family: arial,helvetica; }

		td.param_name { font-weight: bold; text-align:right; }
		td.param_value { color: green; }

		a.column_name { text-decoration: none; color: navy; }
		a.column_name:hover { text-decoration: underline; }
		a.column_references { text-decoration: none; color: red; }
		a.column_references:hover { text-decoration: underline; }
		td.column_name { font-weight: bold; }
		td.column_domain { color: green; }
		td.column_default { color: #AAAAAA; font-family: lucida console, courier; }
		td.column_descr { color: #AAAAFF; }
		td.column_references { color: red }
		td.table_info_caption { text-align: right; }
		td.table_info_value { font-weight: bold; color: navy}
		td.table_description { font-size: 9pt; font-family: verdana; }
		td.column_attr_caption { text-align: right; font-style: italic; }
		td.column_attr_value { color: navy}
		td.column_elem_caption { text-align: right; }
		td.column_elem_value { color: navy}
		select { font-family: tahoma; font-size: 8pt; }
		p.description { font-family: verdana; font-size: 9pt; background-color: lightyellow; padding: 5;}
		div.table_struct_closed { font-family: tahoma; font-size: 8pt; display: 'none'; padding-left: 5pt; padding-top: 2pt; cursor: hand; }
		div.table_struct_open { font-family: tahoma; font-size: 8pt; display: ''; padding-left: 5pt; padding-top: 2pt; }
		div.table_struct { font-family: tahoma; font-size: 8pt; display: ''; padding-left: 5pt; padding-top: 2pt; }
		span.table_struct_name { cursor: hand; }
		span.table_struct_menu { cursor: hand; font-family: webdings; font-size: 8pt; color: navy; }
		a.table_struct { text-decoration: none; color: navy; }
		a.table_struct:hover { text-decoration: underline; }
	</style>

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td>
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/xaf-ace-logo.gif</xsl:attribute>
			</img>
			</td>
			<td align="right">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/ace-logo.gif</xsl:attribute>
			</img>
			</td>
		</tr>
		<tr bgcolor="white">
			<td height="3" colspan="2"></td>
		</tr>
		<tr>
			<td colspan="2">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr valign="top">
						<td width="3"></td>
						<xsl:apply-templates select="app-areas/app-area"/>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	<xsl:if test="$app-area = 'home'">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">Welcome to ACE</td>
	</tr>
	<tr class="heading_rule">
		<td height="1" ></td>
	</tr>
	</table>
	<table><tr><td>
	<h1>Init Parameters</h1>
	<table>
		<xsl:for-each select="context/params/*">
			<xsl:sort select="name"/>
			<tr>
				<td class="param_name"><xsl:value-of select="name"/>:</td>
				<td class="param_value"><xsl:value-of select="value"/></td>
			</tr>
		</xsl:for-each>
	</table>
	<h1>Data Sources</h1>
	<table>
		<xsl:for-each select="context/data-sources/*">
			<xsl:sort select="name"/>
			<tr valign="top">
				<td class="param_name"><xsl:value-of select="name"/>:</td>
				<td class="param_value">
					<xsl:value-of select="driver-name"/>
					<xsl:if test="driver-version">
						Version <xsl:value-of select="driver-version"/><br/>
						<xsl:value-of select="url"/><br/>
						<xsl:value-of select="user-name"/>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
	</table>
	</td></tr></table>
	</xsl:if>
</xsl:template>

<xsl:template match="app-area">
	<xsl:choose>	
	<xsl:when test="@active = 'yes'">
		<td bgcolor="#389cce" width="3" height="16"><img><xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/tab-top-left-corner.gif</xsl:attribute></img></td>
		<td bgcolor="#389cce" height="16">&#160;&#160;<b><a style="text-decoration:none; color:yellow;"><xsl:attribute name="href"><xsl:value-of select="$root-url"/>/<xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="@caption"/></a></b>&#160;&#160;</td>
		<td bgcolor="#389cce" width="3" height="16"><img><xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/tab-top-right-corner.gif</xsl:attribute></img></td>	
	</xsl:when>
	<xsl:otherwise>
		<td bgcolor="#E5E2C9" width="3" height="16"><img><xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/tab-top-left-corner.gif</xsl:attribute></img></td>
		<td bgcolor="#E5E2C9" height="16">&#160;<a style="text-decoration:none; color: black;"><xsl:attribute name="href"><xsl:value-of select="$root-url"/>/<xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="@caption"/></a>&#160;</td>
		<td bgcolor="#E5E2C9" width="3" height="16"><img><xsl:attribute name="src"><xsl:value-of select="$app.ace.images-root-url"/>/tab-top-right-corner.gif</xsl:attribute></img></td>	
	</xsl:otherwise>
	</xsl:choose>
	<td width="3"></td>
</xsl:template>

</xsl:stylesheet>
