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

<xsl:template match="xaf">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading"><xsl:value-of select="$page-heading"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="2" ></td>
	</tr>
	<tr class="heading_detail">
		<td>Source: <xsl:value-of select="meta-info/source-files/source-file/@abs-path"/></td>
	</tr>
	<tr class="heading_rule">
		<td height="1"></td>
	</tr>
	</table>
	<p/>
	<ul>
		<xsl:apply-templates select="access-control/permission"/>
	</ul>

	<h1>Source Files</h1>
	<ol>
	<xsl:for-each select="meta-info/source-files/source-file">
		<li>
			<a>
			<xsl:attribute name="href"><xsl:value-of select="@abs-path"/></xsl:attribute>
			<xsl:value-of select="@abs-path"/>
			</a>
			<xsl:if test="@included-from">
				(from <xsl:value-of select="@included-from"/>)
			</xsl:if>
		</li>
	</xsl:for-each>
	</ol>

	<p/>
	<xsl:if test="meta-info/errors">
		<h1>Errors</h1>
		<ol>
		<xsl:for-each select="meta-info/errors/error">
			<li><xsl:value-of select="."/></li>
		</xsl:for-each>
		</ol>
	</xsl:if>
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
