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
<xsl:param name="framework.shared.images-url"/>
<xsl:param name="framework.ace.images-root-url"/>

<xsl:template match="xaf/config-items">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading"><xsl:value-of select="$page-heading"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="2" ></td>
	</tr>
	<tr class="heading_detail">
		<td>Source: <xsl:value-of select="@source-file"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="1"></td>
	</tr>
	</table>
	<p/>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Value</th>
			<th>Expression</th>
			<th>Final</th>
		</tr>
		<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="*">
			<xsl:sort select="@name"/>
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@value"/></font></td>
				<td><font color="red"><xsl:value-of select="@expression"/></font></td>
				<td><font color="red"><xsl:value-of select="@final"/></font></td>
			</tr>
			<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>
