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

<xsl:template match="xaf/properties">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading"><xsl:value-of select="$page-heading"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="1" ></td>
	</tr>
	</table>
	<br/>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Value</th>
		</tr>
		<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="*">
			<xsl:sort select="name"/>
			<tr valign="top">
				<td><xsl:value-of select="name"/></td>
				<td>
					<font color="green">
					<xsl:value-of select="value"/>
					<xsl:for-each select="value-detail">
						<br/><xsl:value-of select="."/>
					</xsl:for-each>
					</font>
				</td>
			</tr>
			<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="xaf/factory">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading"><xsl:value-of select="@name"/>&#160;<font color="black">Factory</font></td>
	</tr>
	<tr class="heading_rule">
		<td height="2" ></td>
	</tr>
	<tr bgcolor="#EEEEEE">
		<td><xsl:value-of select="@class"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="1" ></td>
	</tr>
	</table>
	<br/>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Class</th>
		</tr>
		<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="*">
			<xsl:sort select="@name"/>
			<tr valign="top">
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
			<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
	<p/>
</xsl:template>

</xsl:stylesheet>
