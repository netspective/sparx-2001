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
	<div class="page_source">
		Source: <xsl:value-of select="@source-file"/>
		(Allow reload: <xsl:value-of select="@allow-reload"/>)
	</div>

	<div class="content">
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Name</th>
			<th class="data_table">Value</th>
			<th class="data_table">Expression</th>
			<th class="data_table">Final</th>
		</tr>
		<xsl:for-each select="*">
			<xsl:sort select="@name"/>
			<xsl:if test="name() = 'config-item'">
			<tr class="data_table">
				<td class="data_table"><xsl:value-of select="@name"/></td>
				<td class="data_table"><font color="green"><xsl:value-of select="@value"/></font></td>
				<td class="data_table"><font color="red"><xsl:value-of select="@expression"/><xsl:if test="not(@expression)">&#160;</xsl:if></font></td>
				<td class="data_table"><font color="red"><xsl:value-of select="@final"/><xsl:if test="not(@final)">&#160;</xsl:if></font></td>
			</tr>
			</xsl:if>
			<xsl:if test="name() = 'config-items'">
			<tr class="data_table">
				<td class="data_table"><font color="navy"><xsl:value-of select="@name"/></font></td>
				<td class="data_table"><font color="navy">List of <xsl:value-of select="count(config-item)"/>:</font></td>
				<td class="data_table"><font color="red">&#160;</font></td>
				<td class="data_table"><font color="red">no</font></td>
			</tr>
			<xsl:for-each select="config-item">
				<tr class="data_table">
					<td class="data_table">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="@name"/></td>
					<td class="data_table"><font color="green"><xsl:value-of select="@value"/></font></td>
					<td class="data_table"><font color="red"><xsl:value-of select="@expression"/><xsl:if test="not(@expression)">&#160;</xsl:if></font></td>
					<td class="data_table"><font color="red"><xsl:value-of select="@final"/><xsl:if test="not(@final)">&#160;</xsl:if></font></td>
				</tr>
			</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
	</table>	
	</div>	
</xsl:template>

</xsl:stylesheet>
