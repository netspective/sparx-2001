<?xml version='1.0'?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dt="http://xaf.com/data-table" xmlns:dc="http://xaf.com/data-column">
	<xsl:output method="html"/>

	<xsl:template match="data-table">
		<style>
			tr.dtr_head { background-color: #eeeedd; }
			tr.dtr_data { }
			td.dtc_head { font-family: verdana,arial,helvetica; font-size: 8pt; font-weight: bold; color: navy; }
			td.dtc_data { font-family: verdana,arial,helvetica; font-size: 8pt }
			td.dtc_numdata { font-family: verdana,arial,helvetica; font-size: 8pt; text-align: right; }
			a.dtc_link { text-decoration: none; }
		</style>
		
		<table cellspacing="0" cellpadding="1" border="0">
			<xsl:variable name="heading-row">
				<xsl:apply-templates select="data-table-head"/>
			</xsl:variable>

			<xsl:copy-of select="$heading-row"/>
			<xsl:variable name="tdCellsCount" select="100%"/>
			
			<xsl:call-template name="get-head-row-separator">
				<xsl:with-param name="tdCellsCount" select="$tdCellsCount"/>
			</xsl:call-template>			
						
			<xsl:apply-templates select="data-table-row-set/data-table-row">
				<xsl:with-param name="column-info" select="data-table-head"/>
				<xsl:with-param name="col-separator">
					<xsl:call-template name="get-data-col-separator"/>
				</xsl:with-param>
				<xsl:with-param name="row-separator">
					<xsl:call-template name="get-data-row-separator">
						<xsl:with-param name="tdCellsCount" select="$tdCellsCount"/>
					</xsl:call-template>			
				</xsl:with-param>
			</xsl:apply-templates>
		</table>
	</xsl:template>
	
	<xsl:template name="get-head-row-separator">
		<xsl:param name="tdCellsCount"/>
		<tr><td><xsl:attribute name="colspan"><xsl:value-of select="$tdCellsCount"/></xsl:attribute><img src="/otrack/resources/images/design/bar.gif" width="100%" height="2"/></td></tr>
	</xsl:template>
	
	<xsl:template name="get-data-row-separator">
		<xsl:param name="tdCellsCount"/>
		<tr><td><xsl:attribute name="colspan"><xsl:value-of select="$tdCellsCount"/></xsl:attribute><img src="/otrack/resources/images/design/bar.gif" width="100%" height="1"/></td></tr>
	</xsl:template>

	<xsl:template name="get-head-col-separator">
		<td class="dtc_head" width="4"></td>
	</xsl:template>

	<xsl:template name="get-data-col-separator">
		<td class="dtc_data" width="4"></td>
	</xsl:template>

	<xsl:template match="data-table-head">
		<xsl:param name="col-separator"/>
		
		<xsl:variable name="col-separator">
			<xsl:call-template name="get-head-col-separator"/>
		</xsl:variable>
		<tr class="dtr_head" valign="top">
			<xsl:copy-of select="$col-separator"/>
			<xsl:apply-templates select="*" mode="head">
				<xsl:with-param name="col-separator" select="$col-separator"/>
			</xsl:apply-templates>
		</tr>
	</xsl:template>
	
	<xsl:template match="data-table-row">
		<xsl:param name="column-info"/>
		<xsl:param name="col-separator"/>
		<xsl:param name="row-separator"/>
		
		<tr class="dtr_data" valign="top">
			<xsl:copy-of select="$col-separator"/>
			<xsl:apply-templates select="*" mode="data">
				<xsl:with-param name="column-info" select="$column-info"/>
				<xsl:with-param name="col-separator" select="$col-separator"/>
			</xsl:apply-templates>
		</tr>
		<xsl:copy-of select="$row-separator"/>
	</xsl:template>

	<xsl:template match="*" mode="head">
		<xsl:param name="col-separator"/>
		<xsl:if test="not(@display = 'no')">
		<td class="dtc_head">
		<xsl:value-of select="@heading"/>
		</td>
		<xsl:copy-of select="$col-separator"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*" mode="data">
		<xsl:param name="column-info"/>
		<xsl:param name="col-separator"/>
		
		<xsl:variable name="my-name" select="name()"/>
		<xsl:variable name="my-info" select="$column-info/*[name() = $my-name]"/>
		<xsl:if test="not($my-info/@display = 'no')">
		<td>
			<xsl:choose>
				<xsl:when test="$my-info/@data-type = 'number'">
					<xsl:attribute name="class">dtc_numdata</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">dtc_data</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="."/>
		</td>
		<xsl:copy-of select="$col-separator"/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>