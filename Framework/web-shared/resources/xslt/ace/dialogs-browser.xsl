<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="page-heading"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="framework.shared.images-url"/>
<xsl:param name="framework.ace.images-root-url"/>

<xsl:param name="ui-images-root-url"><xsl:value-of select="$framework.shared.images-url"/>/dbdd</xsl:param>

<xsl:template match="xaf">
	<xsl:choose>
		<xsl:when test="$detail-type = 'describe' and $detail-name">
			<xsl:apply-templates select="dialogs/dialog[@qualified-name = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:otherwise>
			<div class="content">
				<div class="content_head">Dialogs</div>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
				<tr class="data_table_header">
					<th class="data_table">Actions</th>
					<th class="data_table">ID</th>
					<th class="data_table">Heading</th>
					<th class="data_table" title="Retain Request Parameters">Retain</th>
					<th class="data_table">Fields</th>
					<th class="data_table">Tasks</th>
				</tr>
				<xsl:apply-templates select="dialogs/dialog" mode="toc">
					<xsl:sort select="@qualified-name"/>
				</xsl:apply-templates>
				</table>					

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
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="dialogs" mode="toc">
	<li><b><a><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/','describe','/',@package)"/></xsl:attribute><xsl:value-of select="@package"/></a></b></li>
</xsl:template>

<xsl:template match="dialogs" mode="toc-select">
	<option>
		<xsl:if test="@package = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/describe/',@package)"/>
		</xsl:attribute><xsl:value-of select="@package"/>
	</option>
</xsl:template>

<xsl:template match="register-field" mode="toc">
	<li><b><xsl:value-of select="@tag-name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="register-skin" mode="toc">
	<li><b><xsl:value-of select="@name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="dialog" mode="toc">
	<tr class="data_table">
		<td class="data_table">
			<a title="Click here to see dialog sample">
				<xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/test/',@qualified-name)"/></xsl:attribute>
				<xsl:attribute name="target"><xsl:value-of select="concat('dialog.', @qualified-name)"/></xsl:attribute>
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/exec_dialog.gif</xsl:attribute>
				</img>
			</a>
			<img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/spacer.gif</xsl:attribute></img>
			<a title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/></xsl:attribute><img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/describe_dialog.gif</xsl:attribute></img></a>
		</td>
		<td class="data_table"><b><a class="data_table" title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/></xsl:attribute><xsl:value-of select="@qualified-name"/></a></b></td>
		<td class="data_table"><font color="green"><xsl:value-of select="@heading"/></font></td>
		<td class="data_table"><font color="red"><xsl:value-of select="@retain-params"/><xsl:if test="not(@retain-params)">&#160;</xsl:if></font></td>
		<td class="data_table" align="right"><font color="red"><xsl:value-of select="count(*)"/></font></td>
		<td class="data_table" align="right"><font color="red"><xsl:value-of select="count(execute-tasks/*)"/></font></td>
	</tr>
</xsl:template>

<xsl:template match="dialog" mode="toc-select">
	<option>
		<xsl:if test="@qualified-name = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/>
		</xsl:attribute><xsl:value-of select="@qualified-name"/>
	</option>
</xsl:template>

<xsl:template name="field-detail">
	<xsl:param name="field"/>
	<xsl:param name="indent"/>
	<xsl:param name="no-separator"/>
	<tr valign="top">
		<td></td>
		<td><xsl:value-of select="$indent"/><font color="green"><xsl:value-of select="$field/@name"/></font></td>
		<td><xsl:value-of select="$indent"/><font color="navy"><xsl:value-of select="$field/@caption"/></font></td>
		<td><xsl:value-of select="$indent"/><xsl:value-of select="name()"/></td>
		<td><font color="navy"><xsl:value-of select="$field/@default"/></font></td>
		<td>
			<xsl:for-each select="$field/@*">
				<xsl:if test="name() != 'name' and name() != 'caption' and name() != 'default'">
					<font color="green"><xsl:value-of select="name()"/></font>
					= 
					<font color="navy"><xsl:value-of select="."/></font><br/>
				</xsl:if>
			</xsl:for-each>
		</td>
	</tr>
	<xsl:for-each select="*[not(starts-with(name(), 'field.'))]">
		<xsl:call-template name="field-detail">
			<xsl:with-param name="field" select="."/>
			<xsl:with-param name="no-separator" select="'yes'"/>
			<xsl:with-param name="indent" select="concat($indent, '&#160;&#160;&#160;&#160;')"/>
		</xsl:call-template>
	</xsl:for-each>
	<xsl:if test="not($no-separator)">
		<tr><td colspan="10"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
	</xsl:if>
	<xsl:for-each select="*[starts-with(name(), 'field.')]">
		<xsl:call-template name="field-detail">
			<xsl:with-param name="field" select="."/>
			<xsl:with-param name="indent" select="concat($indent, '&#160;&#160;&#160;&#160;')"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<xsl:template match="dialog" mode="detail">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
			<img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/describe_dialog.gif</xsl:attribute></img>
			&#160;<font color="navy">Dialog</font>&#160;<xsl:value-of select="@qualified-name"/>
		</td>
		<td class="page_menu" align="right">
			Dialogs:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="/xaf/dialogs/dialog" mode="toc-select">
					<xsl:sort select="@qualified-name"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	<tr><td colspan="2">
	<h1>Dialog Attributes</h1>
	<table>
		<xsl:for-each select="@*">
			<tr>
				<td class="param_name"><xsl:value-of select="name()"/>:</td>
				<td class="param_value"><xsl:value-of select="."/></td>
			</tr>
		</xsl:for-each>
	</table>
	<h1>Dialog Fields</h1>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">ID</th>
			<th class="data_table">Name</th>
			<th class="data_table">Caption</th>
			<th class="data_table">Type</th>
			<th class="data_table">Default</th>
			<th class="data_table">Options</th>
		</tr>
	<xsl:for-each select="*">
		<xsl:call-template name="field-detail">
			<xsl:with-param name="field" select="."/>
		</xsl:call-template>
	</xsl:for-each>
	</table>
	</td></tr>
	</table>
</xsl:template>

</xsl:stylesheet>
