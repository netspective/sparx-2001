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
<xsl:param name="app.ace.images-root-url"/>

<xsl:template match="xaf/factories">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">Object Factories</td>
	</tr>
	<tr class="heading_rule">
		<td height="1" ></td>
	</tr>
	</table>
	<table cellspacing="5">
	<tr valign="top">
	<td><xsl:apply-templates select="factory[@name='Value Sources']"/></td>
	<td><xsl:apply-templates select="factory[@name='Dialog Fields']"/></td>	
	</tr>
	<tr><td colspan="2"></td></tr>
	<tr valign="top">
	<td><xsl:apply-templates select="factory[@name='Report Columns']"/></td>
	<td><xsl:apply-templates select="factory[@name='Report Column Formats']"/></td>	
	</tr>
	</table>
</xsl:template>

<xsl:template match="factory[@name='Value Sources']">
	<h1>Value Sources</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Class</th>
		</tr>
		<tr><td colspan="2"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="value-source">
			<xsl:sort select="@name"/>
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
			<tr><td colspan="2"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="factory[@name='Dialog Fields']">
	<h1>Dialog Fields</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Class</th>
		</tr>
		<tr><td colspan="2"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="dialog-field">
			<xsl:sort select="@name"/>
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
			<tr><td colspan="2"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="factory[@name='Report Columns']">
	<h1>Report Columns</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Class</th>
		</tr>
		<tr><td colspan="2"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="report-column">
			<xsl:sort select="@name"/>
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
			<tr><td colspan="2"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="factory[@name='Report Column Formats']">
	<h1>Report Column Formats</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Class</th>
		</tr>
		<tr><td colspan="2"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="report-column-format">
			<xsl:sort select="@name"/>
			<tr>
				<td><xsl:value-of select="@name"/></td>
				<td><font color="green"><xsl:value-of select="@class"/></font></td>
			</tr>
			<tr><td colspan="2"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>
